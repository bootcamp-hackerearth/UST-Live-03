"use client";

import { useEffect, useState } from "react";
import axios from "@/config/axiosConfig";
import CommonList from "@/components/CommonList";
import { useRouter } from "next/navigation";

export default function RolePage() {
  const [data, setData] = useState([]);
  const router = useRouter();

  const fetchRoles = async () => {
    try {
      const res = await axios.post("/role/list", {
        page: 0,
        sizePerPage: 50,
        sortField: "identifier",
        sortDirection: "DESC"
      });

      setData(res.data?.content || []);
    } catch (err) {
      console.log("Role fetch error:", err);
    }
  };

  useEffect(() => {
    fetchRoles();
  }, []);

  const columns = [
    {
      header: "ID",
      accessor: "id"
    },
    {
      header: "Role",
      accessor: "identifier"
    },
    {
      header: "Description",
      accessor: "description"
    }
  ];

  return (
    <div className="min-h-screen bg-[#F6F7F9] p-6">

      <div className="bg-white border border-[#E5E7EB] rounded-xl p-5 mb-6 flex justify-between items-center shadow-sm">

        <div>
          <h1 className="text-lg font-semibold text-[#111827]">
            Roles
          </h1>
          <p className="text-sm text-gray-500">
            Manage system roles and permissions
          </p>
        </div>
        <div className="flex gap-3">
          <button
            onClick={() => router.push("/dashboard/role/add")}
            className="bg-[#2B2B2B] text-white px-4 py-2 rounded-lg hover:bg-black"
          >
            + Add Role
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
        data={data}
        columns={columns}
        onEdit={(row) =>
          router.push(`/dashboard/role/edit/${row.identifier}`)
        }
        onDelete={async (row) => {
          await axios.get(`/role/delete?identifier=${row.identifier}`);
          fetchRoles();
        }}
      />

    </div>
  );
}