import List from "@/components/CommonList";

export default function NodeList() {

    const keys = ["identifier", "path", "roles", "status"]
    return (

        <List keys={keys} routeName="node" title="Node" />

    )
}