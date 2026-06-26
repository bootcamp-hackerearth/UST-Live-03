"use client";

import PropTypes from "prop-types";
import { useState, useEffect } from "react";

function AddressSection({ title, address, onChange, errors }) {
  const idBase = (title || "").replace(/\s+/g, "-").toLowerCase();
  const field = (key) => (e) => onChange({ ...address, [key]: e.target.value });

  const handleZipcodeChange = (e) => {
    const val = e.target.value.replace(/\D/g, "").slice(0, 6);
    onChange({ ...address, zipcode: val });
  };

  const inputClass = (hasError) =>
    `w-full h-14 px-5 rounded-2xl border text-[#101828] outline-none transition-all focus:ring-4 ${
      hasError
        ? "border-red-400 focus:border-red-400 focus:ring-red-100"
        : "border-[#d0d5dd] bg-white focus:border-[#2563eb] focus:ring-blue-100"
    }`;

  return (
    <div className="space-y-4">
      <h3 className="text-sm font-semibold text-[#344054] uppercase tracking-wider">
        {title}
      </h3>
      <div className="grid grid-cols-2 gap-4">
        <div className="col-span-2">
          <label htmlFor={`${idBase}-addressLine`} className="block mb-2 text-sm font-medium text-[#344054]">Address Line</label>
          <input
            id={`${idBase}-addressLine`}
            type="text"
            value={address.addressLine || ""}
            onChange={field("addressLine")}
            className={inputClass(errors?.addressLine)}
          />
          {errors?.addressLine && (
            <p className="mt-1.5 text-xs text-red-500">{errors.addressLine}</p>
          )}
        </div>
        <div>
          <label htmlFor={`${idBase}-city`} className="block mb-2 text-sm font-medium text-[#344054]">City</label>
          <input
            id={`${idBase}-city`}
            type="text"
            value={address.city || ""}
            onChange={field("city")}
            className={inputClass(errors?.city)}
          />
          {errors?.city && (
            <p className="mt-1.5 text-xs text-red-500">{errors.city}</p>
          )}
        </div>
        <div>
          <label htmlFor={`${idBase}-state`} className="block mb-2 text-sm font-medium text-[#344054]">State</label>
          <input
            id={`${idBase}-state`}
            type="text"
            value={address.state || ""}
            onChange={field("state")}
            className={inputClass(errors?.state)}
          />
          {errors?.state && (
            <p className="mt-1.5 text-xs text-red-500">{errors.state}</p>
          )}
        </div>
        <div>
          <label htmlFor={`${idBase}-zipcode`} className="block mb-2 text-sm font-medium text-[#344054]">Zipcode</label>
          <input
            id={`${idBase}-zipcode`}
            type="tel"
            value={address.zipcode || ""}
            onChange={handleZipcodeChange}
            maxLength={6}
            className={inputClass(errors?.zipcode)}
          />
          {errors?.zipcode ? (
            <p className="mt-1.5 text-xs text-red-500">{errors.zipcode}</p>
          ) : (
            <p className="mt-1.5 text-xs text-[#667085]">{(address.zipcode || "").toString().length}/6 digits</p>
          )}
        </div>
        <div>
          <label htmlFor={`${idBase}-country`} className="block mb-2 text-sm font-medium text-[#344054]">Country</label>
          <input
            id={`${idBase}-country`}
            type="text"
            value={address.country || ""}
            onChange={field("country")}
            className={inputClass(errors?.country)}
          />
          {errors?.country && (
            <p className="mt-1.5 text-xs text-red-500">{errors.country}</p>
          )}
        </div>
      </div>
    </div>
  );
}

AddressSection.propTypes = {
  title: PropTypes.string,
  address: PropTypes.shape({
    addressLine: PropTypes.string,
    city: PropTypes.string,
    state: PropTypes.string,
    zipcode: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    country: PropTypes.string,
  }),
  onChange: PropTypes.func,
  errors: PropTypes.object,
};

