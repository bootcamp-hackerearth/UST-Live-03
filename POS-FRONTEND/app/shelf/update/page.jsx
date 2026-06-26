'use client';

import { useEffect, useState } from 'react';
import PropTypes from 'prop-types';
import api from '@/app/services/api';

const UpdateShelf = ({
    data,
    closeModal,
    refreshData
}) => {

    const [shelf, setShelf] = useState(data);

    useEffect(() => {

        const loadShelf = async () => {

            const response = await api.get(
                `/shelf/get?identifier=${data.identifier}`
            );

            setShelf(response.data);
        };

        if (data?.identifier) {
            loadShelf();
        }

    }, [data]);

    const handleSubmit = async (e) => {

        e.preventDefault();

        try {

            await api.put(
                '/shelf/update',
                shelf
            );

            alert('Shelf Updated');

            refreshData();
            closeModal();

        } catch (err) {

            console.error(err);
            alert('Update Failed');
        }
    };

    if (!shelf) return null;

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm">

            <div className="bg-white rounded-2xl border-t-4 border-cyan-500 shadow-lg w-full max-w-lg">

                <div className="px-8 py-6 border-b bg-slate-50">
                    <h2 className="text-2xl font-bold">
                        Update Shelf
                    </h2>
                </div>

                <form
                    onSubmit={handleSubmit}
                    className="p-6 space-y-4"
                >

                    <div>
                        <label
                            htmlFor="identifier"
                            className="text-xs text-slate-500">
                            Identifier
                        </label>

                        <input
                            value={shelf.identifier || ''}
                            readOnly
                            className="w-full h-12 px-4 rounded-xl border bg-slate-100"
                        />
                    </div>

                    <div>
                        <label 
                            htmlFor="shelfname"
                            className="text-xs text-slate-500">
                            Shelf Name
                        </label>

                        <input
                            value={shelf.name || ''}
                            onChange={(e) =>
                                setShelf({
                                    ...shelf,
                                    name: e.target.value
                                })
                            }
                            className="w-full h-12 px-4 rounded-xl border"
                        />
                    </div>

                    <div>
                        <label 
                            htmlFor="status"
                            className="text-xs text-slate-500">
                            Status
                        </label>

                        <select
                            value={shelf.status}
                            onChange={(e) =>
                                setShelf({
                                    ...shelf,
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
                            Update Shelf
                        </button>

                    </div>

                </form>

            </div>

        </div>
    );
};

UpdateShelf.propTypes = {
    data: PropTypes.shape({
        identifier: PropTypes.string,
        name: PropTypes.string,
        status: PropTypes.bool
    }),
    closeModal: PropTypes.func.isRequired,
    refreshData: PropTypes.func.isRequired
};

export default UpdateShelf;