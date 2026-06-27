"use client";

import { useState, useEffect, useCallback, useRef, useMemo } from "react";
import PropTypes from "prop-types";
import { fetchWithAuth } from "@/lib/api";

function validateField(f, val) {
  if (typeof f.validate === "function") return f.validate(val);
  if (!f.required) return null;
  if (val === null || val === undefined || val === "")
    return `${f.label} is required.`;
  if (typeof val === "string" && !val.trim()) return `${f.label} is required.`;
  if (Array.isArray(val) && val.length === 0)
    return `Please select at least one ${f.label}.`;
  return null;
}

function validateForm(fields, form, view) {
  const active = fields.filter(
    (f) => !f.hideInForm && !(view === "edit" && f.hideOnEdit),
  );
  for (const f of active) {
    const msg = validateField(f, form[f.key]);
    if (msg) return msg;
  }
  return null;
}

function Toggle({ active, onChange }) {
  return (
    <button
      onClick={(e) => {
        e.stopPropagation();
        onChange();
      }}
      className={`tgl${active ? " on" : ""}`}
      aria-label="Toggle"
      type="button"
    />
  );
}

Toggle.propTypes = {
  active: PropTypes.bool.isRequired,
  onChange: PropTypes.func.isRequired,
};

const NUMERIC_ALLOW = new Set([
  "Backspace",
  "Delete",
  "Tab",
  "Escape",
  "Enter",
  "ArrowLeft",
  "ArrowRight",
  "ArrowUp",
  "ArrowDown",
  "Home",
  "End",
]);

function blockNonNumeric(e) {
  if (NUMERIC_ALLOW.has(e.key) || e.ctrlKey || e.metaKey) return;
  if (!/^\d$/.test(e.key)) e.preventDefault();
}

function blockNonDecimal(e) {
  if (NUMERIC_ALLOW.has(e.key) || e.ctrlKey || e.metaKey) return;
  if (e.key === "." && !e.target.value.includes(".")) return;
  if (!/^\d$/.test(e.key)) e.preventDefault();
}

function isValidDecimalString(s) {
  if (s === null || s === undefined) return false;
  if (s === "") return true;
  let dotCount = 0;
  for (const ch of s) {
    if (ch === ".") {
      dotCount += 1;
      if (dotCount > 1) return false;
    } else if (ch < "0" || ch > "9") {
      return false;
    }
  }
  return true;
}

function renderTextarea(field, value, onChange, readOnly, cls) {
  return (
    <textarea
      rows={3}
      value={value ?? ""}
      onChange={(e) => onChange(field.key, e.target.value)}
      disabled={readOnly}
      className={cls}
      placeholder={field.placeholder || ""}
    />
  );
}

function renderSelect(field, value, onChange, readOnly, cls) {
  const multi = !!field.multiple;
  let mv = [];
  if (Array.isArray(value)) mv = value;
  else if (value) mv = [value];
  const handleChange = (e) =>
    onChange(
      field.key,
      multi
        ? Array.from(e.target.selectedOptions).map((o) => o.value)
        : e.target.value,
    );
  return (
    <select
      multiple={multi}
      value={multi ? mv : (value ?? "")}
      onChange={handleChange}
      disabled={readOnly}
      className={`${cls}${multi ? " fi-multi" : ""}`}
    >
      {!multi && (
        <option value="">{field.placeholder || "-- Select --"}</option>
      )}
      {(field.options ?? []).map((o) => (
        <option key={o.value} value={o.value}>
          {o.label}
        </option>
      ))}
    </select>
  );
}

function renderNumber(field, value, onChange, readOnly, cls) {
  return (
    <input
      type="text"
      inputMode="decimal"
      value={value ?? ""}
      onChange={(e) => {
        const v = e.target.value;
        if (v === "" || isValidDecimalString(v)) onChange(field.key, v);
      }}
      onKeyDown={blockNonDecimal}
      onPaste={(e) => {
        if (!isValidDecimalString(e.clipboardData.getData("text")))
          e.preventDefault();
      }}
      disabled={readOnly}
      className={cls}
      placeholder={field.placeholder || "0"}
    />
  );
}

function renderTel(field, value, onChange, readOnly, cls) {
  return (
    <input
      type="tel"
      inputMode="numeric"
      value={value ?? ""}
      onChange={(e) =>
        onChange(field.key, e.target.value.replaceAll(/\D/g, ""))
      }
      onKeyDown={blockNonNumeric}
      onPaste={(e) => {
        if (!/^\d+$/.test(e.clipboardData.getData("text"))) e.preventDefault();
      }}
      disabled={readOnly}
      className={cls}
      placeholder={field.placeholder || ""}
      maxLength={field.maxLength || 10}
    />
  );
}

function renderPassword(field, value, onChange, readOnly, cls) {
  return (
    <input
      type="password"
      value={value ?? ""}
      onChange={(e) => onChange(field.key, e.target.value)}
      disabled={readOnly}
      className={cls}
      placeholder={field.placeholder || ""}
      autoComplete="new-password"
    />
  );
}