const validateAddress = (address) => {
  const errors = {};
  if (!address?.addressLine?.trim()) errors.addressLine = "Address line is required.";
  if (!address?.city?.trim()) errors.city = "City is required.";
  if (!address?.state?.trim()) errors.state = "State is required.";
  if (!address?.country?.trim()) errors.country = "Country is required.";
  if (address?.zipcode?.toString()?.length !== 6)
    errors.zipcode = "Zipcode must be exactly 6 digits.";
  return errors;
};

export default function CustomerFields({
  name, setName,
  phoneNo, setPhoneNo,
  partyType, setPartyType,
  balance, setBalance,
  creditLimit, setCreditLimit,
  billingAddress, setBillingAddress,
  shippingAddress, setShippingAddress,
  sameAsShipping, setSameAsShipping,
  isEdit = false,
  onValidate,
}) {
  const [billingErrors, setBillingErrors] = useState({});
  const [shippingErrors, setShippingErrors] = useState({});
  const [partyTypeError, setPartyTypeError] = useState("");

  const partyTypeOptions = ["customer", "dealer", "wholesaler"];

  const handlePartyTypeToggle = (type) => {
    setPartyType((prev) => {
      const updated = prev.includes(type)
        ? prev.filter((t) => t !== type)
        : [...prev, type];
      if (updated.length > 0) setPartyTypeError("");
      return updated;
    });
  };

  const handleBillingChange = (updated) => {
      setBillingAddress(updated);
      const lastKey = Object.keys(updated).pop();
      if (billingErrors?.[lastKey]) {
        setBillingErrors((prev) => ({ ...prev, [lastKey]: undefined }));
      }
      if (sameAsShipping) setShippingAddress({ ...updated, addressType: "shippingAddress" });
    };

  const handleShippingChange = (updated) => {
      setShippingAddress(updated);
      const lastKey = Object.keys(updated).pop();
      if (shippingErrors?.[lastKey]) {
        setShippingErrors((prev) => ({ ...prev, [lastKey]: undefined }));
      }
    };

  const handleSameAsShipping = (e) => {
    setSameAsShipping(e.target.checked);
    if (e.target.checked) {
      setShippingAddress({ ...billingAddress, addressType: "shippingAddress" });
      setShippingErrors({});
    }
  };

  const validate = () => {
    const bErrors = validateAddress(billingAddress);
    const sErrors = sameAsShipping ? {} : validateAddress(shippingAddress);
    setBillingErrors(bErrors);
    setShippingErrors(sErrors);

    if (partyType.length === 0) {
      setPartyTypeError("Please select at least one party type.");
    } else {
      setPartyTypeError("");
    }

    return (
      Object.keys(bErrors).length === 0 &&
      Object.keys(sErrors).length === 0 &&
      partyType.length > 0
    );
  };

  useEffect(() => {
    if (onValidate) {
        onValidate(validate);
    }
    }, [onValidate]);

  const getPartyTypeClass = (type) => {
      if (partyType.includes(type)) {
        return "bg-blue-600 border-blue-600 text-white";
      }
      if (partyTypeError) {
        return "bg-white border-red-400 text-[#344054] hover:bg-gray-50";
      }
      return "bg-white border-[#d0d5dd] text-[#344054] hover:bg-gray-50";
    };

  return (
    <div className="space-y-6">
      <div className="grid grid-cols-2 gap-4">
        <div>
          <label htmlFor="customer-name" className="block mb-2 text-sm font-medium text-[#344054]">Name</label>
          <input
            id="customer-name"
            type="text"
            value={name}
            onChange={(e) => setName(e.target.value)}
            className="w-full h-14 px-5 rounded-2xl border border-[#d0d5dd] bg-white text-[#101828] outline-none transition-all focus:border-[#2563eb] focus:ring-4 focus:ring-blue-100"
          />
        </div>
        <div>
          <label htmlFor="customer-phone" className="block mb-2 text-sm font-medium text-[#344054]">Phone Number</label>
          <input
            id="customer-phone"
            type="tel"
            value={phoneNo}
            onChange={(e) => !isEdit && setPhoneNo(e.target.value.replace(/\D/g, "").slice(0, 10))}
            maxLength={10}
            readOnly={isEdit}
            className={`w-full h-14 px-5 rounded-2xl border border-[#d0d5dd] text-[#101828] outline-none transition-all ${
              isEdit
                ? "bg-gray-100 text-[#667085] cursor-not-allowed"
                : "bg-white focus:border-[#2563eb] focus:ring-4 focus:ring-blue-100"
            }`}
          />
          {isEdit && (
            <p className="mt-2 text-xs text-[#667085]">
              Phone number cannot be changed as it is linked to the address.
            </p>
          )}
        </div>
        <div>
          <label htmlFor="customer-balance" className="block mb-2 text-sm font-medium text-[#344054]">Balance</label>
          <input
            id="customer-balance"
            type="number"
            value={balance}
            onChange={(e) => setBalance(e.target.value)}
            className="w-full h-14 px-5 rounded-2xl border border-[#d0d5dd] bg-white text-[#101828] outline-none transition-all focus:border-[#2563eb] focus:ring-4 focus:ring-blue-100"
          />
        </div>
        <div>
          <label htmlFor="customer-creditLimit" className="block mb-2 text-sm font-medium text-[#344054]">Credit Limit</label>
          <input
            id="customer-creditLimit"
            type="number"
            value={creditLimit}
            onChange={(e) => setCreditLimit(e.target.value)}
            className="w-full h-14 px-5 rounded-2xl border border-[#d0d5dd] bg-white text-[#101828] outline-none transition-all focus:border-[#2563eb] focus:ring-4 focus:ring-blue-100"
          />
        </div>
      </div>

      <div>
        <p className="block mb-3 text-sm font-medium text-[#344054]">Party Type</p>
        <div className="flex gap-3">
          {partyTypeOptions.map((type) => (
            <button
              key={type}
              type="button"
              onClick={() => handlePartyTypeToggle(type)}
              className={`px-4 py-2 rounded-xl border text-sm font-medium transition-all capitalize ${getPartyTypeClass(type)}`}
            >
              {type}
            </button>
          ))}
        </div>
        {partyTypeError && (
          <p className="mt-1.5 text-xs text-red-500">{partyTypeError}</p>
        )}
      </div>

      <div className="border-t border-[#e4e7ec] pt-6">
        <AddressSection
          title="Billing Address"
          address={billingAddress}
          onChange={handleBillingChange}
          errors={billingErrors}
        />
      </div>

      <div className="flex items-center gap-2">
        <input
          id="sameAsShipping"
          type="checkbox"
          checked={sameAsShipping}
          onChange={handleSameAsShipping}
          className="w-4 h-4 rounded border-gray-300 text-blue-600"
        />
        <label htmlFor="sameAsShipping" className="text-sm text-[#344054]">
          Shipping address same as billing address
        </label>
      </div>

      {!sameAsShipping && (
        <AddressSection
          title="Shipping Address"
          address={shippingAddress}
          onChange={handleShippingChange}
          errors={shippingErrors}
        />
      )}
    </div>
  );
}

CustomerFields.propTypes = {
  name: PropTypes.string,
  setName: PropTypes.func,
  phoneNo: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  setPhoneNo: PropTypes.func,
  partyType: PropTypes.arrayOf(PropTypes.string),
  setPartyType: PropTypes.func,
  balance: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  setBalance: PropTypes.func,
  creditLimit: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  setCreditLimit: PropTypes.func,
  billingAddress: PropTypes.shape({
    addressLine: PropTypes.string,
    city: PropTypes.string,
    state: PropTypes.string,
    zipcode: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    country: PropTypes.string,
  }),
  setBillingAddress: PropTypes.func,
  shippingAddress: PropTypes.shape({
    addressLine: PropTypes.string,
    city: PropTypes.string,
    state: PropTypes.string,
    zipcode: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    country: PropTypes.string,
  }),
  setShippingAddress: PropTypes.func,
  sameAsShipping: PropTypes.bool,
  setSameAsShipping: PropTypes.func,
  isEdit: PropTypes.bool,
  onValidate: PropTypes.func,
};