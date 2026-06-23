"use client";

import { useState, useEffect, useCallback, useRef, useMemo } from "react";
import PropTypes from "prop-types";

import { fetchWithAuth } from "@/lib/api";
import { htmlEscape, validators } from "@/lib/security";

const CSS = `
@import url('https://fonts.googleapis.com/css2?family=Barlow:wght@400;500;600;700;800;900&display=swap');
.cu *{box-sizing:border-box}
.cu{font-family:'Barlow',sans-serif;min-height:calc(100vh - 56px);background:#f0f2f5;padding:24px}
.cu-wrap{max-width:1200px;margin:0 auto}
.cu-hd{display:flex;align-items:center;gap:10px;margin-bottom:18px;flex-wrap:wrap}
.cu-title{font-size:19px;font-weight:800;color:#111;letter-spacing:-0.02em;white-space:nowrap}
.cu-badge{background:#005dab;color:#fff;font-size:11px;font-weight:700;padding:2px 8px;border-radius:20px;flex-shrink:0}
.cu-search-wrap{flex:1;min-width:160px;max-width:300px;position:relative;display:flex;align-items:center;margin-left:6px}
.cu-search{width:100%;padding:7px 30px 7px 32px;font-family:'Barlow',sans-serif;font-size:13px;font-weight:500;color:#111;background:#fff;border:1.5px solid #ddd;border-radius:6px;outline:none;transition:border-color .15s}
.cu-search:focus{border-color:#005dab;box-shadow:0 0 0 3px rgba(0,93,171,.1)}
.cu-search::placeholder{color:#aaa;font-weight:400}
.cu-search-icon{position:absolute;left:9px;top:50%;transform:translateY(-50%);pointer-events:none;color:#bbb}
.cu-search-clear{position:absolute;right:8px;top:50%;transform:translateY(-50%);background:none;border:none;cursor:pointer;color:#bbb;font-size:12px;padding:2px;border-radius:3px}
.cu-search-clear:hover{color:#555}
.btn-add{margin-left:auto;padding:7px 16px;background:#e31837;border:none;border-radius:6px;color:#fff;font-family:'Barlow',sans-serif;font-size:12px;font-weight:800;text-transform:uppercase;letter-spacing:.04em;cursor:pointer;transition:background .15s;white-space:nowrap;flex-shrink:0}
.btn-add:hover{background:#c0152a}
.cu-card{background:#fff;border:1px solid #e0e0e0;border-radius:10px;overflow:hidden;box-shadow:0 1px 4px rgba(0,0,0,.04)}
.cu-empty{padding:48px;text-align:center;color:#bbb;font-size:14px;font-weight:500}
table.cu-tbl{width:100%;border-collapse:collapse;font-size:13px}
table.cu-tbl thead tr{background:#005dab}
table.cu-tbl th{padding:10px 14px;text-align:left;font-size:10px;font-weight:700;color:rgba(255,255,255,.85);text-transform:uppercase;letter-spacing:.08em;white-space:nowrap}
table.cu-tbl td{padding:10px 14px;color:#333;border-bottom:1px solid #f0f0f0;vertical-align:middle;font-weight:500}
table.cu-tbl tbody tr:last-child td{border-bottom:none}
table.cu-tbl tbody tr:hover{background:#f8f9fb}
.badge{display:inline-block;padding:2px 7px;border-radius:4px;font-size:10px;font-weight:700;background:#e8f0fa;color:#005dab;text-transform:uppercase;letter-spacing:.04em}
.tgl{width:38px;height:20px;border-radius:999px;background:#ccc;border:none;position:relative;cursor:pointer;transition:background .18s;flex-shrink:0}
.tgl::after{content:"";position:absolute;top:2px;left:2px;width:16px;height:16px;background:#fff;border-radius:50%;box-shadow:0 1px 3px rgba(0,0,0,.2);transition:transform .18s}
.tgl.on{background:#005dab}
.tgl.on::after{transform:translateX(18px)}
.btn-edit{padding:4px 11px;border-radius:5px;border:1.5px solid #005dab;background:#fff;color:#005dab;font-size:11px;font-weight:700;cursor:pointer;font-family:'Barlow',sans-serif;transition:all .13s;text-transform:uppercase}
.btn-edit:hover{background:#005dab;color:#fff}
.btn-del{padding:4px 11px;border-radius:5px;border:1.5px solid #e31837;background:#fff;color:#e31837;font-size:11px;font-weight:700;cursor:pointer;font-family:'Barlow',sans-serif;transition:all .13s;text-transform:uppercase}
.btn-del:hover{background:#e31837;color:#fff}
.act-wrap{display:flex;gap:6px;justify-content:flex-end}
.pag{display:flex;align-items:center;justify-content:space-between;flex-wrap:wrap;gap:10px;padding:10px 14px;border-top:1px solid #f0f0f0}
.pag-info{font-size:11px;color:#aaa;font-weight:600}
.pag-btns{display:flex;align-items:center;gap:3px}
.pb{min-width:30px;height:30px;padding:0 5px;display:inline-flex;align-items:center;justify-content:center;border-radius:5px;border:1.5px solid #e0e0e0;background:#fff;color:#555;font-size:12px;font-weight:700;cursor:pointer;transition:all .13s;font-family:'Barlow',sans-serif}
.pb:hover:not(:disabled){background:#f0f2f5;border-color:#bbb}
.pb.pb-a{background:#005dab;border-color:#005dab;color:#fff}
.pb:disabled{opacity:.35;cursor:not-allowed}
.pb-dots{min-width:30px;height:30px;display:inline-flex;align-items:center;justify-content:center;color:#bbb;font-size:12px}
.cu-ov{position:fixed;inset:0;width:100vw;height:100vh;margin:0;padding:20px;display:flex;align-items:center;justify-content:center;background:rgba(0,0,0,.45);border:none;z-index:100;overflow:auto;backdrop-filter:blur(2px)}
.cu-mbox{width:100%;background:#fff;border-radius:10px;box-shadow:0 8px 40px rgba(0,0,0,.16);overflow:hidden;animation:cuIn .17s ease}
@keyframes cuIn{from{opacity:0;transform:scale(.97) translateY(6px)}to{opacity:1;transform:none}}
.cu-mhead{display:flex;align-items:center;justify-content:space-between;padding:13px 18px;border-bottom:2px solid #f0f0f0;background:#005dab}
.cu-mtitle{font-size:13px;font-weight:800;color:#fff;text-transform:uppercase;letter-spacing:.06em}
.cu-mx{width:26px;height:26px;border-radius:5px;border:none;background:rgba(255,255,255,.2);color:#fff;font-size:11px;cursor:pointer;display:flex;align-items:center;justify-content:center;transition:background .13s;font-weight:700}
.cu-mx:hover{background:rgba(255,255,255,.35)}
.cu-mbody{padding:18px;max-height:80vh;overflow-y:auto}
.cu-alert{padding:9px 12px;background:#fff0f3;border:1px solid #fbbcca;border-radius:6px;font-size:13px;color:#c0152a;margin-bottom:14px;font-weight:600}
.cu-pg-err{padding:10px 14px;background:#fff0f3;border:1px solid #fbbcca;border-radius:6px;font-size:13px;color:#c0152a;margin-bottom:14px;font-weight:600}
.cu-section{font-size:10px;font-weight:800;color:#005dab;text-transform:uppercase;letter-spacing:.1em;padding:4px 0;border-bottom:1.5px solid #e8f0fa;margin:14px 0 10px}
.cu-section:first-child{margin-top:0}
.cu-grid{display:grid;grid-template-columns:1fr 1fr 1fr;gap:10px}
.cu-grid-2{display:grid;grid-template-columns:1fr 1fr;gap:10px}
@media(max-width:540px){.cu-grid,.cu-grid-2{grid-template-columns:1fr}}
.cu-field{display:flex;flex-direction:column;gap:4px}
.cu-label{font-size:10px;font-weight:700;color:#444;text-transform:uppercase;letter-spacing:.09em}
.cu-req{color:#e31837;margin-left:2px}
.fi{width:100%;padding:9px 11px;font-family:'Barlow',sans-serif;font-size:14px;font-weight:500;color:#111;background:#fff;border:1.5px solid #ddd;border-radius:6px;outline:none;transition:border-color .15s,box-shadow .15s}
.fi:focus{border-color:#005dab;box-shadow:0 0 0 3px rgba(0,93,171,.1)}
.fi.fi-sm{padding:7px 10px;font-size:13px}
.fi.fi-ro{background:#f5f5f5;color:#888;cursor:not-allowed}
.fi.fi-err{border-color:#e31837;background:#fffafa}
select.fi option{font-family:'Barlow',sans-serif}
.btn-save{width:100%;padding:10px;background:#e31837;border:none;border-radius:6px;color:#fff;font-family:'Barlow',sans-serif;font-size:13px;font-weight:800;text-transform:uppercase;letter-spacing:.04em;cursor:pointer;margin-top:16px;transition:background .15s}
.btn-save:hover:not(:disabled){background:#c0152a}
.btn-save:disabled{opacity:.45;cursor:not-allowed}
.cu-check-row{display:flex;align-items:center;gap:8px;margin:10px 0 6px}
.cu-check-row input{accent-color:#005dab;width:14px;height:14px}
.cu-check-label{font-size:12px;font-weight:600;color:#555;cursor:pointer;font-family:'Barlow',sans-serif}
.del-body{padding:18px}
.del-msg{font-size:14px;color:#555;line-height:1.7;margin-bottom:18px;font-weight:500}
.del-msg strong{color:#111;font-weight:800}
.del-actions{display:flex;gap:8px;justify-content:flex-end}
.btn-cancel{padding:7px 16px;border-radius:6px;border:1.5px solid #ddd;background:#fff;color:#555;font-family:'Barlow',sans-serif;font-size:12px;font-weight:700;cursor:pointer;text-transform:uppercase}
.btn-cancel:hover{background:#f0f2f5}
.btn-confirm-del{padding:7px 16px;border-radius:6px;border:none;background:#e31837;color:#fff;font-family:'Barlow',sans-serif;font-size:12px;font-weight:800;cursor:pointer;transition:background .15s;text-transform:uppercase}
.btn-confirm-del:hover:not(:disabled){background:#c0152a}
.btn-confirm-del:disabled{opacity:.5;cursor:not-allowed}
`;

