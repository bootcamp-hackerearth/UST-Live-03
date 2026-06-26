"use client";

import { useEffect, useState } from "react";
import axios from "@/config/axiosConfig";
import dynamic from "next/dynamic";
import { UserPlus, ShoppingCart, Package, X } from "lucide-react";
import { useRouter } from "next/navigation";
import CommonModal from "@/components/CommonModal";

const Select = dynamic(() => import("react-select"), { ssr: false });

const selectStyles = {
  control: (base, state) => ({
    ...base,
    background: "#F8FAFC",
    border: `1.5px solid ${state.isFocused ? "#F59E0B" : "#E2E8F0"}`,
    borderRadius: "10px",
    boxShadow: state.isFocused ? "0 0 0 3px rgba(245,158,11,0.12)" : "none",
    minHeight: "42px",
    cursor: "pointer",
    "&:hover": { borderColor: "#F59E0B" },
  }),
  placeholder: (base) => ({ ...base, color: "#94A3B8", fontSize: "14px" }),
  singleValue: (base) => ({ ...base, fontSize: "14px", color: "#1E293B" }),
  input: (base) => ({ ...base, fontSize: "14px" }),
  option: (base, state) => {
    let backgroundColor = "#fff";

    if (state.isSelected) {
      backgroundColor = "#F59E0B";
    } else if (state.isFocused) {
      backgroundColor = "#FEF3C7";
    }

    return {
      ...base,
      fontSize: "14px",
      background: backgroundColor,
      color: state.isSelected ? "#fff" : "#1E293B",
      cursor: "pointer",
    };
  },
  menu: (base) => ({ ...base, borderRadius: "10px", boxShadow: "0 8px 30px rgba(0,0,0,0.12)", border: "1px solid #E2E8F0" }),
  indicatorSeparator: () => ({ display: "none" }),
  dropdownIndicator: (base) => ({ ...base, color: "#94A3B8" }),
};

const PerforatedDivider = () => (
  <div style={{ position: "relative", height: "20px", margin: "0 -24px", overflow: "hidden" }}>
    <svg width="100%" height="20" viewBox="0 0 400 20" preserveAspectRatio="none">
      <path
        d="M0,10 Q5,0 10,10 Q15,20 20,10 Q25,0 30,10 Q35,20 40,10 Q45,0 50,10 Q55,20 60,10 Q65,0 70,10 Q75,20 80,10 Q85,0 90,10 Q95,20 100,10 Q105,0 110,10 Q115,20 120,10 Q125,0 130,10 Q135,20 140,10 Q145,0 150,10 Q155,20 160,10 Q165,0 170,10 Q175,20 180,10 Q185,0 190,10 Q195,20 200,10 Q205,0 210,10 Q215,20 220,10 Q225,0 230,10 Q235,20 240,10 Q245,0 250,10 Q255,20 260,10 Q265,0 270,10 Q275,20 280,10 Q285,0 290,10 Q295,20 300,10 Q305,0 310,10 Q315,20 320,10 Q325,0 330,10 Q335,20 340,10 Q345,0 350,10 Q355,20 360,10 Q365,0 370,10 Q375,20 380,10 Q385,0 390,10 Q395,20 400,10"
        fill="none"
        stroke="#E2E8F0"
        strokeWidth="1.5"
      />
    </svg>
  </div>
);

