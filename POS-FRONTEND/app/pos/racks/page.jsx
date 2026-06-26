// app/pos/racks/page.jsx

"use client";

import BaseListForm from "@/components/lists/BaseListForm";

const COLUMNS = [
    { key: "identifier", label: "Rack ID" },
    {
        key: "shelfs",
        label: "Shelves",
        render: (value) => {
            if (!Array.isArray(value) || value.length === 0) return <span className="text-gray-400 text-xs">—</span>;
            return (
                <div className="flex flex-wrap gap-1">
                    {value.map((s) => (
                        <span key={s} className="text-[10px] font-semibold bg-[#006E74]/8 text-[#006E74] border border-[#006E74]/20 px-2 py-0.5 rounded-full">
                            {s}
                        </span>
                    ))}
                </div>
            );
        },
    },
    { key: "status", label: "Status" },
];

export default function RackListPage() {
    return (
        <BaseListForm
            title="Racks"
            entity="rack"
            columns={COLUMNS}
            addPath="/pos/racks/add"
            editPath="/pos/racks/edit"
            identifierKey="identifier"
        />
    );
}