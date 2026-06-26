"use client";

import List from "@/app/components/CommonList";

export default function Page() {
  return (
    <List
      urlName="shelf"
      keys={["identifier","description", "status"]}
    />
  );
}