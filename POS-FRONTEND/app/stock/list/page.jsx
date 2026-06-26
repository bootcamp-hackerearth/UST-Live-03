"use client";

import List from "@/app/components/CommonList";

export default function Page() {
  return (
    <List
      urlName="stock"
      keys={["id", "identifier", "quantity", "minimumstock"]}
    />
  );
}