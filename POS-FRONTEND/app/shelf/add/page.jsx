'use client';

import { useState } from 'react';
import PropTypes from 'prop-types';
import api from '@/app/services/api';

const AddShelf = ({ closeModal, refreshData }) => {

    const [shelf, setShelf] = useState({
        identifier: '',
        name: '',
        status: true
    });

    const [errors, setErrors] = useState({});

    const validate = () => {
        const newErrors = {};

        if (!shelf.identifier.trim()) {
            newErrors.identifier = 'Shelf ID is required';
        }

        if (!shelf.name.trim()) {
            newErrors.name = 'Shelf Name is required';
        }

        setErrors(newErrors);

        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!validate()) return;

        try {

            const response = await api.post(
                '/shelf/add',
                shelf
            );

            if (response.data?.success === false) {
                alert(response.data.message);
                return;
            }

            alert('Shelf Added Successfully');

            refreshData();
            closeModal();

        } catch (err) {

            console.error(err);

            alert(
                err.response?.data?.message ||
                'Failed to add shelf'
            );
        }
    };

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm">

            <div className="bg-white rounded-2xl border-t-4 border-cyan-500 shadow-lg w-full max-w-lg">

                <div className="px-8 py-6 border-b bg-slate-50">
                    <h2 className="text-2xl font-bold">
                        Add Shelf
                    </h2>
                </div>

                <form
                    onSubmit={handleSubmit}
                    className="p-6 space-y-4"
                >

                    <div>
                        <input
                            placeholder="Shelf Identifier"
                            value={shelf.identifier}
                            onChange={(e) =>
                                setShelf({
                                    ...shelf,
                                    identifier: e.target.value
                                })
                            }
                            className="w-full h-12 px-4 rounded-xl border"
                        />

                        {errors.identifier && (
                            <p className="text-red-500 text-xs mt-1">
                                {errors.identifier}
                            </p>
                        )}
                    </div>

                    <div>
                        <input
                            placeholder="Shelf Name"
                            value={shelf.name}
                            onChange={(e) =>
                                setShelf({
                                    ...shelf,
                                    name: e.target.value
                                })
                            }
                            className="w-full h-12 px-4 rounded-xl border"
                        />

                        {errors.name && (
                            <p className="text-red-500 text-xs mt-1">
                                {errors.name}
                            </p>
                        )}
                    </div>

                    <div className="flex justify-end gap-3 pt-3">
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
                            Save Shelf
                        </button>
                    </div>

                </form>

            </div>

        </div>
    );
};

AddShelf.propTypes = {
    closeModal: PropTypes.func.isRequired,
    refreshData: PropTypes.func.isRequired
};

export default AddShelf;