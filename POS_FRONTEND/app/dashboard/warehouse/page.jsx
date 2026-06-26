"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import axios from "@/config/axiosConfig";
import CommonList from "@/components/CommonList";
import Toggle from "@/components/Toggle";

export default function WarehouseListPage() {

  const router = useRouter();
  const [warehouses, setWarehouses] = useState([]);

  const fetchWarehouses = async () => {
    try {
      const res = await axios.post("/warehouse/list", {
        page: 0,
        sizePerPage: 50
      });
      setWarehouses(res.data?.content || []);
    } catch (e) {
      console.log(e);
      setWarehouses([]);
    }
  };

  useEffect(() => {
    fetchWarehouses();
  }, []);

  const handleEdit = (row) => {
    router.push(`/dashboard/warehouse/edit/${row.identifier}`);
  };

  const toggleStatus = async (row) => {
    try {
      await axios.put(
        `/warehouse/toggle-status?identifier=${row.identifier}`
      );
      fetchWarehouses();
    } catch (e) {
      console.log(e);
    }
  };

  const handleDelete = async (row) => {
    if (!confirm("Delete this warehouse?")) return;

    try {
      await axios.delete(`/warehouse/delete?identifier=${row.identifier}`);
      fetchWarehouses();
    } catch (e) {
      console.log(e);
    }
  };

  const columns = [
    { header: "Warehouse Name", accessor: "identifier" },
    { header: "Region", accessor: "region" },
    { header: "Country", accessor: "country" },
    { header: "Location", accessor: "location" },
    { header: "Contact Name", accessor: "contactName" },
    { header: "Contact Number", accessor: "contactNumber" },
    {
      header: "Status",
      render: (row) => (
        <Toggle
          active={Boolean(row.status)}
          onToggle={() => toggleStatus(row)}
        />
      )
    },
  ];

  return (
    <div className="p-6">

      <div className="flex justify-between mb-5">
        <h1 className="text-2xl font-bold">
          Warehouse List
        </h1>

        <div className="flex gap-3">
          <button
            onClick={() => router.push("/dashboard/warehouse/add")}
            className="bg-slate-900 text-white px-4 py-2 rounded"
          >
            Add Warehouse
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
        data={warehouses}
        columns={columns}
        onEdit={handleEdit}
        onDelete={handleDelete}
      />

    </div>
  );
}