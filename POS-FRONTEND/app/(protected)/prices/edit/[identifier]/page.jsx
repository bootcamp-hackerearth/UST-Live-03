"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import CommonEdit from "@/components/common/CommonEdit";
import SingleDropDown from "@/components/common/SingleDropDown";
import { getItem, updateItem } from "@/services/api";

export default function EditPricePage() {

  const params = useParams();
  const identifier = decodeURIComponent(params.identifier);
  
  const router = useRouter();

  const [price, setPrice] = useState("");
  const [product, setProduct] = useState("");
  const [priceType, setPriceType] = useState("");

  const [loading, setLoading] = useState(false);
  const [pageLoading, setPageLoading] = useState(true);

  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
  try {
    setPageLoading(true);

    const response = await getItem("price", identifier);

    console.log("PRICE RESPONSE:", response);

    setPrice(response?.costPrice || "");
    setProduct(response?.product || "");
    setPriceType(response?.priceType || "");

  } catch (err) {
    console.log(err);
    setError("Failed to load price details");
  } finally {
    setPageLoading(false);
  }
};

  const validate = () => {
    if (!price || price <= 0) {
      setError("Price must be greater than 0");
      return false;
    }

    if (!product) {
      setError("Product is required");
      return false;
    }

    if (!priceType) {
      setError("Price Type is required");
      return false;
    }

    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    setError("");
    setSuccessMessage("");

    if (!validate()) return;

    try {
      setLoading(true);

      await updateItem("price", {
        identifier,
        costPrice: price,
        product: product,
        priceType: priceType
        });

      setSuccessMessage("Price updated successfully");

      setTimeout(() => {
        router.push("/prices");
      }, 800);
    } catch (err) {
      console.log(err);
      setError("Failed to update price");
    } finally {
      setLoading(false);
    }
  };

  if (pageLoading) {
    return (
      <div className="w-full flex justify-center mt-10 text-gray-500">
        Loading...
      </div>
    );
  }

  return (
    <CommonEdit
      title="Edit Price"
      subtitle="Update price details"
      identifier={identifier}
      showDescription={false}
      description=""
      setDescription={() => {}}
      loading={loading}
      error={error}
      successMessage={successMessage}
      onSubmit={handleSubmit}
      cancelPath="/prices"
    >
      <div>
        <label htmlFor="price" className="mb-2 block text-sm font-medium text-[#344054]">
          Price
        </label>

        <input
          id="price"
          type="number"
          value={price}
          onChange={(e) => setPrice(e.target.value)}
          className="h-14 w-full rounded-2xl border border-[#d0d5dd] px-5 text-[15px] text-[#101828] outline-none focus:border-[#2563eb] focus:ring-4 focus:ring-blue-100"
          required
        />
      </div>

      <SingleDropDown
        label="Product"
        model="product"
        value={product}
        onChange={setProduct}
        required
      />

      <div>
  <label htmlFor="priceType" className="mb-2 block text-sm font-medium text-[#344054]">
    Price Type
  </label>

  <select
    id="priceType"
    value={priceType}
    onChange={(e) => setPriceType(e.target.value)}
    className="h-14 w-full rounded-2xl border border-[#d0d5dd] px-5 text-[15px] text-[#101828] outline-none focus:border-[#2563eb] focus:ring-4 focus:ring-blue-100"
    required
  >
    <option value="">Select Price Type</option>
    <option value="MRP">MRP</option>
    <option value="SP">SP</option>
    <option value="CP">CP</option>
  </select>
</div>

    </CommonEdit>
  );
}
