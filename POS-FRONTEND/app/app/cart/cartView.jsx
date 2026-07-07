"use client";

import PropTypes from "prop-types";
const styles = {
  page: { padding: 20, background: "#f5f5f5" },
  header: { display: "flex", alignItems: "center", gap: 12, marginBottom: 20 },
  historyBtn: {
    marginLeft: "auto",
    background: "#fff",
    border: "1px solid #e5e7eb",
    borderRadius: 8,
    padding: "8px 14px",
    cursor: "pointer",
    fontSize: 13,
    fontWeight: 600,
    display: "flex",
    alignItems: "center",
    gap: 6,
    color: "#374151",
  },
  errorBanner: {
    color: "red",
    background: "#fee2e2",
    padding: "8px 12px",
    borderRadius: 8,
    marginBottom: 12,
  },
  successBanner: {
    color: "green",
    background: "#dcfce7",
    padding: "8px 12px",
    borderRadius: 8,
    marginBottom: 12,
  },
  infoGrid: {
    display: "grid",
    gridTemplateColumns: "repeat(3, 1fr)",
    gap: 16,
    marginBottom: 20,
  },
  fieldBox: {
    border: "1px solid #d1d5db",
    borderRadius: 8,
    padding: "10px 16px",
    background: "#fff",
    position: "relative",
  },
  fieldLabel: { fontSize: 12, color: "#6b7280", marginBottom: 4 },
  searchInput: {
    width: "100%",
    border: "1px solid #d1d5db",
    borderRadius: 8,
    padding: "10px 14px",
    fontSize: 14,
    color: "#111",
    outline: "none",
    background: "#fff",
  },
  dropdownList: {
    marginTop: 8,
    maxHeight: 180,
    overflowY: "auto",
    border: "1px solid #d1d5db",
    borderRadius: 8,
    background: "#fff",
    position: "absolute",
    left: 0,
    right: 0,
    zIndex: 50,
  },
  dropdownItem: {
    width: "100%",
    textAlign: "left",
    padding: "10px 14px",
    border: "none",
    borderBottom: "1px solid #f3f4f6",
    background: "transparent",
    cursor: "pointer",
    fontSize: 14,
    color: "#111",
  },
  dropdownItemSub: { display: "block", color: "#6b7280", fontSize: 12 },
  dropdownEmpty: { padding: 10, color: "#6b7280" },
  addCustomerBtn: {
    marginTop: 8,
    width: "100%",
    padding: "8px 0",
    backgroundColor: "#000",
    color: "#fff",
    border: "none",
    borderRadius: 6,
    cursor: "pointer",
    fontWeight: "bold",
    fontSize: 13,
  },
  catalogSearchInput: {
    width: "100%",
    border: "1px solid #e5e7eb",
    borderRadius: 10,
    padding: "12px 16px",
    fontSize: 14,
    outline: "none",
    marginBottom: 14,
    background: "#fff",
  },
  categoryRow: { display: "flex", flexWrap: "wrap", gap: 8, marginBottom: 18 },
  categoryPill: (active) => ({
    padding: "7px 16px",
    borderRadius: 999,
    fontSize: 13,
    fontWeight: 600,
    border: active ? "1px solid #000" : "1px solid #e5e7eb",
    background: active ? "#000" : "#fff",
    color: active ? "#fff" : "#374151",
    cursor: "pointer",
    whiteSpace: "nowrap",
  }),
  cardGrid: {
    display: "grid",
    gridTemplateColumns: "repeat(auto-fill, minmax(150px, 1fr))",
    gap: 16,
    marginBottom: 20,
  },
  productCard: (noPrice) => ({
    background: noPrice ? "#fafafa" : "#fff",
    border: noPrice ? "1px solid #fca5a5" : "1px solid #e5e7eb",
    borderRadius: 14,
    padding: 16,
    cursor: "pointer",
    textAlign: "center",
    transition: "box-shadow .15s, transform .15s",
    position: "relative",
  }),
  noPriceBadge: {
    position: "absolute",
    top: 8,
    right: 8,
    background: "#fee2e2",
    color: "#dc2626",
    fontSize: 10,
    fontWeight: 700,
    padding: "2px 6px",
    borderRadius: 999,
  },
  productImgPlaceholder: {
    width: 60,
    height: 60,
    borderRadius: 12,
    background: "#f3f4f6",
    margin: "0 auto 12px",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    color: "#cbd5e1",
  },
  productName: {
    fontWeight: 700,
    fontSize: 14,
    marginBottom: 2,
    color: "#111",
  },
  productCode: { fontSize: 12, color: "#9ca3af", marginBottom: 6 },
  productPrice: (noPrice) => ({
    fontWeight: 700,
    fontSize: 14,
    color: noPrice ? "#ef4444" : "#2563eb",
  }),
  catalogEmpty: { padding: 40, textAlign: "center", color: "#9ca3af" },
  tableHeader: {
    background: "#111",
    color: "#fff",
    padding: "12px",
    textAlign: "center",
  },
  tableCell: {
    padding: "10px",
    border: "1px solid #e5e7eb",
    textAlign: "center",
  },
  qtyControls: {
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    gap: 6,
  },
  roundBtn: {
    width: 22,
    height: 22,
    borderRadius: "50%",
    border: "none",
    background: "#000",
    color: "#fff",
    cursor: "pointer",
    fontSize: 12,
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    flexShrink: 0,
  },
  removeBtn: {
    background: "#000",
    borderRadius: "50%",
    width: 34,
    height: 34,
    border: "none",
    cursor: "pointer",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    color: "#fff",
    margin: "0 auto",
  },
  summaryWrapper: {
    display: "flex",
    justifyContent: "flex-end",
    marginTop: 20,
  },
  summaryBox: {
    width: 320,
    background: "#fff",
    border: "1px solid #fee2e2",
    borderRadius: 12,
    padding: 16,
    fontSize: 14,
  },
  summaryRow: {
    display: "flex",
    justifyContent: "space-between",
    marginBottom: 10,
  },
  summaryDivider: {
    borderTop: "1px solid #e5e7eb",
    paddingTop: 10,
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
  },
  footerButtons: { display: "flex", gap: 16, marginTop: 20 },
  clearBtn: (disabled) => ({
    flex: 1,
    padding: "12px 0",
    borderRadius: 12,
    fontWeight: 600,
    color: "#fff",
    background: "#ef4444",
    border: "none",
    cursor: "pointer",
    opacity: disabled ? 0.5 : 1,
  }),
  placeOrderBtn: (disabled) => ({
    flex: 1,
    padding: "12px 0",
    borderRadius: 12,
    fontWeight: 600,
    color: "#fff",
    background: "#000",
    border: "none",
    cursor: disabled ? "not-allowed" : "pointer",
    opacity: disabled ? 0.5 : 1,
  }),
  emptyCell: { padding: 40, color: "#9ca3af" },
  loadingState: { padding: 40, textAlign: "center", color: "#9ca3af" },
};

