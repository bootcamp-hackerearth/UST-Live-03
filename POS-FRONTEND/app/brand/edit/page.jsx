"use client";

import { useMemo } from "react";
import { useSearchParams } from "next/navigation";
import CommonUpdateTemplate from "../../components/CommonUpdateTemplate";

export default function BrandEdit() {
    const searchParams = useSearchParams();
    const identifier = searchParams?.get("identifier") || "";

    const extraFields = useMemo(
        () => [
            {
                key: "description",
                label: "Description",
                type: "text",
                placeholder: "Enter description",
                required: true,
            },
        ],
        []
    );

    if (!identifier) {
        return (
            <div className="w-full max-w-3xl mx-auto py-16 text-center text-slate-600">
                No brand selected for editing.
            </div>
        );
    }

    return (
        <CommonUpdateTemplate
            title="Brand"
            apiPath="brand"
            recordId={identifier}
            recordParam="identifier"
            recordGetEndpoint="identifier"
            identifierKey="identifier"
            identifierLabel="Identifier"
            extraFields={extraFields}
            showDescription={false}
            identifierEditable={false}
            onSuccessPath="/brand"
        />
    );
}