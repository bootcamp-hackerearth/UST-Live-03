"use client";
import CommonList from "@/components/table/CommonList";
const stockColumns = [
  {header: "Identifier",field: "identifier"},
  {header: "Product",field: "product"},
  {header: "Warehouse",field: "warehouse"},
  {header: "Quantity",field: "quantity"},
  {header: "Minimum Stock",field: "minimumStock"},
  {
    header: "Stock Status",
    field: "stockStatus",
    render: (item) => {
      if (item.quantity === 0) {
        return (
          <span className="px-3 py-1 rounded-full text-xs font-medium bg-red-100 text-red-700">
            Out of Stock
          </span>
        );
      }
      if (item.quantity <= item.minimumStock) {
        return (
          <span className="px-3 py-1 rounded-full text-xs font-medium bg-yellow-100 text-yellow-700">
            Low Stock
          </span>
        );
      }
      return (
        <span className="px-3 py-1 rounded-full text-xs font-medium bg-green-100 text-green-700">
          In Stock
        </span>
      );
    },
  },
];
export default function StocksPage() {
  return (
    <CommonList
      title="Stocks"
      subtitle="Manage product stock levels"
      entity="stock"
      addPath="/stocks/add"
      editPath="/stocks/edit"
      columns={stockColumns}
      showToggle={false}
    />
  );
}