"use client";

import { useEffect, useState } from "react";
import axios from "@/config/axiosConfig";
import CommonList from "@/components/CommonList";
import { useRouter } from "next/navigation";
import { Plus, Users } from "lucide-react";

export default function UserListPage() {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);

  const router = useRouter();

  const fetchUsers = async (keyword = "") => {
    try {
      setLoading(true);

      const res = await axios.post("/user/list", {
        page: 0,
        sizePerPage: 50,
        sortField: "identifier",
        sortDirection: "DESC",
        keyword,
      });

      setData(res.data?.content || []);
    } catch (err) {
      console.log(err);
      setData([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  const handleSearch = (keyword) => {
    fetchUsers(keyword);
  };

  const columns = [
    { header: "Name", accessor: "name" },
    { header: "Username", accessor: "username" },
    { header: "Phone", accessor: "phoneNo" },
    {
      header: "Roles",
      render: (row) =>
        row.roles?.length
          ? row.roles.join(", ")
          : "N/A",
    },
  ];

  return (
    <div className="space-y-5">
      <div
        className="
        bg-white border border-slate-200
        rounded-xl p-5
        flex justify-between items-center
      "
      >
        <div className="flex items-center gap-2">
          <Users size={18} className="text-slate-700" />

          <div>
            <h1 className="text-base font-semibold text-slate-800">
              Users
            </h1>
            <p className="text-xs text-slate-500">
              Manage system users & roles
            </p>
          </div>
        </div>

        <div className="flex gap-3">
          <button
            onClick={() => router.push("/dashboard/user/add")}
            className="
              flex items-center gap-2
              bg-[#111827] text-white
              px-4 py-2 rounded-lg
              hover:bg-black transition
              text-sm
            "
          >
            <Plus size={16} />
            Add User
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
        loading={loading}
        onSearch={handleSearch}
        onEdit={(row) =>
          router.push(`/dashboard/user/edit/${row.username}`)
        }
        onDelete={async (row) => {
          if (!confirm("Delete this User?")) return;

          try {
            await axios.delete(
              `/user/delete?username=${row.username}`
            );
            fetchUsers();
          } catch (err) {
            console.log(err);
          }
        }}
      />
    </div>
  );
}