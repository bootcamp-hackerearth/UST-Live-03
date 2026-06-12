"use client";

import React, { useEffect, useState } from "react";
import { useParams } from "next/navigation";

import POSLayout from "../../../components/PosLayout";
import SectionForm from "../../../components/common/SectionForm";
import commonApi from "../../../services/commonApi";

function ProductEdit() {
  const params = useParams();
  const identifier = params.identifier;

  const [initialValues, setInitialValues] = useState(null);
  const [categories, setCategories] = useState([]);
  const [brands, setBrands] = useState([]);
  const [models, setModels] = useState([]);
  const [units, setUnits] = useState([]);

  useEffect(() => {
    fetchProduct();
    fetchDropdowns();
  }, []);

  const fetchProduct = async () => {
    const res = await commonApi.get("product", "identifier", identifier);
    setInitialValues(res.data);
  };

  const fetchDropdowns = async () => {
    const categoryRes = await commonApi.active("category");
    const brandRes = await commonApi.active("brand");
    const modelRes = await commonApi.active("model");
    const unitRes = await commonApi.active("unit");

    setCategories(categoryRes.data || []);
    setBrands(brandRes.data || []);
    setModels(modelRes.data || []);
    setUnits(unitRes.data || []);
  };

  if (!initialValues) {
    return <POSLayout>Loading...</POSLayout>;
  }

  const sections = [
    {
      title: "Basic Information",
      columns: 2,
      fields: [
        {
          key: "identifier",
          label: "Product Identifier",
          type: "text",
          disabled: true,
          required: true,
        },
        {
          key: "name",
          label: "Product Name",
          type: "text",
          required: true,
          disabled: true,
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
        title="Edit Product"
        submitUrl="/product/update"
        backUrl="/product"
        sections={sections}
        initialValues={initialValues}
      />
    </POSLayout>
  );
}

export default ProductEdit;