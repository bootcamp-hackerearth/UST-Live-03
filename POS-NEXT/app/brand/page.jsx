import List from "@/components/CommonList";

export default function BrandList() {

    const keys = ["id", "identifier", "description", "status"]
    
    return (

        <List keys={keys} routeName="brand" title="Brand" />

    )
}