const PARTY_TYPES = ["Customer", "Dealer", "Retailer", "Distributor"];
const CREDIT_TYPES = [
  { value: "", label: "-- Select --" },
  { value: "ADVANCE", label: "Advance" },
  { value: "DUE", label: "Due" },
  { value: "NA", label: "NA" },
];
const PAGE_SIZE = 10;

const emptyAddr = (phone = "") => ({
  addressLine: "",
  phoneNo: phone,
  city: "",
  state: "",
  zipCode: "",
  country: "",
});

const emptyForm = (phone = "") => ({
  identifier: phone,
  customerName: "",
  email: "",
  partyType: "Customer",
  credit: "",
  creditType: "",
  creditLimit: "",
  billingAddress: emptyAddr(phone),
  shippingAddress: emptyAddr(phone),
});

function Pagination({ page, totalPages, totalRecords, pageSize, onPageChange }) {
  if (totalPages <= 1) return null;
  const from = page * pageSize + 1;
  const to = Math.min((page + 1) * pageSize, totalRecords);
  const range = [];
  const deltaPages = 2;
  for (let i = 0; i < totalPages; i++) {
    if (i === 0 || i === totalPages - 1 || (i >= page - deltaPages && i <= page + deltaPages))
      range.push(i);
  }
  const items = [];
  let prev = null;
  for (const i of range) {
    if (prev !== null) {
      if (i - prev === 2) items.push({ type: "page", page: prev + 1, key: `p${prev + 1}` });
      else if (i - prev > 2) items.push({ type: "ellipsis", key: `e${i}` });
    }
    items.push({ type: "page", page: i, key: `p${i}` });
    prev = i;
  }
  return (
    <div className="pag">
      <span className="pag-info">Showing {from}–{to} of {totalRecords}</span>
      <div className="pag-btns">
        <button className="pb" onClick={() => onPageChange(0)} disabled={page === 0}>«</button>
        <button className="pb" onClick={() => onPageChange(page - 1)} disabled={page === 0}>‹</button>
        {items.map((item) =>
          item.type === "ellipsis" ? (
            <span key={item.key} className="pb-dots">…</span>
          ) : (
            <button
              key={item.key}
              className={`pb${item.page === page ? " pb-a" : ""}`}
              onClick={() => onPageChange(item.page)}
            >
              {item.page + 1}
            </button>
          ),
        )}
        <button className="pb" onClick={() => onPageChange(page + 1)} disabled={page >= totalPages - 1}>›</button>
        <button className="pb" onClick={() => onPageChange(totalPages - 1)} disabled={page >= totalPages - 1}>»</button>
      </div>
    </div>
  );
}
Pagination.propTypes = {
  page: PropTypes.number.isRequired,
  totalPages: PropTypes.number.isRequired,
  totalRecords: PropTypes.number.isRequired,
  pageSize: PropTypes.number.isRequired,
  onPageChange: PropTypes.func.isRequired,
};

