import PropTypes from "prop-types";

export const baseInputClass =
    "py-2 px-3 border-[1.5px] rounded-lg text-sm outline-none bg-[#fafaf8] box-border w-full transition-all focus:border-brand";

export const fieldWrap = "flex flex-col gap-1.25";
export const labelClass = "text-xs md:text-sm font-semibold text-gray-600";

export const PARTY_TYPE_OPTIONS = ["Customer", "Member", "Dealer"];
export const CREDIT_TYPE_OPTIONS = ["Prepaid", "Postpaid", "Credit Line"];
export const ADDRESS_FIELDS = ["addressLine", "city", "state", "country", "zipCode"];

export const EMPTY_ADDRESS = {
    addressLine: "",
    city: "",
    state: "",
    country: "",
    zipCode: "",
};

export function FieldError({ message }) {
    if (!message) return null;
    return <span className="text-[11px] text-[#e53e3e] mt-0.5 block">{message}</span>;
}

FieldError.propTypes = {
    message: PropTypes.oneOfType([PropTypes.string, PropTypes.node]),
};

export function SectionHeading({ title }) {
    return (
        <div className="col-span-1 md:col-span-2 mt-2 mb-1">
            <p className="text-xs font-bold text-brand uppercase tracking-widest border-b border-gray-100 pb-1">
                {title}
            </p>
        </div>
    );
}

SectionHeading.propTypes = {
    title: PropTypes.oneOfType([PropTypes.string, PropTypes.node]).isRequired,
};

export function formatDateTime(raw) {
    if (!raw) return "—";
    const d = new Date(raw);
    return d.toLocaleString("en-IN", {
        day: "2-digit", month: "short", year: "numeric",
        hour: "2-digit", minute: "2-digit",
    });
}

export function getAddressLabel(field) {
    if (field === "addressLine") return "Address Line";
    if (field === "zipCode") return "Zip Code";
    return field.charAt(0).toUpperCase() + field.slice(1);
}

export function getAddressPlaceholder(field) {
    if (field === "addressLine") return "address line";
    if (field === "zipCode") return "zip code";
    return field;
}

export function buildAddressErrors(address, prefix) {
    const errs = {};
    ADDRESS_FIELDS.forEach((f) => {
        if (!address[f]?.trim()) {
            errs[`${prefix}_${f}`] = `${getAddressLabel(f)} is required.`;
        }
    });
    if (address.zipCode && !/^\d{4,10}$/.test(address.zipCode)) {
        errs[`${prefix}_zipCode`] = "Enter a valid zip code.";
    }
    return errs;
}

export function validateEmail(emailToCheck) {
    if (!emailToCheck) return "Email is required.";
    if (emailToCheck.length > 254 || !/^[^\s@]{1,64}@[^\s@]{1,255}\.[^\s@]{2,24}$/.test(emailToCheck)) {
        return "Enter a valid email address.";
    }
    return "";
}

export function AddressFieldGroup({ title, address, errors, prefix, getInputClass, onChange }) {
    return (
        <>
            <SectionHeading title={title} />
            {ADDRESS_FIELDS.map((f) => (
                <div key={f} className={fieldWrap}>
                    <label htmlFor={`${prefix}_${f}`} className={labelClass}>
                        {getAddressLabel(f)}
                    </label>
                    <input
                        id={`${prefix}_${f}`}
                        className={getInputClass(`${prefix}_${f}`)}
                        type="text"
                        placeholder={`Enter ${getAddressPlaceholder(f)}`}
                        value={address[f]}
                        onChange={(e) => onChange(f, e.target.value)}
                    />
                    <FieldError message={errors[`${prefix}_${f}`]} />
                </div>
            ))}
        </>
    );
}

AddressFieldGroup.propTypes = {
    title: PropTypes.string.isRequired,
    address: PropTypes.object.isRequired,
    errors: PropTypes.object.isRequired,
    prefix: PropTypes.string.isRequired,
    getInputClass: PropTypes.func.isRequired,
    onChange: PropTypes.func.isRequired,
};

export function RecordInfoPanel({ auditInfo }) {
    const rows = [
        ["Created By", auditInfo.createdBy || "—"],
        ["Created On", formatDateTime(auditInfo.createdOn)],
        ["Modified By", auditInfo.modifiedBy || "—"],
        ["Modified On", formatDateTime(auditInfo.modifiedOn)],
    ];
    return (
        <div className="mt-6 pt-5 border-t border-gray-100">
            <p className="text-[11px] font-semibold text-gray-400 uppercase tracking-wider mb-3">Record Info</p>
            <div className="grid grid-cols-2 gap-x-6 gap-y-3">
                {rows.map(([label, value]) => (
                    <div key={label} className="flex flex-col gap-0.5">
                        <span className="text-[11px] font-semibold text-gray-400">{label}</span>
                        <span className="text-[13px] text-gray-700">{value}</span>
                    </div>
                ))}
            </div>
        </div>
    );
}

