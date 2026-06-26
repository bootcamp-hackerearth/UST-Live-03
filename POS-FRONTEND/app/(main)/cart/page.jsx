"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import api, { listItems, addItem, deleteItem } from "@/services/api";
import "./cart.css";

const CART_ID = "CART001";

export default function CartPage() {
  const [products, setProducts] = useState([]);
  const [cartEntries, setCartEntries] = useState([]);
  const [prices, setPrices] = useState([]);
  const [customers, setCustomers] = useState([]);
  const [selectedCustomer, setSelectedCustomer] = useState("");
  const [productSearch, setProductSearch] = useState("");
  const [showCustomerModal, setShowCustomerModal] = useState(false);
  const [vat, setVat] = useState(0);
  const [discount, setDiscount] = useState(0);

  const router = useRouter();

  const handleCheckout = () => {
    if (cartEntries.length === 0) {
      alert("Cart is empty");
      return;
    }

    if (!selectedCustomer) {
      alert("Please select a customer");
      return;
    }

    router.push(
      `/payment?cartId=${CART_ID}&customer=${selectedCustomer}&vat=${vat}&discount=${discount}`
    );
  };

  const [customerForm, setCustomerForm] = useState({
    identifier: "",
    phoneno: "",
    email: "",
    address: "",
    partytype: "Customer",
  });

  // ================= FETCH PRODUCTS =================
  const fetchProducts = async () => {
    const res = await listItems("product", {
      page: 0,
      sizePerPage: 100,
      sortField: "identifier",
    });

    const availableProducts =
      (res?.content || []).filter(
        (p) =>
          p.status === 1 ||
          p.status === true ||
          p.status === "1"
      );

    setProducts(availableProducts);
  };

  // ================= FETCH PRICES =================
  const fetchPrices = async () => {
    const res = await listItems("price", {
      page: 0,
      sizePerPage: 100,
      sortField: "identifier",
    });

    setPrices(res?.content || []);
  };

  // ================= FETCH CUSTOMERS =================
  const fetchCustomers = async () => {
    try {
      const res = await listItems("customer", {
        page: 0,
        sizePerPage: 100,
        sortField: "identifier",
      });

      console.log("CUSTOMER RESPONSE:", res);

      const customerData =
        res?.content ??
        res?.data ??
        (Array.isArray(res) ? res : []);

      setCustomers(customerData);
    } catch (err) {
      console.error("Customer fetch failed:", err);
      setCustomers([]);
    }
  };

  // ================= SAVE CUSTOMER (FIXED) =================
  const saveCustomer = async () => {
    if (!customerForm.identifier?.trim()) {
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

      setSelectedCustomer(payload.identifier);

      setCustomerForm({
        identifier: "",
        phoneno: "",
        email: "",
        address: "",
        partytype: "Customer",
      });

      setShowCustomerModal(false);
    } catch (err) {
      console.error(err);
    }
  };

  // ================= FETCH CART =================
  const fetchCartEntries = async () => {
    const res = await api.post("/api/cartentry/getByCartId", {
      cartId: CART_ID,
    });

    setCartEntries(res.data || []);
  };

  // ================= INITIAL LOAD =================
  useEffect(() => {
    const load = async () => {
      await Promise.all([
        fetchProducts(),
        fetchPrices(),
        fetchCustomers(),
        fetchCartEntries(),
      ]);
    };

    load();
  }, []);

  // ================= PRICE MAP =================
  const priceMap = Object.fromEntries(
    prices.map((p) => [p.identifier, p.sellingPrice])
  );

  // ================= CART ACTIONS =================
  const addToCart = async (id) => {
    await api.post("/api/cartentry/add", {
      cartId: CART_ID,
      product: id,
      quantity: 1,
    });

    fetchCartEntries();
  };

  const increaseQty = async (row) => {
    await api.post("/api/cartentry/add", {
      cartId: row.cartId,
      product: row.product,
      quantity: 1,
    });

    fetchCartEntries();
  };

  const decreaseQty = async (row) => {
    await api.put("/api/cartentry/decrease", null, {
      params: { identifier: row.identifier },
    });

    fetchCartEntries();
  };

  const removeItem = async (id) => {
    await deleteItem("cartentry", id);
    fetchCartEntries();
  };

  const clearCart = async () => {
    await Promise.all(
      cartEntries.map((item) =>
        deleteItem("cartentry", item.identifier)
      )
    );

    fetchCartEntries();
  };

  // ================= TOTAL =================
  const subTotal = cartEntries.reduce(
    (sum, item) => sum + Number(item.totalPrice || 0),
    0
  );

  const vatAmount = (subTotal * Number(vat || 0)) / 100;

  const discountAmount =
    (subTotal * Number(discount || 0)) / 100;

  const grandTotal =
    subTotal + vatAmount - discountAmount;

  // ================= FILTER PRODUCTS =================
  const filteredProducts = products.filter((p) =>
    p.identifier?.toLowerCase().includes(productSearch.toLowerCase())
  );

  return (
    <div className="cartLayout">

      {/* LEFT PANEL */}
      <div className="leftPanel">

        {/* QUICK ACTION */}
        <div className="card">
          <div className="cardHeader">Quick Action</div>

          <div className="customerRow">
            <select
              className="customerSelect"
              value={selectedCustomer}
              onChange={async (e) => {
                const customerId = e.target.value;

                setSelectedCustomer(customerId);

                await api.put("/api/cart/updateCustomer", null, {
                  params: {
                    cartId: CART_ID,
                    customerId,
                  },
                });
              }}
            >
              <option value="">Select Customer</option>

              {customers?.map((c, index) => (
                <option
                  key={c.id}
                  value={c.identifier}
                >
                  {c.identifier}
                </option>
              ))}
            </select>

            <button
              className="addCustomerBtn"
              onClick={() => setShowCustomerModal(true)}
            >
              + Customer
            </button>
          </div>

          <input type="date" className="dateInput" />
        </div>

        {/* CART TABLE */}
        <div className="card">
          <div className="cardHeader">Cart Entries</div>

          <div className="tableWrapper">
            <table className="cartTable">
              <thead>
                <tr>
                  <th>Item</th>
                  <th>Price</th>
                  <th>Qty</th>
                  <th>Total</th>
                  <th>Action</th>
                </tr>
              </thead>

              <tbody>
                {cartEntries.map((item) => (
                  <tr key={item.identifier}>
                    <td>{item.product}</td>
                    <td>₹{item.unitPrice}</td>

                    <td>
                      <div className="qtyBox">
                        <button onClick={() => decreaseQty(item)}>-</button>
                        <span>{item.quantity}</span>
                        <button onClick={() => increaseQty(item)}>+</button>
                      </div>
                    </td>

                    <td>₹{item.totalPrice}</td>

                    <td>
                      <button onClick={() => removeItem(item.identifier)}>
                        ✕
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          <div className="totalBar">
            Total Amount: ₹{grandTotal}
          </div>

          <button className="clearCartBtn" onClick={clearCart}>
            Clear Cart
          </button>
        </div>

        {/* SUMMARY */}
        <div className="card saleSummaryCard">
          <div className="cardHeader">Cart Summary</div>

          <div className="summaryRow">
            <span>Sub Total</span>
            <span className="value">₹{subTotal.toFixed(2)}</span>
          </div>

          <div className="summaryRow">
            <span>VAT (%)</span>
            <input
              type="number"
              className="summaryInput"
              value={vat}
              onChange={(e) => setVat(e.target.value)}
            />
          </div>

          <div className="summaryRow">
            <span>VAT Amount</span>
            <span className="value">₹{vatAmount.toFixed(2)}</span>
          </div>

          <div className="summaryRow">
            <span>Discount (%)</span>
            <input
              type="number"
              className="summaryInput"
              value={discount}
              onChange={(e) => setDiscount(e.target.value)}
            />
          </div>

          <div className="summaryRow">
            <span>Discount Amount</span>
            <span className="value">₹{discountAmount.toFixed(2)}</span>
          </div>

          <div className="summaryRow totalRow">
            <span>Total</span>
            <span className="value">₹{grandTotal.toFixed(2)}</span>
          </div>

          <button
            className="saleBtn"
            onClick={handleCheckout}
          >
            Checkout
          </button>
        </div>
      </div>

      {/* RIGHT PANEL */}
      <div className="rightPanel">

        <div className="card productCardContainer">
          <div className="cardHeader">Products</div>

          <div className="searchBox">
            <input
              className="search"
              placeholder="Search products..."
              value={productSearch}
              onChange={(e) => setProductSearch(e.target.value)}
            />
          </div>

          <div className="productScroll">
            <div className="productList">
              {filteredProducts.map((p) => (
                <div className="productCard" key={p.identifier}>
                  <div className="productName">{p.identifier}</div>
                  <div className="price">
                    ₹{priceMap[p.identifier] || 0}
                  </div>

                  <button
                    className="addBtn"
                    onClick={() => addToCart(p.identifier)}
                  >
                    Add to Cart
                  </button>
                </div>
              ))}
            </div>
          </div>

        </div>
      </div>

      {/* CUSTOMER MODAL */}
      {showCustomerModal && (
        <div className="modalOverlay">
          <div className="customerModal">

            <h2>Add Customer</h2>

            <input
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
              placeholder="Phone"
              value={customerForm.phoneno}
              onChange={(e) =>
                setCustomerForm({
                  ...customerForm,
                  phoneno: e.target.value,
                })
              }
            />

            <input
              placeholder="Email"
              value={customerForm.email}
              onChange={(e) =>
                setCustomerForm({
                  ...customerForm,
                  email: e.target.value,
                })
              }
            />

            <input
              placeholder="Address"
              value={customerForm.address}
              onChange={(e) =>
                setCustomerForm({
                  ...customerForm,
                  address: e.target.value,
                })
              }
            />

            <select
              value={customerForm.partytype}
              onChange={(e) =>
                setCustomerForm({
                  ...customerForm,
                  partytype: e.target.value,
                })
              }
            >
              <option value="Customer">Customer</option>
              <option value="Dealer">Dealer</option>
              <option value="Supplier">Supplier</option>
            </select>

            <div className="modalBtns">
              <button className="saveBtn" onClick={saveCustomer}>
                Save
              </button>

              <button
                className="cancelBtn"
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