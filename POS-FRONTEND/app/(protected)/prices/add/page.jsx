"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import CommonAdd from "@/components/common/CommonAdd";
import SingleDropDown from "@/components/common/SingleDropDown";
import { addItem } from "@/services/api";

export default function AddPrice() {
  const router = useRouter();

  const [product, setProduct] =
    useState("");

  const [priceType, setPriceType] =
    useState("");

  const [costPrice, setCostPrice] =
    useState("");

  const [loading, setLoading] =
    useState(false);

  const [error, setError] =
    useState("");

  const [successMessage, setSuccessMessage] =
    useState("");

  const priceTypeOptions = [
    {
      identifier: "MRP"
    },
    {
      identifier: "SP"
    },
    {
      identifier: "CP"
    }
  ];

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      setLoading(true);

      setError("");

      setSuccessMessage("");

      if (!product) {
        setError(
          "Please select a product"
        );

        return;
      }

      if (!priceType) {
        setError(
          "Please select a price type"
        );

        return;
      }

      if (
        !costPrice ||
        Number(costPrice) <= 0
      ) {
        setError(
          "Price must be greater than 0"
        );

        return;
      }

      const response =
        await addItem("price", {
          product,
          priceType,
          costPrice:
            Number(costPrice)
        });

      if (!response.success) {
        setError(
          response.message ||
            "Failed to create price"
        );

        return;
      }

      setSuccessMessage(
        "Price created successfully"
      );

      setTimeout(() => {
        router.push("/prices");
      }, 1000);
    } catch (error) {
      console.log(error);

      setError(
        error?.response?.data
          ?.message ||
          "Failed to create price"
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <CommonAdd
      title="Add Price"
      subtitle="Create a new product price"
      loading={loading}
      error={error}
      successMessage={successMessage}
      onSubmit={handleSubmit}
      cancelPath="/prices"
      showIdentifier={false}
      showDescription={false}
    >
      <SingleDropDown
        label="Product"
        model="product"
        value={product}
        onChange={setProduct}
        placeholder="Select Product"
        required
      />

      <div>
        <label htmlFor="priceType" className="block mb-2 text-sm font-medium text-[#344054]">
          Price Type
        </label>

        <select
          id="priceType"
          value={priceType}
          onChange={(e) =>
            setPriceType(
              e.target.value
            )
          }
          required
          className="w-full h-14 px-5 rounded-2xl border border-[#d0d5dd] bg-white text-[#101828] outline-none transition-all focus:border-[#2563eb] focus:ring-4 focus:ring-blue-100"
        >
          <option value="">
            Select Price Type
          </option>

          {priceTypeOptions.map(
            (item) => (
              <option
                key={
                  item.identifier
                }
                value={
                  item.identifier
                }
              >
                {item.identifier}
              </option>
            )
          )}
        </select>
      </div>

      <div>
        <label htmlFor="costPrice" className="block mb-2 text-sm font-medium text-[#344054]">
          Cost Price
        </label>

        <input
          id="costPrice"
          type="number"
          value={costPrice}
          onChange={(e) =>
            setCostPrice(
              e.target.value
            )
          }
          required
          min="1"
          step="0.01"
          placeholder="Enter Cost Price"
          className="w-full h-14 px-5 rounded-2xl border border-[#d0d5dd] bg-white text-[#101828] outline-none transition-all focus:border-[#2563eb] focus:ring-4 focus:ring-blue-100"
        />

        <p className="mt-2 text-sm text-[#667085]">
          Price must be greater than 0
        </p>
      </div>
    </CommonAdd>
  );
}