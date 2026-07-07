"use client"
import { useState, useEffect } from "react"
import PropTypes from 'prop-types'
import { Pencil, Trash2, Plus, Search } from 'lucide-react'
import { useRouter } from "next/navigation"
import AccessDenied from "./AccessDenied"

const ListingPage = (props) => {

    const navigate = useRouter();
    const keys = props.keys
    const urlName = props.urlName


    const [listData, setListData] = useState([])
    const [accessDenied, setAccessDenied] = useState(false)

    const [paginationDto, setPaginationDto] = useState({
        "page": 0,
        "sizePerPage": 4,
        "keyword": ""
    })

    const [totalPages, setTotalPages] = useState(0)

    const numbers = Array.from({ length: totalPages }, (_, i) => i + 1);

    async function fetchList() {
        const res = await fetch(`${process.env.NEXT_PUBLIC_BASE_URL}/${urlName}/list`, {
            method: "post",
            headers: {
                "Content-Type": "application/json",
            },
            credentials: "include",
            body: JSON.stringify(paginationDto)
        });

        const response = await res.json();
        console.log(response)
        if (res.status == "403") {
            setAccessDenied(true);
        }
        setListData(response.dtoList)
        setTotalPages(response.totalPages)
    }


    useEffect(() => {
        fetchList()
    }, [])

    useEffect(() => {
        fetchList()
        console.log(listData)
    }, [paginationDto])

    const deleteItem = async (identifier) => {

        const confirmed = confirm(`Are you sure to delete ${identifier}?`);

        if (!confirmed) {
            return;
        }

        const identifierKey = listData?.[0]?.identifier !== null && listData?.[0]?.identifier !== undefined ? "identifier" : "username";
        const res = await fetch(`${process.env.NEXT_PUBLIC_BASE_URL}/${urlName}/delete?${identifierKey}=${identifier}`, {
            method: "delete",
            headers: {
                "Content-Type": "application/json",
            },
            credentials: "include",
        });
        if (res.status == "403") {
            setAccessDenied(true);
        }
        fetchList()
    }

    const getItemToUpdate = (identifier) => {
        navigate.push(`/${urlName}/get/${identifier}`)
    }

    const handleToggle = async (identifier) => {
        await fetch(`${process.env.NEXT_PUBLIC_BASE_URL}/${urlName}/toggle?identifier=${identifier}`, {
            method: "post",
            headers: {
                "Content-Type": "application/json",
            },
            credentials: "include",
        });
        fetchList()
    }

    const renderCellValue = (item, key) => {
        const value = item[key]

        if (key === "status" && typeof value === "boolean") {
            return (
                <button
                    onClick={() => handleToggle(item.identifier)}
                    className={`w-21 px-2.5 py-1 text-xs font-semibold rounded-full transition-all cursor-pointer shadow-sm border ${value
                        ? "bg-emerald-50 text-emerald-700 border-emerald-200 hover:bg-emerald-100"
                        : "bg-slate-50 text-slate-600 border-slate-200 hover:bg-slate-100"
                        }`}
                >
                    {value ? "● Active" : "○ Inactive"}
                </button>
            )
        }

        if (typeof value === "boolean") {
            return (
                <span className={`inline-flex items-center text-xs font-semibold px-2 py-0.5 rounded ${value ? "text-emerald-700" : "text-slate-500"}`}>
                    {value ? "Active" : "Inactive"}
                </span>
            )
        }

        if (Array.isArray(value)) {
            return (
                <span className="bg-slate-100 text-slate-700 text-xs px-2 py-1 rounded-md">
                    {value.length ? value.join(", ") : "-"}
                </span>
            )
        }

        return (
            <span>{value ? String(value) : "-"}</span>
        )
    }

    const searchChange = async (e) => {
        console.log(e.target.value)
        setPaginationDto({
            ...paginationDto,
            keyword: e.target.value
        })
    }

    return (
        <>
            {accessDenied ? <AccessDenied /> : ""}
            <div className="p-8 bg-slate-50 min-h-screen">
                <div className="max-w-7xl mx-auto bg-white rounded-xl border border-slate-200/80 shadow-sm overflow-hidden">

                    <div className="flex items-center justify-between p-6 border-b border-slate-100 bg-white">
                        <div>
                            <h3 className="text-xl font-bold tracking-tight text-slate-900">
                                {urlName.toUpperCase()} MANAGEMENT
                            </h3>
                        </div>
                        <button
                            className="inline-flex items-center gap-1.5 py-2 px-4 bg-slate-900 hover:bg-slate-800 text-white rounded-lg text-sm font-medium shadow-sm transition-colors cursor-pointer"
                            onClick={() => { navigate.push(`${urlName}/add`) }}
                        >
                            <Plus className="w-4 h-4" />
                            Add New
                        </button>
                    </div>

                    {/* Styled Search Input Area */}
                    <div className="p-4 border-b border-slate-100 bg-slate-50/50 flex max-w-md">
                        <div className="relative w-full">
                            <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                                <Search className="h-4 w-4 text-slate-400" />
                            </div>
                            <input
                                type="text"
                                placeholder="Search records..."
                                className="block w-full pl-10 pr-4 py-2 text-sm text-slate-900 border border-slate-200 rounded-lg bg-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-slate-900/10 focus:border-slate-400 transition-all shadow-sm"
                                onChange={searchChange}
                            />
                        </div>
                    </div>

                    <div className="overflow-x-auto">
                        <table className="min-w-full divide-y divide-slate-200 text-left">
                            <thead className="bg-slate-50">
                                <tr>
                                    {keys.map((data) => (
                                        <th key={data} className="p-4 text-xs font-semibold uppercase tracking-wider text-slate-600">
                                            {data.replaceAll(/([A-Z])/g, ' $1').trim()}
                                        </th>
                                    ))}
                                    <th className="p-4 text-xs font-semibold uppercase tracking-wider text-slate-600 text-center w-28">Actions</th>
                                </tr>
                            </thead>

                            <tbody className="bg-white divide-y divide-slate-100">
                                {listData && listData.length > 0 ? (
                                    listData.map((LstData) => (
                                        <tr key={LstData.id} className="hover:bg-slate-50/80 transition-colors">
                                            {keys.map((keyData) => (
                                                <td key={keyData} className="p-4 text-sm font-medium text-slate-700 whitespace-nowrap">
                                                    {renderCellValue(LstData, keyData)}
                                                </td>
                                            ))}

                                            <td className="p-4 whitespace-nowrap">
                                                <div className="flex gap-1 justify-center items-center">
                                                    <button
                                                        onClick={() => getItemToUpdate(LstData.identifier ?? LstData.username)}
                                                        title="Edit Record"
                                                        className="p-1.5 text-slate-500 hover:text-blue-600 hover:bg-blue-50 rounded-md transition-colors cursor-pointer"
                                                    >
                                                        <Pencil className="w-4 h-4" />
                                                    </button>
                                                    <button
                                                        onClick={() => deleteItem(LstData.identifier ?? LstData.username)}
                                                        title="Delete Record"
                                                        className="p-1.5 text-slate-500 hover:text-rose-600 hover:bg-rose-50 rounded-md transition-colors cursor-pointer"
                                                    >
                                                        <Trash2 className="w-4 h-4" />
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>
                                    ))
                                ) : (
                                    <tr>
                                        <td colSpan={keys.length + 1} className="p-12 text-center text-sm text-slate-400 font-medium">
                                            No items available in this list.
                                        </td>
                                    </tr>
                                )}
                            </tbody>
                        </table>
                    </div>

                    {totalPages > 1 && (
                        <div className="flex justify-center items-center p-5 border-t border-slate-100 bg-slate-50/50 gap-1.5 flex-wrap">
                            {numbers.map((num) => (
                                <button
                                    key={num}
                                    onClick={() => {
                                        setPaginationDto(prev => ({
                                            ...prev,
                                            page: num - 1
                                        }));
                                    }}
                                    className={`px-3.5 py-1.5 rounded-lg border text-xs font-semibold transition-all cursor-pointer shadow-sm
                                    ${paginationDto.page === num - 1
                                            ? "bg-slate-900 border-slate-900 text-white"
                                            : "bg-white border-slate-200 text-slate-600 hover:bg-slate-50 hover:text-slate-900"
                                        }`}
                                >
                                    {num}
                                </button>
                            ))}
                        </div>
                    )}

                </div>
            </div>
        </>
    )
}

ListingPage.propTypes = {
    keys: PropTypes.arrayOf(PropTypes.string).isRequired,
    urlName: PropTypes.string.isRequired
}

export default ListingPage