"use client";

import { useEffect, useState, useCallback } from "react";
import { useRouter } from "next/navigation";
import PropTypes from "prop-types";
import { useApiWithLoader } from "@/app/lib/useApiWithLoader";
import { Search, Plus, Edit2, Trash2, ChevronLeft, ChevronRight, ArrowLeft, X } from "lucide-react";

export default function BaseListForm({
    title,
    entity,
    columns = [],
    addPath,
    editPath,
    identifierKey = "identifier"
}) {
    const router = useRouter();
    const { post, delete: del } = useApiWithLoader();

    const [data,        setData]        = useState([]);
    const [loading,     setLoading]     = useState(true);
    const [error,       setError]       = useState("");
    const [totalPages,  setTotalPages]  = useState(1);
    const [totalRecords, setTotalRecords] = useState(0);
    const [searchTerm,  setSearchTerm]  = useState("");
    const [debouncedSearch, setDebouncedSearch] = useState("");
    const [deleting,    setDeleting]    = useState(null);

    const [pagination, setPagination] = useState({
        page:          0,
        sizePerPage:   10,
        sortDirection: "ASCENDING",
        sortField:     "id",
        keyword:       ""
    });

    const currentPage = pagination.page;
    const getId = useCallback((item) => item[identifierKey] ?? item.id, [identifierKey]);

    useEffect(() => {
        const timer = setTimeout(() => {
            setDebouncedSearch(searchTerm.trim());
        }, 400);
        return () => clearTimeout(timer);
    }, [searchTerm]);

    useEffect(() => {
        setPagination((prev) => ({
            ...prev,
            page:    0,
            keyword: debouncedSearch,
        }));
    }, [debouncedSearch]);

    const fetchData = useCallback(async () => {
        try {
            setLoading(true);
            setError("");

            const resData = await post(`/${entity}/list`, pagination);

            if (resData) {
                const list = resData.dtoList || resData.list || resData.content
                    || (Array.isArray(resData) ? resData : []);
                setData(list);

                const pages = resData.totalPages ?? resData.totalPage ?? (list[0]?.totalPages) ?? 1;
                const records = resData.totalRecords ?? resData.totalElements ?? list.length;
                setTotalPages(pages > 0 ? pages : 1);
                setTotalRecords(records);
            }
        } catch (err) {
            const status = err?.response?.status;
            if (status === 404) { router.push("/not-found"); return; }
            if (status === 500) { router.push("/error");     return; }
            setError("Failed to load records.");
            console.error(err);
        } finally {
            setLoading(false);
        }
    }, [entity, pagination, post, router]);

    useEffect(() => { fetchData(); }, [fetchData]);

    const goToPage = (pageIndex) => setPagination((p) => ({ ...p, page: pageIndex }));

    const handleDelete = async (identifier) => {
        if (!globalThis.confirm?.(`Delete this record?\n\nID: ${identifier}`)) return;
        try {
            setDeleting(identifier);
            setError("");
            await del(`/${entity}/delete`, { params: { identifier } });
            setData((prev) => prev.filter((item) => getId(item) !== identifier));
            if (data.length === 1 && currentPage > 0) goToPage(currentPage - 1);
        } catch (err) {
            const status = err?.response?.status;
            if (status === 404) { router.push("/not_found"); return; }
            if (status === 500) { router.push("/error");     return; }
            setError("Delete operation failed.");
        } finally {
            setDeleting(null);
        }
    };

    const handleToggleStatus = async (item) => {
        const targetId = getId(item);
        try {
            setError("");
            const updated = await post(
                `/${entity}/toggle?identifier=${encodeURIComponent(targetId)}`,
                null
            );
            setData((prev) =>
                prev.map((row) =>
                    getId(row) === targetId
                        ? { ...row, status: typeof updated?.status === "boolean" ? updated.status : !row.status }
                        : row
                )
            );
        } catch (err) {
            const status = err?.response?.status;
            if (status === 500) { router.push("/error"); return; }
            setError("Failed to toggle status.");
        }
    };

    const handleClearSearch = () => {
        setSearchTerm("");
        setDebouncedSearch("");
    };

    const formatEditPath = (identifier) => {
        if (!editPath) return "#";
        const clean = editPath.endsWith("/") ? editPath.slice(0, -1) : editPath;
        return `${clean}/${encodeURIComponent(identifier)}`;
    };

    const getVisiblePages = () => {
        let start = currentPage - 1;
        if (start < 0) start = 0;
        if (start + 3 > totalPages) start = Math.max(0, totalPages - 3);
        return Array.from({ length: Math.min(3, totalPages) }, (_, i) => start + i);
    };

    const getResultsLabel = () => {
        const resultCountText = data.length === 1 ? "result" : "results";
        return `${data.length} ${resultCountText} found`;
    };

    const renderCellContent = (item, col) => {
        const value = item[col.key];

        if (col.key === "status") {
            return (
                <button
                    onClick={() => handleToggleStatus(item)}
                    className={`px-3 py-1 rounded-full text-xs font-semibold transition-colors ${
                        item.status
                            ? "bg-[#006E74]/10 text-[#006E74] hover:bg-[#006E74]/20"
                            : "bg-[#231F20]/10 text-[#231F20]/60 hover:bg-[#231F20]/20"
                    }`}
                >
                    {item.status ? "Active" : "Inactive"}
                </button>
            );
        }

        if (col.render) return col.render(value, item);

        if (Array.isArray(value)) {
            return (
                <div className="flex flex-wrap gap-1">
                    {value.map((val) => (
                        <span key={String(val)}
                            className="inline-block bg-[#0097AC]/10 text-[#0097AC] px-2 py-0.5 rounded text-xs font-medium">
                            {String(val)}
                        </span>
                    ))}
                </div>
            );
        }

        return (
            <span className={value ? "text-[#231F20] font-medium" : "text-[#231F20]/40"}>
                {value ?? "—"}
            </span>
        );
    };

    const renderTableBody = () => {
        if (loading) {
            return Array.from({ length: pagination.sizePerPage }, (_, i) => (
                <div key={`skel-${i}`}
                    className="grid gap-4 px-6 py-4 animate-pulse"
                    style={{ gridTemplateColumns: `repeat(${columns.length}, minmax(0, 1fr)) 120px` }}>
                    {Array.from({ length: columns.length + 1 }, (_, j) => (
                        <div key={j} className="h-4 bg-[#231F20]/10 rounded w-3/4" />
                    ))}
                </div>
            ));
        }

        if (data.length === 0) {
            return (
                <div className="text-center py-16 text-[#231F20]/40">
                    {debouncedSearch ? (
                        <div className="flex flex-col items-center gap-3">
                            <Search size={28} className="opacity-30" />
                            <p className="text-sm font-semibold">No results for "{debouncedSearch}"</p>
                            <p className="text-xs">Try a different search term.</p>
                            <button onClick={handleClearSearch}
                                className="text-xs font-semibold text-[#006E74] hover:underline cursor-pointer bg-transparent border-none mt-1">
                                Clear search
                            </button>
                        </div>
                    ) : (
                        <p className="text-sm">No records found.</p>
                    )}
                </div>
            );
        }

        return data.map((item) => {
            const itemId = getId(item);
            return (
                <div key={`row-${itemId}`}
                    className="grid gap-4 px-6 py-4 text-sm text-[#231F20]/90 hover:bg-[#006E74]/5 transition-colors items-center"
                    style={{ gridTemplateColumns: `repeat(${columns.length}, minmax(0, 1fr)) 120px` }}>
                    {columns.map((col) => (
                        <div key={`cell-${itemId}-${col.key}`} className="truncate">
                            {renderCellContent(item, col)}
                        </div>
                    ))}
                    <div className="flex justify-end gap-2">
                        <button onClick={() => router.push(formatEditPath(itemId))}
                            className="flex items-center gap-1.5 px-3 py-1.5 text-xs font-semibold text-[#006E74] hover:bg-[#006E74]/10 border border-[#006E74]/30 rounded-lg transition-colors">
                            <Edit2 size={14} /> Edit
                        </button>
                        <button onClick={() => handleDelete(itemId)} disabled={deleting === itemId}
                            className="flex items-center gap-1.5 px-3 py-1.5 text-xs font-semibold text-[#231F20]/60 hover:bg-[#231F20]/10 border border-[#231F20]/20 rounded-lg transition-colors disabled:opacity-50">
                            <Trash2 size={14} /> Delete
                        </button>
                    </div>
                </div>
            );
        });
    };

    return (
        <div className="min-h-screen bg-slate-50 p-6">
            <div className="mb-8">
                <div className="flex justify-between items-start mb-6">
                    <div>
                        <div className="flex items-center gap-3">
                            <button onClick={() => router.back()}
                                className="p-2 -ml-2 rounded-lg text-[#231F20]/60 hover:text-[#231F20] hover:bg-[#006E74]/10 transition-colors"
                                title="Go back">
                                <ArrowLeft size={22} />
                            </button>
                            <h1 className="text-3xl font-bold text-[#231F20]">{title}</h1>
                        </div>
                        <p className="text-sm text-[#231F20]/70 mt-2 pl-1">
                            Manage and control your {entity.toLowerCase()} records.
                            {totalRecords > 0 && !loading && (
                                <span className="ml-1 text-[#006E74] font-semibold">
                                    {debouncedSearch ? getResultsLabel() : `${totalRecords} total`}
                                </span>
                            )}
                        </p>
                    </div>
                    <button onClick={() => router.push(addPath)}
                        className="flex items-center gap-2 bg-[#006E74] hover:bg-[#0097AC] text-white px-4 py-2.5 rounded-lg font-semibold transition-colors shadow-md cursor-pointer border-none">
                        <Plus size={18} /> Add {title}
                    </button>
                </div>

                <div className="relative">
                    <Search size={18} className="absolute left-3 top-1/2 -translate-y-1/2 text-[#231F20]/40" />
                    <input
                        type="text"
                        placeholder={`Search ${entity.toLowerCase()} records...`}
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        className="w-full pl-10 pr-10 py-2.5 bg-white border border-[#231F20]/20 rounded-lg focus:outline-none focus:border-[#006E74] focus:ring-2 focus:ring-[#006E74]/10 text-[#231F20] transition-all"
                    />
                    {loading && debouncedSearch && (
                        <div className="absolute right-3 top-1/2 -translate-y-1/2">
                            <div className="w-4 h-4 border-2 border-[#006E74]/30 border-t-[#006E74] rounded-full animate-spin" />
                        </div>
                    )}
                    {searchTerm && !loading && (
                        <button onClick={handleClearSearch}
                            className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 cursor-pointer bg-transparent border-none">
                            <X size={16} />
                        </button>
                    )}
                </div>

                {debouncedSearch && (
                    <div className="mt-2 flex items-center gap-2">
                        <span className="text-xs text-[#006E74] font-semibold">
                            Searching DB for: "{debouncedSearch}"
                        </span>
                        <button onClick={handleClearSearch}
                            className="text-xs text-gray-400 hover:text-gray-600 underline cursor-pointer bg-transparent border-none">
                            Clear
                        </button>
                    </div>
                )}
            </div>

            {error && (
                <div className="mb-6 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg text-sm flex items-center gap-2">
                    <span className="w-2 h-2 bg-red-500 rounded-full animate-pulse shrink-0" />
                    {error}
                </div>
            )}

            <div className="bg-white rounded-xl shadow-md overflow-hidden flex flex-col border border-[#231F20]/10">
                <div className="grid gap-4 bg-slate-50 border-b border-[#231F20]/10 px-6 py-4 text-xs font-bold text-[#231F20]/80 uppercase tracking-wider sticky top-0 z-10"
                    style={{ gridTemplateColumns: `repeat(${columns.length}, minmax(0, 1fr)) 120px` }}>
                    {columns.map((col) => <div key={col.key} className="truncate">{col.label}</div>)}
                    <div className="text-right">Actions</div>
                </div>

                <div className="divide-y divide-[#231F20]/5 flex-1">
                    {renderTableBody()}
                </div>

                {!loading && data.length > 0 && (
                    <div className="px-6 py-4 border-t border-[#231F20]/10 bg-slate-50 flex items-center justify-between flex-wrap gap-4">
                        <div className="flex items-center gap-3">
                            <span className="text-xs font-medium text-[#231F20]/70">Rows per page:</span>
                            <select value={pagination.sizePerPage}
                                onChange={(e) => setPagination((p) => ({ ...p, sizePerPage: Number.parseInt(e.target.value, 10), page: 0 }))}
                                className="text-xs px-3 py-1.5 border border-[#231F20]/20 rounded-lg bg-white text-[#231F20] focus:outline-none focus:border-[#006E74] font-semibold cursor-pointer">
                                {[5, 10, 25, 50].map((n) => <option key={n} value={n}>{n}</option>)}
                            </select>
                        </div>

                        <div className="flex items-center gap-6">
                            <span className="text-xs text-[#231F20]/70">
                                Page <span className="font-bold text-[#231F20]">{currentPage + 1}</span> of{" "}
                                <span className="font-bold text-[#231F20]">{totalPages}</span>
                                {" "}·{" "}
                                <span className="font-bold text-[#231F20]">{totalRecords}</span> total
                            </span>

                            <div className="flex items-center gap-1">
                                <button type="button" onClick={() => goToPage(currentPage - 1)} disabled={currentPage === 0}
                                    className={`w-8 h-8 flex items-center justify-center rounded-lg border transition-colors ${
                                        currentPage === 0
                                            ? "opacity-40 cursor-not-allowed bg-slate-100 border-[#231F20]/10 text-[#231F20]/40"
                                            : "bg-white border-[#231F20]/20 hover:bg-slate-50 text-[#231F20] cursor-pointer"
                                    }`}>
                                    <ChevronLeft size={16} />
                                </button>

                                {getVisiblePages().map((pageIndex) => (
                                    <button type="button" key={`page-${pageIndex}`}
                                        onClick={() => goToPage(pageIndex)}
                                        className={`w-8 h-8 flex items-center justify-center rounded-lg border text-xs font-bold transition-colors ${
                                            currentPage === pageIndex
                                                ? "bg-[#006E74] border-[#006E74] text-white"
                                                : "bg-white border-[#231F20]/20 text-[#231F20]/80 hover:bg-slate-50 cursor-pointer"
                                        }`}>
                                        {pageIndex + 1}
                                    </button>
                                ))}

                                <button type="button" onClick={() => goToPage(currentPage + 1)} disabled={currentPage >= totalPages - 1}
                                    className={`w-8 h-8 flex items-center justify-center rounded-lg border transition-colors ${
                                        currentPage >= totalPages - 1
                                            ? "opacity-40 cursor-not-allowed bg-slate-100 border-[#231F20]/10 text-[#231F20]/40"
                                            : "bg-white border-[#231F20]/20 hover:bg-slate-50 text-[#231F20] cursor-pointer"
                                    }`}>
                                    <ChevronRight size={16} />
                                </button>
                            </div>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}

BaseListForm.propTypes = {
    title:         PropTypes.string.isRequired,
    entity:        PropTypes.string.isRequired,
    columns:       PropTypes.arrayOf(PropTypes.shape({
        key:    PropTypes.string.isRequired,
        label:  PropTypes.string.isRequired,
        render: PropTypes.func,
    })),
    addPath:       PropTypes.string.isRequired,
    editPath:      PropTypes.string.isRequired,
    identifierKey: PropTypes.string,
};