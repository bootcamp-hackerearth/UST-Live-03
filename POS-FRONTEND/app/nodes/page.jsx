"use client";

import axios from "axios";
import CommonList from "@/components/CommonList";
import CommonForm from "@/components/CommonForm";
import PropTypes from "@/lib/propTypes";

const nodeFields = [
  {
    name: "identifier",
    label: "Identifier",
  },
  {
    name: "path",
    label: "Path",
  },
  {
    name: "roles",
    label: "Roles",
    type: "multiselect",
    apiUrl: "http://localhost:8080/api/role/list",
  },
];

function NodeForm(props) {
  return (
    <CommonForm
      {...props}
      title="Node"
      fields={nodeFields}
      onSubmit={props.handleSubmit}
    />
  );
}

NodeForm.propTypes = {
  mode: PropTypes.oneOf(["add", "edit"]).isRequired,
  data: PropTypes.object,
  onClose: PropTypes.func.isRequired,
  onSuccess: PropTypes.func,
  handleSubmit: PropTypes.func.isRequired,
};

export default function NodesPage() {
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
          ? "http://localhost:8080/api/node/add"
          : "http://localhost:8080/api/node/update";

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
      title="Node Management"
      subtitle="Manage system nodes"
      apiUrl="http://localhost:8080/api/node/list"
      deleteUrl="http://localhost:8080/api/node/delete"
      dataKey="identifier"
      addButtonText="Add Node"
      FormComponent={NodeForm}
      formComponentProps={{ handleSubmit }}
      columns={[
        {
          label:
            "Identifier",
          key:
            "identifier",
        },
        {
          label: "Path",
          key: "path",
        },
        {
          label:
            "Roles",
          key: "roles",
        },
      ]}
    />
  );
}