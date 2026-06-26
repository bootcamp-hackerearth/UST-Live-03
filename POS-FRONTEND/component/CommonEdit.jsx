"use client";

import { useState, useEffect, useCallback } from "react";
import PropTypes from "prop-types";
import { FiSave } from "react-icons/fi";
import api from "@/app/(main)/api/axios";

// ── Helpers ───────────────────────────────────────────────────────────────────
const toRolesArray = (roles) => {
  if (Array.isArray(roles)) return roles;
  if (roles) return [roles];
  return [];
};

// ── Validators ────────────────────────────────────────────────────────────────
const validateName = (value) => {
  if (value?.trim().length >= 3) return null;
  if (value?.trim().length > 0)
    return "Name must be at least 3 characters long.";
  return "Name is required.";
};
const validatePhone = (value) => {
  if (/^\d{10}$/.test(value ?? "")) return null;
  return "Phone number must contain exactly 10 digits.";
};
const validateUsername = (value) => {
  if (/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value ?? "")) return null;
  return "Please enter a valid email.";
};
const validatePassword = (value) => {
  if (value?.trim().length >= 6) return null;
  if (value?.trim().length > 0)
    return "Password must be at least 6 characters long.";
  return "Password is required.";
};
const validateRoles = (value) => {
  if (Array.isArray(value) && value.length > 0) return null;
  if (typeof value === "string" && value.trim().length > 0) return null;
  return "Role is required.";
};
const validateField = (key, value) => {
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
      className="w-full text-left p-2 text-sm hover:bg-blue-600 hover:text-white cursor-pointer border-b border-gray-100 last:border-none"
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
const DropdownList = ({ fieldKey, options, loadingField, onSelect }) => {
  if (options.length === 0 && loadingField !== fieldKey) return null;
  return (
    <ul className="absolute z-50 w-full bg-white border border-gray-200 rounded-md mt-1 shadow-lg max-h-60 overflow-auto">
      {loadingField === fieldKey ? (
        <li className="p-2 text-sm text-gray-400">Loading...</li>
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
  options: PropTypes.array.isRequired,
  loadingField: PropTypes.string,
  onSelect: PropTypes.func.isRequired,
};

// ── RolesSearchField ──────────────────────────────────────────────────────────
const RolesSearchField = ({
  field,
  formData,
  setFormData,
  allOptions, // full unfiltered list fetched on focus
  openDropdown,
  setOpenDropdown,
  searchTerms,
  setSearchTerms,
  loadingField,
  onRoleSelect,
  onFocus,
}) => {
  const roles = toRolesArray(formData.roles);
  const term = (searchTerms[field.key] ?? "").toLowerCase();

  // Filter client-side from the full list
  const filteredOptions = term
    ? allOptions.filter((item) => item.identifier.toLowerCase().includes(term))
    : allOptions;

  const handleFocus = () => {
    setOpenDropdown(field.key);
    onFocus();
  };

  return (
    <div className="relative">
      <div className="border border-gray-300 rounded-md p-2 bg-white">
        <div className="flex flex-wrap gap-2 mb-2">
          {roles.map((role) => (
            <div
              key={role}
              className="bg-blue-100 text-blue-700 px-3 py-1 rounded-full text-sm flex items-center gap-2"
            >
              <span>{role}</span>
              <button
                type="button"
                className="font-bold hover:text-red-600"
                onClick={(e) => {
                  e.stopPropagation();
                  setFormData({
                    ...formData,
                    roles: roles.filter((r) => r !== role),
                  });
                }}
              >
                ×
              </button>
            </div>
          ))}
        </div>
        <input
          type="text"
          placeholder="Search roles..."
          value={searchTerms[field.key] ?? ""}
          onFocus={handleFocus}
          onClick={(e) => e.stopPropagation()}
          onChange={(e) =>
            setSearchTerms((prev) => ({ ...prev, [field.key]: e.target.value }))
          }
          className="w-full outline-none"
        />
      </div>

      {openDropdown === field.key && (
        <DropdownList
          fieldKey={field.key}
          options={filteredOptions}
          loadingField={loadingField}
          onSelect={onRoleSelect}
        />
      )}
    </div>
  );
};
RolesSearchField.propTypes = {
  field: PropTypes.object.isRequired,
  formData: PropTypes.object.isRequired,
  setFormData: PropTypes.func.isRequired,
  allOptions: PropTypes.array.isRequired,
  openDropdown: PropTypes.string,
  setOpenDropdown: PropTypes.func.isRequired,
  searchTerms: PropTypes.object.isRequired,
  setSearchTerms: PropTypes.func.isRequired,
  loadingField: PropTypes.string,
  onRoleSelect: PropTypes.func.isRequired,
  onFocus: PropTypes.func.isRequired,
};

// ── GenericSearchField ────────────────────────────────────────────────────────
const GenericSearchField = ({
  field,
  formData,
  setFormData,
  allOptions, // full unfiltered list fetched on focus
  openDropdown,
  setOpenDropdown,
  searchTerms,
  setSearchTerms,
  loadingField,
  onGenericSelect,
  onFocus,
}) => {
  const term = (searchTerms[field.key] ?? "").toLowerCase();

  // Filter client-side from the full list
  const filteredOptions = term
    ? allOptions.filter((item) => item.identifier.toLowerCase().includes(term))
    : allOptions;

  const handleFocus = () => {
    setOpenDropdown(field.key);
    onFocus();
  };

  return (
    <div className="relative">
      <input
        type="text"
        value={formData[field.key] ?? ""}
        onFocus={handleFocus}
        onClick={(e) => e.stopPropagation()}
        disabled={field.disabled}
        onChange={(e) => {
          const val = e.target.value;
          setFormData({ ...formData, [field.key]: val });
          setSearchTerms((prev) => ({ ...prev, [field.key]: val }));
        }}
        className="w-full p-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 outline-none"
      />

      {openDropdown === field.key && (
        <DropdownList
          fieldKey={field.key}
          options={filteredOptions}
          loadingField={loadingField}
          onSelect={(item) => onGenericSelect(field.key, item)}
        />
      )}
    </div>
  );
};
GenericSearchField.propTypes = {
  field: PropTypes.object.isRequired,
  formData: PropTypes.object.isRequired,
  setFormData: PropTypes.func.isRequired,
  allOptions: PropTypes.array.isRequired,
  openDropdown: PropTypes.string,
  setOpenDropdown: PropTypes.func.isRequired,
  searchTerms: PropTypes.object.isRequired,
  setSearchTerms: PropTypes.func.isRequired,
  loadingField: PropTypes.string,
  onGenericSelect: PropTypes.func.isRequired,
  onFocus: PropTypes.func.isRequired,
};

// ── Main component ────────────────────────────────────────────────────────────
const CommonEdit = ({
  title,
  icon: TitleIcon,
  formData,
  setFormData,
  fields,
  onSubmit,
  validation,
  moduleName,
  submitLabel = "Update",
}) => {
  const [dropdownData, setDropdownData] = useState({}); // stores full option lists per field
  const [openDropdown, setOpenDropdown] = useState(null);
  const [searchTerms, setSearchTerms] = useState({});
  const [loadingField, setLoadingField] = useState(null);

  useEffect(() => {
    const closeMenus = () => setOpenDropdown(null);
    globalThis.addEventListener("click", closeMenus);
    return () => globalThis.removeEventListener("click", closeMenus);
  }, []);

  // Fetches ALL options for a field (called on focus)
  const fetchAllOptions = useCallback(
    async (field) => {
      if (!field.api) return;
      // Already loaded — no need to refetch
      if (dropdownData[field.key]?.length > 0) return;
      setLoadingField(field.key);
      try {
        const response = await api.post(field.api, {
          page: 0,
          sizePerPage: 100,
          sortField: "identifier",
          search: "",
        });
        let results = response.data.dtoList ?? [];
        if (field.key === "roles") {
          const current = toRolesArray(formData.roles);
          results = results.filter(
            (item) => !current.includes(item.identifier),
          );
        }
        if (field.excludeCurrent) {
          results = results.filter(
            (item) => item.identifier !== formData.identifier,
          );
        }
        setDropdownData((prev) => ({ ...prev, [field.key]: results }));
      } catch (err) {
        console.error(err);
      } finally {
        setLoadingField(null);
      }
    },
    [formData, dropdownData],
  );

  const handleRoleSelect = useCallback(
    (item) => {
      const existing = toRolesArray(formData.roles);
      if (existing.includes(item.identifier)) return;
      setFormData({ ...formData, roles: [...existing, item.identifier] });
      setOpenDropdown(null);
      // Remove selected role from available options
      setDropdownData((prev) => ({
        ...prev,
        roles: (prev.roles ?? []).filter(
          (r) => r.identifier !== item.identifier,
        ),
      }));
    },
    [formData, setFormData],
  );

  const handleGenericSelect = useCallback(
    (fieldKey, item) => {
      setFormData({ ...formData, [fieldKey]: item.identifier });
      setOpenDropdown(null);
    },
    [formData, setFormData],
  );

  const validateForm = () => {
    for (const field of fields) {
      const error = validateField(field.key, formData[field.key]);
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
    const hasRolesField = fields.some((f) => f.key === "roles");
    const payload = hasRolesField
      ? { ...formData, roles: toRolesArray(formData.roles) }
      : { ...formData };

    try {
      const response = await api.put(`/api/${moduleName}/update`, payload);
      if (response.data.success === false) {
        alert(response.data.message);
        return;
      }
      alert(`${title} updated successfully`);
      if (onSubmit) onSubmit(response);
    } catch (err) {
      console.error(err);
      alert(err.response?.data?.message || "Failed to update record.");
    }
  };

  const renderField = (field) => {
    if (field.type === "search" && field.key === "roles") {
      return (
        <RolesSearchField
          field={field}
          formData={formData}
          setFormData={setFormData}
          allOptions={dropdownData[field.key] ?? []}
          openDropdown={openDropdown}
          setOpenDropdown={setOpenDropdown}
          searchTerms={searchTerms}
          setSearchTerms={setSearchTerms}
          loadingField={loadingField}
          onRoleSelect={handleRoleSelect}
          onFocus={() => fetchAllOptions(field)}
        />
      );
    }

    if (field.type === "search") {
      return (
        <GenericSearchField
          field={field}
          formData={formData}
          setFormData={setFormData}
          allOptions={dropdownData[field.key] ?? []}
          openDropdown={openDropdown}
          setOpenDropdown={setOpenDropdown}
          searchTerms={searchTerms}
          setSearchTerms={setSearchTerms}
          loadingField={loadingField}
          onGenericSelect={handleGenericSelect}
          onFocus={() => fetchAllOptions(field)}
        />
      );
    }

    if (field.type === "checkbox") {
      return (
        <div className="flex items-center gap-2">
          <input
            type="checkbox"
            checked={formData[field.key] ?? false}
            onChange={(e) =>
              setFormData({ ...formData, [field.key]: e.target.checked })
            }
          />
          <span>Active</span>
        </div>
      );
    }

    return (
      <input
        type={field.type}
        disabled={field.disabled}
        maxLength={field.key === "phoneNo" ? 10 : undefined}
        value={formData[field.key] ?? ""}
        onChange={(e) => {
          let val = e.target.value;
          if (field.key === "phoneNo") val = val.replace(/\D/g, "");
          setFormData({ ...formData, [field.key]: val });
        }}
        className="w-full p-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 outline-none"
      />
    );
  };

  return (
    <div className="bg-white rounded-xl shadow-xl p-6 w-full max-w-lg text-gray-800">
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

CommonEdit.propTypes = {
  title: PropTypes.string.isRequired,
  icon: PropTypes.elementType,
  formData: PropTypes.shape({
    identifier: PropTypes.string,
    name: PropTypes.string,
    phoneNo: PropTypes.string,
    username: PropTypes.string,
    password: PropTypes.string,
    roles: PropTypes.oneOfType([
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
      disabled: PropTypes.bool,
      excludeCurrent: PropTypes.bool,
    }),
  ).isRequired,
  onSubmit: PropTypes.func,
  validation: PropTypes.func,
  moduleName: PropTypes.string.isRequired,
  submitLabel: PropTypes.string,
};

export default CommonEdit;
