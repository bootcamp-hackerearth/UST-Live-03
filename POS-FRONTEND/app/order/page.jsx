"use client";

import React, { useEffect, useState } from "react";
import POSLayout from "../components/PosLayout";
import commonApi from "../services/commonApi";

function OrderPage() {
  const [orders, setOrders] = useState([]);
  const [products, setProducts] = useState([]);
  const [search, setSearch] = useState("");
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [showInvoice, setShowInvoice] = useState(false);

  const [currentPage, setCurrentPage] = useState(1);
  const [sizePerPage, setSizePerPage] = useState(20);

  const payload = {
    page: 0,
    sizePerPage: 1000,
    sortDirection: "DESC",
    sortField: "id",
    search: "",
  };

  const fetchOrders = async () => {
    try {
      const res = await commonApi.list("order", payload);
      setOrders(res.data.dtoList || []);
    } catch (err) {
      console.log(err);
    }
  };

  const fetchActiveProducts = async () => {
    try {
      const res = await commonApi.active("product");
      setProducts(Array.isArray(res.data) ? res.data : []);
    } catch (err) {
      console.log(err);
    }
  };

  useEffect(() => {
    fetchOrders();
    fetchActiveProducts();
  }, []);

  const formatAmount = (value) =>
    Number(value || 0).toLocaleString("en-IN", {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    });

  const formatDate = (value) => {
    if (!value) return "-";

    return new Date(value).toLocaleString("en-IN", {
      day: "2-digit",
      month: "short",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  const getProductName = (productIdentifier) => {
    const product = products.find((p) => p.identifier === productIdentifier);
    return product?.name || productIdentifier;
  };

  const handleViewOrder = async (order) => {
    try {
      const res = await commonApi.get("order", "identifier", order.identifier);
      setSelectedOrder(res.data);
      setShowInvoice(true);
    } catch (err) {
      console.log(err);
      alert("Failed to fetch order details");
    }
  };

  const filteredOrders = orders.filter((order) => {
    const text = [
      order.identifier,
      order.customerIdentifier,
      order.paymentMethod,
      order.createdBy,
    ]
      .filter(Boolean)
      .join(" ")
      .toLowerCase();

    return text.includes(search.toLowerCase());
  });

  const totalRecords = filteredOrders.length;
  const totalPages = Math.ceil(totalRecords / sizePerPage);
  const startIndex = (currentPage - 1) * sizePerPage;

  const paginatedOrders = filteredOrders.slice(
    startIndex,
    startIndex + sizePerPage
  );

  return (
    <POSLayout>
      <div className="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
        <div className="p-5 border-b flex flex-wrap items-center justify-between gap-3">
          <div>
            <p className="text-xs uppercase tracking-[0.2em] text-red-500 font-semibold">
              Sales history
            </p>
            <h1 className="text-2xl font-bold text-gray-900">Orders</h1>
          </div>

          <input
            type="text"
            placeholder="Search order, customer, payment..."
            value={search}
            onChange={(e) => {
              setSearch(e.target.value);
              setCurrentPage(1);
            }}
            className="w-full sm:w-80 border border-gray-300 rounded-xl px-4 py-3 text-gray-800 outline-none focus:ring-2 focus:ring-red-500"
          />
        </div>

        <div className="px-5 py-4 flex items-center gap-2 text-sm text-gray-700">
          <span>Show</span>

          <select
            value={sizePerPage}
            onChange={(e) => {
              setSizePerPage(Number(e.target.value));
              setCurrentPage(1);
            }}
            className="border border-gray-300 rounded-lg px-3 py-2"
          >
            <option value={5}>5</option>
            <option value={10}>10</option>
            <option value={20}>20</option>
            <option value={50}>50</option>
          </select>

          <span>entries</span>
        </div>

        <div className="overflow-x-auto">
          <table className="min-w-full border-collapse">
            <thead className="bg-gray-100">
              <tr>
                <th className="px-4 py-3 text-left text-sm font-bold text-gray-700">
                  SL
                </th>
                <th className="px-4 py-3 text-left text-sm font-bold text-gray-700">
                  Order No
                </th>
                <th className="px-4 py-3 text-left text-sm font-bold text-gray-700">
                  Customer
                </th>
                <th className="px-4 py-3 text-left text-sm font-bold text-gray-700">
                  Payment
                </th>
                <th className="px-4 py-3 text-right text-sm font-bold text-gray-700">
                  Total
                </th>
                <th className="px-4 py-3 text-left text-sm font-bold text-gray-700">
                  Created By
                </th>
                <th className="px-4 py-3 text-left text-sm font-bold text-gray-700">
                  Created On
                </th>
                <th className="px-4 py-3 text-center text-sm font-bold text-gray-700">
                  Action
                </th>
              </tr>
            </thead>

            <tbody>
              {paginatedOrders.length > 0 ? (
                paginatedOrders.map((order, index) => (
                  <tr
                    key={order.identifier}
                    className="border-t hover:bg-gray-50"
                  >
                    <td className="px-4 py-4 text-sm text-gray-700">
                      {startIndex + index + 1}
                    </td>

                    <td className="px-4 py-4 text-sm font-semibold text-gray-900">
                      {order.identifier}
                    </td>

                    <td className="px-4 py-4 text-sm text-gray-700">
                      {order.customerIdentifier}
                    </td>

                    <td className="px-4 py-4 text-sm">
                      <span className="rounded-full bg-gray-100 px-3 py-1 text-xs font-bold text-gray-700">
                        {order.paymentMethod}
                      </span>
                    </td>

                    <td className="px-4 py-4 text-sm text-right font-bold text-gray-900">
                      ₹{formatAmount(order.totalPrice)}
                    </td>

                    <td className="px-4 py-4 text-sm text-gray-700">
                      {order.createdBy}
                    </td>

                    <td className="px-4 py-4 text-sm text-gray-700">
                      {formatDate(order.createdOn)}
                    </td>

                    <td className="px-4 py-4 text-center">
                      <button
                        onClick={() => handleViewOrder(order)}
                        className="border border-gray-300 bg-white hover:bg-gray-100 px-4 py-2 rounded-lg text-sm font-semibold text-gray-800"
                      >
                        View
                      </button>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={8} className="py-16 text-center text-gray-400">
                    No orders found
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        <div className="p-5 border-t flex flex-wrap items-center justify-between gap-3">
          <div className="text-sm text-gray-600">
            Showing Page {totalPages === 0 ? 0 : currentPage} of {totalPages}
            {" "} Total Records: {totalRecords}
          </div>

          <div className="flex gap-2">
            <button
              disabled={currentPage === 1}
              onClick={() => setCurrentPage((prev) => prev - 1)}
              className="px-4 py-2 border border-gray-300 rounded-lg text-gray-700 disabled:opacity-50 hover:bg-gray-100"
            >
              Previous
            </button>

            <button
              disabled={currentPage === totalPages || totalPages === 0}
              onClick={() => setCurrentPage((prev) => prev + 1)}
              className="px-4 py-2 border border-gray-300 rounded-lg text-gray-700 disabled:opacity-50 hover:bg-gray-100"
            >
              Next
            </button>
          </div>
        </div>
      </div>

      {showInvoice && selectedOrder && (
        <div className="fixed inset-0 bg-black/40 z-50 flex items-center justify-center">
          <div className="bg-white rounded-2xl shadow-2xl w-full max-w-3xl max-h-[90vh] overflow-auto p-6">
            <div className="flex items-start justify-between border-b pb-4">
              <div>
                <p className="text-xs uppercase tracking-[0.2em] text-red-500 font-semibold">
                  Invoice
                </p>
                <h2 className="text-2xl font-bold text-gray-900">
                  {selectedOrder.identifier}
                </h2>
                <p className="text-sm text-gray-500 mt-1">
                  {formatDate(selectedOrder.createdOn)}
                </p>
              </div>

              <button
                onClick={() => setShowInvoice(false)}
                className="w-8 h-8 border border-gray-300 rounded-lg hover:bg-gray-100 text-gray-700 font-bold"
              >
                ×
              </button>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 mt-5">
              <div className="rounded-xl border border-gray-200 p-4">
                <p className="text-xs uppercase text-gray-500 font-bold">
                  Customer
                </p>
                <p className="mt-1 font-semibold text-gray-900">
                  {selectedOrder.customerIdentifier}
                </p>
              </div>

              <div className="rounded-xl border border-gray-200 p-4">
                <p className="text-xs uppercase text-gray-500 font-bold">
                  Payment
                </p>
                <p className="mt-1 font-semibold text-gray-900">
                  {selectedOrder.paymentMethod}
                </p>
              </div>
            </div>

            <div className="mt-5 border rounded-xl overflow-hidden">
              <table className="min-w-full border-collapse">
                <thead className="bg-gray-100">
                  <tr>
                    <th className="px-4 py-3 text-left text-sm font-bold text-gray-700">
                      Product
                    </th>
                    <th className="px-4 py-3 text-center text-sm font-bold text-gray-700">
                      MRP
                    </th>
                    <th className="px-4 py-3 text-center text-sm font-bold text-gray-700">
                      Discount
                    </th>
                    <th className="px-4 py-3 text-center text-sm font-bold text-gray-700">
                      Unit Price
                    </th>
                    <th className="px-4 py-3 text-center text-sm font-bold text-gray-700">
                      Qty
                    </th>
                    <th className="px-4 py-3 text-right text-sm font-bold text-gray-700">
                      Total
                    </th>
                  </tr>
                </thead>

                <tbody>
                  {selectedOrder.entryList?.map((entry) => (
                    <tr key={entry.identifier} className="border-t">
                      <td className="px-4 py-3 text-sm font-semibold text-gray-800">
                        {getProductName(entry.productIdentifier)}
                      </td>

                      <td className="px-4 py-3 text-center text-sm text-gray-700">
                        ₹{formatAmount(entry.mrp)}
                      </td>

                      <td className="px-4 py-3 text-center text-sm text-gray-700">
                        ₹{formatAmount(entry.unitDiscount)}
                      </td>

                      <td className="px-4 py-3 text-center text-sm text-gray-700">
                        ₹{formatAmount(entry.unitPrice)}
                      </td>

                      <td className="px-4 py-3 text-center text-sm font-bold text-gray-900">
                        {entry.quantity}
                      </td>

                      <td className="px-4 py-3 text-right text-sm font-bold text-gray-900">
                        ₹{formatAmount(entry.totalPrice)}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            <div className="mt-5 flex justify-end">
              <div className="w-full sm:w-80 rounded-xl border border-gray-200 bg-gray-50 p-4 space-y-2">
                <div className="flex justify-between text-gray-700">
                  <span>Original Price</span>
                  <span>₹{formatAmount(selectedOrder.originalPrice)}</span>
                </div>

                <div className="flex justify-between text-gray-700">
                  <span>Discount</span>
                  <span>₹{formatAmount(selectedOrder.discount)}</span>
                </div>

                <div className="flex justify-between text-xl font-bold text-gray-900 border-t pt-2">
                  <span>Total</span>
                  <span>₹{formatAmount(selectedOrder.totalPrice)}</span>
                </div>

                <div className="flex justify-between text-gray-700">
                  <span>Received</span>
                  <span>₹{formatAmount(selectedOrder.receivedAmount)}</span>
                </div>

                <div className="flex justify-between text-gray-700">
                  <span>Change</span>
                  <span>₹{formatAmount(selectedOrder.changeAmount)}</span>
                </div>
              </div>
            </div>

            <div className="mt-6 flex justify-end gap-3">
              <button
                onClick={() => globalThis.print()}
                className="bg-red-600 hover:bg-red-700 text-white px-5 py-2 rounded-lg font-bold"
              >
                Print
              </button>
            </div>
          </div>
        </div>
      )}
    </POSLayout>
  );
}

export default OrderPage;