function CustomerFormModal({ open, onClose, onSaved, mode, initialData }) {
  const [form, setForm] = useState(emptyForm());
  const [sameAsBilling, setSameAsBilling] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    if (!open) return;
    if (mode === "add") {
      setForm(emptyForm());
    } else if (initialData) {
      setForm({
        identifier: initialData.identifier ?? "",
        customerName: initialData.customerName ?? "",
        email: initialData.email ?? "",
        partyType: initialData.partyType ?? "Customer",
        credit: initialData.credit ?? "",
        creditType: initialData.creditType ?? "",
        creditLimit: initialData.creditLimit ?? "",
        billingAddress: {
          addressLine: initialData.billingAddress?.addressLine ?? "",
          phoneNo: initialData.billingAddress?.phoneNo ?? initialData.identifier ?? "",
          city: initialData.billingAddress?.city ?? "",
          state: initialData.billingAddress?.state ?? "",
          zipCode: initialData.billingAddress?.zipCode ?? "",
          country: initialData.billingAddress?.country ?? "",
        },
        shippingAddress: {
          addressLine: initialData.shippingAddress?.addressLine ?? "",
          phoneNo: initialData.shippingAddress?.phoneNo ?? initialData.identifier ?? "",
          city: initialData.shippingAddress?.city ?? "",
          state: initialData.shippingAddress?.state ?? "",
          zipCode: initialData.shippingAddress?.zipCode ?? "",
          country: initialData.shippingAddress?.country ?? "",
        },
      });
    }
    setSameAsBilling(false);
    setError("");
  }, [open, mode, initialData]);

  useEffect(() => {
    if (!open) return;
    const h = (e) => { if (e.key === "Escape") onClose(); };
    document.addEventListener("keydown", h);
    return () => document.removeEventListener("keydown", h);
  }, [open, onClose]);

  if (!open) return null;

  const setTop = (k, v) => setForm((f) => ({ ...f, [k]: v }));
  const setBill = (k, v) => {
    setForm((f) => {
      const b = { ...f.billingAddress, [k]: v };
      return {
        ...f,
        billingAddress: b,
        shippingAddress: sameAsBilling ? { ...b } : f.shippingAddress,
      };
    });
  };
  const setShip = (k, v) => setForm((f) => ({ ...f, shippingAddress: { ...f.shippingAddress, [k]: v } }));

  const toggleSame = () => {
    const next = !sameAsBilling;
    setSameAsBilling(next);
    if (next) setForm((f) => ({ ...f, shippingAddress: { ...f.billingAddress } }));
  };

  const handleSubmit = async () => {
    if (!form.identifier.trim()) { setError("Phone number is required."); return; }
    if (!validators.phone(form.identifier)) { setError("Phone number must be exactly 10 digits."); return; }
    if (!form.customerName.trim()) { setError("Customer name is required."); return; }
    if (form.email && !validators.email(form.email)) { setError("Enter a valid email address."); return; }
    if (form.creditLimit && !validators.creditLimit(form.creditLimit)) { setError("Credit limit must be between 0 and 1,000,000."); return; }
    setSaving(true);
    setError("");
    try {
      const payload = {
        ...form,
        billingAddress: { ...form.billingAddress, phoneNo: form.identifier },
        shippingAddress: { ...form.shippingAddress, phoneNo: form.identifier },
      };
      let res;
      if (mode === "add") {
        res = await fetchWithAuth("/api/customers/save", {
          method: "POST",
          body: JSON.stringify(payload),
        });
      } else {
        res = await fetchWithAuth(`/api/customers/update/${form.identifier}`, {
          method: "PUT",
          body: JSON.stringify(payload),
        });
      }
      onSaved(res);
    } catch (e) {
      setError(e.message || "Save failed.");
    } finally {
      setSaving(false);
    }
  };

  const addrKeys = ["addressLine", "city", "state", "zipCode", "country"];
  const addrLabels = {
    addressLine: "Address Line",
    city: "City",
    state: "State",
    zipCode: "Zip Code",
    country: "Country",
  };
  const isEdit = mode === "edit";

  return (
    <dialog className="cu-ov" open aria-label={isEdit ? "Edit Customer" : "Add Customer"}>
      <div className="cu-mbox" style={{ maxWidth: 680 }}>
        <div className="cu-mhead">
          <span className="cu-mtitle">{isEdit ? "Edit Customer" : "Add Customer"}</span>
          <button className="cu-mx" onClick={onClose} type="button">✕</button>
        </div>
        <div className="cu-mbody">
          {error && <div className="cu-alert">{error}</div>}

          <div className="cu-section">Customer Details</div>
          <div className="cu-grid">
            <div className="cu-field">
              <label className="cu-label" htmlFor="phoneInput">Phone Number <span className="cu-req">*</span></label>
              <input
                id="phoneInput"
                className={`fi${isEdit ? " fi-ro" : ""}`}
                value={form.identifier}
                readOnly={isEdit}
                maxLength={10}
                onChange={(e) => { if (!isEdit) setTop("identifier", e.target.value.replaceAll(/\D/g, "")); }}
              />
            </div>
            <div className="cu-field">
              <label className="cu-label" htmlFor="customerNameInput">Customer Name <span className="cu-req">*</span></label>
              <input
                id="customerNameInput"
                className="fi"
                value={form.customerName}
                onChange={(e) => setTop("customerName", e.target.value)}
              />
            </div>
            <div className="cu-field">
              <label className="cu-label" htmlFor="emailInput">Email</label>
              <input
                id="emailInput"
                className="fi"
                type="email"
                value={form.email}
                onChange={(e) => setTop("email", e.target.value)}
              />
            </div>
            <div className="cu-field">
              <label className="cu-label" htmlFor="partyTypeSelect">Party Type</label>
              <select id="partyTypeSelect" className="fi" value={form.partyType} onChange={(e) => setTop("partyType", e.target.value)}>
                {PARTY_TYPES.map((v) => <option key={v} value={v}>{v}</option>)}
              </select>
            </div>
            <div className="cu-field">
              <label className="cu-label" htmlFor="creditInput">Credit</label>
              <input
                id="creditInput"
                className="fi"
                type="number"
                value={form.credit}
                onChange={(e) => setTop("credit", e.target.value)}
              />
            </div>
            <div className="cu-field">
              <label className="cu-label" htmlFor="creditTypeSelect">Credit Type</label>
              <select id="creditTypeSelect" className="fi" value={form.creditType} onChange={(e) => setTop("creditType", e.target.value)}>
                {CREDIT_TYPES.map((o) => <option key={o.value} value={o.value}>{o.label}</option>)}
              </select>
            </div>
            <div className="cu-field">
              <label className="cu-label" htmlFor="creditLimitInput">Credit Limit</label>
              <input
                id="creditLimitInput"
                className="fi"
                type="number"
                value={form.creditLimit}
                onChange={(e) => setTop("creditLimit", e.target.value)}
              />
            </div>
          </div>

          <div className="cu-section">Billing Address</div>
          <div className="cu-grid-2">
            {addrKeys.map((k) => (
              <div className="cu-field" key={k}>
                <label className="cu-label">{addrLabels[k]}</label>
                <input className="fi fi-sm" value={form.billingAddress[k]} onChange={(e) => setBill(k, e.target.value)} />
              </div>
            ))}
          </div>

          <div className="cu-check-row">
            <input type="checkbox" id="cu-sab" checked={sameAsBilling} onChange={toggleSame} />
            <label htmlFor="cu-sab" className="cu-check-label">Shipping address same as billing</label>
          </div>

          {!sameAsBilling && (
            <>
              <div className="cu-section">Shipping Address</div>
              <div className="cu-grid-2">
                {addrKeys.map((k) => (
                  <div className="cu-field" key={k}>
                    <label className="cu-label">{addrLabels[k]}</label>
                    <input className="fi fi-sm" value={form.shippingAddress[k]} onChange={(e) => setShip(k, e.target.value)} />
                  </div>
                ))}
              </div>
            </>
          )}

          {(() => {
            let btnText;
            if (saving) btnText = "Saving…";
            else if (isEdit) btnText = "Update Customer";
            else btnText = "Add Customer";
            return (
              <button className="btn-save" onClick={handleSubmit} disabled={saving}>
                {btnText}
              </button>
            );
          })()}
        </div>
      </div>
    </dialog>
  );
}

