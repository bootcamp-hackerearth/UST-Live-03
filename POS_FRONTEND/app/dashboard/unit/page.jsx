"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import axios from "@/config/axiosConfig";
import CommonList from "@/components/CommonList";
import Toggle from "@/components/Toggle";

export default function UnitListPage() {

  const router = useRouter();
  const [units, setUnits] = useState([]);

  const normalizeUnits = (rows = []) => {
    const seen = new Set();

    return rows.filter((item) => {
      const key = item?.identifier ?? item?.id ?? JSON.stringify(item);

      if (seen.has(key)) {
        return false;
      }

      seen.add(key);
      return true;
    });
  };

  const fetchUnits = async (keyword = "") => {
    try {
      const res = await axios.post("/unit/list", {
        page: 0,
        sizePerPage: 50,
        keyword,
      });

      setUnits(normalizeUnits(res.data?.content || []));
    } catch (e) {
      console.log(e);
      setUnits([]);
    }
  };

  useEffect(() => {
    fetchUnits();
  }, []);

  const handleSearch = (keyword) => {
    console.log("SEARCH =", keyword);
    fetchUnits(keyword);
  };

  const toggleStatus = async (row) => {
    try {
      await axios.put(`/unit/toggle-status?identifier=${row.identifier}`);
      fetchUnits();
    } catch (e) {
      console.log(e);
    }
  };

  const handleEdit = (row) => {
    router.push(`/dashboard/unit/edit/${row.identifier}`);
  };

  const handleDelete = async (row) => {
    if (!confirm("Delete this unit?")) return;

    try {
      await axios.delete(`/unit/delete?identifier=${row.identifier}`);
      fetchUnits();
    } catch (e) {
      console.log(e);
    }
  };

  const columns = [
    { header: "ID", accessor: "id" },
    { header: "Unit Name", accessor: "identifier" },
    { header: "Description", accessor: "description" },
    {
      header: "Status",
      accessor: "status",
      render: (row) => (
        <Toggle
          active={Boolean(row.status)}
          onToggle={() => toggleStatus(row)}
        />
      )
    }
  ];

  return (
    <div className="p-6">

      <div className="flex justify-between mb-5">
        <h1 className="text-2xl font-bold">Unit List</h1>

        <div className="flex gap-3">
          <button
            onClick={() => router.push("/dashboard/unit/add")}
            className="bg-slate-900 text-white px-4 py-2 rounded"
          >
            Add Unit
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
        data={units}
        columns={columns}
        onSearch={handleSearch}
        onEdit={handleEdit}
        onDelete={handleDelete}
      />

    </div>
  );
}