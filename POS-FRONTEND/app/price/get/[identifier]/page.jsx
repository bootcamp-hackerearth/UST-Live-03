"use client";
import { useParams } from "next/navigation";
import { requiredValidation } from "@/validation/validation"
import Update from "@/components/Update";
import WithRoleAccess from '@/components/WithRoleAccess'

const PriceUpdate = () => {
    const params = useParams()

    const urlName = "price"

    const formFelids = [
        { name: "priceAmount", type: "number", validation: requiredValidation, }
    ]
    const dropDowns = [
        { name: "product", urlName: "product/list", httpMethod: "post", ismultiple: false, validation: requiredValidation, isDisabled: true },
    ]

    const hardCodedDropDowns = [
        { name: "priceType", values: ["Selling price", "Cost price", "MRP"], validation: requiredValidation, isDisabled: true }
    ]

    return (
    <WithRoleAccess urlName={urlName}>
        <Update urlName={urlName} formFelids={formFelids} dropDowns={dropDowns} hardCodedDropDowns={hardCodedDropDowns} identifier={params.identifier} />
    </WithRoleAccess>
    )
}

export default PriceUpdate
