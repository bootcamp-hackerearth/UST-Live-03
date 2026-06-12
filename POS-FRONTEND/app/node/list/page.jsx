import List from "@/app/components/CommonList";

export default function Page() {
  return (
    <List
      urlName="node"
       keys = {[ "identifier","path","roles"]}
    />
  );
}