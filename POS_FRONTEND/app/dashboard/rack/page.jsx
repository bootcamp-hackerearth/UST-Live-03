"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import axios from "@/config/axiosConfig";
import CommonList from "@/components/CommonList";
import Toggle from "@/components/Toggle";

export default function RackListPage() {
  const router = useRouter();
  const [racks, setRacks] = useState([]);

  const fetchRacks = async (keyword = "") => {
    try {
      const res = await axios.post("/rack/list", {
        page: 0,
        sizePerPage: 5,
        keyword,
      });

      setRacks(res.data.content || []);
    } catch (e) {
      console.log(e);
      setRacks([]);
    }
  };

  useEffect(() => {
    fetchRacks();
  }, []);

  const handleSearch = (keyword) => {
    fetchRacks(keyword);
  };

  const toggleStatus = async (row) => {
    try {
      await axios.put(
        `/rack/toggle-status?identifier=${row.identifier}`
      );

      setRacks((prev) =>
        prev.map((item) =>
          item.identifier === row.identifier
            ? { ...item, status: !item.status }
            : item
        )
      );
    } catch (e) {
      console.log(e);
    }
  };

  const columns = [
    {
      header: "ID",
      accessor: "id",
    },
    {
      header: "Identifier",
      accessor: "identifier",
    },
    {
      header: "Shelves",
      accessor: "shelves",
      render: (row) =>
        row.shelves?.length
          ? row.shelves.join(", ")
          : "-",
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

  const handleEdit = (row) => {
    router.push(`/dashboard/rack/edit/${row.identifier}`);
  };

  const handleDelete = async (row) => {
    if (!confirm("Delete Rack?")) return;

    try {
      await axios.delete(
        `/rack/delete?identifier=${row.identifier}`
      );
      fetchRacks();
    } catch (e) {
      console.log(e);
    }
  };

  return (
    <div>
      <div className="flex justify-between mb-5">
        <h1 className="text-2xl font-bold">
          Rack List
        </h1>

        <div className="flex gap-3">
          <button
            onClick={() => router.push("/dashboard/rack/add")}
            className="bg-slate-900 text-white px-4 py-2 rounded"
          >
            Add Rack
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
        data={racks}
        columns={columns}
        onSearch={handleSearch}
        onEdit={handleEdit}
        onDelete={handleDelete}
      />
    </div>
  );
}