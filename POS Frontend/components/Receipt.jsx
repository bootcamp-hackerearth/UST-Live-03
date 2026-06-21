"use client";

import { useState } from "react";
import PropTypes from "prop-types";
import { htmlEscape } from "@/lib/security";

const fmt = (n) =>
  `₹${Number(n || 0).toLocaleString("en-IN", { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;

const CSS = `
@import url('https://fonts.googleapis.com/css2?family=Barlow:wght@400;500;600;700;800;900&display=swap');

.receipt-ov{
  position:fixed;
  inset:0;
  background-color:rgba(0,0,0,.6);
  display:flex;
  align-items:center;
  justify-content:center;
  z-index:9999;
  padding:20px;
}

.receipt-box{
  font-family:'Barlow',system-ui,sans-serif;
  background:#fff;
  border-radius:12px;
  max-width:480px;
  width:100%;
  height:90vh;
  display:flex;
  flex-direction:column;
  overflow:hidden;
  box-shadow:0 20px 60px rgba(0,0,0,.3);
}

.receipt-head{
  position:relative;
  background:linear-gradient(135deg,#005dab 0%,#003d75 100%);
  color:#fff;
  padding:20px;
  text-align:center;
  border-radius:12px 12px 0 0;
  flex-shrink:0;
}

.receipt-close{
  position:absolute;
  top:10px;
  right:10px;
  width:26px;
  height:26px;
  border-radius:5px;
  border:none;
  background:rgba(255,255,255,.2);
  color:#fff;
  font-size:11px;
  cursor:pointer;
  display:flex;
  align-items:center;
  justify-content:center;
  font-weight:700;
}

.receipt-check{
  font-size:28px;
  font-weight:900;
  margin-bottom:4px;
}

.receipt-title{
  font-size:18px;
  font-weight:800;
  margin:0 0 4px;
}

.receipt-sub{
  font-size:12px;
  margin:0;
  opacity:.9;
}

.receipt-body{
  flex:1;
  padding:20px;
  overflow-y:auto;
}

.receipt-info-grid{
  display:grid;
  grid-template-columns:1fr 1fr;
  gap:12px;
  margin-bottom:18px;
  padding-bottom:14px;
  border-bottom:1px solid #e8e8e8;
}

.receipt-info-label{
  font-size:10px;
  font-weight:700;
  color:#999;
  margin:0 0 4px;
  text-transform:uppercase;
}

.receipt-info-value{
  font-size:13px;
  font-weight:700;
  color:#111;
  margin:0;
}

.receipt-items-wrap{
  margin-bottom:18px;
}

.receipt-items-label{
  font-size:10px;
  font-weight:700;
  color:#999;
  margin:0 0 10px;
  text-transform:uppercase;
}

.receipt-items-list{
  max-height:300px;
  overflow-y:auto;
  overflow-x:hidden;
  display:flex;
  flex-direction:column;
  gap:8px;
  padding-right:6px;
  border:1px solid #f0f0f0;
  border-radius:6px;
}

.receipt-item-row{
  display:flex;
  justify-content:space-between;
  align-items:center;
  padding:8px;
  border-bottom:1px solid #f0f0f0;
}

.receipt-item-name{
  font-size:12px;
  font-weight:700;
  color:#111;
  margin:0 0 2px;
}

.receipt-item-sub{
  font-size:11px;
  color:#999;
  margin:0;
}

.receipt-item-total{
  text-align:right;
  font-weight:700;
  color:#005dab;
  font-size:13px;
}

.receipt-empty{
  font-size:12px;
  color:#999;
  padding:8px;
}

.receipt-totals{
  margin-bottom:18px;
  padding-top:14px;
  border-top:2px solid #005dab;
  border-bottom:2px solid #005dab;
  padding-bottom:14px;
}

.receipt-discount-row{
  display:flex;
  justify-content:space-between;
  margin-bottom:8px;
  font-size:12px;
}

.receipt-grand-row{
  display:flex;
  justify-content:space-between;
  font-size:16px;
  font-weight:900;
  color:#005dab;
}

.receipt-pay-grid{
  background:#f8f9fb;
  padding:12px;
  border-radius:6px;
  margin-bottom:18px;
  display:grid;
  grid-template-columns:1fr 1fr;
  gap:12px;
}

.receipt-pay-label{
  font-size:10px;
  font-weight:700;
  color:#999;
  margin:0 0 3px;
  text-transform:uppercase;
}

.receipt-pay-value{
  font-size:12px;
  font-weight:700;
  color:#111;
  margin:0;
}

.receipt-pay-value.id{
  color:#005dab;
}

.receipt-footer{
  text-align:center;
  padding:12px 0;
  border-top:1px solid #e8e8e8;
  font-size:11px;
  color:#999;
}

.receipt-actions{
  display:flex;
  gap:10px;
  padding:16px 20px;
  border-top:1px solid #e8e8e8;
  background:#f8f9fb;
  flex-shrink:0;
}

.receipt-action-btn{
  flex:1;
  padding:11px 16px;
  border-radius:6px;
  font-size:12px;
  font-weight:700;
  font-family:'Barlow',sans-serif;
  cursor:pointer;
  border:none;
}
`;


export default function Receipt({ data, receiptRef, onPrint, onClose, onViewOrder, onNewOrder }) {
  const [hoverPrint, setHoverPrint] = useState(false);
  const [hoverView, setHoverView] = useState(false);
  const [hoverNew, setHoverNew] = useState(false);

  if (!data) return null;

  return (
    <div className="receipt-ov">
      <style>{CSS}</style>
      <div className="receipt-box">
        <div className="receipt-head">
          {onClose && (
            <button className="receipt-close" onClick={onClose} type="button">✕</button>
          )}
          <div className="receipt-check">✓</div>
          <h2 className="receipt-title">Order Confirmed</h2>
          <p className="receipt-sub">Receipt #{data.orderId}</p>
        </div>

        <div className="receipt-body" ref={receiptRef}>
          <div className="receipt-info-grid">
            <div>
              <p className="receipt-info-label">Customer</p>
              <p className="receipt-info-value">{htmlEscape(data.customer?.customerName || "Walk-in")}</p>
            </div>
            <div style={{ textAlign: "right" }}>
              <p className="receipt-info-label">Date &amp; Time</p>
              <p className="receipt-info-value">{data.timestamp}</p>
            </div>
          </div>

          <div className="receipt-items-wrap">
            <p className="receipt-items-label">Items Ordered</p>
            {data.items?.length > 0 ? (
              <div className="receipt-items-list">
                {data.items.map((item, idx) => (
                  <div key={`${item.productName}-${idx}`} className="receipt-item-row">
                    <div style={{ flex: 1 }}>
                      <p className="receipt-item-name">{htmlEscape(item.productName || "Unknown Product")}</p>
                      <p className="receipt-item-sub">Qty: {item.quantity} × {fmt(item.price)} = {fmt(item.totalPrice)}</p>
                    </div>
                    <div className="receipt-item-total">{fmt(item.totalPrice)}</div>
                  </div>
                ))}
              </div>
            ) : (
              <p className="receipt-empty">No items</p>
            )}
          </div>

          <div className="receipt-totals">
            {data.totalDiscount > 0 && (
              <div className="receipt-discount-row">
                <span>Discount</span>
                <span>− {fmt(data.totalDiscount)}</span>
              </div>
            )}
            <div className="receipt-grand-row">
              <span>Total Amount</span>
              <span>{fmt(data.totalPrice)}</span>
            </div>
          </div>

          <div className="receipt-pay-grid">
            <div>
              <p className="receipt-pay-label">Payment Mode</p>
              <p className="receipt-pay-value">{data.paymentMode}</p>
            </div>
            <div>
              <p className="receipt-pay-label">Order ID</p>
              <p className="receipt-pay-value id">{data.orderId}</p>
            </div>
          </div>

          <div className="receipt-footer">
            <p>Thank you for your purchase!</p>
            <p>Please keep this receipt for your records</p>
          </div>
        </div>

        <div className="no-print receipt-actions">
          <button
            className="receipt-action-btn"
            onClick={onPrint}
            onMouseEnter={() => setHoverPrint(true)}
            onMouseLeave={() => setHoverPrint(false)}
            style={{ background: hoverPrint ? "#003d75" : "#005dab", color: "#fff" }}
          >
            🖨 Print Receipt
          </button>
          {onViewOrder && (
            <button
              className="receipt-action-btn"
              onClick={onViewOrder}
              onMouseEnter={() => setHoverView(true)}
              onMouseLeave={() => setHoverView(false)}
              style={{ background: hoverView ? "#f0f5ff" : "#fff", color: "#005dab", border: "1.5px solid #005dab" }}
            >
              📋 View Order
            </button>
          )}
          {onNewOrder && (
            <button
              className="receipt-action-btn"
              onClick={onNewOrder}
              onMouseEnter={() => setHoverNew(true)}
              onMouseLeave={() => setHoverNew(false)}
              style={{ background: hoverNew ? "#c0152a" : "#e31837", color: "#fff" }}
            >
              + New Order
            </button>
          )}
        </div>
      </div>
    </div>
  );
}

Receipt.propTypes = {
  data: PropTypes.shape({
    orderId: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    customer: PropTypes.object,
    items: PropTypes.array,
    totalPrice: PropTypes.number,
    totalDiscount: PropTypes.number,
    timestamp: PropTypes.string,
    paymentMode: PropTypes.string,
  }),
  receiptRef: PropTypes.object,
  onPrint: PropTypes.func.isRequired,
  onClose: PropTypes.func,
  onViewOrder: PropTypes.func,
  onNewOrder: PropTypes.func,
};
Receipt.defaultProps = {
  data: null,
  receiptRef: null,
  onClose: null,
  onViewOrder: null,
  onNewOrder: null,
};