"use client";

import PropTypes from "prop-types";
import { useRouter } from "next/navigation";

export default function CommonEdit({
  title,
  subtitle,
  identifier,
  showIdentifier = true,
  description,
  setDescription,
  showDescription = true,
  descriptionRequired = true,
  loading,
  error,
  successMessage,
  onSubmit,
  cancelPath,
  children
}) {
  const router = useRouter();

  return (
    <div className="w-full flex justify-center">
      <div className="w-full max-w-215 bg-white rounded-[28px] border border-[#e4e7ec] shadow-sm px-9 py-8">
        <div className="mb-8">
          <h1 className="text-[30px] font-semibold tracking-[-0.02em] text-[#101828]">
            {title}
          </h1>
          <p className="mt-2 text-[15px] text-[#667085]">
            {subtitle}
          </p>
        </div>

        <form onSubmit={onSubmit} className="space-y-6">
          {successMessage && (
            <div className="rounded-2xl border border-green-200 bg-green-50 px-4 py-3 text-sm text-green-700">
              {successMessage}
            </div>
          )}

          {error && (
            <div className="rounded-2xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-600">
              {error}
            </div>
          )}

          {showIdentifier && (
            <div>
              <label htmlFor="identifier" className="mb-2 block text-sm font-medium text-[#344054]">
                Identifier
              </label>
              <input
                id="identifier"
                type="text"
                value={identifier}
                readOnly
                className="h-14 w-full rounded-2xl border border-[#d0d5dd] bg-gray-100 px-5 text-[15px] text-[#667085] cursor-not-allowed outline-none"
              />
            </div>
          )}

          {showDescription && (
            <div>
              <label htmlFor="description" className="mb-2 block text-sm font-medium text-[#344054]">
                Description
              </label>
              <input
                id="description"
                type="text"
                value={description}
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
              disabled={loading}
              className="h-13.5 rounded-2xl bg-[#2563eb] px-7 text-[15px] font-medium text-white shadow-lg shadow-blue-500/20 transition-all hover:bg-[#1d4ed8] disabled:opacity-50"
            >
              {loading ? "Saving..." : "Save Changes"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

CommonEdit.propTypes = {
  title: PropTypes.string,
  subtitle: PropTypes.string,
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
};