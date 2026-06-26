"use client";

import PropTypes from "prop-types";
import AddressSection from "./AddressSection";
import AuditCard from "./AuditCard";
import {
    INPUT_CLS,
    FieldWrapper,
    ErrorBanner,
    FormActions,
} from "./formUtils";

export default function CustomerForm({
    title,
    subtitle,
    form,
    onFieldChange,
    onAddressChange,
    sameAsShipping,
    onSameAsShippingChange,
    phoneDisabled = false,
    onPhoneChange,
    error,
    loading,
    onSubmit,
    onCancel,
    submitLabel,
    auditData = null,
}) {
    return (
        <div className="mx-auto w-full max-w-3xl">
            <div className="mb-6">
                <h2 className="text-2xl font-black tracking-tight text-slate-950">
                    {title}
                </h2>
                <p className="mt-1 text-sm font-medium text-slate-500">{subtitle}</p>
            </div>

            <form
                onSubmit={onSubmit}
                className="rounded-lg border border-slate-200 bg-white p-6 shadow-sm md:p-8"
            >
                {error && <ErrorBanner message={error} />}

                <div className="grid gap-5 md:grid-cols-2">

                    <FieldWrapper label={phoneDisabled ? "Phone Number" : "Phone Number *"}>
                        <input
                            type="tel"
                            value={form.phoneNo}
                            disabled={phoneDisabled}
                            onChange={
                                phoneDisabled
                                    ? undefined
                                    : (e) =>
                                        onPhoneChange?.(
                                            e.target.value.replaceAll(/\D/g, "").slice(0, 10)
                                        )
                            }
                            placeholder="Enter 10-digit phone number"
                            required={!phoneDisabled}
                            pattern={phoneDisabled ? undefined : "[0-9]{10}"}
                            maxLength={10}
                            className={`${INPUT_CLS} ${phoneDisabled ? "disabled:bg-slate-100 disabled:text-slate-400 cursor-not-allowed" : ""}`}
                        />
                    </FieldWrapper>

                    <FieldWrapper label={phoneDisabled ? "Customer Name" : "Customer Name *"}>
                        <input
                            type="text"
                            value={form.name}
                            onChange={(e) => onFieldChange("name", e.target.value)}
                            placeholder="Enter customer name"
                            required={!phoneDisabled}
                            className={INPUT_CLS}
                        />
                    </FieldWrapper>

                    <div className="md:col-span-2">
                        <FieldWrapper label={phoneDisabled ? "Email" : "Email *"}>
                            <input
                                type="email"
                                value={form.email}
                                onChange={(e) => onFieldChange("email", e.target.value)}
                                placeholder="Enter email"
                                required={!phoneDisabled}
                                className={INPUT_CLS}
                            />
                        </FieldWrapper>
                    </div>

                    <AddressSection
                        title="Shipping Address"
                        prefix="shippingAddress"
                        values={form.shippingAddress}
                        onChange={onAddressChange}
                    />

                    <div className="md:col-span-2 flex items-center gap-2">
                        <input
                            type="checkbox"
                            id="sameAddress"
                            checked={sameAsShipping}
                            onChange={(e) => onSameAsShippingChange(e.target.checked)}
                            className="w-4 h-4 cursor-pointer accent-cyan-600"
                        />
                        <label
                            htmlFor="sameAddress"
                            className="text-sm text-slate-500 cursor-pointer"
                        >
                            Billing address same as shipping
                        </label>
                    </div>

                    {!sameAsShipping && (
                        <AddressSection
                            title="Billing Address"
                            prefix="billingAddress"
                            values={form.billingAddress}
                            onChange={onAddressChange}
                        />
                    )}

                    {auditData && (
                        <>
                            <div className="md:col-span-2">
                                <div className="border-t border-slate-100" />
                            </div>
                            <AuditCard
                                title="Created"
                                byValue={auditData.createdBy}
                                atValue={auditData.createdAt}
                            />
                            <AuditCard
                                title="Last Modified"
                                byValue={auditData.modifiedBy}
                                atValue={auditData.modifiedAt}
                            />
                        </>
                    )}

                </div>

                <FormActions
                    onCancel={onCancel}
                    loading={loading}
                    submitLabel={submitLabel}
                />
            </form>
        </div>
    );
}

const addressShape = PropTypes.shape({
    addressLine: PropTypes.string,
    city: PropTypes.string,
    state: PropTypes.string,
    zip: PropTypes.string,
    country: PropTypes.string,
});

CustomerForm.propTypes = {
    title: PropTypes.string.isRequired,
    subtitle: PropTypes.string.isRequired,
    form: PropTypes.shape({
        phoneNo: PropTypes.string.isRequired,
        name: PropTypes.string.isRequired,
        email: PropTypes.string.isRequired,
        billingAddress: addressShape.isRequired,
        shippingAddress: addressShape.isRequired,
    }).isRequired,
    onFieldChange: PropTypes.func.isRequired,
    onAddressChange: PropTypes.func.isRequired,
    sameAsShipping: PropTypes.bool.isRequired,
    onSameAsShippingChange: PropTypes.func.isRequired,
    phoneDisabled: PropTypes.bool,
    onPhoneChange: PropTypes.func,
    error: PropTypes.string,
    loading: PropTypes.bool.isRequired,
    onSubmit: PropTypes.func.isRequired,
    onCancel: PropTypes.func.isRequired,
    submitLabel: PropTypes.string.isRequired,
    auditData: PropTypes.shape({
    createdBy: PropTypes.string,
    createdAt: PropTypes.string,
    modifiedBy: PropTypes.string,
    modifiedAt: PropTypes.string,
    }),
};