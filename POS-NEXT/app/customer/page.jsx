import List from "@/components/CommonList";

export default function CustomerList() {

  const keys = ["identifier", "name", "phoneNo", "balance", "creditLimit", "userType"]
  return (

    <List routeName="customer" keys={keys} title="Customer" />

  );
}