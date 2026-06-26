import PropTypes from "prop-types";
import { labelStyle, inputStyle, sectionStyle } from "./customerFormStyles";

export function AddressSection({ title, prefix, values, onChange }) {
  const fields = [
    { key: "street", label: "Street" },
    { key: "city", label: "City" },
    { key: "state", label: "State" },
    { key: "postalCode", label: "Postal Code" },
    { key: "country", label: "Country" },
  ];

  return (
    <div style={sectionStyle}>
      <h3 style={{ margin: 0, fontSize: "13px", fontWeight: 700, color: "#1e293b" }}>{title}</h3>
      <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "10px" }}>
        {fields.map(({ key, label }) => {
          const inputId = `${prefix}-${key}`;
          return (
            <div key={key}>
              <label htmlFor={inputId} style={labelStyle}>
                {label}
              </label>
              <input
                id={inputId}
                type="text"
                value={values[key] || ""}
                onChange={(e) => onChange(prefix, key, e.target.value)}
                style={inputStyle}
              />
            </div>
          );
        })}
      </div>
    </div>
  );
}

AddressSection.propTypes = {
  title: PropTypes.string.isRequired,
  prefix: PropTypes.string.isRequired,
  values: PropTypes.object.isRequired,
  onChange: PropTypes.func.isRequired,
};
