"use client";

import axios from "axios";
import CommonList from "@/components/CommonList";
import CommonForm from "@/components/CommonForm";
import PropTypes from "@/lib/propTypes";

const categoryFields = [
  {
    name: "identifier",
    label: "Category Name",
    required: true,
  },
  {
    name: "superCategory",
    label: "Super Category",
    type: "multiselect",
    required: false,
    apiUrl: "http://localhost:8080/api/category/list",
  },
];

function CategoryForm(props) {
  return (
    <CommonForm
      {...props}
      title="Category"
      fields={categoryFields}
      onSubmit={props.handleSubmit}
    />
  );
}

CategoryForm.propTypes = {
  mode: PropTypes.oneOf(["add", "edit"]).isRequired,
  data: PropTypes.object,
  onClose: PropTypes.func.isRequired,
  onSuccess: PropTypes.func,
  handleSubmit: PropTypes.func.isRequired,
};

export default function CategoriesPage() {
  const handleSubmit = async (
    formData,
    mode
  ) => {
    try {
      const token =
        localStorage.getItem(
          "token"
        );

      if (
        !formData.superCategory
      ) {
        formData.superCategory =
          [];
      }

      console.log(
        "Category Payload:",
        formData
      );

      const url =
        mode === "add"
          ? "http://localhost:8080/api/category/add"
          : "http://localhost:8080/api/category/update";

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
      console.log(
        "Category Error:"
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
      title="Category Management"
      subtitle="Manage product categories"
      apiUrl="http://localhost:8080/api/category/list"
      deleteUrl="http://localhost:8080/api/category/delete"
      dataKey="identifier"
      addButtonText="Add Category"
      FormComponent={CategoryForm}
      formComponentProps={{ handleSubmit }}
      columns={[
        {
          label:
            "Category Name",
          key:
            "identifier",
        },
        {
          label:
            "Super Category",
          key:
            "superCategory",
        },
      ]}
    />
  );
}