import React, { useEffect, useState } from "react";
import axios from "axios";
import POSLayout from "../../components/POSLayout";
import SectionForm from "../../components/common/SectionForm";

function WarehouseAdd() {
  const token = localStorage.getItem("token");
  const [existingWarehouse, setExistingWarehouse] = useState([]);

  useEffect(() => {
    const headers = { Authorization: `Bearer ${token}` };
    const base    = "http://localhost:8080/api";

    axios.post(
      `${base}/warehouse/list`,
      { page: 0, sizePerPage: 1000, sortDirection: "ASC", sortField: "id", search: "" },
      { headers }
    ).then((r) => setExistingWarehouse(Array.isArray(r.data) ? r.data : [])).catch(console.log);
  }, [token]);

  const sections = [
    {
      title: "Basic Information",
      columns: 3,
      fields: [
        {
          key: "name",
          label: "Warehouse Name",
          type: "text",
          placeholder: "Enter Warehouse Name",
          required: true,
        },
        {
          key: "identifier",
          label: "Warehouse Identifier",
          type: "text",
          placeholder: "Enter Warehouse Identifier",
          required: true,
        },
        {
          key: "phoneNo",
          label: "Phone Number",
          type: "text",
          placeholder: "Enter Phone Number",
          required: true,
          isPhone: true,
        },
      ],
    },
    {
      title: "Address Information",
      columns: 2,
      fields: [
        {
          key: "address",
          label: "Address",
          type: "text",
          placeholder: "Enter Address",
          required: true,
          fullWidth: true,
        },
        {
          key: "region",
          label: "Region",
          type: "text",
          placeholder: "Enter Region",
          required: true,
        },
        {
          key: "country",
          label: "Country",
          type: "text",
          placeholder: "Enter Country",
          required: true,
        },
      ],
    },
  ];

  return (
    <POSLayout>
      <SectionForm
        title="Add New Warehouse"
        token={token}
        submitUrl="http://localhost:8080/api/warehouse/add"
        backUrl="/warehouses"
        sections={sections}
        existingData={existingWarehouse}
        uniqueFields={["identifier"]}
        initialValues={{
          name:       "",
          identifier: "",
          phoneNo:    "",
          address:    "",
          region:     "",
          country:    "",
        }}
      />
    </POSLayout>
  );
}

export default WarehouseAdd;