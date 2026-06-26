'use client';

import { useEffect, useState } from 'react';
import api from '@/app/services/api';
import PropTypes from 'prop-types';

const UpdateUnit = ({
  data,
  closeModal,
  refreshData
}) => {

  const [unit, setUnit] = useState(data);

  useEffect(() => {

    const loadUnit = async () => {

      try {

        const response = await api.get(
          `/unit/get?identifier=${data.identifier}`
        );

        setUnit(response.data);

      } catch (err) {

        console.error(err);

      }

    };

    loadUnit();

  }, [data]);

  const handleChange = (e) => {

    const { name, value } = e.target;

    setUnit({
      ...unit,
      [name]: value
    });

  };

  const handleSubmit = async (e) => {

    e.preventDefault();

    try {

      await api.put(
        '/unit/update',
        unit
      );

      alert('Unit Updated Successfully');

      refreshData();
      closeModal();

    } catch (err) {

      console.error(err);
      alert('Update Failed');

    }

  };

  if (!unit) return null;

  return (

    <div className="p-6">

      <h2 className="text-xl font-semibold mb-6">
        Update Unit
      </h2>

      <form
        onSubmit={handleSubmit}
        className="space-y-5"
      >

        <div>
          <label 
          htmlFor='identifier'
          className="block mb-2 font-medium">
            Identifier
          </label>

          <input
            value={unit.identifier || ''}
            readOnly
            className="w-full border p-3 rounded-lg bg-gray-100"
          />
        </div>

        <div>
          <label 
          htmlFor='name'
          className="block mb-2 font-medium">
            Name
          </label>

          <input
            type="text"
            name="name"
            value={unit.name || ''}
            onChange={handleChange}
            className="w-full border p-3 rounded-lg"
            required
          />
        </div>

        <div className="flex justify-end gap-3">

          <button
            type="button"
            onClick={closeModal}
            className="border px-4 py-2 rounded-lg"
          >
            Cancel
          </button>

          <button
            type="submit"
            className="bg-blue-600 text-white px-5 py-2 rounded-lg"
          >
            Update
          </button>

        </div>

      </form>

    </div>

  );

};


UpdateUnit.propTypes = {
  data: PropTypes.shape({
    identifier: PropTypes.string.isRequired
  }).isRequired,

  closeModal: PropTypes.func.isRequired,

  refreshData: PropTypes.func.isRequired
};

export default UpdateUnit;