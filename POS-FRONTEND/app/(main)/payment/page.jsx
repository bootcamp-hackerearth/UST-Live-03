"use client";

import { useState, useEffect } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import api, { getCartItems } from "@/services/api";
import styles from "./PaymentPage.module.css";

export default function PaymentPage() {
  const router = useRouter();
  const searchParams = useSearchParams();

  const cartId = searchParams.get("cartId");
  const total = Number(searchParams.get("total") || 0);

  const [paymentMethod, setPaymentMethod] = useState("CASH");
  const [amountReceived, setAmountReceived] = useState("");
  const [loading, setLoading] = useState(false);
  const [cartItems, setCartItems] = useState([]);

  const [showSuccessModal, setShowSuccessModal] = useState(false);
  const [createdOrder, setCreatedOrder] = useState(null);

  useEffect(() => {
    const fetchCartItems = async () => {
      try {
        const items = await getCartItems(cartId);
        setCartItems(items || []);
      } catch (err) {
        console.error("Failed to fetch cart items", err);
      }
    };

    if (cartId) {
      fetchCartItems();
    }
  }, [cartId]);

  const change =
    Number(amountReceived || 0) > total
      ? Number(amountReceived) - total
      : 0;

  const handlePayment = async () => {
    try {
      setLoading(true);

      const response = await api.post(
        "/api/order/create",
        null,
        {
          params: {
            cartId,
            paymentMethod,
          },
        }
      );

      const order = response.data;

      if (order.success === false) {
        alert(order.message);
        return;
      }

      setCreatedOrder(order);
      setShowSuccessModal(true);

    } catch (err) {
      console.error("Payment failed:", err);
      alert("Payment Failed");
    } finally {
      setLoading(false);
    }
};

  const handlePrint = () => {
      const invoiceWindow = window.open("", "_blank");

      if (!invoiceWindow) {
        alert("Please allow popups to print invoice");
        return;
      }

      const invoiceHtml = `
        <html>
          <head>
            <title>Invoice</title>
            <style>
              body {
                font-family: Arial, sans-serif;
                padding: 30px;
                color: #111827;
              }

              h1 {
                text-align: center;
                color: #006E74;
              }

              .header {
                margin-bottom: 20px;
              }

              .row {
                display: flex;
                justify-content: space-between;
                padding: 8px 0;
                border-bottom: 1px solid #ddd;
              }

              .item {
                display: flex;
                justify-content: space-between;
                padding: 8px 0;
              }

              .total {
                margin-top: 20px;
                padding-top: 10px;
                border-top: 2px solid #000;
                font-size: 18px;
                font-weight: bold;
                display: flex;
                justify-content: space-between;
              }

              .footer {
                margin-top: 40px;
                text-align: center;
                font-size: 12px;
                color: gray;
              }
            </style>
          </head>

          <body>

            <h1>Invoice</h1>

            <div class="header">
              <div class="row">
                <span>Order No</span>
                <b>${createdOrder.identifier}</b>
              </div>

              <div class="row">
                <span>Customer</span>
                <b>${createdOrder.customerName || "Walk-in"}</b>
              </div>

              <div class="row">
                <span>Payment</span>
                <b>${createdOrder.paymentMethod}</b>
              </div>

              <div class="row">
                <span>Status</span>
                <b>${createdOrder.orderStatus}</b>
              </div>
            </div>


            <h3>Items</h3>

            ${
              createdOrder.items?.map(
                (item) => `
                  <div class="item">
                    <span>
                      ${item.product} x ${item.quantity}
                    </span>

                    <span>
                      ₹${item.totalPrice}
                    </span>
                  </div>
                `
              ).join("")
            }


            <div class="total">
              <span>Total</span>
              <span>
                ₹${createdOrder.totalPrice}
              </span>
            </div>


            <div class="footer">
              Thank you for shopping with us!
            </div>


          </body>
        </html>
      `;

      invoiceWindow.document.documentElement.innerHTML = invoiceHtml;

      setTimeout(() => {
        invoiceWindow.print();
        invoiceWindow.close();
      }, 500);

    };

  const handleViewOrders = () => {
    setShowSuccessModal(false);
    router.push("/orders");
  };

  return (
    <div className={styles.pageContainer}>
      {/* LEFT */}
      <div className={styles.paymentSection}>
        <h1 className={styles.pageTitle}>
          Payment
        </h1>

        <div className={styles.card}>
          <h3 className={styles.cardTitle}>
            Payment Summary
          </h3>

          <div className={styles.infoRow}>
            <span>Cart</span>
            <span>{cartId}</span>
          </div>

          <div className={styles.infoRow}>
            <span>Total Amount</span>
            <span className={styles.totalAmount}>
              ₹{total.toFixed(2)}
            </span>
          </div>
        </div>

        <div className={styles.card}>
          <h3 className={styles.cardTitle}>
            Payment Method
          </h3>

          <div className={styles.methodGrid}>
            <button
              className={`${styles.methodBtn} ${paymentMethod === "CASH"
                  ? styles.methodSelected
                  : ""
                }`}
              onClick={() => setPaymentMethod("CASH")}
            >
              Cash
            </button>

            <button
              className={`${styles.methodBtn} ${paymentMethod === "CARD"
                  ? styles.methodSelected
                  : ""
                }`}
              onClick={() => setPaymentMethod("CARD")}
            >
              Card
            </button>

            <button
              className={`${styles.methodBtn} ${paymentMethod === "UPI"
                  ? styles.methodSelected
                  : ""
                }`}
              onClick={() => setPaymentMethod("UPI")}
            >
              UPI
            </button>
          </div>
        </div>

        {paymentMethod === "CASH" && (
          <div className={styles.card}>
            <h3 className={styles.cardTitle}>
              Cash Payment
            </h3>

            <input
              type="number"
              placeholder="Amount Received"
              value={amountReceived}
              onChange={(e) =>
                setAmountReceived(e.target.value)
              }
              className={styles.input}
            />

            <div className={styles.changeBox}>
              Change: ₹{change.toFixed(2)}
            </div>
          </div>
        )}

        {paymentMethod === "CARD" && (
          <div className={styles.card}>
            <h3 className={styles.cardTitle}>
              Card Details
            </h3>

            <input
              className={styles.input}
              placeholder="**** **** **** 1234"
            />

            <br />
            <br />

            <input
              className={styles.input}
              placeholder="Authorization Code"
            />
          </div>
        )}

        {paymentMethod === "UPI" && (
          <div className={styles.card}>
            <h3 className={styles.cardTitle}>
              UPI Details
            </h3>

            <input
              className={styles.input}
              placeholder="UPI Transaction Reference"
            />
          </div>
        )}

        <div className={styles.actionButtons}>
          <button
            className={styles.cancelBtn}
            onClick={() => router.back()}
          >
            Cancel
          </button>

          <button
            className={styles.completeBtn}
            onClick={handlePayment}
            disabled={loading}
          >
            {loading
              ? "Processing..."
              : `Complete Payment ₹${total.toFixed(2)}`}
          </button>
        </div>
      </div>

      {/* RIGHT */}
      <div className={styles.orderPanel}>
        <h3 className={styles.orderTitle}>
          Order Summary
        </h3>

        <div className={styles.orderItem}>
          <span className={styles.orderName}>
            Cart
          </span>
          <span>{cartId}</span>
        </div>

        <div className={styles.orderItem}>
          <span className={styles.orderName}>
            Payment
          </span>
          <span>{paymentMethod}</span>
        </div>

        <h4 className={styles.itemsHeading}>
          Items
        </h4>

        {cartItems.map((item) => (
          <div
            key={item.identifier}
            className={styles.orderItem}
          >
            <div>
              <div className={styles.orderName}>
                {item.product}
              </div>

              <small>
                Qty: {item.quantity}
              </small>
            </div>

            <span className={styles.orderPrice}>
              ₹{item.totalPrice}
            </span>
          </div>
        ))}

        <div className={styles.summaryTotal}>
          <span>Total</span>
          <span>₹{total.toFixed(2)}</span>
        </div>
            </div>

      {showSuccessModal && createdOrder && (
        <div className={styles.modalOverlay}>
          <div className={styles.modal}>
            <div className={styles.modalContent}>

              <h2 className={styles.modalTitle}>
                Payment Successful
              </h2>

              <div className={styles.detailRow}>
                <span>Order No</span>
                <b>{createdOrder.identifier}</b>
              </div>

              <div className={styles.detailRow}>
                <span>Customer</span>
                <b>{createdOrder.customerId}</b>
              </div>

              <div className={styles.detailRow}>
                <span>Payment</span>
                <b>{createdOrder.paymentMethod}</b>
              </div>

              <div className={styles.detailRow}>
                <span>Status</span>
                <b>{createdOrder.orderStatus}</b>
              </div>

              <h4 className={styles.itemsHeading}>
                Items
              </h4>

              {createdOrder.items?.map((item) => (
                <div
                  key={item.identifier}
                  className={styles.orderItem}
                >
                  <span>
                    {item.product} × {item.quantity}
                  </span>

                  <span>
                    ₹{item.totalPrice}
                  </span>
                </div>
              ))}

              <div className={styles.summaryTotal}>
                <span>Total</span>
                <span>₹{createdOrder.totalPrice}</span>
              </div>

              <div className={styles.modalButtons}>
                <button
                  className={styles.printBtn}
                  onClick={handlePrint}
                >
                  Print Invoice
                </button>

                <button
                  className={styles.ordersBtn}
                  onClick={handleViewOrders}
                >
                  View All Orders
                </button>
              </div>

            </div>
          </div>
        </div>
      )}

    </div>
  );
}