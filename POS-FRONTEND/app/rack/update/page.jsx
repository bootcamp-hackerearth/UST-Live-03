'use client';

import { useEffect, useState } from 'react';
import api from '@/app/services/api';
import PropTypes from 'prop-types';

const UpdateRack = ({
  data,
  closeModal,
  refreshData
}) => {

  const [rack, setRack] = useState(data);
  const [shelves, setShelves] = useState([]);
  const [message, setMessage] = useState('');
  const [messageType, setMessageType] = useState('');

  useEffect(() => {

    fetchShelves();

    const loadRack = async () => {

      try {

        const response = await api.get(
          `/rack/get?identifier=${data.identifier}`
        );

        setRack(response.data);

      } catch (err) {

        console.error(err);

      }

    };

    loadRack();

  }, [data]);

  const fetchShelves = async () => {

    try {

      const response =
        await api.get('/shelf/shelfactive');

      setShelves(response.data || []);

    } catch (err) {

      console.error(err);

    }

  };

  const handleChange = (e) => {

    const { name, value } = e.target;

    setRack({
      ...rack,
      [name]: value
    });

  };

  const handleShelfChange = (e) => {

    const { value, checked } = e.target;

    if (checked) {

      setRack({
        ...rack,
        shelfs: [
          ...(rack.shelfs || []),
          value
        ]
      });

    } else {

      setRack({
        ...rack,
        shelfs: rack.shelfs.filter(
          shelf => shelf !== value
        )
      });

    }

  };

  const validate = () => {

  if (!rack.name?.trim()) {
    setMessageType('error');
    setMessage('Rack Name is required');
    return false;
  }

  if (!rack.shelfs || rack.shelfs.length === 0) {
    setMessageType('error');
    setMessage('Please select at least one Shelf');
    return false;
  }

  return true;
};

  const handleSubmit = async (e) => {

  e.preventDefault();

  setMessage('');
  setMessageType('');

  if (!validate()) {
    return;
  }

  try {

    await api.put(
      '/rack/update',
      rack
    );

    setMessageType('success');
    setMessage('Rack Updated Successfully');

    refreshData();
    closeModal();

  } catch (err) {

    console.error(err);

    setMessageType('error');
    setMessage('Update Failed');

  }

};

  if (!rack) return null;

  return (

    <div className="p-6">

      <h2 className="text-xl font-semibold mb-6">
        Update Rack
      </h2>

      {message && (
        <div
          className={`mb-4 rounded-lg px-4 py-3 text-sm border ${
            messageType === 'error'
              ? 'border-red-200 bg-red-50 text-red-600'
              : 'border-green-200 bg-green-50 text-green-600'
          }`}
        >
          {message}
        </div>
      )}

      <form
        onSubmit={handleSubmit}
        className="space-y-5"
      >

        <div>
          <label 
          htmlFor='identifier'
          className="block mb-2 text-sm font-semibold text-slate-700">
            Identifier
          </label>

          <input
            value={rack.identifier || ''}
            readOnly
            className="
              w-full
              p-3
              border
              border-slate-300
              rounded-lg
              bg-gray-100
            "
          />
        </div>

        <div>
          <label 
          htmlFor='name'
          className="block mb-2 text-sm font-semibold text-slate-700">
            Name
          </label>

          <input
            type="text"
            name="name"
            value={rack.name || ''}
            onChange={handleChange}
            className="
              w-full
              p-3
              border
              border-slate-300
              rounded-lg
              focus:outline-none
              focus:ring-2
              focus:ring-slate-400
            "
            required
          />
        </div>

        <div className="border rounded-xl p-4 bg-slate-50">

          <h3 className="font-semibold mb-3">
            Select Shelves
          </h3>

          <div className="grid grid-cols-2 gap-2">

            {shelves.map((shelf) => (

              <label
                key={shelf.identifier}
                className="flex items-center gap-2 bg-white border rounded-lg p-2"
              >

                <input
                  type="checkbox"
                  value={shelf.identifier}
                  checked={
                    rack.shelfs?.includes(
                      shelf.identifier
                    ) || false
                  }
                  onChange={handleShelfChange}
                />

                {shelf.name}

              </label>

            ))}

          </div>

        </div>

        <div className="flex justify-end gap-3">

          <button
            type="button"
            onClick={closeModal}
            className="border px-4 py-2 rounded-lg hover:bg-gray-100"
          >
            Cancel
          </button>

          <button
            type="submit"
            className="bg-blue-600 text-white px-5 py-2 rounded-lg hover:bg-blue-700"
          >
            Update
          </button>

        </div>

      </form>

    </div>

  );

};

UpdateRack.propTypes = {
  data: PropTypes.shape({
    identifier: PropTypes.string.isRequired
  }).isRequired,

  closeModal: PropTypes.func.isRequired,

  refreshData: PropTypes.func.isRequired
};

export default UpdateRack;