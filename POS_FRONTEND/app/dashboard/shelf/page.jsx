"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import axios from "@/config/axiosConfig";
import CommonList from "@/components/CommonList";
import Toggle from "@/components/Toggle";

export default function ShelfListPage() {

    const router = useRouter();
    const [shelves, setShelves] = useState([]);

    const fetchShelves = async () => {
        try {
            const res = await axios.post("/shelf/list", {
                page: 0,
                sizePerPage: 50
            });
            setShelves(res.data.content || []);
        } catch (e) {
            console.log(e);
            setShelves([]);
        }
    };

    useEffect(() => {
        fetchShelves();
    }, []);

    const toggleStatus = async (row) => {
        try {
            await axios.put(
                `/shelf/toggle-status?identifier=${row.identifier}`
            );
            fetchShelves();
        } catch (e) {
            console.log(e);
        }
    };

    const handleEdit = (row) => {
        router.push(`/dashboard/shelf/edit/${row.identifier}`);
    };

    const handleDelete = async (row) => {
        if (!confirm("Delete this shelf?")) return;

        try {
            await axios.delete(
                `/shelf/delete?identifier=${row.identifier}`
            );
            fetchShelves();
        } catch (e) {
            console.log(e);
        }
    };

    const columns = [
        { header: "ID", accessor: "id" },
        { header: "Shelf Name", accessor: "identifier" },
        {
            header: "Description",
            accessor: "description",
            render: (row) => row.description || "-"
        },
        {
            header: "Status",
            accessor: "status",
            render: (row) => (
                <Toggle
                    active={Boolean(row.status)}
                    onToggle={() => toggleStatus(row)}
                />
            )
        }
    ];

    return (
        <div className="p-6">

            <div className="flex justify-between mb-5">
                <h1 className="text-2xl font-bold">Shelf List</h1>

                <div className="flex gap-3">
                    <button
                        onClick={() => router.push("/dashboard/shelf/add")}
                        className="bg-slate-900 text-white px-4 py-2 rounded"
                    >
                        Add Shelf
                    </button>

                    <button
                        onClick={() => router.push("/")}
                        className="bg-gray-200 px-4 py-2 rounded-lg"
                    >
                        Home
                    </button>
                </div>
            </div>

            <CommonList
                data={shelves}
                columns={columns}
                onEdit={handleEdit}
                onDelete={handleDelete}
            />

        </div>
    );
}