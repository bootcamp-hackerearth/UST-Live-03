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
  const [errors, setErrors] = useState({});
 
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
    }
  };
 
  const handleChange = (e) => {
    const { name, value } = e.target;
    setPrice((prev) => ({
      ...prev,
      [name]: value
    }));
    setErrors((prev) => ({
      ...prev,
      [name]: ''
    }));
  };
 
  const handleSubmit = async (e) => {
    e.preventDefault();
    if(!validate()) return;
    try {
      const response = await api.post(
        '/price/add',
        price
      );
      setMessage(
        response.data.message ||
        'Price added successfully'
      );
      if (!response.data.success) {
        return;
      }
      refreshData?.();
      closeModal?.();
    } catch (error) {
      console.error(error);
      setMessage(
        'Failed to add price'
      );
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

  const validate = () => {
    const newErrors = {};
    if (!price.product) {
      newErrors.product = 'Product is required';
    }

    if (!price.type) {
      newErrors.type = 'Price type is required';
    }

    if (!price.amount) {
      newErrors.amount = 'Amount is required';
    } 
    else if (Number(price.amount) <= 0) {
      newErrors.amount = 'Amount must be greater than 0';
    }

    if (!price.currency.trim()) {
      newErrors.currency = 'Currency is required';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };
 
  return (
 
    <div className="flex flex-col bg-white rounded-2xl overflow-hidden max-h-[85vh] border-t-4 border-cyan-500 shadow-lg">
      <div className="px-8 pt-6 pb-5 border-b border-slate-200 flex-shrink-0">
 
      <h2 className="text-2xl font-semibold text-slate-800">
        Add Price
      </h2>
 
      <p className="text-sm text-slate-500">
        Create a new price and assign it to a product.
      </p>
 
    </div>
    {message && (
      <div className="mx-8 mt-5 rounded-xl border border-cyan-200 bg-cyan-50 px-4 py-3 text-sm text-cyan-700">
        {message}
      </div>
    )}
 
    <form
      onSubmit={handleSubmit}
      className="p-8"
    >
      <div className="grid md:grid-cols-2 gap-5">
       <div>
        <Dropdown
          label="Product"
          name="product"
          value={price.product}
          options={products}
          onChange={handleChange}
          placeholder="Select Product"
        />
          {errors.product && (
            <p className="text-red-500 text-sm mt-1">
              {errors.product}
            </p>
          )}
       </div>
       <div>
        <Dropdown
          label="Price Type"
          name="type"
          value={price.type}
          options={priceTypes}
          onChange={handleChange}
          placeholder="Select Price Type"
        />
          {errors.type && (
            <p className="text-red-500 text-sm mt-1">
              {errors.type}
            </p>
          )}
          </div>

        <div>
          <label 
            htmlFor="amount"
            className="block mb-2 text-sm font-medium text-slate-700">
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
            {errors.amount && (
              <p className="text-red-500 text-sm mb-1">
                {errors.amount}
              </p>
            )}
        </div>
 
        <div>
          <label 
            htmlFor="currency"
            className="block mb-2 text-sm font-medium text-slate-700">
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
            px-4
            h-12
            border
            border-slate-300
            rounded-xl
            bg-white
            focus:outline-none
            focus:ring-2
            focus:ring-cyan-500
            transition
            "
          />
            {errors.currency && (
              <p className="text-red-500 text-sm mt-1">
                {errors.currency}
              </p>
            )}
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
          bg-cyan-600
          text-white
          hover:bg-cyan-700
          transition\
          shadow-md
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