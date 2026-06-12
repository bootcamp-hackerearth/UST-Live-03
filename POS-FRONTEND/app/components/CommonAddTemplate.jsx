"use client";
import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import PropTypes from "prop-types";
import axiosInstance from "../api/axiosInstance";
import {
  INPUT_CLS,
  FieldWrapper,
  extraFieldShape,
  buildPayload,
  ErrorBanner,
  DescriptionField,
  ExtraFieldsList,
  FormActions,
} from "./formUtils";

function CommonAddTemplate({
  title,
  apiPath,
  extraFields = [],
  extraData = {},
  onSuccessPath,
  showDescription = false,
  identifierKey = "identifier",
  identifierLabel = "Identifier",
  identifierType = "text",
  identifierApiPath,
  identifierApiEndpoint,
  identifierValueKey = "identifier",
  identifierLabelKey = "identifier",
}) {
  const router = useRouter();
  const [identifier, setIdentifier] = useState("");
  const [description, setDescription] = useState("");
  const [values, setValues] = useState({});
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [identifierOptions, setIdentifierOptions] = useState([]);

  useEffect(() => {
    if (identifierType === "select" && identifierApiPath) {
      const endpoint = identifierApiEndpoint
        ? `/${identifierApiPath}/${identifierApiEndpoint}`
        : `/${identifierApiPath}`;
      axiosInstance
        .get(endpoint)
        .then((res) => {
          const data = Array.isArray(res.data)
            ? res.data
            : res.data?.data ?? res.data?.content ?? [];
          setIdentifierOptions(
            data.map((item) => ({
              value: item[identifierValueKey],
              label: item[identifierLabelKey],
            }))
          );
        })
        .catch(() => setIdentifierOptions([]));
    }
  }, [identifierType, identifierApiPath, identifierApiEndpoint, identifierValueKey, identifierLabelKey]);

  const handleChange = (key, value) =>
    setValues((prev) => ({ ...prev, [key]: value }));

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");
    setLoading(true);
    try {
      const payload = buildPayload({
        identifierKey,
        identifier,
        showDescription,
        description,
        values,
        extraFields,
        extraData,
      });
      const response = await axiosInstance.post(`/${apiPath}/add`, payload);
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

  return (
    <div className="mx-auto w-full max-w-3xl">
      <div className="mb-6">
        <h2 className="text-2xl font-black tracking-tight text-slate-950">Add {title}</h2>
        <p className="mt-1 text-sm font-medium text-slate-500">
          Fill in the details to add a new {title.toLowerCase()}.
        </p>
      </div>
      <form
        onSubmit={handleSubmit}
        className="rounded-lg border border-slate-200 bg-white p-6 shadow-sm md:p-8"
      >
        {error && <ErrorBanner message={error} />}

        <div className="grid gap-5 md:grid-cols-2">
          <FieldWrapper label={identifierLabel}>
            {identifierType === "select" ? (
              <select
                value={identifier}
                onChange={(e) => setIdentifier(e.target.value)}
                required
                className={INPUT_CLS}
              >
                <option value="">Select {identifierLabel}</option>
                {identifierOptions.map((opt) => (
                  <option key={opt.value} value={opt.value}>
                    {opt.label}
                  </option>
                ))}
              </select>
            ) : (
              <input
                type="text"
                placeholder={`Enter ${identifierLabel.toLowerCase()}`}
                value={identifier}
                onChange={(e) => setIdentifier(e.target.value)}
                required
                className={INPUT_CLS}
              />
            )}
          </FieldWrapper>

          {showDescription && (
            <DescriptionField
              value={description}
              onChange={(e) => setDescription(e.target.value)}
            />
          )}

          <ExtraFieldsList
            extraFields={extraFields}
            values={values}
            handleChange={handleChange}
          />
        </div>

        <FormActions
          onCancel={() => router.back()}
          loading={loading}
          submitLabel={`Add ${title}`}
        />
      </form>
    </div>
  );
}

CommonAddTemplate.propTypes = {
  title: PropTypes.string.isRequired,
  apiPath: PropTypes.string.isRequired,
  extraFields: PropTypes.arrayOf(extraFieldShape),
  extraData: PropTypes.object,
  onSuccessPath: PropTypes.string,
  showDescription: PropTypes.bool,
  identifierKey: PropTypes.string,
  identifierLabel: PropTypes.string,
  identifierType: PropTypes.string,
  identifierApiPath: PropTypes.string,
  identifierApiEndpoint: PropTypes.string,
  identifierValueKey: PropTypes.string,
  identifierLabelKey: PropTypes.string,
};

export default CommonAddTemplate;