"use client"
import Add from "@/components/Add"
import { requiredValidation, nameValidation, phoneValidation } from "@/validation/validation"
import WithRoleAccess from '@/components/WithRoleAccess'

const WarehouseAdd = () => {

    const urlName = "warehouse"

    const formFelids = [
        { name: "identifier", type: "text", validation: requiredValidation },
        { name: "country", type: "text", validation: requiredValidation },
        { name: "region", type: "text", validation: requiredValidation },
        { name: "location", type: "text", validation: requiredValidation },
        { name: "contactName", type: "text", validation: nameValidation },
        { name: "contactNumber", type: "tel", validation: phoneValidation },
    ]
    const dropDowns = []

    const hardCodedDropDowns = []

    return (
            <WithRoleAccess urlName="warehouse">
                <Add urlName={urlName} formFelids={formFelids} dropDowns={dropDowns} hardCodedDropDowns={hardCodedDropDowns} />    
            </WithRoleAccess>
    )
}

export default WarehouseAdd
