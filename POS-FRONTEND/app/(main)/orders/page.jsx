"use client";

import { useState, useEffect } from "react";
import CommonList from "@/app/components/CommonList/CommonList";
import styles from "./OrdersPage.module.css";

import {
  listItems,
  getItem,
  getOrderItems,
} from "@/services/api";

const OrdersPage = () => {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);

  const [searchTerm, setSearchTerm] = useState("");

  const [selectedOrder, setSelectedOrder] = useState(null);

  const sizePerPage = 5;

  const fetchOrders = async () => {
    if (data.length === 0) {
      setLoading(true);
    }

    try {
      const res = await listItems("order", {
        page,
        sizePerPage,
        sortField: "id",
        search: searchTerm,
      });

      setData(res.content || []);
      setTotalPages(res.totalPages || 1);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchOrders();
  }, [page, searchTerm]);

  useEffect(() => {
    setPage(0);
  }, [searchTerm]);

  const handleView = async (row) => {
    try {
      console.log("Clicked order:", row);

      const order = await getItem(
        "order",
        row.identifier
      );

      const items = await getOrderItems(
        row.identifier
      );

      const orderWithItems = {
        ...order,
        items,
      };

      console.log("Order Details:", orderWithItems);
      setSelectedOrder(orderWithItems);

    } catch (err) {
      console.error(
        "Failed loading order details",
        err
      );
    }
  };

  const columns = [
    {
      label: "SL NO",
      render: (row, index) =>
        page * sizePerPage + index + 1,
    },
    {
      label: "Order No",
      render: (row) => (
        <button
          onClick={() => handleView(row)}
          style={{
            background: "none",
            border: "none",
            color: "#006E74",
            fontWeight: "600",
            cursor: "pointer",
          }}
        >
          {row.identifier}
        </button>
      ),
    },
    {
      key: "customerId",
      label: "Customer",
    },
    {
      key: "paymentMethod",
      label: "Payment",
    },
    {
      key: "orderStatus",
      label: "Status",
    },
    {
      key: "totalPrice",
      label: "Total",
    },
  ];

  return (
    <div className={styles.pageContainer}>

      <div className={styles.listSection}>
        <CommonList
          title="Orders"
          data={data}
          columns={columns}
          loading={loading}
          page={page}
          setPage={setPage}
          totalPages={totalPages}
          searchTerm={searchTerm}
          setSearchTerm={setSearchTerm}
        />
      </div>

      {selectedOrder && (
        <div className={styles.detailsPanel}>

          <h2>Order Details</h2>

          <div className={styles.detailRow}>
            <span>Order No</span>
            <b>{selectedOrder.identifier}</b>
          </div>

          <div className={styles.detailRow}>
            <span>Customer</span>
            <b>{selectedOrder.customerId}</b>
          </div>

          <div className={styles.detailRow}>
            <span>Payment</span>
            <b>{selectedOrder.paymentMethod}</b>
          </div>

          <div className={styles.detailRow}>
            <span>Status</span>
            <b>{selectedOrder.orderStatus}</b>
          </div>

          <h3 className={styles.itemsTitle}>
            Items
          </h3>

          {selectedOrder.items?.map((item) => (
            <div
              key={item.identifier}
              className={styles.itemRow}
            >
              <span>
                {item.product}
              </span>

              <span>
                {item.quantity} x ₹{item.unitPrice}
              </span>
            </div>
          ))}

          <div className={styles.total}>
            Total: ₹{selectedOrder.totalPrice}
          </div>

        </div>
      )}

    </div>
  );
};

export default OrdersPage;