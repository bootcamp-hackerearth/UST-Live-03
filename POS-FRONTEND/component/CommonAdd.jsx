"use client";

import { useState, useEffect, useCallback, useRef } from "react";
import { FiSave } from "react-icons/fi";
import PropTypes from "prop-types";
import api from "@/app/(main)/api/axios";

// ── Validators ────────────────────────────────────────────────────────────────
const validateName = (value) => {
  if (!value?.trim()) return "Name is required.";
  if (value.trim().length < 3)
    return "Name must be at least 3 characters long.";
  return null;
};

const validatePhone = (value) => {
  if (!value || !/^\d{10}$/.test(value))
    return "Phone number must contain exactly 10 digits.";
  return null;
};

const validateUsername = (value) => {
  const emailRegex = /^[^\s@]+@[^\s@.]+\.[^\s@]+$/;
  if (!value || !emailRegex.test(value)) return "Please enter a valid email.";
  return null;
};

const validatePassword = (value) => {
  if (!value?.trim()) return "Password is required.";
  if (value.length < 6) return "Password must be at least 6 characters long.";
  return null;
};

const validateRoles = (value) => {
  if (
    !value ||
    (Array.isArray(value) && value.length === 0) ||
    (typeof value === "string" && !value.trim())
  ) {
    return "Role is required.";
  }
  return null;
};

const validateField = (field, value) => {
  const { key, required, label, type } = field;

  const isEmpty =
    value === undefined ||
    value === null ||
    (typeof value === "string" && value.trim().length === 0) ||
    (Array.isArray(value) && value.length === 0);

  if (required && type !== "checkbox" && isEmpty) {
    return `${label} is required.`;
  }

  switch (key) {
    case "name":
      return validateName(value);
    case "phoneNo":
      return validatePhone(value);
    case "username":
      return validateUsername(value);
    case "password":
      return validatePassword(value);
    case "roles":
      return validateRoles(value);
    default:
      return null;
  }
};

// ── DropdownItem ──────────────────────────────────────────────────────────────
const DropdownItem = ({ item, onSelect }) => (
  <li>
    <button
      type="button"
      className="w-full text-left p-2 text-sm hover:bg-blue-600 hover:text-white border-b border-gray-100 last:border-none"
      onClick={() => onSelect(item)}
    >
      {item.identifier}
    </button>
  </li>
);
DropdownItem.propTypes = {
  item: PropTypes.shape({
    id: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    identifier: PropTypes.string.isRequired,
  }).isRequired,
  onSelect: PropTypes.func.isRequired,
};

// ── DropdownList ──────────────────────────────────────────────────────────────
const DropdownList = ({
  fieldKey,
  options,
  loadingField,
  loadingLabel,
  onSelect,
}) => {
  if (loadingField !== fieldKey && (!options || options.length === 0))
    return null;
  return (
    <ul className="absolute z-50 w-full bg-white border border-gray-200 rounded-md mt-1 shadow-lg max-h-60 overflow-auto">
      {loadingField === fieldKey ? (
        <li className="p-2 text-sm text-gray-400">{loadingLabel}</li>
      ) : (
        options.map((item, index) => (
          <DropdownItem
            key={item.id ?? index}
            item={item}
            onSelect={onSelect}
          />
        ))
      )}
    </ul>
  );
};
DropdownList.propTypes = {
  fieldKey: PropTypes.string.isRequired,
  options: PropTypes.array,
  loadingField: PropTypes.string,
  loadingLabel: PropTypes.string,
  onSelect: PropTypes.func.isRequired,
};
DropdownList.defaultProps = {
  options: [],
  loadingField: null,
  loadingLabel: "Loading...",
};

// ── MultiSearchChip ───────────────────────────────────────────────────────────
const MultiSearchChip = ({ value, onRemove }) => (
  <div className="bg-blue-100 text-blue-700 px-3 py-1 rounded-full text-sm flex items-center gap-2">
    <span>{value}</span>
    <button
      type="button"
      className="font-bold hover:text-red-600"
      onClick={onRemove}
    >
      ×
    </button>
  </div>
);
MultiSearchChip.propTypes = {
  value: PropTypes.string.isRequired,
  onRemove: PropTypes.func.isRequired,
};

