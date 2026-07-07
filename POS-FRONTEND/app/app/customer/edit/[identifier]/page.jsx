"use client";

import { useState } from "react";
import { useRouter, useParams } from "next/navigation";
import CustomerForm from "../../CustomerForm";

function CustomerUpdate() {
  const router = useRouter();
  const params = useParams();
  const { identifier } = params;

  const [success, setSuccess] = useState("");

  const handleSuccess = () => {
    setSuccess("Customer updated successfully!");
    setTimeout(() => router.back(), 1500);
  };

  return (
    <div
      style={{
        padding: 20,
        backgroundColor: "#f5f5f5",
        minHeight: "calc(100vh - 70px)",
      }}
    >
      <div style={{ marginBottom: 20 }}>
        <h1 style={{ color: "#111827", margin: 0 }}>
          Update Customer
        </h1>
        <p
          style={{
            color: "#6b7280",
            margin: "4px 0 0",
            fontSize: 14,
          }}
        >
          Update customer details
        </p>
      </div>

      <div
        style={{
          backgroundColor: "#fff",
          borderRadius: 8,
          boxShadow: "0 2px 10px rgba(0,0,0,0.1)",
          padding: 24,
          maxWidth: 860,
        }}
      >
        {success && (
          <div
            style={{
              backgroundColor: "#dcfce7",
              color: "#16a34a",
              padding: "10px 14px",
              borderRadius: 6,
              marginBottom: 16,
              fontSize: 14,
            }}
          >
            {success}
          </div>
        )}

        <CustomerForm
          identifier={identifier}
          isEdit={true}
          onSuccess={handleSuccess}
          onCancel={() => router.back()}
        />
      </div>
    </div>
  );
}

export default CustomerUpdate;