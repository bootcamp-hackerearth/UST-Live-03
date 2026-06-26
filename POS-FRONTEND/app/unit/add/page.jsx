'use client';

import { useState } from 'react';
import PropTypes from 'prop-types';
import api from '@/app/services/api';

const AddUnit = ({
  closeModal,
  refreshData
}) => {

  const [unit, setUnit] = useState({
    identifier: '',
    name: '',
    status: true
  });

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

      const response = await api.post(
        '/unit/add',
        unit
      );

      const data = response.data;

      if (!data.success) {
        alert(data.message);
        return;
      }

      alert(data.message || 'Unit Added Successfully');

      refreshData();
      closeModal();

    } catch (err) {

      console.error(err);
      alert('Failed to add unit');

    }

  };


  return (

    <div className="p-6">

      <h2 className="text-xl font-semibold mb-6">
        Add Unit
      </h2>


      <form
        onSubmit={handleSubmit}
        className="space-y-5"
      >


        <div>

          <label
            htmlFor="identifier"
            className="block mb-2 font-medium"
          >
            Identifier
          </label>


          <input
            id="identifier"
            type="text"
            name="identifier"
            value={unit.identifier}
            onChange={handleChange}
            className="w-full border p-3 rounded-lg"
            required
          />

        </div>



        <div>

          <label
            htmlFor="name"
            className="block mb-2 font-medium"
          >
            Name
          </label>


          <input
            id="name"
            type="text"
            name="name"
            value={unit.name}
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


AddUnit.propTypes = {
  closeModal: PropTypes.func.isRequired,
  refreshData: PropTypes.func.isRequired
};


export default AddUnit;