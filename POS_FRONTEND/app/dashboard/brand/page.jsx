"use client";

import { useEffect, useState } from "react";
import axios from "@/config/axiosConfig";
import { useRouter } from "next/navigation";
import CommonList from "@/components/CommonList";
import Toggle from "@/components/Toggle";

export default function BrandPage() {
  const router = useRouter();
  const [brands, setBrands] = useState([]);

  useEffect(() => {
    fetchBrands();
  }, []);

  const fetchBrands = async () => {
    try {
      const res = await axios.post("/brand/list", {
        page: 0,
        sizePerPage: 100,
      });

      setBrands(res?.data?.content || []);
    } catch (e) {
      console.log(e);
      setBrands([]);
    }
  };

  const handleEdit = (row) => {
    router.push(`/dashboard/brand/edit/${row.identifier}`);
  };

  const handleDelete = async (row) => {
    try {
      await axios.get(`/brand/delete?identifier=${row.identifier}`);
      fetchBrands();
    } catch (e) {
      console.log(e);
    }
  };

  const toggleStatus = async (row) => {
    try {
      await axios.put(
        `/brand/toggle-status?identifier=${row.identifier}`
      );
      fetchBrands();
    } catch (e) {
      console.log(e);
    }
  };

  const columns = [
    {
      header: "Brand",
      accessor: "identifier",
    },
    {
      header: "Description",
      accessor: "description",
    },
    {
      header: "Status",
      accessor: "status",
      render: (row) => (
        <Toggle
          active={Boolean(row.status)}
          onToggle={() => toggleStatus(row)}
        />
      ),
    },
  ];

  return (
    <div className="p-6">
      <div className="flex justify-between mb-5">
        <h1 className="text-2xl font-bold">Brand List</h1>

        <div className="flex gap-3">
          <button
            onClick={() => router.push("/dashboard/brand/add")}
            className="bg-slate-900 text-white px-4 py-2 rounded"
          >
            Add Brand
          </button>

          <button
            onClick={() => router.push("/")}
            className="bg-gray-200 px-4 py-2 rounded-lg hover:bg-gray-300"
          >
            Back
          </button>
        </div>
      </div>

      <CommonList
        data={brands}
        columns={columns}
        onEdit={handleEdit}
        onDelete={handleDelete}
      />
    </div>
  );
}
