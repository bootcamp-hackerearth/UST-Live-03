import React, { useEffect, useState } from "react";
import axios from "axios";
import POSLayout from "../../components/POSLayout";
import SectionForm from "../../components/common/SectionForm";

function ProductAdd() {
  const token = localStorage.getItem("token");
  const [categories, setCategories]           = useState([]);
  const [brands, setBrands]                   = useState([]);
  const [models, setModels]                   = useState([]);
  const [shelfs, setShelfs]                   = useState([]);
  const [units, setUnits]                     = useState([]);
  const [existingProducts, setExistingProducts] = useState([]);

  useEffect(() => {
    const headers = { Authorization: `Bearer ${token}` };
    const base    = "http://localhost:8080/api";

    axios.get(`${base}/category/active`, { headers }).then((r) => setCategories(r.data)).catch(console.log);
    axios.get(`${base}/brand/active`,    { headers }).then((r) => setBrands(r.data)).catch(console.log);
    axios.get(`${base}/model/active`,    { headers }).then((r) => setModels(r.data)).catch(console.log);
    axios.get(`${base}/shelf/active`,    { headers }).then((r) => setShelfs(r.data)).catch(console.log);
    axios.get(`${base}/unit/active`,     { headers }).then((r) => setUnits(r.data)).catch(console.log);
    axios.post(
      `${base}/product/list`,
      { page: 0, sizePerPage: 1000, sortDirection: "ASC", sortField: "id", search: "" },
      { headers }
    ).then((r) => setExistingProducts(Array.isArray(r.data) ? r.data : [])).catch(console.log);
  }, [token]);

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
          fullWidth: true,
        },
        {
          key: "name",
          label: "Product Name",
          type: "text",
          placeholder: "Enter Product Name",
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
          key: "models",
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
          key: "shelf",
          label: "Shelf",
          type: "multiselect",
          options: shelfs,
          optionLabel: "identifier",
          optionValue: "identifier",
          required: true,
        },
      ],
    },
  ];

  return (
    <POSLayout>
      <SectionForm
        title="Add New Product"
        token={token}
        submitUrl="http://localhost:8080/api/product/add"
        backUrl="/products"
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