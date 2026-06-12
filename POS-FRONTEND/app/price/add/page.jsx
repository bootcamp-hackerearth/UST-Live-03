"use client"
import Add from "@/components/Add"
import { requiredValidation } from "@/validation/validation"
import WithRoleAccess from '@/components/WithRoleAccess'

const PriceAdd = () => {

    const urlName = "price"

    const formFelids = [
        { name: "priceAmount", type: "number", validation: requiredValidation }
    ]
    const dropDowns = [
        { name: "product", urlName: "product/list", httpMethod: "post", ismultiple: false, validation: requiredValidation },
    ]

    const hardCodedDropDowns = [
        { name: "priceType", values: ["Selling price", "Cost price", "MRP"], validation: requiredValidation }
    ]

    return (
        <WithRoleAccess urlName={urlName}>
            <Add urlName={urlName} formFelids={formFelids} dropDowns={dropDowns} hardCodedDropDowns={hardCodedDropDowns} />
        </WithRoleAccess>
    )
}

export default PriceAdd
