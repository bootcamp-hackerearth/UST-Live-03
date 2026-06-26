"use client";

import { useEffect, useState } from "react";
import "./cart.css";

import {
  createCart,
  getCartEntries,
  getListItems,
  updateCartEntry,
  addCartEntry,
  deleteItem,
  addItem,
  createOrder,
} from "@/services/api";

export default function CartPage() {
  const [showPaymentDialog, setShowPaymentDialog] = useState(false);

  // NEW BILL DIALOG
  const [showBillDialog, setShowBillDialog] = useState(false);

  const [placedOrder, setPlacedOrder] = useState(null);

  const [paymentMethod, setPaymentMethod] = useState("Cash");

  const [customers, setCustomers] = useState([]);

  const [products, setProducts] = useState([]);

  const [entries, setEntries] = useState([]);

  const [cart, setCart] = useState(null);

  const [selectedCustomer, setSelectedCustomer] = useState("");

  const [productSearch, setProductSearch] = useState("");

  const [showCustomerModal, setShowCustomerModal] = useState(false);

  const [customerForm, setCustomerForm] = useState({
    identifier: "",
    phoneno: "",
    email: "",
    address: "",
    partytype: "Customer",
  });

  const filteredProducts = products.filter((product) =>
    product.identifier?.toLowerCase().includes(productSearch.toLowerCase()),
  );

  const totalDiscount = entries.reduce(
    (sum, entry) => sum + Number(entry.discount || 0),
    0,
  );

  const subtotal = entries.reduce(
    (sum, entry) => sum + Number(entry.totalPrice || 0),
    0,
  );

  const finalTotal = subtotal - totalDiscount;

  const loadCustomers = async () => {
    try {
      const data = await getListItems("customer");

      setCustomers(data || []);
    } catch (error) {
      console.error(error);
    }
  };

  const saveCustomer = async () => {
    if (!customerForm.identifier.trim()) {
      alert("Customer Name is required");
      return;
    }

    try {
      const payload = {
        identifier: customerForm.identifier.trim(),
        phoneno: customerForm.phoneno,
        email: customerForm.email,
        address: customerForm.address,
        partytype: customerForm.partytype,

        billing: {
          identifier: customerForm.identifier.trim(),
          addressLine: customerForm.address || "",
          city: "",
          state: "",
          pincode: "",
          country: "",
        },

        shipping: {
          identifier: customerForm.identifier.trim(),
          addressLine: customerForm.address || "",
          city: "",
          state: "",
          pincode: "",
          country: "",
        },
      };

      await addItem("customer", payload);

      await loadCustomers();

      setSelectedCustomer(payload.identifier);

      setCustomerForm({
        identifier: "",
        phoneno: "",
        email: "",
        address: "",
        partytype: "Customer",
      });

      setShowCustomerModal(false);

      alert("Customer added successfully");
    } catch (error) {
      console.error("Customer Save Error:", error);

      alert("Failed to add customer");
    }
  };

  const loadProducts = async () => {
    try {
      const data = await getListItems("price");

      setProducts(data || []);
    } catch (error) {
      console.error(error);
    }
  };

  const loadCart = async (cartId) => {
    try {
      const entryData = await getCartEntries(cartId);

      setEntries(entryData || []);

      setCart({
        identifier: cartId,
        totalPrice:
          entryData?.reduce(
            (sum, item) => sum + Number(item.totalPrice || 0),
            0,
          ) || 0,
      });
    } catch (error) {
      console.error(error);
    }
  };

  useEffect(() => {
    loadCustomers();
    loadProducts();

    const cartId = localStorage.getItem("cartId");

    if (cartId) {
      loadCart(cartId);
    }
  }, []);

  const handlePrintBill = () => {
    globalThis.print();
  };

  const handleCreateCart = async () => {
    if (!selectedCustomer) {
      alert("Please select customer");
      return;
    }

    try {
      const cartId = `CART_${Date.now()}`;

      await createCart({
        identifier: cartId,
        customer: selectedCustomer,
        discount: 0,
        totalPrice: 0,
      });

      localStorage.setItem("cartId", cartId);

      await loadCart(cartId);

      alert("Cart created successfully");
    } catch (error) {
      console.error(error);
    }
  };
  const handlePlaceOrder = async () => {
    try {
      if (!cart?.identifier) {
        alert("Cart not found");
        return;
      }

      if (entries.length === 0) {
        alert("Cart is empty");
        return;
      }

      const confirmed = globalThis.confirm(
        `Confirm order using ${paymentMethod}?`,
      );

      if (!confirmed) {
        return;
      }

      const order = await createOrder(cart.identifier, paymentMethod);

      if (!order) {
        alert("Failed to create order");
        return;
      }

      // STORE BILL DATA
      setPlacedOrder({
        orderId: order.identifier || order.orderId || `ORD-${Date.now()}`,

        customer: selectedCustomer,

        paymentMethod,

        items: [...entries],

        subtotal,

        discount: totalDiscount,

        total: finalTotal,

        orderDate: new Date().toLocaleString(),
      });

      // CLEAR CART
      setEntries([]);
      setCart(null);

      localStorage.removeItem("cartId");

      setShowPaymentDialog(false);

      // SHOW BILL POPUP
      setShowBillDialog(true);
    } catch (error) {
      console.error(error);

      alert("Failed to place order");
    }
  };

  const handleAddToCart = async (product) => {
    try {
      const cartId = localStorage.getItem("cartId");

      if (!cartId) {
        alert("Please create a cart first");
        return;
      }

      await addCartEntry({
        cartId,
        product: product.identifier,
        quantity: 1,
        discount: 0,
      });

      await loadCart(cartId);
    } catch (error) {
      console.error(error);
    }
  };

  const handleClearCart = async () => {
    const confirmed = globalThis.confirm(
      "Are you sure you want to clear the entire cart?",
    );

    if (!confirmed) {
      return;
    }

    try {
      const result = await deleteItem("cart", cart?.identifier);

      if (result) {
        setEntries([]);

        setCart({
          identifier: cart?.identifier,
          totalPrice: 0,
        });

        localStorage.removeItem("cartId");

        alert("Cart cleared successfully");
      } else {
        alert("Failed to clear cart");
      }
    } catch (error) {
      console.error(error);

      alert("Failed to clear cart");
    }
  };

  const handleIncrease = async (entry) => {
    try {
      await updateCartEntry({
        identifier: entry.identifier,

        cartId: entry.cartId,

        quantity: Number(entry.quantity) + 1,
      });

      const cartId = localStorage.getItem("cartId");

      if (cartId) {
        await loadCart(cartId);
      }
    } catch (error) {
      console.error(error);
    }
  };

  const handleDecrease = async (entry) => {
    if (Number(entry.quantity) <= 1) {
      return;
    }

    try {
      await updateCartEntry({
        identifier: entry.identifier,

        cartId: entry.cartId,

        quantity: Number(entry.quantity) - 1,
      });

      const cartId = localStorage.getItem("cartId");

      if (cartId) {
        await loadCart(cartId);
      }
    } catch (error) {
      console.error(error);
    }
  };

  return (
    <div className="page">
      <div className="header">
        <h1 className="page-title">POS Cart</h1>
      </div>

      {/* CUSTOMER SECTION */}

      <div className="card">
        <h3 className="section-title">Customer Selection</h3>

        <div className="customer-row">
          <select
            className="select"
            value={selectedCustomer}
            onChange={(e) => setSelectedCustomer(e.target.value)}
          >
            <option value="">Select Customer</option>

            {customers.map((customer) => (
              <option key={customer.identifier} value={customer.identifier}>
                {customer.identifier}
              </option>
            ))}
          </select>

          <button
            className="primary-button"
            onClick={() => setShowCustomerModal(true)}
          >
            + New Customer
          </button>

          <button className="primary-button" onClick={handleCreateCart}>
            Create Cart
          </button>
        </div>
      </div>

      {/* CONTENT LAYOUT */}

      <div className="content-layout">
        {" "}
        {/* LEFT SIDE */}
        <div>
          <div className="card">
            <h3 className="section-title">Product Catalog</h3>

            <input
              type="text"
              placeholder="Search products..."
              value={productSearch}
              onChange={(e) => setProductSearch(e.target.value)}
              className="search-box"
            />

            <div className="product-grid">
              {filteredProducts.map((price) => (
                <div key={price.identifier} className="product-card">
                  <div className="product-image">
                    {price.identifier?.charAt(0)}
                  </div>

                  <div className="product-body">
                    <div className="product-title-card">{price.identifier}</div>

                    <div
                      style={{
                        display: "flex",
                        justifyContent: "space-between",
                        alignItems: "center",
                        marginBottom: "10px",
                      }}
                    >
                      <span
                        style={{
                          fontSize: "12px",
                          color: "#475569",
                        }}
                      >
                        Selling Price
                      </span>

                      <span
                        style={{
                          fontSize: "15px",
                          fontWeight: "700",
                          color: "#059669",
                        }}
                      >
                        ₹{Number(price.sellingPrice || 0).toLocaleString()}
                      </span>
                    </div>

                    <div className="stock-badge">In Stock</div>

                    <button
                      className="add-button"
                      onClick={() => handleAddToCart(price)}
                    >
                      + Add to Cart
                    </button>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
        {/* CART SUMMARY */}
        <div>
          <div className="cart-summary-card">
            <div className="cart-summary-header">
              <div className="cart-summary-title">Cart Summary</div>

              <div className="cart-badge">{entries.length} Items</div>
            </div>

            <div className="cart-items-list">
              {entries.length > 0 ? (
                entries.map((entry) => (
                  <div key={entry.identifier} className="cart-item-card">
                    <div className="cart-item-info">
                      <div className="cart-item-avatar">
                        {entry.product?.charAt(0)}
                      </div>

                      <div>
                        <div className="cart-item-name">{entry.product}</div>

                        <div className="cart-item-price">
                          ₹{Number(entry.unitPrice || 0).toLocaleString()}
                        </div>

                        <div
                          style={{
                            fontSize: "11px",
                            color: "#16a34a",
                          }}
                        >
                          Discount: ₹
                          {Number(entry.discount || 0).toLocaleString()}
                        </div>
                      </div>
                    </div>

                    <div className="cart-qty-container">
                      <button
                        className="cart-qty-btn"
                        onClick={() => handleDecrease(entry)}
                      >
                        −
                      </button>

                      <span className="cart-qty-text">{entry.quantity}</span>

                      <button
                        className="cart-qty-btn"
                        onClick={() => handleIncrease(entry)}
                      >
                        +
                      </button>
                    </div>
                  </div>
                ))
              ) : (
                <div className="empty-cart">No items in cart</div>
              )}
            </div>

            <div className="cart-footer">
              <div className="summary-row">
                <span>Subtotal</span>

                <span>₹{subtotal.toLocaleString()}</span>
              </div>

              <div className="summary-row">
                <span>Discount</span>

                <span>₹{totalDiscount.toLocaleString()}</span>
              </div>

              <div className="summary-total-row">
                <span className="summary-total-label">Total</span>

                <span className="summary-total-value">
                  ₹{finalTotal.toLocaleString()}
                </span>
              </div>

              <button
                className="checkout-button"
                onClick={() => {
                  if (!cart) {
                    alert("Please create a cart first");
                    return;
                  }

                  if (entries.length === 0) {
                    alert("Cart is empty");
                    return;
                  }

                  setShowPaymentDialog(true);
                }}
              >
                Proceed to Checkout
              </button>

              <button className="clear-cart-button" onClick={handleClearCart}>
                Clear Cart
              </button>
            </div>
          </div>
        </div>
      </div>
      {/* PAYMENT DIALOG */}

      {showPaymentDialog && (
        <div className="modal-overlay">
          <div className="payment-modal">
            <h2 className="payment-title">Checkout</h2>

            <div className="payment-total">
              Total Amount: ₹{finalTotal.toLocaleString()}
            </div>

            <div className="payment-options">
              <label className="payment-option">
                <input
                  type="radio"
                  value="Cash"
                  checked={paymentMethod === "Cash"}
                  onChange={(e) => setPaymentMethod(e.target.value)}
                />
                {' '}
                Cash on Delivery
              </label>

              <label className="payment-option">
                <input
                  type="radio"
                  value="UPI"
                  checked={paymentMethod === "UPI"}
                  onChange={(e) => setPaymentMethod(e.target.value)}
                />
                {' '}
                UPI
              </label>

              <label className="payment-option">
                <input
                  type="radio"
                  value="Credit Card"
                  checked={paymentMethod === "Credit Card"}
                  onChange={(e) => setPaymentMethod(e.target.value)}
                />
                {' '}
                Credit Card
              </label>

              <label className="payment-option">
                <input
                  type="radio"
                  value="Debit Card"
                  checked={paymentMethod === "Debit Card"}
                  onChange={(e) => setPaymentMethod(e.target.value)}
                />
                {' '}
                Debit Card
              </label>
            </div>

            <div className="payment-actions">
              <button
                className="cancel-button"
                onClick={() => setShowPaymentDialog(false)}
              >
                Cancel
              </button>

              <button className="place-order-button" onClick={handlePlaceOrder}>
                Place Order
              </button>
            </div>
          </div>
        </div>
      )}

      {/* BILL / ORDER SUMMARY */}

      {showBillDialog && placedOrder && (
        <div className="modal-overlay">
          <div className="bill-modal">
            <h2>Order Summary</h2>

            <div id="bill-content">
              <p>
                <strong>Order ID:</strong> {placedOrder.orderId}
              </p>

              <p>
                <strong>Customer:</strong> {placedOrder.customer}
              </p>

              <p>
                <strong>Date:</strong> {placedOrder.orderDate}
              </p>

              <p>
                <strong>Payment:</strong> {placedOrder.paymentMethod}
              </p>

              <hr />

              {placedOrder.items.map((item) => (
                <div
                  key={item.identifier}
                  style={{
                    display: "flex",
                    justifyContent: "space-between",
                    marginBottom: "8px",
                  }}
                >
                  <span>
                    {item.product} x {item.quantity}
                  </span>

                  <span>₹{Number(item.totalPrice || 0).toLocaleString()}</span>
                </div>
              ))}

              <hr />

              <p>
                <strong>Subtotal:</strong> ₹
                {placedOrder.subtotal.toLocaleString()}
              </p>

              <p>
                <strong>Discount:</strong> ₹
                {placedOrder.discount.toLocaleString()}
              </p>

              <h3>Total: ₹{placedOrder.total.toLocaleString()}</h3>
            </div>

            <div className="payment-actions">
              <button className="place-order-button" onClick={handlePrintBill}>
                Print Bill
              </button>

              <button
                className="cancel-button"
                onClick={() => setShowBillDialog(false)}
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}

      {/* CUSTOMER MODAL */}

      {showCustomerModal && (
        <div className="modal-overlay">
          <div className="payment-modal">
            <h2>Add Customer</h2>

            <input
              className="search-box"
              placeholder="Customer Name"
              value={customerForm.identifier}
              onChange={(e) =>
                setCustomerForm({
                  ...customerForm,
                  identifier: e.target.value,
                })
              }
            />

            <input
              className="search-box"
              placeholder="Phone Number"
              value={customerForm.phoneno}
              onChange={(e) =>
                setCustomerForm({
                  ...customerForm,
                  phoneno: e.target.value,
                })
              }
            />

            <input
              className="search-box"
              placeholder="Email"
              value={customerForm.email}
              onChange={(e) =>
                setCustomerForm({
                  ...customerForm,
                  email: e.target.value,
                })
              }
            />

            <textarea
              className="search-box"
              placeholder="Address"
              value={customerForm.address}
              onChange={(e) =>
                setCustomerForm({
                  ...customerForm,
                  address: e.target.value,
                })
              }
            />

            <div className="payment-actions">
              <button
                className="cancel-button"
                onClick={() => setShowCustomerModal(false)}
              >
                Cancel
              </button>

              <button className="place-order-button" onClick={saveCustomer}>
                Save Customer
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
