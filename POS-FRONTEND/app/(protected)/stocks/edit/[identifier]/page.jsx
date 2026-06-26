"use client";

import {useEffect,useState} from "react";
import {useRouter,useParams} from "next/navigation";
import CommonEdit from "@/components/common/CommonEdit";
import {getItem,updateItem} from "@/services/api";

export default function EditStock() {

  const router = useRouter();
  const params = useParams();
  const identifier = decodeURIComponent(params.identifier);
  const [product,setProduct] = useState("");
  const [warehouse,setWarehouse] = useState("");
  const [quantity,setQuantity] = useState("");
  const [minimumStock,setMinimumStock] = useState("");
  const [loading,setLoading] = useState(false);
  const [pageLoading,setPageLoading] = useState(true);
  const [error,setError] = useState("");
  const [successMessage,setSuccessMessage] = useState("");
  const [auditData,setAuditData] = useState({});

  useEffect(() => {
    loadStock();
  },[]);

  const loadStock = async () => {
    try {
      setPageLoading(true);
      setError("");
      const response = await getItem("stock",identifier);
      setProduct(response.product || "");
      setWarehouse(response.warehouse || "");
      setQuantity(response.quantity ?? "");
      setMinimumStock(response.minimumStock ?? "");
      setAuditData({
        createdBy: response.createdBy,
        createdOn: response.createdOn,
        modifiedBy: response.modifiedBy,
        modifiedOn: response.modifiedOn,
      });
    } catch (error) {
      console.log(error);
      setError("Failed to load stock");
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
      await updateItem("stock",{
        identifier,
        product,
        warehouse,
        quantity: Number(quantity),
        minimumStock: Number(minimumStock),
      });
      setSuccessMessage("Stock updated successfully");
      setTimeout(() => {
        router.push("/stocks");
      },1000);
    } catch (error) {
      console.log(error);
      setError(error?.response?.data?.message || "Failed to update stock");
    } finally {
      setLoading(false);
    }
  };

  if (pageLoading) {
    return (
      <div className="flex items-center justify-center py-20">
        <p className="text-[#667085]">Loading stock...</p>
      </div>
    );
  }

  return (
    <CommonEdit
      title="Edit Stock"
      subtitle="Update stock details"
      identifier={identifier}
      showIdentifier={false}
      showDescription={false}
      loading={loading}
      error={error}
      successMessage={successMessage}
      onSubmit={handleSubmit}
      cancelPath="/stocks"
      auditData={auditData}
    >
      <div>
        <label htmlFor="product" className="block mb-2 text-sm font-medium text-[#344054]">Product</label>
        <input
          id="product"
          type="text"
          value={product}
          readOnly
          className="h-14 w-full rounded-2xl border border-[#d0d5dd] bg-gray-100 px-5 text-[15px] text-[#667085] cursor-not-allowed outline-none"
        />
      </div>
      <div>
        <label htmlFor="warehouse" className="block mb-2 text-sm font-medium text-[#344054]">Warehouse</label>
        <input
          id="warehouse"
          type="text"
          value={warehouse}
          readOnly
          className="h-14 w-full rounded-2xl border border-[#d0d5dd] bg-gray-100 px-5 text-[15px] text-[#667085] cursor-not-allowed outline-none"
        />
      </div>
      <div>
        <label htmlFor="quantity" className="block mb-2 text-sm font-medium text-[#344054]">Quantity</label>
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
        <label htmlFor="minimumStock" className="block mb-2 text-sm font-medium text-[#344054]">Minimum Stock</label>
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
    </CommonEdit>
  );
}