"use client";

import CommonAddPage from "@/app/components/CommonAddPage";
import api from "@/app/services/api";
import { createDropdownField } from "@/app/components/formFields";

export default function ProductAddPage() {
  const handleSubmit = (data) => {
    console.log("Submitting Product:", data);

    if (
      !data.identifier ||
      !data.category ||
      !data.brand ||
      !data.model ||
      !data.unit
    ) {
      alert("Please fill all required fields");
      return;
    }

    return api.post("/api/product/add", {
      identifier: data.identifier,
      category: data.category,
      brand: data.brand,
      model: data.model,
      unit: data.unit,
      quantity: Number(data.quantity) || 0,
      status: data.status === true || data.status === "true",
    });
  };

  const fields = [
    {
      label: "Identifier",
      name: "identifier",
      type: "text",
      placeholder: "Enter product code",
    },

    createDropdownField("Category", "category", "/api/category/findallactive"),
    createDropdownField("Brand", "brand", "/api/brand/list"),
    createDropdownField("Model", "model", "/api/model/list"),
    createDropdownField("Unit", "unit", "/api/unit/list"),

    {
      label: "Quantity",
      name: "quantity",
      type: "number",
    },
    {
      label: "Status",
      name: "status",
      type: "radio",
      options: [
        { label: "Active", value: true },
        { label: "Inactive", value: false },
      ],
    },
  ];

  return (
    <CommonAddPage
      title="Add Product"
      submitApi={handleSubmit}
      redirectRoute="/product/list"
      initialValues={{
        identifier: "",
        category: "",
        brand: "",
        model: "",
        unit: "",
        quantity: 0,
        status: true,
      }}
      fields={fields}
    />
  );
}