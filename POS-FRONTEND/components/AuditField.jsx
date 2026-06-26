import React from 'react'
import PropTypes from 'prop-types'

const AuditField = ({ auditField }) => {
    const formatDate = (dateString) => {
        if (!dateString) return "N/A";
        const date = new Date(dateString);
        return Number.isNaN(date.getTime()) ? dateString : date.toLocaleString();
    };
    return (
        <div className="w-full mt-8 pt-6 border-t border-gray-200">
            <h3 className="text-xs font-semibold text-gray-400 uppercase tracking-wider mb-3">
                Audit Information
            </h3>
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4 bg-gray-50 p-4 rounded-xl border border-gray-100">
                <div>
                    <p className="text-xs text-gray-500 font-medium">Created By</p>
                    <p className="text-sm text-gray-800 mt-0.5 font-semibold">{auditField.createdBy || "N/A"}</p>
                </div>
                <div>
                    <p className="text-xs text-gray-500 font-medium">Created On</p>
                    <p className="text-sm text-gray-600 mt-0.5">{formatDate(auditField.createdOn)}</p>
                </div>
                <div>
                    <p className="text-xs text-gray-500 font-medium">Modified By</p>
                    <p className="text-sm text-gray-800 mt-0.5 font-semibold">{auditField.modifiedBy || "N/A"}</p>
                </div>
                <div>
                    <p className="text-xs text-gray-500 font-medium">Modified On</p>
                    <p className="text-sm text-gray-600 mt-0.5">{formatDate(auditField.modifiedOn)}</p>
                </div>
            </div>
        </div>
    )
}

AuditField.propTypes = {
    auditField: PropTypes.shape({
        createdBy: PropTypes.string,
        createdOn: PropTypes.oneOfType([PropTypes.string, PropTypes.instanceOf(Date), PropTypes.number]),
        modifiedBy: PropTypes.string,
        modifiedOn: PropTypes.oneOfType([PropTypes.string, PropTypes.instanceOf(Date), PropTypes.number])
    }).isRequired
}

export default AuditField
