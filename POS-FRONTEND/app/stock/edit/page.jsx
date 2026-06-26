"use client";

import { useSearchParams } from "next/navigation";
import CommonUpdateTemplate from "../../components/CommonUpdateTemplate";
import { stockExtraFieldsEdit } from "../../components/stockFields";

export default function StockEdit() {
    const searchParams = useSearchParams();
    const identifier = searchParams.get("identifier");

    return (
        <CommonUpdateTemplate
            title="Stock"
            apiPath="stock"
            recordId={identifier}
            recordParam="identifier"
            recordGetEndpoint="identifier"
            identifierKey="identifier"
            extraFields={stockExtraFieldsEdit}
            onSuccessPath="/stock"
            showDescription={false}
            identifierEditable={false}
        />
    );
}