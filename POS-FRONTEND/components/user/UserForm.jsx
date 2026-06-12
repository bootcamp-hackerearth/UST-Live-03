"use client";

import PropTypes from "prop-types";
import MultiCheckBox from "@/components/common/MultiCheckBox";

export default function UserForm({
  name,
  setName,
  username,
  setUsername,
  phoneNo,
  setPhoneNo,
  roles,
  setRoles
}) {
  return (
    <>
      <div>

        <label
          htmlFor="fullName"
          className="block mb-2 text-sm font-medium text-[#344054]"
        >
          Full Name
        </label>

        <input
          id="fullName"
          type="text"
          value={name}
          onChange={(e) =>
            setName(e.target.value)
          }
          required
          placeholder="Enter Full Name"
          className="w-full h-14 px-5 rounded-2xl border border-[#d0d5dd] bg-white text-[#101828] outline-none transition-all focus:border-[#2563eb] focus:ring-4 focus:ring-blue-100"
        />

      </div>

      <div>

        <label
          htmlFor="username"
          className="block mb-2 text-sm font-medium text-[#344054]"
        >
          Username
        </label>

        <input
          id="username"
          type="text"
          value={username}
          onChange={(e) =>
            setUsername(e.target.value)
          }
          required
          placeholder="Enter Username"
          className="w-full h-14 px-5 rounded-2xl border border-[#d0d5dd] bg-white text-[#101828] outline-none transition-all focus:border-[#2563eb] focus:ring-4 focus:ring-blue-100"
        />

      </div>

      <div>

        <label
          htmlFor="phoneNo"
          className="block mb-2 text-sm font-medium text-[#344054]"
        >
          Phone Number
        </label>

        <input
          id="phoneNo"
          type="text"
          value={phoneNo}
          onChange={(e) => {

            const value =
              e.target.value;

            if (
              /^\d*$/.test(value) &&
              value.length <= 10
            ) {
              setPhoneNo(value);
            }

          }}
          required
          placeholder="Enter Phone Number"
          className="w-full h-14 px-5 rounded-2xl border border-[#d0d5dd] bg-white text-[#101828] outline-none transition-all focus:border-[#2563eb] focus:ring-4 focus:ring-blue-100"
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

UserForm.propTypes = {
  name: PropTypes.string.isRequired,
  setName: PropTypes.func.isRequired,
  username: PropTypes.string.isRequired,
  setUsername: PropTypes.func.isRequired,
  phoneNo: PropTypes.string.isRequired,
  setPhoneNo: PropTypes.func.isRequired,
  roles: PropTypes.array.isRequired,
  setRoles: PropTypes.func.isRequired
};