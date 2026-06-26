import PropTypes from "prop-types";

export default function DeleteModal({
  open,
  title,
  item,
  onClose,
  onConfirm,
}) {

  if (!open) {

    return null;

  }

  return (

    <div className="fixed inset-0 bg-black/40 backdrop-blur-sm flex items-center justify-center z-50">

      <div className="bg-white max-w-md rounded-[30px] p-8 shadow-2xl">

        <h2 className="text-2xl font-bold text-gray-900">

          Delete {title}

        </h2>

        <p className="text-gray-500 mt-4 leading-relaxed">
          Are you sure you want to delete {" "}
          <span className="font-semibold text-gray-900">{item?.identifier}</span>?
        </p>

        <div className="flex justify-end gap-4 mt-8">

          <button
            type="button"
            onClick={onClose}
            className="h-12 px-6 rounded-2xl border border-gray-300 text-gray-700 font-medium hover:bg-gray-50 transition-all"
          >

            Cancel

          </button>

          <button
            type="button"
            onClick={onConfirm}
            className="h-12 px-6 rounded-2xl bg-red-600 hover:bg-red-500 text-white font-medium transition-all"
          >

            Delete

          </button>

        </div>

      </div>

    </div>

  );

}

DeleteModal.propTypes = {
  open: PropTypes.bool,
  title: PropTypes.string,
  item: PropTypes.shape({
    identifier: PropTypes.string,
  }),
  onClose: PropTypes.func,
  onConfirm: PropTypes.func,
};