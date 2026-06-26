'use client';

import { useState } from 'react';
import api from '@/app/services/api';
import CommonAdd from '@/components/AddPage';
import PropTypes from 'prop-types';

const AddWarehouse = ({
  closeModal,
  refreshData
}) => {

  const [message, setMessage] = useState('');
  const [messageType, setMessageType] = useState('');

  const [warehouse, setWarehouse] = useState({
    identifier: '',
    region: '',
    city: '',
    state: '',
    country: '',
    capacity: '',
    contactName: '',
    contactNumber: ''
  });

  const handleChange = (e) => {
  const { name, value } = e.target;

  if (name === 'contactNumber') {

    const numericValue = value.replaceAll(/\D/g, '');

    if (
      numericValue.length > 0 &&
      !['6', '7', '8', '9'].includes(numericValue.charAt(0))
    ) {
      return;
    }

    if (numericValue.length > 10) {
      return;
    }

    setWarehouse((prev) => ({
      ...prev,
      contactNumber: numericValue
    }));

    return;
  }

  setWarehouse((prev) => ({
    ...prev,
    [name]: value
  }));
};

  const validate = () => {

    if (!warehouse.identifier.trim()) {
      setMessageType('error');
      setMessage('Identifier is required');
      return false;
    }

    if (!warehouse.region.trim()) {
      setMessageType('error');
      setMessage('Region is required');
      return false;
    }

    if (!warehouse.city.trim()) {
      setMessageType('error');
      setMessage('City is required');
      return false;
    }

    if (!warehouse.state.trim()) {
      setMessageType('error');
      setMessage('State is required');
      return false;
    }

    if (!warehouse.country.trim()) {
      setMessageType('error');
      setMessage('Country is required');
      return false;
    }

    if (!warehouse.capacity.toString().trim()) {
      setMessageType('error');
      setMessage('Capacity is required');
      return false;
    }

    if (!warehouse.contactName.trim()) {
      setMessageType('error');
      setMessage('Contact Name is required');
      return false;
    }

if (!warehouse.contactNumber.trim()) {
  setMessageType('error');
  setMessage('Contact Number is required');
  return false;
}

if (!/^[6-9]\d{9}$/.test(warehouse.contactNumber)) {
  setMessageType('error');
  setMessage(
    'Contact Number must be 10 digits and start with 6, 7, 8 or 9'
  );
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

      const response = await api.post(
        '/warehouse/add',
        warehouse
      );

      const data = response.data;

      if (data.success) {

        setMessageType('success');
        setMessage(
          data.message || 'Warehouse Added Successfully'
        );

        refreshData?.();
        closeModal?.();

      } else {

        setMessageType('error');
        setMessage(
          data.message || 'Failed to add warehouse'
        );
      }

    } catch (err) {

      console.log(err);

      setMessageType('error');
      setMessage('Failed to add warehouse');
    }
  };

  return (

    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">

      <div className="bg-white w-full max-w-4xl rounded-xl shadow-lg max-h-[90vh] flex flex-col">

        <div className="p-5 border-b">
          <h2 className="text-xl font-semibold">
            Add Warehouse
          </h2>
        </div>

        <div className="p-6 overflow-y-auto flex-1">

          {message && (
            <div
              className={`mb-5 rounded-lg px-4 py-3 text-sm border ${
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

            <CommonAdd
              data={warehouse}
              handleChange={handleChange}
              showName={false}
            />

            <div className="grid md:grid-cols-2 gap-4">

              <div>
                <label
                  htmlFor="region"
                  className="text-sm text-gray-600"
                >
                  Region
                </label>

                <input
                  type="text"
                  name="region"
                  value={warehouse.region}
                  onChange={handleChange}
                  className="w-full border p-2.5 rounded-lg"
                  placeholder="Enter region"
                />
              </div>

              <div>
                <label
                  htmlFor="city"
                  className="text-sm text-gray-600"
                >
                  City
                </label>

                <input
                  type="text"
                  name="city"
                  value={warehouse.city}
                  onChange={handleChange}
                  className="w-full border p-2.5 rounded-lg"
                  placeholder="Enter city"
                />
              </div>

              <div>
                <label
                  htmlFor="state"
                  className="text-sm text-gray-600"
                >
                  State
                </label>

                <input
                  type="text"
                  name="state"
                  value={warehouse.state}
                  onChange={handleChange}
                  className="w-full border p-2.5 rounded-lg"
                  placeholder="Enter state"
                />
              </div>

              <div>
                <label
                  htmlFor="country"
                  className="text-sm text-gray-600"
                >
                  Country
                </label>

                <input
                  type="text"
                  name="country"
                  value={warehouse.country}
                  onChange={handleChange}
                  className="w-full border p-2.5 rounded-lg"
                  placeholder="Enter country"
                />
              </div>

              <div>
                <label
                  htmlFor="capacity"
                  className="text-sm text-gray-600"
                >
                  Capacity
                </label>

                <input
                  type="text"
                  name="capacity"
                  value={warehouse.capacity}
                  onChange={handleChange}
                  className="w-full border p-2.5 rounded-lg"
                  placeholder="Enter capacity of a warehouse"
                />
              </div>

              <div>
                <label
                  htmlFor="contactName"
                  className="text-sm text-gray-600"
                >
                  Contact Name
                </label>

                <input
                  type="text"
                  name="contactName"
                  value={warehouse.contactName}
                  onChange={handleChange}
                  placeholder="Enter a contact name"
                  className="w-full border p-2.5 rounded-lg"
                />
              </div>

              <div>
                <label
                  htmlFor="contactNumber"
                  className="text-sm text-gray-600"
                >
                  Contact Number
                </label>

                <input
                  type="text"
                  name="contactNumber"
                  value={warehouse.contactNumber}
                  onChange={handleChange}
                  maxLength={10}
                  placeholder="Enter a valid phone number"
                  className="w-full border p-2.5 rounded-lg"
                />
              </div>

            </div>

          </form>

        </div>

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
            Save Warehouse
          </button>

        </div>

      </div>

    </div>
  );
};

AddWarehouse.propTypes = {
  closeModal: PropTypes.func.isRequired,
  refreshData: PropTypes.func.isRequired
};

export default AddWarehouse;