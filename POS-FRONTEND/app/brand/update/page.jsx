'use client';

import { useEffect, useState } from 'react';
import PropTypes from 'prop-types';

import CommonUpdate from '@/components/UpdatePage';
import api from '@/app/services/api';

const UpdateBrand = ({
    data,
    closeModal,
    refreshData
}) => {

    const [message, setMessage] = useState('');

    const [brand, setBrand] = useState({
        identifier: '',
        name: '',
        description: ''
    });

    useEffect(() => {

        if (data) {

            setBrand({
                identifier: data.identifier || '',
                name: data.name || '',
                description: data.description || ''
            });

        }

    }, [data]);

    const handleChange = (e) => {

        const { name, value } = e.target;

        setBrand(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleSubmit = async (e) => {

        e.preventDefault();

        try {

            const response = await api.put(
                '/brand/update',
                brand
            );

            setMessage(
                response.data.message ||
                'Brand updated successfully'
            );

            refreshData?.();

            setTimeout(() => {
                closeModal?.();
            }, 500);

        } catch (err) {

            console.error(err);
            setMessage('Failed to update brand');

        }
    };

    return (

        <div className="flex flex-col bg-white rounded-2xl overflow-hidden border-t-4 border-cyan-500 shadow-lg">

            <div className="px-8 pt-6 pb-5 border-b">
                <h2 className="text-2xl font-bold">
                    Update Brand
                </h2>
            </div>

            <form
                id="update-brand-form"
                onSubmit={handleSubmit}
                className="p-8 space-y-6"
            >

                {message && (
                    <div className="bg-cyan-50 border border-cyan-200 text-cyan-600 rounded-xl px-4 py-3">
                        {message}
                    </div>
                )}

                <CommonUpdate
                    data={brand}
                    handleChange={handleChange}
                    showIdentifier={true}
                    showName={true}
                    identifierReadOnly={true}
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
                </div>

            </form>

            <div className="px-8 py-5 border-t flex justify-end gap-3">

                <button
                    type="button"
                    onClick={closeModal}
                    className="px-6 py-2 rounded-xl border"
                >
                    Cancel
                </button>

                <button
                    type="submit"
                    form="update-brand-form"
                    className="px-6 py-2 rounded-xl bg-cyan-500 text-white"
                >
                    Update Brand
                </button>

            </div>

        </div>
    );
};

UpdateBrand.propTypes = {
    data: PropTypes.object,
    closeModal: PropTypes.func,
    refreshData: PropTypes.func
};

export default UpdateBrand;