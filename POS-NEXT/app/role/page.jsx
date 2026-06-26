import List from "@/components/CommonList";

export default function RoleList() {

    const keys = ["identifier", "description", "status"]

    return (
        
            <List keys={keys} routeName="role" title="Role" />
    )
}