"use client";

import PropTypes from "prop-types";
import { useRouter } from "next/navigation";

export default function CommonAdd({
  title,
  subtitle,
  identifier,
  setIdentifier,
  description,
  setDescription,
  loading,
  error,
  successMessage,
  onSubmit,
  cancelPath,
  showIdentifier = true,
  showDescription = true,
  children
}) {
  const router = useRouter();

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

        <form
          onSubmit={onSubmit}
          className="space-y-7"
        >
          {successMessage && (
            <div className="px-4 py-3 rounded-2xl border border-green-200 bg-green-50 text-sm text-green-700">
              {successMessage}
            </div>
          )}

          {error && (
            <div className="px-4 py-3 rounded-2xl border border-red-200 bg-red-50 text-sm text-red-600">
              {error}
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
                value={identifier}
                onChange={(e) =>
                  setIdentifier(e.target.value)
                }
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
                value={description}
                onChange={(e) =>
                  setDescription(e.target.value)
                }
                required
                className="w-full h-14 px-5 rounded-2xl border border-[#d0d5dd] bg-white text-[#101828] outline-none transition-all focus:border-[#2563eb] focus:ring-4 focus:ring-blue-100"
              />
            </div>
          )}

          {children}

          <div className="flex justify-end gap-4 pt-4">
            <button
              type="button"
              onClick={() =>
                router.push(cancelPath)
              }
              className="h-14 px-8 rounded-2xl border border-[#d0d5dd] bg-white text-[#344054] font-medium transition-all hover:bg-gray-50"
            >
              Cancel
            </button>

            <button
              type="submit"
              disabled={loading}
              className="h-14 px-8 rounded-2xl bg-[#2563eb] text-white font-medium shadow-lg shadow-blue-500/20 transition-all hover:bg-[#1d4ed8] disabled:opacity-50"
            >
              {loading ? "Saving..." : "Create"}
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
  cancelPath: PropTypes.string,
  showIdentifier: PropTypes.bool,
  showDescription: PropTypes.bool,
  children: PropTypes.node,
};