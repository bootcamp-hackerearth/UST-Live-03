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

export function HttpErrorPopup({ httpError, onClose }) {
  if (!httpError) return null;

  const styles = {
    403: { icon: "🔒", title: "Access Denied", color: "#ef4444", defaultMsg: "You don't have permission to perform this action." },
    404: { icon: "❌", title: "Not Found", color: "#f59e0b", defaultMsg: "The requested resource doesn't exist." },
    400: { icon: "⚠️", title: "Invalid Request", color: "#f59e0b", defaultMsg: "The request contains invalid data." },
    500: { icon: "⚡", title: "Server Error", color: "#ef4444", defaultMsg: "Something went wrong. Please try again later." },
  };

  const info = styles[httpError.statusCode] || styles[500];

  return (
    <div style={{
      position: "fixed", inset: 0, background: "rgba(30,34,53,0.45)",
      display: "flex", alignItems: "center", justifyContent: "center",
      zIndex: 9999,
    }}>
      <div style={{
        background: "#fff", borderRadius: "14px", padding: "32px 36px",
        maxWidth: "380px", textAlign: "center",
        boxShadow: "0 16px 48px rgba(30,34,53,0.25)",
        border: "1.5px solid #e8eaf0",
      }}>
        <div style={{ fontSize: "44px", marginBottom: "12px" }}>{info.icon}</div>
        <div style={{ fontSize: "22px", fontWeight: "700", color: info.color, marginBottom: "6px" }}>
          {httpError.statusCode} · {info.title}
        </div>
        <p style={{ fontSize: "13.5px", color: "#6b7280", margin: "0 0 22px", lineHeight: 1.6 }}>
          {httpError.message || info.defaultMsg}
        </p>
        <button
          type="button"
          onClick={onClose}
          style={{
            padding: "9px 28px", borderRadius: "7px", border: "none",
            background: "linear-gradient(135deg, #363955, #54668E)",
            color: "#fff", fontSize: "13px", fontWeight: "600", cursor: "pointer",
          }}
        >
          Okay
        </button>
      </div>
    </div>
  );
}

HttpErrorPopup.propTypes = {
  httpError: PropTypes.shape({
    statusCode: PropTypes.number,
    message: PropTypes.string,
  }),
  onClose: PropTypes.func.isRequired,
};