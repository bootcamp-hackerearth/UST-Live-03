"use client";

import PropTypes from "prop-types";

export const customerInitialData = {
  name: "",
  phoneNo: "",
  email: "",
  creditLimit: "",
  partyType: "",
  balance: "",
  balanceType: "Due",

  billingAddress: {
    addressLine: "",
    city: "",
    state: "",
    zip: "",
    country: "",
  },

  shippingAddress: {
    addressLine: "",
    city: "",
    state: "",
    zip: "",
    country: "",
  },
};

const InputField = ({
  label,
  name,
  value,
  onChange,
  type = "text",
  error,
  readOnly = false,
}) => (
  <div>
    <label className="block mb-1 font-medium">
      {label}
    </label>

    <input
      type={type}
      name={name}
      value={value || ""}
      onChange={onChange}
      readOnly={readOnly}
      className={`
        w-full
        border
        rounded-lg
        p-3
        text-black
        ${readOnly ? "bg-gray-100 cursor-not-allowed" : ""}
      `}
    />

    {error && (
      <p className="text-red-500 text-sm mt-1">
        {error}
      </p>
    )}
  </div>
);
InputField.propTypes = {
  label: PropTypes.string.isRequired,
  name: PropTypes.string.isRequired,
  value: PropTypes.oneOfType([
    PropTypes.string,
    PropTypes.number,
  ]),
  onChange: PropTypes.func.isRequired,
  type: PropTypes.string,
  error: PropTypes.string,
  readOnly: PropTypes.bool,
};

const AddressSection = ({
  title,
  prefix,
  value,
  handleChange,
  errors,
}) => {
  const updateAddress = (e) => {
    handleChange({
      target: {
        name: prefix,
        value: {
          ...value,
          [e.target.name]: e.target.value,
        },
      },
    });
  };
  AddressSection.propTypes = {
  title: PropTypes.string.isRequired,
  prefix: PropTypes.string.isRequired,
  value: PropTypes.shape({
    addressLine: PropTypes.string,
    city: PropTypes.string,
    state: PropTypes.string,
    zip: PropTypes.string,
    country: PropTypes.string,
  }),
  handleChange: PropTypes.func.isRequired,
  errors: PropTypes.object,
};

  const fields = [
    "addressLine",
    "city",
    "state",
    "zip",
    "country",
  ];

  return (
    <div className="space-y-3">
      <h3 className="font-semibold">
        {title}
      </h3>

      <div className="grid grid-cols-2 gap-4">
        {fields.map((field) => (
          <InputField
            key={field}
            label={field}
            name={field}
            value={value?.[field]}
            onChange={updateAddress}
            error={
              errors[
                `${prefix}${field.charAt(0).toUpperCase()}${field.slice(1)}`
              ]
            }
          />
        ))}
      </div>
    </div>
  );
};

const CustomerForm = ({
  formData,
  handleChange,
  errors = {},
}) => {
  return (
    <div className="space-y-6">
      <div className="grid grid-cols-2 gap-4">

        <InputField
          label="Customer Name"
          name="name"
          value={formData.name}
          onChange={handleChange}
          error={errors.name}
        />

        <InputField
          label="Phone"
          name="phoneNo"
          value={formData.phoneNo}
          onChange={handleChange}
          error={errors.phoneNo}
          readOnly={!!formData.identifier}
        />

        <InputField
          label="Email"
          name="email"
          value={formData.email}
          onChange={handleChange}
          error={errors.email}
        />

        <InputField
          label="Credit Limit"
          name="creditLimit"
          type="number"
          value={formData.creditLimit}
          onChange={handleChange}
          error={errors.creditLimit}
        />

      </div>

      <div className="grid grid-cols-2 gap-4">

<div>
<label className="block mb-1">
  Party Type{' '}

  <select
    name="partyType"
    value={formData.partyType}
    onChange={handleChange}
    className="w-full border rounded-lg p-3"
  >
    <option value="">
      Select
    </option>

    <option value="Customer">
      Customer
    </option>

    <option value="Dealer">
      Dealer
    </option>

    <option value="Wholesaler">
      Wholesaler
    </option>
  </select>
</label>

  {errors.partyType && (
    <p className="text-red-500 text-sm mt-1">
      {errors.partyType}
    </p>
  )}
</div>

        <div className="flex gap-2 items-end">

          <div className="flex-1">
            <InputField
              label="Balance"
              name="balance"
              type="number"
              value={formData.balance}
              onChange={handleChange}
              error={errors.balance}
            />
          </div>

          <select
            name="balanceType"
            value={formData.balanceType}
            onChange={handleChange}
            className="border rounded-lg p-3 h-[50px]"
          >
            <option value="Due">
              Due
            </option>

            <option value="Advance">
              Advance
            </option>
          </select>

        </div>

      </div>

      <AddressSection
        title="Billing Address"
        prefix="billingAddress"
        value={formData.billingAddress}
        handleChange={handleChange}
        errors={errors}
      />
      <AddressSection
        title="Shipping Address"
        prefix="shippingAddress"
        value={formData.shippingAddress}
        handleChange={handleChange}
        errors={errors}
      />

    </div>
  );
};

CustomerForm.propTypes = {
  formData: PropTypes.object.isRequired,
  handleChange: PropTypes.func.isRequired,
  errors: PropTypes.object,
};

export default CustomerForm;