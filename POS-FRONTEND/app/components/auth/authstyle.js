
export const inputBaseStyle = {
  width: "100%",
  paddingTop: "10px",
  paddingBottom: "10px",
  fontSize: "13px",
  backgroundColor: "#ffffff",
  border: "1px solid #e5e5e5",
  borderRadius: "6px",
  color: "#111111",
  outline: "none",
  boxSizing: "border-box",
  transition: "all 0.2s ease-in-out",
};

export const iconStyle = {
  position: "absolute",
  left: "12px",
  top: "50%",
  transform: "translateY(-50%)",
  width: "15px",
  height: "15px",
  pointerEvents: "none",
};

export const containerStyle = {
  minHeight: "100vh",
  width: "100%",
  display: "flex",
  alignItems: "center",
  justifyContent: "center",
  backgroundColor: "#fafafa",
  padding: "16px",
  fontFamily: "-apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif",
  boxSizing: "border-box",
};

export const cardStyle = {
  width: "100%",
  backgroundColor: "#ffffff",
  borderRadius: "12px",
  border: "1px solid #e5e5e5",
  padding: "32px 28px",
  boxSizing: "border-box",
};

export const focusHandler = (e) => {
  e.target.style.borderColor = "#111111";
  e.target.style.backgroundColor = "#fafafa";
};

export const blurHandler = (e) => {
  e.target.style.borderColor = "#e5e5e5";
  e.target.style.backgroundColor = "#ffffff";
};