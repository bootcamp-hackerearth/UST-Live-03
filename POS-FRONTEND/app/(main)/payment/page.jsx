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

      if (response.data.success === false) {
        alert(response.data.message);
        return;
      }

      alert("Payment Successful");

      router.push("/orders");
    } catch (err) {
      console.error("Payment failed:", err);

      alert("Payment Failed");
    } finally {
      setLoading(false);
    }
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

        <div className={styles.orderPanel}>
          <h3 className={styles.orderTitle}>
            Order Summary
          </h3>

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
                ₹{item.unitPrice}
              </span>
            </div>
          ))}


          <div className={styles.orderItem}>
            <span className={styles.orderName}>
              Payment Method
            </span>

            <span>
              {paymentMethod}
            </span>
          </div>


          <div className={styles.summaryTotal}>
            <span>Total</span>
            <span>
              ₹{total.toFixed(2)}
            </span>
          </div>
        </div>

        <div className={styles.orderItem}>
          <span className={styles.orderName}>
            Payment Method
          </span>
          <span>{paymentMethod}</span>
        </div>

        <div className={styles.summaryTotal}>
          <span>Total</span>
          <span>₹{total.toFixed(2)}</span>
        </div>
      </div>
    </div>
  );
}