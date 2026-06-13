'use client';
 
import { FaEdit, FaTrash } from 'react-icons/fa';
 
const ActionButtons = ({
  onEdit,
  onDelete
}) => {
 
  return (
 
    <div className="flex justify-center items-center gap-3">
 
      <button
        onClick={onEdit}
        className="flex items-center gap-2 bg-cyan-50 text-cyan-600 border boder-cyan-200 hover:bg-cyan-100 px-3 py-1.5 rounded-lg transition"
      >
 
        <FaEdit className="text-sm" />
 
        <span className="text-sm font-medium">
          Edit
        </span>
 
      </button>
 
      <button
        onClick={onDelete}
        className="flex items-center gap-2 bg-slate-100 text-slate-700 border border-slate-200 hover:bg-slate-200 px-3 py-1.5 rounded-lg transition"
      >
 
        <FaTrash className="text-sm" />
 
        <span className="text-sm font-medium">
          Delete
        </span>
 
      </button>
 
    </div>
 
  );
};
 
export default ActionButtons;