"use client";

import React from "react";
import PropTypes from "prop-types";

export default function EntityAuditHistory({ activeItem }) {
  const creator = activeItem?.createdBy;
  const createdDate = activeItem?.createdOn;
  const modifier = activeItem?.modifiedBy;
  const modifiedDate = activeItem?.modifiedOn;

  if (!creator && !modifier) return null;

  const formatDate = (dateString) => {
    if (!dateString) 
      return "N/A";
    return new Date(dateString).toLocaleString(undefined, { dateStyle: "short", timeStyle: "medium",});
  };

  return (
    <div className="w-full bg-slate-50 border border-slate-200/60 rounded-xl p-4 mb-6 grid grid-cols-1 sm:grid-cols-2 gap-4 text-[11px]">
      <div className="space-y-1">
        <span className="text-[10px] uppercase font-bold tracking-wider text-slate-400 block mb-1">
          Created Details
        </span>
        <div className="text-slate-700">
          <span className="font-semibold text-slate-500">Created By :</span> {creator || "System"}
        </div>
        <div className="text-slate-700">
          <span className="font-semibold text-slate-500">Created On :</span> {formatDate(createdDate)}
        </div>
      </div>

      <div className="space-y-1 border-t sm:border-t-0 sm:border-l border-slate-200 pt-3 sm:pt-0 sm:pl-4">
        <span className="text-[10px] uppercase font-bold tracking-wider text-slate-400 block mb-1">
          Modified Details
        </span>
        <div className="text-slate-700">
          <span className="font-semibold text-slate-500">Modified By :</span> {modifier || "Not modified"}
        </div>
        <div className="text-slate-700">
          <span className="font-semibold text-slate-500">Modified On :</span> {formatDate(modifiedDate)}
        </div>
      </div>
    </div>
  );
}


EntityAuditHistory.propTypes = {
  activeItem: PropTypes.shape({
    createdBy: PropTypes.string,
    created_by: PropTypes.string,
    createdOn: PropTypes.string,
    created_on: PropTypes.string,
    modifiedBy: PropTypes.string,
    modified_by: PropTypes.string,
    modifiedOn: PropTypes.string,
    modified_on: PropTypes.string,
  })
};
