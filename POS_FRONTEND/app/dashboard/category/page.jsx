"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import axios from "@/config/axiosConfig";
import CommonList from "@/components/CommonList";
import Toggle from "@/components/Toggle";

export default function CategoryListPage() {

  const router = useRouter();
  const [categories, setCategories] = useState([]);

  const fetchCategories = async () => {
    const res = await axios.post("/category/list", {
      page: 0,
      sizePerPage: 5
    });

    setCategories(res.data.content || []);
  };

  useEffect(() => {
    fetchCategories();
  }, []);

  const toggleStatus = async (row) => {
    try {
      await axios.put(
        `/category/toggle-status?identifier=${row.identifier}`
      );

      setCategories(prev =>
        prev.map(item =>
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
      accessor: "id"
    },
    {
      header: "Identifier",
      accessor: "identifier"
    },
    {
      header: "Super Categories",
      accessor: "superCategory",
      render: (row) =>
        row.superCategory?.join(", ")
    },
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

  const handleEdit = (row) => {
    router.push(`/dashboard/category/edit/${row.identifier}`);
  };

  const handleDelete = async (row) => {
    if (!confirm("Delete Category?")) return;

    await axios.delete(
      `/category/delete?identifier=${row.identifier}`
    );

    fetchCategories();
  };

  return (
    <div>

      <div className="flex justify-between mb-5">

        <h1 className="text-2xl font-bold">
          Category List
        </h1>

        <div className="flex gap-3">
          <button
            onClick={() => router.push("/dashboard/category/add")}
            className="bg-slate-900 text-white px-4 py-2 rounded"
          >
            Add Category
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
        data={categories}
        columns={columns}
        onEdit={handleEdit}
        onDelete={handleDelete}
      />

    </div>
  );
}
