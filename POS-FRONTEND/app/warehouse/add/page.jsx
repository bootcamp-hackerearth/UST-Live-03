"use client";

import React, { useEffect, useState } from "react";

import POSLayout from "../../components/PosLayout";
import SectionForm from "../../components/common/SectionForm";
import commonApi from "../../services/commonApi";

function WarehouseAdd() {
  const [existingWarehouse, setExistingWarehouse] = useState([]);

  useEffect(() => {
    fetchWarehouses();
  }, []);

  const fetchWarehouses = async () => {
    try {
      const res = await commonApi.list("warehouse", {
        page: 0,
        sizePerPage: 1000,
        sortDirection: "ASC",
        sortField: "id",
        search: "",
      });

      setExistingWarehouse(res.data.dtoList || []);
    } catch (err) {
      console.log(err);
    }
  };

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
        submitUrl="/warehouse/add"
        backUrl="/warehouse"
        sections={sections}
        existingData={existingWarehouse}
        uniqueFields={["identifier"]}
        initialValues={{
          name: "",
          identifier: "",
          phoneNo: "",
          address: "",
          region: "",
          country: "",
        }}
      />
    </POSLayout>
  );
}

export default WarehouseAdd;