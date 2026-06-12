"use client";

import PropTypes from "prop-types";
import MultiCheckBox from "@/components/common/MultiCheckBox";

export default function NodeFields({
  path,
  setPath,
  roles,
  setRoles,
}) {
  return (
    <>
      <div>
        <label
          htmlFor="path"
          className="block mb-2 text-sm font-medium text-[#475467]"
        >
          Path
        </label>

        <input
          id="path"
          type="text"
          value={path}
          onChange={(e) => setPath(e.target.value)}
          required
          minLength={2}
          maxLength={100}
          placeholder="/products"
          className="w-full h-14 px-4 rounded-2xl border border-[#d0d5dd] bg-white text-sm text-[#101828] outline-none transition-all focus:border-blue-500 focus:ring-4 focus:ring-blue-100"
        />
      </div>

      <MultiCheckBox
        label="Roles"
        model="role"
        values={roles}
        onChange={setRoles}
        required
      />
    </>
  );
}

NodeFields.propTypes = {
  path: PropTypes.string,
  setPath: PropTypes.func.isRequired,
  roles: PropTypes.array,
  setRoles: PropTypes.func.isRequired,
};