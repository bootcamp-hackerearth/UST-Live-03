'use client';

import { useEffect, useState } from 'react';
import PropTypes from 'prop-types';

import CommonAdd from '@/components/AddPage';
import Dropdown from '@/components/Dropdown';
import api from '@/app/services/api';

const AddProduct = ({ closeModal, refreshData }) => {
  const [message, setMessage] = useState('');
  const [errors, setErrors] = useState({});

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

  const fetchList = async (url, setState, transform = (data) => data, method = 'post') => {
    try {
      const response =
        method === 'get'
          ? await api.get(url)
          : await api.post(url, {
            page: 0,
            sizePerPage: 100
          });

      const data = transform(response.data);
      setState(data);
    } catch (error) {
      console.error(error);
    }
  };
  
  useEffect(() => {
    fetchList('/unit/list', setUnits, (data) =>
      data.dtoList.filter(unit => unit.status)
    );

    fetchList('/brand/list', setBrands, (data) =>
      data.dtoList.filter(brand => brand.status)
    );

    fetchList('/category/subcategories', setCategories, (data) => data, 'get');
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setProduct(prev => ({
      ...prev,
      [name]: value
    }));

    setErrors(prev => ({
      ...prev,
      [name]: ''
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if(!validate()) return;
    try {
      const response = await api.post('/product/add', product);
      const data = response.data;

      setMessage(data.message);

      if (!data.success) return;

      refreshData?.();
      closeModal?.();

    } catch (error) {
      console.error(error);
      setMessage('Failed to add product');
    }
  };

  const validate = () => {
    const newErrors = {};

    if (!product.identifier?.trim()) {
      newErrors.identifier = 'Identifier is required';
    }

    if (!product.name?.trim()) {
      newErrors.name = 'Name is required';
    }

    if (!product.unit) {
      newErrors.unit = 'Unit is required';
    }

    if (!product.brand) {
      newErrors.brand = 'Brand is required';
    }

    if (!product.category) {
      newErrors.category = 'Category is required';
    }

    if (!product.description?.trim()) {
      newErrors.description = 'Description is required';
    } else if (product.description.length < 10) {
      newErrors.description = 'Description must be at least 10 characters';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  return (

    <div className="flex flex-col bg-white rounded-2xl overflow-hidden max-h-[85vh] border-t-4 border-cyan-500 shadow-lg">

      <div className="px-8 pt-6 pb-5 border-b border-slate-200 flex-shrink-0">
        <h2 className="text-2xl font-bold text-slate-800">
          Add Product
        </h2>

        <p className="text-sm text-slate-500 mt-1">
          Create a new product and assign its details.
        </p>
      </div>

      <form
        id="add-product-form"
        onSubmit={handleSubmit}
        className="flex-1 overflow-y-auto px-8 py-6 space-y-6"
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

        <CommonAdd
          data={product}
          handleChange={handleChange}
          errors={errors}
        />

        <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
          <div>
          <Dropdown
            label="Unit"
            name="unit"
            value={product.unit}
            options={units}
            onChange={handleChange}
            placeholder="Select Unit"
          />
          {errors.unit && <p className="text-red-500 text-sm mt-1">{errors.unit}</p>}
          </div>

          <div>
          <Dropdown
            label="Brand"
            name="brand"
            value={product.brand}
            options={brands}
            onChange={handleChange}
            placeholder="Select Brand"
          />
            {errors.brand && <p className="text-red-500 text-sm mt-1">{errors.brand}</p>}
          </div>

          <div>
          <Dropdown
            label="Category"
            name="category"
            value={product.category}
            options={categories}
            onChange={handleChange}
            placeholder="Select Category"
          />
            {errors.category && <p className="text-red-500 text-sm mt-1">{errors.category}</p>}
          </div>

        </div>

        <div>
          <label
            htmlFor="description"
            className="block mb-2 text-sm font-semibold text-slate-700">
            Description
          </label>

          <textarea
            id="description"
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
            focus:ring-cyan-500
            focus:border-cyan-500
          "
          />
          {errors.description && (
            <p className="text-red-500 text-sm mt-1">
              {errors.description}
            </p>
          )}
        </div>

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
          Save Product
        </button>

      </div>
      </form>
    </div>
  );
};

AddProduct.propTypes = {
  closeModal: PropTypes.func,
  refreshData: PropTypes.func
};

export default AddProduct;