"use client";

import PropTypes from "prop-types";
import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";

export default function Update({
  identifier,
  apiPath,
  title,
  extraFields = [],
  extraData = {},
  showDescription = true,
  hideIdentifierField = false,
  onClose,
  onSuccess,
}) {
  const router = useRouter();
  const [desc, setDesc] = useState("");
  const [vals, setVals] = useState({});
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState("");
  const [msg, setMsg] = useState("");

  useEffect(() => {
    if (identifier === undefined || identifier === null) {
      setLoading(false);
      return;
    }
    getData();
  }, [identifier]);

  const getData = async () => {
    try {
      const tk = localStorage.getItem("token");
      const r = await fetch(
        `http://localhost:8080/api/${apiPath}/get?identifier=${identifier}`,
        {
          headers: {
            Authorization: `Bearer ${tk}`,
            "Content-Type": "application/json",
          },
        },
      );
      if (!r.ok) throw new Error("failed");
      const d = await r.json();
      setDesc(d.description || "");
      let temp = { id: d.id };
      extraFields.forEach((f) => {
        if (f.type != "custom" && d[f.key] != undefined) temp[f.key] = d[f.key];
        if (f.onLoad) f.onLoad(d[f.key] || "");
      });
      setVals(temp);
    } catch (e) {
      setErr("failed to load");
      console.log(e);
    }
    setLoading(false);
  };

  const isMissingEdit = (field, value) => {
    if (!field.required) return false;
    if (Array.isArray(value)) return value.length === 0;
    return value === undefined || value === null || String(value).trim() === "";
  };

  const validateFieldEdit = (field, value) => {
    if (isMissingEdit(field, value))
      return `${field.label || field.key} is required.`;

    if (field.key === "phoneNo" && value) {
      if (!/^\d{10}$/.test(String(value).trim()))
        return "Phone number must be exactly 10 digits.";
    }

    if (field.key === "username" && value) {
      if (!String(value).trim().endsWith("@gmail.com"))
        return "Username must be a valid Gmail address (e.g. example@gmail.com).";
    }
    return "";
  };

  const validate = () => {
    for (const field of extraFields) {
      const value =
        field.type === "custom" ? extraData[field.key] : vals[field.key];
      const err = validateFieldEdit(field, value);
      if (err) return err;
    }
    return "";
  };

  const submit = async (e) => {
    e.preventDefault();
    setErr("");
    setMsg("");

    const validationError = validate();
    if (validationError) {
      setErr(validationError);
      return;
    }

    const tk = localStorage.getItem("token");
    let payload = { id: vals.id, identifier, ...vals, ...extraData };
    console.log("UPDATE PAYLOAD", payload);
    if (showDescription) payload.description = desc;

    try {
      const r = await fetch(`http://localhost:8080/api/${apiPath}/update`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${tk}`,
        },
        body: JSON.stringify(payload),
      });
      const txt = await r.text();
      const data = txt ? JSON.parse(txt) : {};

      if (r.ok === false || data.success === false) {
        setErr(data.message || "update failed");
        return;
      }
      setMsg(title + " updated successfully");
      setTimeout(() => {
        onSuccess ? onSuccess() : router.back();
      }, 1500);
    } catch (e) {
      console.log(e);
      setErr("something went wrong");
    }
  };

  const box = {
    width: "100%",
    border: "1px solid #d1d5db",
    borderRadius: "6px",
    padding: "10px 14px",
    fontSize: "14px",
    outline: "none",
    boxSizing: "border-box",
  };

  if (loading) return <div style={{ padding: 20 }}>Loading...</div>;

  return (
    <div
      style={{
        padding: 20,
        fontFamily: "Arial",
        background: "#f5f5f5",
        minHeight: "calc(100vh - 70px)",
        boxSizing: "border-box",
      }}
    >
      <div style={{ marginBottom: 20 }}>
        <h1 style={{ margin: 0, color: "#111827" }}>Edit {title}</h1>
        <p style={{ margin: "4px 0 0", fontSize: 14, color: "#6b7280" }}>
          Update the details carefully
        </p>
      </div>

      <div
        style={{
          background: "#fff",
          borderRadius: 8,
          padding: 24,
          maxWidth: 600,
          boxShadow: "0 2px 10px rgba(0,0,0,0.1)",
        }}
      >
        {err && (
          <div
            style={{
              background: "#fee2e2",
              color: "#dc2626",
              padding: 10,
              borderRadius: 6,
              marginBottom: 16,
              fontSize: 14,
            }}
          >
            {err}
          </div>
        )}
        {msg && (
          <div
            style={{
              background: "#dcfce7",
              color: "#16a34a",
              padding: 10,
              borderRadius: 6,
              marginBottom: 16,
              fontSize: 14,
            }}
          >
            {msg}
          </div>
        )}

        <form onSubmit={submit}>
          {!hideIdentifierField && (
            <div style={{ marginBottom: 16 }}>
              <label
                htmlFor="identifier"
                style={{
                  display: "block",
                  fontWeight: "bold",
                  marginBottom: 6,
                  fontSize: 14,
                  color: "#6b7280",
                }}
              >
                Identifier
              </label>
              <input
                id="identifier"
                type="text"
                value={identifier}
                readOnly
                style={{
                  ...box,
                  background: "#f3f4f6",
                  color: "#9ca3af",
                  cursor: "not-allowed",
                }}
              />
            </div>
          )}

          {showDescription && (
            <div style={{ marginBottom: 16 }}>
              <label
                htmlFor="description"
                style={{
                  display: "block",
                  fontWeight: "bold",
                  marginBottom: 6,
                  fontSize: 14,
                }}
              >
                Description
              </label>
              <textarea
                id="description"
                value={desc}
                onChange={(e) => setDesc(e.target.value)}
                style={{ ...box, resize: "vertical", minHeight: 90 }}
              />
            </div>
          )}

          {extraFields.map((field) => {
            if (field.type === "custom") {
              return (
                <div key={field.key} style={{ marginBottom: 16 }}>
                  {typeof field.component === "function"
                    ? field.component()
                    : field.component}
                </div>
              );
            }

            return (
              <div key={field.key} style={{ marginBottom: 16 }}>
                <label
                  htmlFor={field.key}
                  style={{
                    display: "block",
                    fontWeight: "bold",
                    marginBottom: 6,
                    fontSize: 14,
                  }}
                >
                  {field.label}
                </label>
                <input
                  id={field.key}
                  type={field.type || "text"}
                  placeholder={"Enter " + field.label}
                  value={vals[field.key] ?? ""}
                  onChange={(e) =>
                    setVals((p) => ({ ...p, [field.key]: e.target.value }))
                  }
                  style={
                    field.readOnly
                      ? {
                          ...box,
                          background: "#f3f4f6",
                          color: "#9ca3af",
                          cursor: "not-allowed",
                        }
                      : box
                  }
                  required={field.required}
                  readOnly={field.readOnly}
                />
              </div>
            );
          })}

          <div style={{ display: "flex", gap: 12, marginTop: 24 }}>
            <button
              type="button"
              onClick={() => (onClose ? onClose() : router.back())}
              style={{
                flex: 1,
                padding: 10,
                background: "#e5e7eb",
                border: "none",
                borderRadius: 6,
                fontWeight: "bold",
                fontSize: 14,
                cursor: "pointer",
              }}
            >
              Cancel
            </button>
            <button
              type="submit"
              style={{
                flex: 1,
                padding: 10,
                background: "#000",
                color: "#fff",
                border: "none",
                borderRadius: 6,
                fontWeight: "bold",
                fontSize: 14,
                cursor: "pointer",
              }}
            >
              Update {title}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

Update.propTypes = {
  identifier: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  apiPath: PropTypes.string.isRequired,
  title: PropTypes.string.isRequired,
  extraFields: PropTypes.arrayOf(
    PropTypes.shape({
      key: PropTypes.string.isRequired,
      label: PropTypes.string,
      type: PropTypes.string,
      required: PropTypes.bool,
      component: PropTypes.oneOfType([PropTypes.node, PropTypes.func]),
      onLoad: PropTypes.func,
    }),
  ),
  extraData: PropTypes.object,
  showDescription: PropTypes.bool,
  hideIdentifierField: PropTypes.bool,
  onClose: PropTypes.func,
  onSuccess: PropTypes.func,
};
