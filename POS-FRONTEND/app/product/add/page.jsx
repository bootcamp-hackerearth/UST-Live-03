'use client';

import { useEffect, useState } from 'react';
import PropTypes from 'prop-types';
import CommonAdd from '@/components/AddPage';
import Dropdown from '@/components/Dropdown';
import api from '@/app/services/api';

const AddProduct = ({ closeModal, refreshData }) => {

  const [message, setMessage] = useState('');
  const [messageType, setMessageType] = useState('');

  const [product, setProduct] = useState({
    identifier: '',
    name: '',
    unit: '',
    brand: '',
    category: '',
    description: ''
  });

  const [units, setUnits] = useState([]);
  const [brands, setBrands] = useState([]);
  const [categories, setCategories] = useState([]);

  useEffect(() => {
    fetchUnits();
    fetchBrands();
    fetchCategories();
  }, []);

  const fetchUnits = async () => {
    try {
      const response = await api.post('/unit/list', {
        page: 0,
        sizePerPage: 100
      });

      setUnits(
        response.data.dtoList.filter(
          (unit) => unit.status
        )
      );
    } catch (error) {
      console.error(error);
    }
  };

  const fetchBrands = async () => {
    try {
      const response = await api.post('/brand/list', {
        page: 0,
        sizePerPage: 100
      });

      setBrands(
        response.data.dtoList.filter(
          (brand) => brand.status
        )
      );
    } catch (error) {
      console.error(error);
    }
  };

  const fetchCategories = async () => {
    try {
      const response = await api.get(
        '/category/subcategories',
        {
          page: 0,
          sizePerPage: 100
        }
      );

      setCategories(response.data);
    } catch (error) {
      console.error(error);
    }
  };

  const handleChange = (e) => {

    const { name, value } = e.target;

    setProduct((prev) => ({
      ...prev,
      [name]: value
    }));

  };

  const validate = () => {

    if (!product.identifier.trim()) {
      setMessage('Identifier is required');
      setMessageType('error');
      return false;
    }

    if (!product.name.trim()) {
      setMessage('Product name is required');
      setMessageType('error');
      return false;
    }

    if (!product.unit) {
      setMessage('Please select a unit');
      setMessageType('error');
      return false;
    }

    if (!product.brand) {
      setMessage('Please select a brand');
      setMessageType('error');
      return false;
    }

    if (!product.category) {
      setMessage('Please select a category');
      setMessageType('error');
      return false;
    }

    if (!product.description.trim()) {
      setMessage('Description is required');
      setMessageType('error');
      return false;
    }

    return true;

  };

  const handleSubmit = async (e) => {

    e.preventDefault();

    setMessage('');
    setMessageType('');

    if (!validate()) return;

    try {

      const response = await api.post(
        '/product/add',
        product
      );

      const data = response.data;

      setMessage(
        data.message || 'Product added successfully'
      );

      setMessageType(
        data.success ? 'success' : 'error'
      );

      if (!data.success) return;

      refreshData?.();
      closeModal?.();

    } catch (error) {

      console.error(error);

      setMessage('Failed to add product');
      setMessageType('error');

    }

  };

  return (

    <div className="flex flex-col bg-white rounded-2xl overflow-hidden max-h-[85vh]">

      <div className="px-8 pt-6 pb-5 border-b border-slate-200 flex-shrink-0">

        <h2 className="text-2xl font-bold text-slate-800">
          Add Product
        </h2>

        <p className="text-sm text-slate-500 mt-1">
          Create a new product and assign its details.
        </p>

      </div>

      <form
        onSubmit={handleSubmit}
        className="flex-1 overflow-y-auto px-8 py-6 space-y-6"
      >

        {message && (
          <div
            className={`
              rounded-xl px-4 py-3 text-sm border
              ${
                messageType === 'error'
                  ? 'bg-red-50 border-red-200 text-red-600'
                  : 'bg-green-50 border-green-200 text-green-600'
              }
            `}
          >
            {message}
          </div>
        )}

        <CommonAdd
          data={product}
          handleChange={handleChange}
        />

        <div className="grid grid-cols-1 md:grid-cols-3 gap-5">

          <Dropdown
            label="Unit"
            name="unit"
            value={product.unit}
            options={units}
            onChange={handleChange}
            placeholder="Select Unit"
          />

          <Dropdown
            label="Brand"
            name="brand"
            value={product.brand}
            options={brands}
            onChange={handleChange}
            placeholder="Select Brand"
          />

          <Dropdown
            label="Category"
            name="category"
            value={product.category}
            options={categories}
            onChange={handleChange}
            placeholder="Select Category"
          />

        </div>

        <div>

          <label 
          htmlFor="description"
          className="block mb-2 text-sm font-semibold text-slate-700">
            Description
          </label>

          <textarea
            name="description"
            value={product.description}
            onChange={handleChange}
            rows={4}
            placeholder="Enter product description"
            className="
              w-full
              rounded-xl
              border border-slate-300
              px-4 py-3
              text-sm
              resize-none
              focus:outline-none
              focus:ring-2
              focus:ring-indigo-500
              focus:border-indigo-500
            "
          />

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
            hover:bg-slate-100
            transition
          "
        >
          Cancel
        </button>

        <button
          type="submit"
          onClick={handleSubmit}
          className="
            px-6 py-2.5
            rounded-xl
            bg-indigo-600
            text-white
            hover:bg-indigo-700
            transition
            shadow-md
          "
        >
          Save Product
        </button>

      </div>

    </div>

  );

};

AddProduct.propTypes = {
  closeModal: PropTypes.func,
  refreshData: PropTypes.func
};

export default AddProduct;