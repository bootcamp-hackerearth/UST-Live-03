// components/brand/BrandShared.jsx

"use client";

import PropTypes from "prop-types";
import { Upload, X } from "lucide-react";

export { formatDateTime, AuditTrail, FormFooter, ErrorBanner, SuccessBanner } from "@/components/shared/FormShared";

export const inputClass = "w-full px-3 py-2.5 border border-gray-200 rounded-lg text-sm focus:outline-none focus:border-[#006E74] focus:ring-2 focus:ring-[#006E74]/10 bg-white text-[#231F20]";
export const labelClass = "block text-[10px] font-bold text-[#006E74] uppercase tracking-widest mb-1.5";

export function BrandPageHeader({ title, subtitle, icon: Icon }) {
    return (
        <div className="flex items-center gap-3 mb-8">
            <div className="w-10 h-10 rounded-xl bg-[#006E74]/10 flex items-center justify-center">
                <Icon size={20} className="text-[#006E74]" />
            </div>
            <div>
                <h1 className="text-2xl font-bold text-[#231F20]">{title}</h1>
                <p className="text-xs text-gray-400 mt-0.5">{subtitle}</p>
            </div>
        </div>
    );
}

BrandPageHeader.propTypes = {
    title: PropTypes.string.isRequired,
    subtitle: PropTypes.string.isRequired,
    icon: PropTypes.elementType.isRequired,
};

export function IconUploadBlock({ iconFile, iconPreview, fileRef, onFileChange, onClear, isEdit }) {
    let buttonLabel;
    if (iconFile) buttonLabel = "Change icon";
    else if (isEdit) buttonLabel = "Replace icon";
    else buttonLabel = "Upload icon";

    return (
        <div className="mt-5">
            <label htmlFor="brandIcon" className={labelClass}>Brand Icon</label>
            <div className="flex items-center gap-4">
                <div className="w-16 h-16 rounded-xl border-2 border-dashed border-gray-200 bg-gray-50 flex items-center justify-center overflow-hidden shrink-0">
                    {iconPreview ? (
                        <img src={iconPreview} alt="Brand icon preview" className="w-full h-full object-contain" />
                    ) : (
                        <Upload size={20} className="text-gray-300" />
                    )}
                </div>
                <div className="flex flex-col gap-2">
                    <button
                        type="button"
                        onClick={() => fileRef.current?.click()}
                        className="flex items-center gap-2 px-4 py-2 text-xs font-semibold text-[#006E74] border border-[#006E74]/30 rounded-lg hover:bg-[#006E74]/8 transition-colors cursor-pointer bg-white"
                    >
                        <Upload size={13} />
                        {buttonLabel}
                    </button>
                    {iconFile && (
                        <button
                            type="button"
                            onClick={onClear}
                            className="flex items-center gap-1.5 px-4 py-2 text-xs font-semibold text-gray-400 hover:text-red-500 transition-colors cursor-pointer bg-transparent border-none"
                        >
                            <X size={12} /> {isEdit ? "Revert" : "Remove"}
                        </button>
                    )}
                    {iconFile && (
                        <p className="text-[10px] text-gray-400 truncate max-w-[180px]">{iconFile.name}</p>
                    )}
                </div>
            </div>
            <input id="brandIcon" ref={fileRef} type="file" accept="image/*" onChange={onFileChange} className="hidden" />
        </div>
    );
}

IconUploadBlock.propTypes = {
    iconFile: PropTypes.object,
    iconPreview: PropTypes.string,
    fileRef: PropTypes.object.isRequired,
    onFileChange: PropTypes.func.isRequired,
    onClear: PropTypes.func.isRequired,
    isEdit: PropTypes.bool,
};