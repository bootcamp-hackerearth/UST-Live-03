"use client";

import { useEffect } from "react";
import PropTypes from "prop-types";
import { htmlEscape } from "@/lib/security";
import Receipt from "@/components/Receipt";

const fmt = (n) =>
  `₹${Number(n || 0).toLocaleString("en-IN", { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;

function resolveProductName(entry, productMap = {}) {
  return (
    entry?.product?.productName ||
    entry?.productName ||
    entry?.productDto?.productName ||
    entry?.name ||
    productMap[entry?.productIdentifier]?.productName ||
    productMap[entry?.productIdentifier]?.name ||
    entry?.productIdentifier ||
    "Unknown Product"
  );
}

const CSS = `
@import url('https://fonts.googleapis.com/css2?family=Barlow:wght@400;500;600;700;800;900&display=swap');
.pos-wrap*,.pos-wrap*::before,.pos-wrap*::after{box-sizing:border-box;margin:0;padding:0}
.pos-wrap{font-family:'Barlow',system-ui,sans-serif;background:#f0f2f5;min-height:calc(100vh - 52px);display:grid;grid-template-columns:1fr 320px}
@media(max-width:860px){.pos-wrap{grid-template-columns:1fr}}
.pos-main{padding:20px;display:flex;flex-direction:column;gap:14px;min-width:0;overflow-y:auto}
.pos-sidebar{background:#fff;border-left:1px solid #e8e8e8;display:flex;flex-direction:column;position:sticky;top:0;height:calc(100vh - 52px);overflow:hidden}
.pos-card{background:#fff;border:1px solid #e0e0e0;border-radius:10px;overflow:visible;box-shadow:0 1px 4px rgba(0,0,0,.04)}
.pos-card-head{padding:10px 16px;border-bottom:1px solid #f2f2f2;display:flex;align-items:center;justify-content:space-between;gap:8px;border-radius:10px 10px 0 0}
.pos-card-title{font-size:10px;font-weight:800;color:#888;text-transform:uppercase;letter-spacing:.1em;display:flex;align-items:center;gap:7px}
.pos-badge{background:#005dab;color:#fff;font-size:10px;font-weight:700;padding:2px 7px;border-radius:4px}
.pos-err{padding:10px 14px;background:#fff0f3;border:1px solid #fbbcca;border-radius:6px;font-size:13px;color:#c0152a;font-weight:600}
.pos-ok{padding:10px 14px;background:#f0fdf4;border:1px solid #bbf7d0;border-radius:6px;font-size:13px;color:#15803d;font-weight:600}
.lookup-body{padding:13px 16px}
.dd-outer{position:relative}
.phone-inp{width:100%;padding:9px 32px 9px 12px;font-family:'Barlow',sans-serif;font-size:14px;font-weight:600;color:#111;background:#fff;border:1.5px solid #ddd;border-radius:6px;outline:none;transition:border-color .15s,box-shadow .15s}
.phone-inp:focus{border-color:#005dab;box-shadow:0 0 0 3px rgba(0,93,171,.1)}
.phone-clear{position:absolute;right:9px;top:50%;transform:translateY(-50%);background:none;border:none;cursor:pointer;color:#bbb;font-size:11px;display:flex;align-items:center;padding:3px;border-radius:3px;transition:color .12s}
.phone-clear:hover{color:#555}
.lookup-hint{font-size:11px;color:#bbb;font-weight:500;margin-top:6px}
.pos-dd{position:absolute;top:calc(100% + 4px);left:0;right:0;background:#fff;border:1.5px solid #005dab;border-radius:8px;box-shadow:0 12px 48px rgba(0,0,0,.22);z-index:99999;animation:ddIn .12s ease;overflow:hidden;max-height:340px;overflow-y:auto}
@keyframes ddIn{from{opacity:0;transform:translateY(-4px)}to{opacity:1;transform:none}}
.dd-row{padding:10px 13px;cursor:pointer;display:flex;align-items:center;gap:11px;border-bottom:1px solid #f0f0f0;transition:background .1s;min-height:52px;background:none;border-left:none;border-right:none;border-top:none;width:100%;text-align:left;font-family:'Barlow',sans-serif}
.dd-row:last-child{border-bottom:none}
.dd-row:hover,.dd-row.focused{background:#f0f5ff}
.dd-av{width:34px;height:34px;border-radius:50%;background:#005dab;color:#fff;display:flex;align-items:center;justify-content:center;font-size:13px;font-weight:800;flex-shrink:0}
.dd-name{font-size:13px;font-weight:700;color:#111;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
.dd-sub{font-size:11px;color:#888;font-weight:500;margin-top:2px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
.dd-tag{font-size:10px;font-weight:700;background:#e8f0fa;color:#005dab;padding:2px 6px;border-radius:4px;text-transform:uppercase;white-space:nowrap;flex-shrink:0}
.dd-empty{padding:16px;text-align:center;font-size:13px;color:#bbb;font-weight:500}
.dd-new{padding:10px 13px;display:flex;align-items:center;gap:7px;cursor:pointer;background:#fafafa;border-top:2px solid #e8f0fa;border-left:none;border-right:none;border-bottom:none;font-size:13px;font-weight:700;color:#111;transition:background .1s;min-height:44px;width:100%;text-align:left;font-family:'Barlow',sans-serif}
.dd-new:hover,.dd-new.focused{background:#e8f0fa;color:#005dab}
.cust-panel{padding:12px 16px;background:#f8f9fb;display:flex;align-items:flex-start;gap:12px}
.cust-av{width:42px;height:42px;border-radius:50%;background:#005dab;color:#fff;display:flex;align-items:center;justify-content:center;font-size:16px;font-weight:800;flex-shrink:0}
.cust-name{font-size:14px;font-weight:800;color:#111;margin-bottom:2px}
.cust-meta{font-size:12px;color:#666;font-weight:500}
.cust-tag{display:inline-block;font-size:10px;font-weight:700;background:#e8f0fa;color:#005dab;padding:1px 6px;border-radius:4px;text-transform:uppercase;margin-top:3px}
.cust-change{font-size:11px;font-weight:700;color:#005dab;background:none;border:none;cursor:pointer;padding:0;font-family:'Barlow',sans-serif;text-decoration:underline;text-transform:uppercase;letter-spacing:.04em;margin-top:4px;display:inline-block}
.qf-wrap{padding:14px 16px;display:flex;flex-direction:column;gap:11px}
.qf-grid{display:grid;grid-template-columns:1fr 1fr;gap:9px}
@media(max-width:560px){.qf-grid{grid-template-columns:1fr}}
.qf-field{display:flex;flex-direction:column;gap:3px}
.qf-label{font-size:10px;font-weight:700;color:#444;text-transform:uppercase;letter-spacing:.09em}
.qf-req{color:#e31837;margin-left:2px}
.qf-inp{width:100%;padding:8px 10px;font-family:'Barlow',sans-serif;font-size:13px;font-weight:500;color:#111;background:#fff;border:1.5px solid #ddd;border-radius:6px;outline:none;transition:border-color .15s,box-shadow .15s}
.qf-inp:focus{border-color:#005dab;box-shadow:0 0 0 3px rgba(0,93,171,.1)}
.qf-hint{font-size:11px;color:#aaa;font-weight:500}
.qf-row{display:flex;gap:8px}
.srch-bar{padding:9px 13px;border-bottom:1px solid #f2f2f2;display:flex;gap:7px;align-items:center}
.srch-inp{flex:1;padding:7px 11px;font-family:'Barlow',sans-serif;font-size:13px;font-weight:500;color:#111;background:#f5f5f5;border:1.5px solid #e8e8e8;border-radius:6px;outline:none;transition:all .15s}
.srch-inp:focus{border-color:#005dab;background:#fff;box-shadow:0 0 0 3px rgba(0,93,171,.08)}
.prod-list{max-height:300px;overflow-y:auto}
.prod-row{display:flex;align-items:center;gap:9px;padding:8px 14px;border-bottom:1px solid #f5f5f5;transition:background .1s}
.prod-row:last-child{border-bottom:none}
.prod-row:hover{background:#fafafa}
.prod-row.in-cart{background:#f0fdf4}
.prod-info{flex:1;min-width:0}
.prod-name{font-size:13px;font-weight:700;color:#111;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
.prod-sku{font-size:10px;color:#bbb;font-family:monospace;display:block;margin-top:1px}
.incart-chip{font-size:10px;font-weight:700;background:#dcfce7;color:#16a34a;padding:2px 6px;border-radius:4px;text-transform:uppercase;white-space:nowrap;flex-shrink:0}
.qty-sm{width:46px;padding:5px;font-family:'Barlow',sans-serif;font-size:13px;font-weight:700;color:#111;background:#fff;border:1.5px solid #ddd;border-radius:5px;outline:none;text-align:center;-moz-appearance:textfield}
.qty-sm::-webkit-outer-spin-button,.qty-sm::-webkit-inner-spin-button{-webkit-appearance:none}
.qty-sm:focus{border-color:#005dab}
.qty-sm:disabled{background:#f5f5f5;color:#bbb}
.tbl-scroll{overflow-x:auto}
.pos-tbl{width:100%;border-collapse:collapse;font-size:13px}
.pos-tbl thead tr{background:#005dab}
.pos-tbl th{padding:9px 13px;text-align:left;font-size:10px;font-weight:700;color:rgba(255,255,255,.85);text-transform:uppercase;letter-spacing:.08em;white-space:nowrap}
.pos-tbl td{padding:8px 13px;color:#333;border-bottom:1px solid #f0f0f0;vertical-align:middle;font-weight:500}
.pos-tbl tbody tr:last-child td{border-bottom:none}
.pos-tbl tbody tr:hover td{background:#f8f9fb}
.sku-chip{font-size:10px;font-weight:700;color:#bbb;background:#f5f5f5;padding:2px 4px;border-radius:3px;font-family:monospace;display:inline-block;margin-top:2px}
.qty-wrap{display:flex;align-items:center;gap:3px}
.qty-btn{width:24px;height:24px;border-radius:5px;border:1.5px solid #e0e0e0;background:#fff;color:#555;font-size:13px;font-weight:700;cursor:pointer;display:flex;align-items:center;justify-content:center;transition:all .12s;line-height:1;padding:0;font-family:'Barlow',sans-serif}
.qty-btn:hover:not(:disabled){background:#005dab;border-color:#005dab;color:#fff}
.qty-btn:disabled{opacity:.3;cursor:not-allowed}
.qty-val{min-width:28px;height:24px;border-radius:5px;background:#f5f5f5;border:1.5px solid #e8e8e8;font-size:12px;font-weight:700;color:#111;display:flex;align-items:center;justify-content:center}
.btn-rm{width:22px;height:22px;border-radius:4px;border:none;background:none;color:#ddd;font-size:12px;cursor:pointer;display:flex;align-items:center;justify-content:center;transition:color .12s;padding:0}
.btn-rm:hover:not(:disabled){color:#e31837}
.btn-rm:disabled{opacity:.3;cursor:not-allowed}
.btn-primary{padding:7px 15px;background:#005dab;border:none;border-radius:6px;color:#fff;font-family:'Barlow',sans-serif;font-size:12px;font-weight:700;text-transform:uppercase;cursor:pointer;transition:background .15s;display:inline-flex;align-items:center;gap:5px;white-space:nowrap}
.btn-primary:hover:not(:disabled){background:#004a8f}
.btn-primary:disabled{opacity:.4;cursor:not-allowed}
.btn-green{padding:5px 12px;background:#16a34a;border:none;border-radius:6px;color:#fff;font-family:'Barlow',sans-serif;font-size:11px;font-weight:800;text-transform:uppercase;cursor:pointer;transition:background .13s}
.btn-green:hover{background:#15803d}
.btn-ghost{padding:7px 14px;background:#fff;border:1.5px solid #ddd;border-radius:6px;color:#555;font-family:'Barlow',sans-serif;font-size:12px;font-weight:700;text-transform:uppercase;cursor:pointer;transition:background .13s}
.btn-ghost:hover:not(:disabled){background:#f0f2f5}
.btn-ghost:disabled{opacity:.4;cursor:not-allowed}
.btn-danger{padding:7px 15px;background:#e31837;border:none;border-radius:6px;color:#fff;font-family:'Barlow',sans-serif;font-size:12px;font-weight:800;text-transform:uppercase;cursor:pointer;transition:background .15s}
.btn-danger:hover:not(:disabled){background:#c0152a}
.btn-danger:disabled{opacity:.4;cursor:not-allowed}
.btn-add-prod{padding:5px 11px;background:#005dab;border:none;border-radius:5px;color:#fff;font-family:'Barlow',sans-serif;font-size:11px;font-weight:800;text-transform:uppercase;cursor:pointer;transition:background .13s;white-space:nowrap}
.btn-add-prod:hover:not(:disabled){background:#004a8f}
.btn-add-prod:disabled{opacity:.35;cursor:not-allowed}
.btn-sm-out{padding:4px 9px;background:#fff;border:1.5px solid #ddd;border-radius:5px;color:#555;font-family:'Barlow',sans-serif;font-size:11px;font-weight:700;text-transform:uppercase;cursor:pointer;transition:all .13s}
.btn-sm-out:hover{border-color:#005dab;color:#005dab}
.sb-head{background:#005dab;padding:13px 16px;display:flex;align-items:center;justify-content:space-between;flex-shrink:0}
.sb-title{font-size:12px;font-weight:800;color:#fff;text-transform:uppercase;letter-spacing:.07em}
.sb-count{background:rgba(255,255,255,.2);color:#fff;font-size:11px;font-weight:700;padding:2px 9px;border-radius:20px}
.sb-empty{flex:1;display:flex;align-items:center;justify-content:center}
.sb-empty-inner{text-align:center;color:#ccc;font-size:13px;font-weight:500}
.sb-empty-icon{font-size:34px;display:block;margin-bottom:9px}
.sb-list{flex:1;overflow-y:auto}
.sb-row{display:flex;gap:9px;padding:8px 14px;border-bottom:1px solid #f5f5f5;align-items:flex-start}
.sb-row:last-child{border-bottom:none}
.sb-item-name{font-size:12px;font-weight:700;color:#111;line-height:1.3}
.sb-item-sub{font-size:11px;color:#aaa;font-weight:500;margin-top:1px}
.sb-item-total{font-size:13px;font-weight:800;color:#111;white-space:nowrap}
.sb-totals{padding:12px 16px;background:#fafafa;border-top:2px solid #f0f0f0;flex-shrink:0}
.t-row{display:flex;justify-content:space-between;font-size:12px;font-weight:600;color:#999;margin-bottom:5px}
.t-row.green{color:#16a34a}
.t-row.grand{font-size:16px;font-weight:900;color:#111;padding-top:9px;border-top:1.5px solid #e8e8e8;margin-top:5px;margin-bottom:0}
.sb-actions{padding:11px 14px;display:flex;flex-direction:column;gap:7px;border-top:1px solid #f0f0f0;flex-shrink:0}
.btn-checkout{width:100%;padding:11px;background:#e31837;border:none;border-radius:7px;color:#fff;font-family:'Barlow',sans-serif;font-size:13px;font-weight:800;text-transform:uppercase;cursor:pointer;transition:background .15s;display:flex;align-items:center;justify-content:center;gap:5px}
.btn-checkout:hover:not(:disabled){background:#c0152a}
.btn-checkout:disabled{opacity:.4;cursor:not-allowed}
.btn-clear-cart{width:100%;padding:8px;background:#fff;border:1.5px solid #e0e0e0;border-radius:6px;color:#999;font-family:'Barlow',sans-serif;font-size:11px;font-weight:700;text-transform:uppercase;cursor:pointer;transition:all .13s}
.btn-clear-cart:hover:not(:disabled){background:#fff0f3;border-color:#e31837;color:#e31837}
.btn-clear-cart:disabled{opacity:.4;cursor:not-allowed}
.busy-bar{height:3px;background:linear-gradient(90deg,#005dab,#e31837,#005dab);background-size:200% 100%;animation:bb 1.2s linear infinite;flex-shrink:0}
@keyframes bb{from{background-position:100% 0}to{background-position:-100% 0}}
.spin{display:inline-block;width:10px;height:10px;border:2px solid rgba(255,255,255,.3);border-top-color:#fff;border-radius:50%;animation:sp .6s linear infinite;vertical-align:middle}
.spin-dark{display:inline-block;width:10px;height:10px;border:2px solid rgba(0,0,0,.15);border-top-color:#005dab;border-radius:50%;animation:sp .6s linear infinite;vertical-align:middle}
@keyframes sp{to{transform:rotate(360deg)}}
.pos-empty{padding:32px;text-align:center;color:#ccc;font-size:13px;font-weight:500}
.m-ov{position:fixed;inset:0;width:100vw;height:100vh;margin:0;padding:20px;display:flex;align-items:center;justify-content:center;background:rgba(0,0,0,.45);border:none;z-index:200;overflow:auto;backdrop-filter:blur(2px)}
dialog.m-box{position:static;margin:0;padding:0;border:none}
.m-box{width:100%;max-width:400px;background:#fff;border-radius:10px;box-shadow:0 8px 40px rgba(0,0,0,.16);overflow:hidden;animation:mIn .17s ease}
@keyframes mIn{from{opacity:0;transform:scale(.97) translateY(6px)}to{opacity:1;transform:none}}
.m-head{display:flex;align-items:center;justify-content:space-between;padding:12px 17px;background:#005dab}
.m-title{font-size:13px;font-weight:800;color:#fff;text-transform:uppercase;letter-spacing:.06em}
.m-x{width:26px;height:26px;border-radius:5px;border:none;background:rgba(255,255,255,.2);color:#fff;font-size:11px;cursor:pointer;display:flex;align-items:center;justify-content:center;font-weight:700;transition:background .13s}
.m-x:hover{background:rgba(255,255,255,.35)}
.m-body{padding:20px 18px}
.m-text{font-size:14px;color:#555;line-height:1.7;margin:0 0 18px;font-weight:500}
.m-text strong{color:#111;font-weight:800}
.m-footer{display:flex;gap:8px;justify-content:flex-end}
`;

function ConfirmModal({
  open,
  onClose,
  onConfirm,
  title,
  message,
  confirmLabel,
  danger = false,
}) {
  useEffect(() => {
    if (!open) return undefined;
    const handler = (e) => { if (e.key === "Escape") onClose(); };
    globalThis.addEventListener("keydown", handler);
    return () => globalThis.removeEventListener("keydown", handler);
  }, [open, onClose]);

  if (!open) return null;
  return (
    <div className="m-ov">
      <dialog className="m-box" aria-label={title} open>
        <div className="m-head">
          <span className="m-title">{title}</span>
          <button className="m-x" type="button" onClick={onClose}>✕</button>
        </div>
        <div className="m-body">
          <p className="m-text">{message}</p>
          <div className="m-footer">
            <button className="btn-ghost" style={{ fontSize: 12, padding: "6px 14px" }} type="button" onClick={onClose}>Cancel</button>
            <button className={danger ? "btn-danger" : "btn-primary"} style={{ padding: "6px 16px" }} type="button" onClick={onConfirm}>
              {confirmLabel}
            </button>
          </div>
        </div>
      </dialog>
    </div>
  );
}
ConfirmModal.propTypes = {
  open: PropTypes.bool.isRequired,
  onClose: PropTypes.func.isRequired,
  onConfirm: PropTypes.func.isRequired,
  title: PropTypes.string.isRequired,
  message: PropTypes.node.isRequired,
  confirmLabel: PropTypes.string.isRequired,
  danger: PropTypes.bool,
};

function CustomerPanel({ customer, cart = null, onChangeCustomer }) {
  const initial = (customer.customerName?.[0] ?? customer.identifier?.[0] ?? "?").toUpperCase();
  return (
    <div className="cust-panel">
      <div className="cust-av">{initial}</div>
      <div style={{ flex: 1, minWidth: 0 }}>
        <div className="cust-name">{customer.customerName ? htmlEscape(customer.customerName) : customer.identifier}</div>
        <div className="cust-meta">📞 {customer.identifier}{customer.email ? ` · ${htmlEscape(customer.email)}` : ""}</div>
        {customer.billingAddress?.city && (
          <div className="cust-meta">
            📍 {htmlEscape(customer.billingAddress.city)}
            {customer.billingAddress.state ? `, ${htmlEscape(customer.billingAddress.state)}` : ""}
          </div>
        )}
        {customer.partyType && <span className="cust-tag">{customer.partyType}</span>}
        <button className="cust-change" type="button" onClick={onChangeCustomer}>Change customer</button>
      </div>
      {cart && (
        <div style={{ textAlign: "right", flexShrink: 0 }}>
          <div style={{ fontSize: 10, color: "#bbb", fontWeight: 700, textTransform: "uppercase", letterSpacing: ".06em" }}>Cart</div>
          <div style={{ fontSize: 11, fontFamily: "monospace", color: "#888", marginTop: 2 }}>{cart.identifier}</div>
        </div>
      )}
    </div>
  );
}
CustomerPanel.propTypes = {
  customer: PropTypes.object.isRequired,
  cart: PropTypes.object,
  onChangeCustomer: PropTypes.func.isRequired,
};

function QuickForm({ qfPhone, setQfPhone, qfName, setQfName, qfEmail, setQfEmail, qfParty, setQfParty, qfSaving, qfErr, onQFSave, onQFCancel }) {
  return (
    <div className="qf-wrap">
      {qfErr && <div style={{ fontSize: 12, color: "#e31837", fontWeight: 600 }}>{qfErr}</div>}
      <div className="qf-grid">
        <div className="qf-field">
          <label htmlFor="qf-phone" className="qf-label">Phone <span className="qf-req">*</span></label>
          <input
            id="qf-phone"
            className="qf-inp"
            value={qfPhone}
            maxLength={10}
            inputMode="numeric"
            onChange={(e) => setQfPhone(e.target.value.replaceAll(/\D/g, "").slice(0, 10))}
            placeholder="10-digit mobile number"
          />
        </div>
        <div className="qf-field">
          <label htmlFor="qf-name" className="qf-label">Name <span className="qf-req">*</span></label>
          <input id="qf-name" className="qf-inp" value={qfName} onChange={(e) => setQfName(e.target.value)} placeholder="Full name" />
        </div>
        <div className="qf-field">
          <label htmlFor="qf-email" className="qf-label">Email</label>
          <input id="qf-email" className="qf-inp" type="email" value={qfEmail} onChange={(e) => setQfEmail(e.target.value)} placeholder="Optional" />
        </div>
        <div className="qf-field">
          <label htmlFor="qf-party" className="qf-label">Party Type</label>
          <select id="qf-party" className="qf-inp" value={qfParty} onChange={(e) => setQfParty(e.target.value)}>
            {["Customer", "Dealer", "Retailer", "Distributor"].map((t) => <option key={t}>{t}</option>)}
          </select>
        </div>
      </div>
      <p className="qf-hint">Phone &amp; name required — other details can be updated later.</p>
      <div className="qf-row">
        <button className="btn-primary" style={{ flex: 1, justifyContent: "center", padding: "8px 12px" }} type="button" onClick={onQFSave} disabled={qfSaving}>
          {qfSaving ? <><span className="spin" /> Saving…</> : "Save & Start Billing"}
        </button>
        <button className="btn-ghost" type="button" onClick={onQFCancel}>Cancel</button>
      </div>
    </div>
  );
}
QuickForm.propTypes = {
  qfPhone: PropTypes.string.isRequired,
  setQfPhone: PropTypes.func.isRequired,
  qfName: PropTypes.string.isRequired,
  setQfName: PropTypes.func.isRequired,
  qfEmail: PropTypes.string.isRequired,
  setQfEmail: PropTypes.func.isRequired,
  qfParty: PropTypes.string.isRequired,
  setQfParty: PropTypes.func.isRequired,
  qfSaving: PropTypes.bool.isRequired,
  qfErr: PropTypes.string.isRequired,
  onQFSave: PropTypes.func.isRequired,
  onQFCancel: PropTypes.func.isRequired,
};

function CustomerLookup({ phoneInput, handlePhoneChange, handlePhoneKey, ddShow, setDdShow, ddIdx, ddRef, suggestions, custReady, selectCustomer, onNewCustomer }) {
  return (
    <div className="lookup-body">
      <div className="dd-outer" ref={ddRef}>
        <input
          className="phone-inp"
          type="text"
          inputMode="numeric"
          placeholder="Search by phone or name…"
          value={phoneInput}
          onChange={(e) => handlePhoneChange(e.target.value)}
          onKeyDown={handlePhoneKey}
          onFocus={() => { if (phoneInput.length > 0) setDdShow(true); }}
          maxLength={10}
          autoComplete="off"
        />
        {phoneInput && (
          <button className="phone-clear" type="button" onClick={() => handlePhoneChange("")}>✕</button>
        )}
        {ddShow && (
          <div className="pos-dd">
            {!custReady && <div className="dd-empty">Loading…</div>}
            {custReady && suggestions.length === 0 && phoneInput.trim() && (
              <div className="dd-empty">No customers match &ldquo;{phoneInput}&rdquo;</div>
            )}
            {custReady && suggestions.map((c, i) => (
              <button
                key={c.identifier}
                type="button"
                className={`dd-row${i === ddIdx ? " focused" : ""}`}
                onMouseDown={(e) => { e.preventDefault(); selectCustomer(c); }}
              >
                <div className="dd-av">{(c.customerName?.[0] ?? c.identifier?.[0] ?? "?").toUpperCase()}</div>
                <div style={{ flex: 1, minWidth: 0 }}>
                  <div className="dd-name">{c.customerName ? htmlEscape(c.customerName) : c.identifier}</div>
                  <div className="dd-sub">{c.identifier}{c.email ? ` · ${htmlEscape(c.email)}` : ""}</div>
                </div>
                {c.partyType && <span className="dd-tag">{htmlEscape(c.partyType)}</span>}
              </button>
            ))}
            <button
              type="button"
              className={`dd-new${ddIdx === suggestions.length ? " focused" : ""}`}
              onMouseDown={(e) => { e.preventDefault(); onNewCustomer(); }}
            >
              <span style={{ fontSize: 15, fontWeight: 700 }}>＋</span>
              New customer{phoneInput ? ` for "${phoneInput}"` : ""}
            </button>
          </div>
        )}
      </div>
      <p className="lookup-hint">Type a phone number · ↑↓ to navigate · Enter to select</p>
    </div>
  );
}
CustomerLookup.propTypes = {
  phoneInput: PropTypes.string.isRequired,
  handlePhoneChange: PropTypes.func.isRequired,
  handlePhoneKey: PropTypes.func.isRequired,
  ddShow: PropTypes.bool.isRequired,
  setDdShow: PropTypes.func.isRequired,
  ddIdx: PropTypes.number.isRequired,
  ddRef: PropTypes.object.isRequired,
  suggestions: PropTypes.array.isRequired,
  custReady: PropTypes.bool.isRequired,
  selectCustomer: PropTypes.func.isRequired,
  onNewCustomer: PropTypes.func.isRequired,
};

function CustomerSection(props) {
  const { customer, showQF, cart, onNewCustomer, onChangeCustomer } = props;
  const hasCustomer = customer != null;
  const showNewBtn  = !hasCustomer && !showQF;
  const showForm    = !hasCustomer && showQF;
  const showLookup  = !hasCustomer && !showQF;

  return (
    <div className="pos-card">
      <div className="pos-card-head">
        <span className="pos-card-title">Customer</span>
        {showNewBtn && <button className="btn-green" type="button" onClick={onNewCustomer}>+ New Customer</button>}
      </div>
      {hasCustomer  && <CustomerPanel customer={customer} cart={cart} onChangeCustomer={onChangeCustomer} />}
      {showForm     && <QuickForm {...props} />}
      {showLookup   && <CustomerLookup {...props} />}
    </div>
  );
}
CustomerSection.propTypes = {
  customer: PropTypes.object,
  showQF: PropTypes.bool.isRequired,
  cart: PropTypes.object,
  onNewCustomer: PropTypes.func.isRequired,
  onChangeCustomer: PropTypes.func.isRequired,
  phoneInput: PropTypes.string.isRequired,
  handlePhoneChange: PropTypes.func.isRequired,
  handlePhoneKey: PropTypes.func.isRequired,
  ddShow: PropTypes.bool.isRequired,
  setDdShow: PropTypes.func.isRequired,
  ddIdx: PropTypes.number.isRequired,
  ddRef: PropTypes.object.isRequired,
  suggestions: PropTypes.array.isRequired,
  custReady: PropTypes.bool.isRequired,
  selectCustomer: PropTypes.func.isRequired,
  qfPhone: PropTypes.string.isRequired,
  setQfPhone: PropTypes.func.isRequired,
  qfName: PropTypes.string.isRequired,
  setQfName: PropTypes.func.isRequired,
  qfEmail: PropTypes.string.isRequired,
  setQfEmail: PropTypes.func.isRequired,
  qfParty: PropTypes.string.isRequired,
  setQfParty: PropTypes.func.isRequired,
  qfSaving: PropTypes.bool.isRequired,
  qfErr: PropTypes.string.isRequired,
  onQFSave: PropTypes.func.isRequired,
  onQFCancel: PropTypes.func.isRequired,
};

export default function CartPage({
  phoneInput, handlePhoneChange,
  suggestions, custReady,
  ddShow, setDdShow,
  ddIdx, ddRef,
  handlePhoneKey, selectCustomer,
  onNewCustomer,
  customer = null,
  onChangeCustomer,
  showQF,
  qfPhone, setQfPhone,
  qfName, setQfName,
  qfEmail, setQfEmail,
  qfParty, setQfParty,
  qfSaving, qfErr,
  onQFSave, onQFCancel,
  cart = null,
  busy,
  entries, entrySkus,
  totalPrice, totalDiscount, totalMrp,
  products,
  productMap = {},
  filteredProducts,
  prodSearch, setProdSearch,
  prodQtys, setProdQtys,
  onAddProduct, onQtyChange, onRemoveEntry,
  onClearCart, onCheckout,
  pageErr, pageOk,
  confirmClear, setConfirmClear,
  confirmCheckout, setConfirmCheckout,
  orderConfirm = null,
  onPrintReceipt, onViewOrder, onNewOrder, receiptRef,
}) {
  const hasCustomer      = customer != null;
  const hasEntries       = entries.length > 0;
  const hasProducts      = products.length > 0;
  const hasFilteredProds = filteredProducts.length > 0;
  const isMultiItem      = entries.length !== 1;
  const customerMissing  = customer == null;

  return (
    <>
      <style>{CSS}</style>
      <div className="pos-wrap">
        <div className="pos-main">
          {pageErr && <div className="pos-err" role="alert">⚠ {pageErr}</div>}
          {pageOk  && <output className="pos-ok">✓ {pageOk}</output>}

          <CustomerSection
            customer={customer}
            showQF={showQF}
            cart={cart}
            onNewCustomer={onNewCustomer}
            onChangeCustomer={onChangeCustomer}
            phoneInput={phoneInput}
            handlePhoneChange={handlePhoneChange}
            handlePhoneKey={handlePhoneKey}
            ddShow={ddShow}
            setDdShow={setDdShow}
            ddIdx={ddIdx}
            ddRef={ddRef}
            suggestions={suggestions}
            custReady={custReady}
            selectCustomer={selectCustomer}
            qfPhone={qfPhone} setQfPhone={setQfPhone}
            qfName={qfName} setQfName={setQfName}
            qfEmail={qfEmail} setQfEmail={setQfEmail}
            qfParty={qfParty} setQfParty={setQfParty}
            qfSaving={qfSaving} qfErr={qfErr}
            onQFSave={onQFSave} onQFCancel={onQFCancel}
          />

          <div className="pos-card">
            <div className="pos-card-head">
              <span className="pos-card-title">Products</span>
              <span style={{ fontSize: 11, color: "#bbb", fontWeight: 600 }}>
                {filteredProducts.length}{prodSearch.trim() ? ` of ${products.length}` : ""} items
              </span>
            </div>
            <div className="srch-bar">
              <input className="srch-inp" placeholder="Search by name or SKU…" value={prodSearch} onChange={(e) => setProdSearch(e.target.value)} />
              {prodSearch && <button className="btn-sm-out" type="button" onClick={() => setProdSearch("")}>Clear</button>}
            </div>
            {!hasProducts && <div className="pos-empty"><span className="spin-dark" /> Loading products…</div>}
            {hasProducts && !hasFilteredProds && (
              <div className="pos-empty">No products match &ldquo;{prodSearch}&rdquo;</div>
            )}
            {hasFilteredProds && (
              <div className="prod-list">
                {filteredProducts.map((p) => {
                  const inCart = entrySkus.has(p.identifier);
                  const sp  = p.price?.sellingPrice ?? p.sellingPrice ?? null;
                  const mrp = p.price?.mrpPrice     ?? p.mrpPrice     ?? null;
                  return (
                    <div key={p.identifier} className={`prod-row${inCart ? " in-cart" : ""}`}>
                      <div className="prod-info">
                        <div className="prod-name">{p.productName}</div>
                        <span className="prod-sku">{p.identifier}</span>
                      </div>
                      {inCart && <span className="incart-chip">In cart</span>}
                      <div style={{ textAlign: "right", flexShrink: 0 }}>
                        <div style={{ fontSize: 13, fontWeight: 700, color: "#111" }}>
                          {sp == null ? <span style={{ color: "#ddd" }}>—</span> : fmt(sp)}
                        </div>
                        {mrp != null && sp != null && mrp > sp && (
                          <div style={{ fontSize: 10, color: "#bbb", textDecoration: "line-through" }}>{fmt(mrp)}</div>
                        )}
                      </div>
                      <input
                        type="number" min={1} className="qty-sm"
                        value={prodQtys[p.identifier] ?? ""}
                        onChange={(e) => setProdQtys((prev) => ({ ...prev, [p.identifier]: e.target.value.replaceAll(/\D/g, "") }))}
                        placeholder="1"
                        disabled={busy || customerMissing}
                      />
                      <button className="btn-add-prod" type="button" onClick={() => onAddProduct(p)} disabled={busy || customerMissing}>
                        {inCart ? "+ More" : "+ Add"}
                      </button>
                    </div>
                  );
                })}
              </div>
            )}
          </div>

          <div className="pos-card">
            <div className="pos-card-head">
              <span className="pos-card-title">
                Cart Items
                {hasEntries && <span className="pos-badge">{entries.length}</span>}
              </span>
              {hasEntries && <span style={{ fontSize: 12, fontWeight: 700, color: "#111" }}>{fmt(totalPrice)} total</span>}
            </div>
            {hasEntries ? (
              <div className="tbl-scroll">
                <table className="pos-tbl">
                  <thead>
                    <tr>
                      <th>Product</th><th>Price</th><th>MRP</th><th>Qty</th>
                      <th style={{ textAlign: "right" }}>Total</th>
                      <th style={{ textAlign: "right" }}>Savings</th>
                      <th style={{ width: 28 }} />
                    </tr>
                  </thead>
                  <tbody>
                    {entries.map((entry) => (
                      <tr key={entry.identifier}>
                        <td>
                          <div style={{ fontWeight: 700, color: "#111" }}>{resolveProductName(entry, productMap)}</div>
                          <span className="sku-chip">{entry.productIdentifier}</span>
                        </td>
                        <td style={{ fontWeight: 700 }}>
                          {entry.sellingPrice == null ? <span style={{ color: "#ddd" }}>—</span> : fmt(entry.sellingPrice)}
                        </td>
                        <td style={{ color: "#bbb", textDecoration: "line-through", fontSize: 12 }}>
                          {entry.mrpPrice == null ? <span style={{ color: "#eee" }}>—</span> : fmt(entry.mrpPrice)}
                        </td>
                        <td>
                          <div className="qty-wrap">
                            <button className="qty-btn" type="button" onClick={() => onQtyChange(entry, -1)} disabled={busy || (entry.quantity ?? 1) <= 1}>−</button>
                            <span className="qty-val">{entry.quantity}</span>
                            <button className="qty-btn" type="button" onClick={() => onQtyChange(entry, +1)} disabled={busy}>+</button>
                          </div>
                        </td>
                        <td style={{ textAlign: "right", fontWeight: 700 }}>
                          {entry.totalPrice == null ? <span style={{ color: "#ddd" }}>—</span> : fmt(entry.totalPrice)}
                        </td>
                        <td style={{ textAlign: "right", color: "#16a34a", fontWeight: 600, fontSize: 12 }}>
                          {(entry.discount ?? 0) > 0 ? `− ${fmt(entry.discount)}` : <span style={{ color: "#eee" }}>—</span>}
                        </td>
                        <td>
                          <button className="btn-rm" type="button" onClick={() => onRemoveEntry(entry.identifier)} disabled={busy}>✕</button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            ) : (
              <div className="pos-empty">
                {hasCustomer ? "Add products above to start billing." : "Select a customer to begin."}
              </div>
            )}
          </div>
        </div>

        <div className="pos-sidebar">
          {busy && <div className="busy-bar" />}
          <div className="sb-head">
            <span className="sb-title">Bill Summary</span>
            {hasEntries && <span className="sb-count">{entries.length} item{isMultiItem ? "s" : ""}</span>}
          </div>
          {hasEntries ? (
            <>
              <div className="sb-list">
                {entries.map((e) => (
                  <div key={e.identifier} className="sb-row">
                    <div style={{ flex: 1, minWidth: 0 }}>
                      <div className="sb-item-name">{resolveProductName(e, productMap)}</div>
                      <div className="sb-item-sub">{fmt(e.sellingPrice)} × {e.quantity}</div>
                    </div>
                    <div className="sb-item-total">{fmt(e.totalPrice)}</div>
                  </div>
                ))}
              </div>
              <div className="sb-totals">
                {totalMrp > 0      && <div className="t-row"><span>Subtotal (MRP)</span><span>{fmt(totalMrp)}</span></div>}
                {totalDiscount > 0 && <div className="t-row green"><span>You save</span><span>− {fmt(totalDiscount)}</span></div>}
                <div className="t-row grand"><span>Total</span><span>{fmt(totalPrice)}</span></div>
              </div>
              <div className="sb-actions">
                <button className="btn-checkout" type="button" onClick={() => setConfirmCheckout(true)} disabled={busy || !hasEntries}>
                  {busy ? <><span className="spin" /> Placing Order…</> : "✓ Confirm Order"}
                </button>
                <button className="btn-clear-cart" type="button" onClick={() => setConfirmClear(true)} disabled={busy}>Clear Cart</button>
              </div>
            </>
          ) : (
            <div className="sb-empty">
              <div className="sb-empty-inner">
                <span className="sb-empty-icon">🛒</span>
                {hasCustomer ? "No items yet" : "Select a customer to begin"}
              </div>
            </div>
          )}
        </div>
      </div>

      <ConfirmModal
        open={confirmClear} onClose={() => setConfirmClear(false)} onConfirm={onClearCart}
        title="Clear Cart"
        message={<span>Remove all items from <strong>{customer?.customerName ? htmlEscape(customer.customerName) : customer?.identifier}</strong>&apos;s cart? This cannot be undone.</span>}
        confirmLabel="Clear Cart" danger
      />
      <ConfirmModal
        open={confirmCheckout} onClose={() => setConfirmCheckout(false)} onConfirm={onCheckout}
        title="Confirm Order"
        message={
          <span>
            Place order for <strong>{customer?.customerName ? htmlEscape(customer.customerName) : customer?.identifier}</strong>?{" "}
            Total: <strong>{fmt(totalPrice)}</strong>
            {totalDiscount > 0 && <>, savings: <strong style={{ color: "#16a34a" }}>{fmt(totalDiscount)}</strong></>}.
            You will be redirected to the Orders page.
          </span>
        }
        confirmLabel="Place Order"
      />

      {orderConfirm && (
        <Receipt
          data={orderConfirm}
          receiptRef={receiptRef}
          onPrint={onPrintReceipt}
          onViewOrder={onViewOrder}
          onNewOrder={onNewOrder}
        />
      )}
    </>
  );
}

CartPage.propTypes = {
  phoneInput: PropTypes.string.isRequired,
  handlePhoneChange: PropTypes.func.isRequired,
  suggestions: PropTypes.array.isRequired,
  custReady: PropTypes.bool.isRequired,
  ddShow: PropTypes.bool.isRequired,
  setDdShow: PropTypes.func.isRequired,
  ddIdx: PropTypes.number.isRequired,
  ddRef: PropTypes.object.isRequired,
  handlePhoneKey: PropTypes.func.isRequired,
  selectCustomer: PropTypes.func.isRequired,
  onNewCustomer: PropTypes.func.isRequired,
  customer: PropTypes.object,
  onChangeCustomer: PropTypes.func.isRequired,
  showQF: PropTypes.bool.isRequired,
  qfPhone: PropTypes.string.isRequired,
  setQfPhone: PropTypes.func.isRequired,
  qfName: PropTypes.string.isRequired,
  setQfName: PropTypes.func.isRequired,
  qfEmail: PropTypes.string.isRequired,
  setQfEmail: PropTypes.func.isRequired,
  qfParty: PropTypes.string.isRequired,
  setQfParty: PropTypes.func.isRequired,
  qfSaving: PropTypes.bool.isRequired,
  qfErr: PropTypes.string.isRequired,
  onQFSave: PropTypes.func.isRequired,
  onQFCancel: PropTypes.func.isRequired,
  cart: PropTypes.object,
  busy: PropTypes.bool.isRequired,
  entries: PropTypes.array.isRequired,
  entrySkus: PropTypes.instanceOf(Set).isRequired,
  totalPrice: PropTypes.number.isRequired,
  totalDiscount: PropTypes.number.isRequired,
  totalMrp: PropTypes.number.isRequired,
  products: PropTypes.array.isRequired,
  productMap: PropTypes.object,
  filteredProducts: PropTypes.array.isRequired,
  prodSearch: PropTypes.string.isRequired,
  setProdSearch: PropTypes.func.isRequired,
  prodQtys: PropTypes.object.isRequired,
  setProdQtys: PropTypes.func.isRequired,
  onAddProduct: PropTypes.func.isRequired,
  onQtyChange: PropTypes.func.isRequired,
  onRemoveEntry: PropTypes.func.isRequired,
  onClearCart: PropTypes.func.isRequired,
  onCheckout: PropTypes.func.isRequired,
  pageErr: PropTypes.string.isRequired,
  pageOk: PropTypes.string.isRequired,
  confirmClear: PropTypes.bool.isRequired,
  setConfirmClear: PropTypes.func.isRequired,
  confirmCheckout: PropTypes.bool.isRequired,
  setConfirmCheckout: PropTypes.func.isRequired,
  orderConfirm: PropTypes.object,
  onPrintReceipt: PropTypes.func.isRequired,
  onViewOrder: PropTypes.func.isRequired,
  onNewOrder: PropTypes.func.isRequired,
  receiptRef: PropTypes.object.isRequired,
};