"use client"
import Add from "@/components/Add"
import { requiredValidation } from "@/validation/validation"
import WithRoleAccess from '@/components/WithRoleAccess'

const StockAdd = () => {

    const urlName = "stock"

    const formFelids = [
        { name: "quantity", type: "text", validation: requiredValidation },
    ]

    const dropDowns = [
        { name: "product", urlName: "product/list", httpMethod: "post", ismultiple: false, validation: requiredValidation },
        { name: "warehouse", urlName: "warehouse/list", httpMethod: "post", ismultiple: false, validation: requiredValidation }
    ]

    const hardCodedDropDowns = [
        { name: "stockStatus", values: ["InStock", "OutOfStock"], validation: requiredValidation }
    ]

    return (
        <WithRoleAccess urlName={urlName}>
            <Add urlName={urlName} formFelids={formFelids} dropDowns={dropDowns} hardCodedDropDowns={hardCodedDropDowns} />
        </WithRoleAccess>
    )
}

export default StockAdd
