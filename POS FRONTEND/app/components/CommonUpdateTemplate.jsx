"use client";
import PropTypes from "prop-types";
import { useEffect, useState } from "react";
import { useRouter, useParams } from "next/navigation";
import axiosInstance from "../api/axiosInstance";
import FormShell from "./FormShell";
import { applyFieldTransforms, formatAuditDate } from "../lib/fieldUtils";

function CommonUpdateTemplate({
  title,
  apiPath,
  recordParam,
  extraFields,
  extraData,
  onSuccessPath,
  showDescription,
  showIdentifier,
  identifierLabel,
  identifierReadOnly,
  showIdentifierFallback,
  showAuditSummary,
}) {
  const router = useRouter();
  const params = useParams();
  const recordId = decodeURIComponent(params.id || "");

  const [entityId, setEntityId] = useState(null);
  const [identifier, setIdentifier] = useState("");
  const [description, setDescription] = useState("");
  const [values, setValues] = useState({});
  const [recordData, setRecordData] = useState(null);
  const [error, setError] = useState("");
  const [pageLoading, setPageLoading] = useState(true);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await axiosInstance.get(`/${apiPath}/update`, {
          params: { [recordParam]: recordId },
        });
        const data = response.data || {};
        setRecordData(data);
        setEntityId(data.id || null);
        setIdentifier(data.identifier || "");
        setDescription(data.description || "");

        const nextValues = {};
        extraFields.forEach((field) => {
          let value = data[field.key];
          if (value !== null && value !== undefined && typeof value === "object" && !Array.isArray(value)) {
            const extractKey = field.optionValue ?? "identifier";
            value = value[extractKey] ?? value.id ?? "";
          }
          nextValues[field.key] =
            field.asArray && Array.isArray(value) && field.type !== "multiselect"
              ? value[0] || ""
              : value ?? "";
        });
        setValues(nextValues);
      } catch (err) {
        setError(err?.response?.data?.message || err.message || `Unable to load ${title}.`);
      } finally {
        setPageLoading(false);
      }
    };
    fetchData();
  }, [apiPath, recordId]);

  const handleChange = (key, value) => {
    setValues((prev) => ({ ...prev, [key]: value }));
  };

  const buildPayload = () => {
    const payload = {
      ...(entityId ? { id: entityId } : {}),
      [recordParam]: recordId,
      ...(showIdentifier ? { identifier } : {}),
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
      const response = await axiosInstance.put(`/${apiPath}/update`, buildPayload());
      if (response.data.success === false) {
        setError(response.data.message || `Failed to update ${title}`);
        return;
      }
      router.push(onSuccessPath || `/${apiPath}`);
    } catch (err) {
      setError(err?.response?.data?.message || err.message || "Unable to update.");
    } finally {
      setLoading(false);
    }
  };

  const renderIdentifierField = () => {
    if (showIdentifier) {
      return (
        <div className="flex flex-col gap-1">
          <label htmlFor="identifier" className="text-sm font-semibold text-gray-600">
            {identifierLabel}
          </label>
          <input
            id="identifier"
            type="text"
            value={identifier}
            readOnly={identifierReadOnly}
            onChange={identifierReadOnly ? undefined : (event) => setIdentifier(event.target.value)}
            className={`rounded-xl border px-4 py-3 text-sm outline-none transition ${
              identifierReadOnly
                ? "cursor-not-allowed border-gray-200 bg-gray-50 text-gray-500"
                : "border-gray-300 bg-white text-gray-800 focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100"
            }`}
          />
        </div>
      );
    }
    if (showIdentifierFallback) {
      return (
        <div className="flex flex-col gap-1">
          <label htmlFor="identifier" className="text-sm font-semibold text-gray-600">
            {identifierLabel}
          </label>
          <input
            id="identifier"
            type="text"
            value={identifier || recordId}
            readOnly
            className="cursor-not-allowed rounded-xl border border-gray-200 bg-gray-50 px-4 py-3 text-sm text-gray-500"
          />
        </div>
      );
    }
    return (
      <div className="flex flex-col gap-1">
        <label htmlFor="recordId" className="text-sm font-semibold text-gray-600 capitalize">
          {recordParam}
        </label>
        <input
          id="recordId"
          type="text"
          value={recordId}
          disabled
          className="border border-gray-200 rounded-xl px-4 py-3 text-sm text-gray-400 bg-gray-50 cursor-not-allowed"
        />
      </div>
    );
  };

  if (pageLoading) {
    return (
      <div className="w-full py-10 text-center text-slate-600">
        Loading {title.toLowerCase()}...
      </div>
    );
  }

  const auditCards = [
    { label: "Created By", value: recordData?.createdBy },
    { label: "Created On", value: formatAuditDate(recordData?.createdOn) },
    { label: "Modified By", value: recordData?.modifiedBy },
    { label: "Modified On", value: formatAuditDate(recordData?.modifiedOn) },
  ];

  return (
    <div className="w-full max-w-3xl mx-auto space-y-6">
      <FormShell
        title={title}
        mode="update"
        error={error}
        onSubmit={handleSubmit}
        loading={loading}
        showDescription={showDescription}
        description={description}
        onDescriptionChange={setDescription}
        extraFields={extraFields}
        values={values}
        onChange={handleChange}
        identifierSlot={renderIdentifierField()}
      />

      {showAuditSummary && (
        <section className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm md:p-8">
          <div className="flex flex-col gap-2 sm:flex-row sm:items-end sm:justify-between">
            <div>
              <p className="text-xs font-semibold uppercase tracking-[0.2em] text-blue-600">
                Audit Trail
              </p>
              <h3 className="mt-2 text-xl font-bold text-slate-900">
                Created and modified metadata
              </h3>
            </div>
            <p className="text-sm text-slate-500">
              These fields are set automatically by the backend service.
            </p>
          </div>

          <div className="mt-6 grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
            {auditCards.map((item) => (
              <div
                key={item.label}
                className="rounded-2xl border border-slate-200 bg-slate-50 px-4 py-4"
              >
                <p className="text-xs font-semibold uppercase tracking-[0.18em] text-slate-400">
                  {item.label}
                </p>
                <p className="mt-2 break-words text-sm font-semibold text-slate-900">
                  {item.value || "-"}
                </p>
              </div>
            ))}
          </div>
        </section>
      )}
    </div>
  );
}

CommonUpdateTemplate.propTypes = {
  title: PropTypes.string.isRequired,
  apiPath: PropTypes.string.isRequired,
  recordParam: PropTypes.string,
  extraFields: PropTypes.arrayOf(PropTypes.object),
  extraData: PropTypes.object,
  onSuccessPath: PropTypes.string,
  showDescription: PropTypes.bool,
  showIdentifier: PropTypes.bool,
  identifierLabel: PropTypes.string,
  identifierReadOnly: PropTypes.bool,
  showIdentifierFallback: PropTypes.bool,
  showAuditSummary: PropTypes.bool,
};

CommonUpdateTemplate.defaultProps = {
  recordParam: "identifier",
  extraFields: [],
  extraData: {},
  onSuccessPath: null,
  showDescription: true,
  showIdentifier: true,
  identifierLabel: "Identifier",
  identifierReadOnly: false,
  showIdentifierFallback: false,
  showAuditSummary: false,
};

export default CommonUpdateTemplate;