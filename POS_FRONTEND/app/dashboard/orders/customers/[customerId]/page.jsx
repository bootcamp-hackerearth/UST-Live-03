"use client";

import { useEffect, useState } from "react";
import axios from "@/config/axiosConfig";
import { useSearchParams, useRouter } from "next/navigation";

export default function FilteredOrdersPage() {

  const [orders, setOrders] = useState([]);
  const router = useRouter();
  const searchParams = useSearchParams();

  const customerId = searchParams.get("customer");

  useEffect(() => {
    fetchOrders();
  }, [customerId]);

  const fetchOrders = async () => {
    try {
      const res = await axios.post("/order/list", {
        page: 0,
        sizePerPage: 50
      });

      let data = res.data?.content || [];

      if (customerId) {
        data = data.filter(
          (order) => order.customerId === customerId
        );
      }

      setOrders(data);
    } catch (err) {
      console.log(err);
    }
  };

  return (
    <div className="min-h-screen bg-gray-100 p-5">

      <div className="flex justify-between items-center mb-5">
        <h1 className="text-xl font-semibold">
          {customerId
            ? `Orders for ${customerId}`
            : "All Orders"}
        </h1>

        {customerId && (
          <button
            onClick={() => router.push("/dashboard/orders")}
            className="text-sm text-blue-600 underline"
          >
            Clear Filter
          </button>
        )}
      </div>

      {orders.length === 0 ? (
        <div className="bg-white p-6 text-center text-gray-400 rounded">
          No orders found
        </div>
      ) : (

        <div className="grid gap-4">

          {orders.map((order) => (

            <div
              key={order.identifier}
              className="bg-white border rounded-lg p-4 shadow-sm"
            >

              <div className="flex justify-between">

                <div>
                  <div className="text-sm font-semibold">
                    Order ID: {order.identifier}
                  </div>

                  <div className="text-xs text-gray-500">
                    Customer: {order.customerId}
                  </div>

                  <div className="text-xs text-gray-500">
                    Payment: {order.paymentType}
                  </div>
                </div>

                <div className="text-right">
                  <div className="text-xs text-gray-400">
                    Total
                  </div>

                  <div className="text-lg font-bold">
                    ₹{order.totalPrice}
                  </div>
                </div>

              </div>

              <div className="mt-3 flex justify-end">
                <button
                  onClick={() =>
                    router.push(`/dashboard/orders/${order.identifier}`)
                  }
                  className="px-3 py-1 bg-black text-white text-sm rounded"
                >
                  View Receipt
                </button>
              </div>

            </div>

          ))}

        </div>
      )}

    </div>
  );
}