// ── MultiSearchField (roles / shelves / racks) ───────────────────────────────
const MultiSearchField = ({
  field,
  formData,
  dropdownData,
  openDropdown,
  setOpenDropdown,
  searchTerms,
  setSearchTerms,
  loadingField,
  onSelect,
  onRemove,
  onFocusSearch,
}) => {
  const values = Array.isArray(formData[field.key]) ? formData[field.key] : [];
  const options = dropdownData[field.key] ?? [];

  return (
    <div className="relative">
      <div className="border border-gray-300 rounded-md p-2 bg-white">
        <div className="flex flex-wrap gap-2 mb-2">
          {values.map((value) => (
            <MultiSearchChip
              key={value}
              value={value}
              onRemove={() => onRemove(field, value)}
            />
          ))}
        </div>

        <input
          type="text"
          placeholder={`Search ${field.label}...`}
          value={searchTerms[field.key] || ""}
          onFocus={() => onFocusSearch(field)}
          onChange={(e) =>
            setSearchTerms((prev) => ({ ...prev, [field.key]: e.target.value }))
          }
          className="w-full outline-none"
        />
      </div>

      {openDropdown === field.key && (
        <DropdownList
          fieldKey={field.key}
          options={options}
          loadingField={loadingField}
          loadingLabel="Searching..."
          onSelect={(item) => onSelect(field, item)}
        />
      )}
    </div>
  );
};
MultiSearchField.propTypes = {
  field: PropTypes.object.isRequired,
  formData: PropTypes.object.isRequired,
  dropdownData: PropTypes.object.isRequired,
  openDropdown: PropTypes.string,
  setOpenDropdown: PropTypes.func.isRequired,
  searchTerms: PropTypes.object.isRequired,
  setSearchTerms: PropTypes.func.isRequired,
  loadingField: PropTypes.string,
  onSelect: PropTypes.func.isRequired,
  onRemove: PropTypes.func.isRequired,
  onFocusSearch: PropTypes.func.isRequired,
};

// ── GenericSearchField ────────────────────────────────────────────────────────
const GenericSearchField = ({
  field,
  formData,
  dropdownData,
  openDropdown,
  loadingField,
  onSelect,
  onChange,
  onFocusSearch,
}) => {
  const options = dropdownData[field.key] ?? [];

  return (
    <div className="relative">
      <input
        type="text"
        value={formData[field.key] || ""}
        onFocus={() => onFocusSearch(field)}
        onChange={(e) => onChange(field, e.target.value)}
        className="w-full p-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 outline-none"
      />

      {openDropdown === field.key && (
        <DropdownList
          fieldKey={field.key}
          options={options}
          loadingField={loadingField}
          loadingLabel="Searching..."
          onSelect={(item) => onSelect(field, item)}
        />
      )}
    </div>
  );
};
GenericSearchField.propTypes = {
  field: PropTypes.object.isRequired,
  formData: PropTypes.object.isRequired,
  dropdownData: PropTypes.object.isRequired,
  openDropdown: PropTypes.string,
  loadingField: PropTypes.string,
  onSelect: PropTypes.func.isRequired,
  onChange: PropTypes.func.isRequired,
  onFocusSearch: PropTypes.func.isRequired,
};

// ── CheckboxField ─────────────────────────────────────────────────────────────
const CheckboxField = ({ checked, onChange }) => (
  <div className="flex items-center gap-2">
    <input
      type="checkbox"
      checked={checked}
      onChange={(e) => onChange(e.target.checked)}
    />
    <span>Active</span>
  </div>
);
CheckboxField.propTypes = {
  checked: PropTypes.bool.isRequired,
  onChange: PropTypes.func.isRequired,
};

// ── TextField ─────────────────────────────────────────────────────────────────
const TextField = ({ field, value, onChange }) => (
  <input
    type={field.type}
    maxLength={field.key === "phoneNo" ? 10 : undefined}
    value={value}
    onChange={(e) => onChange(e.target.value)}
    className="w-full p-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 outline-none"
  />
);
TextField.propTypes = {
  field: PropTypes.object.isRequired,
  value: PropTypes.string.isRequired,
  onChange: PropTypes.func.isRequired,
};

