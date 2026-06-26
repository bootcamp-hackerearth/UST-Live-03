"use client";

import { useEffect, useState } from "react";
import { listItems, getOrderById, DEFAULT_PAGINATION } from "@/services/api";
import TablePagination from "@/components/table/TablePagination";
import TableSkeleton from "@/components/table/TableSkeleton";
import OrderBillModal from "@/components/order/OrderBillModal";

export default function OrdersPage() {

  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalRecords, setTotalRecords] = useState(0);
  const [pageSize, setPageSize] = useState(DEFAULT_PAGINATION.sizePerPage);
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [billLoading, setBillLoading] = useState(false);

  useEffect(() => {
    loadOrders();
  }, [currentPage]);

  const loadOrders = async () => {
    try {
      setLoading(true);
      setError("");
      const data = await listItems("order", {
        page: currentPage,
        sizePerPage: pageSize,
        sortField: "id",
        sortDirection: "DESC",
      });
      setOrders(data?.items || []);
      setTotalPages(Number(data?.totalPages) || 0);
      setTotalRecords(Number(data?.totalRecords) || 0);
      setPageSize(Number(data?.sizePerPage) || DEFAULT_PAGINATION.sizePerPage);
    } catch (err) {
      console.log(err);
      setError("Failed to load orders");
    } finally {
      setLoading(false);
    }
  };

  const handleRowClick = async (orderId) => {
    try {
      setBillLoading(true);
      const data = await getOrderById(orderId);
      setSelectedOrder(data);
    } catch (err) {
      console.log(err);
    } finally {
      setBillLoading(false);
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return "—";
    return new Date(dateString).toLocaleString("en-IN", {
      day: "2-digit",
      month: "short",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  const PAYMENT_BADGE = {
    CASH: "bg-green-100 text-green-700",
    CARD: "bg-blue-100 text-blue-700",
    ONLINE: "bg-purple-100 text-purple-700",
  };

  if (loading) return <TableSkeleton />;

  if (error) {
    return (
      <div className="flex items-center justify-center h-125">
        <div className="bg-red-50 border border-red-200 text-red-600 px-6 py-4 rounded-2xl">
          {error}
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto">

      {billLoading && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/20 backdrop-blur-sm">
          <div className="bg-white rounded-2xl px-8 py-6 shadow-xl text-gray-700 font-medium">
            Loading bill...
          </div>
        </div>
      )}

      {selectedOrder && (
        <OrderBillModal
          order={selectedOrder}
          onClose={() => setSelectedOrder(null)}
        />
      )}

      <div className="flex items-center justify-between mb-8">
        <div>
          <h1 className="text-4xl font-bold text-gray-900">Orders</h1>
          <p className="text-gray-500 mt-2 text-lg">View all placed orders</p>
        </div>
        <div className="bg-[#f8fafc] border border-gray-200 px-5 py-3 rounded-2xl">
          <p className="text-sm text-gray-500">Total Orders</p>
          <p className="text-2xl font-bold text-gray-900">{totalRecords}</p>
        </div>
      </div>

      <div className="bg-white rounded-[30px] border border-gray-200 overflow-hidden shadow-sm">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-[#f8fafc] border-b border-gray-200">
              <tr>
                <th className="text-left px-6 py-5 text-sm font-semibold text-gray-500">Order No.</th>
                <th className="text-left px-6 py-5 text-sm font-semibold text-gray-500">Customer</th>
                <th className="text-left px-6 py-5 text-sm font-semibold text-gray-500">Payment Method</th>
                <th className="text-left px-6 py-5 text-sm font-semibold text-gray-500">Subtotal</th>
                <th className="text-left px-6 py-5 text-sm font-semibold text-gray-500">Discount</th>
                <th className="text-left px-6 py-5 text-sm font-semibold text-gray-500">Total</th>
                <th className="text-left px-6 py-5 text-sm font-semibold text-gray-500">Ordered At</th>
              </tr>
            </thead>
            <tbody>
              {orders.length === 0 ? (
                <tr>
                  <td colSpan={7} className="px-6 py-16 text-center text-gray-400">
                    No orders found
                  </td>
                </tr>
              ) : (
                orders.map((order) => (
                  <tr
                    key={order.id}
                    onClick={() => handleRowClick(order.id)}
                    className="border-b border-gray-100 hover:bg-[#fafcff] transition-colors cursor-pointer"
                  >
                    <td className="px-6 py-5">
                      <span className="font-semibold text-[#101828]">#{order.id}</span>
                    </td>
                    <td className="px-6 py-5 text-gray-700">{order.identifier}</td>
                    <td className="px-6 py-5">
                      <span className={`px-3 py-1 rounded-xl text-sm font-medium ${PAYMENT_BADGE[order.paymentMethod] || "bg-gray-100 text-gray-700"}`}>
                        {order.paymentMethod}
                      </span>
                    </td>
                    <td className="px-6 py-5 text-gray-700">₹{order.originalPrice}</td>
                    <td className="px-6 py-5 text-green-600 font-medium">−₹{order.discount}</td>
                    <td className="px-6 py-5">
                      <span className="font-bold text-[#101828]">₹{order.totalPrice}</span>
                    </td>
                    <td className="px-6 py-5 text-gray-500 text-sm">{formatDate(order.orderedAt)}</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      <TablePagination
        currentPage={currentPage}
        totalPages={totalPages}
        totalRecords={totalRecords}
        pageSize={pageSize}
        onPageChange={setCurrentPage}
      />
    </div>
  );
}