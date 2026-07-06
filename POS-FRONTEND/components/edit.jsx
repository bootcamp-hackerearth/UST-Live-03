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

  const formatDate = (date) => {
    if (!date) return "-";
    try {
      return new Date(date).toLocaleString();
    } catch {
      return date;
    }
  };

  const handleStatusRedirect = (status) => {
    if (status === 401) router.push("/error?status=401");
    else if (status === 403) router.push("/error?status=403");
    else if (status === 404) router.push("/error?status=404");
    else if (status >= 500) router.push("/error?status=500");
  };

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
        `api/${apiPath}/get?identifier=${identifier}`,
        {
          headers: {
            Authorization: `Bearer ${tk}`,
            "Content-Type": "application/json",
          },
        },
      );

      if (!r.ok) {
        handleStatusRedirect(r.status);
        return;
      }

      const d = await r.json();

      setDesc(d.description || "");

      let temp = { id: d.id };

      extraFields?.forEach((f) => {
        if (!f?.key) return;

        if (f.type !== "custom" && d[f.key] !== undefined) {
          let value = d[f.key];
          if (f.key === "createdOn" || f.key === "modifiedOn") {
            value = formatDate(value);
          }
          temp[f.key] = value;
        }

        f.onLoad?.(d[f.key], d);
      });

      setVals(temp);
    } catch {
      router.push("/error?status=500");
    }

    setLoading(false);
  };

  const submit = async (e) => {
    e.preventDefault();

    setErr("");
    setMsg("");

    const tk = localStorage.getItem("token");

    const payload = {
      id: vals.id,
      identifier,
      ...vals,
      ...extraData,
      modifiedBy: "currentUser",
      modifiedOn: new Date().toISOString(),
    };

    if (showDescription) payload.description = desc;

    try {
      const r = await fetch(`/api/${apiPath}/update`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${tk}`,
        },
        body: JSON.stringify(payload),
      });

      if (!r.ok) {
        handleStatusRedirect(r.status);
        return;
      }

      const txt = await r.text();
      const data = txt ? JSON.parse(txt) : {};

      if (data.success === false) {
        setErr(data.message || "Update failed");
        return;
      }

      setMsg(`${title} updated successfully`);

      setTimeout(() => {
        onSuccess?.();
        if (!onSuccess) router.back();
      }, 1200);
    } catch {
      router.push("/error?status=500");
    }
  };

  const box = {
    width: "100%",
    border: "1px solid #d1d5db",
    borderRadius: "6px",
    padding: "10px 14px",
    fontSize: "14px",
    boxSizing: "border-box",
  };

  if (loading) return <div style={{ padding: 20 }}>Loading...</div>;

  return (
    <div style={{ padding: 20, background: "#f5f5f5", minHeight: "100vh" }}>
      <h2>Edit {title}</h2>

      <div style={{ background: "#fff", padding: 20, borderRadius: 8 }}>
        {err && <div style={{ color: "red" }}>{err}</div>}
        {msg && <div style={{ color: "green" }}>{msg}</div>}

        <form onSubmit={submit}>
          {!hideIdentifierField && (
            <div style={{ marginBottom: 16 }}>
              <label htmlFor="identifier-field">Identifier</label>
              <input
                id="identifier-field"
                value={identifier ?? ""}
                readOnly
                style={{ ...box, background: "#eee" }}
              />
            </div>
          )}

          {showDescription && (
            <div style={{ marginBottom: 16 }}>
              <label htmlFor="description-field">Description</label>
              <textarea
                id="description-field"
                value={desc}
                onChange={(e) => setDesc(e.target.value)}
                style={{ ...box, minHeight: 80 }}
              />
            </div>
          )}

          {extraFields?.map((field) => {
            if (!field?.key) return null;

            if (field.type === "custom") {
              return <div key={field.key}>{field.component}</div>;
            }

            return (
              <div key={field.key} style={{ marginBottom: 16 }}>
                <label htmlFor={`field-${field.key}`}>{field.label}</label>
                <input
                  id={`field-${field.key}`}
                  value={vals[field.key] ?? ""}
                  readOnly={field.readOnly}
                  onChange={(e) =>
                    setVals((p) => ({
                      ...p,
                      [field.key]: e.target.value,
                    }))
                  }
                  style={field.readOnly ? { ...box, background: "#eee" } : box}
                />
              </div>
            );
          })}

          <div style={{ display: "flex", gap: 10 }}>
            <button type="button" onClick={() => router.back()}>
              Cancel
            </button>
            <button type="submit">Update {title}</button>
          </div>
        </form>
      </div>
    </div>
  );
}

Update.propTypes = {
  identifier: PropTypes.any,
  apiPath: PropTypes.string.isRequired,
  title: PropTypes.string.isRequired,
  extraFields: PropTypes.array,
  extraData: PropTypes.object,
  showDescription: PropTypes.bool,
  hideIdentifierField: PropTypes.bool,
  onClose: PropTypes.func,
  onSuccess: PropTypes.func,
};
