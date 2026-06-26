"use client";

import React from "react";
import PropTypes from "prop-types";

const METHOD_ICONS = {
  CASH: (
    <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
      <rect x="2" y="6" width="20" height="12" rx="2"/>
      <circle cx="12" cy="12" r="3"/>
      <path d="M6 12h.01M18 12h.01"/>
    </svg>
  ),
  CARD: (
    <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
      <rect x="2" y="5" width="20" height="14" rx="2"/>
      <line x1="2" y1="10" x2="22" y2="10"/>
    </svg>
  ),
  UPI: (
    <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
      <path d="M12 2L2 7l10 5 10-5-10-5z"/>
      <path d="M2 17l10 5 10-5"/>
      <path d="M2 12l10 5 10-5"/>
    </svg>
  ),
};

function UpiQR() {
  return (
    <div style={{ display: "flex", flexDirection: "column", alignItems: "center", padding: "24px 0 8px" }}>
      <div style={{ padding: "16px", background: "#fff", borderRadius: "16px", border: "1.5px solid #e2e8f0", boxShadow: "0 4px 24px rgba(0,0,0,0.07)" }}>
        <svg width="180" height="180" viewBox="0 0 180 180" xmlns="http://www.w3.org/2000/svg">
          <rect x="10" y="10" width="50" height="50" rx="6" fill="#1e293b"/>
          <rect x="18" y="18" width="34" height="34" rx="3" fill="white"/>
          <rect x="24" y="24" width="22" height="22" rx="2" fill="#1e293b"/>
          
          <rect x="120" y="10" width="50" height="50" rx="6" fill="#1e293b"/>
          <rect x="128" y="18" width="34" height="34" rx="3" fill="white"/>
          <rect x="134" y="24" width="22" height="22" rx="2" fill="#1e293b"/>
          
          <rect x="10" y="120" width="50" height="50" rx="6" fill="#1e293b"/>
          <rect x="18" y="128" width="34" height="34" rx="3" fill="white"/>
          <rect x="24" y="134" width="22" height="22" rx="2" fill="#1e293b"/>
          
          {[
  [72,10],[80,10],[88,10],[72,18],[88,18],[80,26],[72,34],[80,34],[88,34],
  [72,42],[88,42],[72,50],[80,50],[88,50],[72,58],[80,58],[88,58],
  [10,72],[18,72],[34,72],[42,72],[58,72],
  [10,80],[26,80],[50,80],[66,80],[72,80],[88,80],[96,80],[104,80],[112,80],[128,80],[144,80],[160,80],
  [10,88],[18,88],[34,88],[58,88],[72,88],[80,88],[96,88],[112,88],[120,88],[136,88],[152,88],[168,88],
  [10,96],[26,96],[42,96],[58,96],[80,96],[96,96],[104,96],[120,96],[136,96],[152,96],
  [10,104],[18,104],[34,104],[50,104],[66,104],[88,104],[104,104],[112,104],[128,104],[144,104],[160,104],
  [10,112],[26,112],[42,112],[58,112],[72,112],[80,112],[96,112],[120,112],[136,112],[152,112],[168,112],
  [72,120],[88,120],[96,120],[104,120],[112,120],[128,120],[144,120],[160,120],[168,120],
  [80,128],[96,128],[112,128],[120,128],[136,128],[152,128],
  [72,136],[88,136],[104,136],[128,136],[144,136],[160,136],[168,136],
].map(([x, y]) => (
  <rect
    key={`${x}-${y}`}
    x={x}
    y={y}
    width="6"
    height="6"
    rx="12"
    fill="#1e293b"
  />
))}
          <rect x="76" y="76" width="28" height="28" rx="6" fill="white" stroke="#e2e8f0" strokeWidth="1"/>
          <text x="90" y="93" textAnchor="middle" fontSize="9" fontWeight="800" fill="#2563eb" fontFamily="sans-serif">UPI</text>
        </svg>
      </div>
      <p style={{ margin: "14px 0 4px", fontSize: "13px", fontWeight: "600", color: "#1e293b" }}>Scan the dynamic dynamic QR code</p>
      <p style={{ margin: 0, fontSize: "11px", color: "#94a3b8" }}>Terminal reference: merchant@gateway</p>
    </div>
  );
}

