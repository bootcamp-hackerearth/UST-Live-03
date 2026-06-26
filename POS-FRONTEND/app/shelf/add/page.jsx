'use client';

import { useState } from 'react';
import api from '@/app/services/api';
import PropTypes from 'prop-types';

const AddShelf = ({
  closeModal,
  refreshData
}) => {

  const [shelf, setShelf] = useState({
    identifier: '',
    name: '',
    status: true
  });

  const handleChange = (e) => {

    const { name, value } = e.target;

    setShelf({
      ...shelf,
      [name]: value
    });

  };

  const handleSubmit = async (e) => {

  e.preventDefault();

  try {

    const response = await api.post(
      '/shelf/add',
      shelf
    );

    const data = response.data;

    alert(data.message);

    if (data.success) {
      refreshData();
      closeModal();
    }

  } catch (err) {

    console.error(err);
    alert('Failed to add shelf');

  }

  };

  return (

    <div className="p-6">

      <h2 className="text-xl font-semibold mb-6">
        Add Shelf
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
            type="text"
            name="identifier"
            value={shelf.identifier}
            onChange={handleChange}
            className="w-full border p-3 rounded-lg"
            required
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
            value={shelf.name}
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
            className="bg-slate-800 text-white px-5 py-2 rounded-lg"
          >
            Save
          </button>

        </div>

      </form>

    </div>

  );

};

AddShelf.propTypes = {
  closeModal: PropTypes.func.isRequired,
  refreshData: PropTypes.func.isRequired
};

export default AddShelf;