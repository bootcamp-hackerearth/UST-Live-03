"use client";

import { useEffect, useState } from "react";
import ListTemplate from "../components/ListTemplate";
import axiosInstance from "../api/axiosInstance";

export default function StockListPage() {
    const [productNames, setProductNames] = useState({});

    useEffect(() => {
        axiosInstance
            .get("/product/findByStatus")
            .then((res) => {
                const products = Array.isArray(res.data) ? res.data : [];
                const names = {};
                products.forEach((product) => {
                    if (product.identifier) {
                        names[product.identifier] = product.productname || product.identifier;
                    }
                    if (product.productname) {
                        names[product.productname] = product.productname;
                    }
                });
                setProductNames(names);
            })
            .catch(() => setProductNames({}));
    }, []);

    const columns = [
        { field: "identifier", label: "Identifier" },
        {
            field: "productname",
            label: "Product",
            render: (item) => productNames[item.productname] || item.productname || "-",
        },
        { field: "warehouse", label: "Warehouse" },
        { field: "quantity", label: "Quantity" },
        { field: "status", label: "Status" },
    ];

    return (
        <ListTemplate
            title="Stock Management"
            urlName="stock"
            columns={columns}
            rowKey="identifier"
            editKey="identifier"
            deleteKey="identifier"
            deleteParam="identifier"
            showStatus={true}
            editUseQuery={true}
        />
    );
}
