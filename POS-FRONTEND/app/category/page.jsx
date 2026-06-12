"use client"
import ListingPage from '@/components/ListingPage'
import WithRoleAccess from '@/components/WithRoleAccess'

const CategoryList = () => {
  const keys = ["id", "identifier","superCategory"]

    const urlName = "category"

    return (
            <WithRoleAccess urlName={urlName}>
            <ListingPage urlName={urlName} keys={keys} />
        </WithRoleAccess>
    )
}

export default CategoryList