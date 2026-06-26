"use client";

import { useEffect, useState } from "react";
import api, { listItems, addItem } from "@/services/api";
import "./cart.css";

export default function CartPage() {
  const [showBillModal, setShowBillModal] = useState(false);
const [billData, setBillData] = useState(null);
  const [products, setProducts] = useState([]);
  const [filteredProducts, setFilteredProducts] = useState([]);
  const [productSearch, setProductSearch] = useState("");
  const [showPaymentModal, setShowPaymentModal] = useState(false);

  const [paymentMethod, setPaymentMethod] =
  useState("CASH");
  const [cart, setCart] = useState({});
  const [cartEntries, setCartEntries] = useState([]);

  const [customers, setCustomers] = useState([]);
  const [selectedCustomer, setSelectedCustomer] = useState("");

  const [customerSearch, setCustomerSearch] = useState("");
  const [showDropdown, setShowDropdown] = useState(false);

  const [loading, setLoading] = useState(true);

  // COUPON STATES 
  const [couponCode, setCouponCode] = useState("");
  const [appliedCoupon, setAppliedCoupon] = useState(null);
  const [couponMessage, setCouponMessage] = useState("");
  const [showCustomerModal, setShowCustomerModal] = useState(false);

const [newCustomer, setNewCustomer] = useState({
  identifier: "",
  email: "",
  phoneno: "",
  partytype: "Customer",
});

  // INIT 
  useEffect(() => {
    loadCustomers();
    initializeProducts();
  }, []);

  // CUSTOMERS 
  const loadCustomers = async () => {
    try {
      const res = await api.get("/api/customer/list");
      setCustomers(res.data || []);
    } catch (err) {
      console.error("Customer load error:", err);
    }
  };


  //  PRODUCTS 
  const initializeProducts = async () => {
    try {
      const response = await listItems("price", {
        page: 0,
        sizePerPage: 100,
        sortField: "identifier",
      });

      const data = response?.content || [];
      setProducts(data);
      setFilteredProducts(data);
    } catch (err) {
      console.error("Product load error:", err);
    } finally {
      setLoading(false);
    }
  };

  const handleProductSearch = (value) => {
    setProductSearch(value);

    const filtered = products.filter((p) =>
      (p.identifier || "")
        .toLowerCase()
        .includes(value.toLowerCase())
    );

    setFilteredProducts(filtered);
  };

  // CART 
  const loadCart = async (customerEmail) => {
  if (!customerEmail) return;

  const currentCustomer = customerEmail;

  try {
    const cartRes = await api.get("/api/cart/get", {
      params: { identifier: currentCustomer },
    });

    const entryRes = await api.get("/api/cartentry/getByCartId", {
      params: { cartId: currentCustomer },
    });

    const cartData = cartRes.data || {};

    setCart(cartData);
    setCartEntries(entryRes.data || []);

    // IMPORTANT: restore coupon from backend
    if (cartData.couponCode) {
      setAppliedCoupon(cartData.couponCode);
    } else {
      setAppliedCoupon(null);
    }

  } catch (err) {
    console.error("Cart load error:", err);
    setCart({});
    setCartEntries([]);
    setAppliedCoupon(null);
  }
};

  const refreshCart = async () => {
    if (!selectedCustomer) return;
    await loadCart(selectedCustomer);
  };

  // CUSTOMER SELECT 
  const handleSelectCustomer = (email) => {
    setSelectedCustomer(email);
    setShowDropdown(false);
    loadCart(email);
  };

  // CART ACTIONS 
  const addToCart = async (product) => {
    if (!selectedCustomer) return;

    try {
      await addItem("cartentry", {
        cartId: selectedCustomer,
        product: product.identifier,
        quantity: 1,
        discount: 0,
      });

      await refreshCart();
    } catch (err) {
      console.error("Add to cart error:", err);
    }
  };

  const increaseQty = async (entry) => {
  try {
    await api.put("/api/cartentry/updateQuantity", {
      identifier: entry.identifier,
      cartId: selectedCustomer,
      quantity: Number(entry.quantity) + 1,
    });

    await loadCart(selectedCustomer);
  } catch (err) {
    console.error(err);
  }
};

 const decreaseQty = async (entry) => {
  try {
    const newQty = Number(entry.quantity) - 1;

    if (newQty <= 0) {
      await removeItem(entry.identifier);
      return;
    }

    await api.put("/api/cartentry/updateQuantity", {
      identifier: entry.identifier,
      cartId: selectedCustomer,
      quantity: newQty,
    });

    await loadCart(selectedCustomer);
  } catch (err) {
    console.error(err);
  }
};

  const removeItem = async (identifier) => {
    try {
      await api.delete("/api/cartentry/delete", {
        params: {
          identifier,
          cartId: selectedCustomer,
        },
      });

      await refreshCart();
    } catch (err) {
      console.error(err);
    }
  };

  const clearCart = async () => {
    try {
      await api.delete("/api/cart/deleteAll", {
        params: {
          cartId: selectedCustomer,
        },
      });
      setCart({});
      setCartEntries([]);
      setAppliedCoupon(null);
      setCouponCode("");
    } catch (err) {
      console.error(err);
    }
  };

  // COUPON LOGIC 
 const applyCoupon = async () => {
  if (!couponCode) return;

  try {
    const res = await api.post("/api/cart/applyCoupon", null, {
      params: {
        cartId: selectedCustomer,
        couponCode: couponCode,
      },
    });

    const updatedCart = res.data;

    setCart(updatedCart);
    setCartEntries(updatedCart.cartEntries || []);

    setAppliedCoupon(couponCode);

    setCouponMessage("Coupon applied successfully 🎉");

  } catch (err) {
  console.error("Coupon apply error:", err);
  setCouponMessage("Coupon not found or invalid ❌");
  setAppliedCoupon(null);
}
};
const removeCoupon = async () => {
  if (!selectedCustomer) return;

  try {
    const res = await api.delete("/api/cart/removeCoupon", {
      params: {
        cartId: selectedCustomer,
      },
    });

    const updatedCart = res.data;

    setCart(updatedCart);
    setCartEntries(updatedCart.cartEntries || []);

    setAppliedCoupon(null);
    setCouponCode("");
    setCouponMessage("Coupon removed successfully ❌");
  } catch (err) {
    console.error("Remove coupon error:", err);
  }
};

  // LOADING 
  if (loading) {
    return <div className="loading">Loading...</div>;
  }

  return (
    
    <div className="posContainer">

      {/* PRODUCTS  */}
      <div className="productsSection">

        {/* CUSTOMER SELECT */}
        <div className="customerBox">
          <button
            className="customerSelector"
            onClick={() => setShowDropdown(!showDropdown)}
          >
            {selectedCustomer || "Select Customer Email ▼"}
          </button>

{showCustomerModal && (
  <div className="modalOverlay">
    <div className="customerModal">

      <h3>Add Customer</h3>

      <input
        type="text"
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
        type="email"
        placeholder="Email"
        value={newCustomer.email}
        onChange={(e) =>
          setNewCustomer({
            ...newCustomer,
            email: e.target.value,
          })
        }
      />

      <input
        type="text"
        placeholder="Phone Number"
        value={newCustomer.phoneno}
        onChange={(e) =>
          setNewCustomer({
            ...newCustomer,
            phoneno: e.target.value,
          })
        }
      />

      <div className="modalActions">

        <button
          onClick={() => {
            setShowCustomerModal(false);

            setNewCustomer({
              identifier: "",
              email: "",
              phoneno: "",
              partytype: "Customer",
            });
          }}
        >
          Cancel
        </button>

        <button
          onClick={async () => {
            if (!newCustomer.identifier.trim()) {
              alert("Customer Name is required");
              return;
            }

            if (!newCustomer.email.trim()) {
              alert("Email is required");
              return;
            }

            try {
              const customer = await addItem("customer", {
                ...newCustomer,
                partytype: "Customer",
              });

              setCustomers((prev) => [...prev, customer]);

              handleSelectCustomer(customer.email);

              setShowCustomerModal(false);

              setNewCustomer({
                identifier: "",
                email: "",
                phoneno: "",
                partytype: "Customer",
              });

            } catch (err) {
              console.error("Add customer error:", err);

              alert(
                err?.response?.data?.message ||
                "Failed to add customer"
              );
            }
          }}
        >
          Save Customer
        </button>

      </div>

    </div>
  </div>
)}
          {showDropdown && (
            <div className="dropdown">

              <input
                type="text"
                placeholder="Search customer..."
                value={customerSearch}
                onChange={(e) => setCustomerSearch(e.target.value)}
              />

              <div className="dropdownList">
                {customers
                  .filter((c) =>
                    (c.email || "")
                      .toLowerCase()
                      .includes(customerSearch.toLowerCase())
                  )
                  .map((c) => (
                    <button
                      key={c.id || c.email}
                      className="dropdownItem"
                      onClick={() => handleSelectCustomer(c.email)}
                    >
                      {c.email}
                    </button>
                  ))}
              </div>

              {customerSearch &&
                !customers.some((c) => c.email === customerSearch) && (
                  <button
  className="addCustomerBtn"
  onClick={() => {
    setShowDropdown(false); // Close customer dropdown

    setNewCustomer({
      identifier: "",
      email: customerSearch,
      phoneno: "",
      partytype: "Customer",
    });

    setShowCustomerModal(true); // Open modal
  }}
>
  + Add "{customerSearch}"
</button>
                )}
            </div>
          )}
        </div>

        {/* PRODUCT SEARCH */}
        <div className="productSearchBox">
          <input
            type="text"
            placeholder="Search products..."
            value={productSearch}
            onChange={(e) => handleProductSearch(e.target.value)}
          />
        </div>

        <div className="sectionHeader">
          <h2>Products</h2>
        </div>

        {/* PRODUCTS */}
        <div className="productsGrid">
          {filteredProducts.map((product) => (
            <div key={product.identifier} className="productCard">
              <h3>{product.identifier}</h3>
              <p>₹{product.sellingPrice}</p>

              <button
                className="addBtn"
                disabled={!selectedCustomer}
                onClick={() => addToCart(product)}
              >
                ADD
              </button>
            </div>
          ))}
        </div>
      </div>

      {/* CART  */}
      <div className="cartSection">

        <div className="cartHeader">
          <h2>Current Bill</h2>
          <span>{selectedCustomer}</span>
        </div>

        <div className="cartItems">

          {cartEntries.length === 0 ? (
            <div className="emptyCart">No Items Added</div>
          ) : (
            cartEntries.map((entry) => (
              <div key={entry.identifier + entry.product} className="cartItem">

                <div>
                  <strong>{entry.product}</strong>

                  <div className="qtyControls">
                    <button onClick={() => decreaseQty(entry)}>-</button>
                    <span>{entry.quantity}</span>
                    <button onClick={() => increaseQty(entry)}>+</button>
                  </div>

                  <div>Unit Price: ₹{entry.unitPrice || 0}</div>
                </div>

                <div className="rightSide">
                  <span>₹{entry.totalPrice || 0}</span>
                  <button onClick={() => removeItem(entry.identifier)}>✕</button>
                </div>

              </div>
            ))
          )}

        </div>

        {/* COUPON  */}
        <div className="couponBox">

          <input
  type="text"
  placeholder="Enter coupon code (SAVE10, FLAT50...)"
  value={couponCode}
  onChange={(e) => setCouponCode(e.target.value)}
  disabled={!!appliedCoupon}
/>

          <button onClick={applyCoupon}>
            Apply
          </button>

        </div>

        {couponMessage && (
          <div className="couponMessage">
            {couponMessage}
          </div>
        )}

        {appliedCoupon && (
  <div className="appliedCoupon">
    <div>
      Applied Coupon: <b>{appliedCoupon}</b>
    </div>

    <div>
      Discount: <b>-₹{(cart.discount || 0).toFixed(2)}</b>
    </div>

    <button
      onClick={removeCoupon}
      className="removeCouponBtn"
    >
      Remove Coupon
    </button>
  </div>
)}

        {/* SUMMARY  */}
        <div className="summary">
          <div className="summaryRow total">
            <span>Total</span>
            <span>
                ₹{(cart.totalPrice || 0).toFixed(2)}
            </span>
          </div>
        </div>

        {/* ACTIONS */}
        <div className="actionButtons">
          <button className="clearBtn" onClick={clearCart}>
            Clear
          </button>

        <button
  className="checkoutBtn"
  disabled={
    !selectedCustomer ||
    cartEntries.length === 0
  }
  onClick={() =>
    setShowPaymentModal(true)
  }
>
  Checkout
</button>
        </div>

      </div>
      {
  showPaymentModal && (
    <div className="modalOverlay">

      <div className="paymentModal">

        <h3>Select Payment Method</h3>

        <div className="paymentOptions">

          <label>
            <input
              type="radio"
              value="CASH"
              checked={paymentMethod === "CASH"}
              onChange={(e) =>
                setPaymentMethod(
                  e.target.value
                )
              }
            /> Cash
          </label>

          <label>
            <input
              type="radio"
              value="UPI"
              checked={paymentMethod === "UPI"}
              onChange={(e) =>
                setPaymentMethod(
                  e.target.value
                )
              }
            /> UPI
          </label>

          <label>
            <input
              type="radio"
              value="CARD"
              checked={paymentMethod === "CARD"}
              onChange={(e) =>
                setPaymentMethod(
                  e.target.value
                )
              }
            /> Card
          </label>

        </div>

        <div className="modalActions">

          <button
            onClick={() =>
              setShowPaymentModal(false)
            }
          >
            Cancel
          </button>

          <button
            onClick={async () => {

              try {

                const res =
                  await api.post(
                    "/api/order/create",
                    null,
                    {
                      params: {
                        cartId:
                          selectedCustomer,
                        paymentMethod:
                          paymentMethod,
                      },
                    }
                  );
if (res.data?.success) {

  setBillData({
    orderNo: res.data.identifier,
    customer: selectedCustomer,
    paymentMethod,
    items: [...cartEntries],
    discount: cart.discount || 0,
    total: cart.totalPrice || 0,
    date: new Date().toLocaleString(),
  });

 setShowBillModal(true);

setCart({});
setCartEntries([]);
  setAppliedCoupon(null);
  setCouponCode("");

  setShowPaymentModal(false);

  await loadCart(selectedCustomer);
}

              } catch (err) {

                console.error(err);

                alert(
                  "Payment Failed"
                );
              }
            }}
          >
            Pay Now
          </button>

        </div>

      </div>

    </div>
  )
}
{
  showBillModal && billData && (
    <div className="modalOverlay">
<div className="billModal">

  <div className="billTop">

    <h2>Order Successful</h2>

    <span className="successBadge">
      PAID
    </span>

  </div>

  <div className="billInfoGrid">

    <div>
      <span>Order No</span>
      <p>{billData.orderNo}</p>
    </div>

    <div>
      <span>Customer</span>
      <p>{billData.customer}</p>
    </div>

    <div>
      <span>Payment</span>
      <p>{billData.paymentMethod}</p>
    </div>

    <div>
      <span>Date</span>
      <p>{billData.date}</p>
    </div>

  </div>

  <div className="billItemsSection">

    <h3>Items Purchased</h3>

    {billData.items.map((item) => (
      <div
        key={item.identifier}
        className="billItemRow"
      >
        <div>
          <strong>{item.product}</strong>
          <div>
            Qty: {item.quantity}
          </div>
        </div>

        <div>
          ₹{item.totalPrice}
        </div>
      </div>
    ))}

  </div>

  <div className="billTotals">

    <div>
      <span>Discount</span>
      <span>
        ₹{billData.discount.toFixed(2)}
      </span>
    </div>

    <div className="grandTotal">

      <span>Grand Total</span>

      <span>
        ₹{billData.total.toFixed(2)}
      </span>

    </div>

  </div>

  <div className="billActions">

    <button
      className="closeBillBtn"
      onClick={() =>
        setShowBillModal(false)
      }
    >
      Done
    </button>

  </div>

</div>

    </div>
  )
}
    </div>
  );
  
}