"use client";
import { Suspense } from "react";
import CustomerForm from "../../CustomerForm";

export default function CustomerEditPage() {
  return (
    <Suspense fallback={null}>
      <CustomerForm mode="edit" />
    </Suspense>
  );
}
