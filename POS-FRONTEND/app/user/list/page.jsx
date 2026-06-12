import List from "@/app/components/CommonList";

export default function Page() {
  return (
    <List
      urlName="user"
       keys = {["name", "username", "roles", "phoneNo"]}
    />
  );
}