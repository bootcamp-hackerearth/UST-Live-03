"use client";
import { Suspense } from "react";
import CartView from "./CartView";

export default function CartPage() {
  return (
    <Suspense fallback={null}>
      <CartView />
    </Suspense>
    //15:08 03
  );
}