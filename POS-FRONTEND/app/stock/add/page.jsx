"use client";

import React, { useEffect, useState } from "react";

import POSLayout from "../../components/PosLayout";
import SectionForm from "../../components/common/SectionForm";
import commonApi from "../../services/commonApi";

function StockAdd() {
  const [existingStock, setExistingStock] = useState([]);
  const [products, setProducts] = useState([]);
  const [warehouses, setWarehouses] = useState([]);

  useEffect(() => {
    fetchProducts();
    fetchWarehouses();
    fetchStocks();
  }, []);

  const fetchProducts = async () => {
    try {
      const res = await commonApi.active("product");
      setProducts(Array.isArray(res.data) ? res.data : []);
    } catch (err) {
      console.log(err);
    }
  };

  const fetchWarehouses = async () => {
    try {
      const res = await commonApi.list("warehouse", {
        page: 0,
        sizePerPage: 1000,
        sortDirection: "ASC",
        sortField: "id",
        search: "",
      });

      setWarehouses(res.data.dtoList || []);
    } catch (err) {
      console.log(err);
    }
  };

  const fetchStocks = async () => {
    try {
      const res = await commonApi.list("stock", {
        page: 0,
        sizePerPage: 1000,
        sortDirection: "ASC",
        sortField: "id",
        search: "",
      });

      setExistingStock(res.data.dtoList || []);
    } catch (err) {
      console.log(err);
    }
  };

  const sections = [
    {
      title: "Stock Information",
      columns: 2,
      fields: [
        {
          key: "product",
          label: "Product",
          type: "select",
          options: products,
          optionLabel: "identifier",
          optionValue: "identifier",
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
        routeName="stock"
        submitUrl="/stock/add"
        backUrl="/stock"
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