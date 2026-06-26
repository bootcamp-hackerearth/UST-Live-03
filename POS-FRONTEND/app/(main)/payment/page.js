"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import api from "@/services/api";
import "./payment.css";

export default function PaymentPage() {
  const router = useRouter();

  const [data, setData] = useState(null);
  const [paymentMode, setPaymentMode] = useState("");
  const [showSuccess, setShowSuccess] = useState(false);
  const [showInvoice, setShowInvoice] = useState(false);

  const [cardData, setCardData] = useState({
    holderName: "",
    cardNumber: "",
    expiry: "",
    cvv: "",
  });

  const [upiData, setUpiData] = useState({
    provider: "",
    upiId: "",
  });

  const [orderNo, setOrderNo] = useState("");

  useEffect(() => {
    const checkoutData =
      localStorage.getItem("checkoutData");

    if (checkoutData) {
      setData(JSON.parse(checkoutData));
    }

    setOrderNo(`ORD-${Date.now()}`);
  }, []);

  const handlePayment = async () => {
    if (!paymentMode) {
      alert("Please select a payment mode");
      return;
    }

    try {
      const response = await api.post(
        "/api/order/create",
        null,
        {
          params: {
            cartId: data.customer,
            paymentMethod: paymentMode,
          },
        }
      );

      console.log(
        "Order Created Successfully",
        response.data
      );

      if (response.data?.identifier) {
        setOrderNo(
          response.data.identifier
        );
      }

      setShowSuccess(true);

    } catch (err) {
      console.error(
        "Order Creation Failed",
        err
      );

      alert(
        "Failed to create order. Please try again."
      );
    }
  };

  const printInvoice = () => {
    globalThis.print();
  };

  if (!data) {
    return (
      <div className="payment-loading">
        Loading...
      </div>
    );
  }

  return (
    <div className="payment-page">

      <h1 className="page-title">
        Payment & Order Summary
      </h1>

      <div className="payment-card">

        <h2>Customer Details</h2>

        <div className="customer-grid">

          <div>
            <span>Customer</span>
            <strong>{data.customer}</strong>
          </div>

          <div>
            <span>Warehouse</span>
            <strong>{data.warehouse}</strong>
          </div>

          <div>
            <span>Date</span>
            <strong>{data.saleDate}</strong>
          </div>

        </div>

      </div>

      <div className="payment-card">

        <h2>Purchased Products</h2>

        <table className="payment-table">
          <thead>
            <tr>
              <th>Product</th>
              <th>Qty</th>
              <th>Unit Price</th>
              <th>Total</th>
            </tr>
          </thead>

          <tbody>
            {data.items.map((item) => {
              const product =
                item.productName ||
                item.product ||
                "Unknown Product";

              const price = Number(
                item.price ??
                item.unitPrice ??
                0
              );

              const qty = Number(
                item.quantity || 0
              );

              return (
                <tr
                  key={
                    item.identifier ||
                    item.productName ||
                    item.product
                  }
                >
                  <td>{product}</td>
                  <td>{qty}</td>
                  <td>₹{price}</td>
                  <td>
                    ₹{price * qty}
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>

      </div>

      <div className="payment-card">

        <h2>Amount Summary</h2>

        <div className="summary-row">
          <span>Sub Total</span>
          <strong>
            ₹{Number(
              data.grandTotal
            ).toFixed(2)}
          </strong>
        </div>

        <div className="summary-row">
          <span>
            VAT ({data.vat}%)
          </span>
          <strong>
            ₹{Number(
              data.vatAmount
            ).toFixed(2)}
          </strong>
        </div>

        <div className="summary-row">
          <span>
            Discount ({data.discount}%)
          </span>
          <strong>
            ₹{Number(
              data.discountAmount
            ).toFixed(2)}
          </strong>
        </div>

        <div className="summary-row total">
          <span>Grand Total</span>
          <strong>
            ₹{Number(
              data.finalTotal
            ).toFixed(2)}
          </strong>
        </div>

      </div>

      <div className="payment-card">

        <h2>Payment Method</h2>

        <details className="payment-option">
          <summary>
            Credit / Debit Card
          </summary>

          <div className="payment-form">

            <input
              className="payment-input"
              aria-label="Card Holder Name"
              placeholder="Card Holder Name"
              value={cardData.holderName}
              onChange={(e) =>
                setCardData({
                  ...cardData,
                  holderName:
                    e.target.value,
                })
              }
            />

            <input
              className="payment-input"
              aria-label="Card Number"
              placeholder="Card Number"
              value={cardData.cardNumber}
              onChange={(e) =>
                setCardData({
                  ...cardData,
                  cardNumber:
                    e.target.value,
                })
              }
            />

            <input
              className="payment-input"
              aria-label="Expiry Date"
              placeholder="Expiry MM/YY"
              value={cardData.expiry}
              onChange={(e) =>
                setCardData({
                  ...cardData,
                  expiry:
                    e.target.value,
                })
              }
            />

            <input
              className="payment-input"
              aria-label="CVV"
              placeholder="CVV"
              value={cardData.cvv}
              onChange={(e) =>
                setCardData({
                  ...cardData,
                  cvv:
                    e.target.value,
                })
              }
            />

            <button
              className="done-btn"
              onClick={() =>
                setPaymentMode(
                  "Credit / Debit Card"
                )
              }
            >
              Done
            </button>

          </div>
        </details>

        <details className="payment-option">
          <summary>
            Cash On Delivery
          </summary>

          <div className="payment-form">
            <button
              className="done-btn"
              onClick={() =>
                setPaymentMode(
                  "Cash On Delivery"
                )
              }
            >
              Select COD
            </button>
          </div>
        </details>
        <details className="payment-option">
          <summary>UPI</summary>

          <div className="payment-form">

            <select
              className="payment-input"
              value={upiData.provider}
              onChange={(e) =>
                setUpiData({
                  ...upiData,
                  provider:
                    e.target.value,
                })
              }
            >
              <option value="">
                Select Provider
              </option>

              <option>
                Google Pay
              </option>

              <option>
                PhonePe
              </option>

              <option>
                Paytm
              </option>
            </select>

            <input
              className="payment-input"
              aria-label="UPI ID"
              placeholder="UPI ID"
              value={upiData.upiId}
              onChange={(e) =>
                setUpiData({
                  ...upiData,
                  upiId:
                    e.target.value,
                })
              }
            />

            <button
              className="done-btn"
              onClick={() =>
                setPaymentMode(
                  `UPI (${upiData.provider})`
                )
              }
            >
              Done
            </button>

          </div>
        </details>

      </div>

      <div className="payment-actions">

        <button
          className="back-btn"
          onClick={() => router.back()}
        >
          Back
        </button>

        <button
          className="pay-btn"
          onClick={handlePayment}
        >
          Make Payment
        </button>

      </div>

      {showSuccess && !showInvoice && (

        <div className="success-overlay">

          <div className="success-modal">

            <h2>
              ✅ Order Placed Successfully
            </h2>

            <p>
              <strong>
                Order No:
              </strong>{" "}
              {orderNo}
            </p>

            <p>
              <strong>
                Customer:
              </strong>{" "}
              {data.customer}
            </p>

            <p>
              <strong>
                Payment Mode:
              </strong>{" "}
              {paymentMode}
            </p>

            <p>
              <strong>
                Amount Paid:
              </strong>{" "}
              ₹
              {Number(
                data.finalTotal
              ).toFixed(2)}
            </p>

            <div className="success-actions">

              <button
                className="home-btn"
                onClick={() =>
                  router.push("/order")
                }
              >
                All Orders
              </button>

              <button
                className="order-btn"
                onClick={() =>
                  setShowInvoice(true)
                }
              >
                View Invoice
              </button>

            </div>

          </div>

        </div>

      )}
      {showInvoice && (

        <div className="success-overlay">

          <div
            className="success-modal invoice-modal"
          >

            <h2>
              🧾 TAX INVOICE
            </h2>

            <hr />

            <div className="invoice-header">
              <h2>🧾 TAX INVOICE</h2>
            </div>

            <div className="invoice-info">

              <div>
                <strong>Order No</strong>
                <br />
                {orderNo}
              </div>

              <div>
                <strong>Customer</strong>
                <br />
                {data.customer}
              </div>

              <div>
                <strong>Date</strong>
                <br />
                {data.saleDate}
              </div>

              <div>
                <strong>Payment Mode</strong>
                <br />
                {paymentMode}
              </div>

            </div>

            <table className="payment-table">

              <thead>
                <tr>
                  <th>Product</th>
                  <th>Qty</th>
                  <th>Price</th>
                  <th>Total</th>
                </tr>
              </thead>

              <tbody>

                {data.items.map((item) => {

                  const price =
                    Number(item.price || 0);

                  const qty =
                    Number(item.quantity || 0);

                  return (
                    <tr
                      key={
                        item.identifier ||
                        item.productName ||
                        item.product
                      }
                    >
                      <td>
                        {item.productName}
                      </td>

                      <td>{qty}</td>

                      <td>
                        ₹{price}
                      </td>

                      <td>
                        ₹{price * qty}
                      </td>
                    </tr>
                  );
                }
                )}

              </tbody>

            </table>

            <div className="invoice-total">
              Grand Total : ₹
              {Number(data.finalTotal).toFixed(2)}
            </div>

            <div className="invoice-actions">

              <button
                className="pay-btn"
                onClick={printInvoice}
              >
                Print Invoice
              </button>

              <button
                className="back-btn"
                onClick={() =>
                  setShowInvoice(false)
                }
              >
                Close
              </button>

            </div>

          </div>

        </div>

      )}

    </div>
  );
}