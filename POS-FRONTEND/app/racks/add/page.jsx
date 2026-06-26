"use client"
import Add from "@/components/Add"
import { requiredValidation } from "@/validation/validation"
import WithRoleAccess from '@/components/WithRoleAccess'

const RackAdd = () => {

    const urlName = "racks"

    const formFelids = [
        { name: "identifier", type: "text", validation: requiredValidation },
    ]
    const dropDowns = [
        { name: "shelfs", urlName: "shelfs/getAllActive", httpMethod: "get", ismultiple: true, validation: requiredValidation }
    ]
    const hardCodedDropDowns = []

    return (
        <WithRoleAccess urlName={urlName}>
            <Add urlName={urlName} formFelids={formFelids} dropDowns={dropDowns} hardCodedDropDowns={hardCodedDropDowns} />
        </WithRoleAccess>
    )
}

export default RackAdd
