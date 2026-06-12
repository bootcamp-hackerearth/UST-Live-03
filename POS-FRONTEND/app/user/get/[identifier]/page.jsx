"use client";
import { useParams } from "next/navigation";
import { requiredValidation, nameValidation, emailValidation, phoneValidation } from "@/validation/validation"
import Update from "@/components/Update";
import WithRoleAccess from '@/components/WithRoleAccess'

const UserUpadte = () => {
    const params = useParams()

    const urlName = "user"

    const formFelids = [
        { name: "username", type: "email", validation: emailValidation, isDisabled: true },
        { name: "name", type: "text", validation: nameValidation },
        { name: "phoneNo", type: "tel", validation: phoneValidation },
    ]
    const dropDowns = [
        { name: "roles", urlName: "role/list", httpMethod: "post", ismultiple: true, validation: requiredValidation }

    ]
    const hardCodedDropDowns = []

    return (
        <WithRoleAccess urlName="user">
            <Update urlName={urlName} formFelids={formFelids} dropDowns={dropDowns} hardCodedDropDowns={hardCodedDropDowns} identifier={params.identifier} />
        </WithRoleAccess>
    )
}

export default UserUpadte
