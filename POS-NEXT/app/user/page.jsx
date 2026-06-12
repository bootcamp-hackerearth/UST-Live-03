import List from "@/components/CommonList";

export default function UserList() {

  const keys = ["name", "username", "phoneNo", "roles"]

  return (

      <List keys={keys} routeName="user" title="User" />
    
  );
};
