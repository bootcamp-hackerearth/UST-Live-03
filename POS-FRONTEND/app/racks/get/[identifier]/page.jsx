"use client";
import { useParams } from "next/navigation";
import { requiredValidation } from "@/validation/validation"
import Update from "@/components/Update";
import WithRoleAccess from '@/components/WithRoleAccess'

const RackEdit = () => {
    const params = useParams()

     const urlName = "racks"

    const formFelids = [
        { name: "identifier", type: "text", validation: requiredValidation, isDisabled:true },
    ]
    const dropDowns = [
        { name: "shelfs", urlName: "shelfs/getAllActive", httpMethod: "get", ismultiple: true, validation: requiredValidation }
    ]
    const hardCodedDropDowns = []
    return (
        <WithRoleAccess urlName={urlName}>
            <Update urlName={urlName} formFelids={formFelids} dropDowns={dropDowns} hardCodedDropDowns={hardCodedDropDowns} identifier={params.identifier} />
        </WithRoleAccess>
    )
}

export default RackEdit
