import List from "@/components/CommonList";

export default function CategoryList() {

    const keys = ["id", "identifier", "superCategory", "status"]
    
    return (

        <List keys={keys} routeName="category" title="Category" />

    )
}