'use client';

import { useEffect, useState } from 'react';
import api from '@/app/services/api';
import CommonUpdate from '@/components/UpadatePage';
import PropTypes from 'prop-types';

const UpdateWarehouse = ({
  data,
  closeModal,
  refreshData
}) => {

  const [warehouse, setWarehouse] = useState(data);

  useEffect(() => {

    const fetchWarehouse = async () => {

      try {

        const response = await api.get(
          `/warehouse/get?identifier=${data.identifier}`
        );

        setWarehouse(response.data);

      } catch (err) {

        console.error(err);

      }

    };

    if (data?.identifier) {
      fetchWarehouse();
    }

  }, [data]);

  const handleChange = (e) => {

    const { name, value } = e.target;

    setWarehouse({
      ...warehouse,
      [name]: value
    });

  };

  const validateForm = () => {

    const requiredFields = {
      identifier: 'Identifier',
      region: 'Region',
      city: 'City',
      state: 'State',
      country: 'Country',
      capacity: 'Capacity',
      contactName: 'Contact Name',
      contactNumber: 'Contact Number'
    };

    for (const [key, label] of Object.entries(requiredFields)) {

      if (
        warehouse[key] === undefined ||
        warehouse[key] === null ||
        warehouse[key].toString().trim() === ''
      ) {

        alert(`${label} is required`);
        return false;

      }

    }

    if (!/^\d+$/.test(warehouse.capacity)) {

      alert('Capacity must contain only numbers');
      return false;

    }

    if (!/^\d{10}$/.test(warehouse.contactNumber)) {

      alert('Contact Number must be exactly 10 digits');
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
        '/warehouse/update',
        warehouse
      );

      alert('Warehouse Updated Successfully');

      refreshData();
      closeModal();

    } catch (err) {

      console.log(err);
      alert('Failed to update warehouse');

    }

  };

  if (!warehouse) return null;

  return (

    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">

      <div className="bg-white w-full max-w-4xl rounded-xl shadow-lg max-h-[90vh] flex flex-col">

        <div className="p-5 border-b">
          <h2 className="text-xl font-semibold">
            Update Warehouse
          </h2>
        </div>

        <form
          onSubmit={handleSubmit}
          className="p-6 overflow-y-auto space-y-5"
        >

          <div className="grid md:grid-cols-2 gap-4">

            <CommonUpdate
              data={warehouse}
              handleChange={handleChange}
              showName={false}
            />

            <div>

              <label
                htmlFor="region"
                className="block mb-2 text-sm font-semibold text-slate-700"
              >
                Region
              </label>

              <input
                type="text"
                name="region"
                value={warehouse.region || ''}
                onChange={handleChange}
                required
                className="w-full p-3 border border-slate-300 rounded-lg"
              />

            </div>

            <div>

              <label
                htmlFor="city"
                className="block mb-2 text-sm font-semibold text-slate-700"
              >
                City
              </label>

              <input
                type="text"
                name="city"
                value={warehouse.city || ''}
                onChange={handleChange}
                required
                className="w-full p-3 border border-slate-300 rounded-lg"
              />

            </div>

            <div>

              <label
                htmlFor="state"
                className="block mb-2 text-sm font-semibold text-slate-700"
              >
                State
              </label>

              <input
                type="text"
                name="state"
                value={warehouse.state || ''}
                onChange={handleChange}
                required
                className="w-full p-3 border border-slate-300 rounded-lg"
              />

            </div>

            <div>

              <label
                htmlFor="country"
                className="block mb-2 text-sm font-semibold text-slate-700"
              >
                Country
              </label>

              <input
                type="text"
                name="country"
                value={warehouse.country || ''}
                onChange={handleChange}
                required
                className="w-full p-3 border border-slate-300 rounded-lg"
              />

            </div>

            <div>

              <label
                htmlFor="capacity"
                className="block mb-2 text-sm font-semibold text-slate-700"
              >
                Capacity
              </label>

              <input
                type="number"
                name="capacity"
                value={warehouse.capacity || ''}
                onChange={handleChange}
                min="1"
                required
                className="w-full p-3 border border-slate-300 rounded-lg"
              />

            </div>

            <div>

              <label
                htmlFor="contactName"
                className="block mb-2 text-sm font-semibold text-slate-700"
              >
                Contact Name
              </label>

              <input
                type="text"
                name="contactName"
                value={warehouse.contactName || ''}
                onChange={handleChange}
                required
                className="w-full p-3 border border-slate-300 rounded-lg"
              />

            </div>

            <div>

              <label
                htmlFor="contactNumber"
                className="block mb-2 text-sm font-semibold text-slate-700"
              >
                Contact Number
              </label>

              <input
                type="tel"
                name="contactNumber"
                value={warehouse.contactNumber || ''}
                onChange={handleChange}
                maxLength={10}
                pattern="[0-9]{10}"
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
            className="px-5 py-2 rounded-lg border hover:bg-gray-100"
          >
            Cancel
          </button>

          <button
            type="submit"
            onClick={handleSubmit}
            className="px-5 py-2 rounded-lg bg-slate-800 text-white hover:bg-slate-700"
          >
            Update Warehouse
          </button>

        </div>

      </div>

    </div>

  );

};

UpdateWarehouse.propTypes = {
  data: PropTypes.object.isRequired,
  closeModal: PropTypes.func.isRequired,
  refreshData: PropTypes.func.isRequired
};

export default UpdateWarehouse;