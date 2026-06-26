"use client";

import React, { useState, useRef, useEffect } from "react";
import PropTypes from "prop-types";
import AuditSection from "../../components/AuditSection";

function PartyTypeSelect({ onChange, value }) {
  const [selected, setSelected] = useState(
    value ? value.split(",").map((v) => v.trim()).filter(Boolean) : []
  );
  const [open, setOpen] = useState(false);
  const ref = useRef(null);

  const options = [
    { value: "customer", label: "Customer" },
    { value: "dealer", label: "Dealer" },
    { value: "wholesaler", label: "Wholesaler" },
  ];

  useEffect(() => {
    if (value !== undefined) {
      setSelected(value ? value.split(",").map((v) => v.trim()).filter(Boolean) : []);
    }
  }, [value]);

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (ref.current && !ref.current.contains(e.target)) setOpen(false);
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const toggleOption = (val) => {
    const updated = selected.includes(val)
      ? selected.filter((v) => v !== val)
      : [...selected, val];
    setSelected(updated);
    onChange(updated.join(","));
  };

  const displayText =
    selected.length === 0
      ? "Select Party Type"
      : options
          .filter((o) => selected.includes(o.value))
          .map((o) => o.label)
          .join(", ");

  return (
    <div ref={ref} style={{ position: "relative" }}>
      <button
        type="button"
        onClick={() => setOpen((p) => !p)}
        style={{
          width: "100%",
          border: "1px solid #d1d5db",
          borderRadius: 6,
          padding: "10px 14px",
          fontSize: 14,
          backgroundColor: "#fff",
          cursor: "pointer",
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          boxSizing: "border-box",
          userSelect: "none",
        }}
      >
        <span style={{ color: selected.length === 0 ? "#9ca3af" : "#111827" }}>
          {displayText}
        </span>
        <span style={{ fontSize: 10, color: "#6b7280" }}>{open ? "▲" : "▼"}</span>
      </button>

      {open && (
        <div
          style={{
            position: "absolute",
            top: "calc(100% + 4px)",
            left: 0,
            right: 0,
            backgroundColor: "#fff",
            border: "1px solid #d1d5db",
            borderRadius: 6,
            boxShadow: "0 4px 12px rgba(0,0,0,0.1)",
            zIndex: 1100,
            overflow: "hidden",
          }}
        >
          {options.map((opt) => {
            const isSelected = selected.includes(opt.value);
            return (
              <button
                key={opt.value}
                type="button"
                aria-pressed={isSelected}
                onClick={() => toggleOption(opt.value)}
                style={{
                  padding: "10px 14px",
                  fontSize: 14,
                  cursor: "pointer",
                  textAlign: "left",
                  display: "flex",
                  alignItems: "center",
                  gap: 10,
                  width: "100%",
                  backgroundColor: isSelected ? "#f0fdf4" : "#fff",
                  color: isSelected ? "#16a34a" : "#111827",
                  border: "none",
                  borderBottom: "1px solid #f3f4f6",
                }}
              >
                <span
                  style={{
                    width: 16,
                    height: 16,
                    borderRadius: 4,
                    border: `2px solid ${isSelected ? "#16a34a" : "#d1d5db"}`,
                    backgroundColor: isSelected ? "#16a34a" : "#fff",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    flexShrink: 0,
                  }}
                >
                  {isSelected && (
                    <span style={{ color: "#fff", fontSize: 10, lineHeight: 1 }}>✓</span>
                  )}
                </span>
                {opt.label}
              </button>
            );
          })}
        </div>
      )}
    </div>
  );
}

PartyTypeSelect.propTypes = {
  onChange: PropTypes.func.isRequired,
  value: PropTypes.string,
};


const inputStyle = {
  width: "100%",
  border: "1px solid #d1d5db",
  borderRadius: 6,
  padding: "10px 14px",
  fontSize: 14,
  outline: "none",
  boxSizing: "border-box",
};

const readonlyInputStyle = {
  ...inputStyle,
  backgroundColor: "#f3f4f6",
  color: "#6b7280",
  cursor: "not-allowed",
};

