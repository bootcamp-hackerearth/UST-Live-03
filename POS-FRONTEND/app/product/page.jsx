"use client";

import ListTemplate from "../components/ListTemplate";

export default function ProductList() {
  const columns = [
    {
      label: "Identifier",
      field: "identifier",
    },
    {
      label: "Product Name",
      field: "productname",
    },
    {
      label: "Brand",
      field: "brand",
    },
    {
      label: "Model",
      field: "model",
    },
    {
      label: "Category",
      field: "category",
    },
    {
      label: "Unit",
      field: "unit",
    },
    {
      label: "Status",
      field: "status",
    },
  ];

  return (
    <ListTemplate
      title="Product Management"
      columns={columns}
      urlName="product"
      showStatus={true}
      editKey="identifier"
      deleteKey="identifier"
      deleteParam="identifier"
      rowKey="identifier"
      addButtonLabel="Product"
      pageSize={10}
      sortField="identifier"
      editUseQuery={true}
    />
  );
}