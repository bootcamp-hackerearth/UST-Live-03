"use client"
import WithRoleAccess from "@/components/WithRoleAccess"
import ListingPage from "@/components/ListingPage"

const customerList = () => {
  const keys = ["id", "identifier", "name", "email", "partyType", "balance", "creditLimit"]
 
     const urlName = "customer"
 
     return (
         <WithRoleAccess urlName={urlName}>
             <ListingPage urlName={urlName} keys={keys} />
         </WithRoleAccess>
     )
}

export default customerList
