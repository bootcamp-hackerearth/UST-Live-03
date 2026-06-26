"use client";

import React, { useEffect, useState, useRef } from "react";
import { useRouter } from "next/navigation";
import POSLayout from "../components/PosLayout";
import commonApi from "../services/commonApi";
import { Parser } from "expr-eval";

function CartPage() {
  const router = useRouter();

  const [customers, setCustomers] = useState([]);
  const [products, setProducts] = useState([]);
  const [cart, setCart] = useState(null);
  const [selectedCustomer, setSelectedCustomer] = useState("");
  const [customerSearch, setCustomerSearch] = useState("");
  const [showCustomerDropdown, setShowCustomerDropdown] = useState(false);
  const [productSearch, setProductSearch] = useState("");
  const [prices, setPrices] = useState([]);

  const [showCalculator, setShowCalculator] = useState(false);
  const [calcValue, setCalcValue] = useState("");
  const [showCustomerModal, setShowCustomerModal] = useState(false);

  const [showPaymentModal, setShowPaymentModal] = useState(false);
  const [paymentMethod, setPaymentMethod] = useState("CASH");
  const [receivedAmount, setReceivedAmount] = useState("");
  const [showOrderSuccessModal, setShowOrderSuccessModal] = useState(false);
  const [completedOrder, setCompletedOrder] = useState(null);
  const customerDropdownRef = useRef(null);

  const [newCustomer, setNewCustomer] = useState({
    name: "",
    phoneNumber: "",
    email: "",
  });
  const [customerErrors, setCustomerErrors] = useState({});

  const payload = {
    page: 0,
    sizePerPage: 1000,
    sortDirection: "ASC",
    sortField: "id",
    search: "",
  };

  const getProductName = (productIdentifier) => {
    const product = products.find(
      (p) => p.identifier === productIdentifier
    );

    return product ? product.name : productIdentifier;
  };

  const getUniqueByIdentifier = (list) => {
    const map = new Map();

    list.forEach((item) => {
      if (item?.identifier && !map.has(item.identifier)) {
        map.set(item.identifier, item);
      }
    });

    return Array.from(map.values());
  };

  const fetchCustomers = async () => {
    try {
      const res = await commonApi.list("customer", payload);
      const customerList = res.data.dtoList || [];
      setCustomers(getUniqueByIdentifier(customerList));
    } catch (err) {
      console.log(err);
    }
  };

  const fetchActiveProducts = async () => {
    try {
      const res = await commonApi.customGet("product/in-stock");
      setProducts(Array.isArray(res.data) ? res.data : []);
    } catch (err) {
      console.log(err);
    }
  };

  const fetchPrices = async () => {
    try {
      const res = await commonApi.list("price", payload);
      setPrices(res.data.dtoList || []);
    } catch (err) {
      console.log(err);
    }
  };

  const fetchCart = async (cartIdentifier) => {
    if (!cartIdentifier) {
      setCart(null);
      return;
    }

    try {
      const res = await commonApi.customPost("cart/getCart", {
        identifier: cartIdentifier,
      });

      setCart(res.data);
    } catch (err) {
      console.log(err);
      setCart(null);
    }
  };

  useEffect(() => {
    fetchCustomers();
    fetchActiveProducts();
    fetchPrices();
  }, []);
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (
        customerDropdownRef.current &&
        !customerDropdownRef.current.contains(event.target)
      ) {
        setShowCustomerDropdown(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  const getSellingPrice = (productIdentifier) => {
    const price = prices.find(
      (item) =>
        item.productIdentifier === productIdentifier &&
        item.priceType === "SELLING_PRICE"
    );

    return price?.priceAmount || "-";
  };

  const handleCustomerChange = (customerIdentifier) => {
    setSelectedCustomer(customerIdentifier);

    if (!customerIdentifier) {
      setCart(null);
      return;
    }

    localStorage.setItem("cartIdentifier", customerIdentifier);

    commonApi
      .add("cart", {
        identifier: customerIdentifier,
        originalPrice: 0,
        discount: 0,
        totalPrice: 0,
      })
      .then(() => {
        fetchCart(customerIdentifier);
      })
      .catch((err) => {
        console.log(err);
        fetchCart(customerIdentifier);
      });
  };

  const handleAddProduct = async (product) => {
    if (!selectedCustomer) {
      alert("Please select customer first");
      return;
    }

    try {
      const res = await commonApi.add("cartentry", {
        cartIdentifier: selectedCustomer,
        productIdentifier: product.identifier,
        quantity: 1,
      });

      if (res.data?.success === false) {
        alert(res.data.message);
        return;
      }

      fetchCart(selectedCustomer);
    } catch (err) {
      console.log(err);
      alert("Failed to add product");
    }
  };

  const handleIncrease = async (entry) => {
    try {
      const res = await commonApi.add("cartentry", {
        cartIdentifier: entry.cartIdentifier,
        productIdentifier: entry.productIdentifier,
        quantity: 1,
      });

      if (res.data?.success === false) {
        alert(res.data.message);
        return;
      }

      fetchCart(selectedCustomer);
    } catch (err) {
      console.log(err);
    }
  };

  const handleDecrease = async (entry) => {
    try {
      await commonApi.customPost("cart/reduceEntry", {
        cartIdentifier: entry.cartIdentifier,
        productIdentifier: entry.productIdentifier,
      });

      fetchCart(selectedCustomer);
    } catch (err) {
      console.log(err);
    }
  };

  const handleDeleteEntry = async (entry) => {
    try {
      await commonApi.customPost("cart/deleteEntry", {
        cartIdentifier: entry.cartIdentifier,
        productIdentifier: entry.productIdentifier,
      });

      fetchCart(selectedCustomer);
    } catch (err) {
      console.log(err);
    }
  };

  const handleClearCart = async () => {
    if (!selectedCustomer) return;

    try {
      await commonApi.customPost("cart/clearcart", {
        identifier: selectedCustomer,
      });

      fetchCart(selectedCustomer);
    } catch (err) {
      console.log(err);
    }
  };

  const handleOpenPaymentModal = () => {
    if (!selectedCustomer) {
      alert("Please select customer");
      return;
    }

    if (!cart?.entryList?.length) {
      alert("Cart is empty");
      return;
    }

    setPaymentMethod("CASH");
    setReceivedAmount("");
    setShowPaymentModal(true);
  };

  const getChangeAmount = () => {
    const received = Number(receivedAmount || 0);
    const total = Number(totalAmount || 0);
    return received - total;
  };

  const handleCheckout = async () => {
    console.log("CONFIRM PAYMENT CLICKED");

    if (!selectedCustomer) {
      alert("Please select customer");
      return;
    }

    if (!cart?.entryList?.length) {
      alert("Cart is empty");
      return;
    }

    const total = Number(totalAmount || 0);
    const received =
      paymentMethod === "CASH" ? Number(receivedAmount || 0) : total;
    const change = paymentMethod === "CASH" ? received - total : 0;

    if (paymentMethod === "CASH" && received < total) {
      alert("Received amount should be greater than or equal to total amount");
      return;
    }

    try {
      const checkoutResponse = await commonApi.customPost("order/checkout", {
        customerIdentifier: selectedCustomer,
        paymentMethod,
        receivedAmount: received,
        changeAmount: change,
      });

      console.log("CHECKOUT RESPONSE:", checkoutResponse.data);

      if (checkoutResponse.data?.success === false) {
        alert(checkoutResponse.data.message || "Checkout failed");
        return;
      }

      const orderIdentifier =
        checkoutResponse.data?.identifier ||
        checkoutResponse.data?.orderIdentifier;

      if (!orderIdentifier) {
        alert("Order placed, but order identifier not found");
        return;
      }

      const orderResponse = await commonApi.get(
        "order",
        "identifier",
        orderIdentifier
      );

      console.log("ORDER GET RESPONSE:", orderResponse.data);

      setCompletedOrder(orderResponse.data);
      setShowPaymentModal(false);
      setShowOrderSuccessModal(true);

      setReceivedAmount("");
      setPaymentMethod("CASH");

      fetchCart(selectedCustomer);
    } catch (err) {
      console.log("CHECKOUT ERROR:", err);
      console.log("ERROR RESPONSE:", err.response);
      console.log("ERROR DATA:", err.response?.data);
      alert("Checkout failed");
    }
  };

  const handleCalculatorClick = (value) => {
    if (value === "C") {
      setCalcValue("");
      return;
    }

    if (value === "⌫") {
      setCalcValue((prev) => prev.slice(0, -1));
      return;
    }

    if (value === "=") {
      try {
        const safeExpression = calcValue
          .replaceAll("×", "*")
          .replaceAll("÷", "/");

        const result = Parser.evaluate(safeExpression);

        setCalcValue(String(result));
      } catch {
        setCalcValue("Error");
      }

      return;
    }

    if (calcValue === "Error") {
      setCalcValue(value);
      return;
    }

    setCalcValue((prev) => prev + value);
  };

  const handleCustomerInputChange = (e) => {
    const { name, value } = e.target;

    let finalValue = value;

    if (name === "phoneNumber") {
      finalValue = value.replaceAll(/\D/g, "").slice(0, 10);
    }

    setNewCustomer((prev) => ({
      ...prev,
      [name]: finalValue,
    }));

    setCustomerErrors((prev) => ({
      ...prev,
      [name]: "",
    }));
  };
  const handleAddCustomer = async () => {
    const errors = {};

    const name = newCustomer.name.trim();
    const email = newCustomer.email.trim();
    const phoneNumber = newCustomer.phoneNumber.trim();

    const isValidEmail = (value) => {
      const emailValue = value.trim();

      if (!emailValue) return false;
      if (emailValue.includes(" ")) return false;

      const atIndex = emailValue.indexOf("@");
      const lastDotIndex = emailValue.lastIndexOf(".");

      return (
        atIndex > 0 &&
        lastDotIndex > atIndex + 1 &&
        lastDotIndex < emailValue.length - 1 &&
        emailValue.indexOf("@") === emailValue.lastIndexOf("@")
      );
    };
    const phoneRegex = /^[6-9]\d{9}$/;

    if (!name) {
      errors.name = "Customer name is required";
    }

    if (!phoneNumber) {
      errors.phoneNumber = "Phone number is required";
    } else if (!phoneRegex.test(phoneNumber)) {
      errors.phoneNumber =
        "Phone number must be 10 digits and start with 6, 7, 8 or 9";
    }

    if (!email) {
      errors.email = "Email is required";
    } else if (!isValidEmail(email)) {
      errors.email = "Enter a valid email address";
    }

    const emailExists = customers.some(
      (customer) => customer.identifier?.toLowerCase() === email.toLowerCase()
    );

    if (emailExists) {
      errors.email = "Email already exists";
    }

    const phoneExists = customers.some(
      (customer) => String(customer.phoneNumber) === phoneNumber
    );

    if (phoneExists) {
      errors.phoneNumber = "Phone number already exists";
    }

    if (Object.keys(errors).length > 0) {
      setCustomerErrors(errors);
      return;
    }

    try {
      const customerPayload = {
        identifier: email,
        customerName: name,
        phoneNumber,
        partyType: "Customer",
        balance: 0,
        creditLimit: 0,
      };

      const res = await commonApi.add("customer", customerPayload);

      if (res.data?.success === false) {
        alert(res.data.message);
        return;
      }

      setShowCustomerModal(false);
      setCustomerErrors({});
      setNewCustomer({
        name: "",
        phoneNumber: "",
        email: "",
      });

      await fetchCustomers();

      setSelectedCustomer(customerPayload.identifier);
      localStorage.setItem("cartIdentifier", customerPayload.identifier);

      await commonApi.add("cart", {
        identifier: customerPayload.identifier,
        originalPrice: 0,
        discount: 0,
        totalPrice: 0,
      });

      fetchCart(customerPayload.identifier);

      alert("Customer added successfully");
    } catch (err) {
      console.log(err);
      alert(err.response?.data?.message || "Failed to add customer");
    }
  };

  const filteredProducts = products.filter((product) => {
    const searchText = [
      product?.identifier,
      product?.name,
      product?.brand,
      product?.model,
    ]
      .filter(Boolean)
      .join(" ")
      .toLowerCase();

    return searchText.includes(productSearch.toLowerCase());
  });

  const subTotal = cart?.originalPrice || 0;
  const discount = cart?.discount || 0;
  const totalAmount = cart?.totalPrice || 0;
  const itemCount = cart?.entryList?.length || 0;
  const changeAmount = getChangeAmount();

  const selectedCustomerName =
    customers.find((customer) => customer.identifier === selectedCustomer)
      ?.customerName ||
    selectedCustomer ||
    "No customer selected";
  const formatCurrency = (value) => {
    return `₹${Number(value || 0).toFixed(2)}`;
  };
  const filteredCustomers = customers.filter((customer) => {
    const search = customerSearch.toLowerCase();

    return (
      customer.customerName?.toLowerCase().includes(search) ||
      customer.identifier?.toLowerCase().includes(search) ||
      String(customer.phoneNumber || "").includes(search)
    );
  });

  return (
    <POSLayout>
      <div className="mb-4 bg-white rounded-xl border border-gray-100 shadow-sm p-4">
        <div className="flex flex-wrap items-center gap-3">
          <span className="font-bold text-gray-900 mr-2">Quick Action</span>

          <button
            onClick={() => router.push("/product")}
            className="border border-gray-300 bg-white hover:bg-gray-100 px-4 py-2 rounded-lg text-sm font-semibold text-gray-800"
          >
            🛒 Product List
          </button>

          <button
            onClick={() => setShowCustomerModal(true)}
            className="border border-gray-300 bg-white hover:bg-gray-100 px-4 py-2 rounded-lg text-sm font-semibold text-gray-800"
          >
            ➕ Add Customer
          </button>

          <button
            onClick={() => setShowCalculator(true)}
            className="border border-gray-300 bg-white hover:bg-gray-100 px-4 py-2 rounded-lg text-sm font-semibold text-gray-800"
          >
            🧮 Calculator
          </button>
        </div>
      </div>

      <div className="flex flex-col lg:flex-row gap-4 h-[calc(100vh-190px)] overflow-hidden">
        <div className="flex-1 lg:w-1/2 bg-white rounded-2xl shadow-sm border border-gray-100 flex flex-col overflow-hidden">
          <div className="p-4 border-b bg-white">
            <div className="flex flex-wrap items-start justify-between gap-3">
              <div>
                <p className="text-xs uppercase tracking-[0.2em] text-red-500 font-semibold">
                  Billing area
                </p>
                <h2 className="text-xl font-bold text-gray-900 mt-1">
                  Current Sale
                </h2>
                <p className="text-sm text-gray-500 mt-1">
                  Customer: {selectedCustomerName}
                </p>
              </div>

              <button
                onClick={handleClearCart}
                disabled={!selectedCustomer}
                className="bg-red-600 hover:bg-red-700 disabled:opacity-50 text-white px-4 py-2 rounded-lg text-sm font-semibold shadow-sm"
              >
                Clear Cart
              </button>
            </div>

            <div className="mt-3 rounded-xl border border-gray-200 bg-gray-50 p-3">
              <label
                htmlFor="customer-search"
                type="text"
                className="block text-xs font-semibold uppercase tracking-[0.18em] text-gray-500 mb-1"
              >
                Select customer
              </label>

              <div
                className="relative"
                ref={customerDropdownRef}
              >
                <input
                  type="text"
                  placeholder="Search customer by name, email or phone..."
                  value={customerSearch}
                  onChange={(e) => {
                    setCustomerSearch(e.target.value);
                    setShowCustomerDropdown(true);
                  }}
                  onFocus={() => setShowCustomerDropdown(true)}
                  className="w-full border border-gray-300 rounded-lg px-4 py-3 text-gray-800 focus:outline-none focus:ring-2 focus:ring-red-500"
                />

                {showCustomerDropdown && (
                  <div className="absolute left-0 right-0 mt-1 bg-white border border-gray-300 rounded-lg shadow-lg max-h-64 overflow-y-auto z-50">
                    {filteredCustomers.length > 0 ? (
                      filteredCustomers.map((customer) => (
                        <button
                          key={customer.identifier}
                          onClick={() => {
                            handleCustomerChange(customer.identifier);
                            setCustomerSearch(
                              customer.customerName || customer.identifier
                            );
                            setShowCustomerDropdown(false);
                          }}
                          onKeyDown={(e) => {
                            if (e.key === 'Enter' || e.key === ' ') {
                              e.preventDefault();
                              handleCustomerChange(customer.identifier);
                              setCustomerSearch(
                                customer.customerName || customer.identifier
                              );
                              setShowCustomerDropdown(false);
                            }
                          }}
                          className="w-full text-left px-4 py-3 hover:bg-red-50 cursor-pointer border-b last:border-b-0 transition-colors bg-white focus:outline-none focus:ring-2 focus:ring-red-500"
                        >
                          <div className="font-semibold text-gray-900">
                            {customer.customerName}
                          </div>

                          <div className="text-xs text-gray-500">
                            {customer.identifier}
                          </div>

                          <div className="text-xs text-gray-400">
                            {customer.phoneNumber}
                          </div>
                        </button>
                      ))
                    ) : (
                      <div className="px-4 py-4 text-center text-gray-500">
                        No customer found
                      </div>
                    )}
                  </div>
                )}
              </div>
            </div>
          </div>

          <div className="flex-1 overflow-auto">
            <table className="min-w-full border-collapse">
              <thead className="bg-gray-100 sticky top-0 z-10">
                <tr>
                  <th className="px-4 py-3 text-left text-sm font-bold text-gray-800">
                    Item
                  </th>
                  <th className="px-4 py-3 text-center text-sm font-bold text-gray-800">
                    MRP
                  </th>
                  <th className="px-4 py-3 text-center text-sm font-bold text-gray-800">
                    Discount
                  </th>
                  <th className="px-4 py-3 text-center text-sm font-bold text-gray-800">
                    Unit Price
                  </th>
                  <th className="px-4 py-3 text-center text-sm font-bold text-gray-800">
                    Qty
                  </th>
                  <th className="px-4 py-3 text-center text-sm font-bold text-gray-800">
                    Total
                  </th>
                  <th className="px-4 py-3 text-center text-sm font-bold text-gray-800">
                    Action
                  </th>
                </tr>
              </thead>

              <tbody>
                {cart?.entryList?.length > 0 ? (
                  cart.entryList.map((entry, index) => (
                    <tr
                      key={`${entry.identifier || entry.productIdentifier
                        }-${index}`}
                      className="border-t hover:bg-gray-50"
                    >
                      <td className="px-4 py-3 text-sm font-semibold text-gray-800">
                        {getProductName(entry.productIdentifier)}
                      </td>
                      <td className="px-4 py-3 text-center text-sm text-gray-800">
                        ₹{entry.mrp}
                      </td>
                      <td className="px-4 py-3 text-center text-sm text-gray-800">
                        ₹{entry.unitDiscount}
                      </td>
                      <td className="px-4 py-3 text-center text-sm text-gray-800">
                        ₹{entry.unitPrice}
                      </td>

                      <td className="px-4 py-3 text-center">
                        <div className="flex items-center justify-center gap-3">
                          <button
                            onClick={() => handleDecrease(entry)}
                            className="w-8 h-8 border border-gray-300 rounded-md bg-white text-gray-700 font-bold hover:bg-gray-100"
                          >
                            −
                          </button>

                          <span className="w-10 text-center text-base font-bold text-gray-900">
                            {entry.quantity}
                          </span>

                          <button
                            onClick={() => handleIncrease(entry)}
                            className="w-8 h-8 border border-gray-300 rounded-md bg-white text-gray-700 font-bold hover:bg-gray-100"
                          >
                            +
                          </button>
                        </div>
                      </td>

                      <td className="px-4 py-3 text-center text-sm font-bold text-gray-900">
                        ₹{entry.totalPrice}
                      </td>

                      <td className="px-4 py-3 text-center">
                        <button
                          onClick={() => handleDeleteEntry(entry)}
                          className="border border-gray-300 bg-white text-gray-700 px-3 py-1.5 rounded-lg text-sm hover:bg-gray-100"
                        >
                          Remove
                        </button>
                      </td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td colSpan={7} className="text-center py-16">
                      <div className="text-gray-400 text-lg font-semibold">
                        No products added
                      </div>
                      <p className="text-gray-400 text-sm mt-2">
                        Select a customer and add products from the right
                      </p>
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>

          <div className="border-t bg-gray-50 p-4 space-y-2">
            <div className="flex justify-between text-gray-700">
              <span>Sub Total</span>
              <span className="font-semibold">₹{subTotal}</span>
            </div>

            <div className="flex justify-between text-gray-700">
              <span>Discount</span>
              <span className="font-semibold">₹{discount}</span>
            </div>

            <div className="flex justify-between text-xl font-bold text-gray-900 border-t pt-2">
              <span>Total</span>
              <span>₹{totalAmount}</span>
            </div>

            <button
              onClick={handleOpenPaymentModal}
              disabled={!cart?.entryList?.length}
              className="w-full mt-3 bg-red-600 hover:bg-red-700 disabled:opacity-50 text-white py-3 rounded-lg font-bold"
            >
              Sale
            </button>
          </div>
        </div>

        <div className="flex-1 lg:w-1/2 bg-white rounded-2xl shadow-sm border border-gray-100 flex flex-col overflow-hidden">
          <div className="p-4 border-b bg-white">
            <div className="flex items-start justify-between gap-3">
              <div>
                <p className="text-xs uppercase tracking-[0.2em] text-red-500 font-semibold">
                  Product area
                </p>
                <h2 className="text-xl font-bold text-gray-900 mt-1">
                  Product List
                </h2>
              </div>

              <div className="rounded-xl bg-red-50 px-3 py-2 text-right">
                <p className="text-[11px] uppercase tracking-[0.18em] text-red-500">
                  Items
                </p>
                <p className="text-lg font-bold text-gray-900">{itemCount}</p>
              </div>
            </div>

            <input
              type="text"
              placeholder="Search product by name, code, brand..."
              value={productSearch}
              onChange={(e) => setProductSearch(e.target.value)}
              className="mt-3 w-full border border-gray-300 rounded-lg px-4 py-3 text-gray-800 focus:outline-none focus:ring-2 focus:ring-red-500"
            />
          </div>

          <div className="flex-1 overflow-y-auto p-4">
            <div className="grid grid-cols-2 sm:grid-cols-3 gap-4">
              {filteredProducts.map((product, index) => (
                <div
                  key={`${product.identifier}-${index}`}
                  className="rounded-xl border border-gray-200 bg-white overflow-hidden"
                >
                  <div className="h-24 bg-gray-100 flex items-center justify-center border-b">
                    <div className="w-12 h-12 rounded-xl border-2 border-dashed border-gray-300 flex items-center justify-center text-gray-400 text-xs">
                      IMG
                    </div>
                  </div>

                  <div className="p-3">
                    <h3 className="font-bold text-gray-900 text-sm line-clamp-2">
                      {product.name || product.identifier}
                    </h3>

                    <p className="text-xs text-gray-500 mt-1">
                      Price: ₹{getSellingPrice(product.identifier)}
                    </p>

                    <button
                      onClick={() => handleAddProduct(product)}
                      className="mt-2 w-full border border-gray-300 bg-white text-gray-800 text-center py-2 rounded-lg font-bold text-sm hover:bg-gray-100"
                    >
                      Add
                    </button>
                  </div>
                </div>
              ))}

              {filteredProducts.length === 0 && (
                <div className="col-span-full text-center py-16 text-gray-400">
                  No products found
                </div>
              )}
            </div>
          </div>
        </div>
      </div>

      {showPaymentModal && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
          <div className="bg-white rounded-2xl shadow-2xl w-full max-w-md p-5">
            <div className="flex items-center justify-between">
              <h2 className="text-xl font-bold text-gray-900">Payment</h2>

              <button
                onClick={() => setShowPaymentModal(false)}
                className="w-8 h-8 border border-gray-300 rounded-lg hover:bg-gray-100 text-gray-700 font-bold"
              >
                ×
              </button>
            </div>

            <div className="mt-4 rounded-xl bg-gray-50 border border-gray-200 p-4">
              <p className="text-sm text-gray-500">Total Amount</p>
              <p className="text-3xl font-bold text-gray-900">
                ₹{Number(totalAmount || 0).toFixed(2)}
              </p>
            </div>

            <div className="mt-5 grid grid-cols-2 gap-3">
              <button
                onClick={() => setPaymentMethod("CASH")}
                className={`rounded-xl border px-4 py-3 font-semibold ${paymentMethod === "CASH"
                  ? "border-red-500 bg-red-50 text-red-600"
                  : "border-gray-300 text-gray-700 hover:bg-gray-100"
                  }`}
              >
                Cash
              </button>

              <button
                onClick={() => setPaymentMethod("UPI")}
                className={`rounded-xl border px-4 py-3 font-semibold ${paymentMethod === "UPI"
                  ? "border-red-500 bg-red-50 text-red-600"
                  : "border-gray-300 text-gray-700 hover:bg-gray-100"
                  }`}
              >
                UPI
              </button>
            </div>

            {paymentMethod === "UPI" && (
              <div className="mt-5 flex flex-col items-center rounded-xl border border-dashed border-gray-300 p-5">
                <div className="grid h-40 w-40 grid-cols-5 grid-rows-5 gap-1 bg-white p-3 border border-gray-300">
                  {Array.from({ length: 25 }).map((_, index) => {
                    const row = Math.floor(index / 5)
                    const col = index % 5

                    return (
                      <div
                        key={`qr-${row}-${col}`}
                        className={`${index % 2 === 0 || index % 7 === 0
                          ? "bg-gray-900"
                          : "bg-gray-200"
                          }`}
                      />
                    )
                  })}
                </div>

                <p className="mt-3 text-sm text-gray-500 text-center">
                  Scan this dummy QR to complete UPI payment
                </p>
              </div>
            )}

            {paymentMethod === "CASH" && (
              <div className="mt-5">
                <label htmlFor="received-amount" className="text-sm font-semibold text-gray-700">
                  Received Amount
                </label>

                <input
                  id="received-amount"
                  type="number"
                  value={receivedAmount}
                  onChange={(e) => setReceivedAmount(e.target.value)}
                  className="mt-2 w-full rounded-xl border border-gray-300 px-4 py-3 text-gray-900 outline-none focus:ring-2 focus:ring-red-500"
                  placeholder="Enter received amount"
                />

                <div className="mt-4 rounded-xl bg-gray-50 border border-gray-200 p-4">
                  <p className="text-sm text-gray-500">Change to Return</p>
                  <p className="text-2xl font-bold text-gray-900">
                    ₹{Math.max(changeAmount, 0).toFixed(2)}
                  </p>
                </div>
              </div>
            )}

            <button
              onClick={handleCheckout}
              className="mt-6 w-full bg-red-600 hover:bg-red-700 text-white py-3 rounded-lg font-bold"
            >
              Confirm Payment
            </button>
          </div>
        </div>
      )}

      {showCustomerModal && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
          <div className="bg-white rounded-2xl shadow-2xl w-full max-w-md p-5">
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-xl font-bold text-gray-900">Add Customer</h2>

              <button
                onClick={() => setShowCustomerModal(false)}
                className="w-8 h-8 border border-gray-300 rounded-lg hover:bg-gray-100 text-gray-700 font-bold"
              >
                ×
              </button>
            </div>

            <div className="space-y-4">
              <div>
                <label htmlFor="customer-name" className="text-sm font-semibold text-gray-700">
                  Customer Name
                </label>
                <input
                  id="customer-name"
                  type="text"
                  name="name"
                  value={newCustomer.name}
                  onChange={handleCustomerInputChange}
                  placeholder="Enter customer name"
                  className="mt-1 w-full border border-gray-300 rounded-lg px-4 py-3 text-gray-800"
                />
              </div>

              <div>
                <label htmlFor="customer-phone" className="text-sm font-semibold text-gray-700">
                  Phone Number
                </label>
                <input
                  id="customer-phone"
                  type="text"
                  name="phoneNumber"
                  maxLength={10}
                  value={newCustomer.phoneNumber}
                  onChange={handleCustomerInputChange}
                  placeholder="Enter phone number"
                  className="mt-1 w-full border border-gray-300 rounded-lg px-4 py-3 text-gray-800"
                />
                {customerErrors.phoneNumber && (
                  <p className="text-xs text-red-500 mt-1">
                    {customerErrors.phoneNumber}
                  </p>
                )}
              </div>

              <div>
                <label htmlFor="customer-email" className="text-sm font-semibold text-gray-700">
                  Email
                </label>
                <input
                  id="customer-email"
                  type="email"
                  name="email"
                  value={newCustomer.email}
                  onChange={handleCustomerInputChange}
                  placeholder="Enter email"
                  className="mt-1 w-full border border-gray-300 rounded-lg px-4 py-3 text-gray-800"
                />
                {customerErrors.email && (
                  <p className="text-xs text-red-500 mt-1">{customerErrors.email}</p>
                )}
              </div>

              <button
                onClick={handleAddCustomer}
                className="w-full bg-red-600 hover:bg-red-700 text-white py-3 rounded-lg font-bold"
              >
                Save Customer
              </button>
            </div>
          </div>
        </div>
      )}
      {showOrderSuccessModal && completedOrder && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-[9999] px-4">
          <div className="bg-white rounded-2xl shadow-2xl w-full max-w-2xl p-6 max-h-[90vh] overflow-y-auto">

            <div className="flex justify-between items-start border-b pb-4">
              <div>
                <p className="text-xs font-bold tracking-[0.3em] text-red-600">
                  INVOICE
                </p>
                <h2 className="text-2xl font-extrabold text-gray-950 mt-2">
                  {completedOrder.identifier || completedOrder.orderIdentifier || "ORDER"}
                </h2>
                <p className="text-sm text-green-600 font-bold mt-1">
                  ✓ Order placed successfully
                </p>
              </div>

              <button
                onClick={() => {
                  setShowOrderSuccessModal(false);
                  setCompletedOrder(null);
                }}
                className="w-9 h-9 border border-gray-300 rounded-lg text-gray-900 font-bold hover:bg-gray-100"
              >
                ×
              </button>
            </div>

            <div className="grid grid-cols-2 gap-3 mt-5">
              <div className="rounded-xl border border-gray-300 bg-white p-4">
                <p className="text-xs font-bold text-gray-600 uppercase">Customer</p>
                <p className="mt-2 font-extrabold text-gray-950 break-all">
                  {completedOrder.customerIdentifier || selectedCustomer}
                </p>
              </div>

              <div className="rounded-xl border border-gray-300 bg-white p-4">
                <p className="text-xs font-bold text-gray-600 uppercase">Payment</p>
                <p className="mt-2 font-extrabold text-gray-950">
                  {completedOrder.paymentMethod}
                </p>
              </div>
            </div>

            <div className="mt-5 border border-gray-300 rounded-xl overflow-hidden">
              <table className="min-w-full bg-white">
                <thead className="bg-gray-200">
                  <tr>
                    <th className="px-3 py-3 text-left text-xs font-extrabold text-gray-900">Product</th>
                    <th className="px-3 py-3 text-center text-xs font-extrabold text-gray-900">MRP</th>
                    <th className="px-3 py-3 text-center text-xs font-extrabold text-gray-900">Discount</th>
                    <th className="px-3 py-3 text-center text-xs font-extrabold text-gray-900">Unit Price</th>
                    <th className="px-3 py-3 text-center text-xs font-extrabold text-gray-900">Qty</th>
                    <th className="px-3 py-3 text-right text-xs font-extrabold text-gray-900">Total</th>
                  </tr>
                </thead>

                <tbody>
                  {(completedOrder.entryList || []).map((entry) => (
                    <tr key={entry.identifier || entry.productIdentifier} className="border-t border-gray-300 bg-white">
                      <td className="px-3 py-3 font-extrabold text-gray-950">
                        {getProductName(entry.productIdentifier)}
                      </td>
                      <td className="px-3 py-3 text-center font-bold text-gray-900">
                        {formatCurrency(entry.mrp)}
                      </td>
                      <td className="px-3 py-3 text-center font-bold text-gray-900">
                        {formatCurrency(entry.unitDiscount)}
                      </td>
                      <td className="px-3 py-3 text-center font-bold text-gray-900">
                        {formatCurrency(entry.unitPrice)}
                      </td>
                      <td className="px-3 py-3 text-center font-extrabold text-gray-950">
                        {entry.quantity}
                      </td>
                      <td className="px-3 py-3 text-right font-extrabold text-gray-950">
                        {formatCurrency(entry.totalPrice)}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            <div className="mt-5 bg-white border border-gray-300 rounded-xl p-5 space-y-3">
              <div className="flex justify-between font-bold text-gray-900">
                <span>Original Price</span>
                <span>{formatCurrency(completedOrder.originalPrice)}</span>
              </div>

              <div className="flex justify-between font-bold text-gray-900">
                <span>Discount</span>
                <span>{formatCurrency(completedOrder.discount)}</span>
              </div>

              <div className="border-t border-gray-300 pt-3 flex justify-between text-2xl font-extrabold text-red-600">
                <span>Total</span>
                <span>{formatCurrency(completedOrder.totalPrice)}</span>
              </div>
              <div className="flex justify-between font-bold text-gray-900">
                <span>Received</span>
                <span>{formatCurrency(completedOrder.receivedAmount)}</span>
              </div>
              <div className="flex justify-between font-bold text-gray-900">
                <span>Change</span>
                <span>{formatCurrency(completedOrder.changeAmount)}</span>
              </div>
            </div>
            <div className="mt-6 flex justify-end gap-3">
              <button
                onClick={() => {
                  setShowOrderSuccessModal(false);
                  setCompletedOrder(null);
                }}
                className="px-6 py-2.5 border border-gray-300 rounded-lg font-bold text-gray-900 hover:bg-gray-100"
              >
                Close
              </button>
              <button
                onClick={() => globalThis.print()}
                className="px-7 py-2.5 bg-red-600 text-white rounded-lg font-bold hover:bg-red-700"
              >
                Print
              </button>
            </div>
          </div>
        </div>
      )}

      {showCalculator && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
          <div className="bg-white rounded-2xl shadow-2xl w-[340px] p-5">
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-xl font-bold text-black">
                Calculator
              </h2>

              <button
                onClick={() => setShowCalculator(false)}
                className="w-9 h-9 border border-gray-300 rounded-lg hover:bg-gray-100 text-black text-xl font-bold"
              >
                ×
              </button>
            </div>

            <input
              value={calcValue}
              readOnly
              placeholder="0"
              className="w-full border border-gray-300 rounded-lg px-4 py-4 mb-4 text-right text-2xl font-bold text-black bg-white"
            />

            <div className="grid grid-cols-4 gap-3">
              {[
                "C",
                "⌫",
                "÷",
                "×",
                "7",
                "8",
                "9",
                "-",
                "4",
                "5",
                "6",
                "+",
                "1",
                "2",
                "3",
                "=",
                "0",
                ".",
              ].map((value) => (
                <button
                  key={value}
                  onClick={() => handleCalculatorClick(value)}
                  className={`border border-gray-300 rounded-lg py-4 text-lg font-bold text-black hover:bg-gray-100 active:scale-95 transition ${value === "0" ? "col-span-2" : ""
                    } ${value === "="
                      ? "bg-red-600 text-white border-red-600 hover:bg-red-700"
                      : ""
                    }`}
                >
                  {value}
                </button>
              ))}
            </div>
          </div>
        </div>
      )}
    </POSLayout>
  );
}
export default CartPage;