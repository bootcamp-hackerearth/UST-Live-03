import List from "@/components/CommonList";

export default function UnitList() {

    const keys = ["id", "identifier", "status"]
    
    return (

        <List keys={keys} routeName="unit" title="Unit" />

    )
}