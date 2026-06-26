"use client";
import { useParams } from "next/navigation";
import { requiredValidation } from "@/validation/validation"
import Update from "@/components/Update";
import WithRoleAccess from '@/components/WithRoleAccess'

const UnitUpdate = () => {
    const params = useParams()

    const urlName = "models"

    const dropDowns = []
    const hardCodedDropDowns = []

    const formFelids = [
        { name: "identifier", type: "text", validation: requiredValidation, isDisabled:true },
        { name: "description", type: "text", validation: requiredValidation },
    ]

    return (
        <WithRoleAccess urlName={urlName}>
            <Update urlName={urlName} formFelids={formFelids} dropDowns={dropDowns} hardCodedDropDowns={hardCodedDropDowns} identifier={params.identifier} />
        </WithRoleAccess>
    )
}

export default UnitUpdate
