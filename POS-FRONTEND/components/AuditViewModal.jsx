'use client';

import PropTypes from 'prop-types';

const AuditViewModal = ({ data, onClose }) => {

    const formatDate = (value) => {

        if (!value) return 'N/A';

        return new Date(value).toLocaleString(
            'en-IN',
            {
                day: '2-digit',
                month: 'short',
                year: 'numeric',
                hour: '2-digit',
                minute: '2-digit'
            }
        );
    };

    return (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">

            <div className="bg-white rounded-3xl w-full max-w-3xl shadow-2xl overflow-hidden">

                <div className="flex justify-between items-center p-5 border-b">

                    <div>
                        <h2 className="text-2xl font-bold text-slate-800">
                            Audit Details
                        </h2>

                        <p className="text-sm text-slate-500 mt-1">
                            Creation and modification information
                        </p>
                    </div>

                    <button
                        onClick={onClose}
                        className="text-red-500"
                    >
                        ✕
                    </button>

                </div>

                <div className="p-6">

                    <div className="grid grid-cols-2 gap-6">

                        <div className="bg-slate-50 border border-slate-200 rounded-xl p-4">
                            <p className="text-xs font-semibold text-slate-500 uppercase">
                                Identifier
                            </p>
                            <p className="mt-1 text-lg font-semibold text-slate-800">
                                {data.identifier}
                            </p>
                        </div>

                        <div className="bg-cyan-50 border border-cyan-200 rounded-xl p-4">
                            <p className="text-xs font-semibold text-cyan-600 uppercase">
                                Created By
                            </p>
                            <p className="mt-1 text-lg font-semibold text-slate-800">
                                {data.createdBy || 'N/A'}
                            </p>
                        </div>

                        <div className="bg-emerald-50 border border-emerald-200 rounded-xl p-4">
                            <p className="text-xs font-semibold text-emerald-600 uppercase">
                                Created On
                            </p>
                            <p className="mt-1 text-lg font-semibold text-slate-800">
                                {formatDate(data.createdOn)}
                            </p>
                        </div>

                        <div className="bg-violet-50 border border-violet-200 rounded-xl p-4">
                            <p className="text-xs font-semibold text-violet-600 uppercase">
                                Modified By
                            </p>
                            <p className="mt-1 text-lg font-semibold text-slate-800">
                                {data.modifiedBy || 'N/A'}
                            </p>
                        </div>

                        <div className="bg-amber-50 border border-amber-200 rounded-xl p-4 col-span-2">
                            <p className="text-xs font-semibold text-amber-600 uppercase">
                                Modified On
                            </p>
                            <p className="mt-1 text-lg font-semibold text-slate-800">
                                {formatDate(data.modifiedOn)}
                            </p>
                        </div>

                    </div>

                </div>

            </div>

        </div>
    );
};

AuditViewModal.propTypes = {
    data: PropTypes.object.isRequired,
    onClose: PropTypes.func.isRequired
};

export default AuditViewModal;