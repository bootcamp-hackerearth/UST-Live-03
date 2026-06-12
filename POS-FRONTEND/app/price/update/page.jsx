'use client';

import { useEffect, useState } from 'react';
import PropTypes from 'prop-types';
import api from '@/app/services/api';
import CommonUpdate from '@/components/UpadatePage';

const UpdatePrice = ({
  data,
  closeModal,
  refreshData
}) => {

  const [message, setMessage] = useState('');
  const [messageType, setMessageType] = useState('');

  const [price, setPrice] = useState({
    identifier: '',
    product: '',
    type: '',
    amount: '',
    currency: ''
  });

  const [products, setProducts] = useState([]);

  useEffect(() => {
    fetchProducts();
  }, []);

  useEffect(() => {

    if (data) {

      setPrice({
        identifier: data.identifier || '',
        product: data.product || '',
        type: data.type || '',
        amount: data.amount || '',
        currency: data.currency || ''
      });

    }

  }, [data]);

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
      setMessageType('error');
      setMessage('Please select a product');
      return false;
    }

    if (!price.type) {
      setMessageType('error');
      setMessage('Price type is required');
      return false;
    }

    if (
      price.amount === '' ||
      Number(price.amount) <= 0
    ) {
      setMessageType('error');
      setMessage('Enter a valid amount greater than 0');
      return false;
    }

    if (!price.currency.trim()) {
      setMessageType('error');
      setMessage('Currency is required');
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

      await api.post(
        '/price/update',
        price,
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`,
            'Content-Type': 'application/json'
          }
        }
      );

      setMessageType('success');
      setMessage('Price updated successfully');

      refreshData?.();

      setTimeout(() => {
        closeModal?.();
      }, 500);

    } catch (error) {

      console.error(error);

      setMessageType('error');
      setMessage('Failed to update price');

    }

  };

  return (

    <div>

      <div className="mb-8">

        <h2 className="text-2xl font-semibold text-slate-800">
          Update Price
        </h2>

        <p className="text-sm text-slate-500 mt-1">
          Update product pricing information
        </p>

      </div>

      {message && (

        <div
          className={`mb-5 p-3 rounded-lg text-sm border ${
            messageType === 'error'
              ? 'bg-red-50 border-red-200 text-red-600'
              : 'bg-green-50 border-green-200 text-green-600'
          }`}
        >
          {message}
        </div>

      )}

      <form
        onSubmit={handleSubmit}
        className="space-y-5"
      >

        <CommonUpdate
          data={price}
          handleChange={handleChange}
          showIdentifier={true}
          showName={false}
          identifierReadOnly={true}
        />

        <div className="grid grid-cols-2 gap-4">

          <select
            name="product"
            value={price.product}
            onChange={handleChange}
            className="w-full px-4 py-3 border rounded-xl"
          >
            <option value="">
              Select Product
            </option>

            {products.map((product) => (

            <option
              key={product.identifier}
              value={product.identifier}
            >
              {product.name}
            </option>

          ))}

          </select>

          <select
            name="type"
            value={price.type}
            disabled
            className="w-full px-4 py-3 border rounded-xl bg-slate-100 cursor-not-allowed"
          >
            <option value="COST_PRICE">
              Cost Price (CP)
            </option>

            <option value="SELLING_PRICE">
              Selling Price (SP)
            </option>

            <option value="MRP">
              MRP
            </option>
          </select>

          <input
            type="number"
            min="0.01"
            step="0.01"
            name="amount"
            value={price.amount}
            onChange={handleChange}
            placeholder="Amount"
            className="w-full px-4 py-3 border rounded-xl"
          />

          <input
            type="text"
            name="currency"
            value={price.currency}
            onChange={handleChange}
            placeholder="Currency"
            className="w-full px-4 py-3 border rounded-xl"
          />

        </div>

        <div className="flex justify-end gap-3 pt-4">

          <button
            type="button"
            onClick={closeModal}
            className="px-6 py-2.5 rounded-xl bg-slate-100 hover:bg-slate-200"
          >
            Cancel
          </button>

          <button
            type="submit"
            className="px-6 py-2.5 rounded-xl bg-indigo-600 text-white hover:bg-indigo-700"
          >
            Update
          </button>

        </div>

      </form>

    </div>

  );

};

UpdatePrice.propTypes = {
  data: PropTypes.shape({
    identifier: PropTypes.string,
    product: PropTypes.string,
    type: PropTypes.string,
    amount: PropTypes.oneOfType([
      PropTypes.string,
      PropTypes.number
    ]),
    currency: PropTypes.string
  }),
  closeModal: PropTypes.func,
  refreshData: PropTypes.func
};

export default UpdatePrice;