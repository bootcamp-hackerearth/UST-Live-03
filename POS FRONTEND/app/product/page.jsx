"use client";
import ListTemplate from "../components/ListTemplate";
import { auditColumns } from "../components/auditColumns";

export default function ProductList() {
  const columns = [
    { label: "ID", field: "id" },
    { label: "SKU", field: "identifier" },
    { label: "Description", field: "description" },
    {
      label: "Category",
      render: (item) =>
        item.category?.map((category) => (
          <span
            key={category}
            className="bg-blue-100 text-blue-600 px-2 py-1 rounded text-xs mr-1"
          >
            {category}
          </span>
        )),
    },
    { label: "Brand", field: "brand" },
    { label: "Model", field: "model" },
    { label: "Name", field: "productName" },
    { label: "Status", field: "status" },
    ...auditColumns(),
  ];

  return (
    <ListTemplate
      title="Product Management"
      columns={columns}
      urlName="product"
      showStatus={true}
      statusKey="identifier"
      editKey="id"
      deleteKey="identifier"
      deleteParam="identifier"
      addButtonLabel="Product"
      pageSize={10}
    />
  );
}