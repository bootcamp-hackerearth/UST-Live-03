"use client";

import List from "@/app/components/CommonList";

export default function Page() {
  return (
    <List
      urlName="warehouse"
      keys={["identifier", "country", "region", "status"]}
    />
  );
}