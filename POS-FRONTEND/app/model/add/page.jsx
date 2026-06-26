'use client';

import { useState } from 'react';
import PropTypes from 'prop-types';
import api from '@/app/services/api';

const AddModel = ({ closeModal, refreshData }) => {

    const [model, setModel] = useState({
        identifier: '',
        name: '',
        status: true
    });

    const [errors, setErrors] = useState({});

    const handleChange = (e) => {
        const { name, value } = e.target;

        setModel({
            ...model,
            [name]: value
        });

        setErrors({
            ...errors,
            [name]: ''
        });
    };

    const validate = () => {

        const newErrors = {};

        if (!model.identifier.trim()) {
            newErrors.identifier = 'Identifier is required';
        }

        if (!model.name.trim()) {
            newErrors.name = 'Name is required';
        }

        setErrors(newErrors);

        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e) => {

        e.preventDefault();

        if (!validate()) return;

        try {

            await api.post('/model/add', model);

            alert('Model Added');

            refreshData();
            closeModal();

        } catch (err) {

            console.error(err);

            alert(
                err.response?.data?.message ||
                'Failed to add model'
            );
        }
    };

    return (
        <div className="bg-white rounded-2xl border-t-4 border-cyan-500 shadow-lg overflow-hidden">

            <div className="px-8 py-6 border-b bg-slate-50">
                <h2 className="text-2xl font-bold text-slate-800">
                    Add Model
                </h2>
            </div>

            <form
                onSubmit={handleSubmit}
                className="p-8 space-y-5"
            >

                <div>
                    <input
                        name="identifier"
                        value={model.identifier}
                        onChange={handleChange}
                        placeholder="Identifier"
                        className="w-full h-12 px-4 rounded-xl border"
                    />

                    {errors.identifier &&
                        <p className="text-red-500 text-xs mt-1">
                            {errors.identifier}
                        </p>
                    }
                </div>

                <div>
                    <input
                        name="name"
                        value={model.name}
                        onChange={handleChange}
                        placeholder="Model Name"
                        className="w-full h-12 px-4 rounded-xl border"
                    />

                    {errors.name &&
                        <p className="text-red-500 text-xs mt-1">
                            {errors.name}
                        </p>
                    }
                </div>

                <div className="flex justify-end gap-3">

                    <button
                        type="button"
                        onClick={closeModal}
                        className="px-5 py-2 border rounded-xl"
                    >
                        Cancel
                    </button>

                    <button
                        type="submit"
                        className="px-5 py-2 rounded-xl bg-cyan-500 text-white"
                    >
                        Save Model
                    </button>

                </div>

            </form>

        </div>
    );
};

AddModel.propTypes = {
    closeModal: PropTypes.func.isRequired,
    refreshData: PropTypes.func.isRequired
};

export default AddModel;