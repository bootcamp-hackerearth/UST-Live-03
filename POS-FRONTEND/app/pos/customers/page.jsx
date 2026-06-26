"use client";

import { useEffect, useState, useCallback } from "react";
import { useRouter } from "next/navigation";
import api from "@/app/api/axios";
import { Search, Plus, Edit2, Trash2, ChevronLeft, ChevronRight, Users } from "lucide-react";

export default function CustomerListPage() {
    const router = useRouter();

    const [data, setData] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [searchTerm, setSearchTerm] = useState("");
    const [deleting, setDeleting] = useState(null);
    const [totalPages, setTotalPages] = useState(1);
    const [pagination, setPagination] = useState({
        page: 0,
        sizePerPage: 10,
        sortDirection: "ASCENDING",
        sortField: "id",
    });

    const fetchData = useCallback(async () => {
        try {
            setLoading(true);
            setError("");
            const res = await api.post("/customer/list", pagination);
            const raw = res.data;
            if (Array.isArray(raw)) {
                setData(raw);
                setTotalPages(raw[0]?.totalPages || 1);
            } else {
                const list = raw?.dtoList || raw?.content || raw?.list || [];
                setData(list);
                setTotalPages(raw?.totalPages || raw?.totalPage || 1);
            }
        } catch {
            setError("Failed to load customers.");
        } finally {
            setLoading(false);
        }
    }, [pagination]);

    useEffect(() => { fetchData(); }, [fetchData]);

    const handleDelete = async (identifier) => {
        if (!confirm(`Delete customer: ${identifier}?`)) return;
        try {
            setDeleting(identifier);
            await api.delete("/customer/delete", { params: { identifier } });
            setData((prev) => prev.filter((c) => c.identifier !== identifier));
        } catch {
            setError("Delete failed.");
        } finally {
            setDeleting(null);
        }
    };

    const handleToggle = async (customer) => {
        try {
            const res = await api.post(`/customer/toggle?identifier=${encodeURIComponent(customer.identifier)}`, null);
            setData((prev) =>
                prev.map((c) =>
                    c.identifier === customer.identifier
                        ? { ...c, status: res.data?.status ?? !c.status }
                        : c
                )
            );
        } catch {
            setError("Failed to toggle status.");
        }
    };

    const goToPage = (p) => setPagination((prev) => ({ ...prev, page: p }));

    const filtered = data.filter((c) => {
        if (!searchTerm) return true;
        const q = searchTerm.toLowerCase();
        return (
            c.identifier?.toLowerCase().includes(q) ||
            c.customerName?.toLowerCase().includes(q) ||
            c.username?.toLowerCase().includes(q) ||
            c.partyType?.toLowerCase().includes(q)
        );
    });

    const getVisiblePages = () => {
        let start = pagination.page - 1;
        if (start < 0) start = 0;
        if (start + 3 > totalPages) start = Math.max(0, totalPages - 3);
        return Array.from({ length: Math.min(3, totalPages) }, (_, i) => start + i);
    };

    let rows;
    if (loading) {
        rows = (
            Array.from({ length: 6 }, (_, i) => (
                <div key={`skel-${i}`} className="grid grid-cols-[2fr_2fr_1fr_1fr_1fr_120px] gap-4 px-6 py-4 animate-pulse">
                    {Array.from({ length: 6 }, (_, j) => (
                        <div key={j} className="h-4 bg-gray-100 rounded w-3/4" />
                    ))}
                </div>
            ))
        );
    } else if (filtered.length === 0) {
        rows = (
            <div className="text-center py-16 text-gray-400 text-sm">
                {searchTerm ? "No customers match your search." : "No customers found."}
            </div>
        );
    } else {
        rows = filtered.map((c) => (
            <div
                key={c.identifier}
                className="grid grid-cols-[2fr_2fr_1fr_1fr_1fr_120px] gap-4 px-6 py-4 items-center hover:bg-[#006E74]/3 transition-colors text-sm"
            >
                <div>
                    <p className="font-semibold text-[#231F20] truncate">{c.customerName || "Walk-In"}</p>
                    <p className="text-[10px] font-mono text-gray-400 mt-0.5">{c.identifier}</p>
                </div>
                <div className="text-gray-500 truncate text-xs">{c.username || "—"}</div>
                <div>
                    <span className={`text-[10px] font-bold px-2 py-0.5 rounded-full ${c.partyType === "WHOLESALE"
                        ? "bg-amber-50 text-amber-600 border border-amber-200"
                        : "bg-[#006E74]/8 text-[#006E74] border border-[#006E74]/20"
                        }`}>
                        {c.partyType || "RETAIL"}
                    </span>
                </div>
                <div className="text-xs font-semibold text-[#231F20]">
                    {c.balance == null ? (
                        "—"
                    ) : (
                        <span className={c.balanceType === "CREDIT" ? "text-[#006E74]" : "text-[#231F20]"}>
                            ₹{Number(c.balance).toFixed(2)}
                            <span className="text-[9px] text-gray-400 ml-1">{c.balanceType}</span>
                        </span>
                    )}
                </div>
                <div>
                    <button
                        type="button"
                        onClick={() => handleToggle(c)}
                        className={`text-[10px] font-bold px-3 py-1 rounded-full transition-colors border-none cursor-pointer ${c.status
                            ? "bg-emerald-50 text-emerald-600 hover:bg-emerald-100"
                            : "bg-gray-100 text-gray-400 hover:bg-gray-200"
                            }`}
                    >
                        {c.status ? "Active" : "Inactive"}
                    </button>
                </div>
                <div className="flex justify-end gap-2">
                    <button
                        type="button"
                        onClick={() => router.push(`/pos/customers/edit/${encodeURIComponent(c.identifier)}`)}
                        className="flex items-center gap-1 px-2.5 py-1.5 text-xs font-semibold text-[#006E74] border border-[#006E74]/30 rounded-lg hover:bg-[#006E74]/8 transition-colors cursor-pointer"
                    >
                        <Edit2 size={12} /> Edit
                    </button>
                    <button
                        type="button"
                        onClick={() => handleDelete(c.identifier)}
                        disabled={deleting === c.identifier}
                        className="flex items-center gap-1 px-2.5 py-1.5 text-xs font-semibold text-gray-400 border border-gray-200 rounded-lg hover:bg-red-50 hover:text-red-500 hover:border-red-200 transition-colors cursor-pointer disabled:opacity-40"
                    >
                        <Trash2 size={12} /> Delete
                    </button>
                </div>
            </div>
        ));
    }

    return (
        <div className="min-h-screen bg-slate-50 p-6">
            <div className="mb-8 flex items-start justify-between">
                <div className="flex items-center gap-3">
                    <div className="w-10 h-10 rounded-xl bg-[#006E74]/10 flex items-center justify-center">
                        <Users size={20} className="text-[#006E74]" />
                    </div>
                    <div>
                        <h1 className="text-2xl font-bold text-[#231F20]">Customers</h1>
                        <p className="text-xs text-gray-400 mt-0.5">Manage customer profiles and accounts</p>
                    </div>
                </div>
                <button
                    type="button"
                    onClick={() => router.push("/pos/customers/add")}
                    className="flex items-center gap-2 bg-[#006E74] hover:bg-[#0097AC] text-white px-4 py-2.5 rounded-lg text-sm font-semibold transition-colors shadow-md cursor-pointer border-none"
                >
                    <Plus size={16} />
                    Add Customer
                </button>
            </div>

            <div className="relative mb-6">
                <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
                <input
                    type="text"
                    placeholder="Search by name, phone, email, type..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="w-full pl-9 pr-4 py-2.5 bg-white border border-gray-200 rounded-lg text-sm focus:outline-none focus:border-[#006E74] focus:ring-2 focus:ring-[#006E74]/10 text-[#231F20]"
                />
            </div>

            {error && (
                <div className="mb-5 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg text-sm flex items-center gap-2">
                    <span className="w-2 h-2 bg-red-500 rounded-full animate-pulse shrink-0" />
                    {error}
                </div>
            )}

            <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
                <div className="grid grid-cols-[2fr_2fr_1fr_1fr_1fr_120px] gap-4 px-6 py-3 bg-slate-50 border-b border-gray-100 text-[10px] font-bold text-gray-400 uppercase tracking-widest">
                    <div>Customer</div>
                    <div>Email</div>
                    <div>Type</div>
                    <div>Balance</div>
                    <div>Status</div>
                    <div className="text-right">Actions</div>
                </div>

                <div className="divide-y divide-gray-50">{rows}</div>

                {!loading && data.length > 0 && (
                    <div className="px-6 py-4 border-t border-gray-100 bg-slate-50 flex items-center justify-between flex-wrap gap-3">
                        <div className="flex items-center gap-3">
                            <span className="text-xs text-gray-400">Rows per page:</span>
                            <select
                                value={pagination.sizePerPage}
                                onChange={(e) => setPagination((p) => ({ ...p, sizePerPage: Number(e.target.value), page: 0 }))}
                                className="text-xs px-2 py-1.5 border border-gray-200 rounded-lg bg-white text-[#231F20] focus:outline-none cursor-pointer"
                            >
                                {[5, 10, 25, 50].map((n) => <option key={n} value={n}>{n}</option>)}
                            </select>
                        </div>
                        <div className="flex items-center gap-4">
                            <span className="text-xs text-gray-400">
                                Page <b className="text-[#231F20]">{pagination.page + 1}</b> of <b className="text-[#231F20]">{totalPages}</b>
                                {" "}· <b className="text-[#231F20]">{data.length}</b> total
                            </span>
                            <div className="flex items-center gap-1">
                                <button
                                    onClick={() => goToPage(pagination.page - 1)}
                                    disabled={pagination.page === 0}
                                    className="w-8 h-8 flex items-center justify-center rounded-lg border border-gray-200 bg-white text-[#231F20] hover:bg-slate-50 disabled:opacity-40 disabled:cursor-not-allowed cursor-pointer"
                                >
                                    <ChevronLeft size={14} />
                                </button>
                                {getVisiblePages().map((p) => (
                                    <button
                                        key={p}
                                        onClick={() => goToPage(p)}
                                        className={`w-8 h-8 flex items-center justify-center rounded-lg border text-xs font-bold transition-colors cursor-pointer ${pagination.page === p
                                            ? "bg-[#006E74] border-[#006E74] text-white"
                                            : "bg-white border-gray-200 text-gray-600 hover:bg-slate-50"
                                            }`}
                                    >
                                        {p + 1}
                                    </button>
                                ))}
                                <button
                                    onClick={() => goToPage(pagination.page + 1)}
                                    disabled={pagination.page >= totalPages - 1}
                                    className="w-8 h-8 flex items-center justify-center rounded-lg border border-gray-200 bg-white text-[#231F20] hover:bg-slate-50 disabled:opacity-40 disabled:cursor-not-allowed cursor-pointer"
                                >
                                    <ChevronRight size={14} />
                                </button>
                            </div>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}