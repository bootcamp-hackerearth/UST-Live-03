"use client";

import axios from "axios";
import CommonList from "@/components/CommonList";
import CommonForm from "@/components/CommonForm";
import PropTypes from "@/lib/propTypes";

export default function RolesPage() {
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
          ? "http://localhost:8080/api/role/add"
          : "http://localhost:8080/api/role/update";

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

      return true;
    } catch (error) {
      console.log(error);
      return false;
    }
  };

  return (
    <CommonList
      title="Role Management"
      subtitle="Manage system roles"
      apiUrl="http://localhost:8080/api/role/list"
      deleteUrl="http://localhost:8080/api/role/delete"
      dataKey="identifier"
      addButtonText="Add Role"
      FormComponent={RoleForm}
      formComponentProps={{ handleSubmit }}
      columns={[
        {
          label:
            "Role Name",
          key:
            "identifier",
        },
      ]}
    />
  );
}

RoleForm.propTypes = {
  mode: PropTypes.oneOf(["add", "edit"]).isRequired,
  data: PropTypes.object,
  onClose: PropTypes.func.isRequired,
  onSuccess: PropTypes.func,
  handleSubmit: PropTypes.func.isRequired,
};

function RoleForm(props) {
  const { handleSubmit, ...rest } = props;

  return (
    <CommonForm
      {...rest}
      title="Role"
      fields={[
        {
          name: "identifier",
          label: "Role Name",
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