export default function CartPage() {
  const router = useRouter();

  const [customers, setCustomers] = useState([]);
  const [products, setProducts] = useState([]);
  const [cart, setCart] = useState(null);
  const [entries, setEntries] = useState([]);
  const [selectedCustomerId, setSelectedCustomerId] = useState("");
  const [loading, setLoading] = useState(false);

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalLoading, setModalLoading] = useState(false);

  const [showCheckoutModal, setShowCheckoutModal] = useState(false);
  const [modalType, setModalType] = useState("");

  const [paymentType, setPaymentType] = useState("Cash");
  const [lastOrder, setLastOrder] = useState(null);
  const [lastEntries, setLastEntries] = useState([]);

  const initialFormState = {
    customerName: "",
    identifier: "",
    partyType: "",
    phoneNo: "",
    balance: "",
    creditLimit: "",
  };

  const [customerForm, setCustomerForm] = useState(initialFormState);

  useEffect(() => {
    fetchCustomers();
    fetchProducts();
  }, []);

  const fetchCustomers = async () => {
    try {
      const res = await axios.post("/customer/list", { page: 0, sizePerPage: 200 });
      setCustomers(res.data?.content || []);
    } catch {
      setCustomers([]);
    }
  };

  const fetchProducts = async () => {
    try {
      const res = await axios.post("/product/list", { page: 0, sizePerPage: 200 });
      setProducts(res.data?.content || []);
    } catch {
      setProducts([]);
    }
  };

  const loadCart = async (customerId) => {
    if (!customerId) return;
    setLoading(true);
    try {
      const cartRes = await axios.get(`/cart/get?identifier=${customerId}`);
      const cartData = cartRes.data;
      setCart(cartData);
      const entryRes = await axios.post("/cartEntry/list", { page: 0, sizePerPage: 200 });
      const all = entryRes.data?.content || [];
      setEntries(all.filter((e) => e.cartId === cartData.identifier));
    } finally {
      setLoading(false);
    }
  };

  const handleCustomerChange = async (opt) => {
    const value = opt?.value || "";
    setSelectedCustomerId(value);
    await loadCart(value);
  };

  const handleProductSelect = async (opt) => {
    if (!cart?.identifier || !opt?.value) return;
    await axios.post("/cartEntry/add", { cartId: cart.identifier, product: opt.value, quantity: 1 });
    await loadCart(selectedCustomerId);
  };

  const updateQty = async (item, qty) => {
    if (qty < 1) return;
    await axios.put("/cartEntry/updateQuantity", { cartId: item.cartId, product: item.product, quantity: qty });
    await loadCart(selectedCustomerId);
  };

  const deleteItem = async (item) => {
    await axios.delete(`/cartEntry/delete?identifier=${item.identifier}&cartId=${item.cartId}`);
    await loadCart(selectedCustomerId);
  };

  const clearCart = async () => {
    if (!cart?.identifier) return;
    await axios.delete(`/cartEntry/clearCart?cartId=${cart.identifier}`);
    await loadCart(selectedCustomerId);
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setCustomerForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleAddCustomerSubmit = async (e) => {
    e.preventDefault();
    setModalLoading(true);

    const submissionPayload = {
      ...customerForm,
      balance: customerForm.balance === "" ? 0 : Number(customerForm.balance),
      creditLimit: customerForm.creditLimit === "" ? 0 : Number(customerForm.creditLimit),
      partyType: customerForm.partyType || null,
    };

    try {
      await axios.post("/customer/add", submissionPayload);
      setCustomerForm(initialFormState);
      setIsModalOpen(false);
      await fetchCustomers();
    } catch (error) {
      console.error("Error creating customer:", error);
    } finally {
      setModalLoading(false);
    }
  };

  const customerOptions = customers.map((c) => ({
    value: c.identifier,
    label: `${c.customerName} (${c.phoneNo})`,
  }));

  const productOptions = products.map((p) => ({
    value: p.identifier,
    label: p.productName || p.name,
  }));

  const handleConfirmPayment = async () => {
    try {
      const res = await axios.post("/order/place", {
        customerId: selectedCustomerId,
        paymentType
      });

      setLastOrder(res.data);
      setLastEntries([...entries]);

      setEntries([]);
      setCart(null);

      setModalType("receipt");

    } catch (err) {
      console.error("Order error:", err);
      alert("Failed to place order");
    }
  };

  const getProductDisplay = (productId) => {
    const product = products.find(p => p.identifier === productId);
    return product ? `${productId} - ${product.name}` : productId;
  };

  const selectedCustomer = customers.find((c) => c.identifier === selectedCustomerId);

  return (
    <>
      <style>{`
        @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');

        * { box-sizing: border-box; }

        .pos-root {
          min-height: 100vh;
          background: #F1F5F9;
          font-family: 'Inter', sans-serif;
          display: flex;
          flex-direction: column;
        }

        .pos-topbar {
          background: #0F172A;
          color: #fff;
          padding: 0 28px;
          height: 58px;
          display: flex;
          align-items: center;
          justify-content: space-between;
          flex-shrink: 0;
        }
        .pos-topbar-brand {
          display: flex;
          align-items: center;
          gap: 10px;
          font-size: 15px;
          font-weight: 700;
          letter-spacing: -0.3px;
        }
        .pos-topbar-brand-dot {
          width: 8px; height: 8px;
          border-radius: 50%;
          background: #F59E0B;
        }
        .btn-add-customer {
          display: flex;
          align-items: center;
          gap: 7px;
          background: #F59E0B;
          color: #0F172A;
          border: none;
          padding: 8px 16px;
          border-radius: 8px;
          font-size: 13px;
          font-weight: 600;
          cursor: pointer;
          transition: background 0.15s;
          font-family: 'Inter', sans-serif;
        }
        .btn-add-customer:hover { background: #FBBF24; }

        .pos-body {
          flex: 1;
          display: grid;
          grid-template-columns: 1fr 340px;
          gap: 20px;
          padding: 20px 24px;
          max-width: 1400px;
          width: 100%;
          margin: 0 auto;
          align-items: start;
        }

        .card {
          background: #fff;
          border-radius: 14px;
          border: 1px solid #E2E8F0;
          box-shadow: 0 1px 4px rgba(0,0,0,0.05);
        }

        .section-eyebrow {
          font-size: 10px;
          font-weight: 700;
          letter-spacing: 1.2px;
          text-transform: uppercase;
          color: #94A3B8;
          margin-bottom: 8px;
        }

        .left-col { display: flex; flex-direction: column; gap: 16px; }

        .customer-card { padding: 18px 20px; }
        .customer-card-inner { display: flex; gap: 16px; align-items: flex-end; }
        .customer-select-wrap { flex: 1; }
        .customer-chip {
          display: inline-flex;
          align-items: center;
          gap: 8px;
          background: #FEF3C7;
          border: 1px solid #FDE68A;
          border-radius: 8px;
          padding: 7px 12px;
          font-size: 13px;
          font-weight: 600;
          color: #92400E;
          margin-top: 10px;
          white-space: nowrap;
        }
        .customer-chip-avatar {
          width: 22px; height: 22px;
          border-radius: 50%;
          background: #F59E0B;
          color: #fff;
          font-size: 10px;
          font-weight: 700;
          display: flex; align-items: center; justify-content: center;
        }

        .product-card { padding: 18px 20px; }

        .items-card { padding: 18px 20px; }
        .items-header {
          display: flex;
          align-items: center;
          justify-content: space-between;
          margin-bottom: 14px;
        }
        .items-title {
          display: flex;
          align-items: center;
          gap: 8px;
          font-size: 14px;
          font-weight: 700;
          color: #0F172A;
        }
        .items-count {
          background: #0F172A;
          color: #fff;
          font-size: 10px;
          font-weight: 700;
          width: 20px; height: 20px;
          border-radius: 50%;
          display: flex; align-items: center; justify-content: center;
        }
        .btn-clear {
          font-size: 12px;
          font-weight: 600;
          color: #EF4444;
          background: none;
          border: 1px solid #FCA5A5;
          border-radius: 6px;
          padding: 4px 10px;
          cursor: pointer;
          transition: all 0.15s;
          font-family: 'Inter', sans-serif;
        }
        .btn-clear:hover { background: #FEF2F2; }

        .items-grid {
          display: grid;
          grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
          gap: 12px;
        }

        .item-tile {
          background: #F8FAFC;
          border: 1px solid #E2E8F0;
          border-radius: 12px;
          padding: 14px;
          transition: border-color 0.15s, box-shadow 0.15s;
        }
        .item-tile:hover {
          border-color: #F59E0B;
          box-shadow: 0 2px 12px rgba(245,158,11,0.12);
        }
        .item-tile-top {
          display: flex;
          justify-content: space-between;
          align-items: flex-start;
          margin-bottom: 8px;
        }
        .item-name {
          font-size: 13px;
          font-weight: 600;
          color: #0F172A;
          line-height: 1.3;
          flex: 1;
          margin-right: 8px;
        }
        .btn-delete {
          background: none;
          border: none;
          padding: 2px;
          cursor: pointer;
          color: #CBD5E1;
          transition: color 0.15s;
          flex-shrink: 0;
        }
        .btn-delete:hover { color: #EF4444; }
        .item-price {
          font-size: 13px;
          font-weight: 700;
          color: #0F172A;
          font-variant-numeric: tabular-nums;
          margin-bottom: 10px;
        }
        .item-price-label { font-size: 10px; font-weight: 500; color: #94A3B8; margin-right: 2px; }
        .qty-control {
          display: flex;
          align-items: center;
          gap: 0;
          background: #fff;
          border: 1.5px solid #E2E8F0;
          border-radius: 8px;
          overflow: hidden;
          width: fit-content;
        }
        .qty-btn {
          background: none;
          border: none;
          width: 30px; height: 28px;
          display: flex; align-items: center; justify-content: center;
          cursor: pointer;
          font-size: 16px;
          color: #64748B;
          transition: background 0.12s, color 0.12s;
          font-family: 'Inter', sans-serif;
          font-weight: 500;
        }
        .qty-btn:hover { background: #F59E0B; color: #fff; }
        .qty-value {
          min-width: 32px;
          text-align: center;
          font-size: 13px;
          font-weight: 700;
          color: #0F172A;
          border-left: 1px solid #E2E8F0;
          border-right: 1px solid #E2E8F0;
          padding: 0 4px;
          height: 28px;
          display: flex; align-items: center; justify-content: center;
          font-variant-numeric: tabular-nums;
        }

        .empty-state {
          text-align: center;
          padding: 40px 20px;
          color: #94A3B8;
        }
        .empty-state-icon {
          width: 48px; height: 48px;
          border-radius: 12px;
          background: #F1F5F9;
          display: flex; align-items: center; justify-content: center;
          margin: 0 auto 12px;
        }
        .empty-state p { font-size: 13px; margin: 0; }
        .empty-state strong { display: block; font-size: 14px; color: #64748B; margin-bottom: 4px; }

        .loading-bar {
          height: 2px;
          background: linear-gradient(90deg, #F59E0B 0%, #FBBF24 50%, #F59E0B 100%);
          background-size: 200% 100%;
          animation: shimmer 1.2s infinite;
          border-radius: 2px;
          margin-bottom: 12px;
        }
        @keyframes shimmer {
          0% { background-position: -200% 0; }
          100% { background-position: 200% 0; }
        }

        .receipt-panel {
          background: #fff;
          border-radius: 14px;
          border: 1px solid #E2E8F0;
          box-shadow: 0 1px 4px rgba(0,0,0,0.05);
          position: sticky;
          top: 20px;
          overflow: hidden;
        }
        .receipt-header {
          background: #0F172A;
          padding: 18px 24px 16px;
          color: #fff;
        }
        .receipt-header-title {
          font-size: 11px;
          font-weight: 700;
          letter-spacing: 1.4px;
          text-transform: uppercase;
          color: #64748B;
          margin-bottom: 4px;
        }
        .receipt-header-store {
          font-size: 18px;
          font-weight: 700;
          letter-spacing: -0.4px;
        }
        .receipt-header-sub {
          font-size: 11px;
          color: #64748B;
          margin-top: 2px;
          font-variant-numeric: tabular-nums;
        }
        .receipt-body { padding: 20px 24px; }
        .receipt-row {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 9px 0;
          border-bottom: 1px dashed #E2E8F0;
          font-size: 13px;
        }
        .receipt-row:last-of-type { border-bottom: none; }
        .receipt-row-label { color: #64748B; font-weight: 500; }
        .receipt-row-value { font-weight: 600; color: #0F172A; font-variant-numeric: tabular-nums; }
        .receipt-row-discount { color: #EF4444; }

        .receipt-items-list { margin-bottom: 0; }
        .receipt-item-row {
          display: flex;
          justify-content: space-between;
          padding: 6px 0;
          font-size: 12px;
          color: #475569;
          border-bottom: 1px solid #F1F5F9;
        }
        .receipt-item-row:last-child { border-bottom: none; }
        .receipt-item-name {
          flex: 1;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
          margin-right: 8px;
        }
        .receipt-item-qty { color: #94A3B8; margin-right: 8px; }
        .receipt-item-price { font-weight: 600; color: #0F172A; font-variant-numeric: tabular-nums; }

        .receipt-total-row {
          display: flex;
          justify-content: space-between;
          align-items: baseline;
          padding: 14px 0 0;
        }
        .receipt-total-label {
          font-size: 13px;
          font-weight: 700;
          color: #0F172A;
          text-transform: uppercase;
          letter-spacing: 0.5px;
        }
        .receipt-total-value {
          font-size: 22px;
          font-weight: 700;
          color: #0F172A;
          font-variant-numeric: tabular-nums;
          letter-spacing: -0.5px;
        }

        .btn-checkout {
          width: 100%;
          margin-top: 16px;
          background: #F59E0B;
          color: #0F172A;
          border: none;
          border-radius: 10px;
          padding: 14px;
          font-size: 14px;
          font-weight: 700;
          cursor: pointer;
          display: flex;
          align-items: center;
          justify-content: center;
          gap: 8px;
          transition: background 0.15s, transform 0.1s;
          font-family: 'Inter', sans-serif;
          letter-spacing: 0.2px;
        }
        .btn-checkout:hover { background: #FBBF24; }
        .btn-checkout:active { transform: scale(0.98); }
        .btn-checkout:disabled {
          background: #E2E8F0;
          color: #94A3B8;
          cursor: not-allowed;
        }

        .receipt-footer {
          padding: 12px 24px 16px;
          text-align: center;
          font-size: 10px;
          color: #CBD5E1;
          letter-spacing: 0.5px;
        }

        .modal-overlay {
          position: fixed;
          top: 0; left: 0; right: 0; bottom: 0;
          background: rgba(0, 0, 0, 0.4);
          backdrop-filter: blur(4px);
          display: flex;
          align-items: center;
          justify-content: center;
          z-index: 1000;
          padding: 16px;
        }
        .modal-container {
          background: #fff;
          width: 100%;
          max-width: 440px;
          border-radius: 16px;
          box-shadow: 0 10px 25px -5px rgba(0,0,0,0.1), 0 8px 10px -6px rgba(0,0,0,0.1);
          border: 1px solid #E2E8F0;
          overflow: hidden;
          animation: modalFadeIn 0.2s ease-out;
        }
        @keyframes modalFadeIn {
          from { opacity: 0; transform: scale(0.96); }
          to { opacity: 1; transform: scale(1); }
        }
        .modal-header {
          background: #0F172A;
          color: #fff;
          padding: 16px 24px;
          display: flex;
          align-items: center;
          justify-content: space-between;
        }
        .modal-title {
          margin: 0;
          font-size: 15px;
          font-weight: 700;
          letter-spacing: -0.2px;
          display: flex;
          align-items: center;
          gap: 8px;
        }
        .btn-modal-close {
          background: transparent;
          border: none;
          color: #94A3B8;
          cursor: pointer;
          padding: 4px;
          display: flex;
          align-items: center;
          justify-content: center;
          transition: color 0.15s;
        }
        .btn-modal-close:hover { color: #fff; }
        .modal-form {
          padding: 24px;
          display: flex;
          flex-direction: column;
          gap: 16px;
        }
        .form-group {
          display: flex;
          flex-direction: column;
          gap: 6px;
        }
        .form-row {
          display: grid;
          grid-template-columns: 1fr 1fr;
          gap: 16px;
        }
        .form-label {
          font-size: 10px;
          font-weight: 700;
          letter-spacing: 1.2px;
          text-transform: uppercase;
          color: #94A3B8;
        }
        .form-input, .form-select {
          width: 100%;
          background: #F8FAFC;
          border: 1.5px solid #E2E8F0;
          border-radius: 8px;
          height: 40px;
          padding: 0 12px;
          font-size: 13px;
          color: #1E293B;
          outline: none;
          transition: border-color 0.15s, box-shadow 0.15s;
          font-family: 'Inter', sans-serif;
        }
        .form-input:focus, .form-select:focus {
          border-color: #F59E0B;
          box-shadow: 0 0 0 3px rgba(245,158,11,0.1);
        }
        .form-select {
          cursor: pointer;
          appearance: none;
          background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 24 24' stroke='%2394A3B8' stroke-width='2'%3E%3Cpath stroke-linecap='round' stroke-linejoin='round' d='M19 9l-7 7-7-7'/%3E%3C/svg%3E");
          background-repeat: no-repeat;
          background-position: right 12px center;
          background-size: 14px;
          padding-right: 32px;
        }
        .form-select:invalid, .form-select[value=""] {
          color: #94A3B8;
        }
        .modal-actions {
          display: flex;
          justify-content: flex-end;
          gap: 12px;
          margin-top: 8px;
          padding-top: 16px;
          border-top: 1px solid #F1F5F9;
        }
        .btn-modal-cancel {
          background: #F1F5F9;
          color: #475569;
          border: none;
          padding: 10px 16px;
          border-radius: 8px;
          font-size: 13px;
          font-weight: 600;
          cursor: pointer;
          transition: background 0.15s;
          font-family: 'Inter', sans-serif;
        }
        .btn-modal-cancel:hover { background: #E2E8F0; }
        .btn-modal-submit {
          background: #F59E0B;
          color: #0F172A;
          border: none;
          padding: 10px 20px;
          border-radius: 8px;
          font-size: 13px;
          font-weight: 700;
          cursor: pointer;
          transition: background 0.15s;
          font-family: 'Inter', sans-serif;
        }
        .btn-modal-submit:hover { background: #FBBF24; }
        .btn-modal-submit:disabled {
          background: #E2E8F0;
          color: #94A3B8;
          cursor: not-allowed;
        }
      `}</style>

      <div className="pos-root">

        <div className="pos-topbar">

          <div className="pos-topbar-brand">
            <div className="pos-topbar-brand-dot" />
            Point of Sale
          </div>

          <div className="flex gap-2 items-center">

            <button
              className="btn-add-customer"
              onClick={() => setIsModalOpen(true)}
            >
              <UserPlus size={14} />
              Add Customer
            </button>

            <button
              onClick={() => router.push(`/dashboard/orders/customers/${row.customerId}`)}
              className="btn-add-customer"
            >
              Orders
            </button>

          </div>

        </div>

        <div className="pos-body">

          <div className="left-col">

            <div className="card customer-card">
              <div className="section-eyebrow">Customer</div>
              <div className="customer-card-inner">
                <div className="customer-select-wrap">
                  <Select
                    options={customerOptions}
                    onChange={handleCustomerChange}
                    isSearchable
                    placeholder="Search by name or phone…"
                    styles={selectStyles}
                  />
                  {selectedCustomer && (
                    <div className="customer-chip">
                      <div className="customer-chip-avatar">
                        {selectedCustomer.customerName?.charAt(0).toUpperCase()}
                      </div>
                      {selectedCustomer.customerName}
                    </div>
                  )}
                </div>
              </div>
            </div>

            <div className="card product-card">
              <div className="section-eyebrow">Add Product</div>
              <Select
                options={productOptions}
                onChange={handleProductSelect}
                isSearchable
                placeholder={selectedCustomerId ? "Search & add product…" : "Select a customer first"}
                isDisabled={!selectedCustomerId}
                styles={selectStyles}
                value={null}
              />
            </div>

            <div className="card items-card">
              {loading && <div className="loading-bar" />}

              <div className="items-header">
                <div className="items-title">
                  <ShoppingCart size={15} />
                  Cart Items
                  {entries.length > 0 && (
                    <div className="items-count">{entries.length}</div>
                  )}
                </div>
                {entries.length > 0 && (
                  <button className="btn-clear" onClick={clearCart}>Clear all</button>
                )}
              </div>

              {entries.length === 0 ? (
                <div className="empty-state">
                  <div className="empty-state-icon">
                    <Package size={22} color="#CBD5E1" />
                  </div>
                  <strong>No items yet</strong>
                  <p>Select a customer and add products above</p>
                </div>
              ) : (
                <div className="items-grid">
                  {entries.map((item) => (
                    <div key={item.identifier} className="item-tile">
                      <div className="item-tile-top">
                        <div className="item-name">{getProductDisplay(item.product)}</div>
                        <button className="btn-delete" onClick={() => deleteItem(item)}>
                          <X size={14} />
                        </button>
                      </div>
                      <div className="item-price">
                        <span className="item-price-label">₹</span>
                        {item.totalPrice}
                      </div>
                      <div className="qty-control">
                        <button className="qty-btn" onClick={() => updateQty(item, item.quantity - 1)}>−</button>
                        <span className="qty-value">{item.quantity}</span>
                        <button className="qty-btn" onClick={() => updateQty(item, item.quantity + 1)}>+</button>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>

          </div>

          <div className="receipt-panel">

            <div className="receipt-header">
              <div className="receipt-header-title">Order Summary</div>
              <div className="receipt-header-store">Receipt</div>
              <div className="receipt-header-sub">
                {new Date().toLocaleString("en-IN", {
                  day: "numeric", month: "short", year: "numeric",
                  hour: "2-digit", minute: "2-digit",
                })}
              </div>
            </div>

            <PerforatedDivider />

            <div className="receipt-body">

              {entries.length > 0 && (
                <div className="receipt-items-list">
                  <div className="section-eyebrow" style={{ marginBottom: "6px" }}>Items</div>
                  {entries.map((item) => (
                    <div key={item.identifier} className="receipt-item-row">
                      <span className="receipt-item-name">{getProductDisplay(item.product)}</span>
                      {getProductDisplay(item.product)} × {item.quantity}
                      <span className="receipt-item-price">₹{item.totalPrice}</span>
                    </div>
                  ))}
                </div>
              )}

              {entries.length > 0 && (
                <div style={{ borderTop: "1px dashed #E2E8F0", marginTop: "12px", paddingTop: "12px" }} />
              )}

              <div className="receipt-row">
                <span className="receipt-row-label">Subtotal</span>
                <span className="receipt-row-value">₹{cart?.originalPrice || 0}</span>
              </div>
              <div className="receipt-row">
                <span className="receipt-row-label receipt-row-discount">Discount</span>
                <span className="receipt-row-value receipt-row-discount">− ₹{cart?.discount || 0}</span>
              </div>

              <PerforatedDivider />

              <div className="receipt-total-row">
                <span className="receipt-total-label">Total</span>
                <span className="receipt-total-value">₹{cart?.totalPrice || 0}</span>
              </div>

              <button
                onClick={() => {
                  setModalType("payment");
                  setShowCheckoutModal(true);
                }}
                className="btn-checkout"
                disabled={!selectedCustomerId || entries.length === 0}
              >
                Proceed to Checkout
              </button>

            </div>

            <div className="receipt-footer">
              {entries.length} item{entries.length === 1 ? "" : "s"} · POS Terminal
            </div>

          </div>

        </div>
      </div>

      {isModalOpen && (
        <dialog className="modal-overlay" open>
          <form className="modal-container" onSubmit={handleAddCustomerSubmit}>
            <div className="modal-header">
              <h3 className="modal-title">
                <UserPlus size={16} style={{ color: "#F59E0B" }} />
                Add New Customer
              </h3>
              <button className="btn-modal-close" onClick={() => setIsModalOpen(false)} type="button">
                <X size={16} />
              </button>
            </div>

            <div className="modal-form">
              <div className="form-group">
                <label className="form-label" htmlFor="customerName">Customer Name *</label>
                <input
                  id="customerName"
                  type="text"
                  name="customerName"
                  required
                  value={customerForm.customerName}
                  onChange={handleInputChange}
                  placeholder="Enter full name"
                  className="form-input"
                />
              </div>

              <div className="form-group">
                <label className="form-label" htmlFor="identifier">Email *</label>
                <input
                  id="identifier"
                  type="email"
                  name="identifier"
                  required
                  value={customerForm.identifier}
                  onChange={handleInputChange}
                  placeholder="Enter email"
                  className="form-input"
                />
              </div>

              <div className="form-group">
                <label className="form-label" htmlFor="partyType">Party Type</label>
                <select
                  id="partyType"
                  name="partyType"
                  value={customerForm.partyType}
                  onChange={handleInputChange}
                  className="form-select"
                  style={{ color: customerForm.partyType ? "#1E293B" : "#94A3B8" }}
                >
                  <option value="" disabled hidden>Select Type…</option>
                  <option value="INDIVIDUAL">INDIVIDUAL</option>
                  <option value="BUSINESS">BUSINESS</option>
                </select>
              </div>

              <div className="form-group">
                <label className="form-label" htmlFor="phoneNo">Phone Number *</label>
                <input
                  id="phoneNo"
                  type="tel"
                  name="phoneNo"
                  required
                  value={customerForm.phoneNo}
                  onChange={handleInputChange}
                  placeholder="Enter phone number"
                  className="form-input"
                />
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label className="form-label" htmlFor="balance">Opening Balance</label>
                  <input
                    id="balance"
                    type="number"
                    name="balance"
                    value={customerForm.balance}
                    onChange={handleInputChange}
                    placeholder="Enter amount"
                    className="form-input"
                  />
                </div>
                <div className="form-group">
                  <label className="form-label" htmlFor="creditLimit">Credit Limit</label>
                  <input
                    id="creditLimit"
                    type="number"
                    name="creditLimit"
                    value={customerForm.creditLimit}
                    onChange={handleInputChange}
                    placeholder="Enter credit limit"
                    className="form-input"
                  />
                </div>
              </div>

              <div className="modal-actions">
                <button type="button" className="btn-modal-cancel" onClick={() => setIsModalOpen(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn-modal-submit" disabled={modalLoading}>
                  {modalLoading ? "Saving..." : "Save Customer"}
                </button>
              </div>
            </div>
          </form>
        </dialog>
      )}

      <CommonModal
        isOpen={showCheckoutModal}
        onClose={() => setShowCheckoutModal(false)}
        title={modalType === "payment" ? "Checkout" : "Receipt"}
        width="max-w-lg"
      >

        {modalType === "payment" && (
          <div className="space-y-5">

            <div className="text-center">
              <p className="text-xs text-gray-500">Total Payable</p>
              <h2 className="text-2xl font-bold text-black">
                ₹{cart?.totalPrice || 0}
              </h2>
            </div>

            <div className="grid grid-cols-3 gap-3">
              {["Cash", "Card", "UPI"].map((type) => (
                <button
                  key={type}
                  onClick={() => setPaymentType(type)}
                  className={`p-3 rounded-lg border text-sm font-semibold transition ${paymentType === type
                    ? "bg-black text-white border-black"
                    : "bg-gray-50 hover:bg-gray-100"
                    }`}
                >
                  {type}
                </button>
              ))}
            </div>

            <button
              onClick={handleConfirmPayment}
              className="w-full bg-black text-white py-3 rounded-lg font-semibold hover:opacity-90"
            >
              Confirm Payment
            </button>

          </div>
        )}

        {modalType === "receipt" && (
          <div className="space-y-4 text-sm">

            <div className="text-center border-b pb-3">
              <h3 className="font-bold text-lg">Store Receipt</h3>
              <p className="text-xs text-gray-400">
                {new Date().toLocaleString()}
              </p>
            </div>

            <div className="space-y-2 max-h-48 overflow-y-auto pr-1">
              {lastEntries.map((item) => (
                <div key={item.identifier} className="flex justify-between">
                  <span>
                    {item.product} × {item.quantity}
                  </span>
                  <span className="font-medium">
                    ₹{item.totalPrice}
                  </span>
                </div>
              ))}
            </div>

            <div className="border-t pt-3 space-y-1">

              <div className="flex justify-between text-gray-500">
                <span>Subtotal</span>
                <span>₹{lastOrder?.totalOriginalPrice}</span>
              </div>

              <div className="flex justify-between text-red-500">
                <span>Discount</span>
                <span>₹{lastOrder?.discount}</span>
              </div>

              <div className="flex justify-between font-bold text-lg mt-2">
                <span>Total</span>
                <span>₹{lastOrder?.totalPrice}</span>
              </div>

            </div>

            <div className="flex gap-2 pt-2">

              <button
                onClick={() => globalThis.print()}
                className="flex-1 bg-black text-white py-2 rounded-lg font-semibold"
              >
                Print
              </button>

              <button
                onClick={() => setShowCheckoutModal(false)}
                className="flex-1 border py-2 rounded-lg"
              >
                Close
              </button>

            </div>

          </div>
        )}
      </CommonModal>
    </>
  );
}