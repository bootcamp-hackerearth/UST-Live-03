"use client"
import { useParams } from "next/navigation"
import CustomerForm from "@/components/CustomerForm"

const CustomerUpdate = () => {
    const { identifier } = useParams()
    return <CustomerForm mode="update" identifier={identifier} />
}

export default CustomerUpdate