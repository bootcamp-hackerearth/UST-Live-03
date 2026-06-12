import List from "@/app/components/CommonList";

export default function Page() {
  return (
    <List
      urlName="price"
       keys = {[ "identifier","product","priceType","sumPrice"]}
    />
  );
}