"use client";

import List from "@/app/components/CommonList";

export default function Page() {
  return (
    <List
      urlName="rack"
      keys={["identifier", "shelfs","description", "status"]}
    />
  );
}