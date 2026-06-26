'use client';

import PropTypes from 'prop-types';
import { FaEdit, FaTrash } from 'react-icons/fa';

const ActionButtons = ({
  onView,
  onEdit,
  onDelete
}) => {

  return (

    <div className="flex justify-center items-center gap-3">

      <button
        onClick={onEdit}
        className="flex items-center gap-2 bg-blue-50 text-blue-600 hover:bg-blue-100 px-3 py-1.5 rounded-lg transition"
      >

        <FaEdit className="text-sm" />

        <span className="text-sm font-medium">
          Edit
        </span>

      </button>

      <button
        onClick={onDelete}
        className="flex items-center gap-2 bg-red-50 text-red-600 hover:bg-red-100 px-3 py-1.5 rounded-lg transition"
      >

        <FaTrash className="text-sm" />

        <span className="text-sm font-medium">
          Delete
        </span>

      </button>

      <button
        onClick={onView}
        className="bg-blue-500 text-white px-3 py-1 rounded"
      >
        View
      </button>

    </div>

  );
};

ActionButtons.propTypes={
onView: PropTypes.func.isRequired,
onEdit: PropTypes.func.isRequired,
onDelete: PropTypes.func.isRequired,
};

export default ActionButtons;