"use client";
import PropTypes from "prop-types";
import { useEffect, useState } from "react";
import { useRouter, useParams } from "next/navigation";
import { getItem, updateItem } from "@/services/api";
import { extractErrorInfo, isModalErrorStatus } from "@/utils/httpError";
import ErrorModal from "./ErrorModal";

export default function CommonEdit({
  title,
  subtitle,
  entityType,
  redirectPath,
  checkSuccess = false,
  buildPayload,
  identifier: identifierProp,
  showIdentifier = true,
  description: descriptionProp,
  setDescription: setDescriptionProp,
  showDescription = true,
  descriptionRequired = true,
  loading: loadingProp,
  error: errorProp,
  successMessage: successMessageProp,
  onSubmit: onSubmitProp,
  cancelPath,
  children,
  auditData: auditDataProp,
}) {
  const router = useRouter();
  const params = useParams();

  const isManagedMode = !!entityType;

  const [managedDescription, setManagedDescription] = useState("");
  const [managedLoading, setManagedLoading] = useState(false);
  const [pageLoading, setPageLoading] = useState(true);
  const [managedError, setManagedError] = useState("");
  const [managedModalError, setManagedModalError] = useState(null);
  const [managedSuccessMessage, setManagedSuccessMessage] = useState("");
  const [managedAuditData, setManagedAuditData] = useState({});

  const rawIdentifier = params?.identifier;
  const managedIdentifier = rawIdentifier
    ? decodeURIComponent(rawIdentifier)
    : null;

  const identifier = isManagedMode ? managedIdentifier : identifierProp;

  const description = isManagedMode ? managedDescription : descriptionProp;
  const setDescription = isManagedMode
    ? setManagedDescription
    : setDescriptionProp;
  const resolvedLoading = isManagedMode ? managedLoading : loadingProp;
  const resolvedError = isManagedMode ? managedError : errorProp;
  const resolvedSuccessMessage = isManagedMode
    ? managedSuccessMessage
    : successMessageProp;
  const resolvedAuditData = isManagedMode ? managedAuditData : auditDataProp;

  useEffect(() => {
    if (!isManagedMode || !identifier) return;
    const loadEntity = async () => {
      try {
        setPageLoading(true);
        setManagedError("");
        const response = await getItem(entityType, identifier);
        setManagedDescription(response.description || "");
        setManagedAuditData({
          createdBy: response.createdBy,
          createdOn: response.createdOn,
          modifiedBy: response.modifiedBy,
          modifiedOn: response.modifiedOn,
        });
      } catch (err) {
        console.log(err);
        const { status, message } = extractErrorInfo(
          err,
          `Failed to load ${entityType}`,
        );
        if (isModalErrorStatus(status)) {
          setManagedModalError({ status, message });
        } else {
          setManagedError(message);
        }
      } finally {
        setPageLoading(false);
      }
    };
    loadEntity();
  }, [isManagedMode, entityType, identifier]);

  const managedSubmit = async (e) => {
    e.preventDefault();
    try {
      setManagedLoading(true);
      setManagedError("");
      setManagedSuccessMessage("");
      const payload = buildPayload
        ? buildPayload({ identifier, description: managedDescription })
        : { identifier, description: managedDescription };
      const response = await updateItem(entityType, payload);
      if (checkSuccess && response?.success === false) {
        setManagedError(response.message || `Failed to update ${entityType}`);
        return;
      }
      const label = entityType.charAt(0).toUpperCase() + entityType.slice(1);
      setManagedSuccessMessage(`${label} updated successfully`);
      setTimeout(() => router.push(redirectPath), 1000);
    } catch (err) {
      console.log(err);
      const { status, message } = extractErrorInfo(
        err,
        `Failed to update ${entityType}`,
      );
      if (isModalErrorStatus(status)) {
        setManagedModalError({ status, message });
      } else {
        setManagedError(message);
      }
    } finally {
      setManagedLoading(false);
    }
  };

  const onSubmit = isManagedMode ? managedSubmit : onSubmitProp;

  const formatDate = (dateStr) => {
    if (!dateStr) return "—";
    return new Date(dateStr).toLocaleString("en-IN", {
      day: "2-digit",
      month: "short",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  const showAudit =
    resolvedAuditData?.createdBy || resolvedAuditData?.createdOn;

  if (isManagedMode && pageLoading) {
    return (
      <div className="flex items-center justify-center py-20">
        <p className="text-[#667085]">Loading {entityType}...</p>
      </div>
    );
  }

  return (
    <>
      <ErrorModal
        open={!!managedModalError}
        status={managedModalError?.status}
        message={managedModalError?.message}
        onClose={() => setManagedModalError(null)}
      />
      <div className="w-full flex justify-center">
        <div className="w-full max-w-215 bg-white rounded-[28px] border border-[#e4e7ec] shadow-sm px-9 py-8">
          <div className="mb-8">
            <h1 className="text-[30px] font-semibold tracking-[-0.02em] text-[#101828]">
              {title}
            </h1>
            <p className="mt-2 text-[15px] text-[#667085]">{subtitle}</p>
          </div>
          {showAudit && (
            <div className="mb-6 grid grid-cols-2 gap-x-8 gap-y-4 rounded-2xl border border-[#e4e7ec] bg-[#f9fafb] px-6 py-4">
              <div className="flex flex-col gap-4">
                <div>
                  <p className="text-xs font-medium text-[#667085] uppercase tracking-wide">
                    Created By
                  </p>
                  <p className="mt-1 text-sm text-[#101828]">
                    {resolvedAuditData?.createdBy || "—"}
                  </p>
                </div>
                <div>
                  <p className="text-xs font-medium text-[#667085] uppercase tracking-wide">
                    Created On
                  </p>
                  <p className="mt-1 text-sm text-[#101828]">
                    {formatDate(resolvedAuditData?.createdOn)}
                  </p>
                </div>
              </div>
              <div className="flex flex-col gap-4">
                <div>
                  <p className="text-xs font-medium text-[#667085] uppercase tracking-wide">
                    Modified By
                  </p>
                  <p className="mt-1 text-sm text-[#101828]">
                    {resolvedAuditData?.modifiedBy || "—"}
                  </p>
                </div>
                <div>
                  <p className="text-xs font-medium text-[#667085] uppercase tracking-wide">
                    Modified On
                  </p>
                  <p className="mt-1 text-sm text-[#101828]">
                    {formatDate(resolvedAuditData?.modifiedOn)}
                  </p>
                </div>
              </div>
            </div>
          )}
          <form onSubmit={onSubmit} className="space-y-6">
            {resolvedSuccessMessage && (
              <div className="rounded-2xl border border-green-200 bg-green-50 px-4 py-3 text-sm text-green-700">
                {resolvedSuccessMessage}
              </div>
            )}
            {resolvedError && (
              <div className="rounded-2xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-600">
                {resolvedError}
              </div>
            )}
            {showIdentifier && (
              <div>
                <label
                  htmlFor="identifier"
                  className="mb-2 block text-sm font-medium text-[#344054]"
                >
                  Identifier
                </label>
                <input
                  id="identifier"
                  type="text"
                  value={identifier || ""}
                  readOnly
                  className="h-14 w-full rounded-2xl border border-[#d0d5dd] bg-gray-100 px-5 text-[15px] text-[#667085] cursor-not-allowed outline-none"
                />
              </div>
            )}
            {showDescription && (
              <div>
                <label
                  htmlFor="description"
                  className="mb-2 block text-sm font-medium text-[#344054]"
                >
                  Description
                </label>
                <input
                  id="description"
                  type="text"
                  value={description || ""}
                  onChange={(e) => setDescription(e.target.value)}
                  required={descriptionRequired}
                  minLength={3}
                  maxLength={100}
                  className="h-14 w-full rounded-2xl border border-[#d0d5dd] bg-white px-5 text-[15px] text-[#101828] outline-none transition-all focus:border-[#2563eb] focus:ring-4 focus:ring-blue-100"
                />
                {descriptionRequired && (
                  <p className="mt-2 text-sm text-[#667085]">
                    Description must be between 3 and 100 characters
                  </p>
                )}
              </div>
            )}
            <div className="space-y-6">{children}</div>
            <div className="flex justify-end gap-4 pt-2">
              <button
                type="button"
                onClick={() => router.push(cancelPath)}
                className="h-13.5 rounded-2xl border border-[#d0d5dd] bg-white px-7 text-[15px] font-medium text-[#344054] transition-all hover:bg-gray-50"
              >
                Cancel
              </button>
              <button
                type="submit"
                disabled={resolvedLoading}
                className="h-13.5 rounded-2xl bg-[#2563eb] px-7 text-[15px] font-medium text-white shadow-lg shadow-blue-500/20 transition-all hover:bg-[#1d4ed8] disabled:opacity-50"
              >
                {resolvedLoading ? "Saving..." : "Save Changes"}
              </button>
            </div>
          </form>
        </div>
      </div>
    </>
  );
}

CommonEdit.propTypes = {
  title: PropTypes.string,
  subtitle: PropTypes.string,
  entityType: PropTypes.string,
  redirectPath: PropTypes.string,
  checkSuccess: PropTypes.bool,
  buildPayload: PropTypes.func,
  identifier: PropTypes.string,
  showIdentifier: PropTypes.bool,
  description: PropTypes.string,
  setDescription: PropTypes.func,
  showDescription: PropTypes.bool,
  descriptionRequired: PropTypes.bool,
  loading: PropTypes.bool,
  error: PropTypes.string,
  successMessage: PropTypes.string,
  onSubmit: PropTypes.func,
  cancelPath: PropTypes.string,
  children: PropTypes.node,
  auditData: PropTypes.object,
};
