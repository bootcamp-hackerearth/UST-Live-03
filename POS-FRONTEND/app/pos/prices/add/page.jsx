// app/pos/prices/add/page.jsx

"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import api from "../../../api/axios";
import SingleDropdown from "../../../../components/dropDowns/singleDropDown.jsx";
import { showToast, confirmToast } from "../../../lib/toast";
import { Loader } from "lucide-react";

export default function AddPricePage() {
  const router = useRouter();

  const [product, setProduct] = useState("");
  const [mrp, setMrp] = useState("");
  const [sellingPrice, setSellingPrice] = useState("");
  const [costPrice, setCostPrice] = useState("");
  const [effectiveFrom, setEffectiveFrom] = useState("");

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);

  const hasEmptyPrices = !mrp || !sellingPrice || !costPrice;

  const getSecurityHeaders = () => {
    if (typeof globalThis !== "undefined" && globalThis.localStorage) {
      const token = globalThis.localStorage.getItem("token");
      return token ? { Authorization: `Bearer ${token}` } : {};
    }
    return {};
  };

  const submitPrice = async () => {
    setLoading(true);

    try {
      const payload = {
        identifier: product,
        mrp: mrp ? Number.parseFloat(mrp) : null,
        sellingPrice: sellingPrice ? Number.parseFloat(sellingPrice) : null,
        costPrice: costPrice ? Number.parseFloat(costPrice) : null,
        effectiveFrom: effectiveFrom || null,
      };

      const res = await api.post("/price/add", payload, {
        headers: getSecurityHeaders(),
      });
      const data = res.data;

      if (data?.identifier) {
        setSuccess("Price configured successfully");
        showToast("Price added successfully!", "success");
        setTimeout(() => router.push("/pos/prices"), 1500);
      } else {
        setError("Failed to add price. Please try again.");
        showToast("Failed to add price", "error");
      }
    } catch (err) {
      const errorMsg = err.response?.data?.message || "Unable to connect to server";
      setError(errorMsg);
      showToast(errorMsg, "error");
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSuccess("");

    if (!product) {
      setError("Please select a product");
      showToast("Please select a product", "error");
      return;
    }

    if (hasEmptyPrices) {
      confirmToast(
        "Some prices are not configured. Do you want to submit anyway?",
        () => submitPrice(),
        () => {
          showToast("Submission cancelled", "info");
        }
      );
      return;
    }

    submitPrice();
  };

  return (
    <div className="min-h-screen bg-white p-6 font-sans">

      <div className="mb-6">
        <h1 className="text-2xl font-bold text-[#231F20]">
          Configure Price
        </h1>
        <p className="text-sm text-[#0097AC] mt-1">
          Set MRP, selling price, and cost price for a product
        </p>
      </div>

      {error && (
        <div className="mb-4 px-4 py-3 border text-sm font-medium rounded-md bg-white text-red-600 border-red-300">
          {error}
        </div>
      )}
      {success && (
        <div className="mb-4 px-4 py-3 border text-sm font-medium rounded-md bg-white text-green-600 border-green-300">
          {success}
        </div>
      )}

      <form onSubmit={handleSubmit} className="space-y-6">

        <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">

          <div>
            <SingleDropdown
              label="Select Product"
              entity="product"
              selectedValue={product}
              onChange={setProduct}
              valueField="identifier"
              labelField="identifier"
              headers={getSecurityHeaders()}
            />
          </div>

          <div>
            <label htmlFor="mrp-input-field" className="block text-xs font-semibold text-[#006E74] uppercase tracking-wider cursor-pointer">
              MRP (Maximum Retail Price)
            </label>
            <input
              id="mrp-input-field"
              className="mt-1 w-full px-4 py-2 border rounded-md text-sm border-[#006E74]/30 focus:border-[#006E74] focus:ring-1 focus:ring-[#006E74] outline-none text-slate-800"
              type="number"
              step="0.01"
              placeholder="Enter MRP"
              value={mrp}
              onChange={(e) => setMrp(e.target.value)}
            />
          </div>

          <div>
            <label htmlFor="selling-price-input-field" className="block text-xs font-semibold text-[#006E74] uppercase tracking-wider cursor-pointer">
              Selling Price
            </label>
            <input
              id="selling-price-input-field"
              className="mt-1 w-full px-4 py-2 border rounded-md text-sm border-[#006E74]/30 focus:border-[#006E74] focus:ring-1 focus:ring-[#006E74] outline-none text-slate-800"
              type="number"
              step="0.01"
              placeholder="Enter selling price"
              value={sellingPrice}
              onChange={(e) => setSellingPrice(e.target.value)}
            />
          </div>

          <div>
            <label htmlFor="cost-price-input-field" className="block text-xs font-semibold text-[#006E74] uppercase tracking-wider cursor-pointer">
              Cost Price
            </label>
            <input
              id="cost-price-input-field"
              className="mt-1 w-full px-4 py-2 border rounded-md text-sm border-[#006E74]/30 focus:border-[#006E74] focus:ring-1 focus:ring-[#006E74] outline-none text-slate-800"
              type="number"
              step="0.01"
              placeholder="Enter cost price"
              value={costPrice}
              onChange={(e) => setCostPrice(e.target.value)}
            />
          </div>

          <div>
            <label htmlFor="effective-date-input-field" className="block text-xs font-semibold text-[#006E74] uppercase tracking-wider cursor-pointer">
              Effective From Date
            </label>
            <input
              id="effective-date-input-field"
              className="mt-1 w-full px-4 py-2 border rounded-md text-sm border-[#006E74]/30 focus:border-[#006E74] focus:ring-1 focus:ring-[#006E74] outline-none text-slate-800"
              type="date"
              value={effectiveFrom}
              onChange={(e) => setEffectiveFrom(e.target.value)}
            />
          </div>

        </div>

        <div className="sticky bottom-0 bg-white pt-4 border-t border-[#006E74]/20 flex justify-end gap-3">
          <button
            type="button"
            onClick={() => router.back()}
            className="px-6 py-2 text-sm border rounded-md text-[#231F20] border-[#006E74]/30 hover:bg-slate-50 transition cursor-pointer"
          >
            Cancel
          </button>

          <button
            type="submit"
            disabled={loading || !product}
            className={`px-6 py-2 text-sm rounded-md text-white flex items-center justify-center gap-2 transition ${loading || !product
                ? "bg-[#006E74]/50 cursor-not-allowed opacity-70"
                : "bg-[#006E74] hover:bg-[#0097AC] cursor-pointer"
              }`}
          >
            {loading ? (
              <>
                <Loader size={16} className="animate-spin" />
                Saving...
              </>
            ) : (
              "Configure Price"
            )}
          </button>
        </div>
      </form>
    </div>
  );
}