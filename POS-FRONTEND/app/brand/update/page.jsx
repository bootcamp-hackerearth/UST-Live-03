'use client';

import { useEffect, useState } from 'react';
import api from '@/app/services/api';
import CommonUpdate from '@/components/UpadatePage';
import PropTypes from 'prop-types';

const UpdateBrand = ({
  data,
  closeModal,
  refreshData
}) => {

  const [brand, setBrand] = useState(data);

  useEffect(() => {

    const fetchBrand = async () => {

      try {

        const response = await api.get(
          `/brand/get?identifier=${data.identifier}`
        );

        setBrand(response.data);

      } catch (err) {

        console.error(err);

      }

    };

    if (data?.identifier) {
      fetchBrand();
    }

  }, [data]);

  const handleChange = (e) => {

    const { name, value } = e.target;

    setBrand({
      ...brand,
      [name]: value
    });

  };

  const validateForm = () => {

    if (!brand.identifier?.trim()) {

      alert('Identifier is required');
      return false;

    }

    if (!brand.name?.trim()) {

      alert('Brand Name is required');
      return false;

    }

    if (!brand.description?.trim()) {

      alert('Description is required');
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

      await api.put(
        '/brand/update',
        brand
      );

      alert('Brand Updated Successfully');

      refreshData();
      closeModal();

    } catch (err) {

      console.error(err);
      alert('Failed to update brand');

    }

  };

  if (!brand) return null;

  return (

    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">

      <div className="bg-white w-full max-w-3xl rounded-xl shadow-lg">

        <div className="p-5 border-b">
          <h2 className="text-xl font-semibold">
            Update Brand
          </h2>
        </div>

        <form
          onSubmit={handleSubmit}
          className="p-6 space-y-5"
        >

          <div className="grid md:grid-cols-2 gap-4">

            <CommonUpdate
              data={brand}
              handleChange={handleChange}
            />

            <div className="md:col-span-2">

              <label
                htmlFor="description"
                className="block mb-2 text-sm font-semibold text-slate-700"
              >
                Description
              </label>

              <textarea
                name="description"
                value={brand.description || ''}
                onChange={handleChange}
                rows={4}
                required
                className="w-full p-3 border border-slate-300 rounded-lg"
              />

            </div>

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
            className="px-5 py-2 rounded-lg bg-slate-800 text-white"
          >
            Update Brand
          </button>

        </div>

      </div>

    </div>

  );

};

UpdateBrand.propTypes = {
  data: PropTypes.shape({
    identifier: PropTypes.string,
    name: PropTypes.string,
    description: PropTypes.string
  }),
  closeModal: PropTypes.func,
  refreshData: PropTypes.func
};

export default UpdateBrand;