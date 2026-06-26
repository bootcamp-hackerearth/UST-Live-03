'use client';

import { useState } from 'react';
import api from '@/app/services/api';
import CommonAdd from '@/components/AddPage';
import PropTypes from 'prop-types';

const AddBrand = ({
  closeModal,
  refreshData
}) => {

  const [brand, setBrand] = useState({
    identifier: '',
    name: '',
    description: ''
  });

  const [message, setMessage] = useState('');
  const [messageType, setMessageType] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {

    const { name, value } = e.target;

    setBrand({
      ...brand,
      [name]: value
    });

  };

  const validateForm = () => {

    if (!brand.identifier.trim()) {

      setMessage('Identifier is required');
      setMessageType('error');
      return false;

    }

    if (!brand.name.trim()) {

      setMessage('Brand Name is required');
      setMessageType('error');
      return false;

    }

    if (!brand.description.trim()) {

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

    if (!validateForm()) {
      return;
    }

    setLoading(true);

    try {

      const response = await api.post(
        '/brand/add',
        brand
      );

      const data = response.data;

      setMessage(data.message);

      if (data.success) {

        setMessageType('success');

        refreshData?.();

        setTimeout(() => {
          closeModal?.();
        }, 1000);

      } else {

        setMessageType('error');

      }

    } catch (err) {

      console.error(err);

      setMessage('Failed to add brand');
      setMessageType('error');

    } finally {

      setLoading(false);

    }

  };

  return (

    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">

      <div className="bg-white w-full max-w-3xl rounded-xl shadow-lg">

        <div className="p-5 border-b">
          <h2 className="text-xl font-semibold">
            Add Brand
          </h2>
        </div>

        <form
          onSubmit={handleSubmit}
          className="p-6 space-y-5"
        >

          {message && (

            <div
              className={`px-3 py-2 rounded-lg border text-sm font-medium ${
                messageType === 'success'
                  ? 'bg-green-100 border-green-300 text-green-700'
                  : 'bg-red-100 border-red-300 text-red-700'
              }`}
            >
              {message}
            </div>

          )}

          <CommonAdd
            data={brand}
            handleChange={handleChange}
          />

          <div>

            <label
              htmlFor="description"
              className="block mb-2 text-sm font-semibold text-slate-700"
            >
              Description
            </label>

            <textarea
              name="description"
              value={brand.description}
              onChange={handleChange}
              rows={4}
              required
              className="w-full p-3 border border-slate-300 rounded-lg"
            />

          </div>

        </form>

        <div className="p-4 border-t flex justify-end gap-3">

          <button
            type="button"
            onClick={closeModal}
            className="px-5 py-2 rounded-lg border"
          >
            Cancel
          </button>

          <button
            type="submit"
            onClick={handleSubmit}
            disabled={loading}
            className="px-5 py-2 rounded-lg bg-slate-800 text-white disabled:opacity-50"
          >
            {loading ? 'Saving...' : 'Save Brand'}
          </button>

        </div>

      </div>

    </div>

  );

};

AddBrand.propTypes = {
  closeModal: PropTypes.func,
  refreshData: PropTypes.func
};

export default AddBrand;