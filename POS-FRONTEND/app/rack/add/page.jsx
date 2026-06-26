'use client';

import { useEffect, useState } from 'react';
import api from '@/app/services/api';
import PropTypes from 'prop-types';

const AddRack = ({
  closeModal,
  refreshData
}) => {

  const [shelves, setShelves] = useState([]);

  const [rack, setRack] = useState({
    identifier: '',
    name: '',
    shelfs: [],
    status: true
  });

  useEffect(() => {
    fetchShelves();
  }, []);

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
          ...rack.shelfs,
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

  const handleSubmit = async (e) => {

  e.preventDefault();

  try {

    const response = await api.post(
      '/rack/add',
      rack
    );

    const data = response.data;

    alert(data.message);

    if (data.success) {
      refreshData();
      closeModal();
    }

  } catch (err) {

    console.error(err);
    alert('Failed to add rack');

  }

};

  return (

    <div className="p-6">

      <h2 className="text-xl font-semibold mb-6">
        Add Rack
      </h2>

      <form
        onSubmit={handleSubmit}
        className="space-y-5"
      >

        <input
          type="text"
          name="identifier"
          placeholder="Identifier"
          value={rack.identifier}
          onChange={handleChange}
          className="w-full border p-3 rounded-lg"
          required
        />

        <input
          type="text"
          name="name"
          placeholder="Rack Name"
          value={rack.name}
          onChange={handleChange}
          className="w-full border p-3 rounded-lg"
          required
        />

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

AddRack.propTypes = {
  closeModal: PropTypes.func.isRequired,
  refreshData: PropTypes.func.isRequired
};

export default AddRack;