import List from "@/components/CommonList";

export default function CategoryList() {

    const keys = ["id", "identifier", "superCategory"]
    
    return (

        <List keys={keys} routeName="category" title="Category" />

    )
}