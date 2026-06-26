'use client';

import { useState, useEffect } from 'react';
import PropTypes from 'prop-types';
import api from '@/app/services/api';

const AddRack = ({ closeModal, refreshData }) => {

    const [shelves, setShelves] = useState([]);

    const [rack, setRack] = useState({
        identifier: '',
        name: '',
        shelfs: [],
        status: true
    });

    const [errors, setErrors] = useState({});

    useEffect(() => {
        fetchShelves();
    }, []);

    const fetchShelves = async () => {
        try {

            const response = await api.post('/shelf/list', {
                page: 0,
                sizePerPage: 100
            });

            setShelves(response.data.dtoList || []);

        } catch (err) {

            console.error(err);
        }
    };

    const handleChange = (e) => {

        const { name, value } = e.target;

        setRack({
            ...rack,
            [name]: value
        });

        setErrors(prev => ({
            ...prev,
            [name]: ''
        }));
    };

    const handleShelfChange = (e) => {

        const values = Array.from(
            e.target.selectedOptions,
            option => option.value
        );

        setRack({
            ...rack,
            shelfs: values
        });
    };

    const validate = () => {

        const newErrors = {};

    
        if (!rack.identifier.trim()) {
            newErrors.identifier = 'Identifier is required';
        } else if (rack.identifier.length < 3) {
            newErrors.identifier =
                'Identifier must contain at least 3 characters';
        } else if (!/^[A-Za-z0-9\s]+$/.test(rack.identifier)) {
            newErrors.identifier =
                'Only letters, numbers and spaces are allowed';
        }

        
        if (!rack.name.trim()) {
            newErrors.name = 'Rack Name is required';
        } else if (rack.name.length < 3) {
            newErrors.name =
                'Rack Name must contain at least 3 characters';
        } else if (!/^[A-Za-z\s]+$/.test(rack.name)) {
            newErrors.name =
                'Rack Name can contain only letters and spaces';
        }

        if (rack.shelfs.length === 0) {
            newErrors.shelfs = 'Select at least one shelf';
        }

        setErrors(newErrors);

        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e) => {

        e.preventDefault();

        if (!validate()) return;

        try {

            await api.post('/rack/add', rack);

            alert('Rack Added');

            refreshData();
            closeModal();

        } catch (err) {

            console.error(err);

            alert(
                err.response?.data?.message ||
                'Failed to add rack'
            );
        }
    };

    return (
        <div className="bg-white rounded-2xl border-t-4 border-cyan-500 shadow-lg overflow-hidden">

            <div className="px-8 py-6 border-b bg-slate-50">
                <h2 className="text-2xl font-bold text-slate-800">
                    Add Rack
                </h2>
            </div>

            <form
                onSubmit={handleSubmit}
                className="p-8 space-y-5"
            >

                <input
                    name="identifier"
                    value={rack.identifier}
                    onChange={handleChange}
                    placeholder="Identifier"
                    className="w-full h-12 px-4 rounded-xl border"
                />


                {errors.identifier && (
                    <p className="text-red-500 text-xs mt-1">
                        {errors.identifier}
                    </p>
                )}

                <input
                    name="name"
                    value={rack.name}
                    onChange={handleChange}
                    placeholder="Rack Name"
                    className="w-full h-12 px-4 rounded-xl border"
                />

                {errors.name && (
                    <p className="text-red-500 text-xs mt-1">
                        {errors.name}
                    </p>
                )}

                <select
                    multiple
                    value={rack.shelfs}
                    onChange={handleShelfChange}
                    className="w-full min-h-[150px] px-4 py-3 rounded-xl border"
                >
                    {shelves.map((shelf) => (
                        <option
                            key={shelf.identifier}
                            value={shelf.identifier}
                        >
                            {shelf.identifier} - {shelf.name}
                        </option>
                    ))}
                </select>

                {errors.shelfs && (
                    <p className="text-red-500 text-xs">
                        {errors.shelfs}
                    </p>
                )}

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
                        Save Rack
                    </button>

                </div>

            </form>

        </div>
    );
};

AddRack.propTypes = {
    closeModal: PropTypes.func.isRequired,
    refreshData: PropTypes.func.isRequired
};

export default AddRack;