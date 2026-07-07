"use client";

import { useState, useEffect } from "react";
import CartUI from "./cartView";
import CustomerAddModal from "./CustomerAddModal";
import PaymentModal from "./PaymentModal";
import CartHistoryModal from "./CartHistoryModal";

function Cart() {
  const [customers, setCustomers] = useState([]);
  const [products, setProducts] = useState([]);
  const [customer, setCustomer] = useState("");
  const [customerName, setCustomerName] = useState("");
  const [customerSearch, setCustomerSearch] = useState("");
  const [customerDropdownOpen, setCustomerDropdownOpen] = useState(false);
  const [cartData, setCartData] = useState(null);
  const [catalogSearch, setCatalogSearch] = useState("");
  const [activeCategory, setActiveCategory] = useState("All");
  const [saving, setSaving] = useState(false);
  const [clearing, setClearing] = useState(false);
  const [placing, setPlacing] = useState(false);
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const [showAddCustomer, setShowAddCustomer] = useState(false);
  const [showPaymentModal, setShowPaymentModal] = useState(false);
  const [showHistoryModal, setShowHistoryModal] = useState(false);

  const showSuccess = (msg) => {
    setSuccessMessage(msg);
    setTimeout(() => setSuccessMessage(""), 2000);
  };
  const showError = (msg) => {
    setErrorMessage(msg);
    setTimeout(() => setErrorMessage(""), 3000);
  };

  const loadCustomers = () => {
    const token = localStorage.getItem("token");
    fetch("/api/customer/list", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: "Bearer " + token,
      },
      body: JSON.stringify({ page: 0, sizePerPage: 500 }),
    })
      .then((res) => res.json())
      .then((data) =>
        setCustomers(Array.isArray(data) ? data : data.dtoList || []),
      )
      .catch(() => showError("Failed to load customers"));
  };

  useEffect(() => {
    const token = localStorage.getItem("token");

    const productsPromise = fetch(
      "/api/product/getAllActive",
      {
        method: "GET",
        headers: { Authorization: "Bearer " + token },
      },
    ).then((res) => res.json());

    const pricesPromise = fetch("/api/price/list", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: "Bearer " + token,
      },
      body: JSON.stringify({ page: 0, sizePerPage: 500 }),
    })
      .then((res) => res.json())
      .catch(() => []);

    Promise.all([productsPromise, pricesPromise])
      .then(([productData, priceData]) => {
        const allProducts = Array.isArray(productData) ? productData : [];
        const allPrices = Array.isArray(priceData)
          ? priceData
          : priceData?.dtoList || [];

        const priceMap = {};
        allPrices.forEach((p) => {
          if (p.identifier) priceMap[p.identifier] = p;
        });

        const merged = allProducts.map((prod) => {
          const price =
            priceMap[prod.identifier] || priceMap[prod.productCode] || null;
          return {
            ...prod,
            sellingPrice: price
              ? (price.sellingPrice ??
                price.selling_price ??
                price.selling_Price ??
                0)
              : 0,
            mrp: price ? (price.mrp ?? price.MRP ?? 0) : 0,
            price: price
              ? (price.costPrice ?? price.cost_price ?? price.Cost_Price ?? 0)
              : 0,
          };
        });

        setProducts(merged);
      })
      .catch(() => showError("Failed to load products"));

    loadCustomers();
  }, []);

  const fetchCart = async (cartIdentifier) => {
    if (!cartIdentifier) {
      setCartData(null);
      return;
    }
    const token = localStorage.getItem("token");
    setLoading(true);
    try {
      const response = await fetch("/api/cart/getCart", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: "Bearer " + token,
        },
        body: JSON.stringify({ identifier: cartIdentifier }),
      });
      if (response.status === 401) {
        showError("Session expired.");
        return;
      }
      const result = await response.json();
      setCartData(result);
    } catch {
      setCartData(null);
      showError("Failed to load cart");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCart(customer);
  }, [customer]);

  const filteredCustomers = customerSearch.trim()
    ? customers.filter((c) => {
        const query = customerSearch.trim().toLowerCase();
        return (
          (c.name || "").toLowerCase().includes(query) ||
          (c.identifier || "").toLowerCase().includes(query)
        );
      })
    : [];

  const categories = [
    "All",
    ...Array.from(
      new Set(
        products.map((p) => p.category || p.categoryName).filter(Boolean),
      ),
    ),
  ];

  const catalogProducts = products.filter((p) => {
    const matchesCategory =
      activeCategory === "All" ||
      p.category === activeCategory ||
      p.categoryName === activeCategory;
    const q = catalogSearch.trim().toLowerCase();
    const matchesSearch =
      !q ||
      (p.productName || "").toLowerCase().includes(q) ||
      (p.productCode || p.sku || p.identifier || "").toLowerCase().includes(q);
    return matchesCategory && matchesSearch;
  });

  const selectCustomer = (selected) => {
    setCustomer(selected.identifier);
    setCustomerName(selected.name || selected.identifier);
    setCustomerSearch(selected.name || selected.identifier);
    setCustomerDropdownOpen(false);
  };

  const handleCustomerSearchChange = (e) => {
    const query = e.target.value;
    setCustomerSearch(query);
    setCustomerDropdownOpen(true);
    if (!query) {
      setCustomer("");
      setCustomerName("");
      setCartData(null);
    }
  };

  const handleAddProduct = async (product) => {
    if (!product || !customer) {
      showError("Please select a customer first");
      return;
    }
    if (!product.sellingPrice || Number(product.sellingPrice) <= 0) {
      showError(
        `Price not set for "${product.productName || "this product"}". Please add a price before adding to cart.`,
      );
      return;
    }
    const token = localStorage.getItem("token");
    setSaving(true);
    try {
      const response = await fetch(
        "/api/cartEntry/addEntry",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: "Bearer " + token,
          },
          body: JSON.stringify({
            product: product.identifier,
            cart: customer,
            quantity: 1,
          }),
        },
      );
      if (response.status === 401) {
        showError("Session expired.");
        return;
      }
      if (!response.ok) throw new Error("Failed to add cart entry");
      await fetchCart(customer);
      showSuccess(`${product.productName || "Item"} added to cart`);
    } catch {
      showError("Failed to add item");
    } finally {
      setSaving(false);
    }
  };

  const handleQtyChange = async (index, delta) => {
    const entry = cartData?.cartEntryDtoList?.[index];
    if (!entry) return;
    const token = localStorage.getItem("token");
    try {
      const response = await fetch(
        "/api/cartEntry/addEntry",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: "Bearer " + token,
          },
          body: JSON.stringify({
            product: entry.product,
            cart: customer,
            quantity: delta,
          }),
        },
      );
      if (!response.ok) throw new Error("Failed to update cart entry");
      await fetchCart(customer);
    } catch {
      showError("Failed to update quantity");
    }
  };

  const handleRemove = async (index) => {
    const entry = cartData?.cartEntryDtoList?.[index];
    if (!entry) return;
    const token = localStorage.getItem("token");
    try {
      const response = await fetch(
        `/api/cartEntry/deleteEntry?identifier=${entry.identifier}`,
        { method: "GET", headers: { Authorization: "Bearer " + token } },
      );
      if (response.status === 401) {
        showError("Session expired.");
        return;
      }
      if (!response.ok) throw new Error("Failed to delete cart entry");
      await fetchCart(customer);
      showSuccess("Item removed");
    } catch {
      showError("Failed to remove item");
    }
  };

  const handleClearCart = async () => {
    if (!customer || entries.length === 0) return;
    const token = localStorage.getItem("token");
    setClearing(true);
    try {
      for (const entry of entries) {
        await fetch(
          `/api/cartEntry/deleteEntry?identifier=${entry.identifier}`,
          { method: "GET", headers: { Authorization: "Bearer " + token } },
        );
      }
      await fetchCart(customer);
      showSuccess("Cart cleared");
    } catch {
      showError("Failed to clear cart");
    } finally {
      setClearing(false);
    }
  };

  const handleConfirmPayment = async (method, onSuccess) => {
    const token = localStorage.getItem("token");
    setPlacing(true);
    try {
      const response = await fetch(
        "/api/order/placeOrder",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: "Bearer " + token,
          },
          body: JSON.stringify({ identifier: customer }),
        },
      );
      if (response.status === 401) {
        showError("Session expired.");
        return;
      }
      if (!response.ok) throw new Error("Failed to place order");
      const orderData = await response.json();
      await fetchCart(customer);
      onSuccess(orderData.identifier || customer);
    } catch {
      showError("Failed to place order");
    } finally {
      setPlacing(false);
    }
  };

  const entries = cartData?.cartEntryDtoList ?? [];
  const totalDiscount = entries.reduce(
    (sum, e) => sum + Number(e.discount || 0),
    0,
  );
  const totalPayable = entries.reduce(
    (sum, e) => sum + Number(e.totalPrice || 0),
    0,
  );

  return (
    <>
      {showAddCustomer && (
        <CustomerAddModal
          onClose={() => setShowAddCustomer(false)}
          onSuccess={(newCustomer) => {
            setShowAddCustomer(false);
            loadCustomers();
            if (newCustomer) selectCustomer(newCustomer);
            showSuccess("Customer added successfully!");
          }}
        />
      )}

      {showPaymentModal && (
        <PaymentModal
          entries={entries}
          products={products}
          totalDiscount={totalDiscount}
          totalPayable={totalPayable}
          customerName={customerName}
          placing={placing}
          onConfirm={handleConfirmPayment}
          onClose={() => setShowPaymentModal(false)}
        />
      )}

      {showHistoryModal && customer && (
        <CartHistoryModal
          products={products}
          onClose={() => setShowHistoryModal(false)}
        />
      )}

      <CartUI
        customers={customers}
        products={products}
        entries={entries}
        cartData={cartData}
        customer={customer}
        customerName={customerName}
        customerSearch={customerSearch}
        customerDropdownOpen={customerDropdownOpen}
        filteredCustomers={filteredCustomers}
        onCustomerSearchChange={handleCustomerSearchChange}
        onSelectCustomer={selectCustomer}
        onAddCustomerClick={() => setShowAddCustomer(true)}
        catalogProducts={catalogProducts}
        catalogSearch={catalogSearch}
        onCatalogSearchChange={(e) => setCatalogSearch(e.target.value)}
        categories={categories}
        activeCategory={activeCategory}
        onCategoryChange={setActiveCategory}
        onAddProduct={handleAddProduct}
        onQtyChange={handleQtyChange}
        onRemove={handleRemove}
        onClearCart={handleClearCart}
        onPlaceOrder={() => setShowPaymentModal(true)}
        onShowHistory={() => setShowHistoryModal(true)}
        totalDiscount={totalDiscount}
        totalPayable={totalPayable}
        saving={saving}
        clearing={clearing}
        placingOrder={false}
        loading={loading}
        errorMessage={errorMessage}
        successMessage={successMessage}
        hasCustomer={!!customer}
      />
    </>
  );
}

export default Cart;
