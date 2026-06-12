"use client";

import React, { useEffect, useState } from "react";

import POSLayout from "../../components/PosLayout";
import SectionForm from "../../components/common/SectionForm";
import commonApi from "../../services/commonApi";

function PriceAdd() {
  const [existingPrice, setExistingPrice] = useState([]);
  const [products, setProducts] = useState([]);

  useEffect(() => {
    fetchPrices();
    fetchProducts();
  }, []);

  const fetchPrices = async () => {
    try {
      const res = await commonApi.list("price", {
        page: 0,
        sizePerPage: 1000,
        sortDirection: "ASC",
        sortField: "id",
        search: "",
      });

      setExistingPrice(res.data.dtoList || []);
    } catch (err) {
      console.log(err);
    }
  };

  const fetchProducts = async () => {
    try {
      const res = await commonApi.list("product", {
        page: 0,
        sizePerPage: 1000,
        sortDirection: "ASC",
        sortField: "id",
        search: "",
      });

      setProducts(res.data.dtoList || []);
    } catch (err) {
      console.log(err);
    }
  };

 const sections = [
  {
    key: "priceInformation",
    title: "Price Information",
    columns: 3,
    fields: [
      {
        key: "productIdentifier",
        label: "Product",
        type: "select",
        options: products,
        optionLabel: "identifier",
        optionValue: "identifier",
        required: true,
      },
      {
        key: "priceType",
        label: "Price Type",
        type: "select",
        options: [
          { label: "MRP", value: "MRP" },
          { label: "Selling Price", value: "SELLING_PRICE" },
          { label: "Cost Price", value: "COST_PRICE" },
        ],
        optionLabel: "label",
        optionValue: "value",
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
        submitUrl="/price/add"
        backUrl="/price"
        sections={sections}
        existingData={existingPrice}
        uniqueFields={["identifier"]}
        initialValues={{
          productIdentifier: "",
          priceType: "",
          priceAmount: "",
        }}
      />
    </POSLayout>
  );
}

export default PriceAdd;