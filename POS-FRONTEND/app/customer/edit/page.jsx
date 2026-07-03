"use client";

import { Suspense } from "react";
import Edit from "@/app/components/CommonEdit";
import api from "@/app/api";
import { useRouter } from "next/navigation";

import { customerFields } from "../CustomerField";
import {
  buildCustomerPayload,
  mapCustomerAddresses,
} from "../CustomerUtility";

function CustomerEdit() {
  const router = useRouter();

  const fields = customerFields.map((field) => {
    if (field.name === "phoneNo") {
      return {
        ...field,
        readOnly: true,
      };
    }

    if (field.name === "identifier") {
      return {
        ...field,
        readOnly: true,
      };
    }

    return field;
  });

  const customSubmit = async (formData) => {
    const payload = buildCustomerPayload(formData);

    try {
      const res = await api.put("/customer/update", payload);

      if (res.data?.success === false) {
        alert(res.data.message);
        return;
      }

      alert("Customer updated successfully");
      router.push("/customer/list");
    } catch (err) {
      console.error(err);
      alert("Update failed");
    }
  };

  return (
    <Edit
      urlName="customer"
      fields={fields}
      identifier="identifier"
      transformFetchData={mapCustomerAddresses}
      customSubmit={customSubmit}
    />
  );
}

export default function CustomerEditPage() {
  return (
    <Suspense fallback={<div>Loading...</div>}>
      <CustomerEdit />
    </Suspense>
  );
}
