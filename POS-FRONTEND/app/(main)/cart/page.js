"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import api, {
  deleteItem,
  listItems
} from "@/services/api";
import "./cart.css";

export default function CartPage() {
  const router = useRouter();
  const [products, setProducts] = useState([]);
  const [prices, setPrices] = useState([]);
  const [cartEntries, setCartEntries] = useState([]);
  const [loading, setLoading] = useState(true);
  const [customers, setCustomers] = useState([]);
  const [warehouses, setWarehouses] = useState([]);

  const [selectedCustomer, setSelectedCustomer] = useState("");
  const [selectedWarehouse, setSelectedWarehouse] = useState("")
  const [vat, setVat] = useState(0);
  const [discount, setDiscount] = useState(0);

  const [saleDate, setSaleDate] = useState(
    new Date().toISOString().split("T")[0]
  );

  const [searchTerm, setSearchTerm] = useState("");
  const [showCustomerModal, setShowCustomerModal] = useState(false);

  const [newCustomer, setNewCustomer] = useState({});

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


  const fetchPrices = async () => {
    try {
      const response = await listItems(
        "price",
        {
          page: 0,
          sizePerPage: 100,
          sortField: "identifier",
        }
      );

      setPrices(response?.content || []);
    } catch (err) {
      console.error("PRICE ERROR", err);
    }
  };

  const fetchCustomers = async () => {
    try {
      const response = await listItems("customer", {
        page: 0,
        sizePerPage: 100,
      });

      setCustomers(response?.content || []);
    } catch (err) {
      console.error(err);
    }
  };

  const fetchWarehouses = async () => {
    try {
      const response = await listItems("warehouse", {
        page: 0,
        sizePerPage: 100,
      });

      setWarehouses(response?.content || []);
    } catch (err) {
      console.error(err);
    }
  };

  const fetchCartEntries = async () => {
    try {
      const response = await api.post(
        "/api/cartentry/getByCartId",
        {
          cartId: selectedCustomer,
        }
      );

      setCartEntries(response.data || []);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    const loadData = async () => {
      setLoading(true);

      await Promise.all([
        fetchProducts(),
        fetchPrices(),
        fetchCartEntries(),
        fetchCustomers(),
        fetchWarehouses(),
      ]);

      setLoading(false);
    };

    loadData();
  }, []);

  useEffect(() => {
    if (selectedCustomer) {
      fetchCartEntries();
    }
  }, [selectedCustomer]);

  const addToCart = async (productId) => {
    if (!selectedCustomer) {
      alert("Please select customer first");
      return;
    }

    try {
      await api.post("/api/cartentry/add", {
        cartId: selectedCustomer,
        product: productId,
        quantity: 1,
      });

      fetchCartEntries();
    } catch (err) {
      console.error(err);
    }
  };

  const increaseQty = async (row) => {
    try {
      await api.post("/api/cartentry/add", {
        cartId: row.cartId,
        product: row.product,
        quantity: 1,
      });

      fetchCartEntries();
    } catch (err) {
      console.error(err);
    }
  };

  const decreaseQty = async (row) => {
    try {
      await api.put(
        "/api/cartentry/decrease",
        null,
        {
          params: {
            identifier: row.identifier,
          },
        }
      );

      fetchCartEntries();
    } catch (err) {
      console.error(err);
    }
  };

  const removeItem = async (identifier) => {
    try {
      await deleteItem(
        "cartentry",
        identifier
      );

      fetchCartEntries();
    } catch (err) {
      console.error(err);
    }
  };

  const clearCart = async () => {
    try {
      await Promise.all(
        cartEntries.map((item) =>
          deleteItem(
            "cartentry",
            item.identifier
          )
        )
      );

      fetchCartEntries();
    } catch (err) {
      console.error(err);
    }
  };

  const handleAddCustomer = async () => {
    try {
      await api.post("/api/customer/add", {
        identifier: newCustomer.identifier,
        phoneno: newCustomer.phoneno,
        email: newCustomer.email,
        address: newCustomer.address,
        partytype: newCustomer.partytype,
      });

      setShowCustomerModal(false);

      fetchCustomers();
    } catch (err) {
      console.error(err);
    }
  };

  const grandTotal = cartEntries.reduce(
    (sum, item) =>
      sum + Number(item.totalPrice || 0),
    0
  );

  const vatAmount =
    (grandTotal * Number(vat || 0)) / 100;

  const discountAmount =
    (grandTotal * Number(discount || 0)) / 100;

  const finalTotal =
    grandTotal + vatAmount - discountAmount;

  if (loading) {
    return <div>Loading...</div>;
  }

  const getSellingPrice = (productId) => {
    const priceObj = prices.find(
      (p) => p.identifier === productId
    );

    return priceObj?.sellingPrice || 0;
  };

  return (
    <div className="cartPage">

      <div className="quickActionSection">

        <h2 className="sectionTitle">
          Quick Action
        </h2>

        <div className="quickActionRow">

          <input
            type="date"
            value={saleDate}
            onChange={(e) =>
              setSaleDate(e.target.value)
            }
            className="quickInput"
          />

          <div className="customerSelectWrapper">

            <select
              value={selectedCustomer}
              onChange={(e) =>
                setSelectedCustomer(
                  e.target.value
                )
              }
              className="quickInput"
            >
              <option value="">
                Select Customer
              </option>

              {customers.map((customer) => (
                <option
                  key={customer.identifier}
                  value={customer.email}
                >
                  {customer.name ||
                    customer.email}
                </option>
              ))}
            </select>

            <button
              type="button"
              className="addCustomerBtn"
              onClick={() =>
                setShowCustomerModal(true)
              }
            >
              +
            </button>

          </div>

          <select
            value={selectedWarehouse}
            onChange={(e) =>
              setSelectedWarehouse(
                e.target.value
              )
            }
            className="quickInput"
          >
            <option value="">
              Select Warehouse
            </option>

            {warehouses.map(
              (warehouse) => (
                <option
                  key={
                    warehouse.identifier
                  }
                  value={
                    warehouse.identifier
                  }
                >
                  {warehouse.name ||
                    warehouse.identifier}
                </option>
              )
            )}
          </select>

        </div>

      </div>

      {/* LEFT SIDE */}

      <div className="cartSection">

        <h2 className="sectionTitle">🛒 Cart Entries</h2>
        <button
          className="clearCartBtn"
          onClick={clearCart}
        >
          Clear Cart
        </button>

        <table className="cartTable">
          <thead>
            <tr>
              <th>Items</th>
              <th>Sale Price</th>
              <th>Qty</th>
              <th>Sub Total</th>
              <th>Action</th>
            </tr>
          </thead>

          <tbody>
            {cartEntries.map((item) => (
              <tr key={item.identifier}>
                <td>{item.product}</td>

                <td>
                  ₹{item.unitPrice}
                </td>

                <td>
                  <div className="qtyBox">

                    <button
                      className="qtyBtn"
                      onClick={() =>
                        decreaseQty(item)
                      }
                    >
                      -
                    </button>

                    <span>
                      {item.quantity}
                    </span>

                    <button
                      className="qtyBtn"
                      onClick={() =>
                        increaseQty(item)
                      }
                    >
                      +
                    </button>

                  </div>
                </td>

                <td>
                  ₹{item.totalPrice}
                </td>

                <td>
                  <button
                    className="deleteBtn"
                    onClick={() =>
                      removeItem(
                        item.identifier
                      )
                    }
                  >
                    🗑️
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>


      </div>

      <div className="productSection">

        <div className="productHeader">

          <input
            type="text"
            placeholder="🔍 Search product..."
            value={searchTerm}
            onChange={(e) =>
              setSearchTerm(
                e.target.value
              )
            }
            className="productSearch"
          />

        </div>

        <div className="productGrid">

          {products
            .filter((product) =>
              product.identifier
                ?.toLowerCase()
                .includes(searchTerm.toLowerCase())
            )
            .map((product) => (
              <button
                key={product.identifier}
                type="button"
                className="productCard"
                onClick={() =>
                  addToCart(product.identifier)
                }
              >
                <div className="productImage">
                  No Image
                </div>

                <h4>{product.identifier}</h4>

                <p className="productPrice">
                  ₹{getSellingPrice(product.identifier)}
                </p>
              </button>
            ))}

        </div>

      </div>

      <div className="saleSection">

        <h2>Sale Summary</h2>

        <div className="saleRow">
          <span>Sub Total</span>
          <strong>₹{grandTotal.toFixed(2)}</strong>
        </div>

        <div className="saleRow">
          <span>VAT (%)</span>

          <input
            type="number"
            min="0"
            value={vat}
            onChange={(e) => setVat(e.target.value)}
          />
        </div>

        <div className="saleRow">
          <span>VAT Amount</span>
          <strong>₹{vatAmount.toFixed(2)}</strong>
        </div>

        <div className="saleRow">
          <span>Discount (%)</span>

          <input
            type="number"
            min="0"
            value={discount}
            onChange={(e) => setDiscount(e.target.value)}
          />
        </div>

        <div className="saleRow">
          <span>Discount Amount</span>
          <strong>₹{discountAmount.toFixed(2)}</strong>
        </div>

        <div className="saleRow totalRow">
          <span>Total Amount</span>
          <strong>₹{finalTotal.toFixed(2)}</strong>
        </div>

        <button
          className="saleBtn"
          onClick={() => {

            if (!selectedCustomer) {
              alert("Please select a customer before checkout");
              return;
            }

            if (cartEntries.length === 0) {
              alert("Cart is empty");
              return;
            }

            localStorage.setItem(
              "checkoutData",
              JSON.stringify({
                customer: selectedCustomer,
                warehouse: selectedWarehouse,
                saleDate,
                items: cartEntries.map((item) => ({
                  productName:
                    item.product?.identifier ||
                    item.productName ||
                    item.product ||
                    "Unknown Product",

                  price: Number(
                    item.unitPrice ||
                    item.price ||
                    item.sellingPrice ||
                    item.totalPrice / item.quantity ||
                    0
                  ),

                  quantity: Number(item.quantity || 0),

                  totalPrice: Number(item.totalPrice || 0),
                })),
                vat,
                discount,
                grandTotal,
                vatAmount,
                discountAmount,
                finalTotal,
              })
            );

            router.push("/payment");
          }}
        >
          Checkout
        </button>

        {showCustomerModal && (
          <div className="modalOverlay">
            <div className="modal">

              <h2>Add Customer</h2>

              <input
                type="text"
                placeholder="Identifier"
                value={newCustomer.identifier || ""}
                onChange={(e) =>
                  setNewCustomer({
                    ...newCustomer,
                    identifier: e.target.value,
                  })
                }
              />

              <input
                type="text"
                placeholder="Phone No"
                value={newCustomer.phoneno || ""}
                onChange={(e) =>
                  setNewCustomer({
                    ...newCustomer,
                    phoneno: e.target.value,
                  })
                }
              />

              <input
                type="email"
                placeholder="Email"
                value={newCustomer.email || ""}
                onChange={(e) =>
                  setNewCustomer({
                    ...newCustomer,
                    email: e.target.value,
                  })
                }
              />

              <input
                type="text"
                placeholder="Address"
                value={newCustomer.address || ""}
                onChange={(e) =>
                  setNewCustomer({
                    ...newCustomer,
                    address: e.target.value,
                  })
                }
              />

              <div className="modalActions">
                <button onClick={handleAddCustomer}>
                  Add
                </button>

                <button
                  onClick={() =>
                    setShowCustomerModal(false)
                  }
                >
                  Cancel
                </button>
              </div>

            </div>
          </div>
        )}

      </div>
    </div>
  );
}