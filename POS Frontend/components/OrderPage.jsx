"use client";

import PropTypes from "prop-types";
import Receipt from "@/components/Receipt";

const fmt = (n) =>
  `₹${Number(n || 0).toLocaleString("en-IN", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  })}`;

const fmtDate = (d) => {
  if (!d) return "—";
  return new Date(d).toLocaleString("en-IN", {
    day: "2-digit", month: "short", year: "numeric",
    hour: "2-digit", minute: "2-digit",
  });
};

function orderToReceiptData(order) {
  if (!order) return null;
  return {
    orderId: order.orderId,
    customer: order.customer ?? { customerName: order.customerIdentifier },
    items: (order.orderEntries ?? []).map((e) => ({
      productName: e.product?.productName ?? e.productIdentifier ?? "Unknown Product",
      quantity: e.quantity ?? 1,
      price: Number(e.sellingPrice ?? 0),
      totalPrice: Number(e.totalPrice ?? 0),
    })),
    totalPrice: Number(order.grandTotal ?? order.totalPrice ?? 0),
    totalDiscount: Number(order.discount ?? 0),
    timestamp: fmtDate(order.orderDate),
    paymentMode: order.paymentMode ?? "—",
  };
}

const CSS = `
@import url('https://fonts.googleapis.com/css2?family=Barlow:wght@400;500;600;700;800;900&display=swap');
.ord-wrap*,.ord-wrap*::before,.ord-wrap*::after{box-sizing:border-box;margin:0;padding:0}
.ord-wrap{font-family:'Barlow',system-ui,sans-serif;background:#f0f2f5;min-height:calc(100vh - 52px);padding:24px}
.ord-header{display:flex;align-items:center;justify-content:space-between;gap:12px;margin-bottom:20px;flex-wrap:wrap}
.ord-title{font-size:22px;font-weight:900;color:#111;letter-spacing:-.02em}
.ord-subtitle{font-size:13px;color:#aaa;font-weight:500;margin-top:2px}
.ord-header-actions{display:flex;gap:8px;align-items:center;flex-wrap:wrap}
.ord-card{background:#fff;border:1px solid #e0e0e0;border-radius:10px;overflow:hidden;box-shadow:0 1px 4px rgba(0,0,0,.04)}
.ord-toolbar{padding:12px 16px;border-bottom:1px solid #f2f2f2;display:flex;gap:10px;align-items:center;flex-wrap:wrap}
.ord-srch{flex:1;min-width:200px;padding:8px 12px;font-family:'Barlow',sans-serif;font-size:13px;font-weight:500;color:#111;background:#f5f5f5;border:1.5px solid #e8e8e8;border-radius:6px;outline:none;transition:all .15s}
.ord-srch:focus{border-color:#005dab;background:#fff;box-shadow:0 0 0 3px rgba(0,93,171,.08)}
.ord-count{font-size:12px;color:#bbb;font-weight:600;white-space:nowrap}
.tbl-scroll{overflow-x:auto}
.ord-tbl{width:100%;border-collapse:collapse;font-size:13px}
.ord-tbl thead tr{background:#005dab}
.ord-tbl th{padding:10px 14px;text-align:left;font-size:10px;font-weight:700;color:rgba(255,255,255,.85);text-transform:uppercase;letter-spacing:.08em;white-space:nowrap}
.ord-tbl td{padding:9px 14px;color:#333;border-bottom:1px solid #f0f0f0;vertical-align:middle;font-weight:500}
.ord-tbl tbody tr:last-child td{border-bottom:none}
.ord-tbl tbody tr:hover td{background:#f8f9fb}
.ord-id{font-family:monospace;font-size:11px;font-weight:700;color:#005dab;background:#e8f0fa;padding:2px 7px;border-radius:4px;white-space:nowrap}
.pay-chip{font-size:10px;font-weight:700;padding:2px 8px;border-radius:4px;text-transform:uppercase;white-space:nowrap}
.pay-cash{background:#dcfce7;color:#16a34a}
.pay-card{background:#e8f0fa;color:#005dab}
.pay-upi{background:#fef9c3;color:#b45309}
.pay-other{background:#f5f5f5;color:#666}
.act-row{display:flex;gap:5px;align-items:center}
.btn-view{padding:4px 10px;background:#005dab;border:none;border-radius:5px;color:#fff;font-family:'Barlow',sans-serif;font-size:11px;font-weight:700;text-transform:uppercase;cursor:pointer;transition:background .13s;white-space:nowrap}
.btn-view:hover{background:#004a8f}
.btn-del{padding:4px 8px;background:#fff;border:1.5px solid #e0e0e0;border-radius:5px;color:#ccc;font-family:'Barlow',sans-serif;font-size:11px;font-weight:700;cursor:pointer;transition:all .13s;white-space:nowrap}
.btn-del:hover{border-color:#e31837;color:#e31837;background:#fff0f3}
.btn-primary{padding:8px 16px;background:#005dab;border:none;border-radius:6px;color:#fff;font-family:'Barlow',sans-serif;font-size:12px;font-weight:700;text-transform:uppercase;cursor:pointer;transition:background .15s;display:inline-flex;align-items:center;gap:5px;white-space:nowrap}
.btn-primary:hover:not(:disabled){background:#004a8f}
.btn-primary:disabled{opacity:.4;cursor:not-allowed}
.btn-secondary{padding:8px 16px;background:#fff;border:1.5px solid #005dab;border-radius:6px;color:#005dab;font-family:'Barlow',sans-serif;font-size:12px;font-weight:700;text-transform:uppercase;cursor:pointer;transition:all .15s;display:inline-flex;align-items:center;gap:5px;white-space:nowrap}
.btn-secondary:hover{background:#e8f0fa}
.btn-ghost{padding:7px 14px;background:#fff;border:1.5px solid #ddd;border-radius:6px;color:#555;font-family:'Barlow',sans-serif;font-size:12px;font-weight:700;text-transform:uppercase;cursor:pointer;transition:background .13s}
.btn-ghost:hover:not(:disabled){background:#f0f2f5}
.btn-ghost:disabled{opacity:.4;cursor:not-allowed}
.btn-danger{padding:7px 15px;background:#e31837;border:none;border-radius:6px;color:#fff;font-family:'Barlow',sans-serif;font-size:12px;font-weight:800;text-transform:uppercase;cursor:pointer;transition:background .15s}
.btn-danger:hover:not(:disabled){background:#c0152a}
.btn-danger:disabled{opacity:.4;cursor:not-allowed}
.ord-empty{padding:48px;text-align:center;color:#ccc;font-size:14px;font-weight:500}
.ord-empty-icon{font-size:36px;display:block;margin-bottom:10px}
.ord-empty-action{margin-top:16px}
.pos-err{padding:10px 14px;background:#fff0f3;border:1px solid #fbbcca;border-radius:6px;font-size:13px;color:#c0152a;font-weight:600;margin-bottom:14px}
.pos-ok{padding:10px 14px;background:#f0fdf4;border:1px solid #bbf7d0;border-radius:6px;font-size:13px;color:#15803d;font-weight:600;margin-bottom:14px}
.busy-bar{height:3px;background:linear-gradient(90deg,#005dab,#e31837,#005dab);background-size:200% 100%;animation:bb 1.2s linear infinite;border-radius:2px;margin-bottom:10px}
@keyframes bb{from{background-position:100% 0}to{background-position:-100% 0}}
.spin{display:inline-block;width:10px;height:10px;border:2px solid rgba(255,255,255,.3);border-top-color:#fff;border-radius:50%;animation:sp .6s linear infinite;vertical-align:middle}
.spin-dark{display:inline-block;width:10px;height:10px;border:2px solid rgba(0,0,0,.15);border-top-color:#005dab;border-radius:50%;animation:sp .6s linear infinite;vertical-align:middle}
@keyframes sp{to{transform:rotate(360deg)}}
.m-ov{position:fixed;inset:0;width:100vw;height:100vh;margin:0;padding:20px;display:flex;align-items:center;justify-content:center;background:rgba(0,0,0,.45);border:none;z-index:200;overflow:auto;backdrop-filter:blur(2px)}
.m-box{width:100%;max-width:480px;background:#fff;border-radius:10px;box-shadow:0 8px 40px rgba(0,0,0,.16);overflow:hidden;animation:mIn .17s ease}
@keyframes mIn{from{opacity:0;transform:scale(.97) translateY(6px)}to{opacity:1;transform:none}}
.m-head{display:flex;align-items:center;justify-content:space-between;padding:12px 17px;background:#005dab}
.m-title{font-size:13px;font-weight:800;color:#fff;text-transform:uppercase;letter-spacing:.06em}
.m-x{width:26px;height:26px;border-radius:5px;border:none;background:rgba(255,255,255,.2);color:#fff;font-size:11px;cursor:pointer;display:flex;align-items:center;justify-content:center;font-weight:700;transition:background .13s}
.m-x:hover{background:rgba(255,255,255,.35)}
.m-body{padding:20px 18px;display:flex;flex-direction:column;gap:13px}
.m-field{display:flex;flex-direction:column;gap:4px}
.m-label{font-size:10px;font-weight:700;color:#444;text-transform:uppercase;letter-spacing:.09em}
.m-req{color:#e31837;margin-left:2px}
.m-inp,.m-sel{width:100%;padding:8px 11px;font-family:'Barlow',sans-serif;font-size:13px;font-weight:500;color:#111;background:#fff;border:1.5px solid #ddd;border-radius:6px;outline:none;transition:border-color .15s,box-shadow .15s}
.m-inp:focus,.m-sel:focus{border-color:#005dab;box-shadow:0 0 0 3px rgba(0,93,171,.1)}
.m-err{font-size:12px;color:#e31837;font-weight:600}
.m-footer{display:flex;gap:8px;justify-content:flex-end;padding:12px 18px;border-top:1px solid #f2f2f2}
.m-text{font-size:14px;color:#555;line-height:1.7;margin:0;font-weight:500}
.m-text strong{color:#111;font-weight:800}
.ord-pagination{padding:12px 16px;border-top:1px solid #f2f2f2;display:flex;align-items:center;justify-content:space-between;gap:12px;flex-wrap:wrap}
.ord-page-info{font-size:12px;color:#888;font-weight:500;white-space:nowrap}
.ord-page-btns{display:flex;gap:4px;align-items:center}
.ord-page-btn{padding:6px 10px;background:#fff;border:1.5px solid #ddd;border-radius:4px;font-family:'Barlow',sans-serif;font-size:11px;font-weight:600;color:#111;cursor:pointer;transition:all .12s;white-space:nowrap}
.ord-page-btn:hover:not(:disabled){border-color:#005dab;color:#005dab;background:#e8f0fa}
.ord-page-btn:disabled{opacity:.4;cursor:not-allowed}
.ord-page-btn.active{background:#005dab;border-color:#005dab;color:#fff}
`;

