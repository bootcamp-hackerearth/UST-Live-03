"use client";

import { useEffect, useState } from "react";
import axios from "@/config/axiosConfig";
import CommonList from "@/components/CommonList";
import Toggle from "@/components/Toggle";
import { useRouter } from "next/navigation";

export default function CustomerPage() {
  const router = useRouter();
  const [data, setData] = useState([]);

  const normalizeCustomers = (rows = []) => {
    const seen = new Set();

    return rows.filter((item) => {
      const key =
        item?.identifier ??
        item?.customerName ??
        JSON.stringify(item);

      if (seen.has(key)) {
        return false;
      }

      seen.add(key);
      return true;
    });
  };

  const fetchCustomers = async (keyword = "") => {
    try {
      const res = await axios.post("/customer/list", {
        page: 0,
        sizePerPage: 100,
        keyword,
      });

      setData(normalizeCustomers(res.data?.content || []));
    } catch (err) {
      console.log(err);
      setData([]);
    }
  };

  useEffect(() => {
    fetchCustomers();
  }, []);

  const handleSearch = (keyword) => {
    fetchCustomers(keyword);
  };

  const toggleStatus = async (row) => {
    try {
      await axios.put(
        `/customer/toggle-status?identifier=${row.identifier}`
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
    { header: "Email", accessor: "identifier" },
    { header: "Name", accessor: "customerName" },
    { header: "Type", accessor: "partyType" },
    { header: "Phone", accessor: "phoneNo" },
    { header: "Balance", accessor: "balance" },
    { header: "Credit Limit", accessor: "creditLimit" },
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
    <div className="p-6 bg-slate-100 min-h-screen">
      <div className="flex justify-between mb-4">
        <h1 className="text-2xl font-bold">Customers</h1>

        <div className="flex gap-3">
          <button
            onClick={() =>
              router.push("/dashboard/customer/add")
            }
            className="bg-slate-900 text-white px-4 py-2 rounded"
          >
            Add Customer
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
          router.push(
            `/dashboard/customer/edit/${row.identifier}`
          )
        }
        onDelete={async (row) => {
          if (!confirm("Delete this Customer?")) return;

          try {
            await axios.delete(
              `/customer/delete?identifier=${row.identifier}`
            );
            fetchCustomers();
          } catch (err) {
            console.log(err);
          }
        }}
      />
    </div>
  );
}