"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import CommonAdd from "@/components/common/CommonAdd";
import { addItem } from "@/services/api";
import ProductFields from "@/components/product/ProductFields";

export default function AddProduct() {

  const router = useRouter();

  const [identifier, setIdentifier] =
    useState("");

  const [description, setDescription] =
    useState("");

  const [brand, setBrand] =
    useState("");

  const [model, setModel] =
    useState("");

  const [unit, setUnit] =
    useState("");

  const [category, setCategory] =
    useState([]);

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

      if (category.length === 0) {

        setError(
          "Please select at least one category"
        );

        return;

      }

      await addItem("product", {

        identifier,

        description,

        brand,

        model,

        unit,

        category

      });

      setSuccessMessage(
        "Product added successfully"
      );

      setTimeout(() => {

        router.push("/products");

      }, 1000);

    } catch (err) {

      setError(

        err?.response?.data?.message ||

        "Failed to add product"

      );

    } finally {

      setLoading(false);

    }

  };

  return (

    <CommonAdd

      title="Add Product"

      subtitle="Create a new product"

      identifier={identifier}

      setIdentifier={setIdentifier}

      description={description}

      setDescription={setDescription}

      loading={loading}

      error={error}

      successMessage={successMessage}

      onSubmit={handleSubmit}

      cancelPath="/products"

    >
      <ProductFields

        brand={brand}

        setBrand={setBrand}

        model={model}

        setModel={setModel}

        unit={unit}

        setUnit={setUnit}

        category={category}

        setCategory={setCategory}

      />

    </CommonAdd>

  );

}