"use client";

import { useState, useEffect, useRef } from "react";
import { useRouter, useParams } from "next/navigation";
import PropTypes from "prop-types";
import api from "@/api/axios";
import { useSidebarOpen } from "@/components/useDropdownOptions";
import AuditCard, { formatDateTime, metadataOuterSt } from "@/components/AuditCard";
import { CustomerFormFooter } from "@/app/customer/AddressPair";
import {
    C,
    inputSt, inputErrSt, labelSt, errTextSt,
    sectionSt, sectionTitleSt,
    pageSt, containerSt, cardSt,
    headerRowSt, backBtnSt, titleSt, introTextSt,
    alertBoxSt,
    PARTY_TYPE_DROPDOWN_OPTIONS, CREDIT_TYPE_DROPDOWN_OPTIONS,
    EMPTY_ADDRESS, makeFieldSetter, validateCustomerCommon,
} from "@/app/customer/customerShared";

const inputReadOnlySt = { ...inputSt, background: "#f7f8fc", color: "#94a3b8", cursor: "not-allowed" };
const auditBorderSt = { ...metadataOuterSt, border: `1px solid ${C.border}` };

function StaticSingleDropdown({ label, options, selectedValue, onChange, error }) {
  const [isOpen, setIsOpen] = useState(false);
  const wrapRef = useRef(null);
  const boxRef = useRef(null);
  const [menuPos, setMenuPos] = useState({ top: 0, left: 0, width: 0 });

  useEffect(() => {
    if (!isOpen || !boxRef.current) return;
    const rect = boxRef.current.getBoundingClientRect();
    setMenuPos({
      top: rect.bottom + 4,
      left: rect.left,
      width: rect.width
    });
  }, [isOpen]);

  const handleToggle = () => setIsOpen(!isOpen);
  const handleSelect = (value) => {
    onChange(value);
    setIsOpen(false);
  };

  useEffect(() => {
    function handleClickOutside(event) {
      if (wrapRef.current && !wrapRef.current.contains(event.target)) {
        setIsOpen(false);
      }
    }
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  return (
    <>
      <style>{`
                .ssd-wrap { width: 100%; position: relative; font-family: "Segoe UI", sans-serif; }
                .ssd-label { font-size: 11px; font-weight: 700; color: #666666; margin-bottom: 0; margin-top: 0; display: block; letter-spacing: 0.6px; text-transform: uppercase; }
                .ssd-box {
                    width: 100%; padding: 10px 14px; height: 42px;
                    background: #fff; border: 1.2px solid #d1d5db;
                    border-radius: 8px; cursor: pointer;
                    box-sizing: border-box; font-size: 13px;
                    color: #1a1a1a; transition: border-color 0.15s;
                    display: flex; align-items: center; justify-content: space-between;
                }
                .ssd-box.err { border-color: #dc2626; background: #fee2e2; }
                .ssd-box:hover { border-color: #000000; }
                .ssd-box.open { border-color: #1a1a1a; }
                .ssd-chevron { font-size: 10px; color: #999999; transition: transform 0.2s; }
                .ssd-chevron.open { transform: rotate(180deg); }
                .ssd-menu {
                    position: fixed;
                    background: #ffffff; border: 1.5px solid #e8e8e8;
                    border-radius: 8px; margin-top: 4px;
                    max-height: 200px; overflow-y: auto;
                    box-shadow: 0 8px 24px rgba(0,0,0,0.08);
                    z-index: 99999;
                }
                .ssd-item {
                    width: 100%; padding: 9px 12px; display: flex;
                    align-items: center; gap: 9px;
                    cursor: pointer; border: none; border-bottom: 1px solid #f3f4f6;
                    background: transparent;
                    transition: background 0.1s; font-size: 13px; color: #374151;
                    text-align: left;
                }
                .ssd-item:last-child { border-bottom: none; }
                .ssd-item:hover { background: #f5f5f5; }
                .ssd-item.selected { background: rgba(0,0,0,0.06); color: #000000; font-weight: 500; }
            `}</style>
      <div className="ssd-wrap" ref={wrapRef}>
                <label className="ssd-label">{label}</label>
                <button
                    ref={boxRef}
                    type="button"
                    className={`ssd-box${isOpen ? " open" : ""}${error ? " err" : ""}`}
                    onClick={handleToggle}
                    aria-expanded={isOpen}
                    aria-haspopup="listbox"
                >
                    <span>
                        {selectedValue
                            ? options.find(o => o.value === selectedValue)?.label ?? selectedValue
                            : <span style={{ color: "#9ca3af" }}>Select…</span>}
                    </span>
                    <span className={`ssd-chevron${isOpen ? " open" : ""}`}>▼</span>
                </button>
                {isOpen && (
                    <div
                        className="ssd-menu"
                        style={{ top: menuPos.top, left: menuPos.left, width: menuPos.width }}
                        aria-label={`${label} options`}
                    >
                        {options.map(opt => (
                            <button
                                key={opt.value}
                                type="button"
                                className={`ssd-item${selectedValue === opt.value ? " selected" : ""}`}
                                onClick={() => handleSelect(opt.value)}
                            >
                                <span style={{ width: 18, display: "inline-block", textAlign: "center" }}>
                                    {selectedValue === opt.value ? "✓" : ""}
                                </span>
                                {opt.label}
                            </button>
                        ))}
                    </div>
                )}
            </div>
        </>
    );
}

StaticSingleDropdown.propTypes = {
  label: PropTypes.string.isRequired,
  options: PropTypes.arrayOf(PropTypes.shape({
    value: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    label: PropTypes.string,
  })).isRequired,
  selectedValue: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  onChange: PropTypes.func.isRequired,
  error: PropTypes.string,
};

export default function EditCustomer() {
    const router = useRouter();
    const params = useParams();
    const identifier = params?.identifier;
    const isSidebarOpen = useSidebarOpen();
 
    const [customerName, setCustomerName] = useState("");
    const [email, setEmail] = useState("");
    const [partyType, setPartyType] = useState("");
    const [credit, setCredit] = useState("");
    const [creditType, setCreditType] = useState("");
    const [creditLimit, setCreditLimit] = useState("");
    const [billingAddress, setBillingAddress] = useState({ ...EMPTY_ADDRESS });
    const [shippingAddress, setShippingAddress] = useState({ ...EMPTY_ADDRESS });
 
    const [auditInfo, setAuditInfo] = useState({
        createdBy: "",
        createdAt: "",
        modifiedBy: "",
        modifiedAt: "",
    });
 
    const [errors, setErrors] = useState({});
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");
    const [loading, setLoading] = useState(false);
    const [fetching, setFetching] = useState(true);
 
    useEffect(() => {
        if (!identifier) return;
        api
            .get("/customer/get", { params: { identifier } })
            .then((res) => {
                const d = res.data;
                setCustomerName(d.customerName ?? "");
                setEmail(d.email ?? "");
                setPartyType(d.partyType ?? "");
                setCredit(d.credit ?? "");
                setCreditType(d.creditType ?? "");
                setCreditLimit(d.creditLimit ?? "");
                setBillingAddress(d.billingAddress ? { ...EMPTY_ADDRESS, ...d.billingAddress } : EMPTY_ADDRESS);
                setShippingAddress(d.shippingAddress ? { ...EMPTY_ADDRESS, ...d.shippingAddress } : EMPTY_ADDRESS);
                setAuditInfo({
                    createdBy: d.createdBy,
                    createdAt: d.createdAt,
                    modifiedBy: d.modifiedBy,
                    modifiedAt: d.modifiedAt,
                });
            })
            .catch((err) => {
                console.error(err);
                setError("Failed to load customer data.");
            })
            .finally(() => setFetching(false));
    }, [identifier]);
 
    const setBillingField = makeFieldSetter(setBillingAddress);
    const setShippingField = makeFieldSetter(setShippingAddress);

    function validate() {
        const e = validateCustomerCommon({ customerName, email, partyType, credit, creditType, creditLimit, billingAddress, shippingAddress });
        setErrors(e);
        return Object.keys(e).length === 0;
    }
 
    async function handleSubmit(e) {
        e.preventDefault();
        setError("");
        setSuccess("");
        if (!validate()) return;
 
        setLoading(true);
        try {
            const res = await api.put("/customer/update", {
                identifier,
                customerName,
                email,
                partyType,
                credit: Number(credit),
                creditType,
                creditLimit: Number(creditLimit),
                billingAddress,
                shippingAddress,
            });
            const data = res.data;
            if (data?.success === false) {
                setError(data.message || "Failed to update customer.");
                return;
            }
            setSuccess("Customer updated successfully");
            setTimeout(() => router.back(), 1200);
        } catch (err) {
            console.error(err);
            setError("Unable to connect to server. Please try again.");
        } finally {
            setLoading(false);
        }
    }
 
    if (fetching) {
        return (
            <div style={{ ...pageSt, left: isSidebarOpen ? "220px" : "55px", display: "flex", alignItems: "center", justifyContent: "center" }}>
                <div style={{ color: C.muted, fontSize: "14px" }}>Loading customer details…</div>
            </div>
        );
    }
 
    return (
        <div style={{ ...pageSt, left: isSidebarOpen ? "220px" : "55px" }}>
            <div style={containerSt}>
                <div style={cardSt}>
                    <div style={headerRowSt}>
                        <button type="button" onClick={() => router.back()} style={backBtnSt}>⮜ Back</button>
                        <div>
                            <h2 style={titleSt}>Edit Customer</h2>
                            <p style={introTextSt}>Update customer profile details, credit settings, and address information without changing core data.</p>
                        </div>
                    </div>
 
                    {(error || success) && (
                        <div
                            style={{
                                ...alertBoxSt,
                                background: error ? C.errorBg : C.greenBg,
                                border: error ? `1px solid ${C.red}` : `1px solid ${C.green}`,
                                color: error ? C.red : C.green,
                            }}
                        >
                            {error || success}
                        </div>
                    )}
 
                    <form onSubmit={handleSubmit}>
                        <div style={sectionSt}>
                            <div style={sectionTitleSt}>Customer Details</div>
                            <div style={{ display: "grid", gridTemplateColumns: "repeat(3, 1fr)", gap: "16px" }}>
                                <div>
                                    <label style={labelSt} htmlFor="edit-customer-identifier">Phone Number</label>
                                    <input id="edit-customer-identifier" type="text" value={identifier} readOnly style={inputReadOnlySt} />
                                </div>
                                <div>
                                    <label style={labelSt} htmlFor="edit-customer-name">Customer Name</label>
                                    <input id="edit-customer-name" type="text" value={customerName} onChange={(e) => setCustomerName(e.target.value)} style={errors.customerName ? inputErrSt : inputSt} />
                                    {errors.customerName && <span style={errTextSt}>{errors.customerName}</span>}
                                </div>
                                <div>
                                    <label style={labelSt} htmlFor="edit-customer-email">Email</label>
                                    <input id="edit-customer-email" type="email" value={email} onChange={(e) => setEmail(e.target.value)} style={errors.email ? inputErrSt : inputSt} />
                                    {errors.email && <span style={errTextSt}>{errors.email}</span>}
                                </div>
                                <div>
                                    <StaticSingleDropdown
                                        label="Party Type"
                                        options={PARTY_TYPE_DROPDOWN_OPTIONS}
                                        selectedValue={partyType}
                                        onChange={setPartyType}
                                        error={errors.partyType}
                                    />
                                    {errors.partyType && <span style={errTextSt}>{errors.partyType}</span>}
                                </div>
                                <div>
                                    <label style={labelSt} htmlFor="edit-customer-credit">Credit</label>
                                    <input id="edit-customer-credit" type="number" min="0" value={credit} onChange={(e) => setCredit(e.target.value)} style={errors.credit ? inputErrSt : inputSt} />
                                    {errors.credit && <span style={errTextSt}>{errors.credit}</span>}
                                </div>
                                <div>
                                    <StaticSingleDropdown
                                        label="Credit Type"
                                        options={CREDIT_TYPE_DROPDOWN_OPTIONS}
                                        selectedValue={creditType}
                                        onChange={setCreditType}
                                        error={errors.creditType}
                                    />
                                    {errors.creditType && <span style={errTextSt}>{errors.creditType}</span>}
                                </div>
                                <div>
                                    <label style={labelSt} htmlFor="edit-customer-creditLimit">Credit Limit</label>
                                    <input id="edit-customer-creditLimit" type="number" min="0" value={creditLimit} onChange={(e) => setCreditLimit(e.target.value)} style={errors.creditLimit ? inputErrSt : inputSt} />
                                    {errors.creditLimit && <span style={errTextSt}>{errors.creditLimit}</span>}
                                </div>
                            </div>
                        </div>
 
                        <CustomerFormFooter billingAddress={billingAddress} shippingAddress={shippingAddress} errors={errors} onBillingChange={setBillingField} onShippingChange={setShippingField} loading={loading} onCancel={() => router.back()} submitLabel="Update Customer" loadingLabel="Updating…" />
                    </form>
 
                    <div style={auditBorderSt}>
                        <AuditCard
                            heading="Created"
                            rows={[
                                { label: "By", value: auditInfo.createdBy },
                                { label: "At", value: formatDateTime(auditInfo.createdAt) },
                            ]}
                        />
                        <AuditCard
                            heading="Last Modified"
                            rows={[
                                { label: "By", value: auditInfo.modifiedBy },
                                { label: "At", value: formatDateTime(auditInfo.modifiedAt) },
                            ]}
                        />
                    </div>
                </div>
            </div>
        </div>
    );
}