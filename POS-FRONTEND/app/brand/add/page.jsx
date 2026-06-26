'use client';

import { useState } from 'react';
import PropTypes from 'prop-types';

import CommonAdd from '@/components/AddPage';
import api from '@/app/services/api';

const AddBrand = ({
    closeModal,
    refreshData
}) => {

    const [message, setMessage] = useState('');

    const [errors, setErrors] = useState({});

    const [brand, setBrand] = useState({
        identifier: '',
        name: '',
        description: ''
    });

    const handleChange = (e) => {

        const { name, value } = e.target;

        setBrand(prev => ({
            ...prev,
            [name]: value
        }));

        setErrors(prev => ({
            ...prev,
            [name]: ''
        }));
    };

    const validate = () => {

        const newErrors = {};

        if (!brand.identifier.trim()) {
            newErrors.identifier = 'Identifier is required';
        }

        if (!brand.name.trim()) {
            newErrors.name = 'Name is required';
        }

        if (!brand.description.trim()) {
            newErrors.description = 'Description is required';
        }

        setErrors(newErrors);

        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e) => {

        e.preventDefault();

        if (!validate()) return;

        try {

            const response = await api.post(
                '/brand/add',
                brand
            );

            const data = response.data;

            setMessage(data.message);

            if (!data.success) return;

            refreshData?.();
            closeModal?.();

        } catch (err) {

            console.error(err);
            setMessage('Failed to add brand');

        }
    };

    return (

        <div className="flex flex-col bg-white rounded-2xl overflow-hidden border-t-4 border-cyan-500 shadow-lg">

            <div className="px-8 pt-6 pb-5 border-b">
                <h2 className="text-2xl font-bold">
                    Add Brand
                </h2>
            </div>

            <form
                onSubmit={handleSubmit}
                className="p-8 space-y-6"
            >

                {message && (
                    <div className="bg-cyan-50 border border-cyan-200 text-cyan-600 rounded-xl px-4 py-3">
                        {message}
                    </div>
                )}

                <CommonAdd
                    data={brand}
                    handleChange={handleChange}
                    errors={errors}
                />

                <div>
                    <label
                        htmlFor="description" 
                        className="block mb-2 text-sm font-semibold">
                        Description
                    </label>

                    <textarea
                        name="description"
                        value={brand.description}
                        onChange={handleChange}
                        rows={4}
                        className="w-full border rounded-xl px-4 py-3"
                    />

                    {errors.description && (
                        <p className="text-red-500 text-sm mt-1">
                            {errors.description}
                        </p>
                    )}
                </div>

                <div className="flex justify-end gap-3">

                    <button
                        type="button"
                        onClick={closeModal}
                        className="px-6 py-2 rounded-xl border"
                    >
                        Cancel
                    </button>

                    <button
                        type="submit"
                        className="px-6 py-2 rounded-xl bg-cyan-500 text-white"
                    >
                        Save Brand
                    </button>

                </div>

            </form>

        </div>
    );
};

AddBrand.propTypes = {
    closeModal: PropTypes.func,
    refreshData: PropTypes.func
};

export default AddBrand;