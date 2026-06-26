'use client';

import { useEffect, useState } from 'react';
import PropTypes from 'prop-types';

import CommonUpdate from '@/components/UpdatePage';
import api from '@/app/services/api';

const UpdateWarehouse = ({
    data,
    closeModal,
    refreshData
}) => {

    const [message, setMessage] = useState('');

    const [warehouse, setWarehouse] = useState({
        identifier: '',
        name: '',
        region: '',
        city: '',
        state: '',
        country: '',
        capacity: '',
        contactName: '',
        contactNumber: ''
    });

    useEffect(() => {

        if (data) {

            setWarehouse({
                identifier: data.identifier || '',
                name: data.name || '',
                region: data.region || '',
                city: data.city || '',
                state: data.state || '',
                country: data.country || '',
                capacity: data.capacity || '',
                contactName: data.contactName || '',
                contactNumber: data.contactNumber || ''
            });

        }

    }, [data]);

    const handleChange = (e) => {

        const { name, value } = e.target;

        setWarehouse((prev) => ({
            ...prev,
            [name]: value
        }));
    };

    const handleSubmit = async (e) => {

        e.preventDefault();

        try {

            await api.put(
                '/warehouse/update',
                warehouse
            );

            setMessage(
                'Warehouse updated successfully'
            );

            refreshData?.();

            setTimeout(() => {
                closeModal?.();
            }, 500);

        } catch (error) {

            console.error(error);
            setMessage(
                'Failed to update warehouse'
            );

        }
    };

    return (

        <div className="flex flex-col bg-white rounded-2xl overflow-hidden max-h-[85vh] border-t-4 border-cyan-500 shadow-lg">

            <div className="px-8 pt-6 pb-5 border-b">
                <h2 className="text-2xl font-bold">
                    Update Warehouse
                </h2>
            </div>

            <form
                id="update-warehouse-form"
                onSubmit={handleSubmit}
                className="flex-1 overflow-y-auto px-8 py-6 space-y-5"
            >

                {message && (
                    <div className="bg-cyan-50 border border-cyan-200 text-cyan-700 rounded-xl px-4 py-3">
                        {message}
                    </div>
                )}

                <CommonUpdate
                    data={warehouse}
                    handleChange={handleChange}
                    showIdentifier={true}
                    showName={true}
                    identifierReadOnly={true}
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
                                value={warehouse[field] || ''}
                                onChange={handleChange}
                                className="w-full border rounded-xl px-4 py-3"
                            />
                        </div>
                    ))}
                </div>
            </form>

            <div className="px-8 py-5 border-t flex justify-end gap-3">

                <button
                    type="button"
                    onClick={closeModal}
                    className="px-6 py-2 border rounded-xl"
                >
                    Cancel
                </button>

                <button
                    type="submit"
                    form="update-warehouse-form"
                    className="px-6 py-2 bg-cyan-500 text-white rounded-xl"
                >
                    Update Warehouse
                </button>
            </div>
        </div>
    );
};

UpdateWarehouse.propTypes = {
    data: PropTypes.object,
    closeModal: PropTypes.func,
    refreshData: PropTypes.func
};

export default UpdateWarehouse;