"use client";

import { useEffect, useState } from "react";
import api from "../api/axios";
import Modal from "@/component/Modal";

const OrderList = () => {
  const [customers, setCustomers] = useState([]);

  const [selectedOrder, setSelectedOrder] = useState(null);
  const [showOrderModal, setShowOrderModal] = useState(false);
  const [orderEntries, setOrderEntries] = useState([]);

  const [selectedCustomerName, setSelectedCustomerName] = useState("");

  const [showCustomerDropdown, setShowCustomerDropdown] = useState(false);

  const [orders, setOrders] = useState([]);

  const [search, setSearch] = useState("");
  const [page, setPage] = useState(0);
  const [totalPage, setTotalPage] = useState(0);

  useEffect(() => {
    loadCustomers();
  }, []);

  const loadOrder = async () => {
    try {
      const response = await api.post("/api/order/list", {
        page,
        sizePerPage: 5,
        sortField: "identifier",
        sortDirection: "ASC",
        search,
      });

      setOrders(response.data.dtoList || []);
      setTotalPage(response.data.totalPage || 0);
    } catch (err) {
      console.error(err);
    }
  };
  useEffect(() => {
    loadOrder();
  }, [page, search]);
  const loadCustomers = async () => {
    try {
      const response = await api.get("/api/customer/list");

      setCustomers(response.data || []);
    } catch (err) {
      console.error(err);
    }
  };

  const loadOrders = async (email) => {
    try {
      const response = await api.get(`/api/order/getByCustomer?email=${email}`);
      console.log("Orders: ", response.data);
      setOrders(response.data || []);
    } catch (err) {
      console.error(err);
    }
  };
  const loadOrderEntries = async (orderId) => {
    try {
      const response = await api.get(
        `/api/orderentry/getByOrderId?orderId=${orderId}`,
      );

      setOrderEntries(response.data || []);
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <div className="p-6">
      <div className="bg-white rounded-xl shadow-md p-6 text-gray-600">
        <h2 className="text-2xl font-bold text-gray-800 mb-6">Order History</h2>

        {/* Customer Dropdown */}

        <div className="relative mb-6">
          <label
            htmlFor="customerSelect"
            className="block mb-2 font-medium text-gray-600"
          >
            Customer
          </label>

          <input
            id="customerSelect"
            type="text"
            value={selectedCustomerName}
            placeholder="Select Customer"
            readOnly
            onClick={() => setShowCustomerDropdown(!showCustomerDropdown)}
            className="w-full border rounded p-2"
          />

          {showCustomerDropdown && customers.length > 0 && (
            <ul className="absolute z-50 bg-white border rounded w-full mt-1 max-h-48 overflow-auto">
              {customers.map((customer) => (
                <li key={customer.id}>
                  <button
                    type="button"
                    className="w-full text-left p-2 hover:bg-blue-600 hover:text-white"
                    onClick={() => {
                      setSelectedCustomerName(customer.email);
                      loadOrders(customer.email);
                      setShowCustomerDropdown(false);
                    }}
                  >
                    {customer.email}
                  </button>
                </li>
              ))}
            </ul>
          )}
        </div>
        <div className="mb-4">
          <input
            type="text"
            placeholder="Search Order ID..."
            value={search}
            onChange={(e) => {
              setSearch(e.target.value);
              setPage(0);
            }}
            className="w-full border rounded-lg p-2"
          />
        </div>
        {orders.length > 0 && (
          <div className="overflow-x-auto mt-6">
            <table className="w-full border-collapse">
              <thead>
                <tr className="bg-gray-100">
                  <th className="border p-2">Order ID</th>

                  <th className="border p-2">Payment Method</th>

                  <th className="border p-2">Payment Time</th>

                  <th className="border p-2">Subtotal</th>

                  <th className="border p-2">Discount</th>

                  <th className="border p-2">Total</th>
                </tr>
              </thead>

              <tbody>
                {orders.map((order) => (
                  <tr
                    key={order.identifier}
                    className="cursor-pointer hover:bg-blue-50"
                    onClick={() => {
                      setSelectedOrder(order);

                      loadOrderEntries(order.identifier);
                      setShowOrderModal(true);
                    }}
                  >
                    <td className="border p-2">{order.identifier}</td>

                    <td className="border p-2">{order.paymentMethod}</td>

                    <td className="border p-2">{order.paymentTime}</td>

                    <td className="border p-2">₹ {order.subtotal}</td>

                    <td className="border p-2">₹ {order.discount}</td>

                    <td className="border p-2 font-bold text-green-600">
                      ₹ {order.total}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            <div className="flex justify-center items-center gap-4 mt-6">
              <button
                onClick={() => setPage((prev) => Math.max(prev - 1, 0))}
                disabled={page === 0}
                className="px-4 py-2 bg-gray-200 rounded disabled:opacity-50"
              >
                Previous
              </button>

              <span>
                Page {page + 1} of {totalPage}
              </span>

              <button
                onClick={() =>
                  setPage((prev) => (prev + 1 < totalPage ? prev + 1 : prev))
                }
                disabled={page + 1 >= totalPage}
                className="px-4 py-2 bg-gray-200 rounded disabled:opacity-50"
              >
                Next
              </button>
            </div>
          </div>
        )}
        <Modal isOpen={showOrderModal} onClose={() => setShowOrderModal(false)}>
          <h2 className="text-2xl font-bold text-gray-800 mb-6">
            Order Details
          </h2>

          <div className="grid grid-cols-2 gap-6 mb-6 text-gray-700">
            <div>
              <strong>Order ID</strong>
              <div>{selectedOrder?.identifier}</div>
            </div>

            <div>
              <strong>Customer</strong>
              <div>{selectedOrder?.customerEmail}</div>
            </div>

            <div>
              <strong>Payment Method</strong>
              <div>{selectedOrder?.paymentMethod}</div>
            </div>

            <div>
              <strong>Payment Time</strong>
              <div>{selectedOrder?.paymentTime}</div>
            </div>

            <div className="col-span-2">
              <strong>Shipping Address</strong>
              <div>{selectedOrder?.shippingAddress}</div>
            </div>
          </div>

          <table className="w-full border-collapse mb-6">
            <thead>
              <tr className="bg-gray-100">
                <th className="border p-2">Product</th>

                <th className="border p-2">Qty</th>

                <th className="border p-2">Unit Price</th>

                <th className="border p-2">Discount</th>

                <th className="border p-2">Total</th>
              </tr>
            </thead>

            <tbody>
              {orderEntries.map((entry) => (
                <tr key={entry.identifier}>
                  <td className="border p-2">{entry.product}</td>

                  <td className="border p-2">{entry.quantity}</td>

                  <td className="border p-2">₹ {entry.unitPrice}</td>

                  <td className="border p-2">₹ {entry.discount}</td>

                  <td className="border p-2">₹ {entry.totalPrice}</td>
                </tr>
              ))}
            </tbody>
          </table>

          <div className="flex justify-end">
            <div className="w-[320px] border rounded-lg p-4 bg-gray-50">
              <div className="flex justify-between">
                <span>Sub Total</span>
                <span>₹ {selectedOrder?.subtotal}</span>
              </div>

              <div className="flex justify-between text-red-600">
                <span>Discount</span>
                <span>- ₹ {selectedOrder?.discount}</span>
              </div>

              <div className="border-t my-2"></div>

              <div className="flex justify-between text-lg font-bold text-green-600">
                <span>Total</span>
                <span>₹ {selectedOrder?.total}</span>
              </div>
            </div>
          </div>
        </Modal>
      </div>
    </div>
  );
};

export default OrderList;
