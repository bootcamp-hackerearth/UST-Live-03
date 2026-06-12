import React from "react";
import CommonList from "@/app/components/CommonList";

export default function CartList() {
  const columns = [
    {
      field: "identifier",
      label: "Cart ID",
    },
    {
      field: "couponCode",
      label: "Coupon Code",
    },
    {
      field: "originalPrice",
      label: "Original Price",
    },
    {
      field: "totalDiscount",
      label: "Total Discount",
    },
    {
      field: "totalPrice",
      label: "Final Price",
    },
    {
      field: "status",
      label: "Status",
    },
  ];

  return (
    <CommonList
      title="Cart"
      columns={columns}
      urlName="cart"
      showStatus={true}
    />
  );
}