RecordInfoPanel.propTypes = {
    auditInfo: PropTypes.object.isRequired,
};
export function CustomerInfoFields({ values, errors, getInputClass, onChange }) {
    return (
        <>
            <div className={fieldWrap}>
                <label htmlFor="customerName" className={labelClass}>Customer Name</label>
                <input
                    id="customerName"
                    className={getInputClass("customerName")}
                    type="text"
                    placeholder="Enter customer name"
                    value={values.customerName}
                    onChange={(e) => onChange("customerName", e.target.value)}
                />
                <FieldError message={errors.customerName} />
            </div>

            <div className={fieldWrap}>
                <label htmlFor="email" className={labelClass}>Email</label>
                <input
                    id="email"
                    className={getInputClass("email")}
                    type="text"
                    placeholder="Enter email"
                    value={values.email}
                    onChange={(e) => onChange("email", e.target.value)}
                />
                <FieldError message={errors.email} />
            </div>

            <div className={fieldWrap}>
                <label htmlFor="partyType" className={labelClass}>Party Type</label>
                <select
                    id="partyType"
                    className={getInputClass("partyType")}
                    value={values.partyType}
                    onChange={(e) => onChange("partyType", e.target.value)}
                >
                    <option value="">Select Party Type</option>
                    {PARTY_TYPE_OPTIONS.map((opt) => (
                        <option key={opt} value={opt}>{opt}</option>
                    ))}
                </select>
                <FieldError message={errors.partyType} />
            </div>

            <SectionHeading title="Credit Info" />

            <div className={fieldWrap}>
                <label htmlFor="creditType" className={labelClass}>Credit Type</label>
                <select
                    id="creditType"
                    className={getInputClass("creditType")}
                    value={values.creditType}
                    onChange={(e) => onChange("creditType", e.target.value)}
                >
                    <option value="">Select Credit Type</option>
                    {CREDIT_TYPE_OPTIONS.map((opt) => (
                        <option key={opt} value={opt}>{opt}</option>
                    ))}
                </select>
                <FieldError message={errors.creditType} />
            </div>

            <div className={fieldWrap}>
                <label htmlFor="credit" className={labelClass}>Credit</label>
                <input
                    id="credit"
                    className={getInputClass("credit")}
                    type="number"
                    placeholder="Enter credit"
                    value={values.credit}
                    onChange={(e) => onChange("credit", e.target.value)}
                />
                <FieldError message={errors.credit} />
            </div>

            <div className={fieldWrap}>
                <label htmlFor="creditLimit" className={labelClass}>Credit Limit</label>
                <input
                    id="creditLimit"
                    className={getInputClass("creditLimit")}
                    type="number"
                    placeholder="Enter credit limit"
                    value={values.creditLimit}
                    onChange={(e) => onChange("creditLimit", e.target.value)}
                />
                <FieldError message={errors.creditLimit} />
            </div>
        </>
    );
}

CustomerInfoFields.propTypes = {
    values: PropTypes.object.isRequired,
    errors: PropTypes.object.isRequired,
    getInputClass: PropTypes.func.isRequired,
    onChange: PropTypes.func.isRequired,
};
export function getInputClass(errors) {
    return (errKey) =>
        `${baseInputClass} ${errors[errKey] ? "border-[#e53e3e]" : "border-gray-300"}`;
}

export function validateCustomerCommonFields({ customerName, email, partyType, creditType, credit, creditLimit }) {
    const errs = {};

    if (!customerName.trim()) errs.customerName = "Customer name is required.";

    const emailError = validateEmail(email?.trim() || "");
    if (emailError) errs.email = emailError;

    if (!partyType) errs.partyType = "Party type is required.";
    if (!creditType) errs.creditType = "Credit type is required.";

    if (credit === "" || credit === null) errs.credit = "Credit is required.";
    else if (Number.isNaN(Number(credit)) || Number(credit) < 0) errs.credit = "Enter a valid credit amount.";

    if (creditLimit === "" || creditLimit === null) errs.creditLimit = "Credit limit is required.";
    else if (Number.isNaN(Number(creditLimit)) || Number(creditLimit) < 0) errs.creditLimit = "Enter a valid credit limit.";

    return errs;
}

export function createAddressChangeHandler(prefix, setAddress, errors, setErrors, onAfterChange) {
    return function handleChange(key, value) {
        setAddress((prev) => ({ ...prev, [key]: value }));
        if (errors[`${prefix}_${key}`]) setErrors((prev) => ({ ...prev, [`${prefix}_${key}`]: "" }));
        if (onAfterChange) onAfterChange(key, value);
    };
}