"use client"
import ListingPage from '@/components/ListingPage'
import WithRoleAccess from '@/components/WithRoleAccess'

const RoleList = () => {
    const keys = ["id", "identifier", "description"]

    const urlName = "role"

    return (
        <WithRoleAccess urlName={urlName}>
            <ListingPage urlName={urlName} keys={keys} />
        </WithRoleAccess>
    )
}

export default RoleList