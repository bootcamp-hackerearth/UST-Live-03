import React from "react";
import DynamicList from "../../components/common/DynamicList";
import POSLayout from "../../components/POSLayout";

function StockList() {

    const token = localStorage.getItem("token");

    const columns = [
        {
            key: "identifier",
            label: "Stock Identifier",
            type: "text",
        },
        {
            key: "product",
            label: "Product",
            type: "text",
        },
        {
            key: "warehouse",
            label: "Warehouse",
            type: "text",
        },
         {
            key: "minimumStock",
            label: "Minimum Stock",
            type: "text",
        },
          {
            key: "quantity",
            label: "Quantity",
            type: "text",
        },
         {
            key: "status",
            label: "Status",
            type: "stockStatus",

            toggleUrl:
                "http://localhost:8080/api/stock/toggle",

        },

    ];

    return (

        <POSLayout>
            <DynamicList
                title="Stock List"
                apiUrl="http://localhost:8080/api/stock/list"
                token={token}
                columns={columns}

                editUrl="/stock/edit"
                addUrl="/stock/add"
                deleteUrl="http://localhost:8080/api/stock/delete"
            />
        </POSLayout>

    );
}

export default StockList;