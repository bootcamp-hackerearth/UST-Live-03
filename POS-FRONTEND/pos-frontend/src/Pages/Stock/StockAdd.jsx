import React, { useEffect, useState } from "react";
import axios from "axios";
import POSLayout from "../../components/POSLayout";
import SectionForm from "../../components/common/SectionForm";

function StockAdd() {
    const token = localStorage.getItem("token");
    const [existingStock, setExistingStock] = useState([]);
    const [products, setProducts] = useState([]);
    const [warehouses, setWarehouses] = useState([]);
    const base = "http://localhost:8080/api";

    useEffect(() => {
        const headers = { Authorization: `Bearer ${token}` };
        axios.get(`${base}/product/active`, { headers })
            .then((r) => setProducts(Array.isArray(r.data) ? r.data : []))
            .catch(console.log);
    }, [token]);

    useEffect(() => {
        const headers = { Authorization: `Bearer ${token}` };
        axios.post(
            `${base}/warehouse/list`,
            { page: 0, sizePerPage: 1000, sortDirection: "ASC", sortField: "id", search: "" },
            { headers }
        )
            .then((r) => setWarehouses(Array.isArray(r.data) ? r.data : []))
            .catch(console.log);
    }, [token]);

    useEffect(() => {
        const headers = { Authorization: `Bearer ${token}` };
        axios.post(
            `${base}/stock/list`,
            { page: 0, sizePerPage: 1000, sortDirection: "ASC", sortField: "id", search: "" },
            { headers }
        ).then((r) => setExistingStock(Array.isArray(r.data) ? r.data : [])).catch(console.log);
    }, [token]);

    const sections = [
        {
            title: "Price Information",
            columns: 2,
            fields: [
                {
                    key: "product",
                    label: "Product",
                    type: "select",
                    options: products,
                    optionLabel: "name",
                    optionValue: "name",
                    required: true,
                },
                {
                    key: "warehouse",
                    label: "Warehouse",
                    type: "select",
                    options: warehouses,
                    optionLabel: "name",
                    optionValue: "name",
                    required: true,
                },
                {
                    key: "minimumStock",
                    label: "Minimum Stock",
                    type: "number",
                    placeholder: "Enter Minimum Stock",
                    required: true,
                },
                {
                    key: "quantity",
                    label: "Quantity",
                    type: "number",
                    placeholder: "Enter Quantity",
                    required: true,
                },
            ],
        },
    ];

    return (
        <POSLayout>
            <SectionForm
                title="Add New Stock"
                token={token}
                submitUrl="http://localhost:8080/api/stock/add"
                backUrl="/stocks"
                sections={sections}
                existingData={existingStock}
                uniqueFields={["identifier"]}
                initialValues={{
                    product: "",
                    warehouse: "",
                    minimumStock: "",
                    quantity: "",
                }}
            />
        </POSLayout>
    );
}

export default StockAdd;