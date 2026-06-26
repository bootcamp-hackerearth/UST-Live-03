"use client";

import { useEffect, useState } from "react";
import api from "../api";

export default function CartPage() {

  const [customers, setCustomers] = useState([]);
  const [products, setProducts] = useState([]);
  const [price, setPrice] = useState([]);

  const [selectedCustomer, setSelectedCustomer] = useState("");
  const [cartItems, setCartItems] = useState([]);

  const [showPaymentPopup, setShowPaymentPopup] = useState(false);
const [paymentMethod, setPaymentMethod] = useState("CASH");

  const [productSearch, setProductSearch] = useState("");
  const [placedOrderDetails, setPlacedOrderDetails] = useState(null);

  const [showAddCustomer, setShowAddCustomer] = useState(false);
  const [newCustomer, setNewCustomer] = useState({
    name: "",
    identifier: "",
    phoneNo: "",
  });

  useEffect(() => {
    loadCustomers();
    loadProducts();
    loadPrice();
  }, []);

  const loadCustomers = async () => {
    const res = await api.post("/customer/list", {
      page: 0,
      sizePerPage: 100,
    });
    setCustomers(res.data.dtoList || []);
  };

  const loadProducts = async () => {
    const res = await api.post("/product/list-active");
    setProducts(res.data || []);
  };

  const loadPrice = async () => {
    const res = await api.post("/price/list", {
      page: 0,
      sizePerPage: 100,
    });
    setPrice(res.data.dtoList || []);
  };

  const getSellingPrice = (productId) => {
    const p = price.find(
      (pr) => pr.product === productId && pr.priceType === "sellingPrice"
    );
    return p?.sumPrice || 0;
  };

  const loadCart = async (cartIdentifier) => {
    const itemsRes = await api.post("/cartentry/getByCartId", {
      cartIdentifier,
    });
    setCartItems(itemsRes.data || []);
  };

  const addProductToCart = async (product) => {
    if (!selectedCustomer) {
      alert("Select customer first");
      return;
    }

    await api.post("/cartentry/add", {
      cartIdentifier: selectedCustomer,
      productIdentifier: product.identifier,
      quantity: 1,
    });

    loadCart(selectedCustomer);
  };

  const updateQuantity = async (productIdentifier, qty) => {
    await api.post("/cartentry/add", {
      cartIdentifier: selectedCustomer,
      productIdentifier,
      quantity: qty,
    });

    loadCart(selectedCustomer);
  };

  const removeItem = async (identifier) => {
    await api.delete("/cartentry/delete", {
      params: { identifier },
    });
    loadCart(selectedCustomer);
  };

  const clearCart = async () => {
    await Promise.all(
      cartItems.map((item) =>
        api.delete("/cartentry/delete", {
          params: { identifier: item.identifier },
        })
      )
    );
    loadCart(selectedCustomer);
  };
  const createCartIfNeeded = async (customerIdentifier) => {
  try {
    await api.post("/cart/add", {
      identifier: customerIdentifier,
    });
  } catch (error) {
    console.error("Cart creation error:", error);
  }
};

const handleCustomerSelect = async (customerIdentifier) => {
  setSelectedCustomer(customerIdentifier);

  if (!customerIdentifier) {
    setCartItems([]);
    return;
  }

  await createCartIfNeeded(customerIdentifier);
  await loadCart(customerIdentifier);
};

 
const isValidEmail = (email) =>
  typeof email === "string" &&
  email.length <= 254 &&
  /^[a-zA-Z0-9._%+-]{1,64}@[a-zA-Z0-9.-]{1,253}\.[a-zA-Z]{2,}$/.test(email);

  const isDuplicateEmail = (email) =>
    customers.some(
      (c) => c.identifier.toLowerCase() === email.toLowerCase()
    );

 const isValidPhone = (phone) =>
  /^\d{10}$/.test(phone);
 
  const saveCustomer = async () => {
    const { name, identifier, phoneNo } = newCustomer;

    if (!name || !identifier || !phoneNo) {
      alert("All fields are required");
      return;
    }

    if (!isValidEmail(identifier)) {
      alert("Invalid email format");
      return;
    }

    if (isDuplicateEmail(identifier)) {
      alert("Customer with this email already exists");
      return;
    }

    if (!isValidPhone(phoneNo)) {
      alert("Phone number must be exactly 10 digits");
      return;
    }

    const phone = Number(phoneNo);

    const payload = {
      name,
      identifier,
      phoneNo: phone,
      billingAddress: {
        addressLine: "",
        city: "",
        state: "",
        zipcode: 0,
        country: "",
        phoneNo: phone,
        addressType: "billing",
      },
      shippingAddress: {
        addressLine: "",
        city: "",
        state: "",
        zipcode: 0,
        country: "",
        phoneNo: phone,
        addressType: "shipping",
      },
    };

    try {
      await api.post("/customer/add", payload);
      await loadCustomers();

      await createCartIfNeeded(identifier);

setSelectedCustomer(identifier);

await loadCart(identifier);

setShowAddCustomer(false);

setNewCustomer({
  name: "",
  identifier: "",
  phoneNo: "",
});
    } catch (err) {
      console.error(err);
      alert("Failed to add customer");
    }
  };

  const filteredProducts = products.filter((product) =>
    product.identifier
      ?.toLowerCase()
      .includes(productSearch.toLowerCase())
  );

  const originalPrice = cartItems.reduce(
    (sum, item) => sum + Number(item.originalPrice || 0),
    0
  );

  const discount = cartItems.reduce(
    (sum, item) => sum + Number(item.discount || 0),
    0
  );

  const totalPrice = cartItems.reduce(
    (sum, item) => sum + Number(item.totalPrice || 0),
    0
  );
 const placeOrder = async () => {
  try {
    const customerObj = customers.find(
      (c) => c.identifier === selectedCustomer
    );

    if (!customerObj) {
      alert("Customer not found");
      return;
    }

    const response = await api.post("/order/place", {
      identifier: selectedCustomer,
      customerIdentifier: customerObj.identifier,
      paymentMethod,
    });

    const orderId = response.data.identifier;

    const detailsRes = await api.get("/order/get", {
      params: { identifier: orderId },
    });

    setPlacedOrderDetails(detailsRes.data);

    await clearCart();

    setShowPaymentPopup(false);

  } catch (error) {
    console.error(error);
    alert("Failed to place order");
  }
};

  return (
    <div style={{ padding: 24, background: "#f9fafb", minHeight: "100vh", color: "#111827" }}>
      <div style={{ display: "grid", gridTemplateColumns: "2fr 1fr", gap: 24 }}>

        <div style={{ background: "#ffffff", borderRadius: 12, padding: 20, border: "1px solid #e5e7eb" }}>
          <h2 style={{ fontSize: 20, fontWeight: 700, marginBottom: 16 }}>🛒 Cart</h2>

          <div style={{ display: "flex", gap: 10, marginBottom: 16 }}>
            <select
              value={selectedCustomer}
             onChange={(e) =>
  handleCustomerSelect(e.target.value)
}
              style={{
                flex: 1,
                border: "1px solid #e5e7eb",
                borderRadius: 8,
                padding: 12,
              }}
            >
              <option value="">Select Customer</option>
              {customers.map((c) => (
                <option key={c.identifier} value={c.identifier}>
                  {c.identifier}
                </option>
              ))}
            </select>

            <button
              onClick={() => setShowAddCustomer(true)}
              style={{
                background: "#0f766e",
                color: "#fff",
                padding: "12px 14px",
                borderRadius: 8,
                whiteSpace: "nowrap",
              }}
            >
              + Add Customer
            </button>
          </div>

          {showAddCustomer && (
            <div style={{
              position: "fixed",
              top: 0, left: 0, right: 0, bottom: 0,
              background: "rgba(0,0,0,0.4)",
              display: "flex",
              justifyContent: "center",
              alignItems: "center"
            }}>
              <div style={{ background: "#fff", padding: 20, borderRadius: 10, width: 300 }}>
                <h3>Add Customer</h3>

                <input
                  placeholder="Name"
                  value={newCustomer.name}
                  onChange={(e) =>
                    setNewCustomer({ ...newCustomer, name: e.target.value })
                  }
                  style={{ width: "100%", marginBottom: 8 }}
                />

                <input
                  placeholder="Email"
                  value={newCustomer.identifier}
                  onChange={(e) =>
                    setNewCustomer({ ...newCustomer, identifier: e.target.value })
                  }
                  style={{ width: "100%", marginBottom: 8 }}
                />

                <input
                  placeholder="Phone"
                  value={newCustomer.phoneNo}
                  onChange={(e) => {
                   const val = e.target.value.replaceAll(/\D/g, "");
                    setNewCustomer({ ...newCustomer, phoneNo: val });
                  }}
                  maxLength={10}
                  style={{ width: "100%" }}
                />

                <div style={{ marginTop: 10, display: "flex", justifyContent: "space-between" }}>
                  <button onClick={saveCustomer}>Save</button>
                  <button onClick={() => setShowAddCustomer(false)}>Cancel</button>
                </div>
              </div>
            </div>
          )}

          <button
            onClick={clearCart}
            style={{
              background: "#111827",
              color: "#fff",
              padding: "8px 16px",
              borderRadius: 6,
              marginBottom: 12,
            }}
          >
            Clear Cart
          </button>

          <div style={{ overflowX: "auto" }}>
            <table style={{ width: "100%", borderCollapse: "collapse" }}>
              <thead style={{ background: "#f3f4f6" }}>
                <tr>
                  <th style={{ padding: 12, textAlign: "left" }}>Product</th>
                  <th style={{ padding: 12 }}>MRP</th>
                  <th style={{ padding: 12 }}>Discount/Unit</th>
                  <th style={{ padding: 12 }}>Price</th>
                  <th style={{ padding: 12 }}>Qty</th>
                  <th style={{ padding: 12 }}>Total</th>
                  <th style={{ padding: 12 }}>Action</th>
                </tr>
              </thead>

              <tbody>
                {cartItems.map((item) => (
                  <tr key={item.identifier} style={{ borderBottom: "1px solid #e5e7eb" }}>
                    <td style={{ padding: 12 }}>{item.productIdentifier}</td>
                    <td style={{ padding: 12, textAlign: "center" }}>
                      ₹{(
                        Number(item.unitPrice || 0) +
                        Number(item.discount || 0) / Number(item.quantity || 1)
                      ).toFixed(2)}
                    </td>
                    <td style={{ padding: 12, textAlign: "center" }}>
                      ₹{(
                        Number(item.discount || 0) /
                        Number(item.quantity || 1)
                      ).toFixed(2)}
                    </td>
                    <td style={{ padding: 12, textAlign: "center" }}>₹{item.unitPrice}</td>
                    <td style={{ padding: 12, textAlign: "center" }}>
                      <div style={{ display: "flex", justifyContent: "center", gap: 10 }}>
                        <button onClick={() => {
                          if (item.quantity <= 1) removeItem(item.identifier);
                          else updateQuantity(item.productIdentifier, -1);
                        }}>−</button>
                        <span>{item.quantity}</span>
                        <button onClick={() => updateQuantity(item.productIdentifier, 1)}>+</button>
                      </div>
                    </td>
                    <td style={{ padding: 12, textAlign: "center", fontWeight: 600 }}>
                      ₹{item.totalPrice}
                    </td>
                    <td style={{ padding: 12, textAlign: "center" }}>
                      <button
                        onClick={() => removeItem(item.identifier)}
                        style={{
                          background: "#dc2626",
                          color: "#fff",
                          padding: "6px 12px",
                          borderRadius: 4,
                        }}
                      >
                        Remove
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

         <div style={{ marginTop: 20, textAlign: "right" }}>
  <p>Original: ₹{originalPrice.toFixed(2)}</p>
  <p>Discount: ₹{discount.toFixed(2)}</p>
  <h2>Total: ₹{totalPrice.toFixed(2)}</h2>

  <button
  onClick={() => {
    if (!selectedCustomer) {
      alert("Select customer first");
      return;
    }

    if (cartItems.length === 0) {
      alert("Cart is empty");
      return;
    }

    setShowPaymentPopup(true);
  }}
  style={{
    background: "#16a34a",
    color: "#fff",
    padding: "10px 20px",
    borderRadius: 8,
    marginTop: 12,
    fontWeight: 600,
  }}
>
  Place Order
</button>
</div>

        </div>

        <div style={{ background: "#ffffff", borderRadius: 12, padding: 20, border: "1px solid #e5e7eb" }}>
          <h2 style={{ marginBottom: 12 }}>Products</h2>

          <input
            type="text"
            placeholder="Search Product"
            value={productSearch}
            onChange={(e) => setProductSearch(e.target.value)}
            style={{
              width: "100%",
              border: "1px solid #e5e7eb",
              borderRadius: 8,
              padding: 8,
              marginBottom: 12,
            }}
          />

          <div style={{ maxHeight: "70vh", overflowY: "auto", paddingRight: 4 }}>
            {filteredProducts.map((product) => (
              <button
  key={product.identifier}
  type="button"
  onClick={() => addProductToCart(product)}
  style={{
    width: "100%",
    border: "1px solid #e5e7eb",
    borderRadius: 8,
    padding: 12,
    marginBottom: 10,
    cursor: "pointer",
    textAlign: "left",
    background: "#fff",
  }}
>
  <h4>{product.identifier}</h4>

  <p>₹{getSellingPrice(product.identifier)}</p>

  <div
    style={{
      width: "100%",
      background: "#0f766e",
      color: "#fff",
      padding: 6,
      borderRadius: 6,
      textAlign: "center",
    }}
  >
    Add
  </div>
</button>

            ))}
          </div>
        </div>

      </div>
      {showPaymentPopup && (
  <div
    style={{
      position: "fixed",
      top: 0,
      left: 0,
      right: 0,
      bottom: 0,
      background: "rgba(0,0,0,0.5)",
      display: "flex",
      justifyContent: "center",
      alignItems: "center",
      zIndex: 9999,
    }}
  >
    <div
      style={{
        background: "#fff",
        width: 400,
        padding: 24,
        borderRadius: 12,
      }}
    >
      <h3
        style={{
          marginBottom: 20,
        }}
      >
        Select Payment Method
      </h3>

      <select
        value={paymentMethod}
        onChange={(e) =>
          setPaymentMethod(e.target.value)
        }
        style={{
          width: "100%",
          padding: 12,
          borderRadius: 8,
          border: "1px solid #ddd",
          marginBottom: 20,
        }}
      >
        <option value="CASH">Cash</option>
        <option value="CARD">Card</option>
        <option value="ONLINE">Online</option>
      </select>

      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
        }}
      >
        <button
          onClick={() =>
            setShowPaymentPopup(false)
          }
          style={{
            padding: "10px 20px",
          }}
        >
          Cancel
        </button>

        <button
  onClick={placeOrder}
  style={{
    background: "#16a34a",
    color: "#fff",
    padding: "10px 20px",
    borderRadius: 8,
  }}
>
  Place Order
</button>
      </div>
    </div>
  </div>
)}

      {placedOrderDetails && (
        <div
          style={{
            position: "fixed",
            inset: 0,
            background: "rgba(0,0,0,0.4)",
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            zIndex: 9999,
          }}
        >
          <div
            style={{
              width: "900px",
              maxHeight: "90vh",
              overflowY: "auto",
              background: "#fff",
              borderRadius: 20,
              padding: 24,
            }}
            id="print-area"
          >
            <div style={{ display: "flex", justifyContent: "space-between" }}>
              <h2>Order Details</h2>

              <button onClick={() => setPlacedOrderDetails(null)}>
                ✕
              </button>
            </div>

            <div style={{
              display: "grid",
              gridTemplateColumns: "1fr 1fr",
              gap: 20,
              marginBottom: 20
            }}>
              <div>
                <p><b>Order No:</b> {placedOrderDetails.identifier}</p>
                <p><b>Customer:</b> {placedOrderDetails.customerEmail || placedOrderDetails.customerIdentifier}</p>
              </div>

              <div>
                <p><b>Payment:</b> {placedOrderDetails.paymentMethod}</p>
                <p><b>Date:</b> {new Date(placedOrderDetails.orderPlacedTime).toLocaleString()}</p>
              </div>
            </div>

           <table style={{ width: "100%", borderCollapse: "collapse", marginTop: 20 }}>
  <thead>
    <tr>
      <th style={{ padding: 10, textAlign: "left" }}>Product</th>
      <th style={{ padding: 10, textAlign: "left" }}>Qty</th>
      <th style={{ padding: 10, textAlign: "left" }}>Price</th>
      <th style={{ padding: 10, textAlign: "left" }}>Discount</th>
      <th style={{ padding: 10, textAlign: "left" }}>Total</th>
    </tr>
  </thead>

  <tbody>
    {placedOrderDetails.entryList?.map((item) => (
  <tr
    key={
      item.identifier ||
      `${item.productIdentifier}-${item.quantity}-${item.unitPrice}`
    }
  >
        <td style={{ padding: 10, textAlign: "left" }}>
          {item.productIdentifier}
        </td>

        <td style={{ padding: 10, textAlign: "left" }}>
          {item.quantity}
        </td>

        <td style={{ padding: 10, textAlign: "left" }}>
          ₹{item.unitPrice}
        </td>

        <td style={{ padding: 10, textAlign: "left" }}>
          ₹{item.discount}
        </td>

        <td style={{ padding: 10, textAlign: "left" }}>
          ₹{item.totalPrice}
        </td>
      </tr>
    ))}
  </tbody>
</table>

            <div style={{ textAlign: "right", marginTop: 20 }}>
              <p>Original: ₹{placedOrderDetails.originalPrice}</p>
              <p>Discount: ₹{placedOrderDetails.discount}</p>
              <h2>Total: ₹{placedOrderDetails.totalPrice}</h2>
            </div>

            <div style={{ marginTop: 20, textAlign: "right" }}>
              <button
                onClick={() => globalThis.print()}
                style={{
                  background: "#111827",
                  color: "#fff",
                  padding: "10px 20px",
                  borderRadius: 8,
                }}
              >
                🖨 Print Receipt
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}