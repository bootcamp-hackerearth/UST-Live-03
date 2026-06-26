"use client";

import React, { useEffect, useState } from "react";
import { useParams } from "next/navigation";

import POSLayout from "../../../components/PosLayout";
import SectionForm from "../../../components/common/SectionForm";
import commonApi from "../../../services/commonApi";

function CustomerEdit() {
  const params = useParams();
  const identifier = decodeURIComponent(
    params.identifier
  );
  const [initialValues, setInitialValues] = useState(null);
  const [existingCustomers, setExistingCustomers] = useState([]);

  useEffect(() => {
    if (identifier) {
      fetchCustomer();
      fetchCustomers();
    }
  }, [identifier]);

  const fetchCustomer = async () => {
    try {
      const res = await commonApi.get("customer", "identifier", identifier);
      const data = res.data;

      setInitialValues({
        id: data.id,
        customerName: data.customerName || "",
        identifier: data.identifier || "",
        phoneNumber: data.phoneNumber || "",
        partyType: data.partyType || "",
        creditLimit: data.creditLimit || "",
        balance: data.balance || "",

        createdBy: data.createdBy,
        createdOn: data.createdOn,
        modifiedBy: data.modifiedBy,
        modifiedOn: data.modifiedOn,

        billingAddressLine: data.billingAddress?.addressLine || "",
        billingCity: data.billingAddress?.city || "",
        billingState: data.billingAddress?.state || "",
        billingCountry: data.billingAddress?.country || "India",
        billingZipcode: data.billingAddress?.zipcode || "",
        billingPhoneNumber: data.billingAddress?.phoneNumber || "",

        shippingAddressLine: data.shippingAddress?.addressLine || "",
        shippingCity: data.shippingAddress?.city || "",
        shippingState: data.shippingAddress?.state || "",
        shippingCountry: data.shippingAddress?.country || "India",
        shippingZipcode: data.shippingAddress?.zipcode || "",
        shippingPhoneNumber: data.shippingAddress?.phoneNumber || "",
      });
    } catch (err) {
      console.log(err);
    }
  };

  const fetchCustomers = async () => {
    try {
      const res = await commonApi.list("customer", {
        page: 0,
        sizePerPage: 1000,
        sortDirection: "ASC",
        sortField: "id",
        search: "",
      });

      setExistingCustomers(res.data.dtoList || []);
    } catch (err) {
      console.log(err);
    }
  };

  if (!initialValues) {
    return <POSLayout>Loading...</POSLayout>;
  }

  const sections = [
    {
      title: "Basic Information",
      columns: 2,
      fields: [
        {
          key: "customerName",
          label: "Customer Name",
          type: "text",
          required: true,
        },
        {
          key: "identifier",
          label: "Email",
          type: "email",
          required: true,
          disabled: true,
        },
        {
          key: "phoneNumber",
          label: "Phone Number",
          type: "text",
          required: true,
          isPhone: true,
        },
        {
          key: "partyType",
          label: "Party Type",
          type: "text",
          required: true,
        },
      ],
    },
    {
      title: "Credit & Balance Details",
      columns: 2,
      fields: [
        {
          key: "creditLimit",
          label: "Credit Limit",
          type: "number",
          required: true,
        },
        {
          key: "balance",
          label: "Balance",
          type: "number",
          required: true,
        },
      ],
    },
    {
      key: "billing",
      title: "Billing Address",
      columns: 2,
      fields: [
        {
          key: "billingAddressLine",
          label: "Address Line",
          type: "text",
          required: true,
          fullWidth: true,
        },
        {
          key: "billingCity",
          label: "City",
          type: "text",
          required: true,
        },
        {
          key: "billingState",
          label: "State",
          type: "text",
          required: true,
        },
        {
          key: "billingCountry",
          label: "Country",
          type: "text",
          required: true,
        },
        {
          key: "billingZipcode",
          label: "ZIP Code",
          type: "number",
          required: true,
        },
        {
          key: "billingPhoneNumber",
          label: "Phone Number",
          type: "text",
          required: true,
          isPhone: true,
        },
      ],
    },
    {
      key: "shipping",
      title: "Shipping Address",
      subtitle: "Leave blank if same as billing",
      copyFrom: "billing",
      columns: 2,
      fields: [
        {
          key: "shippingAddressLine",
          label: "Address Line",
          type: "text",
          fullWidth: true,
        },
        {
          key: "shippingCity",
          label: "City",
          type: "text",
        },
        {
          key: "shippingState",
          label: "State",
          type: "text",
        },
        {
          key: "shippingCountry",
          label: "Country",
          type: "text",
        },
        {
          key: "shippingZipcode",
          label: "ZIP Code",
          type: "number",
        },
        {
          key: "shippingPhoneNumber",
          label: "Phone Number",
          type: "text",
          isPhone: true,
        },
      ],
    },
  ];

  const buildCustomPayload = (formData) => ({
    id: initialValues.id,
    identifier: initialValues.identifier,
    customerName: formData.customerName,
    phoneNumber: Number(formData.phoneNumber),
    partyType: formData.partyType,
    creditLimit: Number(formData.creditLimit),
    balance: Number(formData.balance),
    billingAddress: {
      addressType: "Billing",
      addressLine: formData.billingAddressLine,
      city: formData.billingCity,
      state: formData.billingState,
      country: formData.billingCountry,
      zipcode: Number(formData.billingZipcode),
      phoneNumber: Number(formData.billingPhoneNumber),
    },
    shippingAddress: {
      addressType: "Shipping",
      addressLine: formData.shippingAddressLine || formData.billingAddressLine,
      city: formData.shippingCity || formData.billingCity,
      state: formData.shippingState || formData.billingState,
      country: formData.shippingCountry || formData.billingCountry,
      zipcode: Number(formData.shippingZipcode || formData.billingZipcode),
      phoneNumber: Number(
        formData.shippingPhoneNumber || formData.billingPhoneNumber
      ),
    },
  });

  return (
    <POSLayout>
      <SectionForm
        title="Edit Customer"
        submitUrl="/customer/update"
        backUrl="/customer"
        sections={sections}
        existingData={existingCustomers}
        uniqueFields={["identifier"]}
        buildCustomPayload={buildCustomPayload}
        initialValues={initialValues}
      />
    </POSLayout>
  );
}

export default CustomerEdit;