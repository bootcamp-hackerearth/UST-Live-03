import React from "react";
import DynamicList from "../../components/common/DynamicList";
import POSLayout from "../../components/POSLayout";

function PriceList() {

    const token = localStorage.getItem("token");

    const columns = [
        {
            key: "identifier",
            label: "Price Identifier",
            type: "text",
        },
        {
            key: "productIdentifier",
            label: "Product Identifier",
            type: "text",
        },
        {
            key: "priceType",
            label: "Price Type",
            type: "text",
        },
         {
            key: "priceAmount",
            label: "Price Amount",
            type: "text",
        },
    ];

    return (

        <POSLayout>
            <DynamicList
                title="Price List"
                apiUrl="http://localhost:8080/api/price/list"
                token={token}
                columns={columns}

                editUrl="/price/edit"    
                addUrl={"/price/add"}
                deleteUrl="http://localhost:8080/api/price/delete"
            />
        </POSLayout>

    );
}

export default PriceList;