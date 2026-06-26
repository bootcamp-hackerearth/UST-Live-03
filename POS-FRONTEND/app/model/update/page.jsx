'use client';

import { useState, useEffect } from 'react';
import PropTypes from 'prop-types';
import api from '@/app/services/api';

const UpdateModel = ({
    data,
    closeModal,
    refreshData
}) => {

    const [model, setModel] = useState(data);

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

    const handleSubmit = async (e) => {

        e.preventDefault();

        try {

            await api.put(
                '/model/update',
                model
            );

            alert('Model Updated');

            refreshData();
            closeModal();

        } catch (err) {

            console.error(err);

            alert(
                err.response?.data?.message ||
                'Update Failed'
            );
        }
    };

    if (!model) return null;

    return (
        <div className="bg-white rounded-2xl border-t-4 border-cyan-500 shadow-lg overflow-hidden">

            <div className="px-8 py-6 border-b bg-slate-50">
                <h2 className="text-2xl font-bold text-slate-800">
                    Update Model
                </h2>
            </div>

            <form
                onSubmit={handleSubmit}
                className="p-8 space-y-5"
            >

                <input
                    value={model.identifier || ''}
                    readOnly
                    className="w-full h-12 px-4 rounded-xl border bg-slate-100"
                />

                <input
                    name="name"
                    value={model.name || ''}
                    onChange={handleChange}
                    placeholder="Model Name"
                    className="w-full h-12 px-4 rounded-xl border"
                />

                <select
                    name="status"
                    value={model.status}
                    onChange={(e) =>
                        setModel({
                            ...model,
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
                        Update Model
                    </button>

                </div>

            </form>

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