'use client';

import { useState } from 'react';
import api from '@/app/services/api';
import PropTypes from 'prop-types';

const AddModel = ({
  closeModal,
  refreshData
}) => {

  const [model, setModel] = useState({
    identifier: '',
    name: '',
    status: true
  });

  const [message, setMessage] = useState('');
  const [messageType, setMessageType] = useState('');

  const handleChange = (e) => {

    const { name, value } = e.target;

    setModel({
      ...model,
      [name]: value
    });

  };

  const validate = () => {

    if (!model.identifier.trim()) {
      setMessageType('error');
      setMessage('Identifier is required');
      return false;
    }

    if (!model.name.trim()) {
      setMessageType('error');
      setMessage('Name is required');
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
        '/model/add',
        model
      );

      const data = response.data;

      if (data.success) {

        setMessageType('success');
        setMessage(
          data.message || 'Model Added Successfully'
        );

        refreshData?.();

        setTimeout(() => {
          closeModal?.();
        }, 1000);

      } else {

        setMessageType('error');
        setMessage(
          data.message || 'Failed to add model'
        );
      }

    } catch (err) {

      console.error(err);

      setMessageType('error');
      setMessage('Failed to add model');

    }

  };

  return (

    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">

      <div className="bg-white w-full max-w-2xl rounded-xl shadow-lg">

        <div className="p-5 border-b">

          <h2 className="text-xl font-semibold">
            Add Model
          </h2>

          {message && (
            <div
              className={`mt-4 rounded-lg px-4 py-3 text-sm border ${
                messageType === 'error'
                  ? 'border-red-200 bg-red-50 text-red-600'
                  : 'border-green-200 bg-green-50 text-green-600'
              }`}
            >
              {message}
            </div>
          )}

        </div>

        <form
          onSubmit={handleSubmit}
          className="p-6 space-y-4"
        >

          <div>

            <label
              htmlFor="identifier"
              className="text-sm text-gray-600"
            >
              Identifier
            </label>

            <input
              type="text"
              name="identifier"
              value={model.identifier}
              onChange={handleChange}
              className="w-full border p-2.5 rounded-lg"
            />

          </div>

          <div>

            <label
              htmlFor="name"
              className="text-sm text-gray-600"
            >
              Name
            </label>

            <input
              type="text"
              name="name"
              value={model.name}
              onChange={handleChange}
              className="w-full border p-2.5 rounded-lg"
            />

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
            Save Model
          </button>

        </div>

      </div>

    </div>

  );

};

AddModel.propTypes = {
  closeModal: PropTypes.func.isRequired,
  refreshData: PropTypes.func.isRequired
};

export default AddModel;