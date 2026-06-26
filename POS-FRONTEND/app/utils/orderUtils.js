import PropTypes from "prop-types";

export const fmt = (n) =>
  Number(n || 0).toLocaleString("en-IN", { minimumFractionDigits: 2 });
export const apiFetch = async (url, token, options = {}) => {
  const { headers: customHeaders, ...restOptions } = options;
  const response = await fetch(url, {
    ...restOptions,
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
      ...customHeaders,
    },
  });
  if (!response.ok) throw new Error(`Request failed: ${response.status}`);
  return response.json();
};

export const fetchProducts = (token) =>
  apiFetch("http://localhost:8080/api/product/list", token, {
    method: "POST",
    body: JSON.stringify({ page: 0, sizePerPage: 500 }),
  });

export const fetchCustomers = (token) =>
  apiFetch("http://localhost:8080/api/customer/list", token, {
    method: "POST",
    body: JSON.stringify({ page: 0, sizePerPage: 500 }),
  });

export const fetchOrders = (token) =>
  apiFetch("http://localhost:8080/api/order/list", token, { method: "GET" });

export const getCustomerName = (customers, identifier) =>
  customers.find((c) => c.identifier === identifier)?.name || identifier;

export const getProductName = (products, identifier) =>
  products.find((p) => p.identifier === identifier)?.productName || identifier;

export const getOrderEntries = (order) => order.orderEntryDtoList || [];

export const makeShowError = (setErrorMessage) => (msg) => {
  setErrorMessage(msg);
  setTimeout(() => setErrorMessage(""), 3000);
};

const ORDER_STATUS = {
  CANCELLED: { background: "#fee2e2", color: "#dc2626" },
  DEFAULT: { background: "#dcfce7", color: "#16a34a" },
};

const ORDER_STATUS_ROUNDED = {
  CANCELLED: { background: "#fef2f2", color: "#dc2626" },
  DEFAULT: { background: "#f0fdf4", color: "#16a34a" },
};

const PAYMENT_COLORS = {
  UPI: { background: "#eff6ff", color: "#2563eb" },
  CARD: { background: "#f5f3ff", color: "#7c3aed" },
  DEFAULT: { background: "#f0fdf4", color: "#16a34a" },
};

export const badgeStyle = (status) => ({
  display: "inline-block",
  padding: "2px 10px",
  borderRadius: 99,
  fontSize: 12,
  fontWeight: 600,
  ...(ORDER_STATUS[status] || ORDER_STATUS.DEFAULT),
});

export const statusBadgeStyle = (status) => ({
  display: "inline-block",
  padding: "3px 10px",
  borderRadius: 6,
  fontSize: 12,
  fontWeight: 600,
  ...(ORDER_STATUS_ROUNDED[status] || ORDER_STATUS_ROUNDED.DEFAULT),
});

export const paymentBadgeStyle = (method) => ({
  display: "inline-block",
  padding: "3px 10px",
  borderRadius: 6,
  fontSize: 12,
  fontWeight: 600,
  ...(PAYMENT_COLORS[method] || PAYMENT_COLORS.DEFAULT),
});

export const errorBannerStyle = {
  color: "red",
  background: "#fee2e2",
  padding: "8px 12px",
  borderRadius: 8,
  marginBottom: 12,
};

export const EntryShape = PropTypes.shape({
  identifier: PropTypes.any,
  product: PropTypes.any,
  price: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  sellingPrice: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  quantity: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  discount: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  totalPrice: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
});

export const ProductShape = PropTypes.shape({
  identifier: PropTypes.any,
  productName: PropTypes.string,
});

export const CustomerShape = PropTypes.shape({
  identifier: PropTypes.any,
  name: PropTypes.string,
  phoneNo: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
});

export const OrderShape = PropTypes.shape({
  identifier: PropTypes.any,
  customer: PropTypes.any,
  orderStatus: PropTypes.string,
  totalPrice: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  totalDiscount: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  orderEntryDtoList: PropTypes.arrayOf(EntryShape),
});
