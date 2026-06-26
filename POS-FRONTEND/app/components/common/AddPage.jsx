"use client";

import React, { useEffect, useState } from "react";
import PropTypes from "prop-types";

import POSLayout from "../PosLayout";
import SectionForm from "./SectionForm";
import commonApi from "../../services/commonApi";
import { defaultListPayload } from "../../constants/pagination";

function AddPage({
  title,
  submitUrl,
  backUrl,
  sections,
  initialValues,
  existingRoute,
  uniqueFields,
  buildCustomPayload,
  onLoadExtraData,
}) {
  const [existingData, setExistingData] = useState([]);

  useEffect(() => {
    const loadData = async () => {
      try {
        if (existingRoute) {
          const res = await commonApi.list(existingRoute, defaultListPayload);
          setExistingData(res.data.dtoList || []);
        }

        if (onLoadExtraData) {
          await onLoadExtraData();
        }
      } catch (err) {
        console.log(err);
      }
    };

    loadData();
  }, [existingRoute, onLoadExtraData]);

  return (
    <POSLayout>
      <SectionForm
        title={title}
        submitUrl={submitUrl}
        backUrl={backUrl}
        sections={sections}
        existingData={existingData}
        uniqueFields={uniqueFields}
        buildCustomPayload={buildCustomPayload}
        initialValues={initialValues}
      />
    </POSLayout>
  );
}

AddPage.propTypes = {
  title: PropTypes.string.isRequired,
  submitUrl: PropTypes.string.isRequired,
  backUrl: PropTypes.string.isRequired,
  sections: PropTypes.arrayOf(PropTypes.object).isRequired,
  initialValues: PropTypes.object.isRequired,
  existingRoute: PropTypes.string,
  uniqueFields: PropTypes.arrayOf(PropTypes.string),
  buildCustomPayload: PropTypes.func,
  onLoadExtraData: PropTypes.func,
};

export default AddPage;