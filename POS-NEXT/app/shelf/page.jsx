import List from "@/components/CommonList";

export default function ShelfList() {

    const keys = ["id", "identifier", "status"]
    
    return (

        <List keys={keys} routeName="shelf" title="Shelf" />

    )
}