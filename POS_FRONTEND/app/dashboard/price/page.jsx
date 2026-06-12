"use client";

import { useEffect, useState } from "react";
import axios from "@/config/axiosConfig";
import CommonList from "@/components/CommonList";
import { useRouter } from "next/navigation";

export default function PricePage() {
  const [data, setData] = useState([]);
  const router = useRouter();

  const fetchPrices = async () => {
    const res = await axios.post("/price/list", {
      page: 0,
      sizePerPage: 50,
      sortField: "identifier",
      sortDirection: "DESC"
    });

    setData(res.data.content || []);
  };

  useEffect(() => {
    fetchPrices();
  }, []);

  const columns = [
    { header: "Identifier", accessor: "identifier" },
    { header: "Product", accessor: "product" },
    { header: "Price", accessor: "priceAmount" },
    { header: "Type", accessor: "priceType" }
  ];

  return (
    <div className="space-y-6">

      <div className="bg-white border rounded-2xl p-5 flex justify-between items-center">
        <div>
          <h1 className="text-lg font-semibold">Price</h1>
          <p className="text-sm text-gray-500">Manage pricing</p>
        </div>
        <div className="flex gap-3">
          <button
            onClick={() => router.push("/dashboard/price/add")}
            className="bg-slate-900 text-white px-4 py-2 rounded-lg"
          >
            + Add Price
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
          router.push(
            `/dashboard/price/edit/${row.identifier}`
          )
        }
        onDelete={async (row) => {
          await axios.get(
            `/price/delete?identifier=${row.identifier}`
          );

          fetchPrices();
        }}
      />
    </div>
  );
}