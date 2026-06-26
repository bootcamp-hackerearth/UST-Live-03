'use client';

import { useState } from 'react';
import api from '@/app/services/api';
import PropTypes from 'prop-types';

const AddCustomer = ({
  closeModal,
  refreshData
}) => {

  const [customer, setCustomer] = useState({
    name: '',
    phoneNo: '',
    email: '',
    partyType: '',
    balance: '',
    balanceType: 'Due',
    creditLimit: '',
    status: true,

    billingAddress: {
      addressLine: '',
      city: '',
      state: '',
      zip: '',
      country: ''
    },

    shippingAddress: {
      addressLine: '',
      city: '',
      state: '',
      zip: '',
      country: ''
    }
  });
  const [sameAsBilling, setSameAsBilling] = useState(false);

  const [message, setMessage] = useState('');
  const [messageType, setMessageType] = useState('');

  const handleChange = (e) => {

    const { name, value } = e.target;

    setCustomer({
      ...customer,
      [name]: value
    });

  };

  const handleAddressChange = (type, field, value) => {
  setCustomer((prev) => {
    const updated = {
      ...prev,
      [type]: {
        ...prev[type],
        [field]: value
      }
    };

    if (sameAsBilling && type === "billingAddress") {
      updated.shippingAddress = {
        ...updated.billingAddress
      };
    }

    return updated;
  });
};

const handleSameAsBilling = (checked) => {
  setSameAsBilling(checked);

  if (checked) {
    setCustomer((prev) => ({
      ...prev,
      shippingAddress: { ...prev.billingAddress }
    }));
  }
};

const isValidEmail = (email) => {
  const input = document.createElement("input");
  input.type = "email";
  input.value = email.trim();
  return input.checkValidity();
};

const validate = () => {

  const validations = [
    {
      valid: customer.name.trim(),
      message: 'Customer Name is required'
    },
    {
      valid: customer.phoneNo.trim(),
      message: 'Phone Number is required'
    },
    {
      valid: /^[6-9]\d{9}$/.test(customer.phoneNo),
      message: 'Phone Number must be 10 digits and start with 6, 7, 8 or 9'
    },
    {
      valid: customer.email.trim(),
      message: 'Email is required'
    },
    {
      valid: isValidEmail(customer.email),
      message: 'Please enter a valid email address'
    },
    {
      valid: customer.partyType.trim(),
      message: 'Party Type is required'
    },
    {
      valid: customer.balance.toString().trim(),
      message: 'Balance is required'
    },
    {
      valid: customer.creditLimit.toString().trim(),
      message: 'Credit Limit is required'
    },
    {
      valid: customer.billingAddress.addressLine.trim(),
      message: 'Billing Address is required'
    },
    {
      valid: customer.billingAddress.city.trim(),
      message: 'Billing City is required'
    },
    {
      valid: customer.billingAddress.state.trim(),
      message: 'Billing State is required'
    },
    {
      valid: customer.billingAddress.zip.trim(),
      message: 'Billing ZIP is required'
    },
    {
      valid: customer.billingAddress.country.trim(),
      message: 'Billing Country is required'
    }
  ];

  if (!sameAsBilling) {
    validations.push(
      {
        valid: customer.shippingAddress.addressLine.trim(),
        message: 'Shipping Address is required'
      },
      {
        valid: customer.shippingAddress.city.trim(),
        message: 'Shipping City is required'
      },
      {
        valid: customer.shippingAddress.state.trim(),
        message: 'Shipping State is required'
      },
      {
        valid: customer.shippingAddress.zip.trim(),
        message: 'Shipping ZIP is required'
      },
      {
        valid: customer.shippingAddress.country.trim(),
        message: 'Shipping Country is required'
      }
    );
  }

  const error = validations.find(({ valid }) => !valid);

  if (error) {
    setMessageType('error');
    setMessage(error.message);
    return false;
  }

  return true;
};

const handleSubmit = async (e) => {

  e.preventDefault();

  setMessage('');
  setMessageType('');

  if (!validate()) {
    return;
  }

  try {
    const response = await api.post(
      "/customer/add",
      customer
    );

    if (response.data.success) {

      alert(
        response.data.message ||
        "Customer Added Successfully"
      );

      refreshData();
      closeModal();

    } else {

      alert(
        response.data.message ||
        "Failed to add customer"
      );

    }

  } catch (err) {

    console.error(err);

    alert(
      err?.response?.data?.message ||
      "Failed to add customer"
    );

  }
};

  return (
  <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
    
    <div className="bg-white w-full max-w-4xl rounded-xl shadow-lg max-h-[90vh] flex flex-col">

      <div className="p-5 border-b">
        <h2 className="text-xl font-semibold">Add Customer</h2>
        {message && (
          <div
            className={`mt-4 rounded-lg px-4 py-3 text-sm border ${
              messageType === 'error'
                ? 'border-red-200 bg-red-50 text-red-600'
                : 'border-green-200 bg-green-50 text-green-600'
            }`}
          >
            {message}
          </div>
        )}
      </div>

      <form
        onSubmit={handleSubmit}
        className="p-6 overflow-y-auto space-y-6"
      >

        <div className="grid md:grid-cols-2 gap-4">

          <div>
            <label 
            htmlFor='name'
            className="text-sm text-gray-600">Customer Name</label>
            <input
              name="name"
              value={customer.name}
              onChange={handleChange}
              className="w-full border p-2.5 rounded-lg focus:ring-2 focus:ring-slate-400"
              required
            />
          </div>

          <div>
            <label 
            htmlFor='phoneNo'
            className="text-sm text-gray-600">Phone Number</label>

            <input
              name="phoneNo"
              value={customer.phoneNo}
              onChange={(e) => {
                const value = e.target.value.replaceAll(/\D/g, '');
                  if (
                    value.length > 0 &&
                    !['6', '7', '8', '9'].includes(value.charAt(0))
                  ) {
                    return;
                  }

                if (value.length <= 10) {
                  setCustomer({
                    ...customer,
                    phoneNo: value,
                  });
                }
              }}
              pattern="[6-9]\d{9}"
              maxLength={10}
              placeholder="Enter 10 digit mobile number"
              className="w-full border p-2.5 rounded-lg focus:ring-2 focus:ring-slate-400"
              required
            />

            {customer.phoneNo &&
              !/^[6-9]\d{9}$/.test(customer.phoneNo) && (
                <p className="text-red-500 text-sm mt-1">
                  Mobile number must start with 6, 7, 8, or 9 and contain 10 digits
                </p>
              )}
          </div>

          <div>
            <label 
            htmlFor='email'
            className="text-sm text-gray-600">Email</label>
            <input
              type="email"
              name="email"
              value={customer.email}
              onChange={handleChange}
              className="w-full border p-2.5 rounded-lg focus:ring-2 focus:ring-slate-400"
              required
            />
          </div>

          <div>
            <label 
            htmlFor='partyType'
            className="text-sm text-gray-600">Party Type</label>
            <select
              name="partyType"
              value={customer.partyType}
              onChange={handleChange}
              className="w-full border p-2.5 rounded-lg focus:ring-2 focus:ring-slate-400"
            >
              <option value="">Select Party Type</option>
              <option value="Customer">Customer</option>
              <option value="Dealer">Dealer</option>
              <option value="Wholesaler">Wholesaler</option>
            </select>
          </div>

          <div>
            <label 
            htmlFor='balance'
            className="text-sm text-gray-600">Balance</label>
            <input
              name="balance"
              value={customer.balance}
              onChange={handleChange}
              className="w-full border p-2.5 rounded-lg"
            />
          </div>

          <div>
            <label 
            htmlFor='balanceType'
            className="text-sm text-gray-600">Balance Type</label>
            <select
              name="balanceType"
              value={customer.balanceType}
              onChange={handleChange}
              className="w-full border p-2.5 rounded-lg"
            >
              <option value="Due">Due</option>
              <option value="Advance">Advance</option>
            </select>
          </div>

          <div>
            <label 
            htmlFor='creditLimit'
            className="text-sm text-gray-600">Credit Limit</label>
            <input
              name="creditLimit"
              value={customer.creditLimit}
              onChange={handleChange}
              className="w-full border p-2.5 rounded-lg"
            />
          </div>

        </div>

        <div className="border rounded-xl p-4 bg-gray-50">
          <h3 className="font-semibold mb-3">Billing Address</h3>

          <div className="grid md:grid-cols-2 gap-4">
            {['addressLine', 'city', 'state', 'zip', 'country'].map((field) => (
              <input
                key={field}
                value={customer.billingAddress[field]}
                onChange={(e) =>
                  handleAddressChange('billingAddress', field, e.target.value)
                }
                placeholder={field}
                className="border p-2.5 rounded-lg focus:ring-2 focus:ring-slate-400"
              />
            ))}
          </div>
        </div>

        <div className="flex items-center gap-2">
          <input
            type="checkbox"
            id="sameAsBilling"
            checked={sameAsBilling}
            onChange={(e) => handleSameAsBilling(e.target.checked)}
          />

          <label
            htmlFor="sameAsBilling"
            className="text-sm font-medium text-gray-700"
          >
            Shipping Address same as Billing Address
          </label>
        </div>

        <div className="border rounded-xl p-4 bg-gray-50">
          <h3 className="font-semibold mb-3">Shipping Address</h3>

          <div className="grid md:grid-cols-2 gap-4">
            {['addressLine', 'city', 'state', 'zip', 'country'].map((field) => (
              <input
                key={field}
                value={customer.shippingAddress[field]}
                onChange={(e) =>
                  handleAddressChange(
                    "shippingAddress",
                    field,
                    e.target.value
                  )
                }
                placeholder={field}
                disabled={sameAsBilling}
                className={`border p-2.5 rounded-lg focus:ring-2 focus:ring-slate-400 ${
                  sameAsBilling
                    ? "bg-gray-200 cursor-not-allowed"
                    : ""
                }`}
              />
            ))}
          </div>
        </div>

      </form>

      <div className="p-4 border-t flex justify-end gap-3">
        <button
          type="button"
          onClick={closeModal}
          className="px-5 py-2 rounded-lg border hover:bg-gray-100"
        >
          Cancel
        </button>

        <button
          onClick={handleSubmit}
          className="px-5 py-2 rounded-lg bg-slate-800 text-white hover:bg-slate-700"
        >
          Save Customer
        </button>
      </div>

    </div>
  </div>
);
};

AddCustomer.propTypes = {
  closeModal: PropTypes.func.isRequired,
  refreshData: PropTypes.func.isRequired
};

export default AddCustomer;