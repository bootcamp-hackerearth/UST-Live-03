"use client";
import PropTypes from "prop-types";
import { useState } from "react";
import { useRouter } from "next/navigation";
import axiosInstance from "../api/axiosInstance";
import SingleSelectDropdown from "./Dropdown/SingleSelectDropdown";
import FormShell from "./FormShell";
import { applyFieldTransforms } from "../lib/fieldUtils";

function CommonAddTemplate({
  title,
  apiPath,
  extraFields,
  extraData,
  onSuccessPath,
  showDescription,
  identifierApiEndpoint,
  identifierOptionValue,
  identifierOptionLabel,
}) {
  const router = useRouter();
  const [identifier, setIdentifier] = useState("");
  const [description, setDescription] = useState("");
  const [values, setValues] = useState({});
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleChange = (key, value) => {
    setValues((prev) => ({ ...prev, [key]: value }));
  };

  const buildPayload = () => {
    const payload = {
      identifier,
      ...(showDescription ? { description } : {}),
      ...values,
      ...extraData,
    };
    applyFieldTransforms(payload, extraFields);
    return payload;
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");
    setLoading(true);
    try {
      const response = await axiosInstance.post(`/${apiPath}/add`, buildPayload());
      if (response.data.success === false) {
        setError(response.data.message || `Failed to add ${title}`);
        return;
      }
      router.push(onSuccessPath || `/${apiPath}`);
    } catch (err) {
      setError(err?.response?.data?.message || err.message || "Unable to save.");
    } finally {
      setLoading(false);
    }
  };

  const identifierSlot = identifierApiEndpoint ? (
    <SingleSelectDropdown
      label="Identifier"
      name="identifier"
      apiEndpoint={identifierApiEndpoint}
      value={identifier}
      optionValue={identifierOptionValue}
      optionLabel={identifierOptionLabel}
      placeholder="Select identifier"
      required
      onChange={(_name, val) => setIdentifier(val)}
    />
  ) : (
    <div className="flex flex-col gap-1">
      <label htmlFor="identifier" className="text-sm font-semibold text-gray-600">
        Identifier
      </label>
      <input
        id="identifier"
        type="text"
        placeholder="Enter identifier"
        value={identifier}
        onChange={(event) => setIdentifier(event.target.value)}
        required
        className="border border-gray-300 rounded-xl px-4 py-3 text-sm text-gray-800 bg-white outline-none focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100 transition"
      />
    </div>
  );

  return (
    <FormShell
      title={title}
      mode="add"
      error={error}
      onSubmit={handleSubmit}
      loading={loading}
      showDescription={showDescription}
      description={description}
      onDescriptionChange={setDescription}
      extraFields={extraFields}
      values={values}
      onChange={handleChange}
      identifierSlot={identifierSlot}
    />
  );
}

CommonAddTemplate.propTypes = {
  title: PropTypes.string.isRequired,
  apiPath: PropTypes.string.isRequired,
  extraFields: PropTypes.arrayOf(PropTypes.object),
  extraData: PropTypes.object,
  onSuccessPath: PropTypes.string,
  showDescription: PropTypes.bool,
  identifierApiEndpoint: PropTypes.string,
  identifierOptionValue: PropTypes.string,
  identifierOptionLabel: PropTypes.string,
};

CommonAddTemplate.defaultProps = {
  extraFields: [],
  extraData: {},
  onSuccessPath: null,
  showDescription: true,
  identifierApiEndpoint: null,
  identifierOptionValue: "identifier",
  identifierOptionLabel: "identifier",
};

export default CommonAddTemplate;