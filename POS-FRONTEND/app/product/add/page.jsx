"use client"
import Add from "@/components/Add"
import { requiredValidation } from "@/validation/validation"
import WithRoleAccess from '@/components/WithRoleAccess'
import { ProjectPageDropDowns } from "@/components/productPageDropDown"

const ProductAdd = () => {

    const urlName = "product"

    const formFelids = [
        { name: "identifier", type: "text", validation: requiredValidation },
        { name: "name", type: "text", validation: requiredValidation },
    ]
    const dropDowns = ProjectPageDropDowns

    const hardCodedDropDowns = []

    return (
        <WithRoleAccess urlName={urlName}>
            <Add urlName={urlName} formFelids={formFelids} dropDowns={dropDowns} hardCodedDropDowns={hardCodedDropDowns} />
        </WithRoleAccess>
    )
}

export default ProductAdd
