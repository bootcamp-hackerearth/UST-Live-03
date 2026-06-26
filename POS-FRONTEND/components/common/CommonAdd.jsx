"use client";

import { useState } from "react";
import PropTypes from "prop-types";
import { useRouter } from "next/navigation";
import { addItem } from "@/services/api";

export default function CommonAdd({
  title,
  subtitle,
  identifier,
  setIdentifier,
  description,
  setDescription,
  loading: externalLoading,
  error: externalError,
  successMessage: externalSuccessMessage,
  onSubmit,
  entityConfig,
  cancelPath,
  showIdentifier = true,
  showDescription = true,
  children
}) {
  const router = useRouter();

  const [internalIdentifier, setInternalIdentifier] = useState("");
  const [internalDescription, setInternalDescription] = useState("");
  const [internalLoading, setInternalLoading] = useState(false);
  const [internalError, setInternalError] = useState("");
  const [internalSuccess, setInternalSuccess] = useState("");

  const isSimpleMode = !!entityConfig;

  const resolvedIdentifier = isSimpleMode ? internalIdentifier : identifier;
  const resolvedSetIdentifier = isSimpleMode ? setInternalIdentifier : setIdentifier;
  const resolvedDescription = isSimpleMode ? internalDescription : description;
  const resolvedSetDescription = isSimpleMode ? setInternalDescription : setDescription;
  const resolvedLoading = isSimpleMode ? internalLoading : externalLoading;
  const resolvedError = isSimpleMode ? internalError : externalError;
  const resolvedSuccess = isSimpleMode ? internalSuccess : externalSuccessMessage;

  const internalSubmit = async (e) => {
    e.preventDefault();
    try {
      setInternalLoading(true);
      setInternalError("");
      setInternalSuccess("");
      await addItem(entityConfig.entityName, {
        identifier: internalIdentifier,
        description: internalDescription,
      });
      setInternalSuccess(entityConfig.successMessage);
      setTimeout(() => {
        router.push(cancelPath);
      }, 1000);
    } catch (err) {
      setInternalError(
        err?.response?.data?.message || `Failed to add ${entityConfig.entityName}`
      );
    } finally {
      setInternalLoading(false);
    }
  };

  const resolvedSubmit = isSimpleMode ? internalSubmit : onSubmit;

  return (
    <div className="max-w-4xl mx-auto">
      <div className="bg-white rounded-4xl border border-[#eaecf0] shadow-sm px-10 py-10">
        <div className="mb-10">
          <h1 className="text-[30px] font-semibold text-[#101828]">
            {title}
          </h1>
          <p className="mt-2 text-[15px] text-[#667085]">
            {subtitle}
          </p>
        </div>

        <form onSubmit={resolvedSubmit} className="space-y-7">
          {resolvedSuccess && (
            <div className="px-4 py-3 rounded-2xl border border-green-200 bg-green-50 text-sm text-green-700">
              {resolvedSuccess}
            </div>
          )}

          {resolvedError && (
            <div className="px-4 py-3 rounded-2xl border border-red-200 bg-red-50 text-sm text-red-600">
              {resolvedError}
            </div>
          )}

          {showIdentifier && (
            <div>
              <label htmlFor="identifier" className="block mb-2 text-sm font-medium text-[#344054]">
                Identifier
              </label>
              <input
                id="identifier"
                type="text"
                value={resolvedIdentifier}
                onChange={(e) => resolvedSetIdentifier(e.target.value)}
                required
                className="w-full h-14 px-5 rounded-2xl border border-[#d0d5dd] bg-white text-[#101828] outline-none transition-all focus:border-[#2563eb] focus:ring-4 focus:ring-blue-100"
              />
            </div>
          )}

          {showDescription && (
            <div>
              <label htmlFor="description" className="block mb-2 text-sm font-medium text-[#344054]">
                Description
              </label>
              <input
                id="description"
                type="text"
                value={resolvedDescription}
                onChange={(e) => resolvedSetDescription(e.target.value)}
                required
                className="w-full h-14 px-5 rounded-2xl border border-[#d0d5dd] bg-white text-[#101828] outline-none transition-all focus:border-[#2563eb] focus:ring-4 focus:ring-blue-100"
              />
            </div>
          )}

          {children}

          <div className="flex justify-end gap-4 pt-4">
            <button
              type="button"
              onClick={() => router.push(cancelPath)}
              className="h-14 px-8 rounded-2xl border border-[#d0d5dd] bg-white text-[#344054] font-medium transition-all hover:bg-gray-50"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={resolvedLoading}
              className="h-14 px-8 rounded-2xl bg-[#2563eb] text-white font-medium shadow-lg shadow-blue-500/20 transition-all hover:bg-[#1d4ed8] disabled:opacity-50"
            >
              {resolvedLoading ? "Saving..." : "Create"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

CommonAdd.propTypes = {
  title: PropTypes.string,
  subtitle: PropTypes.string,
  identifier: PropTypes.string,
  setIdentifier: PropTypes.func,
  description: PropTypes.string,
  setDescription: PropTypes.func,
  loading: PropTypes.bool,
  error: PropTypes.string,
  successMessage: PropTypes.string,
  onSubmit: PropTypes.func,
  entityConfig: PropTypes.shape({
    entityName: PropTypes.string.isRequired,
    successMessage: PropTypes.string.isRequired,
  }),
  cancelPath: PropTypes.string,
  showIdentifier: PropTypes.bool,
  showDescription: PropTypes.bool,
  children: PropTypes.node,
};