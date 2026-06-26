// components/shared/FormShared.jsx

"use client";

import PropTypes from "prop-types";

export function formatDateTime(value) {
    if (!value) return "—";
    try {
        if (Array.isArray(value)) {
            const [y, mo, d, h = 0, mi = 0, s = 0] = value;
            return new Date(y, mo - 1, d, h, mi, s).toLocaleString("en-IN", { dateStyle: "medium", timeStyle: "short" });
        }
        return new Date(value).toLocaleString("en-IN", { dateStyle: "medium", timeStyle: "short" });
    } catch { return String(value); }
}

export function AuditTrail({ audit }) {
    if (!audit?.createdBy && !audit?.createdAt) return null;
    return (
        <div className="bg-white rounded-xl border border-gray-100 shadow-sm p-6">
            <p className="text-[10px] font-bold text-[#006E74] uppercase tracking-widest mb-4">Audit Trail</p>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div className="flex items-start gap-3 bg-[#006E74]/4 rounded-xl px-4 py-3 border border-[#006E74]/10">
                    <div className="w-7 h-7 rounded-lg bg-[#006E74]/10 flex items-center justify-center shrink-0">
                        <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="#006E74" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                            <circle cx="12" cy="12" r="10" /><polyline points="12 6 12 12 16 14" />
                        </svg>
                    </div>
                    <div>
                        <p className="text-[9px] font-bold text-[#006E74]/60 uppercase tracking-widest">Created</p>
                        <p className="text-xs font-semibold text-[#231F20] mt-0.5">{audit.createdBy || "—"}</p>
                        <p className="text-[10px] text-gray-400 font-mono mt-0.5">{formatDateTime(audit.createdAt)}</p>
                    </div>
                </div>
                <div className="flex items-start gap-3 bg-gray-50 rounded-xl px-4 py-3 border border-gray-100">
                    <div className="w-7 h-7 rounded-lg bg-gray-200 flex items-center justify-center shrink-0">
                        <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="#6B7280" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                            <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" />
                            <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" />
                        </svg>
                    </div>
                    <div>
                        <p className="text-[9px] font-bold text-gray-400 uppercase tracking-widest">Last Modified</p>
                        <p className="text-xs font-semibold text-[#231F20] mt-0.5">{audit.modifiedBy || "Not yet modified"}</p>
                        <p className="text-[10px] text-gray-400 font-mono mt-0.5">{formatDateTime(audit.modifiedAt)}</p>
                    </div>
                </div>
            </div>
        </div>
    );
}

AuditTrail.propTypes = {
    audit: PropTypes.shape({
        createdBy: PropTypes.string,
        createdAt: PropTypes.any,
        modifiedBy: PropTypes.string,
        modifiedAt: PropTypes.any,
    }),
};

export function FormFooter({ onCancel, loading, submitLabel }) {
    return (
        <div className="sticky bottom-0 bg-slate-50 pt-4 border-t border-gray-200 flex justify-end gap-3">
            <button
                type="button"
                onClick={onCancel}
                className="px-6 py-2.5 text-sm border border-gray-200 rounded-lg text-gray-600 hover:bg-gray-100 transition-colors cursor-pointer bg-white"
            >
                Cancel
            </button>
            <button
                type="submit"
                disabled={loading}
                className="px-6 py-2.5 text-sm rounded-lg text-white font-semibold bg-[#006E74] hover:bg-[#0097AC] disabled:bg-gray-300 disabled:cursor-not-allowed transition-colors cursor-pointer border-none"
            >
                {loading ? "Saving..." : submitLabel}
            </button>
        </div>
    );
}

FormFooter.propTypes = {
    onCancel: PropTypes.func.isRequired,
    loading: PropTypes.bool.isRequired,
    submitLabel: PropTypes.string.isRequired,
};

export function ErrorBanner({ message }) {
    if (!message) return null;
    return (
        <div role="alert" className="mb-5 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg text-sm flex items-center gap-2">
            ⚠️ {message}
        </div>
    );
}

ErrorBanner.propTypes = { message: PropTypes.string };

export function SuccessBanner({ message }) {
    if (!message) return null;
    return (
        <output className="block mb-5 bg-emerald-50 border border-emerald-200 text-emerald-700 px-4 py-3 rounded-lg text-sm">
            ✓ {message}
        </output>
    );
}

SuccessBanner.propTypes = { message: PropTypes.string };

export function handleSubmitResponse({
    res,
    successMessage,
    setError,
    setSuccess,
    setAudit,
    router,
    delay = 1500,
}) {
    if (res.data?.success === false) {
        setError(res.data.message || "Operation failed.");
        return false;
    }
    setSuccess(successMessage);
    if (setAudit) {
        setAudit((p) => ({
            ...p,
            modifiedBy: res.data?.modifiedBy ?? p.modifiedBy,
            modifiedAt: res.data?.modifiedAt ?? p.modifiedAt,
        }));
    }
    setTimeout(() => router.back(), delay);
    return true;
}