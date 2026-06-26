// components/customer/CustomerShared.jsx
"use client";

import PropTypes from "prop-types";
import { ChevronDown, ChevronUp } from "lucide-react";

export { formatDateTime, AuditTrail, FormFooter, ErrorBanner, SuccessBanner } from "@/components/shared/FormShared";

export const inputClass = "w-full px-3 py-2.5 border border-gray-200 rounded-lg text-sm focus:outline-none focus:border-[#006E74] focus:ring-2 focus:ring-[#006E74]/10 bg-white text-[#231F20]";
export const labelClass = "block text-[10px] font-bold text-[#006E74] uppercase tracking-widest mb-1.5";

export function AddressBlock({ data, onChange, title }) {
    return (
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 mt-4">
            {[
                { key: "addressLine", label: "Address Line", full: true },
                { key: "city", label: "City" },
                { key: "state", label: "State" },
                { key: "country", label: "Country" },
                { key: "zipcode", label: "Zipcode" },
            ].map(({ key, label, full }) => {
                const id = `${title || "addr"}-${key}`;
                return (
                    <div key={key} className={full ? "sm:col-span-2" : ""}>
                        <label htmlFor={id} className={labelClass}>{label}</label>
                        <input
                            id={id}
                            type="text"
                            value={data[key] || ""}
                            onChange={(e) => onChange(key, e.target.value)}
                            placeholder={`Enter ${label.toLowerCase()}`}
                            className={inputClass}
                        />
                    </div>
                );
            })}
        </div>
    );
}

AddressBlock.propTypes = {
    data: PropTypes.object.isRequired,
    onChange: PropTypes.func.isRequired,
    title: PropTypes.string,
};

export function AccordionSection({ label, isOpen, onToggle, children }) {
    return (
        <div className="bg-white rounded-xl border border-gray-100 shadow-sm overflow-hidden">
            <button
                type="button"
                onClick={onToggle}
                className="w-full flex items-center justify-between px-6 py-4 text-left hover:bg-slate-50 transition-colors cursor-pointer border-none bg-transparent"
            >
                <span className="text-xs font-bold text-gray-400 uppercase tracking-widest">{label}</span>
                {isOpen
                    ? <ChevronUp size={16} className="text-gray-400" />
                    : <ChevronDown size={16} className="text-gray-400" />
                }
            </button>
            {isOpen && (
                <div className="px-6 pb-6 border-t border-gray-50">
                    {children}
                </div>
            )}
        </div>
    );
}

AccordionSection.propTypes = {
    label: PropTypes.string.isRequired,
    isOpen: PropTypes.bool.isRequired,
    onToggle: PropTypes.func.isRequired,
    children: PropTypes.node.isRequired,
};

export function CustomerPageHeader({ title, subtitle, icon: Icon }) {
    return (
        <div className="flex items-center gap-3 mb-8">
            <div className="w-10 h-10 rounded-xl bg-[#006E74]/10 flex items-center justify-center">
                <Icon size={20} className="text-[#006E74]" />
            </div>
            <div>
                <h1 className="text-2xl font-bold text-[#231F20]">{title}</h1>
                <p className="text-xs text-gray-400 mt-0.5">{subtitle}</p>
            </div>
        </div>
    );
}

CustomerPageHeader.propTypes = {
    title: PropTypes.string.isRequired,
    subtitle: PropTypes.string.isRequired,
    icon: PropTypes.elementType.isRequired,
};

