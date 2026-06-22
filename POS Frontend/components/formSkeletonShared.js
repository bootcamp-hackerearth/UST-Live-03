export const C = {
  navy:     "#000000",
  mid:      "#1a1a1a",
  light:    "#333333",
  gray:     "#e8e8e8",
  offWhite: "#f5f5f5",
  text:     "#1a1a1a",
  muted:    "#666666",
  white:    "#ffffff",
  error:    "#d32f2f",
  errorBg:  "#ffebee",
};

export const INPUT_PAD    = "9px 12px";
export const INPUT_BORDER = `1.5px solid ${C.gray}`;
export const INPUT_RADIUS = "7px";
export const INPUT_SIZE   = "13px";
export const INPUT_BG     = "#fafafa";
export const BTN_RADIUS   = "7px";
export const BTN_SIZE     = "13px";
export const BTN_WEIGHT   = "600";

const BOX_PAD            = "9px 14px";
const GRADIENT           = "linear-gradient(135deg, #000000, #1a1a1a)";
const TEXT_DARK          = "#374151";
const BOX_BORDER_SUCCESS = "1px solid #d1d5db";
const BOX_BORDER_ERROR   = "1px solid #f5c6c6";
const BOX_BG_SUCCESS     = "#f4f4f4";
const FLEX_COL_HIDDEN    = { display: "flex", flexDirection: "column", overflow: "hidden" };
const NAVY_BOLD_700      = { fontWeight: "700", color: C.navy };
const BOX_SHARED = {
  borderRadius: BTN_RADIUS,
  padding:      BOX_PAD,
  fontSize:     INPUT_SIZE,
  marginBottom: "14px",
  textAlign:    "center",
  gridColumn:   "span 2",
};
const INPUT_BASE = {
  padding:      INPUT_PAD,
  border:       INPUT_BORDER,
  borderRadius: INPUT_RADIUS,
  fontSize:     INPUT_SIZE,
  outline:      "none",
  background:   INPUT_BG,
  boxSizing:    "border-box",
  width:        "100%",
  color:        C.text,
};

export const sharedStyles = {
  page: {
    ...FLEX_COL_HIDDEN,
    position: "fixed", top: "60px", left: "220px", right: 0, bottom: 0,
    backgroundColor: C.white, fontFamily: "'Segoe UI', sans-serif",
  },
  inner: {
    ...FLEX_COL_HIDDEN,
    flex: 1, padding: "20px 24px",
  },
  topRow: {
    display: "flex", alignItems: "center", gap: "12px",
    marginBottom: "16px", flexShrink: 0, position: "relative",
  },
  backBtn: {
    padding: "7px 16px", backgroundColor: "transparent",
    color: C.navy, border: "none",
    borderRadius: BTN_RADIUS, fontSize: "12px",
    fontWeight: BTN_WEIGHT, cursor: "pointer", flexShrink: 0,
    transition: "all 0.2s ease",
  },
  pageTitle: {
    ...NAVY_BOLD_700,
    position: "absolute", left: "50%", transform: "translateX(-50%)",
    margin: 0, fontSize: "19px", whiteSpace: "nowrap",
  },
  cardWrap: {
    display: "flex", alignItems: "center", justifyContent: "center",
    flex: 1, overflow: "hidden",
  },
  card: {
    background: C.white, borderRadius: "10px",
    boxShadow: "0 2px 12px rgba(0,0,0,0.05)",
    border: `1px solid ${C.gray}`,
    padding: "24px 28px", width: "100%",
    maxWidth: "720px", maxHeight: "100%", overflow: "auto",
  },
  cardTitle:    { ...NAVY_BOLD_700, fontSize: "16px", margin: "0 0 3px" },
  cardSubtitle: { fontSize: "12px", color: C.muted, marginBottom: "18px" },
  successBox: {
    ...BOX_SHARED,
    background: BOX_BG_SUCCESS,
    border:     BOX_BORDER_SUCCESS,
    color:      C.mid,
  },
  errorBox: {
    ...BOX_SHARED,
    background: C.errorBg,
    border:     BOX_BORDER_ERROR,
    color:      C.error,
  },
  form: {
    display: "grid", gridTemplateColumns: "1fr 1fr",
    columnGap: "20px", rowGap: "12px",
  },
  field:      { display: "flex", flexDirection: "column", gap: "4px" },
  label:      { fontSize: "12px", fontWeight: BTN_WEIGHT, color: TEXT_DARK, letterSpacing: "0.2px" },
  input:      { ...INPUT_BASE },
  inputError: { border: `1.5px solid ${C.error}`, background: C.errorBg },
  fieldError: { fontSize: "11px", color: C.error, marginTop: "2px" },
  select:     { ...INPUT_BASE },
  multiWrap: {
    display: "flex", flexWrap: "wrap", gap: "6px", padding: "9px",
    border: INPUT_BORDER, borderRadius: INPUT_RADIUS,
    background: INPUT_BG, minHeight: "42px",
  },
  chip: {
    padding: "4px 12px", borderRadius: "20px",
    border: INPUT_BORDER,
    background: C.white, fontSize: "12px",
    cursor: "pointer", fontWeight: "500", color: TEXT_DARK,
  },
  chipSelected: {
    background: GRADIENT,
    borderColor: C.navy, color: C.white, fontWeight: BTN_WEIGHT,
  },
  buttonRow: {
    display: "flex", gap: "10px", marginTop: "6px", gridColumn: "span 2",
  },
  cancelBtn: {
    flex: 1, padding: "10px",
    background: C.offWhite, color: TEXT_DARK,
    border: `1px solid ${C.gray}`,
    borderRadius: BTN_RADIUS, fontSize: BTN_SIZE,
    fontWeight: BTN_WEIGHT, cursor: "pointer",
  },
  submitBtn: {
    flex: 1, padding: "10px",
    background: GRADIENT,
    color: C.white, border: "none",
    borderRadius: BTN_RADIUS, fontSize: BTN_SIZE,
    fontWeight: BTN_WEIGHT, cursor: "pointer",
    boxShadow: "0 3px 10px rgba(0,0,0,0.2)",
    transition: "all 0.2s ease",
  },
  submitBtnDisabled: { background: "#999999", boxShadow: "none", cursor: "not-allowed" },
};