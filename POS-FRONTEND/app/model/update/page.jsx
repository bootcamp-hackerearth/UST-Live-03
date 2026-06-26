'use client';

import { useEffect, useState } from 'react';
import api from '@/app/services/api';
import PropTypes from 'prop-types';

const UpdateModel = ({
  data,
  closeModal,
  refreshData
}) => {

  const [model, setModel] = useState(data);
  const [message, setMessage] = useState('');
  const [messageType, setMessageType] = useState('');

  useEffect(() => {

    const fetchModel = async () => {

      try {

        const response = await api.get(
          `/model/get?identifier=${data.identifier}`
        );

        setModel(response.data);

      } catch (err) {

        console.error(err);

      }

    };

    if (data?.identifier) {
      fetchModel();
    }

  }, [data]);

  const handleChange = (e) => {

    const { name, value } = e.target;

    setModel({
      ...model,
      [name]: value
    });

  };

  const validate = () => {

    if (!model.name?.trim()) {
      setMessageType('error');
      setMessage('Model Name is required');
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
        '/model/update',
        model
      );

      setMessageType('success');
      setMessage('Model Updated Successfully');

      refreshData();
      closeModal();

    } catch (err) {

      console.error(err);

      setMessageType('error');
      setMessage('Update Failed');

    }

  };

  if (!model) return null;

  return (

    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">

      <div className="bg-white w-full max-w-2xl rounded-xl shadow-lg">

        <div className="p-5 border-b">

          <h2 className="text-xl font-semibold">
            Update Model
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
              value={model.identifier || ''}
              readOnly
              className="w-full border p-2.5 rounded-lg bg-gray-100"
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
              value={model.name || ''}
              onChange={handleChange}
              className="w-full border p-2.5 rounded-lg"
              required
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
            type="button"
            onClick={handleSubmit}
            className="px-5 py-2 rounded-lg bg-slate-800 text-white hover:bg-slate-700"
          >
            Update Model
          </button>

        </div>

      </div>

    </div>

  );

};

UpdateModel.propTypes = {
  data: PropTypes.shape({
    identifier: PropTypes.string,
    name: PropTypes.string,
    status: PropTypes.bool
  }).isRequired,
  closeModal: PropTypes.func.isRequired,
  refreshData: PropTypes.func.isRequired
};

export default UpdateModel;