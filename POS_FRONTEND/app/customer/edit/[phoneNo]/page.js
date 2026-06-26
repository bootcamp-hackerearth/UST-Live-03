"use client";

import CommonEditPage from "@/app/components/CommonEditPage";
import { getCustomerFields } from "../../customerFields";
import api from "@/app/services/api";

export default function EditCustomerPage() {

  const fetchCustomer = async (phoneNo) => {
    try {
      const response = await api.get("/api/customer/get", {
        params: { phoneNo },
      });

      console.log(" Fetched:", response.data);
      
      if (response.data && !response.data.email) {
        response.data.email = response.data.identifier;
      }

      return response.data;
    } catch (error) {
      console.error(
        " Fetch Error:",
        error.response?.data || error.message
      );
      throw error;
    }
  };

  const transformPayload = (data) => {
    return {
      ...data,
      identifier: data.email, 
      billingAddress: {
        ...data.billingAddress,
        addressType: "Billing",
        phoneNo: data.phoneNo,
      },
      shippingAddress: {
        ...data.shippingAddress,
        addressType: "Shipping",
        phoneNo: data.phoneNo,
      },
    };
  };

  const updateCustomer = async (data) => {
    try {
      const response = await api.put(
        "/api/customer/update",
        transformPayload(data)
      );

      console.log(" Updated Customer Context:", response.data);
      return response;
    } catch (error) {
      console.error(
        " Update Operational Error:",
        error.response?.data || error.message
      );
      throw error;
    }
  };

  return (
    <CommonEditPage
      title="Edit Customer Profile Details"
      fetchApi={fetchCustomer}
      updateApi={updateCustomer}
      redirectRoute="/customer/list"
      fields={getCustomerFields(true)}
      identifierParam="phoneNo" 
      submitButtonText="Update Customer"
    />
  );
}