function Field({ label, children }) {
  const htmlFor = React.isValidElement(children) ? children.props?.id : undefined;
  return (
    <div style={{ display: "flex", flexDirection: "column", gap: 6 }}>
      <label htmlFor={htmlFor} style={{ fontWeight: "bold", fontSize: 13, color: "#374151" }}>
        {label}
      </label>
      {children}
    </div>
  );
}

Field.propTypes = {
  label: PropTypes.string.isRequired,
  children: PropTypes.node,
};

function Row({ children }) {
  return (
    <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 12 }}>
      {children}
    </div>
  );
}

Row.propTypes = {
  children: PropTypes.node,
};

function SectionTitle({ title }) {
  return (
    <div style={{ margin: "12px 0 4px" }}>
      <h4 style={{ fontSize: 13, fontWeight: 700, color: "#111827", margin: "0 0 4px" }}>
        {title}
      </h4>
      <hr style={{ border: "none", borderTop: "1px solid #e5e7eb", margin: 0 }} />
    </div>
  );
}

SectionTitle.propTypes = {
  title: PropTypes.string.isRequired,
};

const validatePhoneNumber = (phoneStr) => {
  if (!/^\d{10}$/.test(phoneStr)) return "Phone number must be exactly 10 digits.";
  return "";
};

const validateZipcode = (zipcode, label) => {
  if (zipcode && !/^\d+$/.test(zipcode)) return `${label} zipcode must be numeric.`;
  return "";
};

function getAddressPayload(getVal, prefix) {
  const zipcode = getVal(`${prefix}.zipcode`);
  return {
    addressLine: getVal(`${prefix}.addressLine`),
    city: getVal(`${prefix}.city`),
    state: getVal(`${prefix}.state`),
    zipcode: zipcode ? Number(zipcode) : 0,
    country: getVal(`${prefix}.country`),
  };
}

function extractCustomer(data) {
  return data?.dtoList ? data.dtoList[0] : data;
}

function createAuditData(customer) {
  return {
    createdBy: customer.createdBy || "-",
    createdOn: customer.createdOn ? new Date(customer.createdOn).toLocaleString() : "-",
    modifiedBy: customer.modifiedBy || "-",
    modifiedOn: customer.modifiedOn ? new Date(customer.modifiedOn).toLocaleString() : "-",
  };
}

function getSubmitLabel(isEdit, saving) {
  if (saving) {
    return "Saving…";
  }
  if (isEdit) {
    return "Update Customer";
  }
  return "Add Customer";
}

function AddressSection({ prefix, idPrefix, values }) {
  const v = values ?? {};
  return (
    <>
      <SectionTitle title={prefix === "shipping" ? "Shipping Address" : "Billing Address"} />
      <Row>
        <Field label="Address Line">
          <input
            id={idPrefix + prefix + ".addressLine"}
            type="text"
            placeholder="Enter address"
            defaultValue={v.addressLine || ""}
            style={inputStyle}
          />
        </Field>
        <Field label="City">
          <input
            id={idPrefix + prefix + ".city"}
            type="text"
            placeholder="Enter city"
            defaultValue={v.city || ""}
            style={inputStyle}
          />
        </Field>
      </Row>
      <Row>
        <Field label="State">
          <input
            id={idPrefix + prefix + ".state"}
            type="text"
            placeholder="Enter state"
            defaultValue={v.state || ""}
            style={inputStyle}
          />
        </Field>
        <Field label="Zipcode">
          <input
            id={idPrefix + prefix + ".zipcode"}
            type="text"
            inputMode="numeric"
            placeholder="Enter zipcode"
            defaultValue={v.zipcode || ""}
            style={inputStyle}
          />
        </Field>
      </Row>
      <Row>
        <Field label="Country">
          <input
            id={idPrefix + prefix + ".country"}
            type="text"
            placeholder="Enter country"
            defaultValue={v.country || ""}
            style={inputStyle}
          />
        </Field>
        <div />
      </Row>
    </>
  );
}