// ── Main component ────────────────────────────────────────────────────────────
const CommonAdd = ({
  title,
  icon: TitleIcon,
  formData,
  setFormData,
  fields,
  onSubmit,
  validation,
  moduleName,
  submitLabel = "Add",
}) => {
  const [dropdownData, setDropdownData] = useState({});
  const [openDropdown, setOpenDropdown] = useState(null);
  const [searchTerms, setSearchTerms] = useState({});
  const [loadingField, setLoadingField] = useState(null);
  const containerRef = useRef(null);

  // Close dropdown on any click outside the form container.
  // (Ref-based check replaces the old per-field stopPropagation div,
  // which needed fake interactive semantics for a11y.)
  useEffect(() => {
    const handleOutsideClick = (event) => {
      if (
        containerRef.current &&
        !containerRef.current.contains(event.target)
      ) {
        setOpenDropdown(null);
      }
    };
    document.addEventListener("mousedown", handleOutsideClick);
    return () => document.removeEventListener("mousedown", handleOutsideClick);
  }, []);

  const searchField = useCallback(
    async (field, term) => {
      if (!field.api) return;
      setLoadingField(field.key);
      try {
        const response = await api.post(field.api, {
          page: 0,
          sizePerPage: 10,
          sortField: "identifier",
          search: term,
        });
        let results = response.data.dtoList || [];

        if (["roles", "shelves", "racks"].includes(field.key)) {
          const selected = Array.isArray(formData[field.key])
            ? formData[field.key]
            : [];
          results = results.filter(
            (item) => !selected.includes(item.identifier),
          );
        }

        if (field.filterFunction) {
          results = results.filter(field.filterFunction);
        }

        setDropdownData((prev) => ({ ...prev, [field.key]: results }));
      } catch (err) {
        console.error(err);
      } finally {
        setLoadingField(null);
      }
    },
    [formData],
  );

  const handleFocusSearch = useCallback(
    (field) => {
      setOpenDropdown(field.key);
      searchField(field, searchTerms[field.key] || "");
    },
    [searchField, searchTerms],
  );

  const handleMultiSelect = useCallback(
    (field, item) => {
      setFormData((prev) => {
        const existing = Array.isArray(prev[field.key]) ? prev[field.key] : [];
        if (existing.includes(item.identifier)) return prev;
        return { ...prev, [field.key]: [...existing, item.identifier] };
      });
      setOpenDropdown(null);
    },
    [setFormData],
  );

  const handleMultiRemove = useCallback(
    (field, value) => {
      setFormData((prev) => ({
        ...prev,
        [field.key]: (Array.isArray(prev[field.key])
          ? prev[field.key]
          : []
        ).filter((v) => v !== value),
      }));
      searchField(field, searchTerms[field.key] || "");
    },
    [setFormData, searchField, searchTerms],
  );

  const handleGenericSelect = useCallback(
    (field, item) => {
      setFormData((prev) => ({ ...prev, [field.key]: item.identifier }));
      setOpenDropdown(null);
    },
    [setFormData],
  );

  const handleGenericChange = useCallback(
    (field, val) => {
      setFormData((prev) => ({ ...prev, [field.key]: val }));
      setSearchTerms((prev) => ({ ...prev, [field.key]: val }));
    },
    [setFormData],
  );

  const handleTextChange = useCallback(
    (field, val) => {
      const cleaned = field.key === "phoneNo" ? val.replace(/\D/g, "") : val;
      setFormData((prev) => ({ ...prev, [field.key]: cleaned }));
    },
    [setFormData],
  );

  const handleCheckboxChange = useCallback(
    (field, checked) => {
      setFormData((prev) => ({ ...prev, [field.key]: checked }));
    },
    [setFormData],
  );

  const validateForm = () => {
    for (const field of fields) {
      const error = validateField(field, formData[field.key]);
      if (error) {
        alert(error);
        return false;
      }
    }
    return true;
  };

  const handleFormSubmit = async (e) => {
    e.preventDefault();
    if (!validateForm()) return;

    if (validation) {
      const error = validation(formData);
      if (error) {
        alert(error);
        return;
      }
    }

    try {
      let roles = formData.roles;
      if (roles !== undefined && !Array.isArray(roles)) roles = [roles];

      let shelves = formData.shelves;
      if (shelves !== undefined && !Array.isArray(shelves)) shelves = [shelves];

      let racks = formData.racks;
      if (racks !== undefined && !Array.isArray(racks)) racks = [racks];

      const payload = { ...formData, roles, shelves, racks };

      const response = await api.post(`/api/${moduleName}/add`, payload);
      if (response.data.success === false) {
        alert(response.data.message);
        return;
      }

      alert(`${title} added successfully`);
      if (onSubmit) onSubmit(response);
    } catch (err) {
      console.error(err);
      alert(err.response?.data?.message || "Failed to add record.");
    }
  };

  useEffect(() => {
    const timer = setTimeout(() => {
      fields
        .filter((field) => field.type === "search")
        .forEach((field) => {
          searchField(field, searchTerms[field.key] || "");
        });
    }, 300);
    return () => clearTimeout(timer);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [searchTerms, fields]);

  const renderField = (field) => {
    const isMultiSearch =
      field.type === "search" &&
      ["roles", "shelves", "racks"].includes(field.key);

    if (isMultiSearch) {
      return (
        <MultiSearchField
          field={field}
          formData={formData}
          dropdownData={dropdownData}
          openDropdown={openDropdown}
          setOpenDropdown={setOpenDropdown}
          searchTerms={searchTerms}
          setSearchTerms={setSearchTerms}
          loadingField={loadingField}
          onSelect={handleMultiSelect}
          onRemove={handleMultiRemove}
          onFocusSearch={handleFocusSearch}
        />
      );
    }

    if (field.type === "search") {
      return (
        <GenericSearchField
          field={field}
          formData={formData}
          dropdownData={dropdownData}
          openDropdown={openDropdown}
          loadingField={loadingField}
          onSelect={handleGenericSelect}
          onChange={handleGenericChange}
          onFocusSearch={handleFocusSearch}
        />
      );
    }

    if (field.type === "checkbox") {
      return (
        <CheckboxField
          checked={formData[field.key] || false}
          onChange={(checked) => handleCheckboxChange(field, checked)}
        />
      );
    }

    return (
      <TextField
        field={field}
        value={formData[field.key] || ""}
        onChange={(val) => handleTextChange(field, val)}
      />
    );
  };

  return (
    <div
      ref={containerRef}
      className="bg-white rounded-xl shadow-xl p-6 w-full max-w-lg text-gray-800"
    >
      <div className="mb-6">
        <h2 className="text-xl font-bold flex items-center gap-2">
          {TitleIcon && <TitleIcon className="text-blue-600" />}
          {title}
        </h2>
      </div>

      <form onSubmit={handleFormSubmit} className="space-y-4">
        {fields.map((field) => (
          <div key={field.key}>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              {field.label}
            </label>
            {renderField(field)}
          </div>
        ))}

        <button
          type="submit"
          className="w-full bg-blue-600 text-white py-2 rounded-md hover:bg-blue-700 transition flex items-center justify-center gap-2 font-medium"
        >
          <FiSave />
          {submitLabel}
        </button>
      </form>
    </div>
  );
};

CommonAdd.propTypes = {
  title: PropTypes.string.isRequired,
  icon: PropTypes.elementType,
  formData: PropTypes.shape({
    name: PropTypes.string,
    phoneNo: PropTypes.string,
    username: PropTypes.string,
    password: PropTypes.string,
    roles: PropTypes.oneOfType([
      PropTypes.arrayOf(PropTypes.string),
      PropTypes.string,
    ]),
    shelves: PropTypes.oneOfType([
      PropTypes.arrayOf(PropTypes.string),
      PropTypes.string,
    ]),
    racks: PropTypes.oneOfType([
      PropTypes.arrayOf(PropTypes.string),
      PropTypes.string,
    ]),
  }).isRequired,
  setFormData: PropTypes.func.isRequired,
  fields: PropTypes.arrayOf(
    PropTypes.shape({
      key: PropTypes.string.isRequired,
      label: PropTypes.string.isRequired,
      type: PropTypes.string.isRequired,
      api: PropTypes.string,
      filterFunction: PropTypes.func,
    }),
  ).isRequired,
  onSubmit: PropTypes.func,
  validation: PropTypes.func,
  moduleName: PropTypes.string.isRequired,
  submitLabel: PropTypes.string,
};

export default CommonAdd;