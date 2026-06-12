"use client";

import React, { useEffect, useState } from "react";
import { useParams } from "next/navigation";

import POSLayout from "../../../components/PosLayout";
import SectionForm from "../../../components/common/SectionForm";
import commonApi from "../../../services/commonApi";

function PriceEdit() {
  const params = useParams();
  const identifier = params.identifier;

  const [initialValues, setInitialValues] = useState(null);

  useEffect(() => {
    fetchPrice();
  }, []);

  const fetchPrice = async () => {
    try {
      const res = await commonApi.get("price", "identifier", identifier);
      setInitialValues(res.data);
    } catch (err) {
      console.log(err);
    }
  };

  if (!initialValues) {
    return (
      <POSLayout>
        <div className="bg-white rounded-xl p-6 shadow-sm">
          Loading...
        </div>
      </POSLayout>
    );
  }

  const sections = [
    {
      title: "Price Information",
      columns: 3,
      fields: [
        {
          key: "productIdentifier",
          label: "Product Identifier",
          type: "text",
          disabled: true,
          required: true,
        },
        {
          key: "priceType",
          label: "Price Type",
          type: "text",
          required: true,
          disabled: true,
        },
        {
          key: "priceAmount",
          label: "Price Amount",
          type: "text",
          placeholder: "Enter Price Amount",
          required: true,
        },
      ],
    },
  ];

  const buildPricePayload = (formData) => {
    return {
      id: initialValues.id,
      identifier: initialValues.identifier,
      productIdentifier: initialValues.productIdentifier,
      priceType: formData.priceType,
      priceAmount: formData.priceAmount,
    };
  };

  return (
    <POSLayout>
      <SectionForm
        title="Edit Price"
        submitUrl="/price/update"
        backUrl="/price"
        sections={sections}
        initialValues={initialValues}
        buildCustomPayload={buildPricePayload}
        uniqueFields={["productIdentifier", "priceType"]}
      />
    </POSLayout>
  );
}

export default PriceEdit;