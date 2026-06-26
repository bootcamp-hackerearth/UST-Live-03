"use client";
import {useState} from "react";
import {useRouter} from "next/navigation";
import CommonAdd from "@/components/common/CommonAdd";
import {addItem} from "@/services/api";
import SingleDropDown from "@/components/common/SingleDropDown";

export default function AddStock() {

  const router = useRouter();
  const [product,setProduct] = useState("");
  const [warehouse,setWarehouse] = useState("");
  const [quantity,setQuantity] = useState("");
  const [minimumStock,setMinimumStock] = useState("");
  const [loading,setLoading] = useState(false);
  const [error,setError] = useState("");
  const [successMessage,setSuccessMessage] = useState("");
  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      setLoading(true);
      setError("");
      setSuccessMessage("");
      if (!product) {
        setError("Please select a product");
        return;
      }
      if (!warehouse) {
        setError("Please select a warehouse");
        return;
      }
      await addItem("stock",{
        product,
        warehouse,
        quantity: Number(quantity),
        minimumStock: Number(minimumStock),
      });
      setSuccessMessage("Stock added successfully");
      setTimeout(() => {
        router.push("/stocks");
      },1000);
    } catch (err) {
      setError(err?.response?.data?.message || "Failed to add stock");
    } finally {
      setLoading(false);
    }
  };
  return (
    <CommonAdd
      title="Add Stock"
      subtitle="Create a new stock entry"
      showIdentifier={false}
      showDescription={false}
      loading={loading}
      error={error}
      successMessage={successMessage}
      onSubmit={handleSubmit}
      cancelPath="/stocks"
    >
      <SingleDropDown
        label="Product"
        model="product"
        value={product}
        onChange={setProduct}
        placeholder="Select Product"
        required={true}
      />
      <SingleDropDown
        label="Warehouse"
        model="warehouse"
        value={warehouse}
        onChange={setWarehouse}
        placeholder="Select Warehouse"
        required={true}
      />
      <div>
        <label htmlFor="quantity" className="block mb-2 text-sm font-medium text-[#344054]">
          Quantity
        </label>
        <input
          id="quantity"
          type="number"
          min="0"
          value={quantity}
          onChange={(e) => setQuantity(e.target.value)}
          required
          className="w-full h-14 px-5 rounded-2xl border border-[#d0d5dd] bg-white text-[#101828] outline-none transition-all focus:border-[#2563eb] focus:ring-4 focus:ring-blue-100"
        />
      </div>
      <div>
        <label htmlFor="minimumStock" className="block mb-2 text-sm font-medium text-[#344054]">
          Minimum Stock
        </label>
        <input
          id="minimumStock"
          type="number"
          min="0"
          value={minimumStock}
          onChange={(e) => setMinimumStock(e.target.value)}
          required
          className="w-full h-14 px-5 rounded-2xl border border-[#d0d5dd] bg-white text-[#101828] outline-none transition-all focus:border-[#2563eb] focus:ring-4 focus:ring-blue-100"
        />
      </div>
    </CommonAdd>
  );
}