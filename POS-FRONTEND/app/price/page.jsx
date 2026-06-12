"use client"
import ListingPage from '@/components/ListingPage'
import WithRoleAccess from '@/components/WithRoleAccess'

const PriceList = () => {
    const keys = ["id", "identifier", "product", "priceAmount", "priceType"]

    const urlName = "price"

    return (
        <WithRoleAccess urlName={urlName}>
            <ListingPage urlName={urlName} keys={keys} />
        </WithRoleAccess>
    )
}

export default PriceList