"use client";

import { useMemo } from "react";
import { useSearchParams } from "next/navigation";
import CommonUpdateTemplate from "../../components/CommonUpdateTemplate";

export default function RacksEdit() {
    const searchParams = useSearchParams();
    const identifier = searchParams?.get("identifier") || "";

    const extraFields = useMemo(
        () => [
            {
                key: "shelfs",
                label: "Shelfs",
                type: "multiselect",
                apiEndpoint: "/shelfs/findByStatus",
                required: true,
            },
        ],
        []
    );

    if (!identifier) {
        return (
            <div className="w-full max-w-3xl mx-auto py-16 text-center text-slate-600">
                No rack selected for editing.
            </div>
        );
    }

    return (
        <CommonUpdateTemplate
            title="Rack"
            apiPath="racks"
            recordId={identifier}
            recordParam="identifier"
            recordGetEndpoint="identifier"
            identifierKey="identifier"
            identifierLabel="Identifier"
            extraFields={extraFields}
            showDescription={false}
            identifierEditable={false}
            onSuccessPath="/racks"
        />
    );
}