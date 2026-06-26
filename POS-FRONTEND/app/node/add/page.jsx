"use client"
import Add from "@/components/Add"
import { requiredValidation,pathValidation } from "@/validation/validation"
import WithRoleAccess from '@/components/WithRoleAccess'

const NodeAdd = () => {

    const urlName = "node"

    const formFelids = [
        { name: "identifier", type: "text", validation: requiredValidation, isDisabled: false },
        { name: "path", type: "text", validation: pathValidation },
    ]
    const dropDowns = [
        { name: "roles", urlName: "role/list", httpMethod: "post", ismultiple: true, validation: requiredValidation }
    ]
    const hardCodedDropDowns = []

    return (
        <WithRoleAccess urlName={urlName}>
            <Add urlName={urlName} formFelids={formFelids} dropDowns={dropDowns} hardCodedDropDowns={hardCodedDropDowns} />
        </WithRoleAccess>
    )
}

export default NodeAdd
