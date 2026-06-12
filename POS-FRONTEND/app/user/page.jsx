"use client"
import ListingPage from '@/components/ListingPage'
import WithRoleAccess from '@/components/WithRoleAccess'

const UserList = () => (
    <WithRoleAccess urlName="user">
        <ListingPage urlName="user" keys={["id", "username", "name", "phoneNo", "roles"]} />
    </WithRoleAccess>
)

export default UserList