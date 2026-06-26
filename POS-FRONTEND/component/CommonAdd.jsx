"use client";

import { useState, useEffect } from "react";
import { FiSave } from "react-icons/fi";
import PropTypes from "prop-types";
import api from "@/app/(main)/api/axios";

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
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

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
  useEffect(() => {
    const closeMenus = () => setOpenDropdown(null);

    globalThis.addEventListener("click", closeMenus);

    return () => globalThis.removeEventListener("click", closeMenus);
  }, []);

  const searchField = async (field, term) => {
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

      if (field.filterFunction) {
        results = results.filter(field.filterFunction);
      }

      setDropdownData((prev) => ({
        ...prev,
        [field.key]: results,
      }));
    } catch (err) {
      console.error(err);
    } finally {
      setLoadingField(null);
    }
  };
  const handleFormSubmit = async (e) => {

    e.preventDefault();
    const isValid = validateForm();
  if (!isValid) {
    return;
  }

  if (validation) {

    const error = validation(formData);

    if (error) {
      alert(error);
      return;
    }
  }

    try {
    let roles = formData.roles;

    if (roles !== undefined && !Array.isArray(roles)) {
      roles = [roles];
    }

    const payload = {
      ...formData,
      roles,
    };

    const response = await api.post(`/api/${moduleName}/add`, payload);

    if (response.data.success === false) {

      alert(response.data.message);
      return;
    }

    alert(`${title} added successfully`);

    if (onSubmit) {
      onSubmit(response);
    }

  } catch (err) {

    console.error(err);

    alert(
      err.response?.data?.message ||
      "Failed to add record."
    );
  }
};

  useEffect(() => {
    const timer = setTimeout(() => {
      fields
        .filter((field) => field.type === "search")
        .forEach((field) => {
          const term = searchTerms[field.key] || "";
          searchField(field, term);
        });
    }, 300);

    return () => clearTimeout(timer);
  }, [searchTerms, fields]);
  

  return (
    <div className="bg-white rounded-xl shadow-xl p-6 w-full max-w-lg text-gray-800">
      <div className="mb-6">
        <h2 className="text-xl font-bold flex items-center gap-2">
          {TitleIcon && <TitleIcon className="text-blue-600" />}
          {title}
        </h2>
      </div>

      <form onSubmit={handleFormSubmit} className="space-y-4">
        {fields.map((field) => {
          const isSearch = field.type === "search";
          const isCheckbox = field.type === "checkbox";
          let fieldInput;
          if (isSearch) {
            fieldInput = (
              <div className="relative">
                <input
                  type="text"
                  value={formData[field.key] || ""}
                  onClick={(e) => {
                    e.stopPropagation();
                    setOpenDropdown(field.key);
                  }}
                  onFocus={() => setOpenDropdown(field.key)}
                  onChange={(e) => {
                    let val = e.target.value;

                    if (field.key === "phoneNo") {
                      val = val.replace(/\D/g, "");
                    }

                    setFormData({
                      ...formData,
                      [field.key]: val,
                    });

                    setSearchTerms((prev) => ({
                      ...prev,
                      [field.key]: val,
                    }));
                  }}
                  className="w-full p-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 outline-none"
                />

                {openDropdown === field.key && (
                  <ul className="absolute z-50 w-full bg-white border border-gray-200 rounded-md mt-1 shadow-lg max-h-60 overflow-auto">
                    {loadingField === field.key ? (
                      <li className="p-2 text-sm text-gray-400">
                        Searching...
                      </li>
                    ) : (
                      dropdownData[field.key]?.map((item, index) => (
                        <li key={item.id || index}>
                          <button
                            type="button"
                            className="w-full text-left p-2 text-sm hover:bg-blue-600 hover:text-white border-b border-gray-100 last:border-none"
                            onClick={() => {
                              setFormData({
                                ...formData,
                                [field.key]: item.identifier,
                              });

                              setOpenDropdown(null);
                            }}
                          >
                            {item.identifier}
                          </button>
                        </li>
                      ))
                    )}
                  </ul>
                )}
              </div>
            );
          } else if (isCheckbox) {
            fieldInput = (
              <div className="flex items-center gap-2">
                <input
                  type="checkbox"
                  checked={formData[field.key] || false}
                  onChange={(e) =>
                    setFormData({
                      ...formData,
                      [field.key]: e.target.checked,
                    })
                  }
                />
                <span>Active</span>
              </div>
            );
          } else {
            fieldInput = (
              <input
                type={field.type}
                maxLength={field.key === "phoneNo" ? 10 : undefined}
                value={formData[field.key] || ""}
                onChange={(e) => {
                  let val = e.target.value;

                  if (field.key === "phoneNo") {
                    val = val.replace(/\D/g, "");
                  }

                  setFormData({
                    ...formData,
                    [field.key]: val,
                  });
                }}
                className="w-full p-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 outline-none"
              />
            );
          }

          return (
            <div key={field.key}>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                {field.label}
              </label>
                {fieldInput}
            </div>
          );
        })}

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