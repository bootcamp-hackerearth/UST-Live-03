"use client";

import List from "@/app/components/CommonList";

export default function Page() {
  return (
    <List
      urlName="brand"
      keys={["identifier", "description", "status"]}
    />
  );
}