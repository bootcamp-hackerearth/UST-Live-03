import List from "@/components/CommonList";

export default function WarehouseList() {

    const keys = ["identifier", "contactName", "contactNumber", "status", "location", "region", "country"]
    
    return (

        <List keys={keys} routeName="warehouse" title="Warehouse" />

    )
}