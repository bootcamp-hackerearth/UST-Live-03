"use client";

import { useState, useEffect } from "react";
import { FiShoppingCart, FiPlus } from "react-icons/fi";
import { useRouter } from "next/navigation";
import api from "../api/axios";
import CommonList from "@/component/CommonList";

const CartList = () => {
  const [customer, setCustomer] = useState("");
  const [customers, setCustomers] = useState([]);
  const [showCustomerDropdown, setShowCustomerDropdown] = useState(false);
  const [selectedCustomerName, setSelectedCustomerName] = useState("");
  const [product, setProduct] = useState("");
  const [searchTerm, setSearchTerm] = useState("");
  const [quantity, setQuantity] = useState(1);
  const [showPaymentModal, setShowPaymentModal] = useState(false);
  const [showOrderModal, setShowOrderModal] = useState(false);
  const [orderedItems, setOrderedItems] = useState([]);
  const [createdOrder, setCreatedOrder] = useState(null);
  const [paymentMethod, setPaymentMethod] = useState("CASH");
  const router = useRouter();
  const [customerDetails, setCustomerDetails] = useState(null);

  const [products, setProducts] = useState([]);
  const [cartEntries, setCartEntries] = useState([]);

  const [cartTotal, setCartTotal] = useState(0);

  const [loading, setLoading] = useState(false);

  const loadCustomers = async (term) => {
    try {
      const response = await api.get("/api/customer/list");
      console.log(response.data);
      setCustomers(response.data || []);
    } catch (err) {
      console.error("Failed to load customers", err);
    }
  };

  useEffect(() => {
    loadCustomers();
  }, []);

  const searchProducts = async (term) => {
    try {
      const response = await api.get("/api/price/list");
      const filteredProducts = (response.data || []).filter((item) =>
        item.identifier?.toLowerCase().includes(term.toLowerCase()),
      );

      setProducts(filteredProducts);
    } catch (err) {
      console.error(err);
    }
  };

  const refreshCart = async () => {
    try {
      const entryResponse = await api.post("/api/cartentry/getByCartId", {
        cartId: customer,
      });

      setCartEntries(entryResponse.data || []);

      const total = (entryResponse.data || []).reduce(
        (sum, item) => sum + Number(item.totalPrice || 0),
        0,
      );

      setCartTotal(total);
    } catch (err) {
      console.error(err);
    }
  };

  const handleClearCart = async () => {
    if (!customer) {
      alert("Please select a cart");
      return;
    }

    const confirmClear = globalThis.confirm(`Remove all items from ${customer}?`);

    if (!confirmClear) return;

    try {
      await api.get(`/api/cartentry/clearCart?cartId=${customer}`);

      setCartEntries([]);
      setCartTotal(0);
      alert("All cart items removed successfully");
    } catch (err) {
      console.error(err);
      alert("Failed to clear cart");
    }
  };

  const handleAddProduct = async () => {
    if (!customer) {
      alert("Please select a customer first");
      return;
    }
    if (!product) {
      alert("Select a product");
      return;
    }

    try {
      setLoading(true);

      await api.post("/api/cartentry/add", {
        cartId: customer,
        product,
        quantity,
      });

      setProduct("");
      setQuantity(1);

      refreshCart();
    } catch (err) {
      console.error(err);

      alert("Failed to add product");
    } finally {
      setLoading(false);
    }
  };

  const updateQuantity = async (entry, change) => {
    const newQty = Number(entry.quantity) + change;
    if (newQty < 1) {
      return;
    }
    try {
      await api.post("/api/cartentry/update", {
        ...entry,
        quantity: newQty,
      });
      refreshCart();
    } catch (err) {
      console.error(err);
      alert("Failed to update quantity");
    }
  };

  const handlePlaceOrder = async () => {
    try {
      const currentItems = [...cartEntries];
      const response = await api.post("/api/order/placeOrder", {
        cartId: customer,
        paymentMethod: paymentMethod,
        paymentTime: new Date().toISOString(),
      });

      alert(`Order Created : ${response.data.identifier}`);

      setCreatedOrder(response.data);

      setOrderedItems(currentItems);

      setShowPaymentModal(false);

      setShowOrderModal(true);

      setCartEntries([]);

      setCartTotal(0);

      refreshCart();
    } catch (err) {
      console.error(err);

      alert("Failed to place order");
    }
  };

  const totalDiscount = cartEntries.reduce(
    (sum, item) =>
      sum + Number(item.discount || 0) * Number(item.quantity || 0),
    0,
  );

  useEffect(() => {
    if (customer) refreshCart();
  }, [customer]);

  const cartColumns = [
    {
      header: "Product",
      key: "product",
    },
    {
      header: "Qty",
      key: "quantity",
    },
    {
      header: "Unit Price",
      key: "unitPrice",
    },
    {
      header: "Discount",
      key: "discount",
    },
    {
      header: "Total",
      key: "totalPrice",
    },
  ];

  return (
    <div className="p-6">
      <div className="bg-white rounded-xl shadow-md p-6">
        <div className="flex items-center gap-3 mb-6 text-blue-600">
          <FiShoppingCart size={24} />

          <h2 className="text-2xl font-bold text-gray-800">Cart Billing</h2>
        </div>

        <div className="relative mb-4">
          <label
            htmlFor="customerInput"
            className="block mb-1 font-medium text-gray-600"
          >
            Customer
          </label>

          <input
            id="customerInput"
            type="text"
            placeholder="Search Customer"
            value={selectedCustomerName}
            onFocus={() => setShowCustomerDropdown(true)}
            onClick={() => {
              setShowCustomerDropdown(!showCustomerDropdown);
            }}
            readOnly
            className="w-full border rounded p-2 text-gray-400"
          />

          {showCustomerDropdown && customers.length > 0 && (
            <ul className="absolute z-50 w-full bg-white border rounded mt-1 max-h-48 overflow-auto text-gray-600">
              {customers.map((customer) => (
                <li key={customer.id}>
                  <button
                    type="button"
                    className="p-2 hover:bg-blue-600 hover:text-white cursor-pointer"
                    onClick={() => {
                      setCustomer(customer.email);
                      setSelectedCustomerName(customer.email);
                      setCustomerDetails(customer);
                      setShowCustomerDropdown(false);
                    }}
                  >
                    {customer.email}
                  </button>
                </li>
              ))}
            </ul>
          )}
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6 text-gray-600">
          <div>
            <label htmlFor="productInput" className="block mb-1 font-medium">
              Product
            </label>

            <input
              id="productInput"
              type="text"
              placeholder="Search Product"
              value={product}
              onChange={(e) => {
                setProduct(e.target.value);

                searchProducts(e.target.value);
              }}
              className="w-full border rounded p-2"
            />

            {products.length > 0 && (
              <div className="border rounded mt-1 bg-white max-h-40 overflow-auto">
                {products.map((item) => (
                  <button
                    type="button"
                    key={item.id}
                    className="w-full text-left p-2 hover:bg-blue-600 hover:text-white"
                    onClick={() => {
                      setProduct(item.identifier);
                      setProducts([]);
                    }}
                  >
                    {item.identifier}
                  </button>
                ))}
              </div>
            )}
          </div>

          <div>
            <label htmlFor="quantityInput" className="block mb-1 font-medium">
              Quantity
            </label>

            <input
              id="quantityInput"
              type="number"
              min="1"
              value={quantity}
              onChange={(e) => setQuantity(e.target.value)}
              className="w-full border rounded p-2"
            />
          </div>

          <div className="flex items-end">
            <button
              onClick={handleAddProduct}
              disabled={loading}
              className="w-full bg-blue-600 text-white rounded p-2 hover:bg-blue-700 flex justify-center items-center gap-2"
            >
              <FiPlus />
              Add Product
            </button>
          </div>
        </div>

        <CommonList
          title=""
          columns={cartColumns}
          data={cartEntries}
          searchTerm={searchTerm}
          setSearchTerm={setSearchTerm}
          onSearchChange={(value) => {
            setSearchTerm(value);
          }}
          loading={loading}
          filterFunction={(item, term) =>
            item.product?.toLowerCase().includes(term.toLowerCase())
          }
          renderCustomCell={(key, item) => {
            switch (key) {
              case "quantity":
                return (
                  <div className="flex items-center justify-center gap-3">
                    <button
                      onClick={() => updateQuantity(item, -1)}
                      className="w-8 h-8 rounded-full bg-red-100 text-red-600"
                    >
                      -
                    </button>

                    <span>{item.quantity}</span>

                    <button
                      onClick={() => updateQuantity(item, 1)}
                      className="w-8 h-8 rounded-full bg-green-100 text-green-600"
                    >
                      +
                    </button>
                  </div>
                );

              case "unitPrice":
                return `₹${item.unitPrice}`;

              case "discount":
                return `₹${Number(item.discount || 0) * Number(item.quantity || 0)}`;

              case "totalPrice":
                return `₹${item.totalPrice}`;

              default:
                return item[key];
            }
          }}
          deleteApi={(identifier) =>
            api.delete("/api/cartentry/delete", {
              params: { identifier },
            })
          }
          deleteIdentifierField="identifier"
          refreshData={refreshCart}
          hideAddButton={true}
        />

        <div className="mt-6 flex justify-end">
          <div className="border rounded-lg p-4 min-w-[280px] bg-gray-50 text-gray-700">
            <div className="flex justify-between py-1">
              <span>Sub Total</span>
              <span className="text-blue-600 font-semibold">
                ₹ {cartTotal + totalDiscount}
              </span>
            </div>

            <div className="flex justify-between py-1">
              <span>Discount</span>
              <span className="text-red-600 font-semibold">
                - ₹ {totalDiscount}
              </span>
            </div>

            <div className="border-t my-2"></div>

            <div className="flex justify-between py-1 text-xl font-bold">
              <span>Total</span>
              <span className="text-blue-600">₹ {cartTotal}</span>
            </div>
            <div className="mb-3">
              <button
                className="w-full
      bg-red-600
      text-white
      py-3
      rounded-lg
      font-semibold
      hover:bg-red-700
      transition-all
      duration-200
      shadow-md
      flex
      items-center
      justify-center
      gap-2
      disabled:bg-gray-300
      disabled:cursor-not-allowed"
                onClick={handleClearCart}
                disabled={!customer}
              >
                Clear Cart
              </button>
              <button
                className="
        w-full
        bg-green-600
        text-white
        py-3
        rounded-lg
        font-semibold
        hover:bg-green-700
        transition-all
        duration-200
        shadow-md
        mt-3
    "
                disabled={!customer || cartEntries.length === 0}
                onClick={() => setShowPaymentModal(true)}
              >
                Checkout
              </button>
            </div>
          </div>
        </div>
      </div>

      {showPaymentModal && (
        <div className="fixed inset-0 bg-black/50 flex justify-center items-center z-50 text-gray-600">
          <div className="bg-white rounded-xl shadow-xl p-6 w-[600px]">
            <h2 className="text-2xl font-bold mb-4">Payment</h2>

            <div className="mb-4 text-gray-700">
              <p>
                <strong>Email:</strong> {customer}
              </p>

              <p>
                <strong>Address:</strong> {customerDetails?.address}
              </p>
            </div>

            <div className="border rounded-lg p-4 mb-4">
              <div className="flex justify-between">
                <span>Sub Total</span>
                <span>₹ {cartTotal + totalDiscount}</span>
              </div>

              <div className="flex justify-between">
                <span>Discount</span>
                <span>₹ {totalDiscount}</span>
              </div>

              <div className="flex justify-between font-bold text-lg">
                <span>Total</span>
                <span>₹ {cartTotal}</span>
              </div>
            </div>

            <div className="mb-4">
              <label htmlFor="paymentMethod" className="block mb-2">
                Payment Method
              </label>

              <select
                id="paymentMethod"
                value={paymentMethod}
                onChange={(e) => setPaymentMethod(e.target.value)}
                className="w-full border rounded p-2"
              >
                <option value="CASH">Cash</option>
                <option value="UPI">UPI</option>
                <option value="ONLINE">Online</option>
              </select>
            </div>

            <div className="flex justify-end gap-3">
              <button
                onClick={() => setShowPaymentModal(false)}
                className="px-4 py-2 bg-gray-300 rounded"
              >
                Cancel
              </button>

              <button
                onClick={handlePlaceOrder}
                className="px-4 py-2 bg-green-600 text-white rounded"
              >
                Place Order
              </button>
            </div>
          </div>
        </div>
      )}
      {showOrderModal && createdOrder && (
        <div className="fixed inset-0 bg-black/50 flex justify-center items-center z-50 text-gray-600">
          <div className="bg-white rounded-xl shadow-xl p-6 w-[850px] max-h-[90vh] overflow-y-auto">
            <h2 className="text-2xl font-bold text-green-600 mb-6">
              Order Placed Successfully
            </h2>

            <div className="grid grid-cols-2 gap-4 mb-6 text-gray-700">
              <div>
                <strong>Order ID</strong>
                <div>{createdOrder.identifier}</div>
              </div>

              <div>
                <strong>Customer</strong>
                <div>{createdOrder.customerEmail}</div>
              </div>

              <div>
                <strong>Payment Method</strong>
                <div>{createdOrder.paymentMethod}</div>
              </div>

              <div>
                <strong>Payment Time</strong>
                <div>{createdOrder.paymentTime}</div>
              </div>

              <div className="col-span-2">
                <strong>Shipping Address</strong>
                <div>{createdOrder.shippingAddress}</div>
              </div>
            </div>

            <table className="w-full border-collapse mb-6">
              <thead>
                <tr className="bg-gray-100">
                  <th className="border p-2">Product</th>

                  <th className="border p-2">Qty</th>

                  <th className="border p-2">Unit Price</th>

                  <th className="border p-2">Discount</th>

                  <th className="border p-2">Total</th>
                </tr>
              </thead>

              <tbody>
                {orderedItems.map((item) => (
                  <tr key={item.identifier}>
                    <td className="border p-2">{item.product}</td>

                    <td className="border p-2">{item.quantity}</td>

                    <td className="border p-2">₹ {item.unitPrice}</td>

                    <td className="border p-2">
                      ₹{" "}
                      {Number(item.discount || 0) * Number(item.quantity || 0)}
                    </td>

                    <td className="border p-2">₹ {item.totalPrice}</td>
                  </tr>
                ))}
              </tbody>
            </table>

            <div className="flex justify-end">
              <div className="w-[320px] border rounded-lg p-4 bg-gray-50">
                <div className="flex justify-between">
                  <span>Sub Total</span>

                  <span>₹ {createdOrder.subtotal}</span>
                </div>

                <div className="flex justify-between text-red-600">
                  <span>Discount</span>

                  <span>₹ {createdOrder.discount}</span>
                </div>

                <div className="border-t my-2"></div>

                <div className="flex justify-between font-bold text-xl text-green-600">
                  <span>Total</span>

                  <span>₹ {createdOrder.total}</span>
                </div>
              </div>
            </div>

            <div className="flex justify-end gap-3 mt-6">
              <button
                onClick={() => {
                  setShowOrderModal(false);
                  router.push("/order/list");
                }}
                className="
      px-6
      py-2
      bg-green-600
      text-white
      rounded-lg
      hover:bg-green-700
    "
              >
                View Order History
              </button>

              <button
                onClick={() => setShowOrderModal(false)}
                className="
      px-6
      py-2
      bg-blue-600
      text-white
      rounded-lg
      hover:bg-blue-700
    "
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default CartList;
