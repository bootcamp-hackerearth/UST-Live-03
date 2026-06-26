"use client";

import { useEffect, useState } from "react";
import {
  addItem,
  listItems,
  clearCart,
  getCartItems,
  deleteItem,
  addToCart,
} from "@/services/api";
import { useRouter } from "next/navigation";
import styles from "./CartPage.module.css";
import Select from "react-select";

export default function CartPage() {
  const router = useRouter();
  const [cartItems, setCartItems] = useState([]);
  const [cartId, setCartId] = useState("");
  const [loading, setLoading] = useState(true);
  const [products, setProducts] = useState([]);
  const [customers, setCustomers] = useState([]);
  const [selectedCustomer, setSelectedCustomer] = useState(null);
  const [productSearch, setProductSearch] = useState("");
  const TEMP_CART_ID = "WALK_IN";
  const [discount, setDiscount] = useState(0);

  const [showCustomerModal, setShowCustomerModal] = useState(false);
  const [newCustomer, setNewCustomer] = useState({
    identifier: "",
    phoneNo: "",
    email: "",
  });

  const selectStyles = {
    control: (base, state) => ({
      ...base,
      minHeight: "42px",
      borderRadius: "8px",
      borderColor: state.isFocused ? "#4f46e5" : "#d1d5db",
      boxShadow: "none",
      cursor: "pointer",
      "&:hover": {
        borderColor: "#4f46e5",
      },
    }),

    valueContainer: (base) => ({
      ...base,
      padding: "2px 10px",
    }),

    placeholder: (base) => ({
      ...base,
      color: "#9ca3af",
      fontSize: "14px",
    }),

    singleValue: (base) => ({
      ...base,
      color: "#111827",
      fontSize: "14px",
    }),

    menu: (base) => ({
      ...base,
      borderRadius: "8px",
      overflow: "hidden",
      zIndex: 9999,
    }),

    option: (base, state) => {
      let bg = "#fff";

      if (state.isSelected) bg = "#4f46e5";
      else if (state.isFocused) bg = "#eef2ff";

      return {
        ...base,
        backgroundColor: bg,
        color: state.isSelected ? "#fff" : "#111827",
        cursor: "pointer",
        fontSize: "14px",
      };
    },
  };

  const fetchProducts = async () => {
    try {
      const res = await listItems("price", {
        page: 0,
        sizePerPage: 100,
        sortField: "id",
        search: productSearch,
      });

      setProducts(res?.content || []);
    } catch (err) {
      console.error("Failed to load prices", err);
    }
  };

  const fetchCart = async (id) => {
    try {
      setLoading(true);

      const items = await getCartItems(id);

      setCartItems(items || []);
    } catch (err) {
      console.error("Cart load failed", err);
    } finally {
      setLoading(false);
    }
  };

  const fetchCustomers = async () => {
    try {
      const res = await listItems("customer", {
        page: 0,
        sizePerPage: 100,
      });

      setCustomers(res.content || []);
    } catch (err) {
      console.error("Failed to load customers", err);
    }
  };

  useEffect(() => {
    setCartId(TEMP_CART_ID);
    fetchCart(TEMP_CART_ID);

    fetchProducts();
    fetchCustomers();
  }, []);

  useEffect(() => {
    fetchProducts();
  }, [productSearch]);

  const removeItem = async (identifier) => {
    try {
      await deleteItem(
        "cartentry",
        identifier
      );

      fetchCart(cartId);
    } catch (err) {
      console.error("Delete failed", err);
    }
  };

  const increaseQuantity = async (item) => {
    try {
      await addToCart({
        cartId: item.cartId,
        product: item.product,
        quantity: 1,
      });

      fetchCart(cartId);
    } catch (err) {
      console.error("Increase failed", err);
    }
  };

  const decreaseQuantity = async (item) => {
    try {
      if (Number(item.quantity) <= 1) {
        removeItem(item.identifier);
        return;
      }

      await addToCart({
        cartId: item.cartId,
        product: item.product,
        quantity: -1,
      });

      fetchCart(cartId);
    } catch (err) {
      console.error("Decrease failed", err);
    }
  };

  const total = cartItems.reduce(
    (sum, item) => sum + Number(item.totalPrice || 0),
    0
  );

  const finalTotal = Math.max(
    Number(total) - Number(discount || 0),
    0
  );

  const addProductToCart = async (product) => {
    try {
      await addToCart({
        cartId,
        product: product.identifier,
        quantity: 1,
        discount: 0,
      });

      fetchCart(cartId);
    } catch (err) {
      console.error("Failed to add product", err);
    }
  };

  const handleClearCart = async () => {
    try {
      await clearCart(cartId);
      fetchCart(cartId);
    } catch (err) {
      console.error("Failed to clear cart", err);
    }
  };

  const handleAddCustomer = async () => {
    try {
      const payload = {
        identifier: newCustomer.identifier,
        phoneNo: newCustomer.phoneNo,
        email: newCustomer.email,
        address: "",
        partyType: null,

        billing: {
          addressLine: "",
          city: "",
          state: "",
          pincode: "",
          country: "",
        },

        shipping: {
          addressLine: "",
          city: "",
          state: "",
          pincode: "",
          country: "",
        },
      };

      await addItem("customer", payload);

      await fetchCustomers();

      const customerOption = {
        label: payload.identifier,
        value: payload.identifier,
      };

      setSelectedCustomer(customerOption);
      setCartId(payload.identifier);
      fetchCart(payload.identifier);

      setShowCustomerModal(false);

      setNewCustomer({
        identifier: "",
        phoneNo: "",
        email: "",
      });
    } catch (err) {
      console.error("Failed to add customer", err);
    }
  };

  const handleCheckout = () => {
    router.push(
      `/payment?cartId=${cartId}&total=${finalTotal}`
    );
  };

  if (loading) return <div>Loading cart...</div>;

  return (

    <div className={styles.pageContainer}>
      {/* LEFT SIDE */}
      <div className={styles.cartSection}>
        <div className={styles.tableHeader}>
          <h2 className={styles.cartTitle}>🛒 Cart</h2>

          <div className={styles.cartBadge}>
            {selectedCustomer
              ? `Customer: ${selectedCustomer.value}`
              : "Walk-in Customer"}
          </div>

          <div className={styles.customerActions}>
            <div style={{ flex: 1 }}>
              <Select
                isClearable
                styles={selectStyles}
                options={customers.map((c) => ({
                  label: c.identifier,
                  value: c.identifier,
                }))}
                value={selectedCustomer}
                onChange={(selected) => {
                  if (!selected) {
                    setSelectedCustomer(null);
                    setCartId(TEMP_CART_ID);
                    fetchCart(TEMP_CART_ID);
                    return;
                  }

                  setSelectedCustomer(selected);
                  setCartId(selected.value);
                  fetchCart(selected.value);
                }}
                placeholder="Choose customer..."
              />
            </div>

            <button
              className={styles.addCustomerBtn}
              onClick={() => setShowCustomerModal(true)}
            >
              + Customer
            </button>
          </div>
        </div>

        {cartItems.length === 0 ? (
          <div className={styles.emptyRow}>
            Your cart is empty
          </div>
        ) : (
          <>
            <div className={styles.tableWrapper}>
              <table className={styles.cartTable}>
                <thead>
                  <tr>
                    <th>Product</th>
                    <th>Unit Price</th>
                    <th>Qty</th>
                    <th>Total</th>
                    <th>Action</th>
                  </tr>
                </thead>

                <tbody>
                  {cartItems.map((item) => (
                    <tr key={item.identifier}>
                      <td>{item.product}</td>
                      <td>{item.unitPrice}</td>

                      <td>
                        <div className={styles.qtyControl}>
                          <button
                            className={styles.qtyBtn}
                            onClick={() => decreaseQuantity(item)}
                          >
                            -
                          </button>

                          <span className={styles.qtyValue}>
                            {item.quantity}
                          </span>

                          <button
                            className={styles.qtyBtn}
                            onClick={() => increaseQuantity(item)}
                          >
                            +
                          </button>
                        </div>
                      </td>

                      <td>{item.totalPrice}</td>

                      <td>
                        <button
                          className={styles.removeBtn}
                          onClick={() => removeItem(item.identifier)}
                        >
                          Remove
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            <div className={styles.summaryCard}>
              <h3 className={styles.summaryTitle}>Cart Summary</h3>

              <div className={styles.summaryRow}>
                <span>Subtotal</span>
                <span>₹{total.toFixed(2)}</span>
              </div>

              <div className={styles.summaryRow}>
                <span>Discount</span>

                <input
                  type="number"
                  min="0"
                  value={discount}
                  onChange={(e) => setDiscount(e.target.value)}
                  className={styles.discountInput}
                  placeholder="Enter discount"
                />
              </div>

              <div className={styles.summaryRow}>
                <span>Discount Applied</span>
                <span>- ₹{Number(discount || 0).toFixed(2)}</span>
              </div>

              <div className={styles.summaryTotal}>
                <span>Grand Total</span>
                <span>₹{finalTotal.toFixed(2)}</span>
              </div>

              <div className={styles.summaryActions}>
                <button
                  className={styles.clearCartBtn}
                  onClick={handleClearCart}
                  disabled={!cartItems.length}
                >
                  Clear Cart
                </button>

                <button
                  className={styles.checkoutBtn}
                  disabled={!cartItems.length}
                  onClick={handleCheckout}
                >
                  Checkout
                </button>

              </div>
            </div>
          </>
        )}
      </div>

      {/* RIGHT SIDE */}
      <div className={styles.productPanel}>
        <h3 className={styles.productPanelTitle}>
          Products
        </h3>

        <div style={{ marginBottom: "12px" }}>
          <input
            type="text"
            placeholder="Search products..."
            value={productSearch}
            onChange={(e) => setProductSearch(e.target.value)}
            className={styles.productSearch}
          />
        </div>

        <div className={styles.productGridWrapper}>
          <div className={styles.productGrid}>
            {products.map((product) => (
              <button
                key={product.identifier}
                type="button"
                className={styles.productCard}
                onClick={() => addProductToCart(product)}
              >
                <div className={styles.productName}>
                  {product.identifier}
                </div>

                <div className={styles.productPrice}>
                  ₹{product.sellingPrice}
                </div>
              </button>
            ))}
          </div>
        </div>
      </div>
      {showCustomerModal && (
        <div className={styles.modalOverlay}>
          <div className={styles.modalBox}>
            <h2>Add Customer</h2>

            <input
              className={styles.modalInput}
              placeholder="Customer Name"
              value={newCustomer.identifier}
              onChange={(e) =>
                setNewCustomer({
                  ...newCustomer,
                  identifier: e.target.value,
                })
              }
            />

            <input
              className={styles.modalInput}
              placeholder="Phone Number"
              value={newCustomer.phoneNo}
              onChange={(e) =>
                setNewCustomer({
                  ...newCustomer,
                  phoneNo: e.target.value,
                })
              }
            />

            <input
              className={styles.modalInput}
              placeholder="Email Address"
              value={newCustomer.email}
              onChange={(e) =>
                setNewCustomer({
                  ...newCustomer,
                  email: e.target.value,
                })
              }
            />

            <div className={styles.modalActions}>
              <button
                className={styles.modalAddBtn}
                onClick={handleAddCustomer}
              >
                Add
              </button>

              <button
                className={styles.modalCancelBtn}
                onClick={() => setShowCustomerModal(false)}
              >
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}