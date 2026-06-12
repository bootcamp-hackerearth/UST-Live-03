"use client";

import axios from "axios";
import CommonList from "@/components/CommonList";
import CommonForm from "@/components/CommonForm";
import PropTypes from "@/lib/propTypes";

export default function UsersPage() {
  const handleSubmit = async (
    formData,
    mode
  ) => {
    try {
      const token =
        localStorage.getItem(
          "token"
        );

      const url =
        mode === "add"
          ? "http://localhost:8080/api/user/register"
          : "http://localhost:8080/api/user/update";

      console.log(
        "Submitting User:",
        formData
      );

      const response =
        await axios.post(
          url,
          formData,
          {
            headers: {
              Authorization: `Bearer ${token}`,
              "Content-Type":
                "application/json",
            },
          }
        );

      console.log(
        "Response:",
        response.data
      );

      return true;
    } catch (error) {
      console.log(
        "User Submit Error:"
      );
      console.log(error);
      console.log(
        error?.response?.data
      );

      return false;
    }
  };

  return (
    <CommonList
      title="User Management"
      subtitle="Manage system users"
      apiUrl="http://localhost:8080/api/user/list"
      deleteUrl="http://localhost:8080/api/user/delete"
      dataKey="username"
      addButtonText="Add User"
      FormComponent={UserForm}
      formComponentProps={{ handleSubmit }}
      columns={[
        {
          label: "Name",
          key: "name",
        },
        {
          label: "Username",
          key: "username",
        },
        {
          label:
            "Phone Number",
          key: "phoneNo",
        },
        {
          label: "Roles",
          key: "roles",
        },
      ]}
    />
  );
}

function UserForm(props) {
  const { handleSubmit, ...rest } = props;

  return (
    <CommonForm
      {...rest}
      title="User"
      fields={[
        { name: "name", label: "Name" },
        { name: "username", label: "Username" },
        { name: "phoneNo", label: "Phone Number" },
        { name: "password", label: "Password", type: "password" },
        {
          name: "roles",
          label: "Roles",
          type: "multiselect",
          apiUrl: "http://localhost:8080/api/role/list",
        },
      ]}
      onSubmit={async (formData) => {
        const success = await handleSubmit(formData, props.mode);

        if (success && props.onSuccess) {
          props.onSuccess();
        }
      }}
    />
  );
}

UserForm.propTypes = {
  mode: PropTypes.oneOf(["add", "edit"]).isRequired,
  data: PropTypes.object,
  onClose: PropTypes.func.isRequired,
  onSuccess: PropTypes.func,
  handleSubmit: PropTypes.func.isRequired,
};