import ListTemplate from "../components/ListTemplate";

export default function PriceList() {
  const columns = [
    { label: "ID", field: "id" },
    { label: "Identifier", field: "identifier" },
    { label: "Product", field: "productName" },
    { label: "Cost Price", field: "costPrice" },
    { label: "Selling Price", field: "sellingPrice" },
    { label: "MRP", field: "mrp" },
    { label: "Status", field: "status" },
  ];

  return (
    <ListTemplate
      title="Price Management"
      columns={columns}
      urlName="price"
      showStatus={true}
      editKey="id"
      deleteKey="identifier"
      deleteParam="identifier"
      statusKey="identifier"
      addButtonLabel="Price"
      pageSize={10}
    />
  );
}