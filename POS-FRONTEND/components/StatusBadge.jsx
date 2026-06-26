"use client";

import React from "react";
import PropTypes from "prop-types";

export default function StatusBadge({ status }) {
    return (
        <span
            className={`px-2 py-0.5 rounded-md border text-[10px] font-bold uppercase tracking-wide ${status
                    ? "bg-emerald-50 text-[#10b981] border-emerald-200"
                    : "bg-[#fff2f2] text-[#e55555] border-[#ffd6d6]"
                }`}
        >
            {status ? "Active" : "Inactive"}
        </span>
    );
}

StatusBadge.propTypes = {
    status: PropTypes.oneOfType([PropTypes.bool, PropTypes.string, PropTypes.number]),
};