export function CustomerCoreFields({ form, onChange, partyTypes, balanceTypes, identifierField = null }) {
    return (
        <div className="bg-white rounded-xl border border-gray-100 shadow-sm p-6">
            <p className="text-xs font-bold text-gray-400 uppercase tracking-widest mb-5">Core Details</p>

            {/* Identifier row — shown on both add (editable) and edit (read-only) */}
            {identifierField && (
                <div className="mb-5">
                    <label htmlFor="cust-identifier" className={labelClass}>
                        {identifierField.label}
                        {identifierField.required && <span className="text-red-400"> *</span>}
                    </label>
                    <input
                        id="cust-identifier"
                        type="tel"
                        value={identifierField.value}
                        disabled={identifierField.disabled}
                        onChange={identifierField.onChange ? (e) => identifierField.onChange(e.target.value) : undefined}
                        placeholder={identifierField.placeholder || ""}
                        required={identifierField.required}
                        className={
                            identifierField.disabled
                                ? "w-full px-3 py-2.5 border border-gray-100 rounded-lg text-sm bg-gray-50 text-gray-400 cursor-not-allowed"
                                : inputClass
                        }
                    />
                </div>
            )}

            <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-3 gap-5">
                <div>
                    <label htmlFor="cust-name" className={labelClass}>
                        Full Name {!identifierField?.disabled && <span className="text-red-400">*</span>}
                    </label>
                    <input
                        id="cust-name"
                        type="text"
                        value={form.customerName}
                        onChange={(e) => onChange("customerName", e.target.value)}
                        placeholder="Customer full name"
                        className={inputClass}
                        required={!identifierField?.disabled}
                    />
                </div>
                <div>
                    <label htmlFor="cust-email" className={labelClass}>
                        Email Address {!identifierField?.disabled && <span className="text-red-400">*</span>}
                    </label>
                    <input
                        id="cust-email"
                        type="email"
                        value={form.username}
                        onChange={(e) => onChange("username", e.target.value)}
                        placeholder="name@email.com"
                        className={inputClass}
                        required={!identifierField?.disabled}
                    />
                </div>
                <div>
                    <label htmlFor="cust-partyType" className={labelClass}>Party Type</label>
                    <select
                        id="cust-partyType"
                        value={form.partyType}
                        onChange={(e) => onChange("partyType", e.target.value)}
                        className={inputClass}
                    >
                        {partyTypes.map((t) => <option key={t} value={t}>{t}</option>)}
                    </select>
                </div>
                <div>
                    <label htmlFor="cust-balance" className={labelClass}>
                        {identifierField?.disabled ? "Balance" : "Opening Balance"}
                    </label>
                    <input
                        id="cust-balance"
                        type="number"
                        value={form.balance}
                        onChange={(e) => onChange("balance", e.target.value)}
                        placeholder="0.00"
                        min="0"
                        step="0.01"
                        className={inputClass}
                    />
                </div>
                <div>
                    <label htmlFor="cust-balanceType" className={labelClass}>Balance Type</label>
                    <select
                        id="cust-balanceType"
                        value={form.balanceType}
                        onChange={(e) => onChange("balanceType", e.target.value)}
                        className={inputClass}
                    >
                        {balanceTypes.map((t) => <option key={t} value={t}>{t}</option>)}
                    </select>
                </div>
                <div>
                    <label htmlFor="cust-creditLimit" className={labelClass}>Credit Limit</label>
                    <input
                        id="cust-creditLimit"
                        type="number"
                        value={form.creditLimit}
                        onChange={(e) => onChange("creditLimit", e.target.value)}
                        placeholder="0.00"
                        min="0"
                        step="0.01"
                        className={inputClass}
                    />
                </div>
            </div>
        </div>
    );
}

CustomerCoreFields.propTypes = {
    form: PropTypes.object.isRequired,
    onChange: PropTypes.func.isRequired,
    partyTypes: PropTypes.arrayOf(PropTypes.string).isRequired,
    balanceTypes: PropTypes.arrayOf(PropTypes.string).isRequired,
    identifierField: PropTypes.shape({
        label: PropTypes.string,
        value: PropTypes.string,
        disabled: PropTypes.bool,
        onChange: PropTypes.func,
        placeholder: PropTypes.string,
        required: PropTypes.bool,
    }),
};

export function CustomerLoadingSkeleton() {
    return (
        <div className="min-h-screen bg-slate-50 p-6">
            <div className="animate-pulse space-y-4">
                <div className="h-8 w-48 bg-gray-200 rounded-lg" />
                <div className="bg-white rounded-xl border border-gray-100 p-6">
                    <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-3 gap-5">
                        {Array.from({ length: 6 }, (_, i) => (
                            <div key={i} className="h-10 bg-gray-100 rounded-lg" />
                        ))}
                    </div>
                </div>
            </div>
        </div>
    );
}