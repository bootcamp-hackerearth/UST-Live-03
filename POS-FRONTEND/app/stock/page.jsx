"use client"
import ListingPage from '@/components/ListingPage'
import WithRoleAccess from '@/components/WithRoleAccess'

const ShelfsList = () => {
    const keys = ["id", "identifier", "product","quantity","stockStatus","warehouse"]

    const urlName = "stock"

    return (
        <WithRoleAccess urlName={urlName}>
            <ListingPage urlName={urlName} keys={keys} />
        </WithRoleAccess>
    )
}

export default ShelfsList