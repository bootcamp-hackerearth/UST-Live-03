"use client";

import PropTypes from "prop-types";
import { FieldWrapper, INPUT_CLS } from "./formUtils";

export default function AddressSection({ title, prefix, values, onChange }) {
    const fields = [
        { key: "addressLine", label: "Address" },
        { key: "city", label: "City" },
        { key: "state", label: "State" },
        { key: "zip", label: "Postal Code" },
        { key: "country", label: "Country" },
    ];
    return (
        <>
            <div className="md:col-span-2">
                <h3 className="text-sm font-bold text-slate-700 mb-1">{title}</h3>
                <div className="border-t border-slate-100" />
            </div>
            {fields.map(({ key, label }) => (
                <FieldWrapper key={key} label={label}>
                    <input
                        type="text"
                        value={values[key] || ""}
                        onChange={(e) => onChange(prefix, key, e.target.value)}
                        placeholder={`Enter ${label.toLowerCase()}`}
                        className={INPUT_CLS}
                    />
                </FieldWrapper>
            ))}
        </>
    );
}
AddressSection.propTypes = {
    title: PropTypes.string.isRequired,
    prefix: PropTypes.string.isRequired,
    values: PropTypes.shape({
        addressLine: PropTypes.string,
        city: PropTypes.string,
        state: PropTypes.string,
        zip: PropTypes.string,
        country: PropTypes.string,
    }).isRequired,
    onChange: PropTypes.func.isRequired,
};
