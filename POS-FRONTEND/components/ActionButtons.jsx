'use client';
 
import { FaEdit, FaTrash } from 'react-icons/fa';
import PropTypes from 'prop-types';
 
const ActionButtons = ({
  onView,
  onEdit,
  onDelete
}) => {
 
  return (
 
    <div className="flex justify-center items-center gap-3">
      <button
        onClick={onEdit}
        className="flex items-center gap-2 bg-sky-50 text-sky-700 border border-sky-200 hover:bg-sky-100 px-3 py-1.5 rounded-lg transition"
      >
        <FaEdit className="text-sm" />
 
        <span className="text-sm font-medium">
          Edit
        </span>
      </button>
 
      <button
        onClick={onDelete}
        className="flex items-center gap-2 bg-rose-50 text-rose-700 border border-rose-200 hover:bg-rose-100 px-3 py-1.5 rounded-lg transition"
      >
        <FaTrash className="text-sm" />
 
        <span className="text-sm font-medium">
          Delete
        </span>
      </button>

      <button
        onClick={onView}
        className="flex items-center gap-2 bg-emerald-50 text-emerald-700 border border-emerald-200 hover:bg-emerald-100 px-3 py-1.5 rounded-lg transition"
      >
        View
      </button>
    </div>
  );
};

ActionButtons.propTypes = {
  onView: PropTypes.func.isRequired,
  onEdit: PropTypes.func.isRequired,
  onDelete: PropTypes.func.isRequired
};

export default ActionButtons;