"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";

import CommonAdd from "@/components/common/CommonAdd";
import SingleDropDown from "@/components/common/SingleDropDown";

import { addItem } from "@/services/api";

export default function AddCategory() {
  const router = useRouter();

  const [identifier, setIdentifier] =
    useState("");

  const [superCategory, setSuperCategory] =
    useState("");

  const [loading, setLoading] =
    useState(false);

  const [error, setError] =
    useState("");

  const [successMessage, setSuccessMessage] =
    useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      setLoading(true);

      setError("");

      setSuccessMessage("");

      if (!identifier.trim()) {
        setError(
          "Category name is required"
        );

        return;
      }

      const payload = {
        identifier:
          identifier.trim(),
        superCategory
      };

      const response =
        await addItem(
          "category",
          payload
        );

      if (!response.success) {
        setError(
          response.message ||
            "Failed to create category"
        );

        return;
      }

      setSuccessMessage(
        "Category created successfully"
      );

      setTimeout(() => {
        router.push("/categories");
      }, 1000);
    } catch (error) {
      console.log(error);

      setError(
        error?.response?.data
          ?.message ||
          "Failed to create category"
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <CommonAdd
      title="Add Category"
      subtitle="Create a new category"
      identifier={identifier}
      setIdentifier={
        setIdentifier
      }
      loading={loading}
      error={error}
      successMessage={
        successMessage
      }
      onSubmit={handleSubmit}
      cancelPath="/categories"
      showDescription={false}
    >
      <SingleDropDown
        label="Super Category"
        model="category"
        value={superCategory}
        onChange={setSuperCategory}
        placeholder="Select Super Category"
      />
    </CommonAdd>
  );
}