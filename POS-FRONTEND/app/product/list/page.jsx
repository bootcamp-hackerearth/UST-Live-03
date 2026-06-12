import List from "@/app/components/CommonList";

export default function Page() {
  return (
    <List
      urlName="product"
       keys = {[ "identifier", "brand", "models", "unit", "category","status"]}
    />
  );
}