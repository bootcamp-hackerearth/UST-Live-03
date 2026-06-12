'use client';

import { useEffect, useState } from 'react';
import PropTypes from 'prop-types';
import api from '@/app/services/api';
import CommonUpdate from '@/components/UpadatePage';

const UpdateProduct = ({
  data,
  closeModal,
  refreshData
}) => {

  const [message, setMessage] = useState('');
  const [isError, setIsError] = useState(false);

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

  useEffect(() => {

    if (data) {

      setProduct({
        identifier: data.identifier || '',
        name: data.name || '',
        unit: data.unit || '',
        brand: data.brand || '',
        category: data.category || '',
        description: data.description || ''
      });

    }

  }, [data]);

  const fetchUnits = async () => {

    try {

      const res = await api.post(
        '/unit/list',
        {
          page: 0,
          sizePerPage: 100
        }
      );

      setUnits(
        (res.data.dtoList || res.data || []).filter(
          (unit) => unit.status
        )
      );

    } catch (err) {

      console.error(err);

    }
  };

  const fetchBrands = async () => {

    try {

      const res = await api.post(
        '/brand/list',
        {
          page: 0,
          sizePerPage: 100
        }
      );

      setBrands(
        (res.data.dtoList || res.data || []).filter(
          (brand) => brand.status
        )
      );

    } catch (err) {

      console.error(err);

    }
  };

  const fetchCategories = async () => {

    try {

      const res = await api.post(
        '/category/list',
        {
          page: 0,
          sizePerPage: 100
        }
      );

      setCategories(
        res.data.dtoList || res.data || []
      );

    } catch (err) {

      console.error(err);

    }
  };

  const handleChange = (e) => {

  const { name, value } = e.target;

  setProduct((prev) => ({
    ...prev,
    [name]: value
  }));

  if (message) {

    setMessage('');
    setIsError(false);

  }

};

  const handleSubmit = async (e) => {

    e.preventDefault();

    if (!product.identifier?.trim()) {

  setMessage('Identifier is required');
  setIsError(true);
  return;

}

if (!product.name?.trim()) {

  setMessage('Product name is required');
  setIsError(true);
  return;

}

if (!product.unit) {

  setMessage('Please select a unit');
  setIsError(true);
  return;

}

if (!product.brand) {

  setMessage('Please select a brand');
  setIsError(true);
  return;

}

if (!product.category) {

  setMessage('Please select a category');
  setIsError(true);
  return;

}

if (!product.description?.trim()) {

  setMessage('Description is required');
  setIsError(true);
  return;

}

    try {

      await api.post(
        '/product/update',
        product,
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`,
            'Content-Type': 'application/json'
          }
        }
      );

      setMessage('Product updated successfully');
      setIsError(false);

      if (refreshData) {
        refreshData();
      }

      setTimeout(() => {

        if (closeModal) {
          closeModal();
        }

      }, 500);

    } catch (error) {

      console.error(error);

      setMessage('Failed to update product');
      setIsError(true);

    }
  };

  return (
    <div className="flex flex-col bg-white rounded-2xl overflow-hidden max-h-[85vh]">

      <div className="px-8 pt-6 pb-5 border-b border-slate-200 flex-shrink-0">
        <h2 className="text-2xl font-bold text-slate-800">
          Update Product
        </h2>

        <p className="text-sm text-slate-500 mt-1">
          Modify product details and save changes.
        </p>
      </div>

      <form
        id="update-product-form"
        onSubmit={handleSubmit}
        noValidate
        className="flex-1 overflow-y-auto px-8 py-6 space-y-6"
      >

        {message && (
          <div
            className={`
              rounded-xl px-4 py-3 text-sm border
              ${
                isError
                  ? 'bg-red-50 border-red-200 text-red-600'
                  : 'bg-green-50 border-green-200 text-green-600'
              }
            `}
          >
            {message}
          </div>
        )}

        <CommonUpdate
          data={product}
          handleChange={handleChange}
          showIdentifier={true}
          showName={true}
          identifierReadOnly={true}
        />

        <div className="grid grid-cols-1 md:grid-cols-3 gap-5">

          <div>
            <label 
            htmlFor="unit"
            className="block mb-2 text-sm font-semibold text-slate-700">
              Unit
            </label>

            <select
              name="unit"
              value={product.unit}
              onChange={handleChange}
              className="
                w-full
                h-12
                px-4
                rounded-xl
                border border-slate-300
                focus:outline-none
                focus:ring-2
                focus:ring-indigo-500
              "
            >
              <option value="">
                Select Unit
              </option>

              {units.map((u) => (
              <option
                key={u.identifier}
                value={u.identifier}
              >
                {u.name}
              </option>
            ))}
            </select>
          </div>

          <div>
            <label 
            htmlFor="brand"
            className="block mb-2 text-sm font-semibold text-slate-700">
              Brand
            </label>

            <select
              name="brand"
              value={product.brand}
              onChange={handleChange}
              className="
                w-full
                h-12
                px-4
                rounded-xl
                border border-slate-300
                focus:outline-none
                focus:ring-2
                focus:ring-indigo-500
              "
            >
              <option value="">
                Select Brand
              </option>

              {brands.map((b) => (
              <option
                key={b.identifier}
                value={b.identifier}
              >
                {b.name}
              </option>
            ))}
            </select>
          </div>

          <div>
            <label 
            htmlFor="category"
            className="block mb-2 text-sm font-semibold text-slate-700">
              Category
            </label>

            <select
              name="category"
              value={product.category}
              onChange={handleChange}
              className="
                w-full
                h-12
                px-4
                rounded-xl
                border border-slate-300
                focus:outline-none
                focus:ring-2
                focus:ring-indigo-500
              "
            >
              <option value="">
                Select Category
              </option>

              {categories.map((c) => (
              <option
                key={c.identifier}
                value={c.identifier}
              >
                {c.name}
              </option>
            ))}
            </select>
          </div>

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
          form="update-product-form"
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
          Update Product
        </button>

      </div>

    </div>
  );
};

UpdateProduct.propTypes = {
  data: PropTypes.shape({
    identifier: PropTypes.string,
    name: PropTypes.string,
    unit: PropTypes.string,
    brand: PropTypes.string,
    category: PropTypes.string,
    description: PropTypes.string
  }),
  closeModal: PropTypes.func,
  refreshData: PropTypes.func
};

export default UpdateProduct;