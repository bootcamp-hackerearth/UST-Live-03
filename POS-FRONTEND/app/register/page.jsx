"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import {fetchActiveRoles, registerUser,} from "@/services/api";
import { validateUserForm } from "@/utils/userValidation";
import { AUTH_MESSAGES } from "@/constants/messages";
import AuthLayout from "@/components/auth/AuthLayout";
import AuthCard from "@/components/auth/AuthCard";
import AuthInput from "@/components/auth/AuthInput";
import AuthButton from "@/components/auth/AuthButton";
import Alert from "@/components/common/Alert";

export default function RegisterPage() {

  const router = useRouter();

  const [name, setName] = useState("");

  const [username, setUsername] = useState("");

  const [phoneNo, setPhoneNo] = useState("");

  const [password, setPassword] = useState("");

  const [confirmPassword, setConfirmPassword] =
    useState("");

  const [roles, setRoles] = useState([]);

  const [selectedRoles, setSelectedRoles] =
    useState([]);

  const [loading, setLoading] = useState(false);

  const [loadingRoles, setLoadingRoles] =
    useState(true);

  const [error, setError] = useState("");

  const [successMessage, setSuccessMessage] =
    useState("");

  useEffect(() => {

    loadRoles();

  }, []);

  const loadRoles = async () => {

    try {

      setLoadingRoles(true);

      const data = await fetchActiveRoles();

      setRoles(data || []);

    } catch (error) {

      console.log(error);

      setError("Failed to load roles");

    } finally {

      setLoadingRoles(false);

    }

  };

  const clearMessages = () => {

    if (error) {

      setError("");

    }

    if (successMessage) {

      setSuccessMessage("");

    }

  };

  const handleRoleChange = (roleIdentifier) => {

    clearMessages();

    if (selectedRoles.includes(roleIdentifier)) {

      setSelectedRoles(

        selectedRoles.filter(
          (role) => role !== roleIdentifier
        )

      );

    } else {

      setSelectedRoles([
        ...selectedRoles,
        roleIdentifier,
      ]);

    }

  };

  const handleSubmit = async (event) => {

    event.preventDefault();

    clearMessages();

    const validationError =
      validateUserForm({
        name,
        username,
        phoneNo,
        password,
        confirmPassword,
        roles: selectedRoles
      });

    if (validationError) {

      setError(validationError);

      return;

    }

    try {

      setLoading(true);

      const userData = {

        name,
        username,
        phoneNo,
        password,
        roles: selectedRoles,

      };

      const response =
        await registerUser(userData);

      if (response.success === false) {

        setError(response.message);

        return;

      }

      setSuccessMessage(
        AUTH_MESSAGES.REGISTER_SUCCESS
      );

      setTimeout(() => {

        router.push("/login");

      }, 2000);

    } catch (error) {

      console.log(error);

      setError("Something went wrong");

    } finally {

      setLoading(false);

    }

  };

  return (

    <AuthLayout

      leftContent={

        <>

          <div>

            <div className="mb-20">

              <h1 className="text-5xl font-bold leading-tight">

                Build your retail business smarter.

              </h1>

              <p className="mt-8 text-2xl text-gray-300 leading-relaxed">

                Create your account and start
                managing products, billing,
                inventory, customers, and sales
                seamlessly.

              </p>

            </div>

          </div>

          <div className="border-t border-gray-800 pt-10">

            <h2 className="text-4xl font-bold mb-6">

              Need assistance?

            </h2>

            <div className="text-xl text-gray-300 space-y-3">

              <p>+91 9876543210</p>

              <p>support@yourpos.com</p>

            </div>

          </div>

        </>

      }

    >

      <AuthCard
        title="Create Account"
        subtitle="Start using your POS system today"
      >

        <Alert
          type="error"
          message={error}
        />

        <Alert
          type="success"
          message={successMessage}
        />

        <form
          onSubmit={handleSubmit}
          className="space-y-6"
        >

          <AuthInput
            label="Full Name"
            type="text"
            value={name}
            onChange={(event) => {

              setName(event.target.value);

              clearMessages();

            }}
            placeholder="Enter your full name"
            disabled={loading}
          />

          <AuthInput
            label="Email Address"
            type="text"
            value={username}
            onChange={(event) => {

              setUsername(
                event.target.value
              );

              clearMessages();

            }}
            placeholder="Enter your email"
            disabled={loading}
          />

          <AuthInput
            label="Phone Number"
            type="text"
            value={phoneNo}
            onChange={(event) => {

              const value =
                event.target.value;

              if (
                /^\d*$/.test(value) &&
                value.length <= 10
              ) {

                setPhoneNo(value);

              }

              clearMessages();

            }}
            placeholder="Enter phone number"
            disabled={loading}
          />

          <AuthInput
            label="Password"
            type="password"
            value={password}
            onChange={(event) => {

              setPassword(
                event.target.value
              );

              clearMessages();

            }}
            placeholder="Enter password"
            disabled={loading}
          />

          <AuthInput
            label="Confirm Password"
            type="password"
            value={confirmPassword}
            onChange={(event) => {

              setConfirmPassword(
                event.target.value
              );

              clearMessages();

            }}
            placeholder="Confirm password"
            disabled={loading}
          />
          <div>

            <p className="text-lg font-medium text-gray-700 mb-4">

              Select Roles
            </p>

            {
              loadingRoles ? (

                <p className="text-gray-500">

                  Loading roles...
                </p>

              ) : (

                <div className="grid grid-cols-2 gap-4">

                  {

                    roles.map((role) => (

                      <label
                        key={role.identifier}
                        className="flex items-center gap-3 border border-gray-300 rounded-xl px-4 py-4 cursor-pointer hover:border-blue-600 transition-all bg-white"
                      >

                        <input
                          type="checkbox"
                          checked={
                            selectedRoles.includes(
                              role.identifier
                            )
                          }
                          onChange={() =>
                            handleRoleChange(
                              role.identifier
                            )
                          }
                          disabled={loading}
                          className="h-5 w-5 accent-blue-600"
                        />

                        <span className="text-gray-700 text-lg">

                          {role.identifier}

                        </span>

                      </label>

                    ))
                  }

                </div>
              )
            }
          </div>

          <div className="space-y-4 pt-4">

            <AuthButton
              text="Create Account"
              loadingText="Creating Account..."
              loading={loading}
            />

            <div className="text-center">

              <p className="text-gray-600">
                Already have an account?
              </p>

              <button
                type="button"
                disabled={loading}
                onClick={() =>
                  router.push("/login")
                }
                className="mt-2 text-[#0066ff] font-semibold hover:underline disabled:opacity-50"
              >
                Sign In
              </button>

            </div>
          </div>
        </form>
      </AuthCard>
    </AuthLayout>
  );
}