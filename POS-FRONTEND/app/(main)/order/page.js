"use client";

import { useEffect, useState } from "react";
import {
  listItems,
  getItem
} from "@/services/api";
import "./order.css";

export default function Order() {

  const [orders, setOrders] = useState([]);
  const [expandedId, setExpandedId] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchOrders();
  }, []);

  const fetchOrders = async () => {
    try {

      const response = await listItems(
        "order",
        {
          page: 0,
          sizePerPage: 100,
        }
      );

      const orderList = response?.content || [];

      const ordersWithItems =
        await Promise.all(
          orderList.map(async (order) => {
            try {

              const fullOrder =
                await getItem(
                  "order",
                  order.identifier
                );

              return fullOrder;

            } catch (err) {
              console.error(err);
              return order;
            }
          })
        );

      setOrders(ordersWithItems);

    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const toggle = (id) => {
    setExpandedId(
      expandedId === id ? null : id
    );
  };

  const formatPrice = (value) => {
    const num = Number(value || 0);

    return `₹${num.toLocaleString(
      "en-IN"
    )}`;
  };

  const getTotal = (order) => {

    if (
      order?.totalPrice !== undefined &&
      order?.totalPrice !== null
    ) {
      return Number(order.totalPrice);
    }

    return (
      order?.items?.reduce(
        (sum, item) =>
          sum +
          Number(
            item.totalPrice || 0
          ),
        0
      ) || 0
    );
  };

  const totalOrders = orders.length;

  const deliveredOrders =
    orders.filter(
      (o) =>
        o.orderStatus === "DELIVERED" ||
        o.orderStatus === "CONFIRMED"
    ).length;

  const revenue = orders.reduce(
    (sum, order) =>
      sum + getTotal(order),
    0
  );

  if (loading) {
    return (
      <div className="order-page">
        Loading Orders...
      </div>
    );
  }

  const getStatusClass = (status) => {
    if (status === "DELIVERED") {
      return "delivered";
    }

    if (status === "CONFIRMED") {
      return "confirmed";
    }

    return "pending";
  };


  return (
    <div className="order-page">

      <div className="order-stats">

        <div className="stat-card">
          <div className="stat-title">
            Total Orders
          </div>

          <div className="stat-value">
            {totalOrders}
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-title">
            Delivered Orders
          </div>

          <div className="stat-value green">
            {deliveredOrders}
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-title">
            Total Revenue
          </div>

          <div className="stat-value blue">
            {formatPrice(revenue)}
          </div>
        </div>

      </div>
      {orders.map((order) => (

        <div
          key={order.identifier}
          className="order-card"
        >

          <button
            type="button"
            className="order-header"
            onClick={() => toggle(order.identifier)}
          >

            <div className="order-left">

              <div className="order-no">
                {order.identifier}
              </div>

              <div className="order-user">
                {order.customerId}
              </div>

            </div>

            <div className="order-right">

              <div className="order-date">
                {order.createdAt
                  ? new Date(order.createdAt).toLocaleDateString()
                  : "-"}
              </div>

              <span
                className={`order-status ${getStatusClass(order.orderStatus)}`}
              >
                {order.orderStatus}
              </span>

              <div className="order-total">
                {formatPrice(getTotal(order))}
              </div>

            </div>

          </button>

          {expandedId ===
            order.identifier && (

              <div className="order-details">

                <div>
                  <b>Payment Mode:</b>{" "}
                  {order.paymentMethod}
                </div>

                <table className="table">

                  <thead>
                    <tr>
                      <th>Product</th>
                      <th>Qty</th>
                      <th>Price</th>
                      <th>Total</th>
                    </tr>
                  </thead>

                  <tbody>

                    {order.items?.map((item) => (
                      <tr key={item.identifier}>

                        <td>
                          {item.product}
                        </td>

                        <td>
                          {item.quantity}
                        </td>

                        <td>
                          {formatPrice(
                            item.unitPrice
                          )}
                        </td>

                        <td>
                          {formatPrice(
                            item.totalPrice
                          )}
                        </td>

                      </tr>
                    )
                    )}

                  </tbody>

                </table>

                <div className="grand-total">

                  Grand Total:{" "}
                  {formatPrice(
                    getTotal(order)
                  )}

                </div>

              </div>

            )}

        </div>

      ))}

    </div>
  );
}