function renderDefault(field, value, onChange, readOnly, cls) {
  return (
    <input
      type={field.type ?? "text"}
      value={value ?? ""}
      onChange={(e) => onChange(field.key, e.target.value)}
      disabled={readOnly}
      className={cls}
      placeholder={field.placeholder || ""}
    />
  );
}

function renderCellValue(raw, f, currencyFields, idKey) {
  if (Array.isArray(raw)) {
    if (raw.length === 0) return <span style={{ color: "#ccc" }}>—</span>;
    return (
      <div style={{ display: "flex", flexWrap: "wrap", gap: 3 }}>
        {raw.map((v) => (
          <span key={String(v)} className="badge">
            {v}
          </span>
        ))}
      </div>
    );
  }
  if (raw === null || raw === undefined || raw === "") {
    return <span style={{ color: "#ccc" }}>—</span>;
  }
  if (currencyFields.includes(f.key)) {
    return `₹ ${Number(raw).toLocaleString("en-IN")}`;
  }
  if (f.key === idKey) {
    return <span style={{ fontWeight: 700, color: "#111" }}>{raw}</span>;
  }
  if (typeof raw === "boolean") {
    return <span className="badge">{raw ? "Yes" : "No"}</span>;
  }
  return String(raw);
}

function FieldInput({ field, value, onChange, readOnly, hasError }) {
  const cls = `fi${hasError ? " fi-err" : ""}${readOnly ? " fi-ro" : ""}`;
  switch (field.type) {
    case "textarea":
      return renderTextarea(field, value, onChange, readOnly, cls);
    case "select":
      return renderSelect(field, value, onChange, readOnly, cls);
    case "number":
      return renderNumber(field, value, onChange, readOnly, cls);
    case "tel":
      return renderTel(field, value, onChange, readOnly, cls);
    case "password":
      return renderPassword(field, value, onChange, readOnly, cls);
    default:
      return renderDefault(field, value, onChange, readOnly, cls);
  }
}