function CustomerSearch({
  customerSearch,
  customerDropdownOpen,
  filteredCustomers,
  onSearchChange,
  onSelect,
  onAddCustomerClick,
}) {
  return (
    <div style={styles.fieldBox}>
      <p style={styles.fieldLabel}>Customer</p>
      <input
        type="search"
        value={customerSearch}
        onChange={onSearchChange}
        placeholder="Search by name or phone"
        style={styles.searchInput}
      />
      {customerSearch.trim() && customerDropdownOpen && (
        <div style={styles.dropdownList}>
          {filteredCustomers.length > 0 ? (
            filteredCustomers.map((c) => (
              <button
                key={c.identifier}
                type="button"
                onClick={() => onSelect(c)}
                style={styles.dropdownItem}
              >
                {c.name || c.identifier}
                <span style={styles.dropdownItemSub}>{c.identifier}</span>
              </button>
            ))
          ) : (
            <div style={styles.dropdownEmpty}>No customer found</div>
          )}
        </div>
      )}
      <button
        type="button"
        onClick={onAddCustomerClick}
        style={styles.addCustomerBtn}
      >
        + Add New Customer
      </button>
    </div>
  );
}

CustomerSearch.propTypes = {
  customerSearch: PropTypes.string,
  customerDropdownOpen: PropTypes.bool,
  filteredCustomers: PropTypes.array,
  onSearchChange: PropTypes.func.isRequired,
  onSelect: PropTypes.func.isRequired,
  onAddCustomerClick: PropTypes.func.isRequired,
};

