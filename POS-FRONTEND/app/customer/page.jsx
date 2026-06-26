"use client";

import PropTypes from "prop-types";
import CommonList from "@/components/CommonList";
import CommonForm from "@/components/CommonForm";

const createAddressFields = (prefix, label) => [
  {
    name: `${prefix}.addressLine`,
    placeholder: `${label} Address`,
  },
  {
    name: `${prefix}.city`,
    placeholder: `${label} City`,
  },
  {
    name: `${prefix}.state`,
    placeholder: `${label} State`,
  },
  {
    name: `${prefix}.zipcode`,
    placeholder: `${label} Zipcode`,
  },
  {
    name: `${prefix}.country`,
    placeholder: `${label} Country`,
  },
];

const customerFields = [
  {
    name: "identifier",
    placeholder: "Identifier",
    disableOnEdit: true,
  },
  {
    name: "name",
    placeholder: "Customer Name",
  },
  {
    name: "phoneNo",
    placeholder: "Phone Number",
  },
  {
    name: "userType",
    placeholder: "User Type",
    type: "select",
    options: [
      { value: "Retail", label: "Retail" },
      { value: "Wholesale", label: "Wholesale" },
      {value:"Dealer",label:"Dealer"},
    ],
  },
  {
    name: "balance",
    placeholder: "Balance",
  },
  {
    name: "creditLimit",
    placeholder: "Credit Limit",
  },
  ...createAddressFields("shippingAddress", "Shipping"),
  ...createAddressFields("billingAddress", "Billing"),
];

const customerValidate = (formData) => {
  const errors = {};

  if (!formData.identifier?.trim()) {
    errors.identifier = "Identifier is required";
  }

  if (!formData.name?.trim()) {
    errors.name = "Customer Name is required";
  }

  if (!formData.phoneNo?.toString().trim()) {
    errors.phoneNo = "Phone Number is required";
  }

  if (!formData.userType) {
    errors.userType = "User Type is required";
  }

  return errors;
};

const CustomerForm = (props) => {
  const rest = { ...props };
  delete rest.fields;

  const handleSubmit = (formData) => {
    props.onSubmit({
      ...formData,
      status: props.data?.status ?? true,
    });
  };

  return (
    <CommonForm
      {...rest}
      title="Customer"
      fields={customerFields}
      validate={customerValidate}
      onSubmit={handleSubmit}
    />
  );
};

CustomerForm.propTypes = {
  onSubmit: PropTypes.func.isRequired,
  data: PropTypes.shape({
    status: PropTypes.bool,
  }),
  fields: PropTypes.arrayOf(PropTypes.object),
  mode: PropTypes.string,
  onClose: PropTypes.func,
  validate: PropTypes.func,
};

CustomerForm.defaultProps = {
  data: {},
  fields: [],
  mode: "add",
  onClose: () => {},
  validate: null,
};

export default function CustomerPage() {
  const keys = [
    "identifier",
    "name",
    "phoneNo",
    "userType",
    "balance",
    "creditLimit",
    "status",
  ];

  return (
    <CommonList
      routeName="customer"
      keys={keys}
      editField="identifier"
      FormComponent={CustomerForm}
    />
  );
}