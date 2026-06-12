import List from "@/components/CommonList";

export default function ProductList() {

  const keys = ["identifier", "name", "unit", "brand", "category", "model", "status"]
  return (

    <List routeName="product" keys={keys} title="Product" />

  );
}