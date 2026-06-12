"use client";

import React, { useEffect, useState } from "react";

import POSLayout from "../../components/PosLayout";
import SectionForm from "../../components/common/SectionForm";
import commonApi from "../../services/commonApi";

function ProductAdd() {
  const [categories, setCategories] = useState([]);
  const [brands, setBrands] = useState([]);
  const [models, setModels] = useState([]);
  const [units, setUnits] = useState([]);
  const [existingProducts, setExistingProducts] = useState([]);

  useEffect(() => {
    fetchDropdowns();
    fetchProducts();
  }, []);

  const fetchDropdowns = async () => {
    try {
      const [
        categoryRes,
        brandRes,
        modelRes,
        unitRes,
      ] = await Promise.all([
        commonApi.active("category"),
        commonApi.active("brand"),
        commonApi.active("model"),
        commonApi.active("unit"),
      ]);

      setCategories(categoryRes.data || []);
      setBrands(brandRes.data || []);
      setModels(modelRes.data || []);
      setUnits(unitRes.data || []);
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

      setExistingProducts(res.data.dtoList || []);
    } catch (err) {
      console.log(err);
    }
  };

  const sections = [
    {
      title: "Basic Information",
      columns: 2,
      fields: [
        {
          key: "identifier",
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
        
      ],
    },
    {
      title: "Classification",
      columns: 2,
      fields: [
        {
          key: "categories",
          label: "Categories",
          type: "multiselect",
          options: categories,
          optionLabel: "identifier",
          optionValue: "identifier",
          required: true,
        },
        {
          key: "unit",
          label: "Unit",
          type: "select",
          options: units,
          optionLabel: "identifier",
          optionValue: "identifier",
          required: true,
        },
        {
          key: "brand",
          label: "Brand",
          type: "select",
          options: brands,
          optionLabel: "identifier",
          optionValue: "identifier",
          required: true,
        },
        {
          key: "model",
          label: "Model",
          type: "select",
          options: models,
          optionLabel: "identifier",
          optionValue: "identifier",
          required: true,
        },
        {
      key: "status",
      label: "Status",
      type: "select",
      options: [
        {
          label: "Active",
          value: true,
        },
        {
          label: "Inactive",
          value: false,
        },
      ],
      optionLabel: "label",
      optionValue: "value",
      required: true,
    },
      ],
    },
  ];

  return (
    <POSLayout>
      <SectionForm
        title="Add New Product"
        submitUrl="/product/add"
        backUrl="/product"
        sections={sections}
        existingData={existingProducts}
        uniqueFields={["identifier"]}
        initialValues={{
          identifier: "",
          name: "",
          categories: [],
          brand: "",
          model: "",
          shelf: [],
          unit: "",
        }}
      />
    </POSLayout>
  );
}

export default ProductAdd;