AddressSection.propTypes = {
  prefix: PropTypes.string.isRequired,
  idPrefix: PropTypes.string.isRequired,
  values: PropTypes.object,
};

function CustomerForm({
  identifier,
  isEdit = false,
  onSuccess,
  onCancel,
  idPrefix = "",
  simple = false,
}) {

  const [partyType, setPartyType] = useState("");
  const [prefillData, setPrefillData] = useState(null); 
  const [auditData, setAuditData] = useState({});
  const [loadError, setLoadError] = useState("");
  const [error, setError] = useState("");
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (!isEdit || !identifier) {
      setPrefillData({});
      return;
    }
    const token = localStorage.getItem("token");
    if (!token) {
      setLoadError("Not logged in.");
      setPrefillData({});
      return;
    }

    fetch(`http://localhost:8080/api/customer/get?identifier=${identifier}`, {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then(async (res) => {
        if (!res.ok) throw new Error(`Server error (${res.status})`);
        return res.json();
      })
      .then((data) => {
        const customer = extractCustomer(data);
        setPartyType(customer.partyType || "");
        setPrefillData(customer);
        setAuditData(createAuditData(customer));
      })
      .catch((err) => {
        setLoadError("Failed to load customer: " + err.message);
        setPrefillData({});
      });
  }, [isEdit, identifier]);

  const getVal = (id) =>
    document.getElementById(idPrefix + id)?.value?.trim() || "";

  const validateAndBuildPayload = () => {
    const token = localStorage.getItem("token");
    if (!token) return { error: "Not logged in." };

    const phoneStr = isEdit
      ? String(prefillData?.phoneNo || getVal("phoneNo"))
      : getVal("phoneNo");

    if (!isEdit) {
      const phoneErr = validatePhoneNumber(phoneStr);
      if (phoneErr) return { error: phoneErr };
    }

    const shipZip = getVal("shipping.zipcode");
    const shipErr = validateZipcode(shipZip, "Shipping");
    if (shipErr) return { error: shipErr };

    const billZip = getVal("billing.zipcode");
    const billErr = validateZipcode(billZip, "Billing");
    if (billErr) return { error: billErr };

    const payload = {
      name: getVal("name"),
      phoneNo: Number(phoneStr),
      identifier: isEdit ? identifier : phoneStr,
      partyType,
      balance: getVal("balance") ? Number(getVal("balance")) : 0,
      creditLimit: getVal("creditLimit") ? Number(getVal("creditLimit")) : 0,
      shippingAddress: getAddressPayload(getVal, "shipping"),
      billingAddress: getAddressPayload(getVal, "billing"),
    };

    return { token, payload, phoneStr };
  };

  const getRequestConfig = () => ({
    url: isEdit
      ? "http://localhost:8080/api/customer/update"
      : "http://localhost:8080/api/customer/add",
    method: isEdit ? "PUT" : "POST",
  });

  const parseResponse = async (res) => {
    const text = await res.text();
    try {
      return { data: JSON.parse(text), text };
    } catch {
      return { data: null, text };
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    const validated = validateAndBuildPayload();
    if (validated.error) {
      setError(validated.error);
      return;
    }

    const { token, payload, phoneStr } = validated;
    const { url, method } = getRequestConfig();

    try {
      setSaving(true);
      const res = await fetch(url, {
        method,
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(payload),
      });

      if (res.status === 401) {
        setError("Session expired. Please log in again.");
        return;
      }

      const { data, text } = await parseResponse(res);

      if (!res.ok) {
        setError(data?.message || text || `Server error (${res.status})`);
        return;
      }

      if (data?.success === false) {
        setError(data.message || `Failed to ${isEdit ? "update" : "add"} customer.`);
        return;
      }

      onSuccess({ identifier: isEdit ? identifier : phoneStr, name: getVal("name") });
    } catch {
      setError("Unable to connect to server.");
    } finally {
      setSaving(false);
    }
  };

  if (isEdit && prefillData === null) {
    return (
      <div style={{ padding: 24, textAlign: "center", color: "#6b7280", fontSize: 14 }}>
        {loadError || "Loading customer data…"}
      </div>
    );
  }

  const pf = prefillData || {};

  let submitLabel = "Add Customer";
  if (saving) {
    submitLabel = "Saving…";
  } else if (isEdit) {
    submitLabel = "Update Customer";
  }

  return (
    <>
      {(error || loadError) && (
        <div
          style={{
            backgroundColor: "#fee2e2",
            color: "#dc2626",
            padding: "10px 14px",
            borderRadius: 6,
            marginBottom: 12,
            fontSize: 13,
          }}
        >
          <strong>Error:</strong> {error || loadError}
        </div>
      )}

      <form
        onSubmit={handleSubmit}
        style={{ display: "flex", flexDirection: "column", gap: 12 }}
      >
        <Row>
          <Field label="Customer Name *">
            <input
              id={idPrefix + "name"}
              type="text"
              placeholder="Enter customer name"
              defaultValue={pf.name || ""}
              style={inputStyle}
              required
            />
          </Field>

          <Field label="Phone Number *">
            {isEdit ? (
              <input
                id={idPrefix + "phoneNo"}
                type="text"
                value={pf.phoneNo || ""}
                readOnly
                style={readonlyInputStyle}
              />
            ) : (
              <input
                id={idPrefix + "phoneNo"}
                type="text"
                inputMode="numeric"
                maxLength={10}
                placeholder="10-digit phone"
                style={inputStyle}
                required
              />
            )}
          </Field>
        </Row>

        {isEdit && (
          <Row>
            <Field label="Identifier (read-only)">
              <input
                type="text"
                value={identifier || ""}
                readOnly
                style={readonlyInputStyle}
              />
            </Field>
            <div />
          </Row>
        )}

        {!simple && (
          <>
            <Row>
              <Field label="Party Type">
                <PartyTypeSelect onChange={setPartyType} value={partyType} />
              </Field>
              <div />
            </Row>

            <Row>
              <Field label="Balance">
                <input
                  id={idPrefix + "balance"}
                  type="number"
                  placeholder="Enter balance"
                  defaultValue={pf.balance ?? ""}
                  style={inputStyle}
                />
              </Field>
              <Field label="Credit Limit">
                <input
                  id={idPrefix + "creditLimit"}
                  type="number"
                  placeholder="Enter credit limit"
                  defaultValue={pf.creditLimit ?? ""}
                  style={inputStyle}
                />
              </Field>
            </Row>

            <AddressSection
              prefix="shipping"
              idPrefix={idPrefix}
              values={pf.shippingAddress}
            />
            <AddressSection
              prefix="billing"
              idPrefix={idPrefix}
              values={pf.billingAddress}
            />
            {isEdit && <AuditSection data={auditData} />}
          </>
        )}

        <div style={{ display: "flex", gap: 12, marginTop: 8 }}>
          <button
            type="button"
            onClick={onCancel}
            style={{
              flex: 1,
              padding: 10,
              backgroundColor: "#e5e7eb",
              color: "#111827",
              border: "none",
              borderRadius: 6,
              cursor: "pointer",
              fontWeight: "bold",
              fontSize: 14,
            }}
          >
            Cancel
          </button>
          <button
            type="submit"
            disabled={saving}
            style={{
              flex: 1,
              padding: 10,
              backgroundColor: "#000",
              color: "#fff",
              border: "none",
              borderRadius: 6,
              cursor: saving ? "not-allowed" : "pointer",
              fontWeight: "bold",
              fontSize: 14,
              opacity: saving ? 0.6 : 1,
            }}
          >
            {submitLabel}
          </button>
        </div>
      </form>
    </>
  );
}

CustomerForm.propTypes = {
  identifier: PropTypes.string,
  isEdit: PropTypes.bool,
  onSuccess: PropTypes.func.isRequired,
  onCancel: PropTypes.func.isRequired,
  idPrefix: PropTypes.string,
  simple: PropTypes.bool,
};

export default CustomerForm;