function PayChip({ mode }) {
  const m = (mode || "").toUpperCase();
  let cls;
  if (m === "CASH") cls = "pay-cash";
  else if (m === "CARD") cls = "pay-card";
  else if (m === "UPI") cls = "pay-upi";
  else cls = "pay-other";
  return <span className={`pay-chip ${cls}`}>{mode || "—"}</span>;
}
PayChip.propTypes = { mode: PropTypes.string };
PayChip.defaultProps = { mode: "" };

function Modal({ open, onClose, title, children }) {
  if (!open) return null;
  return (
    <dialog className="m-ov" open aria-label={title}>
      <div className="m-box">
        <div className="m-head">
          <span className="m-title">{title}</span>
          <button className="m-x" onClick={onClose}>✕</button>
        </div>
        {children}
      </div>
    </dialog>
  );
}
Modal.propTypes = {
  open: PropTypes.bool.isRequired,
  onClose: PropTypes.func.isRequired,
  title: PropTypes.string.isRequired,
  children: PropTypes.node.isRequired,
};

export default function OrderPage({
  orders, allOrders, loading, busy,
  pageErr, pageOk,
  search, setSearch,
  viewOrder, setViewOrder,
  receiptRef, onPrintReceipt,
  carts,
  showPlaceOrder, setShowPlaceOrder,
  poCartId, setPoCartId,
  poPayment, setPoPayment,
  poErr, poSaving, onPlaceOrder,
  confirmDelete, setConfirmDelete, onDelete,
  onGoToCart,
  currentPage = 0,
  totalPages = 1,
  onPageChange = () => {},
}) {
  return (
    <>
      <style>{CSS}</style>
      <div className="ord-wrap">

        <div className="ord-header">
          <div>
            <div className="ord-title">Orders</div>
            <div className="ord-subtitle">{allOrders.length} order{allOrders.length === 1 ? "" : "s"} total</div>
          </div>
          <div className="ord-header-actions">
            <button className="btn-secondary" onClick={onGoToCart}>
              🛒 Go to Cart
            </button>
            <button className="btn-primary" onClick={() => setShowPlaceOrder(true)}>
              ＋ Place Order
            </button>
          </div>
        </div>

        {busy && <div className="busy-bar" />}
        {pageErr && <div className="pos-err">⚠ {pageErr}</div>}
        {pageOk && <div className="pos-ok">✓ {pageOk}</div>}

        <div className="ord-card">
          <div className="ord-toolbar">
            <input
              className="ord-srch"
              placeholder="Search by order ID, customer, payment mode…"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />
            {search && (
              <button className="btn-ghost" style={{ padding: "6px 12px", fontSize: 11 }} onClick={() => setSearch("")}>
                Clear
              </button>
            )}
            <span className="ord-count">
              {orders.length === allOrders.length
                ? `${orders.length}`
                : `${orders.length} of ${allOrders.length}`}{" "}
              result{orders.length === 1 ? "" : "s"}
            </span>
          </div>

          {loading ? (
            <div className="ord-empty"><span className="spin-dark" /> Loading orders…</div>
          ) : (() => {
            if (orders.length === 0) {
              return (
                <div className="ord-empty">
                  <span className="ord-empty-icon">📋</span>
                  {search ? `No orders match "${search}"` : "No orders yet."}
                </div>
              );
            }
            return (
              <div className="tbl-scroll">
              <table className="ord-tbl">
                <thead>
                  <tr>
                    <th>Order ID</th>
                    <th>Customer</th>
                    <th>Date</th>
                    <th>Payment</th>
                    <th style={{ textAlign: "right" }}>Total</th>
                    <th style={{ textAlign: "right" }}>Discount</th>
                    <th style={{ textAlign: "right" }}>Grand Total</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {orders.map((order) => (
                    <tr key={order.identifier}>
                      <td><span className="ord-id">{order.orderId}</span></td>
                      <td>
                        <div style={{ fontWeight: 700, color: "#111" }}>
                          {order.customer?.customerName || order.customerIdentifier || "—"}
                        </div>
                        {order.customer?.customerName && (
                          <div style={{ fontSize: 11, color: "#bbb", fontFamily: "monospace" }}>
                            {order.customerIdentifier}
                          </div>
                        )}
                      </td>
                      <td style={{ fontSize: 12, color: "#555", whiteSpace: "nowrap" }}>{fmtDate(order.orderDate)}</td>
                      <td><PayChip mode={order.paymentMode} /></td>
                      <td style={{ textAlign: "right", fontWeight: 600 }}>{fmt(order.totalPrice)}</td>
                      <td style={{ textAlign: "right", color: "#16a34a", fontWeight: 600 }}>
                        {(order.discount ?? 0) > 0 ? `− ${fmt(order.discount)}` : <span style={{ color: "#eee" }}>—</span>}
                      </td>
                      <td style={{ textAlign: "right", fontWeight: 800, color: "#111" }}>{fmt(order.grandTotal)}</td>
                      <td>
                        <div className="act-row">
                          <button className="btn-view" onClick={() => setViewOrder(order)}>View</button>
                          <button className="btn-del" onClick={() => setConfirmDelete(order)}>✕</button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>

              {totalPages > 1 && (
                <div className="ord-pagination">
                  <div className="ord-page-info">
                    Page {currentPage + 1} of {totalPages}
                  </div>
                  <div className="ord-page-btns">
                    <button
                      className="ord-page-btn"
                      onClick={() => onPageChange(currentPage - 1)}
                      disabled={currentPage === 0}
                    >
                      ← Previous
                    </button>
                    {Array.from({ length: Math.min(5, totalPages) }).map((_, i) => {
                      const pageNum = i + 1;
                      return (
                        <button
                          key={`page-btn-${pageNum}`}
                          className={`ord-page-btn${currentPage === i ? " active" : ""}`}
                          onClick={() => onPageChange(i)}
                        >
                          {pageNum}
                        </button>
                      );
                    })}
                    {totalPages > 5 && currentPage < totalPages - 2 && <span>…</span>}
                    <button
                      className="ord-page-btn"
                      onClick={() => onPageChange(currentPage + 1)}
                      disabled={currentPage >= totalPages - 1}
                    >
                      Next →
                    </button>
                  </div>
                </div>
              )}
              </div>
            );
            })()}
        </div>
      </div>

      <Modal
        open={showPlaceOrder}
        onClose={() => { setShowPlaceOrder(false); setPoCartId(""); setPoPayment("CASH"); }}
        title="Place New Order"
      >
        <div className="m-body">
          {poErr && <div className="m-err">⚠ {poErr}</div>}
          <div className="m-field">
            <label className="m-label" htmlFor="cart-select">Cart <span className="m-req">*</span></label>
            <select id="cart-select" className="m-sel" value={poCartId} onChange={(e) => setPoCartId(e.target.value)}>
              <option value="">— Select a cart —</option>
              {carts.map((c) => (
                <option key={c.identifier} value={c.identifier}>
                  {c.identifier}{c.username && c.username !== c.identifier ? ` · ${c.username}` : ""}
                  {c.totalPrice ? ` · ${fmt(c.totalPrice)}` : ""}
                </option>
              ))}
            </select>
          </div>
          <div className="m-field">
            <label className="m-label" htmlFor="payment-select">Payment Mode</label>
            <select id="payment-select" className="m-sel" value={poPayment} onChange={(e) => setPoPayment(e.target.value)}>
              {["CASH", "CARD", "UPI", "BANK_TRANSFER", "OTHER"].map((m) => (
                <option key={m} value={m}>{m}</option>
              ))}
            </select>
          </div>
        </div>
        <div className="m-footer">
          <button className="btn-ghost" onClick={() => { setShowPlaceOrder(false); setPoCartId(""); setPoPayment("CASH"); }}>
            Cancel
          </button>
          <button className="btn-primary" onClick={onPlaceOrder} disabled={poSaving}>
            {poSaving ? <><span className="spin" /> Placing…</> : "Place Order"}
          </button>
        </div>
      </Modal>

      {viewOrder && (
        <Receipt
          data={orderToReceiptData(viewOrder)}
          receiptRef={receiptRef}
          onPrint={onPrintReceipt}
          onClose={() => setViewOrder(null)}
        />
      )}

      {/* Confirm Delete Modal */}
      <Modal open={!!confirmDelete} onClose={() => setConfirmDelete(null)} title="Cancel Order">
        {confirmDelete && (
          <>
            <div className="m-body">
              <p className="m-text">
                Cancel order <strong>{confirmDelete.orderId}</strong> for{" "}
                <strong>{confirmDelete.customer?.customerName || confirmDelete.customerIdentifier}</strong>?
                This cannot be undone.
              </p>
            </div>
            <div className="m-footer">
              <button className="btn-ghost" onClick={() => setConfirmDelete(null)}>Keep Order</button>
              <button className="btn-danger" onClick={onDelete}>Cancel Order</button>
            </div>
          </>
        )}
      </Modal>
    </>
  );
}

OrderPage.propTypes = {
  orders: PropTypes.array.isRequired,
  allOrders: PropTypes.array.isRequired,
  loading: PropTypes.bool.isRequired,
  busy: PropTypes.bool.isRequired,
  pageErr: PropTypes.string.isRequired,
  pageOk: PropTypes.string.isRequired,
  search: PropTypes.string.isRequired,
  setSearch: PropTypes.func.isRequired,
  viewOrder: PropTypes.object,
  setViewOrder: PropTypes.func.isRequired,
  receiptRef: PropTypes.object.isRequired,
  onPrintReceipt: PropTypes.func.isRequired,
  carts: PropTypes.array.isRequired,
  showPlaceOrder: PropTypes.bool.isRequired,
  setShowPlaceOrder: PropTypes.func.isRequired,
  poCartId: PropTypes.string.isRequired,
  setPoCartId: PropTypes.func.isRequired,
  poPayment: PropTypes.string.isRequired,
  setPoPayment: PropTypes.func.isRequired,
  poErr: PropTypes.string.isRequired,
  poSaving: PropTypes.bool.isRequired,
  onPlaceOrder: PropTypes.func.isRequired,
  confirmDelete: PropTypes.object,
  setConfirmDelete: PropTypes.func.isRequired,
  onDelete: PropTypes.func.isRequired,
  onGoToCart: PropTypes.func.isRequired,
  currentPage: PropTypes.number,
  totalPages: PropTypes.number,
  onPageChange: PropTypes.func,
};
OrderPage.defaultProps = {
  viewOrder: null,
  confirmDelete: null,
};