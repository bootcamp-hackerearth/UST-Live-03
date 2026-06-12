"use client";

import {useEffect,useState} from "react";
import {useRouter} from "next/navigation";
import PropTypes from "prop-types";

import CommonEdit from "@/components/common/CommonEdit";
import UserForm from "@/components/user/UserForm";

import {updateItem} from "@/services/api";
import {validateEmail,validatePhone} from "@/utils/validation";
import {AUTH_MESSAGES} from "@/constants/messages";
import {useAuth} from "@/context/AuthContext";

export default function UserEditor({
  title,
  subtitle,
  cancelPath,
  loadUser,
  afterSaveRedirect,
  refreshCurrentUser = false,
  refreshCurrentUserIfEditingSelf = false
}) {

  const router = useRouter();
  const {user} = useAuth();
  const [id,setId] = useState(null);
  const [username,setUsername] = useState("");
  const [name,setName] = useState("");
  const [phoneNo,setPhoneNo] = useState("");
  const [roles,setRoles] = useState([]);
  const [loading,setLoading] = useState(false);
  const [pageLoading,setPageLoading] = useState(true);
  const [error,setError] = useState("");
  const [successMessage,setSuccessMessage] = useState("");

  useEffect(() => {
    fetchUser();
  }, []);

  const fetchUser = async () => {

    try {

      setPageLoading(true);

      setError("");

      const response =
        await loadUser();

      setId(response.id);

      setUsername(response.username || "");

      setName(response.name || "");

      setPhoneNo(response.phoneNo || "");

      setRoles(response.roles || []);

    } catch (error) {

      console.log(error);

      setError("Failed to load user");

    } finally {

      setPageLoading(false);

    }

  };

  const validateForm = () => {

    if (
      name.trim() === "" ||
      username.trim() === "" ||
      phoneNo.trim() === ""
    ) {
      return AUTH_MESSAGES.ALL_FIELDS_REQUIRED;
    }

    if (!validateEmail(username)) {
      return AUTH_MESSAGES.INVALID_EMAIL;
    }

    if (!validatePhone(phoneNo)) {
      return AUTH_MESSAGES.INVALID_PHONE;
    }

    if (roles.length === 0) {
      return AUTH_MESSAGES.ROLE_REQUIRED;
    }

    return null;

  };

  const handleSubmit = async (e) => {

    e.preventDefault();

    try {

      setLoading(true);

      setError("");

      setSuccessMessage("");

      const validationError =
        validateForm();

      if (validationError) {

        setError(validationError);

        return;

      }

      const payload = {
        id,
        name,
        username,
        phoneNo,
        roles
      };

      const response =
        await updateItem(
          "user",
          payload
        );

      if (!response.success) {

        setError(
          response.message ||
          "Failed to update user"
        );

        return;

      }

      if (refreshCurrentUser) {
          setSuccessMessage("Username updated successfully, please login to continue");
          setTimeout(() => {
            router.push("/login?message=Username updated successfully, please login to continue");
          }, 1500);
          return;
        }

      if (
        refreshCurrentUserIfEditingSelf &&
        user?.id === id
      ) {
        setSuccessMessage("Username updated successfully, login to continue");
        setTimeout(() => {
          router.push("/login?message=Username updated successfully, please login to continue");
        }, 1500);
        return;
      }

      setSuccessMessage(
        "User updated successfully"
      );

      setTimeout(() => {

        router.push(
          afterSaveRedirect
        );

      },1000);

    } catch (error) {

      console.log(error);

      setError(
        error?.response?.data
          ?.message ||
        "Failed to update user"
      );

    } finally {

      setLoading(false);

    }

  };

  if (pageLoading) {

    return (

      <div className="flex items-center justify-center py-20">

        <p className="text-[#667085]">
          Loading user...
        </p>

      </div>

    );

  }

  return (

    <CommonEdit
      title={title}
      subtitle={subtitle}
      showIdentifier={false}
      loading={loading}
      error={error}
      successMessage={successMessage}
      onSubmit={handleSubmit}
      cancelPath={cancelPath}
      showDescription={false}
    >

      <UserForm
        name={name}
        setName={setName}
        username={username}
        setUsername={setUsername}
        phoneNo={phoneNo}
        setPhoneNo={setPhoneNo}
        roles={roles}
        setRoles={setRoles}
      />

    </CommonEdit>

  );
}

UserEditor.propTypes = {
  title: PropTypes.string.isRequired,
  subtitle: PropTypes.string.isRequired,
  cancelPath: PropTypes.string.isRequired,
  loadUser: PropTypes.func.isRequired,
  afterSaveRedirect: PropTypes.string.isRequired,
  refreshCurrentUser: PropTypes.bool,
  refreshCurrentUserIfEditingSelf: PropTypes.bool
};
