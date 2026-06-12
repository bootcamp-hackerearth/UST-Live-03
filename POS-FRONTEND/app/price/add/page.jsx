'use client';

import { useEffect, useState } from 'react';
import PropTypes from 'prop-types';
import api from '@/app/services/api';
import Dropdown from '@/components/Dropdown';

const AddPrice = ({
  closeModal,
  refreshData
}) => {

  const [message, setMessage] = useState('');
  const [messageType, setMessageType] = useState('');

  const [price, setPrice] = useState({
    product: '',
    type: '',
    amount: '',
    currency: ''
  });

  const [products, setProducts] = useState([]);

  useEffect(() => {
    fetchProducts();
  }, []);

  const fetchProducts = async () => {

    try {

      const response = await api.post(
        '/product/list',
        {
          page: 0,
          sizePerPage: 100
        }
      );

      setProducts(
        response.data.dtoList || []
      );

    } catch (error) {

      console.error(error);

      setMessage('Failed to load products');
      setMessageType('error');

    }

  };

  const handleChange = (e) => {

    const { name, value } = e.target;

    setPrice((prev) => ({
      ...prev,
      [name]: value
    }));

  };

  const validate = () => {

    if (!price.product) {
      setMessage('Please select a product');
      setMessageType('error');
      return false;
    }

    if (!price.type) {
      setMessage('Please select a price type');
      setMessageType('error');
      return false;
    }

    if (!price.amount || Number(price.amount) <= 0) {
      setMessage('Please enter a valid amount');
      setMessageType('error');
      return false;
    }

    if (!price.currency.trim()) {
      setMessage('Please enter a currency');
      setMessageType('error');
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
        '/price/add',
        price
      );

      const data = response.data;

      setMessage(
        data.message || 'Price added successfully'
      );

      setMessageType(
        data.success ? 'success' : 'error'
      );

      if (!data.success) {
        return;
      }

      refreshData?.();

      setTimeout(() => {
        closeModal?.();
      }, 1000);

    } catch (error) {

      console.error(error);

      setMessage(
        'Failed to add price'
      );

      setMessageType('error');

    }

  };

  const priceTypes = [
    {
      identifier: 'COST_PRICE',
      name: 'Cost Price (CP)'
    },
    {
      identifier: 'SELLING_PRICE',
      name: 'Selling Price (SP)'
    },
    {
      identifier: 'MRP',
      name: 'MRP'
    }
  ];

  return (

    <div className="w-full bg-white">

      <div className="px-8 py-6 border-b border-slate-100">

        <h2 className="text-2xl font-semibold text-slate-800">
          Add Price
        </h2>

        <p className="text-sm text-slate-500 mt-1">
          Create a new price and assign it to a product.
        </p>

      </div>

      {message && (

        <div
          className={`mx-8 mt-5 rounded-lg border px-4 py-3 text-sm ${
            messageType === 'error'
              ? 'border-red-200 bg-red-50 text-red-700'
              : 'border-green-200 bg-green-50 text-green-700'
          }`}
        >
          {message}
        </div>

      )}

      <form
        onSubmit={handleSubmit}
        className="p-8"
      >

        <div className="grid md:grid-cols-2 gap-5">

          <Dropdown
            label="Product"
            name="product"
            value={price.product}
            options={products}
            onChange={handleChange}
            placeholder="Select Product"
          />

          <Dropdown
            label="Price Type"
            name="type"
            value={price.type}
            options={priceTypes}
            onChange={handleChange}
            placeholder="Select Price Type"
          />

          <div>

            <label 
            htmlFor="amount"
            className="block mb-2 text-sm font-medium text-slate-700">
              Amount
            </label>

            <input
              type="number"
              name="amount"
              value={price.amount}
              onChange={handleChange}
              placeholder="Enter amount"
              min="0"
              step="0.01"
              className="
              w-full
              px-4
              py-3
              border
              border-slate-300
              rounded-xl
              focus:outline-none
              focus:ring-2
              focus:ring-indigo-500
              "
            />

          </div>

          <div>

            <label 
            htmlFor="currency"
            className="block mb-2 text-sm font-medium text-slate-700">
              Currency
            </label>

            <input
              type="text"
              name="currency"
              value={price.currency}
              onChange={handleChange}
              placeholder="e.g. INR, USD"
              className="
              w-full
              px-4
              py-3
              border
              border-slate-300
              rounded-xl
              focus:outline-none
              focus:ring-2
              focus:ring-indigo-500
              "
            />

          </div>

        </div>

        <div className="flex justify-end gap-3 mt-8 pt-5 border-t border-slate-100">

          <button
            type="button"
            onClick={() => closeModal?.()}
            className="
            px-6
            py-2.5
            rounded-xl
            border
            border-slate-300
            text-slate-700
            hover:bg-slate-100
            transition
            "
          >
            Cancel
          </button>

          <button
            type="submit"
            className="
            px-6
            py-2.5
            rounded-xl
            bg-indigo-600
            text-white
            hover:bg-indigo-700
            transition
            "
          >
            Save Price
          </button>

        </div>

      </form>

    </div>

  );

};

AddPrice.propTypes = {
  closeModal: PropTypes.func,
  refreshData: PropTypes.func
};

export default AddPrice;