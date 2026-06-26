"use client";

import { useMemo } from "react";
import { useSearchParams } from "next/navigation";
import CommonUpdateTemplate from "../../components/CommonUpdateTemplate";

export default function ShelfsEdit() {
    const searchParams = useSearchParams();
    const identifier = searchParams?.get("identifier") || "";

    const extraFields = useMemo(
        () => [
            {
                key: "status",
                label: "Status",
                type: "select",
                options: [
                    { value: "true", label: "Active" },
                    { value: "false", label: "Inactive" },
                ],
                valueType: "boolean",
                required: true,
            },
        ],
        []
    );

    if (!identifier) {
        return (
            <div className="w-full max-w-3xl mx-auto py-16 text-center text-slate-600">
                No shelfs selected for editing.
            </div>
        );
    }

    return (
        <CommonUpdateTemplate
            title="Shelfs"
            apiPath="shelfs"
            recordId={identifier}
            recordParam="identifier"
            recordGetEndpoint="identifier"
            identifierKey="identifier"
            identifierLabel="Identifier"
            extraFields={extraFields}
            showDescription={false}
            identifierEditable={false}
            onSuccessPath="/shelfs"
        />
    );
}