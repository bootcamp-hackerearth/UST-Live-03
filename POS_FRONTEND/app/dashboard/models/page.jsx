"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import axios from "@/config/axiosConfig";
import CommonList from "@/components/CommonList";
import Toggle from "@/components/Toggle";

export default function ModelListPage() {

  const router = useRouter();
  const [models, setModels] = useState([]);

  const normalizeModels = (rows = []) => {
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

  const fetchModels = async () => {
    try {
      const res = await axios.post("/model/list", {
        page: 0,
        sizePerPage: 50
      });
      setModels(normalizeModels(res.data?.content || []));
    } catch (e) {
      console.log(e);
      setModels([]);
    }
  };

  useEffect(() => {
    fetchModels();
  }, []);

  const toggleStatus = async (row) => {
    try {
      await axios.put(`/model/toggle-status?identifier=${row.identifier}`);
      fetchModels();
    } catch (e) {
      console.log(e);
    }
  };

  const handleEdit = (row) => {
    router.push(`/dashboard/models/edit/${row.identifier}`);
  };

  const handleDelete = async (row) => {
    if (!confirm("Delete this model?")) return;

    try {
      await axios.delete(`/model/delete?identifier=${row.identifier}`);
      fetchModels();
    } catch (e) {
      console.log(e);
    }
  };

  const columns = [
    { header: "ID", accessor: "id" },
    { header: "Model Name", accessor: "identifier" },
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
        <h1 className="text-2xl font-bold">Model List</h1>

        <div className="flex gap-3">
          <button
            onClick={() => router.push("/dashboard/models/add")}
            className="bg-slate-900 text-white px-4 py-2 rounded"
          >
            Add Model
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
        data={models}
        columns={columns}
        onEdit={handleEdit}
        onDelete={handleDelete}
      />

    </div>
  );
}
