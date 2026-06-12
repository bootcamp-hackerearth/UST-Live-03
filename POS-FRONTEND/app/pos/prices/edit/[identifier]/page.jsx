// app/pos/prices/edit/[identifier]/page.jsx

"use client";

import { useEffect, useState } from "react";
import { useRouter, useParams } from "next/navigation";
import api from "../../../../api/axios";
import { showToast, confirmToast } from "../../../../lib/toast";
import { Loader } from "lucide-react";

export default function EditPricePage() {
  const router = useRouter();
  const params = useParams();
  const identifier = params?.identifier;

  const [identifierDisplay, setIdentifierDisplay] = useState("");
  const [mrp, setMrp] = useState("");
  const [sellingPrice, setSellingPrice] = useState("");
  const [costPrice, setCostPrice] = useState("");
  const [effectiveFrom, setEffectiveFrom] = useState("");

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  const [originalValues, setOriginalValues] = useState({});

  const getSecurityHeaders = () => {
    if (typeof globalThis !== "undefined" && globalThis.localStorage) {
      const token = globalThis.localStorage.getItem("token");
      return token ? { Authorization: `Bearer ${token}` } : {};
    }
    return {};
  };

  useEffect(() => {
    async function loadData() {
      try {
        setLoading(true);
        const res = await api.get("/price/get", {
          params: { identifier },
          headers: getSecurityHeaders()
        });

        const data = res.data;

        setIdentifierDisplay(data.identifier);
        setMrp(data.mrp ? data.mrp.toString() : "");
        setSellingPrice(data.sellingPrice ? data.sellingPrice.toString() : "");
        setCostPrice(data.costPrice ? data.costPrice.toString() : "");
        setEffectiveFrom(data.effectiveFrom || "");

        setOriginalValues({
          mrp: data.mrp,
          sellingPrice: data.sellingPrice,
          costPrice: data.costPrice,
          effectiveFrom: data.effectiveFrom,
        });
      } catch (err) {
        const errorMsg = err.response?.data?.message || "Failed to load price data";
        setError(errorMsg);
        showToast(errorMsg, "error");
      } finally {
        setLoading(false);
      }
    }

    if (identifier) {
      loadData();
    }
  }, [identifier]);

  const checkForClearedPrices = () => {
    const clearedPrices = [];
    if (originalValues.mrp && !mrp) clearedPrices.push("MRP");
    if (originalValues.sellingPrice && !sellingPrice) clearedPrices.push("Selling Price");
    if (originalValues.costPrice && !costPrice) clearedPrices.push("Cost Price");
    return clearedPrices;
  };

  const submitPrice = async () => {
    setSubmitting(true);

    try {
      const payload = {
        identifier: identifierDisplay,
        mrp: mrp ? Number.parseFloat(mrp) : null,
        sellingPrice: sellingPrice ? Number.parseFloat(sellingPrice) : null,
        costPrice: costPrice ? Number.parseFloat(costPrice) : null,
        effectiveFrom: effectiveFrom || null,
      };

      const res = await api.post("/price/update", payload, {
        headers: getSecurityHeaders()
      });

      if (res.data?.identifier) {
        setSuccess("Price updated successfully");
        showToast("Price updated successfully!", "success");
        setTimeout(() => router.push("/pos/prices"), 1500);
      } else {
        setError("Update failed. Please try again.");
        showToast("Failed to update price", "error");
      }
    } catch (err) {
      const errorMsg = err.response?.data?.message || "Unable to connect to server";
      setError(errorMsg);
      showToast(errorMsg, "error");
    } finally {
      setSubmitting(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSuccess("");

    const clearedPrices = checkForClearedPrices();

    if (clearedPrices.length > 0) {
      confirmToast(
        `You've cleared: ${clearedPrices.join(", ")}. Continue with this change?`,
        () => submitPrice(),
        () => {
          showToast("Update cancelled", "info");
        }
      );
      return;
    }

    submitPrice();
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-white p-6 font-sans">
        <div className="mb-6 animate-pulse">
          <div className="h-8 bg-slate-200 rounded w-1/4 mb-2" />
          <div className="h-4 bg-slate-100 rounded w-1/3" />
        </div>
        <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6 animate-pulse">
          {[1, 2, 3, 4, 5].map((idx) => (
            <div key={`skeleton-${idx}`} className="space-y-2">
              <div className="h-3 bg-slate-200 rounded w-1/4" />
              <div className="h-10 bg-slate-100 rounded w-full" />
            </div>
          ))}
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-white p-6 font-sans">
      
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-[#231F20]">
          Edit Price
        </h1>
        <p className="text-sm text-[#0097AC] mt-1">
          Fill in the details below
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
            <label htmlFor="edit-identifier-input" className="block text-xs font-semibold text-[#006E74] uppercase tracking-wider cursor-pointer">
              Identifier
            </label>
            <input
              id="edit-identifier-input"
              className="mt-1 w-full px-4 py-2 border rounded-md text-sm bg-slate-50 border-[#006E74]/20 text-slate-500 cursor-not-allowed outline-none font-medium"
              type="text"
              value={identifierDisplay}
              disabled
            />
          </div>

          <div>
            <label htmlFor="edit-mrp-input" className="block text-xs font-semibold text-[#006E74] uppercase tracking-wider cursor-pointer">
              MRP (Maximum Retail Price)
            </label>
            <input
              id="edit-mrp-input"
              className="mt-1 w-full px-4 py-2 border rounded-md text-sm border-[#006E74]/30 focus:border-[#006E74] focus:ring-1 focus:ring-[#006E74] outline-none text-slate-800"
              type="number"
              step="0.01"
              placeholder="Enter mrp"
              value={mrp}
              onChange={(e) => setMrp(e.target.value)}
            />
          </div>

          <div>
            <label htmlFor="edit-selling-price-input" className="block text-xs font-semibold text-[#006E74] uppercase tracking-wider cursor-pointer">
              Selling Price
            </label>
            <input
              id="edit-selling-price-input"
              className="mt-1 w-full px-4 py-2 border rounded-md text-sm border-[#006E74]/30 focus:border-[#006E74] focus:ring-1 focus:ring-[#006E74] outline-none text-slate-800"
              type="number"
              step="0.01"
              placeholder="Enter selling price"
              value={sellingPrice}
              onChange={(e) => setSellingPrice(e.target.value)}
            />
          </div>

          <div>
            <label htmlFor="edit-cost-price-input" className="block text-xs font-semibold text-[#006E74] uppercase tracking-wider cursor-pointer">
              Cost Price
            </label>
            <input
              id="edit-cost-price-input"
              className="mt-1 w-full px-4 py-2 border rounded-md text-sm border-[#006E74]/30 focus:border-[#006E74] focus:ring-1 focus:ring-[#006E74] outline-none text-slate-800"
              type="number"
              step="0.01"
              placeholder="Enter cost price"
              value={costPrice}
              onChange={(e) => setCostPrice(e.target.value)}
            />
          </div>

          <div>
            <label htmlFor="edit-effective-date-input" className="block text-xs font-semibold text-[#006E74] uppercase tracking-wider cursor-pointer">
              Effective From Date
            </label>
            <input
              id="edit-effective-date-input"
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
            disabled={submitting}
            className={`px-6 py-2 text-sm rounded-md text-white flex items-center justify-center gap-2 transition ${
              submitting
                ? "bg-[#006E74]/50 cursor-not-allowed opacity-70"
                : "bg-[#006E74] hover:bg-[#0097AC] cursor-pointer"
            }`}
          >
            {submitting ? (
              <>
                <Loader size={16} className="animate-spin" />
                Saving...
              </>
            ) : (
              "Update Price"
            )}
          </button>
        </div>

      </form>
    </div>
  );
}