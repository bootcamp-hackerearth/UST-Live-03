"use client";

import { useEffect, useState } from "react";
import axios from "@/config/axiosConfig";
import CommonList from "@/components/CommonList";
import Toggle from "@/components/Toggle";
import { useRouter } from "next/navigation";

export default function RolePage() {
  const [data, setData] = useState([]);
  const router = useRouter();

  const normalizeRoles = (rows = []) => {
    const seen = new Set();

    return rows.filter((item) => {
      const key =
        item?.identifier ??
        item?.id ??
        JSON.stringify(item);

      if (seen.has(key)) {
        return false;
      }

      seen.add(key);
      return true;
    });
  };

  const fetchRoles = async (keyword = "") => {
    try {
      const res = await axios.post("/role/list", {
        page: 0,
        sizePerPage: 50,
        sortField: "identifier",
        sortDirection: "DESC",
        keyword,
      });

      setData(normalizeRoles(res.data?.content || []));
    } catch (err) {
      console.log("Role fetch error:", err);
      setData([]);
    }
  };

  useEffect(() => {
    fetchRoles();
  }, []);

  const handleSearch = (keyword) => {
    fetchRoles(keyword);
  };

  const toggleStatus = async (row) => {
    try {
      await axios.put(
        `/role/toggle-status?identifier=${row.identifier}`
      );

      setData((prev) =>
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
      header: "Role",
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
        onSearch={handleSearch}
        onEdit={(row) =>
          router.push(`/dashboard/role/edit/${row.identifier}`)
        }
        onDelete={async (row) => {
          await axios.delete(
            `/role/delete?identifier=${row.identifier}`
          );
          fetchRoles();
        }}
      />
    </div>
  );
}