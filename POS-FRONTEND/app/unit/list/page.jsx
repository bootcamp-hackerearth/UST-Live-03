"use client";

import List from "@/app/components/CommonList";

export default function Page() {
  return (
    <List
      urlName="unit"
      keys={[ "identifier", "description", "status"]}
    />
  );
}