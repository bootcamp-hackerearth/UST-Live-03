"use client";

import ListTemplate from "../components/ListTemplate";

export default function PriceList() {
  const columns = [
    {
      label: "Identifier",
      field: "identifier",
    },
    {
      label: "Cost Price",
      field: "costprice",
    },
    {
      label: "Selling Price",
      field: "sellingprice",
    },
    {
      label: "MRP",
      field: "mrpprice",
    },
    {
      label: "Status",
      field: "status",
    },
  ];

  return (
    <ListTemplate
      title="Price Management"
      columns={columns}
      urlName="price"
      showStatus={true}
      editKey="identifier"
      deleteKey="identifier"
      deleteParam="identifier"
      rowKey="identifier"
      addButtonLabel="Price"
      pageSize={10}
      sortField="identifier"
      editUseQuery={true}
    />
  );
}
