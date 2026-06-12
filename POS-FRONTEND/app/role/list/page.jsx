import List from "@/app/components/CommonList";

export default function Page() {
  return (
    <List
      urlName="role"
       keys = {[ "identifier","description"]}
    />
  );
}