function ProductCatalog({
  products,
  search,
  onSearchChange,
  categories,
  activeCategory,
  onCategoryChange,
  onAdd,
  saving,
}) {
  return (
    <div>
      <input
        type="search"
        value={search}
        onChange={onSearchChange}
        placeholder="Search by name or code..."
        style={styles.catalogSearchInput}
      />

      {categories.length > 1 && (
        <div style={styles.categoryRow}>
          {categories.map((cat) => (
            <button
              key={cat}
              type="button"
              style={styles.categoryPill(activeCategory === cat)}
              onClick={() => onCategoryChange(cat)}
            >
              {cat}
            </button>
          ))}
        </div>
      )}

      {products.length === 0 ? (
        <div style={styles.catalogEmpty}>No products found.</div>
      ) : (
        <div style={styles.cardGrid}>
          {products.map((p) => {
            const noPrice = !p.sellingPrice || Number(p.sellingPrice) <= 0;
            return (
              <button
                key={p.identifier}
                type="button"
                disabled={saving}
                onClick={() => onAdd(p)}
                style={{
                  ...styles.productCard(noPrice),
                  opacity: saving ? 0.6 : 1,
                }}
              >
                {noPrice && <span style={styles.noPriceBadge}>No Price</span>}
                <div style={styles.productImgPlaceholder}>
                  <svg
                    width="26"
                    height="26"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    strokeWidth="1.5"
                  >
                    <path d="M21 8l-9-5-9 5 9 5 9-5z" />
                    <path d="M3 8v8l9 5 9-5V8" />
                    <path d="M12 13v8" />
                  </svg>
                </div>
                <p style={styles.productName}>{p.productName}</p>
                <p style={styles.productCode}>
                  {p.productCode || p.sku || p.identifier}
                </p>
                <p style={styles.productPrice(noPrice)}>
                  {noPrice
                    ? "Price not set"
                    : `₹${Number(p.sellingPrice).toLocaleString("en-IN", { minimumFractionDigits: 2 })}`}
                </p>
              </button>
            );
          })}
        </div>
      )}
    </div>
  );
}

ProductCatalog.propTypes = {
  products: PropTypes.array.isRequired,
  search: PropTypes.string,
  onSearchChange: PropTypes.func.isRequired,
  categories: PropTypes.array,
  activeCategory: PropTypes.string,
  onCategoryChange: PropTypes.func.isRequired,
  onAdd: PropTypes.func.isRequired,
  saving: PropTypes.bool,
};

