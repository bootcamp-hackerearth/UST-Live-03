"use client"
import Add from "@/components/Add"
import { requiredValidation } from "@/validation/validation"
import WithRoleAccess from '@/components/WithRoleAccess'

const UnitAdd = () => {

    const urlName = "unit"

    const formFelids = [
        { name: "identifier", type: "text", validation: requiredValidation },
        { name: "description", type: "text", validation: requiredValidation },
    ]
    const dropDowns = []
    const hardCodedDropDowns = []

    return (
        <WithRoleAccess urlName={urlName}>
            <Add urlName={urlName} formFelids={formFelids} dropDowns={dropDowns} hardCodedDropDowns={hardCodedDropDowns} />
        </WithRoleAccess>
    )
}

export default UnitAdd