function Pagination({
  page,
  totalPages,
  onPageChange,
  totalRecords,
  pageSize,
}) {
  if (totalPages <= 1) return null;
  const from = page * pageSize + 1;
  const to = Math.min((page + 1) * pageSize, totalRecords);
  const pages = (() => {
    const delta = 2;
    const range = [];
    for (let i = 0; i < totalPages; i += 1) {
      if (
        i === 0 ||
        i === totalPages - 1 ||
        (i >= page - delta && i <= page + delta)
      )
        range.push(i);
    }
    const pagesList = [];
    let previous = null;
    for (const item of range) {
      if (previous !== null) {
        if (item - previous === 2)
          pagesList.push({
            type: "page",
            page: previous + 1,
            key: `p${previous + 1}`,
          });
        else if (item - previous > 2)
          pagesList.push({ type: "ellipsis", key: `e${item}` });
      }
      pagesList.push({ type: "page", page: item, key: `p${item}` });
      previous = item;
    }
    return pagesList;
  })();
  return (
    <div className="pag">
      <span className="pag-info">
        Showing {from}–{to} of {totalRecords}
      </span>
      <div className="pag-btns">
        <button
          className="pb"
          onClick={() => onPageChange(0)}
          disabled={page === 0}
        >
          «
        </button>
        <button
          className="pb"
          onClick={() => onPageChange(page - 1)}
          disabled={page === 0}
        >
          ‹
        </button>
        {pages.map((item) =>
          item.type === "ellipsis" ? (
            <span key={item.key} className="pb-dots">
              …
            </span>
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
        <button
          className="pb"
          onClick={() => onPageChange(page + 1)}
          disabled={page >= totalPages - 1}
        >
          ›
        </button>
        <button
          className="pb"
          onClick={() => onPageChange(totalPages - 1)}
          disabled={page >= totalPages - 1}
        >
          »
        </button>
      </div>
    </div>
  );
}

function Modal({ open, onClose, title, children, width = 480 }) {
  useEffect(() => {
    if (!open) return;
    const h = (e) => {
      if (e.key === "Escape") onClose();
    };
    document.addEventListener("keydown", h);
    return () => document.removeEventListener("keydown", h);
  }, [open, onClose]);
  if (!open) return null;
  return (
    <dialog className="m-ov" open aria-label={title}>
      <div className="m-box" style={{ maxWidth: width }}>
        <div className="m-box-inner" tabIndex={-1}>
          <div className="m-head">
            <span className="m-title">{title}</span>
            <button className="m-x" onClick={onClose} type="button">
              ✕
            </button>
          </div>
          <div className="m-body">{children}</div>
        </div>
      </div>
    </dialog>
  );
}

const CSS = `
@import url('https://fonts.googleapis.com/css2?family=Barlow:wght@400;500;600;700;800;900&display=swap');
.cr{font-family:'Barlow',sans-serif;min-height:calc(100vh - 56px);background:#f0f2f5;padding:24px}
.cr *{box-sizing:border-box}
.tgl{width:38px;height:20px;border-radius:999px;background:#ccc;border:none;position:relative;cursor:pointer;transition:background .18s;flex-shrink:0}
.tgl::after{content:"";position:absolute;top:2px;left:2px;width:16px;height:16px;background:#fff;border-radius:50%;box-shadow:0 1px 3px rgba(0,0,0,.2);transition:transform .18s}
.tgl.on{background:#005dab}
.tgl.on::after{transform:translateX(18px)}
.fi{width:100%;padding:9px 11px;font-family:'Barlow',sans-serif;font-size:14px;font-weight:500;color:#111;background:#fff;border:1.5px solid #ddd;border-radius:6px;outline:none;transition:border-color .15s,box-shadow .15s}
.fi:focus{border-color:#005dab;box-shadow:0 0 0 3px rgba(0,93,171,0.1)}
.fi.fi-err{border-color:#e31837;background:#fffafa}
.fi.fi-ro{background:#f5f5f5;color:#888;cursor:not-allowed}
.fi-multi{height:112px}
select.fi option{font-family:'Barlow',sans-serif}
.cr-wrap{max-width:1100px;margin:0 auto}
.cr-hd{display:flex;align-items:center;gap:10px;margin-bottom:18px;flex-wrap:wrap}
.cr-title{font-size:19px;font-weight:800;color:#111;letter-spacing:-0.02em;white-space:nowrap}
.cr-badge{background:#005dab;color:#fff;font-size:11px;font-weight:700;padding:2px 8px;border-radius:20px;letter-spacing:0.04em;flex-shrink:0}
.cr-search-wrap{flex:1;min-width:160px;max-width:300px;position:relative;display:flex;align-items:center;margin-left:6px}
.cr-search{width:100%;padding:7px 30px 7px 32px;font-family:'Barlow',sans-serif;font-size:13px;font-weight:500;color:#111;background:#fff;border:1.5px solid #ddd;border-radius:6px;outline:none;transition:border-color .15s,box-shadow .15s}
.cr-search:focus{border-color:#005dab;box-shadow:0 0 0 3px rgba(0,93,171,0.1)}
.cr-search::placeholder{color:#aaa;font-weight:400}
.cr-search-icon{position:absolute;left:9px;top:50%;transform:translateY(-50%);pointer-events:none;color:#bbb;font-size:15px;line-height:1}
.cr-search-clear{position:absolute;right:8px;top:50%;transform:translateY(-50%);background:none;border:none;cursor:pointer;color:#bbb;font-size:12px;line-height:1;padding:2px;display:flex;align-items:center;justify-content:center;border-radius:3px;transition:color .13s,background .13s}
.cr-search-clear:hover{color:#555;background:#f0f0f0}
.btn-back{width:32px;height:32px;border-radius:6px;border:1.5px solid #ddd;background:#fff;cursor:pointer;display:flex;align-items:center;justify-content:center;font-size:14px;color:#555;transition:background .13s;text-decoration:none;font-weight:700;flex-shrink:0}
.btn-back:hover{background:#e8f0fa;border-color:#005dab;color:#005dab}
.btn-add{margin-left:auto;padding:7px 16px;background:#e31837;border:none;border-radius:6px;color:#fff;font-family:'Barlow',sans-serif;font-size:12px;font-weight:800;text-transform:uppercase;letter-spacing:0.04em;cursor:pointer;transition:background .15s;white-space:nowrap;flex-shrink:0}
.btn-add:hover{background:#c0152a}
.tbl-card{background:#fff;border:1px solid #e0e0e0;border-radius:10px;overflow:hidden;box-shadow:0 1px 4px rgba(0,0,0,.04)}
.tbl-empty{padding:48px;text-align:center;color:#bbb;font-size:14px;font-weight:500}
table{width:100%;border-collapse:collapse;font-size:13px}
thead tr{background:#005dab}
th{padding:10px 14px;text-align:left;font-size:10px;font-weight:700;color:rgba(255,255,255,0.85);text-transform:uppercase;letter-spacing:0.08em;white-space:nowrap}
td{padding:10px 14px;color:#333;border-bottom:1px solid #f0f0f0;vertical-align:middle;font-weight:500}
tbody tr:last-child td{border-bottom:none}
tbody tr:hover{background:#f8f9fb}
.badge{display:inline-block;padding:2px 7px;border-radius:4px;font-size:10px;font-weight:700;background:#e8f0fa;color:#005dab;text-transform:uppercase;letter-spacing:0.04em}
.btn-edit{padding:4px 11px;border-radius:5px;border:1.5px solid #005dab;background:#fff;color:#005dab;font-size:11px;font-weight:700;cursor:pointer;font-family:'Barlow',sans-serif;transition:all .13s;text-transform:uppercase;letter-spacing:0.03em}
.btn-edit:hover{background:#005dab;color:#fff}
.btn-del{padding:4px 11px;border-radius:5px;border:1.5px solid #e31837;background:#fff;color:#e31837;font-size:11px;font-weight:700;cursor:pointer;font-family:'Barlow',sans-serif;transition:all .13s;text-transform:uppercase;letter-spacing:0.03em}
.btn-del:hover{background:#e31837;color:#fff}
.act-wrap{display:flex;gap:6px;justify-content:flex-end}
.pag{display:flex;align-items:center;justify-content:space-between;flex-wrap:wrap;gap:10px;padding:10px 14px;border-top:1px solid #f0f0f0}
.pag-info{font-size:11px;color:#aaa;font-weight:600}
.pag-btns{display:flex;align-items:center;gap:3px}
.pb{min-width:30px;height:30px;padding:0 5px;display:inline-flex;align-items:center;justify-content:center;border-radius:5px;border:1.5px solid #e0e0e0;background:#fff;color:#555;font-size:12px;font-weight:700;cursor:pointer;transition:all .13s;font-family:'Barlow',sans-serif}
.pb:hover:not(:disabled){background:#f0f2f5;border-color:#bbb;color:#111}
.pb.pb-a{background:#005dab;border-color:#005dab;color:#fff}
.pb:disabled{opacity:.35;cursor:not-allowed}
.pb-dots{min-width:30px;height:30px;display:inline-flex;align-items:center;justify-content:center;color:#bbb;font-size:12px}
.m-ov{position:fixed;inset:0;width:100vw;height:100vh;margin:0;padding:20px;display:flex;align-items:center;justify-content:center;background:rgba(0,0,0,.45);border:none;z-index:100;overflow:auto;backdrop-filter:blur(2px)}
.m-box{width:100%;background:#fff;border-radius:10px;box-shadow:0 8px 40px rgba(0,0,0,.16);overflow:hidden;animation:mIn .17s ease}
@keyframes mIn{from{opacity:0;transform:scale(.97) translateY(6px)}to{opacity:1;transform:none}}
.m-head{display:flex;align-items:center;justify-content:space-between;padding:13px 18px;border-bottom:2px solid #f0f0f0;background:#005dab}
.m-title{font-size:13px;font-weight:800;color:#fff;text-transform:uppercase;letter-spacing:0.06em}
.m-x{width:26px;height:26px;border-radius:5px;border:none;background:rgba(255,255,255,0.2);color:#fff;font-size:11px;cursor:pointer;display:flex;align-items:center;justify-content:center;transition:background .13s;font-weight:700}
.m-x:hover{background:rgba(255,255,255,0.35)}
.m-body{padding:18px;max-height:72vh;overflow-y:auto}
.m-alert{padding:9px 12px;background:#fff0f3;border:1px solid #fbbcca;border-radius:6px;font-size:13px;color:#c0152a;margin-bottom:14px;font-weight:600;display:flex;align-items:flex-start;gap:7px}
.m-alert::before{content:'!';width:16px;height:16px;border-radius:50%;background:#e31837;color:#fff;display:inline-flex;align-items:center;justify-content:center;font-size:10px;font-weight:900;flex-shrink:0;margin-top:1px}
.m-field{display:flex;flex-direction:column;gap:4px;margin-bottom:13px}
.m-label{font-size:10px;font-weight:700;color:#444;text-transform:uppercase;letter-spacing:0.09em}
.m-req{color:#e31837;margin-left:2px}
.m-hint{font-size:11px;color:#aaa;margin-top:2px;font-weight:500}
.btn-save{width:100%;padding:10px;background:#e31837;border:none;border-radius:6px;color:#fff;font-family:'Barlow',sans-serif;font-size:13px;font-weight:800;text-transform:uppercase;letter-spacing:0.04em;cursor:pointer;margin-top:4px;transition:background .15s}
.btn-save:hover:not(:disabled){background:#c0152a}
.btn-save:disabled{opacity:.45;cursor:not-allowed}
.del-body{padding:18px}
.del-msg{font-size:14px;color:#555;line-height:1.7;margin-bottom:18px;font-weight:500}
.del-msg strong{color:#111;font-weight:800}
.del-warn{padding:9px 12px;background:#fff8e1;border:1px solid #ffe082;border-radius:6px;font-size:12px;color:#b45309;margin-bottom:14px;font-weight:600}
.del-actions{display:flex;gap:8px;justify-content:flex-end}
.btn-cancel{padding:7px 16px;border-radius:6px;border:1.5px solid #ddd;background:#fff;color:#555;font-family:'Barlow',sans-serif;font-size:12px;font-weight:700;cursor:pointer;transition:background .13s;text-transform:uppercase}
.btn-cancel:hover{background:#f0f2f5}
.btn-confirm-del{padding:7px 16px;border-radius:6px;border:none;background:#e31837;color:#fff;font-family:'Barlow',sans-serif;font-size:12px;font-weight:800;cursor:pointer;transition:background .15s;text-transform:uppercase;letter-spacing:0.04em}
.btn-confirm-del:hover:not(:disabled){background:#c0152a}
.btn-confirm-del:disabled{opacity:.5;cursor:not-allowed}
.pg-err{padding:10px 14px;background:#fff0f3;border:1px solid #fbbcca;border-radius:6px;font-size:13px;color:#c0152a;margin-bottom:14px;font-weight:600}
`;

function recordToSearchString(record, listFields) {
  return listFields
    .map((f) => {
      const v = record[f.key];
      if (v === null || v === undefined) return "";
      if (Array.isArray(v)) return v.join(" ");
      return String(v);
    })
    .join(" ")
    .toLowerCase();
}

export default function CrudPage({ config }) {
  const {
    title,
    singularTitle,
    listEndpoint,
    getEndpoint,
    saveEndpoint,
    updateEndpoint,
    deleteEndpoint,
    toggleEndpoint,
    toggleField = "active",
    idKey = "identifier",
    fields,
    loadOptions,
    currencyFields = [],
    beforeDelete,
    showToggle,
    homeUrl,
    pageSize = 2,
    onDeleteSelf,
    getCurrentUserId,
    getRecord,
    customUpdateRecord,
  } = config;

  const [allRecords, setAllRecords] = useState([]);
  const [form, setForm] = useState({});
  const [modal, setModal] = useState(null);
  const [editId, setEditId] = useState(null);
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [saving, setSaving] = useState(false);
  const [listLoading, setListLoading] = useState(false);
  const [error, setError] = useState("");
  const [fieldErrors, setFieldErrors] = useState({});
  const [page, setPage] = useState(0);
  const [dynOptions, setDynOptions] = useState({});
  const [searchQuery, setSearchQuery] = useState("");
  const searchRef = useRef(null);
  const togglingRef = useRef(new Set());

  useEffect(() => {
    loadOptions?.()
      .then(setDynOptions)
      .catch(() => {});
  }, []);

  const resolvedFields = fields.map((f) =>
    dynOptions[f.key] ? { ...f, options: dynOptions[f.key] } : f,
  );

  const listFields = resolvedFields.filter((f) => !f.hideInList);

  const fetchList = useCallback(async () => {
    setListLoading(true);
    setError("");
    try {
      const data = await fetchWithAuth(listEndpoint, {
        method: "POST",
        body: JSON.stringify({ page: 0, sizePerPage: 10000 }),
      });
      const dataList = Array.isArray(data) ? data : (data?.dtoList ?? []);
      const multiKeys = new Set(
        fields.filter((f) => f.multiple).map((f) => f.key),
      );
      const rows = dataList.map((record) => {
        const out = { ...record };
        for (const key of multiKeys) {
          if (typeof out[key] === "string") {
            out[key] = out[key]
              .split(",")
              .map((s) => s.trim())
              .filter(Boolean);
          } else if (!Array.isArray(out[key])) {
            out[key] = [];
          }
        }
        return out;
      });
      setAllRecords(rows);
    } catch (e) {
      if (e.message !== "Unauthorized") setError(e.message || "Failed to load records.");
    } finally {
      setListLoading(false);
    }
  }, [listEndpoint, fields]);

  useEffect(() => {
    fetchList();
  }, [fetchList]);

  const filteredRecords = useMemo(() => {
    const q = searchQuery.trim().toLowerCase();
    if (!q) return allRecords;
    return allRecords.filter((r) =>
      recordToSearchString(r, listFields).includes(q),
    );
  }, [allRecords, searchQuery, listFields]);

  const totalRecords = filteredRecords.length;
  const totalPages = Math.max(1, Math.ceil(totalRecords / pageSize));
  const safePage = Math.min(page, totalPages - 1);
  const pageRecords = filteredRecords.slice(
    safePage * pageSize,
    (safePage + 1) * pageSize,
  );

  useEffect(() => {
    setPage(0);
  }, [searchQuery]);

  const handlePageChange = (p) => setPage(p);

  const closeModal = () => {
    setModal(null);
    setError("");
    setFieldErrors({});
    setSaving(false);
  };

  const openAdd = async () => {
    const opts = await loadOptions?.().catch(() => ({}));
    if (opts) setDynOptions(opts);
    const defaults = {};
    fields.forEach((f) => {
      if (f.type === "select" && f.multiple) defaults[f.key] = [];
      else if (f.type === "select") defaults[f.key] = "";
    });
    setForm(defaults);
    setEditId(null);
    setError("");
    setFieldErrors({});
    setModal("add");
  };

  const openEdit = async (record) => {
    setError("");
    setFieldErrors({});
    try {
      const [data, opts] = await Promise.all([
        getRecord
          ? getRecord(record)
          : fetchWithAuth(getEndpoint(record[idKey])),
        loadOptions?.() ?? Promise.resolve({}),
      ]);
      if (opts) setDynOptions(opts);
      const multiKeys = new Set(
        fields.filter((f) => f.multiple).map((f) => f.key),
      );
      const normalized = {};
      for (const [k, v] of Object.entries(data ?? {})) {
        if (multiKeys.has(k)) {
          if (typeof v === "string") {
            normalized[k] = v
              .split(",")
              .map((s) => s.trim())
              .filter(Boolean);
          } else if (Array.isArray(v)) {
            normalized[k] = v.map((item) =>
              typeof item === "object"
                ? (item.identifier ?? item.name ?? item.value ?? String(item))
                : String(item),
            );
          } else {
            normalized[k] = [];
          }
        } else if (Array.isArray(v)) {
          normalized[k] = v[0] ?? "";
        } else {
          normalized[k] = v;
        }
      }
      setForm(normalized);
      setEditId(record[idKey]);
      setModal("edit");
    } catch (e) {
      if (e.message !== "Unauthorized") setError(e.message || "Failed to load record.");
    }
  };

  const openDelete = (record) => {
    setDeleteTarget(record);
    setModal("delete");
  };

  const handleFieldChange = (key, value) => {
    setForm((f) => ({ ...f, [key]: value }));
    if (fieldErrors[key]) setFieldErrors((e) => ({ ...e, [key]: false }));
    setError("");
  };

  const buildFieldErrors = (activeFields) => {
    const errs = {};
    activeFields.forEach((f) => {
      if (validateField(f, form[f.key])) errs[f.key] = true;
    });
    return errs;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const vErr = validateForm(resolvedFields, form, modal);
    if (vErr) {
      setError(vErr);
      const activeFields = resolvedFields.filter(
        (f) => !f.hideInForm && !(modal === "edit" && f.hideOnEdit),
      );
      setFieldErrors(buildFieldErrors(activeFields));
      return;
    }
    setError("");
    setFieldErrors({});
    setSaving(true);

    const singleSelectKeys = new Set(
      fields
        .filter((f) => f.type === "select" && !f.multiple)
        .map((f) => f.key),
    );
    const payload = Object.fromEntries(
      Object.entries(form).map(([k, v]) => [
        k,
        singleSelectKeys.has(k) && v === "" ? null : v,
      ]),
    );

    try {
      let response;

      if (modal === "add") {
        response = await fetchWithAuth(saveEndpoint, {
          method: "POST",
          body: JSON.stringify(payload),
        });
      } else if (customUpdateRecord) {
        response = await customUpdateRecord(editId, payload);
      } else {
        response = await fetchWithAuth(updateEndpoint(editId), {
          method: "PUT",
          body: JSON.stringify(payload),
        });
      }

      if (response?.success === false) {
        setError(response.message || "Operation failed.");
        return;
      }

      closeModal();
      fetchList();
    } catch (e) {
      setError(e.message || "Save failed. Please try again.");
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    if (beforeDelete) {
      const errMsg = await beforeDelete(deleteTarget);
      if (errMsg) {
        alert(errMsg);
        closeModal();
        return;
      }
    }
    setSaving(true);
    try {
      await fetchWithAuth(deleteEndpoint(deleteTarget[idKey]), {
        method: "DELETE",
        body: JSON.stringify({}),
      });
      if (onDeleteSelf && getCurrentUserId?.() === deleteTarget[idKey]) {
        onDeleteSelf(deleteTarget);
        return;
      }
      closeModal();
      fetchList();
    } catch (e) {
      setError(e.message || "Delete failed.");
    } finally {
      setSaving(false);
    }
  };

  const extractUpdatedRecord = (response) => {
    if (response.data && typeof response.data === "object") {
      return response.data;
    }
    if (response[idKey]) {
      return response;
    }
    if (response.id || response.identifier) {
      return response;
    }
    return null;
  };

  const handleToggleResponse = async (recordId, response) => {
    const updatedRecord = extractUpdatedRecord(response);

    if (updatedRecord) {
      setAllRecords((prev) =>
        prev.map((r) => (r[idKey] === recordId ? updatedRecord : r)),
      );
      return;
    }

    const isSuccessIndicator =
      typeof response === "boolean" || response === true || response === null;
    if (isSuccessIndicator && getEndpoint) {
      try {
        const refetchedRecord = await fetchWithAuth(getEndpoint(recordId));
        if (refetchedRecord) {
          setAllRecords((prev) =>
            prev.map((r) => (r[idKey] === recordId ? refetchedRecord : r)),
          );
        }
      } catch {
      }
    }
  };

  const getToggleFieldName = (record) => {
    let fieldName = toggleField;
    if (!Object.hasOwn(record, fieldName)) {
      fieldName = Object.hasOwn(record, "active")
        ? "active"
        : "status";
    }
    return fieldName;
  };

  const handleToggle = async (record) => {
    if (!toggleEndpoint) return;

    const recordId = record[idKey];
    if (togglingRef.current.has(recordId)) return;

    togglingRef.current.add(recordId);
    const originalRecord = { ...record };
    const toggleFieldName = getToggleFieldName(record);
    const currentValue = record[toggleFieldName];

    setAllRecords((prev) =>
      prev.map((r) =>
        r[idKey] === recordId ? { ...r, [toggleFieldName]: !currentValue } : r,
      ),
    );

    try {
      const response = await fetchWithAuth(toggleEndpoint(recordId), {
        method: "POST",
        body: JSON.stringify({}),
      });

      if (response) {
        await handleToggleResponse(recordId, response);
      }
    } catch (e) {
      setAllRecords((prev) =>
        prev.map((r) => (r[idKey] === recordId ? originalRecord : r)),
      );
      setError(e.message || "Failed to toggle. You may not have permission to perform this action.");
    } finally {
      togglingRef.current.delete(recordId);
    }
  };

  const isSelfDelete =
    !!onDeleteSelf && getCurrentUserId?.() === deleteTarget?.[idKey];

  const formFields = resolvedFields.filter(
    (f) => !f.hideInForm && !(modal === "edit" && f.hideOnEdit),
  );

  let saveButtonText;
  if (saving) saveButtonText = "Saving…";
  else if (modal === "add") saveButtonText = `Add ${singularTitle ?? ""}`;
  else saveButtonText = `Update ${singularTitle ?? ""}`;

  let tableBody;
  if (listLoading) {
    tableBody = <div className="tbl-empty">Loading…</div>;
  } else if (pageRecords.length === 0) {
    tableBody = (
      <div className="tbl-empty">
        {searchQuery.trim()
          ? `No results for "${searchQuery}".`
          : `No ${title.toLowerCase()} found.`}
      </div>
    );
  } else {
    tableBody = (
      <div style={{ overflowX: "auto" }}>
        <table>
          <thead>
            <tr>
              {listFields.map((f) => (
                <th key={f.key}>{f.label}</th>
              ))}
              {toggleEndpoint && <th>Status</th>}
              <th style={{ textAlign: "right" }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {pageRecords.map((record) => (
              <tr key={record[idKey] ?? record.identifier ?? record.id}>
                {listFields.map((f) => (
                  <td key={f.key}>
                    {renderCellValue(record[f.key], f, currencyFields, idKey)}
                  </td>
                ))}
                {toggleEndpoint && (
                  <td>
                    {!showToggle || showToggle(record) ? (
                      <Toggle
                        active={!!record[getToggleFieldName(record)]}
                        onChange={() => handleToggle(record)}
                      />
                    ) : (
                      <span style={{ color: "#ccc" }}>—</span>
                    )}
                  </td>
                )}
                <td>
                  <div className="act-wrap">
                    <button
                      className="btn-edit"
                      onClick={() => openEdit(record)}
                    >
                      Edit
                    </button>
                    <button
                      className="btn-del"
                      onClick={() => openDelete(record)}
                    >
                      Delete
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        <Pagination
          page={safePage}
          totalPages={totalPages}
          totalRecords={totalRecords}
          pageSize={pageSize}
          onPageChange={handlePageChange}
        />
      </div>
    );
  }

  return (
    <>
      <style>{CSS}</style>
      <div className="cr">
        <div className="cr-wrap">
          <div className="cr-hd">
            {homeUrl && (
              <a href={homeUrl} className="btn-back">
                ←
              </a>
            )}
            <h1 className="cr-title">{title}</h1>
            {allRecords.length > 0 && (
              <span className="cr-badge">{allRecords.length}</span>
            )}
            <div className="cr-search-wrap">
              <span className="cr-search-icon" aria-hidden="true">
                <svg
                  width="14"
                  height="14"
                  viewBox="0 0 16 16"
                  fill="none"
                  xmlns="http://www.w3.org/2000/svg"
                >
                  <circle
                    cx="6.5"
                    cy="6.5"
                    r="5"
                    stroke="currentColor"
                    strokeWidth="1.8"
                  />
                  <path
                    d="M10.5 10.5L14 14"
                    stroke="currentColor"
                    strokeWidth="1.8"
                    strokeLinecap="round"
                  />
                </svg>
              </span>
              <input
                ref={searchRef}
                type="text"
                className="cr-search"
                placeholder={`Search ${title.toLowerCase()}…`}
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                aria-label={`Search ${title}`}
              />
              {searchQuery && (
                <button
                  className="cr-search-clear"
                  onClick={() => {
                    setSearchQuery("");
                    searchRef.current?.focus();
                  }}
                  type="button"
                  aria-label="Clear search"
                >
                  ✕
                </button>
              )}
            </div>
            <button className="btn-add" onClick={openAdd}>
              + Add {singularTitle ?? ""}
            </button>
          </div>
          {error && !modal && (
            <div className="pg-err" role="alert">
              {error}
            </div>
          )}
          <div className="tbl-card">{tableBody}</div>
        </div>
      </div>

      <Modal
        open={modal === "add" || modal === "edit"}
        onClose={closeModal}
        title={
          modal === "add"
            ? `Add ${singularTitle ?? title}`
            : `Edit ${singularTitle ?? title}`
        }
      >
        {error && (
          <div className="m-alert" role="alert">
            {error}
          </div>
        )}
        <form onSubmit={handleSubmit} noValidate>
          {formFields.map((field) => {
            const ro = modal === "edit" && field.readOnlyOnEdit;
            return (
              <div className="m-field" key={field.key}>
                <label className="m-label">
                  {field.label}
                  {field.required && <span className="m-req">*</span>}
                </label>
                <FieldInput
                  field={field}
                  value={form[field.key]}
                  onChange={handleFieldChange}
                  readOnly={ro}
                  hasError={!!fieldErrors[field.key]}
                />
                {field.type === "select" && field.multiple && (
                  <p className="m-hint">Hold Ctrl / Cmd to select multiple</p>
                )}
              </div>
            );
          })}
          {modal === "edit" && (
            <div style={{ marginTop: 18, paddingTop: 14, borderTop: "1px solid #e8e8e8" }}>
              <p style={{ fontSize: 10, fontWeight: 700, color: "#999", textTransform: "uppercase", letterSpacing: "0.06em", marginBottom: 9 }}>Audit Information</p>
              <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 9, fontSize: 12, color: "#666" }}>
                {form.createdBy && (
                  <div>
                    <p style={{ fontSize: 9, color: "#aaa", fontWeight: 600, marginBottom: 2 }}>Created By</p>
                    <p style={{ color: "#333", fontWeight: 500 }}>{form.createdBy}</p>
                  </div>
                )}
                {form.createdAt && (
                  <div>
                    <p style={{ fontSize: 9, color: "#aaa", fontWeight: 600, marginBottom: 2 }}>Created At</p>
                    <p style={{ color: "#333", fontWeight: 500 }}>{new Date(form.createdAt).toLocaleString("en-IN")}</p>
                  </div>
                )}
                {form.modifiedBy && (
                  <div>
                    <p style={{ fontSize: 9, color: "#aaa", fontWeight: 600, marginBottom: 2 }}>Modified By</p>
                    <p style={{ color: "#333", fontWeight: 500 }}>{form.modifiedBy}</p>
                  </div>
                )}
                {form.modifiedAt && (
                  <div>
                    <p style={{ fontSize: 9, color: "#aaa", fontWeight: 600, marginBottom: 2 }}>Modified At</p>
                    <p style={{ color: "#333", fontWeight: 500 }}>{new Date(form.modifiedAt).toLocaleString("en-IN")}</p>
                  </div>
                )}
              </div>
            </div>
          )}
          <button type="submit" className="btn-save" disabled={saving}>
            {saveButtonText}
          </button>
        </form>
      </Modal>

      <Modal
        open={modal === "delete"}
        onClose={closeModal}
        title={`Delete ${singularTitle ?? "Record"}`}
        width={420}
      >
        <div className="del-body">
          <p className="del-msg">
            Are you sure you want to delete{" "}
            <strong>{deleteTarget?.[idKey]}</strong>? This action cannot be
            undone.
          </p>
          {isSelfDelete && (
            <div className="del-warn">
              ⚠ You are deleting your own account. You will be logged out
              immediately.
            </div>
          )}
          {error && (
            <div className="pg-err" role="alert" style={{ marginBottom: 12 }}>
              {error}
            </div>
          )}
          <div className="del-actions">
            <button className="btn-cancel" onClick={closeModal} type="button">
              Cancel
            </button>
            <button
              className="btn-confirm-del"
              onClick={handleDelete}
              disabled={saving}
              type="button"
            >
              {saving ? "Deleting…" : "Delete"}
            </button>
          </div>
        </div>
      </Modal>
    </>
  );
}

CrudPage.propTypes = {
  config: PropTypes.shape({
    title: PropTypes.string.isRequired,
    singularTitle: PropTypes.string,
    listEndpoint: PropTypes.string.isRequired,
    getEndpoint: PropTypes.func.isRequired,
    saveEndpoint: PropTypes.string.isRequired,
    updateEndpoint: PropTypes.func.isRequired,
    deleteEndpoint: PropTypes.func.isRequired,
    toggleEndpoint: PropTypes.func,
    toggleField: PropTypes.string,
    idKey: PropTypes.string,
    fields: PropTypes.arrayOf(
      PropTypes.shape({
        key: PropTypes.string.isRequired,
        label: PropTypes.string.isRequired,
        type: PropTypes.string,
        required: PropTypes.bool,
        multiple: PropTypes.bool,
        hideInList: PropTypes.bool,
        hideInForm: PropTypes.bool,
        hideOnEdit: PropTypes.bool,
        readOnlyOnEdit: PropTypes.bool,
        placeholder: PropTypes.string,
        options: PropTypes.array,
        maxLength: PropTypes.number,
        validate: PropTypes.func,
      }),
    ).isRequired,
    loadOptions: PropTypes.func,
    currencyFields: PropTypes.arrayOf(PropTypes.string),
    beforeDelete: PropTypes.func,
    showToggle: PropTypes.func,
    homeUrl: PropTypes.string,
    pageSize: PropTypes.number,
    onDeleteSelf: PropTypes.func,
    getCurrentUserId: PropTypes.func,
    getRecord: PropTypes.func,
    customUpdateRecord: PropTypes.func,
  }).isRequired,
};

FieldInput.propTypes = {
  field: PropTypes.shape({
    key: PropTypes.string.isRequired,
    label: PropTypes.string,
    type: PropTypes.string,
    placeholder: PropTypes.string,
    multiple: PropTypes.bool,
    options: PropTypes.arrayOf(
      PropTypes.shape({ value: PropTypes.string, label: PropTypes.string }),
    ),
    maxLength: PropTypes.number,
    hideInForm: PropTypes.bool,
    hideOnEdit: PropTypes.bool,
    readOnlyOnEdit: PropTypes.bool,
    required: PropTypes.bool,
    validate: PropTypes.func,
  }).isRequired,
  value: PropTypes.oneOfType([
    PropTypes.string,
    PropTypes.number,
    PropTypes.array,
  ]),
  onChange: PropTypes.func.isRequired,
  readOnly: PropTypes.bool,
  hasError: PropTypes.bool,
};

FieldInput.defaultProps = {
  value: "",
  readOnly: false,
  hasError: false,
};

Pagination.propTypes = {
  page: PropTypes.number.isRequired,
  totalPages: PropTypes.number.isRequired,
  onPageChange: PropTypes.func.isRequired,
  totalRecords: PropTypes.number.isRequired,
  pageSize: PropTypes.number.isRequired,
};

Modal.propTypes = {
  open: PropTypes.bool.isRequired,
  onClose: PropTypes.func.isRequired,
  title: PropTypes.string.isRequired,
  children: PropTypes.node.isRequired,
  width: PropTypes.number,
};

Modal.defaultProps = {
  width: 480,
};