"use client";

import PropTypes from "prop-types";
import { sectionSt, sectionTitleSt, labelSt, inputSt, inputErrSt, errTextSt, ADDRESS_FIELDS, buttonRowSt, cancelBtnSt, submitBtnSt, submitBtnDisabledSt } from "@/app/customer/customerShared";

function AddressFields({ prefix, address, errors, onChange }) {
    return (
        <div style={sectionSt}>
            <div style={sectionTitleSt}>{prefix} Address</div>
            <div style={{ display: "flex", flexDirection: "column", gap: "12px" }}>
                {ADDRESS_FIELDS.map((f) => (
                    <div key={f.key}>
                        <label style={labelSt}>{f.label}</label>
                        <input
                            type="text"
                            value={address[f.key]}
                            onChange={(e) => onChange(f.key, e.target.value)}
                            style={errors[`${prefix.toLowerCase()}_${f.key}`] ? inputErrSt : inputSt}
                        />
                        {errors[`${prefix.toLowerCase()}_${f.key}`] && (
                            <span style={errTextSt}>{errors[`${prefix.toLowerCase()}_${f.key}`]}</span>
                        )}
                    </div>
                ))}
            </div>
        </div>
    );
}

AddressFields.propTypes = {
    prefix: PropTypes.string.isRequired,
    address: PropTypes.object.isRequired,
    errors: PropTypes.object.isRequired,
    onChange: PropTypes.func.isRequired,
};

export default function AddressPair({ billingAddress, shippingAddress, errors, onBillingChange, onShippingChange }) {
    return (
        <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "16px" }}>
            <AddressFields prefix="Billing" address={billingAddress} errors={errors} onChange={onBillingChange} />
            <AddressFields prefix="Shipping" address={shippingAddress} errors={errors} onChange={onShippingChange} />
        </div>
    );
}

AddressPair.propTypes = {
    billingAddress: PropTypes.object.isRequired,
    shippingAddress: PropTypes.object.isRequired,
    errors: PropTypes.object.isRequired,
    onBillingChange: PropTypes.func.isRequired,
    onShippingChange: PropTypes.func.isRequired,
};

export function CustomerFormFooter({ billingAddress, shippingAddress, errors, onBillingChange, onShippingChange, loading, onCancel, submitLabel, loadingLabel }) {
    return (
        <>
            <AddressPair billingAddress={billingAddress} shippingAddress={shippingAddress} errors={errors} onBillingChange={onBillingChange} onShippingChange={onShippingChange} />
            <div style={buttonRowSt}>
                <button type="button" onClick={onCancel} style={cancelBtnSt}>Cancel</button>
                <button type="submit" disabled={loading} style={loading ? submitBtnDisabledSt : submitBtnSt}>
                    {loading ? loadingLabel : submitLabel}
                </button>
            </div>
        </>
    );
}

CustomerFormFooter.propTypes = {
    billingAddress: PropTypes.object.isRequired,
    shippingAddress: PropTypes.object.isRequired,
    errors: PropTypes.object.isRequired,
    onBillingChange: PropTypes.func.isRequired,
    onShippingChange: PropTypes.func.isRequired,
    loading: PropTypes.bool.isRequired,
    onCancel: PropTypes.func.isRequired,
    submitLabel: PropTypes.string.isRequired,
    loadingLabel: PropTypes.string.isRequired,
};
