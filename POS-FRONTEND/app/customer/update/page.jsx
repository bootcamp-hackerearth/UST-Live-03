'use client';

import { useEffect, useState } from 'react';
import api from '@/app/services/api';
import PropTypes from 'prop-types';

const UpdateCustomer = ({ data, closeModal, refreshData }) => {

  const [customer, setCustomer] = useState(data);

  useEffect(() => {

  const fetchCustomer = async () => {

    try {

      const response = await api.get(
        `/customer/get?identifier=${data.identifier}`
      );

      console.log('Customer Data:', response.data);

      setCustomer(response.data);

    } catch (err) {

      console.error(err);

    }
  };

  if (data?.identifier) {
    fetchCustomer();
  }

}, [data]);

  const handleChange = (e) => {
    const { name, value } = e.target;

    setCustomer({
      ...customer,
      [name]: value
    });
  };

  const handleAddressChange = (type, field, value) => {
    setCustomer({
      ...customer,
      [type]: {
        ...customer[type],
        [field]: value
      }
    });
  };

  const validateForm = () => {

  const validations = [
    {
      valid: customer.name?.trim(),
      message: 'Customer Name is required'
    },
    {
      valid: customer.email?.trim(),
      message: 'Email is required'
    },
    {
      valid: customer.partyType?.trim(),
      message: 'Party Type is required'
    },
    {
      valid: customer.balance !== '' && customer.balance !== null,
      message: 'Balance is required'
    },
    {
      valid: customer.balanceType?.trim(),
      message: 'Balance Type is required'
    },
    {
      valid: customer.creditLimit !== '' && customer.creditLimit !== null,
      message: 'Credit Limit is required'
    },
    {
      valid: customer.billingAddress?.addressLine?.trim(),
      message: 'Billing Address Line is required'
    },
    {
      valid: customer.billingAddress?.city?.trim(),
      message: 'Billing City is required'
    },
    {
      valid: customer.billingAddress?.state?.trim(),
      message: 'Billing State is required'
    },
    {
      valid: customer.billingAddress?.zip?.trim(),
      message: 'Billing ZIP is required'
    },
    {
      valid: customer.billingAddress?.country?.trim(),
      message: 'Billing Country is required'
    },
    {
      valid: customer.shippingAddress?.addressLine?.trim(),
      message: 'Shipping Address Line is required'
    },
    {
      valid: customer.shippingAddress?.city?.trim(),
      message: 'Shipping City is required'
    },
    {
      valid: customer.shippingAddress?.state?.trim(),
      message: 'Shipping State is required'
    },
    {
      valid: customer.shippingAddress?.zip?.trim(),
      message: 'Shipping ZIP is required'
    },
    {
      valid: customer.shippingAddress?.country?.trim(),
      message: 'Shipping Country is required'
    }
  ];

  const error = validations.find(({ valid }) => !valid);

  if (error) {
    alert(error.message);
    return false;
  }

  return true;
};

  const handleSubmit = async (e) => {

  e.preventDefault();

  if (!validateForm()) {
    return;
  }

  try {

    await api.put('/customer/update', customer);

      alert('Customer Updated');
      refreshData();
      closeModal();

    } catch (err) {
      console.error(err);
      alert('Update Failed');
    }
  };

  if (!customer) return null;

  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">

      <div className="bg-white w-full max-w-4xl rounded-xl shadow-lg max-h-[90vh] flex flex-col">

        <div className="p-5 border-b">
          <h2 className="text-xl font-semibold">Update Customer</h2>
        </div>

        <form
          onSubmit={handleSubmit}
          className="p-6 overflow-y-auto space-y-6"
        >

          <div className="grid md:grid-cols-2 gap-4">

            <div>
              <label 
              htmlFor='identifier'
              className="text-sm text-gray-600">Customer ID</label>
              <input
                value={customer.identifier || ''}
                readOnly
                className="w-full border p-2.5 rounded-lg bg-gray-100"
              />
            </div>

            <div>
              <label 
              htmlFor='phoneNo'
              className="text-sm text-gray-600">Phone Number</label>
              <input
                value={customer.phoneNo || ''}
                readOnly
                className="w-full border p-2.5 rounded-lg bg-gray-100"
              />
            </div>

            <div>
              <label 
              htmlFor='name'
              className="text-sm text-gray-600">Customer Name</label>
              <input
                name="name"
                required
                value={customer.name || ''}
                onChange={handleChange}
                className="w-full border p-2.5 rounded-lg focus:ring-2 focus:ring-slate-400"
              />
            </div>

            <div>
              <label 
              htmlFor='email'
              className="text-sm text-gray-600">Email</label>
              <input
                name="email"
                type="email"
                required
                value={customer.email || ''}
                onChange={handleChange}
                className="w-full border p-2.5 rounded-lg"
              />
            </div>

            <div>
              <label 
              htmlFor='partyType'
              className="text-sm text-gray-600">Party Type</label>
              <select
                name="partyType"
                required
                value={customer.partyType || ''}
                onChange={handleChange}
                className="w-full border p-2.5 rounded-lg"
              >
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
                type="number"
                required
                value={customer.balance || ''}
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
                required
                value={customer.balanceType || ''}
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
                type="number"
                required
                value={customer.creditLimit || ''}
                onChange={handleChange}
                className="w-full border p-2.5 rounded-lg"
              />
            </div>

            <div>
              <label 
              htmlFor='status'
              className="text-sm text-gray-600">Status</label>
              <select
                name="status"
                required
                value={customer.status}
                onChange={(e) =>
                  setCustomer({
                    ...customer,
                    status: e.target.value === 'true'
                  })
                }
                className="w-full border p-2.5 rounded-lg"
              >
                <option value="true">Active</option>
                <option value="false">Inactive</option>
              </select>
            </div>

          </div>

          <div className="border rounded-xl p-4 bg-gray-50">
            <h3 className="font-semibold mb-3">Billing Address</h3>

            <div className="grid md:grid-cols-2 gap-4">
              {['addressLine', 'city', 'state', 'zip', 'country'].map((field) => (
                <input
                  key={field}
                  required
                  value={customer.billingAddress?.[field] || ''}
                  onChange={(e) =>
                    handleAddressChange('billingAddress', field, e.target.value)
                  }
                  placeholder={field}
                  className="border p-2.5 rounded-lg"
                />
              ))}
            </div>
          </div>

          <div className="border rounded-xl p-4 bg-gray-50">
            <h3 className="font-semibold mb-3">Shipping Address</h3>

            <div className="grid md:grid-cols-2 gap-4">
              {['addressLine', 'city', 'state', 'zip', 'country'].map((field) => (
                <input
                  key={field}
                  required
                  value={customer.shippingAddress?.[field] || ''}
                  onChange={(e) =>
                    handleAddressChange('shippingAddress', field, e.target.value)
                  }
                  placeholder={field}
                  className="border p-2.5 rounded-lg"
                />
              ))}
            </div>
          </div>

        </form>

        <div className="p-4 border-t flex justify-end gap-3">
          <button
            onClick={closeModal}
            className="px-5 py-2 rounded-lg border hover:bg-gray-100"
          >
            Cancel
          </button>

          <button
            onClick={handleSubmit}
            className="px-5 py-2 rounded-lg bg-blue-600 text-white hover:bg-blue-700"
          >
            Update Customer
          </button>
        </div>

      </div>
    </div>
  );
};

UpdateCustomer.propTypes = {
  data: PropTypes.shape({
    identifier: PropTypes.oneOfType([
      PropTypes.string,
      PropTypes.number
    ])
  }).isRequired,
  closeModal: PropTypes.func.isRequired,
  refreshData: PropTypes.func.isRequired
};

export default UpdateCustomer;