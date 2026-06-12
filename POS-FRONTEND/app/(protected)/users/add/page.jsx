"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import CommonAdd from "@/components/common/CommonAdd";
import MultiCheckBox from "@/components/common/MultiCheckBox";
import { registerUser } from "@/services/api";
import { validateUserForm } from "@/utils/userValidation";

export default function AddUser() {
  const router = useRouter();

  const [name,setName] =
    useState("");

  const [username,setUsername] =
    useState("");

  const [phoneNo,setPhoneNo] =
    useState("");

  const [password,setPassword] =
    useState("");

  const [
    confirmPassword,
    setConfirmPassword
  ] = useState("");

  const [roles,setRoles] =
    useState([]);

  const [loading,setLoading] =
    useState(false);

  const [error,setError] =
    useState("");

  const [
    successMessage,
    setSuccessMessage
  ] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      setLoading(true);

      setError("");

      setSuccessMessage("");

      const validationError =
        validateUserForm({
          name,
          username,
          phoneNo,
          password,
          confirmPassword,
          roles
        });

      if (validationError) {
        setError(validationError);

        return;
      }

      const payload = {
        name,
        username,
        phoneNo,
        password,
        roles
      };

        const response =
        await registerUser(
            payload
        );

      if (!response.success) {
        setError(
          response.message ||
          "Failed to create user"
        );

        return;
      }

      setSuccessMessage(
        "User created successfully"
      );

      setTimeout(() => {
        router.push("/users");
      },1000);
    } catch (error) {
      console.log(error);

      setError(
        error?.response?.data
          ?.message ||
        "Failed to create user"
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <CommonAdd
      title="Add User"
      subtitle="Create a new system user"
      loading={loading}
      error={error}
      successMessage={successMessage}
      onSubmit={handleSubmit}
      cancelPath="/users"
      showIdentifier={false}
      showDescription={false}
    >
      <div>
        <label htmlFor="fullName" className="block mb-2 text-sm font-medium text-[#344054]">
          Full Name
        </label>

        <input
          id="fullName"
          type="text"
          value={name}
          onChange={(e) =>
            setName(
              e.target.value
            )
          }
          required
          placeholder="Enter Full Name"
          className="w-full h-14 px-5 rounded-2xl border border-[#d0d5dd] bg-white text-[#101828] outline-none transition-all focus:border-[#2563eb] focus:ring-4 focus:ring-blue-100"
        />
      </div>

      <div>
        <label htmlFor="email" className="block mb-2 text-sm font-medium text-[#344054]">
          Email Address
        </label>

        <input
          id="email"
          type="text"
          value={username}
          onChange={(e) =>
            setUsername(
              e.target.value
            )
          }
          required
          placeholder="Enter Email Address"
          className="w-full h-14 px-5 rounded-2xl border border-[#d0d5dd] bg-white text-[#101828] outline-none transition-all focus:border-[#2563eb] focus:ring-4 focus:ring-blue-100"
        />
      </div>

      <div>
        <label htmlFor="phoneNo" className="block mb-2 text-sm font-medium text-[#344054]">
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

      <div>
        <label htmlFor="password" className="block mb-2 text-sm font-medium text-[#344054]">
          Password
        </label>

        <input
          id="password"
          type="password"
          value={password}
          onChange={(e) =>
            setPassword(
              e.target.value
            )
          }
          required
          placeholder="Enter Password"
          className="w-full h-14 px-5 rounded-2xl border border-[#d0d5dd] bg-white text-[#101828] outline-none transition-all focus:border-[#2563eb] focus:ring-4 focus:ring-blue-100"
        />
      </div>

      <div>
        <label htmlFor="confirmPassword" className="block mb-2 text-sm font-medium text-[#344054]">
          Confirm Password
        </label>

        <input
          id="confirmPassword"
          type="password"
          value={confirmPassword}
          onChange={(e) =>
            setConfirmPassword(
              e.target.value
            )
          }
          required
          placeholder="Confirm Password"
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
    </CommonAdd>
  );
}