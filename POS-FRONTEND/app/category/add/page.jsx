"use client"
import Add from "@/components/Add"
import { requiredValidation } from "@/validation/validation"
import WithRoleAccess from '@/components/WithRoleAccess'

const CategoryAdd = () => {

    const urlName = "category"

    const formFelids = [
        { name: "identifier", type: "text", validation: requiredValidation },
    ]
    const dropDowns = [
        { name: "superCategory", urlName: "category/list", httpMethod: "post", ismultiple: true}
    ]
    const hardCodedDropDowns = []

    return (
        <WithRoleAccess urlName={urlName}>
            <Add urlName={urlName} formFelids={formFelids} dropDowns={dropDowns} hardCodedDropDowns={hardCodedDropDowns} />
        </WithRoleAccess>
    )
}

export default CategoryAdd
