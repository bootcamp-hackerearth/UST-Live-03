"use client";

import { useEffect, useState } from "react";
import axios from "@/config/axiosConfig";
import CommonList from "@/components/CommonList";
import { useRouter } from "next/navigation";

export default function NodePage() {
  const [data, setData] = useState([]);
  const router = useRouter();

  const fetchNodes = async () => {
    try {
      const res = await axios.post("/node/list", {
        page: 0,
        sizePerPage: 50,
        sortField: "identifier",
        sortDirection: "DESC"
      });

      setData(res.data?.content || []);
    } catch (err) {
      console.log(err);
    }
  };

  useEffect(() => {
    fetchNodes();
  }, []);

  const columns = [
    { header: "Path", accessor: "path" },
    { header: "Identifier", accessor: "identifier" },
    {
      header: "Roles",
      accessor: "roles",
      render: (row) =>
        Array.isArray(row.roles)
          ? row.roles.join(", ")
          : row.roles
    }
  ];

  return (
    <div className="min-h-screen bg-[#F6F7F9] p-6">

      <div className="bg-white border border-[#E5E7EB] rounded-xl p-5 mb-6 flex justify-between items-center">

        <div>
          <h1 className="text-lg font-semibold text-[#111827]">
            Nodes
          </h1>
          <p className="text-sm text-gray-500">
            Manage system navigation nodes
          </p>
        </div>

        <div className="flex gap-3">

          <button
            onClick={() => router.push("/dashboard/node/add")}
            className="bg-[#2B2B2B] text-white px-4 py-2 rounded-lg hover:bg-black"
          >
            + Add Node
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
          router.push(`/dashboard/node/edit/${row.identifier}`)
        }
        onDelete={async (row) => {
          await axios.get(`/node/delete?identifier=${row.identifier}`);
          fetchNodes();
        }}
      />

    </div>
  );
}