import React, { useEffect, useState } from "react";
import axios from "axios";
import POSLayout from "../../components/POSLayout";
import SectionForm from "../../components/common/SectionForm";

function PriceAdd() {
    const token = localStorage.getItem("token");
    const [existingPrice, setExistingPrice] = useState([]);

    useEffect(() => {
        const headers = { Authorization: `Bearer ${token}` };
        const base = "http://localhost:8080/api";
        axios.post(
            `${base}/price/list`,
            { page: 0, sizePerPage: 1000, sortDirection: "ASC", sortField: "id", search: "" },
            { headers }
        ).then((r) => setExistingPrice(Array.isArray(r.data) ? r.data : [])).catch(console.log);
    }, [token]);

    const sections = [
        {
            title: "Price Information",
            columns: 2,
            fields: [
                {
                    key: "productIdentifier",
                    label: "Product Identifier",
                    type: "text",
                    placeholder: "Enter Product Identifier",
                    required: true,
                },
                {
                    key: "name",
                    label: "Product Name",
                    type: "text",
                    placeholder: "Enter Product Name",
                    required: true,
                },
                {
                    key: "priceType",
                    label: "Price Type",
                    type: "text",
                    placeholder: "Enter Price Type",
                    required: true,
                },
                {
                    key: "priceAmount",
                    label: "Price Amount",
                    type: "text",
                    placeholder: "Enter Price Amount",
                    required: true,
                },
            ],
        },
    ];

    return (
        <POSLayout>
            <SectionForm
                title="Add New Price"
                token={token}
                submitUrl="http://localhost:8080/api/price/add"
                backUrl="/prices"
                sections={sections}
                existingData={existingPrice}
                uniqueFields={["identifier"]}
                initialValues={{
                    productIdentifier: "",
                    name: "",
                    priceType: "",
                    priceAmount: "",
                }}
            />
        </POSLayout>
    );
}

export default PriceAdd;