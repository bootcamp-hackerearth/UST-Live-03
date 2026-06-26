import List from "@/components/CommonList";

export default function RacksList() {

    const keys = ["id", "identifier", "shelves", "status"]
    
    return (

        <List keys={keys} routeName="racks" title="Racks" />

    )
}