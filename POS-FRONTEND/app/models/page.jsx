"use client"
import ListingPage from '@/components/ListingPage'
import WithRoleAccess from '@/components/WithRoleAccess'

const UnitList = () => {
    const keys = ["id", "identifier","description", "status"]

    const urlName = "models"

    return (
        <WithRoleAccess urlName={urlName}>
            <ListingPage urlName={urlName} keys={keys} />
        </WithRoleAccess>
    )
}

export default UnitList