function CartTable({ entries, products, onQtyChange, onRemove }) {
  const columns = [
    "PRODUCT",
    "MRP",
    "SELLING PRICE",
    "QTY",
    "DISCOUNT",
    "SUBTOTAL",
    "ACTIONS",
  ];
  return (
    <table
      width="100%"
      style={{ background: "#fff", borderCollapse: "collapse" }}
    >
      <thead>
        <tr>
          {columns.map((h) => (
            <th key={h} style={styles.tableHeader}>
              {h}
            </th>
          ))}
        </tr>
      </thead>
      <tbody>
        {entries.length === 0 ? (
          <tr>
            <td
              colSpan={7}
              style={{ ...styles.tableCell, ...styles.emptyCell }}
            >
              No items yet — select a product above to begin.
            </td>
          </tr>
        ) : (
          entries.map((entry, i) => (
            <tr key={entry.identifier ?? i}>
              <td style={styles.tableCell}>
                {products.find((p) => p.identifier === entry.product)
                  ?.productName || entry.product}
              </td>
              <td
                style={{
                  ...styles.tableCell,
                  color: "#9ca3af",
                  textDecoration: "line-through",
                }}
              >
                ₹
                {Number(entry.price || 0).toLocaleString("en-IN", {
                  minimumFractionDigits: 2,
                })}
              </td>
              <td style={styles.tableCell}>
                ₹
                {Number(entry.sellingPrice || 0).toLocaleString("en-IN", {
                  minimumFractionDigits: 2,
                })}
              </td>
              <td style={styles.tableCell}>
                <div style={styles.qtyControls}>
                  <button
                    type="button"
                    onClick={() => onQtyChange(i, -1)}
                    style={styles.roundBtn}
                  >
                    −
                  </button>
                  <span style={{ minWidth: 20, textAlign: "center" }}>
                    {entry.quantity}
                  </span>
                  <button
                    type="button"
                    onClick={() => onQtyChange(i, 1)}
                    style={styles.roundBtn}
                  >
                    +
                  </button>
                </div>
              </td>
              <td style={{ ...styles.tableCell, color: "green" }}>
                ₹
                {Number(entry.discount || 0).toLocaleString("en-IN", {
                  minimumFractionDigits: 2,
                })}
              </td>
              <td style={{ ...styles.tableCell, fontWeight: 600 }}>
                ₹
                {Number(entry.totalPrice || 0).toLocaleString("en-IN", {
                  minimumFractionDigits: 2,
                })}
              </td>
              <td style={styles.tableCell}>
                <button
                  type="button"
                  onClick={() => onRemove(i)}
                  style={styles.removeBtn}
                >
                  🗑️
                </button>
              </td>
            </tr>
          ))
        )}
      </tbody>
    </table>
  );
}

CartTable.propTypes = {
  entries: PropTypes.array.isRequired,
  products: PropTypes.array.isRequired,
  onQtyChange: PropTypes.func.isRequired,
  onRemove: PropTypes.func.isRequired,
};

function OrderSummary({ totalDiscount, totalPayable }) {
  const fmt = (n) => n.toLocaleString("en-IN", { minimumFractionDigits: 2 });
  return (
    <div style={styles.summaryWrapper}>
      <div style={styles.summaryBox}>
        <div style={styles.summaryRow}>
          <span style={{ color: "#6b7280" }}>Total Discount</span>
          <span style={{ color: "green" }}>- ₹{fmt(totalDiscount)}</span>
        </div>
        <div style={styles.summaryDivider}>
          <div
            style={{
              display: "flex",
              justifyContent: "space-between",
              flex: 1,
            }}
          >
            <span style={{ color: "#6b7280" }}>Total Payable</span>
            <span style={{ fontWeight: 700, fontSize: 16 }}>
              ₹{fmt(totalPayable)}
            </span>
          </div>
        </div>
      </div>
    </div>
  );
}

OrderSummary.propTypes = {
  totalDiscount: PropTypes.number,
  totalPayable: PropTypes.number,
};

