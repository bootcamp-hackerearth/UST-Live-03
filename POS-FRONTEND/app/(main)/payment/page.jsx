"use client";

import { useEffect, useState } from "react";
import { useSearchParams, useRouter } from "next/navigation";
import api from "@/services/api";
import "./payment.css";

export default function PaymentPage() {
  const searchParams = useSearchParams();
  const router = useRouter();

  const cartId = searchParams.get("cartId");
  const customer = searchParams.get("customer");
  const vat = Number(searchParams.get("vat") || 0);
  const discount = Number(searchParams.get("discount") || 0);

  const [cartEntries, setCartEntries] = useState([]);
  const [paymentMethod, setPaymentMethod] = useState("CASH");

  const [cardNumber, setCardNumber] = useState("");
  const [cardHolder, setCardHolder] = useState("");

  const [upiApp, setUpiApp] = useState("");
  const [upiId, setUpiId] = useState("");

  const [showSuccess, setShowSuccess] = useState(false);
  const [showOrderSummary, setShowOrderSummary] =
    useState(false);

  const [orderData, setOrderData] = useState(null);

  useEffect(() => {
    fetchCart();
  }, []);

  const fetchCart = async () => {
    try {
      const res = await api.post(
        "/api/cartentry/getByCartId",
        {
          cartId,
        }
      );

      setCartEntries(res.data || []);
    } catch (err) {
      console.error(err);
    }
  };

  const subTotal = cartEntries.reduce(
    (sum, item) =>
      sum + Number(item.totalPrice || 0),
    0
  );

  const vatAmount = (subTotal * vat) / 100;

  const discountAmount =
    (subTotal * discount) / 100;

  const grandTotal =
    subTotal + vatAmount - discountAmount;

  const clearCartAndGo = async () => {
    try {
      await api.post(
  "/api/cartentry/clear",
  null,
  {
    params: {
      cartId,
    },
  }
);

      router.push("/cart");
    } catch (err) {
      console.error(err);

      // still go to cart
      router.push("/cart");
    }
  };
const printInvoice = () => {
  const win = window.open("", "", "width=900,height=700");

  if (!win) {
    alert("Popup blocked by browser");
    return;
  }

  const doc = win.document;

  doc.head.innerHTML = `
    <title>Invoice</title>

    <style>
      body{
        font-family: Arial, sans-serif;
        padding:40px;
        color:#111827;
        background:#f8fafc;
      }

      .header{
        text-align:center;
        margin-bottom:30px;
      }

      .header h1{
        margin:0;
        color:rgb(0,60,81);
      }

      .info{
        margin-bottom:25px;
        padding:15px;
        background:#ffffff;
        border:1px solid #e5e7eb;
        border-radius:8px;
      }

      .info p{
        margin:8px 0;
      }

      table{
        width:100%;
        border-collapse:collapse;
        margin-top:20px;
        table-layout:fixed;
        background:white;
      }

      th{
        background:rgb(0,60,81);
        color:white;
        padding:14px;
        text-align:left;
      }

      td{
        padding:14px;
        border:1px solid #ddd;
      }

      th:nth-child(1),
      td:nth-child(1){
        text-align:left;
      }

      th:nth-child(2),
      td:nth-child(2){
        text-align:center;
        width:80px;
      }

      th:nth-child(3),
      td:nth-child(3){
        text-align:right;
        width:140px;
      }

      th:nth-child(4),
      td:nth-child(4){
        text-align:right;
        width:140px;
      }

      tbody tr:nth-child(even){
        background:#f8fafc;
      }

      .totals{
        margin-top:30px;
        width:320px;
        margin-left:auto;
      }

      .totals div{
        display:flex;
        justify-content:space-between;
        margin:10px 0;
      }

      .grand{
        font-size:22px;
        font-weight:bold;
        color:rgb(0,60,81);
        border-top:2px solid #ddd;
        padding-top:10px;
      }
    </style>
  `;
    doc.body.innerHTML = `
    <div class="header">
      <h1>INVOICE</h1>
    </div>

    <div class="info">
      <p><strong>Order No:</strong> ${orderData.identifier}</p>
      <p><strong>Customer:</strong> ${customer}</p>
      <p><strong>Payment Method:</strong> ${paymentMethod}</p>
    </div>

    <table>
      <thead>
        <tr>
          <th>Product</th>
          <th>Qty</th>
          <th>Unit Price</th>
          <th>Total</th>
        </tr>
      </thead>

      <tbody>
        ${cartEntries
          .map(
            (item) => `
              <tr>
                <td>${item.product}</td>
                <td>${item.quantity}</td>
                <td>₹${Number(item.unitPrice).toFixed(2)}</td>
                <td>₹${Number(item.totalPrice).toFixed(2)}</td>
              </tr>
            `
          )
          .join("")}
      </tbody>
    </table>

    <div class="totals">
      <div>
        <span>Sub Total</span>
        <strong>₹${subTotal.toFixed(2)}</strong>
      </div>

      <div>
        <span>VAT (${vat}%)</span>
        <strong>₹${vatAmount.toFixed(2)}</strong>
      </div>

      <div>
        <span>Discount (${discount}%)</span>
        <strong>₹${discountAmount.toFixed(2)}</strong>
      </div>

      <div class="grand">
        <span>Total</span>
        <strong>₹${grandTotal.toFixed(2)}</strong>
      </div>
    </div>
  `;

  doc.close();

  setTimeout(() => {
    win.print();
  }, 300);
};
  const makePayment = async () => {
    if (
      paymentMethod === "CARD" &&
      (!cardNumber || !cardHolder)
    ) {
      alert("Please enter card details");
      return;
    }

    if (
      paymentMethod === "UPI" &&
      (!upiApp || !upiId)
    ) {
      alert("Please enter UPI details");
      return;
    }

    try {
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

      if (response.data.success) {
        setOrderData(response.data);
        setShowSuccess(true);
      } else {
        alert(response.data.message);
      }
    } catch (err) {
      console.error(err);
      alert("Payment Failed");
    }
  };
return (
  <div className="paymentPage">

    <div className="paymentCard">

      <h1>Payment</h1>

      <div className="summaryBox">

        <div className="row">
          <span>Customer</span>
          <strong>{customer}</strong>
        </div>

        <div className="row">
          <span>Cart</span>
          <strong>{cartId}</strong>
        </div>

        <div className="summaryTableWrapper">
          <table className="summaryTable">

            <thead>
              <tr>
                <th>Product</th>
                <th>Qty</th>
                <th>Unit Price</th>
                <th>Total</th>
              </tr>
            </thead>

            <tbody>

              {cartEntries.map((item) => (
                <tr key={item.identifier}>
                  <td>{item.product}</td>
                  <td>{item.quantity}</td>
                  <td>₹{item.unitPrice}</td>
                  <td>₹{item.totalPrice}</td>
                </tr>
              ))}

            </tbody>

          </table>
        </div>

        <div className="summaryTotals">

          <div className="row">
            <span>Sub Total</span>
            <strong>
              ₹{subTotal.toFixed(2)}
            </strong>
          </div>

          <div className="row">
            <span>VAT ({vat}%)</span>
            <strong>
              ₹{vatAmount.toFixed(2)}
            </strong>
          </div>

          <div className="row">
            <span>
              Discount ({discount}%)
            </span>
            <strong>
              - ₹{discountAmount.toFixed(2)}
            </strong>
          </div>

          <div className="row grandTotal">
            <span>Grand Total</span>
            <strong>
              ₹{grandTotal.toFixed(2)}
            </strong>
          </div>

        </div>

      </div>

      <div className="paymentMethods">

        <h3>Select Payment Method</h3>

        <div className="paymentOptions">

          <button
            className={`paymentOption ${
              paymentMethod === "CASH"
                ? "active"
                : ""
            }`}
            onClick={() =>
              setPaymentMethod("CASH")
            }
          >
            Cash
          </button>

          <button
            className={`paymentOption ${
              paymentMethod === "CARD"
                ? "active"
                : ""
            }`}
            onClick={() =>
              setPaymentMethod("CARD")
            }
          >
            Card
          </button>

          <button
            className={`paymentOption ${
              paymentMethod === "UPI"
                ? "active"
                : ""
            }`}
            onClick={() =>
              setPaymentMethod("UPI")
            }
          >
            UPI
          </button>

        </div>

        {paymentMethod === "CARD" && (
          <div className="paymentDetails">

            <input
              type="text"
              placeholder="Card Number"
              value={cardNumber}
              onChange={(e) =>
                setCardNumber(
                  e.target.value
                )
              }
            />

            <input
              type="text"
              placeholder="Card Holder Name"
              value={cardHolder}
              onChange={(e) =>
                setCardHolder(
                  e.target.value
                )
              }
            />

          </div>
        )}

        {paymentMethod === "UPI" && (
          <div className="paymentDetails">

            <select
              value={upiApp}
              onChange={(e) =>
                setUpiApp(e.target.value)
              }
            >
              <option value="">
                Select App
              </option>

              <option value="GPAY">
                Google Pay
              </option>

              <option value="PHONEPE">
                PhonePe
              </option>

              <option value="PAYTM">
                Paytm
              </option>

              <option value="BHIM">
                BHIM
              </option>

            </select>

            <input
              type="text"
              placeholder="UPI ID"
              value={upiId}
              onChange={(e) =>
                setUpiId(
                  e.target.value
                )
              }
            />

          </div>
        )}

      </div>

      <div className="buttonRow">

        <button
          className="cancelBtn"
          onClick={() => router.back()}
        >
          Back
        </button>

        <button
          className="payBtn"
          onClick={makePayment}
        >
          Make Payment
        </button>

      </div>

    </div>

    {/* SUCCESS MODAL */}

    {showSuccess && orderData && (

      <div className="successOverlay">

        <div className="successModal">

          <div className="successIcon">
            ✓
          </div>

          <h2>
            Order Placed Successfully
          </h2>

          <div id="invoicePrintArea">

            <div className="successCard">

              <div className="successRow">
                <span>Order Number</span>
                <strong>
                  {orderData.identifier}
                </strong>
              </div>

              <div className="successRow">
                <span>Customer</span>
                <strong>{customer}</strong>
              </div>

              <div className="successRow">
                <span>Payment Method</span>
                <strong>
                  {paymentMethod}
                </strong>
              </div>

              <div className="successRow">
                <span>Items</span>
               <strong>
  {cartEntries.reduce(
    (sum, item) => sum + Number(item.quantity || 0),
    0
  )}
</strong>
              </div>

              <div className="successRow">
                <span>Sub Total</span>
                <strong>
                  ₹{subTotal.toFixed(2)}
                </strong>
              </div>

              <div className="successRow">
                <span>VAT</span>
                <strong>
                  ₹{vatAmount.toFixed(2)}
                </strong>
              </div>

              <div className="successRow">
                <span>Discount</span>
                <strong>
                  ₹{discountAmount.toFixed(2)}
                </strong>
              </div>

              <div className="successRow totalAmount">
                <span>Total Paid</span>
                <strong>
                  ₹{grandTotal.toFixed(2)}
                </strong>
              </div>

            </div>

          </div>

          <div className="successActions">

            <button
              className="homeBtn"
              onClick={printInvoice}
            >
              Print Invoice
            </button>

            <button
              className="ordersBtn"
              onClick={() => {
                setShowSuccess(false);
                setShowOrderSummary(true);
              }}
            >
              View Order
            </button>

          </div>

        </div>

      </div>

    )}

    {/* ORDER SUMMARY MODAL */}

    {showOrderSummary && (

      <div className="successOverlay">

        <div className="successModal orderSummaryModal">

          <h2>Order Summary</h2>
<div className="summaryHeader">
  <p>
    <strong>Order:</strong> {orderData?.identifier}
  </p>

  <p>
    <strong>Customer:</strong> {customer}
  </p>

  <p>
    <strong>Payment:</strong> {paymentMethod}
  </p>
</div>

          <div className="summaryTableWrapper">

            <table className="summaryTable">

              <thead>
                <tr>
                  <th>Product</th>
                  <th>Qty</th>
                  <th>Unit Price</th>
                  <th>Total</th>
                </tr>
              </thead>

              <tbody>

                {cartEntries.map((item) => (
                  <tr key={item.identifier}>
                    <td>{item.product}</td>
                    <td>{item.quantity}</td>
                    <td>₹{item.unitPrice}</td>
                    <td>₹{item.totalPrice}</td>
                  </tr>
                ))}

              </tbody>

            </table>

          </div>

          <div className="successCard">

            <div className="successRow">
              <span>Sub Total</span>
              <strong>
                ₹{subTotal.toFixed(2)}
              </strong>
            </div>

            <div className="successRow">
              <span>VAT</span>
              <strong>
                ₹{vatAmount.toFixed(2)}
              </strong>
            </div>

            <div className="successRow">
              <span>Discount</span>
              <strong>
                ₹{discountAmount.toFixed(2)}
              </strong>
            </div>

            <div className="successRow totalAmount">
              <span>Total Paid</span>
              <strong>
                ₹{grandTotal.toFixed(2)}
              </strong>
            </div>

          </div>

          <div className="successActions">

            <button
              className="homeBtn"
              onClick={clearCartAndGo}
            >
              Back To Cart
            </button>

            <button
              className="ordersBtn"
              onClick={() =>
                router.push("/order")
              }
            >
              All Orders
            </button>

          </div>

        </div>

      </div>
    )}

  </div>
);
}