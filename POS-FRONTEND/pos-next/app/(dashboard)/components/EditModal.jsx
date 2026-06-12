"use client";
import PropTypes from "prop-types";
import GenericForm from "./GenericForm";

export default function EditModal({
  row,
  config,
  onClose,
  onSuccess,  
  onCancel,
}) {
  return (
    <div
      className=" fixed inset-0 z-50 flex items-center justify-center bg-black/40"
    >
      <div
        className=" w-full max-w-4xl rounded-2xl bg-white shadow-2xl max-h-[90vh] overflow-y-auto"
      >
        <div className="flex items-center justify-between border-b px-6 py-4">
          <h2 className="text-lg text-black font-semibold">
            Edit Record
          </h2>

          <button
            onClick={onClose}
            className="text-2xl"
          >
            ×
          </button>
        </div>
        <div className="p-6">
          <GenericForm
            config={config}
            mode="edit"
            initialData={row}
            onSuccess={onSuccess}
            onCancel={onClose}
          />
        </div>
      </div>
    </div>
  );
}

EditModal.propTypes = {
  row: PropTypes.object,
  config: PropTypes.object.isRequired,
  onClose: PropTypes.func.isRequired,
  onSuccess: PropTypes.func,
  onCancel: PropTypes.func,
};