function CartUI({
  products,
  entries,
  customer,
  customerName,
  customerSearch,
  customerDropdownOpen,
  filteredCustomers,
  onCustomerSearchChange,
  onSelectCustomer,
  onAddCustomerClick,
  catalogProducts,
  catalogSearch,
  onCatalogSearchChange,
  categories,
  activeCategory,
  onCategoryChange,
  onAddProduct,
  onQtyChange,
  onRemove,
  onClearCart,
  onPlaceOrder,
  onShowHistory,
  totalDiscount,
  totalPayable,
  saving,
  clearing,
  placingOrder,
  loading,
  errorMessage,
  successMessage,
  hasCustomer,
}) {
  const today = new Date().toLocaleDateString("en-GB").replaceAll("/", "-");
  const placeOrderDisabled = !customer || entries.length === 0 || placingOrder;
  const namePrefix = customerName ? customerName + " • " : "";
  const displayCustomer = customer ? namePrefix + customer : "-";

  return (
    <div style={styles.page}>
      <div style={styles.header}>
        <h2 style={{ margin: 0 }}>🛒 CART</h2>
        {hasCustomer && (
          <button
            type="button"
            style={styles.historyBtn}
            onClick={onShowHistory}
          >
            🕐 Order History
          </button>
        )}
      </div>

      {errorMessage && <p style={styles.errorBanner}>{errorMessage}</p>}
      {successMessage && <p style={styles.successBanner}>{successMessage}</p>}

      <div style={styles.infoGrid}>
        <CustomerSearch
          customerSearch={customerSearch}
          customerDropdownOpen={customerDropdownOpen}
          filteredCustomers={filteredCustomers}
          onSearchChange={onCustomerSearchChange}
          onSelect={onSelectCustomer}
          onAddCustomerClick={onAddCustomerClick}
        />
        <div style={styles.fieldBox}>
          <p style={styles.fieldLabel}>Customer ID</p>
          <p style={{ margin: 0, fontFamily: "monospace" }}>
            {displayCustomer}
          </p>
        </div>
        <div style={styles.fieldBox}>
          <p style={styles.fieldLabel}>Date</p>
          <p style={{ margin: 0, color: "#6b7280" }}>{today}</p>
        </div>
      </div>

      {customer && (
        <ProductCatalog
          products={catalogProducts}
          search={catalogSearch}
          onSearchChange={onCatalogSearchChange}
          categories={categories}
          activeCategory={activeCategory}
          onCategoryChange={onCategoryChange}
          onAdd={onAddProduct}
          saving={saving}
        />
      )}

      {loading ? (
        <div style={styles.loadingState}>Loading cart...</div>
      ) : (
        <>
          <CartTable
            entries={entries}
            products={products}
            onQtyChange={onQtyChange}
            onRemove={onRemove}
          />
          <OrderSummary
            totalDiscount={totalDiscount}
            totalPayable={totalPayable}
          />
        </>
      )}

      <div style={styles.footerButtons}>
        <button
          type="button"
          onClick={onClearCart}
          disabled={clearing || entries.length === 0}
          style={styles.clearBtn(clearing || entries.length === 0)}
        >
          {clearing ? "Clearing..." : "Clear Cart"}
        </button>
        <button
          type="button"
          onClick={onPlaceOrder}
          disabled={placeOrderDisabled}
          style={styles.placeOrderBtn(placeOrderDisabled)}
        >
          {placingOrder ? "Placing Order..." : "Place Order"}
        </button>
      </div>
    </div>
  );
}

export default CartUI;

CartUI.propTypes = {
  products: PropTypes.array,
  entries: PropTypes.array.isRequired,
  customer: PropTypes.string,
  customerName: PropTypes.string,
  customerSearch: PropTypes.string,
  customerDropdownOpen: PropTypes.bool,
  filteredCustomers: PropTypes.array,
  onCustomerSearchChange: PropTypes.func.isRequired,
  onSelectCustomer: PropTypes.func.isRequired,
  onAddCustomerClick: PropTypes.func.isRequired,
  catalogProducts: PropTypes.array,
  catalogSearch: PropTypes.string,
  onCatalogSearchChange: PropTypes.func.isRequired,
  categories: PropTypes.array,
  activeCategory: PropTypes.string,
  onCategoryChange: PropTypes.func.isRequired,
  onAddProduct: PropTypes.func.isRequired,
  onQtyChange: PropTypes.func.isRequired,
  onRemove: PropTypes.func.isRequired,
  onClearCart: PropTypes.func.isRequired,
  onPlaceOrder: PropTypes.func.isRequired,
  onShowHistory: PropTypes.func.isRequired,
  totalDiscount: PropTypes.number,
  totalPayable: PropTypes.number,
  saving: PropTypes.bool,
  clearing: PropTypes.bool,
  placingOrder: PropTypes.bool,
  loading: PropTypes.bool,
  errorMessage: PropTypes.string,
  successMessage: PropTypes.string,
  hasCustomer: PropTypes.bool,
};
