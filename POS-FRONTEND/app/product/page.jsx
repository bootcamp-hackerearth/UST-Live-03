"use client";

import axios from "axios";
import CommonList from "@/components/CommonList";
import CommonForm from "@/components/CommonForm";
import PropTypes from "@/lib/propTypes";

export default function ProductsPage() {
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
          ? "http://localhost:8080/api/product/add"
          : "http://localhost:8080/api/product/update";

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
      title="Product Management"
      subtitle="Manage Products"
      apiUrl="http://localhost:8080/api/product/list"
      deleteUrl="http://localhost:8080/api/product/delete"
      dataKey="identifier"
      addButtonText="Add Product"
      FormComponent={ProductForm}
      formComponentProps={{ handleSubmit }}
      columns={[
        {
          label: "UID",
          key: "identifier",
        },
        {
          label: "Product Name",
          key: "name",
        },
        {
          label: "Unit",
          key: "unit",
        },
        {
          label: "Category",
          key: "category",
        },
        {
          label: "Brand",
          key: "brand",
        },
      ]}
    />
  );
}

ProductForm.propTypes = {
  mode: PropTypes.oneOf(["add", "edit"]).isRequired,
  data: PropTypes.object,
  onClose: PropTypes.func.isRequired,
  onSuccess: PropTypes.func,
  handleSubmit: PropTypes.func.isRequired,
};

function ProductForm(props) {
  const { handleSubmit, ...rest } = props;

  return (
    <CommonForm
      {...rest}
      title="Product"
      fields={[
        {
          name: "identifier",
          label: "UID",
        },
        {
          name: "name",
          label: "Product Name",
        },
        {
          name: "unit",
          label: "Unit",
        },
        {
          name: "category",
          label: "Category",
          type: "select",
          apiUrl: "http://localhost:8080/api/category/list",
        },
        {
          name: "brand",
          label: "Brand",
          type: "select",
          apiUrl: "http://localhost:8080/api/brand/list",
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