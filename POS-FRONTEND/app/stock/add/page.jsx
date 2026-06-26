"use client";

import CommonAddTemplate from "../../components/CommonAddTemplate";
import { stockExtraFieldsAdd } from "../../components/stockFields";

export default function StockAdd() {
    return (
        <CommonAddTemplate
            title="Stock"
            apiPath="stock"
            identifierKey="identifier"
            identifierLabel="Identifier"
            extraFields={stockExtraFieldsAdd}
            onSuccessPath="/stock"
        />
    );
}