"use client";
import { useEffect, useState } from "react";
import { useRouter, useParams } from "next/navigation";
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

function CommonUpdateTemplate({
  title,
  apiPath,
  recordId: incomingRecordId,
  recordParam = "identifier",
  recordGetEndpoint = "identifier",
  identifierKey = "identifier",
  identifierLabel = "Identifier",
  extraFields = [],
  extraData = {},
  onSuccessPath,
  showDescription = true,
  identifierEditable = true,
}) {
  const router = useRouter();
  const params = useParams();
  const recordIdValue = params?.id || incomingRecordId || "";
  const recordIdentifier = recordIdValue ? decodeURIComponent(recordIdValue) : "";

  const [id, setId] = useState(null);
  const [identifier, setIdentifier] = useState("");
  const [description, setDescription] = useState("");
  const [values, setValues] = useState({});
  const [error, setError] = useState("");
  const [pageLoading, setPageLoading] = useState(true);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await axiosInstance.get(
          `/${apiPath}/${recordGetEndpoint}`,
          { params: { [recordParam]: recordIdentifier } }
        );
        const data = response.data || {};
        console.log("Loaded Data:", data);
        setId(data.id || null);
        setIdentifier(data[identifierKey] || data.identifier || "");
        setDescription(data.description || "");
        const nextValues = {};
        extraFields.forEach((field) => {
          const value = data[field.key];
          nextValues[field.key] =
            field.asArray && Array.isArray(value) && field.type !== "multiselect"
              ? value[0] || ""
              : value ?? "";
        });
        setValues(nextValues);
      } catch (err) {
        setError(
          err?.response?.data?.message || err.message || `Unable to load ${title}.`
        );
      } finally {
        setPageLoading(false);
      }
    };
    if (recordIdentifier) fetchData();
  }, [apiPath, recordIdentifier, recordParam, recordGetEndpoint, identifierKey, extraFields, title]);

  const handleChange = (key, value) =>
    setValues((prev) => ({ ...prev, [key]: value }));

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");
    setLoading(true);
    try {
      const payload = buildPayload({
        id,
        identifierKey,
        identifier,
        showDescription,
        description,
        values,
        extraFields,
        extraData,
      });
      console.log("Update Payload:", payload);
      const response = await axiosInstance.post(`/${apiPath}/update`, payload);
      if (response.data?.success === false) {
        setError(response.data.message || `Failed to update ${title}`);
        return;
      }
      router.push(onSuccessPath || `/${apiPath}`);
    } catch (err) {
      console.error(err);
      setError(err?.response?.data?.message || err.message || "Unable to update.");
    } finally {
      setLoading(false);
    }
  };

  if (pageLoading) {
    return (
      <div className="mx-auto flex w-full max-w-3xl items-center justify-center py-20">
        <div className="text-sm font-semibold text-slate-500">Loading...</div>
      </div>
    );
  }

  return (
    <div className="mx-auto w-full max-w-3xl">
      <div className="mb-6">
        <h2 className="text-2xl font-black tracking-tight text-slate-950">Update {title}</h2>
        <p className="mt-1 text-sm font-medium text-slate-500">
          Update the details of the {title.toLowerCase()}.
        </p>
      </div>
      <form
        onSubmit={handleSubmit}
        className="rounded-lg border border-slate-200 bg-white p-6 shadow-sm md:p-8"
      >
        {error && <ErrorBanner message={error} />}

        <div className="grid gap-5 md:grid-cols-2">
          <FieldWrapper label={identifierLabel}>
            <input
              type="text"
              value={identifier}
              onChange={(e) => setIdentifier(e.target.value)}
              disabled={!identifierEditable}
              required
              className={`disabled:bg-slate-100 disabled:text-slate-500 ${INPUT_CLS}`}
            />
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
          submitLabel={`Update ${title}`}
        />
      </form>
    </div>
  );
}

CommonUpdateTemplate.propTypes = {
  title: PropTypes.string.isRequired,
  apiPath: PropTypes.string.isRequired,
  recordId: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  recordParam: PropTypes.string,
  recordGetEndpoint: PropTypes.string,
  identifierKey: PropTypes.string,
  identifierLabel: PropTypes.string,
  extraFields: PropTypes.arrayOf(extraFieldShape),
  extraData: PropTypes.object,
  onSuccessPath: PropTypes.string,
  showDescription: PropTypes.bool,
  identifierEditable: PropTypes.bool,
};

export default CommonUpdateTemplate;