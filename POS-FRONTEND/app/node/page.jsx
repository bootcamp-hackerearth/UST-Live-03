"use client"
import ListingPage from '@/components/ListingPage'
import WithRoleAccess from '@/components/WithRoleAccess'

const NodeList = () => {
    const keys = ["id", "identifier", "path", "roles", "status"]

    const urlName = "node"

    return (
        <WithRoleAccess urlName={urlName}>
            <ListingPage urlName={urlName} keys={keys} />
        </WithRoleAccess>
    )
}

export default NodeList