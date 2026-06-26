'use client';

import { useState } from 'react';
import PropTypes from 'prop-types';

import CommonAdd from '@/components/AddPage';
import api from '@/app/services/api';

const AddWarehouse = ({
    closeModal,
    refreshData
}) => {

    const [message, setMessage] = useState('');
    const [errors, setErrors] = useState({});

    const [warehouse, setWarehouse] = useState({
        identifier: '',
        name:'',
        contactName: '',
        region: '',
        city: '',
        state: '',
        country: '',
        capacity: '',
        contactNumber: ''
    });

    const handleChange = (e) => {

        const { name, value } = e.target;

        setWarehouse((prev) => ({
            ...prev,
            [name]: value
        }));

        setErrors((prev) => ({
            ...prev,
            [name]: ''
        }));
    };

    const validate = () => {

        const newErrors = {};

        if (!warehouse.identifier.trim()) {
            newErrors.identifier = 'Identifier is required';
        }

        if (!warehouse.name.trim()) {
            newErrors.name = 'Name is required';
        }

        if (!warehouse.contactName.trim()) {
            newErrors.contactName = 'Contact Name is required';
        }

        if (!warehouse.region.trim()) {
            newErrors.region = 'Region is required';
        }

        if (!warehouse.city.trim()) {
            newErrors.city = 'City is required';
        }

        if (!warehouse.state.trim()) {
            newErrors.state = 'State is required';
        }

        if (!warehouse.country.trim()) {
            newErrors.country = 'Country is required';
        }

        if (!warehouse.capacity.trim()) {
            newErrors.capacity = 'Capacity is required';
        }

        if (!warehouse.contactNumber.trim()) {
            newErrors.contactNumber = 'Contact Number is required';
        }

        setErrors(newErrors);

        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e) => {

        e.preventDefault();

        if (!validate()) return;

        try {

            const response = await api.post(
                '/warehouse/add',
                warehouse
            );

            const data = response.data;

            setMessage(data.message);

            if (!data.success) return;

            refreshData?.();
            closeModal?.();

        } 
        catch (error) {

            console.error("Warehouse Error:", error);

            console.log(
                "Response:",
                error.response?.data
            );

            console.log(
                "Status:",
                error.response?.status
            );

            setMessage(
                error.response?.data?.message ||
                'Failed to add warehouse'
            );
        }
    };

    return (

        <div className="flex flex-col bg-white rounded-2xl overflow-hidden max-h-[85vh] border-t-4 border-cyan-500 shadow-lg">

            <div className="px-8 pt-6 pb-5 border-b">
                <h2 className="text-2xl font-bold">
                    Add Warehouse
                </h2>
            </div>

            <form
                id="add-warehouse-form"
                onSubmit={handleSubmit}
                className="flex-1 overflow-y-auto px-8 py-6 space-y-5"
            >

                {message && (
                    <div className="bg-cyan-50 border border-cyan-200 text-cyan-700 rounded-xl px-4 py-3">
                        {message}
                    </div>
                )}

                <CommonAdd
                    data={warehouse}
                    handleChange={handleChange}
                    errors={errors}
                />

                <div className="grid md:grid-cols-2 gap-5">

                    {[
                        'contactName',
                        'region',
                        'city',
                        'state',
                        'country',
                        'capacity',
                        'contactNumber'
                    ].map((field) => (

                        <div key={field}>

                            <label className="block mb-2 text-sm font-semibold">
                                {field}
                            </label>

                            <input
                                type="text"
                                name={field}
                                value={warehouse[field]}
                                onChange={handleChange}
                                className="w-full border rounded-xl px-4 py-3"
                            />

                            {errors[field] && (
                                <p className="text-red-500 text-sm mt-1">
                                    {errors[field]}
                                </p>
                            )}

                        </div>

                    ))}

                </div>

            </form>

            <div className="px-8 py-5 border-t flex justify-end gap-3">

                <button
                    onClick={closeModal}
                    type="button"
                    className="px-6 py-2 border rounded-xl"
                >
                    Cancel
                </button>

                <button
                    type="submit"
                    form="add-warehouse-form"
                    className="px-6 py-2 bg-cyan-500 text-white rounded-xl"
                >
                    Save Warehouse
                </button>

            </div>

        </div>

    );
};

AddWarehouse.propTypes = {
    closeModal: PropTypes.func,
    refreshData: PropTypes.func
};

export default AddWarehouse;