"use client";

import CommonAddForm from "@/app/components/CommonAddPage";
import api from "@/app/services/api";

export default function AddRolePage() {

  const fields = [
    {
      name: "identifier",
      label: "Role Identifier",
      type: "text",
    },
    {
      name: "description",
      label: "Description",
      type: "text",
    },
  ];

  
  const handleSubmit = async (data) => {
    try {
      const response = await api.post("/api/role/add", data);

      console.log("ROLE RESPONSE:", response.data);

      const res = response.data;

  
      if (res.success === false) {
        alert(res.message);
        return false;  
      }

      
      alert(res.message || " Role added successfully");
      return true;    

    } catch (error) {
      console.error("ERROR:", error);
      alert(" Server error");
      return false;
    }
  };

  return (
    <CommonAddForm
      title="Add Role"

      submitApi={handleSubmit}  

      redirectRoute="/role/list"

      fields={fields}

      initialValues={{
        identifier: "",
        description: "",
      }}

      submitButtonText="Save Role"
    />
  );
}