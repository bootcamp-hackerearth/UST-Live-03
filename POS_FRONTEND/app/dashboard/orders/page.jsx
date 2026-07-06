"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import axios from "@/config/axiosConfig";
import CommonList from "@/components/CommonList";

export default function OrderListPage() {
  const [orders, setOrders] = useState([]);
  const router = useRouter();

  const fetchOrders = async (keyword = "") => {
    try {
      const res = await axios.post("/order/list", {
        page: 0,
        sizePerPage: 50,
        keyword,
      });

      setOrders(res.data?.content || []);
    } catch (err) {
      console.log(err);
      setOrders([]);
    }
  };

  useEffect(() => {
    fetchOrders();
  }, []);

  const handleSearch = (keyword) => {
    fetchOrders(keyword);
  };

  const columns = [
    {
      header: "Order ID",
      accessor: "identifier",
    },
    {
      header: "Customer",
      accessor: "customerId",
    },
    {
      header: "Payment Type",
      accessor: "paymentType",
    },
    {
      header: "Total",
      render: (row) => `₹${row.totalPrice}`,
    },
    {
      header: "View",
      render: (row) => (
        <button
          onClick={() =>
            router.push(`/dashboard/orders/customers/${row.customerId}`)
          }
          className="text-blue-600 underline text-sm"
        >
          View Orders
        </button>
      ),
    },
  ];

  return (
    <div className="p-5">
      <h1 className="text-xl font-semibold mb-4">Orders</h1>

      <CommonList
        data={orders}
        columns={columns}
        onSearch={handleSearch}
      />
    </div>
  );
}