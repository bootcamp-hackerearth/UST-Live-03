import List from "@/components/CommonList";

export default function ModelsList() {

    const keys = ["id", "identifier", "description", "status"]
    
    return (

        <List keys={keys} routeName="models" title="Model" />

    )
}