function CardUI() {
  return (
    <div style={{ padding: "20px 0 8px" }}>
      <div style={{
        background: "linear-gradient(135deg, #1e293b 0%, #0f172a 100%)",
        borderRadius: "16px",
        padding: "24px",
        marginBottom: "20px",
        position: "relative",
        overflow: "hidden",
        minHeight: "155px",
        boxShadow: "0 10px 25px -5px rgba(15,23,42,0.15)"
      }}>
        <div style={{ position: "absolute", top: "-30px", right: "-30px", width: "120px", height: "120px", borderRadius: "50%", background: "rgba(255,255,255,0.04)" }}/>
        <div style={{ width: "38px", height: "28px", background: "linear-gradient(135deg, #fbbf24 0%, #d97706 100%)", borderRadius: "6px", marginBottom: "32px", position: "relative" }}>
          <div style={{ position: "absolute", inset: "4px", border: "1px solid rgba(0,0,0,0.15)", borderRadius: "4px" }} />
        </div>

        <div style={{ fontFamily: "monospace", fontSize: "16px", color: "#f8fafc", letterSpacing: "3.5px", marginBottom: "18px", textShadow: "0 2px 4px rgba(0,0,0,0.2)" }}>
          •••• •••• •••• 8842
        </div>

        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-end" }}>
          <div>
            <div style={{ fontSize: "9px", color: "#64748b", letterSpacing: "1px", marginBottom: "2px", fontWeight: "600" }}>CARDHOLDER</div>
            <div style={{ fontSize: "12px", color: "#f1f5f9", fontWeight: "500", letterSpacing: "0.5px" }}>EXTERNAL CHIP TERMINAL</div>
          </div>
          <div style={{ textAlign: "right" }}>
            <div style={{ fontSize: "9px", color: "#64748b", letterSpacing: "1px", marginBottom: "2px", fontWeight: "600" }}>EXPIRES</div>
            <div style={{ fontSize: "12px", color: "#f1f5f9", fontFamily: "monospace" }}>••/••</div>
          </div>
        </div>
      </div>

      <div style={{ background: "#f0fdf4", border: "1px solid #bbf7d0", borderRadius: "12px", padding: "12px 16px", display: "flex", alignItems: "center", gap: "10px" }}>
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#16a34a" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
          <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
        </svg>
        <span style={{ fontSize: "12px", color: "#15803d", fontWeight: "600" }}>Insert, Swipe or Tap instrument on external point-of-sale machine</span>
      </div>
    </div>
  );
}

Payment.propTypes = {
  cart: PropTypes.shape({
    totalPrice: PropTypes.number.isRequired,
  }).isRequired,
  paymentMethod: PropTypes.string,
  setPaymentMethod: PropTypes.func.isRequired,
  receivedAmount: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  setReceivedAmount: PropTypes.func.isRequired,
  changeAmount: PropTypes.number.isRequired,
  setStep: PropTypes.func.isRequired,
  handleCheckoutSubmit: PropTypes.func.isRequired,
};

export default Payment;

