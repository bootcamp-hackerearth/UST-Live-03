"use client"
import Add from "@/components/Add"
import { requiredValidation, emailValidation, nameValidation, passwordValidation, phoneValidation } from "@/validation/validation"
import WithRoleAccess from '@/components/WithRoleAccess'

const UserAdd = () => {

    const urlName = "user"

    const formFelids = [
        { name: "username", type: "email", validation: emailValidation },
        { name: "name", type: "text", validation: nameValidation },
        { name: "phoneNo", type: "tel", validation: phoneValidation },
        { name: "password", type: "password", validation: passwordValidation },
    ]
    const dropDowns = [
        { name: "roles", urlName: "role/list", httpMethod: "post", ismultiple: true, validation: requiredValidation }

    ]
    const hardCodedDropDowns = []

    return (
            <WithRoleAccess urlName="user">
                <Add urlName={urlName} formFelids={formFelids} dropDowns={dropDowns} hardCodedDropDowns={hardCodedDropDowns} />    
            </WithRoleAccess>
    )
}

export default UserAdd
