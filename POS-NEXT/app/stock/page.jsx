import List from "@/components/CommonList";

export default function StockList() {

  const keys = ["identifier", "product", "quantity", "stockStatus", "warehouse"]
  return (

    <List routeName="stock" keys={keys} title="Stock" />

  );
}