function Payment({
  cart,
  paymentMethod,
  setPaymentMethod,
  receivedAmount,
  setReceivedAmount,
  changeAmount,
  setStep,
  handleCheckoutSubmit,
}) {
  return (
    <div style={{ maxWidth: "860px", margin: "0 auto", padding: "12px" }}>
      <div style={{ display: "flex", gap: "24px", alignItems: "flex-start", fontFamily: "system-ui, -apple-system, sans-serif" }}>
        
        <div style={{ flex: "1.5", background: "#ffffff", borderRadius: "20px", border: "1px solid #e2e8f0", boxShadow: "0 1px 3px 0 rgba(0, 0, 0, 0.05)", overflow: "hidden" }}>
          
          <div style={{ padding: "20px 24px", borderBottom: "1px solid #f1f5f9", backgroundColor: "#fff" }}>
            <p style={{ margin: 0, fontSize: "11px", fontWeight: "700", color: "#3b82f6", letterSpacing: "1.5px", textTransform: "uppercase" }}>Transaction Gateway</p>
            <h3 style={{ margin: "4px 0 0", fontSize: "18px", fontWeight: "700", color: "#0f172a" }}>Select Settlement Method</h3>
          </div>

          <div style={{ padding: "24px" }}>
            <div style={{ display: "flex", gap: "12px", marginBottom: "24px" }}>
              {["CASH", "CARD", "UPI"].map((m) => {
                const isSelected = paymentMethod === m;
                return (
                  <button
                    key={m}
                    onClick={() => setPaymentMethod(m)}
                    style={{
                      flex: 1,
                      padding: "16px 12px",
                      fontWeight: "600",
                      fontSize: "13px",
                      border: isSelected ? "2px solid #2563eb" : "1.5px solid #e2e8f0",
                      backgroundColor: isSelected ? "#f0f6ff" : "#ffffff",
                      color: isSelected ? "#1d4ed8" : "#475569",
                      cursor: "pointer",
                      borderRadius: "12px",
                      display: "flex",
                      flexDirection: "column",
                      alignItems: "center",
                      gap: "10px",
                      transition: "all 0.15s ease-in-out",
                    }}
                  >
                    <span style={{ color: isSelected ? "#2563eb" : "#94a3b8" }}>{METHOD_ICONS[m]}</span>
                    {m}
                  </button>
                );
              })}
            </div>

            {paymentMethod === "CASH" && (
              <div style={{ display: "flex", flexDirection: "column", gap: "16px" }}>
                <div>
                  <label htmlFor="cash-received" style={{ display: "block", fontSize: "12px", fontWeight: "600", color: "#475569", marginBottom: "6px" }}>
                    Cash Received Tender
                  </label>
                  <div style={{ position: "relative" }}>
                    <span style={{ position: "absolute", left: "14px", top: "50%", transform: "translateY(-50%)", fontSize: "16px", fontWeight: "600", color: "#94a3b8" }}>₹</span>
                    <input
                      id="cash-received"
                      type="number"
                      value={receivedAmount}
                      onChange={(e) => setReceivedAmount(e.target.value)}
                      placeholder="0.00"
                      style={{
                        width: "100%",
                        padding: "12px 14px 12px 30px",
                        fontSize: "15px",
                        fontWeight: "600",
                        border: "1.5px solid #e2e8f0",
                        borderRadius: "10px",
                        boxSizing: "border-box",
                        outline: "none",
                        color: "#0f172a",
                        transition: "border-color 0.2s"
                      }}
                      onFocus={(e) => e.target.style.borderColor = "#2563eb"}
                      onBlur={(e) => e.target.style.borderColor = "#e2e8f0"}
                    />
                  </div>
                </div>

                <div style={{ background: "#f8fafc", border: "1.5px solid #e2e8f0", borderRadius: "12px", padding: "14px 16px" }}>
                  <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                    <span style={{ fontSize: "13px", color: "#475569", fontWeight: "500" }}>Change Balance Owed</span>
                    <span style={{ fontSize: "18px", fontWeight: "700", color: changeAmount >= 0 ? "#16a34a" : "#ef4444" }}>
                      ₹{changeAmount >= 0 ? changeAmount.toFixed(2) : "0.00"}
                    </span>
                  </div>
                </div>
              </div>
            )}

            {paymentMethod === "CARD" && <CardUI />}
            {paymentMethod === "UPI" && <UpiQR />}
            {!paymentMethod && (
              <div style={{ textAlign: "center", padding: "40px 0", color: "#94a3b8" }}>
                <div style={{ display: "inline-flex", padding: "12px", background: "#f8fafc", borderRadius: "50%", marginBottom: "12px" }}>
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
                    <rect x="2" y="5" width="20" height="14" rx="2"/>
                    <line x1="2" y1="10" x2="22" y2="10"/>
                  </svg>
                </div>
                <p style={{ margin: 0, fontSize: "13px", fontWeight: "500" }}>Awaiting allocation parameters</p>
              </div>
            )}
          </div>

          <div style={{ padding: "16px 24px", borderTop: "1px solid #f1f5f9", display: "flex", gap: "12px", backgroundColor: "#fafafa" }}>
            <button
              onClick={() => setStep("cart")}
              style={{
                flex: "0.5",
                padding: "12px",
                fontSize: "13px",
                fontWeight: "600",
                color: "#475569",
                background: "#ffffff",
                border: "1.5px solid #e2e8f0",
                borderRadius: "10px",
                cursor: "pointer",
                transition: "all 0.15s"
              }}
            >
              Back
            </button>
            <button
              onClick={handleCheckoutSubmit}
              disabled={!paymentMethod}
              style={{
                flex: "1.5",
                padding: "12px",
                fontSize: "13px",
                fontWeight: "600",
                color: "#ffffff",
                background: paymentMethod ? "#2563eb" : "#cbd5e1",
                border: "none",
                borderRadius: "10px",
                cursor: paymentMethod ? "pointer" : "not-allowed",
                transition: "background-color 0.15s"
              }}
            >
              {paymentMethod ? `Authorize ${paymentMethod} Settlement` : "Select Options Matrix"}
            </button>
          </div>
        </div>

        <div style={{ flex: "0.9", background: "#0f172a", borderRadius: "20px", padding: "24px", color: "#ffffff", boxShadow: "0 4px 20px rgba(0,0,0,0.08)" }}>
          <p style={{ margin: "0 0 16px", fontSize: "11px", fontWeight: "700", color: "#64748b", letterSpacing: "1px", textTransform: "uppercase" }}>Order Breakdown</p>
          
          <div style={{ borderBottom: "1px solid #1e293b", paddingBottom: "16px", marginBottom: "16px" }}>
            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "baseline" }}>
              <span style={{ fontSize: "13px", color: "#94a3b8" }}>Total Ledger Balance</span>
              <span style={{ fontSize: "26px", fontWeight: "800", color: "#38bdf8" }}>₹{cart.totalPrice}</span>
            </div>
          </div>

          <div style={{ display: "flex", flexDirection: "column", gap: "10px" }}>
            <div style={{ display: "flex", justifyContent: "space-between", fontSize: "12px" }}>
              <span style={{ color: "#64748b" }}>Status</span>
              <span style={{ color: "#f1f5f9", fontWeight: "600" }}>Awaiting Funds</span>
            </div>
            {paymentMethod && (
              <div style={{ display: "flex", justifyContent: "space-between", fontSize: "12px" }}>
                <span style={{ color: "#64748b" }}>Selected Pipeline</span>
                <span style={{ color: "#38bdf8", fontWeight: "600" }}>{paymentMethod}</span>
              </div>
            )}
          </div>
        </div>

      </div>
    </div>
  );
}