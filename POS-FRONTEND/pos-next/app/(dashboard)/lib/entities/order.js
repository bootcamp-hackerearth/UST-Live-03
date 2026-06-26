export const order = {
  endpoint: "/api/order/list",
  entityName: "Order",
  columns: [
    { field: "serialNumber", label: "S.No" },
    { field: "identifier", label: "Order Id" },
    { field: "createdOn", label: "Created On" },
    { field: "createdBy", label: "Created By" },
    { field: "modifiedOn", label: "Modified On" },
    { field: "modifiedBy", label: "Modified By" },
    { field: "customerIdentifier", label: "Customer Id" },
    { field: "originalPrice", label: "Order Price" },
    { field: "discount", label: "Discount" },
    { field: "receivedAmount", label: "Amount Paid" },
    { field: "changeAmount", label: "Balance Paid" },
    { field: "paymentMethod", label: "Payment Mode" },
  ],
};