"use client";

import PropTypes from "prop-types";
import CommonAddPage from "@/app/components/CommonAddPage";
import api from "@/app/services/api";

export default function AddCustomerForm({ onSaved, onCancel }) {
  const handleSubmit = async (data) => {
    const payload = {
      customerName: data.customerName,
      phoneNo: data.phoneNo,
      partyType: "CUSTOMER",
      creditType: "",
      credit: 0,
      creditLimit: 0,

      billingAddress: {
        addressLine: "",
        city: "",
        state: "",
        zipCode: "",
        country: "",
      },

      shippingAddress: {
        addressLine: "",
        city: "",
        state: "",
        zipCode: "",
        country: "",
      },
    };

    try {
      const response = await api.post("/api/customer/add", payload);

      if (response.data.success === false) {
        alert(response.data.message);
        return false;
      }

      alert(response.data.message || "Customer added successfully");

      const newCustomerIdentifier =
        response.data.dto?.identifier ||
        response.data.data?.identifier ||
        data.phoneNo;

      if (onSaved) {
        onSaved(newCustomerIdentifier);
      }

      return true;
    } catch (error) {
      console.error(error);
      alert("Failed to save customer");
      return false;
    }
  };

  return (
    <div style={{ position: "relative", marginBottom: "20px" }}>
      <CommonAddPage
        title="Quick Add Customer"
        submitApi={handleSubmit}
        redirectRoute="#"
        onCancel={onCancel}
        submitButtonText="Save Customer"
        initialValues={{
          customerName: "",
          phoneNo: "",
        }}
        fields={[
          {
            label: "Customer Name *",
            name: "customerName",
            type: "text",
            required: true,
          },
          {
            label: "Phone Number *",
            name: "phoneNo",
            type: "number",
            required: true,
          },
        ]}
      />
    </div>
  );
}

AddCustomerForm.propTypes = {
  onSaved: PropTypes.func,
  onCancel: PropTypes.func,
};