'use client';
 
import { useEffect, useState } from 'react';
import PropTypes from 'prop-types';
 
import api from '@/app/services/api';
import CommonUpdate from '@/components/UpdatePage';
import Dropdown from '@/components/Dropdown';

 
const UpdatePrice = ({
  data,
  closeModal,
  refreshData
}) => {
 
  const [message, setMessage] = useState('');
 
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
 
  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await api.put(
        '/price/update',
        price,
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`,
            'Content-Type': 'application/json'
          }
        }
      );

      setMessage('Price updated successfully');

      refreshData?.();
      setTimeout(() => {
        closeModal?.();
      }, 500);

    } catch (error) {
      console.error(error);
      setMessage('Failed to update price');
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

    <div className="flex flex-col bg-white rounded-2xl overflow-hidden max-h-[85vh] border-t-4 border-cyan-500 shadow-lg">

      <div className="px-8 pt-6 pb-5 border-b border-slate-200 flex-shrink-0">
        <h2 className="text-2xl font-bold text-slate-800">
          Update Price
        </h2>

        <p className="text-sm text-slate-500 mt-1">
          Modify product pricing information and save changes.
        </p>
      </div>

      <form
        id="update-price-form"
        onSubmit={handleSubmit}
        className="flex-1 overflow-y-auto px-8 py-6 space-y-8"
      >
        {message && (
          <div
            className={`
            rounded-xl px-4 py-3 text-sm
            ${message.toLowerCase().includes('failed')
                ? 'bg-red-50 border border-red-200 text-red-600'
                : 'bg-cyan-50 border border-cyan-200 text-cyan-600'
              }
          `}
          >
            {message}
          </div>
        )}

        <CommonUpdate
          data={price}
          handleChange={handleChange}
          showIdentifier={true}
          showName={false}
          identifierReadOnly={true}
        />

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
              className="block mb-2 text-sm font-medium text-slate-700"
            >
              Amount
            </label>

            <input
              id="amount"
              type="number"
              name="amount"
              value={price.amount}
              onChange={handleChange}
              placeholder="Enter amount"
              className="
              w-full
              h-12
              px-4
              border
              border-slate-300
              rounded-xl
              focus:outline-none
              focus:ring-2
              focus:ring-cyan-500
              transition
            "
            />
          </div>

          <div>
            <label 
              htmlFor="currency"
              className="block mb-2 text-sm font-medium text-slate-700"
            >
              Currency
            </label>

            <input
              id="currency"
              type="text"
              name="currency"
              value={price.currency}
              onChange={handleChange}
              placeholder="e.g. INR, USD"
              className="
              w-full
              h-12
              px-4
              border
              border-slate-300
              rounded-xl
              focus:outline-none
              focus:ring-2
              focus:ring-cyan-500
              transition
            "
            />
          </div>

        </div>

      </form>

      <div className="px-8 py-5 border-t border-slate-200 flex justify-end gap-3 flex-shrink-0">

        <button
          type="button"
          onClick={closeModal}
          className="
          px-6 py-2.5
          rounded-xl
          border border-slate-300
          text-slate-700
          font-medium
          hover:bg-slate-100
          transition
        "
        >
          Cancel
        </button>

        <button
          type="submit"
          form="update-price-form"
          className="
          px-6 py-2.5
          rounded-xl
          bg-cyan-500
          text-white
          hover:bg-cyan-600
          transition
          shadow-md
        "
        >
          Update Price
        </button>

      </div>

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