"use client";

import { useEffect, useState } from "react";
import api from "@/services/api";
import "./order.css";

export default function OrderPage() {
  const [orders, setOrders] = useState([]);
  const [expandedOrder, setExpandedOrder] = useState(null);
  const [orderItems, setOrderItems] = useState([]);

  useEffect(() => {
    loadOrders();
  }, []);

  const loadOrders = async () => {
    try {
      const response = await api.get("/api/order/list");
      setOrders(response.data || []);
    } catch (err) {
      console.error("Failed to load orders", err);
    }
  };

  const loadOrderDetails = async (identifier) => {
    try {

      if (expandedOrder === identifier) {
        setExpandedOrder(null);
        return;
      }

      const itemRes = await api.get(
        "/api/orderitem/getByOrder",
        {
          params: {
            orderIdentifier: identifier,
          },
        }
      );

      setOrderItems(itemRes.data || []);
      setExpandedOrder(identifier);

    } catch (err) {
      console.error(err);
    }
  };
  return (
    <div className="orderPage">
      <div className="orderLeft">
        <div className="pageHeader">
          <div>
            <h1>Order Management</h1>
            <p>Track and manage customer orders</p>
          </div>
        </div>
        <div className="ordersContainer">

          {orders.map((order) => (

            <button
              type="button"
              key={order.identifier}
              className="orderCard"
              onClick={() =>
                loadOrderDetails(order.identifier)
              }
            >

              <div className="orderHeader">

                <div>
                  <h3>{order.identifier}</h3>
                  <p>{order.customerId}</p>
                 
                </div>

                <div className="orderRight">

                <div className="rightRow">
                  <span className="orderDate">
                    {new Date(order.createdAt).toLocaleDateString()}
                  </span>

                  <span className={`statusBadge ${order.orderStatus?.toLowerCase()}`}>
                    {order.orderStatus}
                  </span>
                </div>

                <h2 className="price">₹{order.totalPrice}</h2>

              </div>

              </div>

              {expandedOrder === order.identifier && (

                <div className="orderDetails">

                  <h4>
                    Payment Mode:
                    {" "}
                    {order.paymentMethod}
                  </h4>

                  <table className="summaryTable">

                    <thead>
                      <tr>
                        <th>Product</th>
                        <th>Qty</th>
                        <th>Price</th>
                        <th>Total</th>
                      </tr>
                    </thead>

                    <tbody>

                      {orderItems.map((item) => (
                        <tr key={item.identifier}>
                          <td>{item.product}</td>
                          <td>{item.quantity}</td>
                          <td>₹{item.unitPrice}</td>
                          <td>₹{item.totalPrice}</td>
                        </tr>
                      ))}

                    </tbody>

                  </table>

                  <div className="grandTotal">
                    Grand Total:
                    ₹{order.totalPrice}
                  </div>

                </div>

              )}

            </button>

          ))}

        </div>

      </div>
    </div>
  );
}