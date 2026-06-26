"use client";
import { useParams } from "next/navigation";
import { requiredValidation } from "@/validation/validation"
import Update from "@/components/Update";
import WithRoleAccess from '@/components/WithRoleAccess'

const StockUpdate = () => {
    const params = useParams()

     const urlName = "stock"

    const formFelids = [
        { name: "quantity", type: "text", validation: requiredValidation },
    ]

    const dropDowns = [
        { name: "product", urlName: "product/list", httpMethod: "post", ismultiple: false, validation: requiredValidation, isDisabled:true },
        { name: "warehouse", urlName: "warehouse/list", httpMethod: "post", ismultiple: false, validation: requiredValidation, isDisabled:true  }
    ]
    
    const hardCodedDropDowns = [
        { name: "stockStatus", values: ["InStock", "OutOfStock"], validation: requiredValidation }
    ]

    return (
        <WithRoleAccess urlName={urlName}>
            <Update urlName={urlName} formFelids={formFelids} dropDowns={dropDowns} hardCodedDropDowns={hardCodedDropDowns} identifier={params.identifier} />
        </WithRoleAccess>
    )
}

export default StockUpdate