CustomerFormModal.propTypes = {
  open: PropTypes.bool.isRequired,
  onClose: PropTypes.func.isRequired,
  onSaved: PropTypes.func.isRequired,
  mode: PropTypes.oneOf(["add", "edit"]).isRequired,
  initialData: PropTypes.object,
};
CustomerFormModal.defaultProps = { initialData: null };

export default function CustomersPage() {
  const [records, setRecords] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [search, setSearch] = useState("");
  const [debouncedSearch, setDebouncedSearch] = useState("");
  const [page, setPage] = useState(0);
  const [modal, setModal] = useState(null);
  const [editData, setEditData] = useState(null);
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [deleting, setDeleting] = useState(false);
  const searchRef = useRef(null);
  const debounceTimerRef = useRef(null);

  const fetchList = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const data = await fetchWithAuth("/api/customers/list", {
        method: "POST",
        body: JSON.stringify({ page: 0, sizePerPage: 10000 }),
      });
      setRecords(Array.isArray(data) ? data : (data?.dtoList ?? []));
    } catch (e) {
      if (e.status !== 401) setError(e.message || "Failed to load customers.");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { fetchList(); }, [fetchList]);

  useEffect(() => {
    if (debounceTimerRef.current) clearTimeout(debounceTimerRef.current);
    debounceTimerRef.current = setTimeout(() => {
      setDebouncedSearch(search);
      setPage(0);
    }, 300);
    return () => { if (debounceTimerRef.current) clearTimeout(debounceTimerRef.current); };
  }, [search]);

  const filtered = useMemo(() => {
    const q = debouncedSearch.trim().toLowerCase();
    if (!q) return records;
    return records.filter((r) =>
      [r.identifier, r.customerName, r.email, r.partyType].some(
        (v) => v && String(v).toLowerCase().includes(q),
      ),
    );
  }, [records, debouncedSearch]);

  const totalPages = Math.max(1, Math.ceil(filtered.length / PAGE_SIZE));
  const safePage = Math.min(page, totalPages - 1);
  const pageRows = filtered.slice(safePage * PAGE_SIZE, (safePage + 1) * PAGE_SIZE);

  const closeModal = () => { setModal(null); setEditData(null); setDeleteTarget(null); };

  const openEdit = async (record) => {
    setError("");
    try {
      const data = await fetchWithAuth(`/api/customers/${record.identifier}`);
      setEditData(data);
      setModal("edit");
    } catch (e) {
      if (e.status !== 401) setError(e.message || "Failed to load customer.");
    }
  };

  const openDelete = (record) => { setDeleteTarget(record); setModal("delete"); };

  const handleSaved = () => { closeModal(); fetchList(); };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    setDeleting(true);
    try {
      await fetchWithAuth(`/api/customers/delete/${deleteTarget.identifier}`, {
        method: "DELETE",
        body: JSON.stringify({}),
      });
      closeModal();
      fetchList();
    } catch (e) {
      setError(e.message || "Delete failed.");
      closeModal();
    } finally {
      setDeleting(false);
    }
  };

  const handleToggle = async (record) => {
    setRecords((prev) =>
      prev.map((r) => r.identifier === record.identifier ? { ...r, status: !r.status } : r),
    );
    try {
      await fetchWithAuth(`/api/customers/toggle/${record.identifier}`, {
        method: "POST",
        body: JSON.stringify({}),
      });
    } catch (e) {
      setRecords((prev) =>
        prev.map((r) => r.identifier === record.identifier ? { ...r, status: !r.status } : r),
      );
      if (e.status !== 401) setError(e.message || "Failed to update status.");
    }
  };

  return (
    <>
      <style>{CSS}</style>
      <div className="cu">
        <div className="cu-wrap">
          <div className="cu-hd">
            <h1 className="cu-title">Customer Management</h1>
            {records.length > 0 && <span className="cu-badge">{records.length}</span>}
            <div className="cu-search-wrap">
              <span className="cu-search-icon">
                <svg width="14" height="14" viewBox="0 0 16 16" fill="none">
                  <circle cx="6.5" cy="6.5" r="5" stroke="currentColor" strokeWidth="1.8" />
                  <path d="M10.5 10.5L14 14" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" />
                </svg>
              </span>
              <input
                ref={searchRef}
                type="text"
                className="cu-search"
                placeholder="Search customers…"
                value={search}
                onChange={(e) => setSearch(e.target.value)}
              />
              {search && (
                <button className="cu-search-clear" onClick={() => { setSearch(""); searchRef.current?.focus(); }} type="button">
                  ✕
                </button>
              )}
            </div>
            <button className="btn-add" onClick={() => setModal("add")}>+ Add Customer</button>
          </div>

          {error && <div className="cu-pg-err" role="alert">{error}</div>}

          <div className="cu-card">
            {loading ? (
              <div className="cu-empty">Loading…</div>
            ) : (() => {
              if (pageRows.length === 0) {
                return (
                  <div className="cu-empty">
                    {search.trim() ? `No results for "${search}".` : "No customers found."}
                  </div>
                );
              }
              return (
                <div style={{ overflowX: "auto" }}>
                  <table className="cu-tbl">
                    <thead>
                      <tr>
                        <th>Phone</th>
                        <th>Name</th>
                        <th>Email</th>
                        <th>Party Type</th>
                        <th>Credit</th>
                        <th>Credit Type</th>
                        <th>Credit Limit</th>
                        <th>Status</th>
                        <th style={{ textAlign: "right" }}>Actions</th>
                      </tr>
                    </thead>
                    <tbody>
                      {pageRows.map((r) => (
                        <tr key={r.identifier}>
                          <td style={{ fontWeight: 700, color: "#111" }}>{r.identifier}</td>
                          <td>{r.customerName ? htmlEscape(r.customerName) : <span style={{ color: "#ccc" }}>—</span>}</td>
                          <td>{r.email ? htmlEscape(r.email) : <span style={{ color: "#ccc" }}>—</span>}</td>
                          <td>{r.partyType ? <span className="badge">{r.partyType}</span> : <span style={{ color: "#ccc" }}>—</span>}</td>
                          <td>{r.credit ?? <span style={{ color: "#ccc" }}>—</span>}</td>
                          <td>{r.creditType || <span style={{ color: "#ccc" }}>—</span>}</td>
                          <td>{r.creditLimit ?? <span style={{ color: "#ccc" }}>—</span>}</td>
                          <td>
                            <button
                              className={`tgl${r.status ? " on" : ""}`}
                              onClick={(e) => { e.stopPropagation(); handleToggle(r); }}
                              aria-label="Toggle status"
                              type="button"
                            />
                          </td>
                          <td>
                            <div className="act-wrap">
                              <button className="btn-edit" onClick={() => openEdit(r)}>Edit</button>
                              <button className="btn-del" onClick={() => openDelete(r)}>Delete</button>
                            </div>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                  <Pagination
                    page={safePage}
                    totalPages={totalPages}
                    totalRecords={filtered.length}
                    pageSize={PAGE_SIZE}
                    onPageChange={setPage}
                  />
                </div>
              );
            })()}
          </div>
        </div>
      </div>

      <CustomerFormModal
        open={modal === "add" || modal === "edit"}
        onClose={closeModal}
        onSaved={handleSaved}
        mode={modal === "edit" ? "edit" : "add"}
        initialData={editData}
      />

      {modal === "delete" && (
        <dialog className="cu-ov" open aria-label="Delete Customer">
          <div className="cu-mbox" style={{ maxWidth: 420 }}>
            <div className="cu-mhead">
              <span className="cu-mtitle">Delete Customer</span>
              <button className="cu-mx" onClick={closeModal} type="button">✕</button>
            </div>
            <div className="del-body">
              <p className="del-msg">
                Are you sure you want to delete customer{" "}
                <strong>{deleteTarget?.customerName ? htmlEscape(deleteTarget.customerName) : deleteTarget?.identifier}</strong>?
                This action cannot be undone.
              </p>
              <div className="del-actions">
                <button className="btn-cancel" onClick={closeModal} type="button">Cancel</button>
                <button className="btn-confirm-del" onClick={handleDelete} disabled={deleting} type="button">
                  {deleting ? "Deleting…" : "Delete"}
                </button>
              </div>
            </div>
          </div>
        </dialog>
      )}
    </>
  );
}