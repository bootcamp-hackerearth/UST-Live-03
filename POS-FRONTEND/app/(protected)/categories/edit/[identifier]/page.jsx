"use client";

import { useEffect,useState } from "react";
import { useParams,useRouter } from "next/navigation";
import CommonEdit from "@/components/common/CommonEdit";
import SingleDropDown from "@/components/common/SingleDropDown";
import { getItem, updateItem } from "@/services/api";

export default function EditCategory() {
  const router = useRouter();

  const params = useParams();

  const [identifier,setIdentifier] =
    useState("");

  const [superCategory,setSuperCategory] =
    useState("");

  const [loading,setLoading] =
    useState(false);

  const [pageLoading,setPageLoading] =
    useState(true);

  const [error,setError] =
    useState("");

  const [successMessage,setSuccessMessage] =
    useState("");

  useEffect(() => {
    loadCategory();
  }, []);

  const loadCategory = async () => {
    try {
      setPageLoading(true);

      const response =
        await getItem(
          "category",
          params.identifier
        );

      setIdentifier(
        response.identifier || ""
      );

      setSuperCategory(
        response.superCategory || ""
      );
    } catch (error) {
      console.log(error);

      setError(
        "Failed to load category"
      );
    } finally {
      setPageLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      setLoading(true);

      setError("");

      setSuccessMessage("");

      const response =
        await updateItem(
          "category",
          {
            identifier,
            superCategory
          }
        );

      if (!response.success) {
        setError(
          response.message ||
          "Failed to update category"
        );

        return;
      }

      setSuccessMessage(
        "Category updated successfully"
      );

      setTimeout(() => {
        router.push("/categories");
      },1000);
    } catch (error) {
      console.log(error);

      setError(
        error?.response?.data
          ?.message ||
        "Failed to update category"
      );
    } finally {
      setLoading(false);
    }
  };

  if (pageLoading) {
    return (
      <div className="text-sm text-gray-500">
        Loading...
      </div>
    );
  }

  return (
    <CommonEdit
      title="Edit Category"
      subtitle="Update category details"
      identifier={identifier}
      loading={loading}
      error={error}
      successMessage={successMessage}
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
    </CommonEdit>
  );
}