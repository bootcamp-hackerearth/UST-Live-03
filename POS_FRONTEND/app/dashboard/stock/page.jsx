"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import axios from "@/config/axiosConfig";
import CommonList from "@/components/CommonList";

export default function StockListPage() {

  const router = useRouter();
  const [stocks, setStocks] = useState([]);

  const fetchStocks = async () => {
    try {
      const res = await axios.post("/stock/list", {
        page: 0,
        sizePerPage: 50
      });
      setStocks(res.data?.content || []);
    } catch (e) {
      console.log(e);
      setStocks([]);
    }
  };

  useEffect(() => {
    fetchStocks();
  }, []);

  const handleEdit = (row) => {
    router.push(`/dashboard/stock/edit/${row.identifier}`);
  };

  const handleDelete = async (row) => {
    if (!confirm("Delete stock?")) return;

    try {
      await axios.delete(`/stock/delete?identifier=${row.identifier}`);
      fetchStocks();
    } catch (e) {
      console.log(e);
    }
  };

  const columns = [
    { header: "ID", accessor: "id" },
    { header: "Identifier", accessor: "identifier" },
    { header: "Product", accessor: "product" },
    { header: "Quantity", accessor: "quantity" },
    { header: "Stock Status", accessor: "stockStatus" },
    { header: "Warehouse", accessor: "warehouse" },

  ];

  return (
    <div className="p-6">

      <div className="flex justify-between mb-5">
        <h1 className="text-2xl font-bold">Stock List</h1>

        <div className="flex gap-3">
          <button
            onClick={() => router.push("/dashboard/stock/add")}
            className="bg-slate-900 text-white px-4 py-2 rounded"
          >
            Add Stock
          </button>

          <button
            onClick={() => router.push("/")}
            className="bg-gray-200 px-4 py-2 rounded"
          >
            Home
          </button>
        </div>
      </div>

      <CommonList
        data={stocks}
        columns={columns}
        onEdit={handleEdit}
        onDelete={handleDelete}
      />

    </div>
  );
}