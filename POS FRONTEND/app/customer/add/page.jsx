"use client";
import { Suspense } from "react";
import CustomerForm from "../CustomerForm";

export default function CustomerAddPage() {
  return (
    <Suspense fallback={null}>
      <CustomerForm mode="add" />
    </Suspense>
  );
}

