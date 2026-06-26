"use client";

import List from "@/app/components/CommonList";

export default function Page() {
  return (
    <List
      urlName="models"
      keys={["identifier", "description", "status"]}
    />
  );
}