"use client";

import { useEffect, useState } from "react";
import axios from "@/config/axiosConfig";
import CommonList from "@/components/CommonList";
import Toggle from "@/components/Toggle";
import { useRouter } from "next/navigation";

export default function ProductPage() {

  const [data, setData] = useState([]);
  const router = useRouter();

  const normalizeProducts = (rows = []) => {
    const seen = new Set();

    return rows.filter((item) => {
      const key = item?.identifier ?? item?.name ?? item?.id ?? JSON.stringify(item);

      if (seen.has(key)) {
        return false;
      }

      seen.add(key);
      return true;
    });
  };

  const fetchProducts = async () => {
    try {
      const res = await axios.post("/product/list", {
        page: 0,
        sizePerPage: 50,
        sortField: "name",
        sortDirection: "DESC"
      });

      setData(normalizeProducts(res.data?.content || []));

    } catch (err) {
      console.log("Product fetch error:", err);
    }
  };

  useEffect(() => {
    fetchProducts();
  }, []);

  const toggleStatus = async (row) => {
    try {
      await axios.put(
        `/product/toggle-status?identifier=${row.identifier}`
      );

      setData(prev =>
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
      header: "Product",
      accessor: "name"
    },
    {
      header: "Brand",
      accessor: "brand"
    },
    {
      header: "Model",
      accessor: "model"
    },
    {
      header: "Unit",
      accessor: "unit"
    },
    {
      header: "Price",
      accessor: "price"
    },
    {
      header: "Category",
      accessor: "category"
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

  return (
    <div className="min-h-screen bg-[#F6F7F9] p-6">

      <div className="bg-white border border-[#E5E7EB] rounded-xl p-5 mb-6 flex justify-between items-center shadow-sm">

        <div>
          <h1 className="text-lg font-semibold text-[#111827]">
            Products
          </h1>

          <p className="text-sm text-gray-500">
            Manage products
          </p>
        </div>

        <div className="flex gap-3">
          <button
            onClick={() =>
              router.push("/dashboard/product/add")
            }
            className="bg-[#2B2B2B] text-white px-4 py-2 rounded-lg hover:bg-black"
          >
            + Add Product
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
            `/dashboard/product/edit/${row.identifier || row.name}`
          )
        }
        onDelete={async (row) => {
          await axios.delete(
            `/product/delete?identifier=${row.identifier || row.name}`
          );
          fetchProducts();
        }}
      />

    </div>
  );
}
