"use client";

import React, { useState, useCallback } from "react";
import PropTypes from "prop-types";
import { useDropdownPosition, useDropdownOptions, dropdownStyles, DropdownChevron, DropdownMenu } from "./DropdownShared";

const P = "sd2";

export default function SingleDropdown({
  label,
  apiUrl,
  selectedValue,
  onChange,
  valueField = "identifier",
  labelField = "identifier",
  filterOut = null,
}) {
  const [isOpen, setIsOpen] = useState(false);
  const closeDropdown = useCallback(() => setIsOpen(false), []);
  const { boxRef, wrapRef, menuPos, recalculate } = useDropdownPosition(closeDropdown);
  const { options, loading } = useDropdownOptions({ apiUrl, valueField, labelField, filterOut });

  const handleToggle = () => {
    if (!isOpen) recalculate();
    setIsOpen((prev) => !prev);
  };

  const handleSelect = (value) => {
    onChange(value === selectedValue ? "" : value);
    setIsOpen(false);
  };

  if (loading) {
    return <p style={{ fontSize: "12px", color: "#6b7280", marginBottom: "6px" }}>Loading {label}...</p>;
  }

  const optionItems = options.length === 0
    ? <div className={`${P}-item`}>No items found</div>
    : options.map((opt) => (
      <button
        key={opt.value}
        type="button"
        className={`${P}-item${selectedValue === opt.value ? " selected" : ""}`}
        onClick={() => handleSelect(opt.value)}
      >
        <span aria-hidden="true" style={{ width: 18, display: "inline-block", textAlign: "center" }}>
          {selectedValue === opt.value ? "✓" : ""}
        </span>
        {opt.label}
      </button>
    ));

  return (
    <div className={`${P}-wrap`} ref={wrapRef}>
      <style>{dropdownStyles(P)}</style>
      <label className={`${P}-label`}>{label}</label>
      <button
        ref={boxRef}
        type="button"
        className={`${P}-box${isOpen ? " open" : ""}`}
        onClick={handleToggle}
        aria-expanded={isOpen}
        aria-haspopup="listbox"
      >
        <span>
          {selectedValue
            ? options.find((o) => o.value === selectedValue)?.label ?? selectedValue
            : <span style={{ color: "#9ca3af" }}>Select...</span>}
        </span>
        <DropdownChevron prefix={P} isOpen={isOpen} />
      </button>
      <DropdownMenu prefix={P} isOpen={isOpen} menuPos={menuPos} label={label}>
        {optionItems}
      </DropdownMenu>
    </div>
  );
}

SingleDropdown.propTypes = {
  label: PropTypes.string.isRequired,
  apiUrl: PropTypes.string.isRequired,
  selectedValue: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  onChange: PropTypes.func.isRequired,
  valueField: PropTypes.string,
  labelField: PropTypes.string,
  filterOut: PropTypes.any,
};