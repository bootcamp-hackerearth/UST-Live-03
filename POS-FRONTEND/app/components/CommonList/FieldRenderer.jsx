"use client";

import PropTypes from "prop-types";
import Select from "react-select";

const FieldRenderer = ({
  field,
  item,
  setItem,
  selectStyles
}) => {

  if (field.type === "select") {
    return (
      <Select
        styles={selectStyles}
        options={field.options || []}
        isSearchable={true}
        isMulti={field.multiple}

        onChange={(selected) => {
          const value = field.multiple
            ? selected?.map(x => x.value) || []
            : selected?.value || "";

          setItem({
            ...item,
            [field.name]: value
          });
        }}
      />
    );
  }

  return (
    <input
      className="inputField"
      placeholder={field.label}
      value={item?.[field.name] || ""}

      onChange={(e) => {
        setItem({
          ...item,
          [field.name]: e.target.value
        });
      }}
    />
  );
};

FieldRenderer.propTypes = {
  field: PropTypes.shape({
    name: PropTypes.string.isRequired,
    label: PropTypes.string,
    type: PropTypes.string,
    multiple: PropTypes.bool,
    options: PropTypes.arrayOf(
      PropTypes.shape({
        label: PropTypes.string,
        value: PropTypes.any,
      })
    ),
  }).isRequired,

  item: PropTypes.object,

  setItem: PropTypes.func.isRequired,

  selectStyles: PropTypes.object,
};

export default FieldRenderer;