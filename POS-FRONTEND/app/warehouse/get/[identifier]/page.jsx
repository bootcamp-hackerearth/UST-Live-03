"use client";
import { useParams } from "next/navigation";
import { requiredValidation, nameValidation, phoneValidation } from "@/validation/validation"
import Update from "@/components/Update";
import WithRoleAccess from '@/components/WithRoleAccess'

const UserUpadte = () => {
    const params = useParams()

    const urlName = "warehouse"

    const formFelids = [
        { name: "identifier", type: "text", validation: requiredValidation, isDisabled: true },
        { name: "country", type: "text", validation: requiredValidation },
        { name: "region", type: "text", validation: requiredValidation },
        { name: "location", type: "text", validation: requiredValidation },
        { name: "contactName", type: "text", validation: nameValidation },
        { name: "contactNumber", type: "tel", validation: phoneValidation },
    ]
    const dropDowns = []

    const hardCodedDropDowns = []

    return (
        <WithRoleAccess urlName="user">
            <Update urlName={urlName} formFelids={formFelids} dropDowns={dropDowns} hardCodedDropDowns={hardCodedDropDowns} identifier={params.identifier} />
        </WithRoleAccess>
    )
}

export default UserUpadte
