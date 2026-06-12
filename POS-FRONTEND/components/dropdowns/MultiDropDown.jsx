"use client";

import React, { useState, useCallback } from "react";
import PropTypes from "prop-types";
import { useDropdownPosition, useDropdownOptions, dropdownStyles, DropdownMenu } from "./DropdownShared";

const P = "md2";

export default function MultiDropDown({
  label,
  apiUrl,
  selectedValues,
  onChange,
  valueField = "identifier",
  labelField = "identifier",
}) {
  const [isOpen, setIsOpen] = useState(false);
  const closeDropdown = useCallback(() => setIsOpen(false), []);
  const { boxRef, wrapRef, menuPos, recalculate } = useDropdownPosition(closeDropdown);
  const { options, loading } = useDropdownOptions({ apiUrl, valueField, labelField });

  const handleToggle = () => {
    if (!isOpen) recalculate();
    setIsOpen((prev) => !prev);
  };

  const handleSelect = (value) => {
    const updated = selectedValues.includes(value)
      ? selectedValues.filter((v) => v !== value)
      : [...selectedValues, value];
    onChange(updated);
  };

  if (loading) {
    return <p style={{ fontSize: "12px", color: "#6b7280", marginBottom: "6px" }}>Loading {label}…</p>;
  }

  const optionItems = options.length === 0
    ? <div className={`${P}-item`}>No items found</div>
    : options.map((opt) => {
      const isSel = selectedValues.includes(opt.value);
      return (
        <button
          key={opt.value}
          type="button"
          className={`${P}-item${isSel ? " selected" : ""}`}
          onClick={() => handleSelect(opt.value)}
          onKeyDown={(e) => {
            if (e.key === "Enter" || e.key === " ") { e.preventDefault(); handleSelect(opt.value); }
          }}
        >
          <span style={{ width: 18, display: "inline-block", textAlign: "center" }}>
            {isSel ? "✓" : ""}
          </span>
          <span style={{ marginLeft: 6 }}>{opt.label}</span>
        </button>
      );
    });

  return (
    <>
      <style>{dropdownStyles(P)}</style>
      <div className={`${P}-wrap`} ref={wrapRef}>
        <label className={`${P}-label`}>{label}</label>
        <button
          type="button"
          ref={boxRef}
          className={`${P}-box${isOpen ? " open" : ""}`}
          onClick={handleToggle}
          onKeyDown={(e) => {
            if (e.key === "Enter" || e.key === " ") { e.preventDefault(); handleToggle(); }
          }}
          aria-expanded={isOpen}
          aria-haspopup="listbox"
        >
          <span>
            {selectedValues.length > 0
              ? selectedValues.map((v) => options.find((o) => o.value === v)?.label).filter(Boolean).join(", ")
              : <span style={{ color: "#9ca3af" }}>Select…</span>}
          </span>
          <DropdownMenu prefix={P} isOpen={isOpen} menuPos={menuPos} label={label}>
            {optionItems}
          </DropdownMenu>
        </button>
      </div>
    </>
  );
}

MultiDropDown.propTypes = {
  label: PropTypes.string.isRequired,
  apiUrl: PropTypes.string.isRequired,
  selectedValues: PropTypes.array.isRequired,
  onChange: PropTypes.func.isRequired,
  valueField: PropTypes.string,
  labelField: PropTypes.string,
};