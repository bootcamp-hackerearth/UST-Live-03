"use client";

import Add from "@/app/components/CommonAdd";
import api from "@/app/api";
import { useRouter } from "next/navigation";

import { customerFields } from "../CustomerField";
import { buildCustomerPayload } from "../CustomerUtility";

export default function CustomerAddPage() {
  const router = useRouter();

  const handleSubmitTransform = async (formData) => {
    const payload = buildCustomerPayload(formData);

    try {
      await api.post("/customer/add", payload);
      alert("Customer added successfully");
      router.push("/customer/list");
    } catch (err) {
      console.error(err.response?.data || err.message);
      alert("Failed to add customer");
    }
  };

  return (
    <Add
      urlName="customer"
      fields={customerFields}
      customSubmit={handleSubmitTransform}
    />
  );
}