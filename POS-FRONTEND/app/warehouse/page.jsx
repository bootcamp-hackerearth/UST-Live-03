"use client"
import ListingPage from '@/components/ListingPage'
import WithRoleAccess from '@/components/WithRoleAccess'

const WarehouseList = () => (
    <WithRoleAccess urlName="warehouse">
        <ListingPage urlName="warehouse" keys={["id", "identifier","location","contactName", "contactNumber", "region","country"]} />
    </WithRoleAccess>
)

export default WarehouseList