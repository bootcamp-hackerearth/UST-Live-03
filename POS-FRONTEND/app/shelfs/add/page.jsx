"use client"
import Add from "@/components/Add"
import { requiredValidation } from "@/validation/validation"
import WithRoleAccess from '@/components/WithRoleAccess'

const ShelfsAdd = () => {

    const urlName = "shelfs"

    const formFelids = [
        { name: "identifier", type: "text", validation: requiredValidation },
    ]
    const dropDowns = []
    const hardCodedDropDowns = []

    return (
        <WithRoleAccess urlName={urlName}>
            <Add urlName={urlName} formFelids={formFelids} dropDowns={dropDowns} hardCodedDropDowns={hardCodedDropDowns} />
        </WithRoleAccess>
    )
}

export default ShelfsAdd
