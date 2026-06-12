"use client";

import axios from "axios";
import CommonList from "@/components/CommonList";
import CommonForm from "@/components/CommonForm";
import PropTypes from "@/lib/propTypes";

const priceFields = [
  {
    name: "product",
    label: "Product",
    type: "select",
    apiUrl:
      "http://localhost:8080/api/product/list",
  },
  {
    name: "priceAmount",
    label: "Price Amount",
    type: "number",
  },
  {
    name: "priceType",
    label: "Price Type",
    type: "staticSelect",
    options: [
      "selling price",
      "cost price",
      "MRP",
    ],
  },
];

function PriceForm(props) {
  return (
    <CommonForm
      {...props}
      title="Price"
      fields={priceFields}
      onSubmit={props.handleSubmit}
    />
  );
}

PriceForm.propTypes = {
  mode: PropTypes.oneOf(["add", "edit"]).isRequired,
  data: PropTypes.object,
  onClose: PropTypes.func.isRequired,
  onSuccess: PropTypes.func,
  handleSubmit: PropTypes.func.isRequired,
};

export default function PricesPage() {
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
          ? "http://localhost:8080/api/price/add"
          : "http://localhost:8080/api/price/update";

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
      title="Price Management"
      subtitle="Manage product prices"
      apiUrl="http://localhost:8080/api/price/list"
      deleteUrl="http://localhost:8080/api/price/delete"
      dataKey="identifier"
      addButtonText="Add Price"
      FormComponent={PriceForm}
      formComponentProps={{ handleSubmit }}
      columns={[
        {
          label: "Price Code",
          key: "identifier",
        },
        {
          label: "Product",
          key: "product",
        },
        {
          label: "Price Amount",
          key: "priceAmount",
        },
        {
          label: "Price Type",
          key: "priceType",
        },
      ]}
    />
  );
}