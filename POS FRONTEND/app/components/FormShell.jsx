"use client";
import PropTypes from "prop-types";
import { useRouter } from "next/navigation";
import FieldRenderer from "./FieldRenderer";

export default function FormShell({
  title,
  mode,
  error,
  onSubmit,
  loading,
  showDescription,
  description,
  onDescriptionChange,
  extraFields,
  values,
  onChange,
  identifierSlot,
}) {
  const router = useRouter();
  const isAdd = mode === "add";
  const verb = isAdd ? "Add" : "Update";
  const loadingLabel = isAdd ? "Saving..." : "Updating...";

  return (
    <div className="w-full max-w-3xl mx-auto">
      <div className="mb-6">
        <h2 className="text-2xl font-bold text-slate-900">
          {verb} {title}
        </h2>
        <p className="mt-1 text-sm text-slate-500">
          {isAdd
            ? `Fill in the details to add a new ${title.toLowerCase()}.`
            : `Edit the details for this ${title.toLowerCase()}.`}
        </p>
      </div>

      <form
        onSubmit={onSubmit}
        className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm md:p-8"
      >
        {error && (
          <div className="mb-5 rounded-xl bg-rose-50 px-4 py-3 text-sm font-medium text-rose-600">
            {error}
          </div>
        )}

        <div className="grid gap-5 md:grid-cols-2">
          {identifierSlot}

          {showDescription && (
            <div className="flex flex-col gap-1 md:col-span-2">
              <label htmlFor="description" className="text-sm font-semibold text-gray-600">
                Description
              </label>
              <textarea
                id="description"
                placeholder="Enter description"
                value={description}
                onChange={(event) => onDescriptionChange(event.target.value)}
                rows={3}
                className="border border-gray-300 rounded-xl px-4 py-3 text-sm text-gray-800 bg-white outline-none focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100 transition resize-none"
              />
            </div>
          )}

          {extraFields.map((field) => (
            <div key={field.key}>
              <FieldRenderer field={field} values={values} onChange={onChange} />
            </div>
          ))}
        </div>

        <div className="mt-8 flex flex-col-reverse gap-3 sm:flex-row sm:justify-end">
          <button
            type="button"
            onClick={() => router.back()}
            className="inline-flex items-center justify-center rounded-xl bg-slate-100 px-5 py-3 text-sm font-semibold text-slate-700 transition hover:bg-slate-200"
          >
            Cancel
          </button>
          <button
            type="submit"
            disabled={loading}
            className="inline-flex items-center justify-center rounded-xl bg-emerald-600 px-5 py-3 text-sm font-semibold text-white transition hover:bg-emerald-700 disabled:cursor-not-allowed disabled:opacity-60"
          >
            {loading ? loadingLabel : `${verb} ${title}`}
          </button>
        </div>
      </form>
    </div>
  );
}

FormShell.propTypes = {
  title: PropTypes.string.isRequired,
  mode: PropTypes.oneOf(["add", "update"]).isRequired,
  error: PropTypes.string,
  onSubmit: PropTypes.func.isRequired,
  loading: PropTypes.bool.isRequired,
  showDescription: PropTypes.bool,
  description: PropTypes.string,
  onDescriptionChange: PropTypes.func,
  extraFields: PropTypes.arrayOf(PropTypes.object),
  values: PropTypes.object,
  onChange: PropTypes.func,
  identifierSlot: PropTypes.node,
};

FormShell.defaultProps = {
  error: "",
  showDescription: false,
  description: "",
  onDescriptionChange: () => {},
  extraFields: [],
  values: {},
  onChange: () => {},
  identifierSlot: null,
};