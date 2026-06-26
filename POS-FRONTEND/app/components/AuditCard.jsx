"use client";

import PropTypes from "prop-types";
import { formatDateTime } from "../lib/formatDateTime";

export default function AuditCard({ title, byValue, atValue }) {
    return (
        <div className="flex flex-col gap-1">
            <span className="text-sm font-semibold text-slate-800">{title}</span>
            <div className="rounded-lg border border-slate-200 bg-white px-4 py-3 text-sm">
                <div className="flex items-center gap-4 text-slate-500">
                    <span className="w-4 shrink-0 font-medium">By</span>
                    <span className="font-medium text-slate-700">{byValue || "—"}</span>
                </div>
                <div className="mt-1.5 flex items-center gap-4 text-slate-500">
                    <span className="w-4 shrink-0 font-medium">At</span>
                    <span className="font-medium text-slate-700">{formatDateTime(atValue)}</span>
                </div>
            </div>
        </div>
    );
}

AuditCard.propTypes = {
    title: PropTypes.string.isRequired,
    byValue: PropTypes.string,
    atValue: PropTypes.string,
};

AuditCard.defaultProps = {
    byValue: null,
    atValue: null,
};
