import PropTypes from "prop-types";

export const labelStyle = {
  fontSize: "11px", fontWeight: "700",
  color: "#4b5563", letterSpacing: "0.4px",
  textTransform: "uppercase",
};

export const inputStyle = {
  padding: "9px 12px",
  borderWidth: "1.5px", borderStyle: "solid", borderColor: "#E8E8E8",
  borderRadius: "7px", fontSize: "13px", outline: "none",
  background: "#fafafa", boxSizing: "border-box", width: "100%",
  color: "#1e2235",
};

export const inputErrorStyle = { borderColor: "#c0392b", background: "#fdf2f2" };

export const errText = { fontSize: "11px", color: "#c0392b", marginTop: "2px" };

const alertC = { error: "#c0392b", errorBg: "#fdf2f2" };

export function AlertBox({ error, success }) {
  if (!error && !success) return null;
  return (
    <div style={{ padding: "12px 28px 0" }}>
      {error && (
        <div style={{
          background: alertC.errorBg, border: "1px solid #f5c6c6",
          color: alertC.error, borderRadius: "7px",
          padding: "9px 14px", fontSize: "13px",
        }}>
          {error}
        </div>
      )}
      {success && (
        <div style={{
          background: "#f0fdf4", border: "1px solid #86efac",
          color: "#166534", borderRadius: "7px",
          padding: "9px 14px", fontSize: "13px",
        }}>
          {success}
        </div>
      )}
    </div>
  );
}

AlertBox.propTypes = {
  error: PropTypes.string,
  success: PropTypes.string,
};