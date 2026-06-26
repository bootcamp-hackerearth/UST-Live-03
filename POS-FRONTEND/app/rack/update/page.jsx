'use client';

import { useState, useEffect } from 'react';
import PropTypes from 'prop-types';
import api from '@/app/services/api';

const UpdateRack = ({
    data,
    closeModal,
    refreshData
}) => {

    const [rack, setRack] = useState(data);
    const [shelves, setShelves] = useState([]);

    useEffect(() => {

        fetchShelves();

        const fetchRack = async () => {

            try {

                const response =
                    await api.get(
                        `/rack/get?identifier=${data.identifier}`
                    );

                setRack(response.data);

            } catch (err) {

                console.error(err);
            }
        };

        if (data?.identifier) {
            fetchRack();
        }

    }, [data]);

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

    const handleSubmit = async (e) => {

        e.preventDefault();

        try {

            await api.put(
                '/rack/update',
                rack
            );

            alert('Rack Updated');

            refreshData();
            closeModal();

        } catch (err) {

            console.error(err);

            alert('Update Failed');
        }
    };

    if (!rack) return null;

    return (
        <div className="bg-white rounded-2xl border-t-4 border-cyan-500 shadow-lg overflow-hidden">

            <div className="px-8 py-6 border-b bg-slate-50">
                <h2 className="text-2xl font-bold text-slate-800">
                    Update Rack
                </h2>
            </div>

            <form
                onSubmit={handleSubmit}
                className="p-8 space-y-5"
            >

                <input
                    value={rack.identifier || ''}
                    readOnly
                    className="w-full h-12 px-4 rounded-xl border bg-slate-100"
                />

                <input
                    name="name"
                    value={rack.name || ''}
                    onChange={handleChange}
                    placeholder="Rack Name"
                    className="w-full h-12 px-4 rounded-xl border"
                />

                <select
                    multiple
                    value={rack.shelfs || []}
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

                <select
                    name="status"
                    value={rack.status}
                    onChange={(e) =>
                        setRack({
                            ...rack,
                            status:
                                e.target.value === 'true'
                        })
                    }
                    className="w-full h-12 px-4 rounded-xl border"
                >
                    <option value="true">
                        Active
                    </option>

                    <option value="false">
                        Inactive
                    </option>
                </select>

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
                        Update Rack
                    </button>

                </div>

            </form>

        </div>
    );
};

UpdateRack.propTypes = {
    data: PropTypes.shape({
        identifier: PropTypes.string,
        name: PropTypes.string,
        shelfs: PropTypes.arrayOf(
            PropTypes.string
        ),
        status: PropTypes.bool
    }).isRequired,
    closeModal: PropTypes.func.isRequired,
    refreshData: PropTypes.func.isRequired
};

export default UpdateRack;