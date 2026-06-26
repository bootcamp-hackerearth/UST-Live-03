'use client';

import { useEffect, useState } from 'react';
import api from '@/app/services/api';
import PropTypes from 'prop-types';

const UpdateShelf = ({
  data,
  closeModal,
  refreshData
}) => {

  const [shelf, setShelf] = useState(data);

  useEffect(() => {

    const loadShelf = async () => {

      try {

        const response = await api.get(
          `/shelf/get?identifier=${data.identifier}`
        );

        setShelf(response.data);

      } catch (err) {

        console.error(err);

      }

    };

    loadShelf();

  }, [data]);

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

      await api.put(
        '/shelf/update',
        shelf
      );

      alert('Shelf Updated Successfully');

      refreshData();
      closeModal();

    } catch (err) {

      console.error(err);
      alert('Update Failed');

    }

  };

  if (!shelf) return null;

  return (

    <div className="p-6">

      <h2 className="text-xl font-semibold mb-6">
        Update Shelf
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
            value={shelf.identifier || ''}
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
            value={shelf.name || ''}
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

UpdateShelf.propTypes = {
  data: PropTypes.shape({
    identifier: PropTypes.string.isRequired
  }).isRequired,

  closeModal: PropTypes.func.isRequired,

  refreshData: PropTypes.func.isRequired
};

export default UpdateShelf;