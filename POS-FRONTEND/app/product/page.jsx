"use client"
import ListingPage from '@/components/ListingPage'
import WithRoleAccess from '@/components/WithRoleAccess'

const ProductList = () => {
    const keys = ["id", "identifier", "name", "brand", "category", "model", "unit"]

    const urlName = "product"

    return (
        <WithRoleAccess urlName={urlName}>
            <ListingPage urlName={urlName} keys={keys} />
        </WithRoleAccess>
    )
}

export default ProductList