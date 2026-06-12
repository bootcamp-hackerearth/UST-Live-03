import List from "@/components/CommonList";

export default function PriceList() {

    const keys = ["identifier", "priceAmount", "product", "priceType"]
    return (

        <List keys={keys} routeName="price" title="Price" />
    )
}