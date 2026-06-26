"use client";

import React, { useEffect, useState } from "react";
import PropTypes from "prop-types";
import { useParams } from "next/navigation";

import POSLayout from "../PosLayout";
import SectionForm from "./SectionForm";
import commonApi from "../../services/commonApi";

function EditPage({ title, routeName, backUrl, sections }) {
  const params = useParams();
  const identifier = params.identifier;

  const [initialValues, setInitialValues] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const res = await commonApi.get(routeName, "identifier", identifier);
        setInitialValues(res.data);
      } catch (err) {
        console.log(err);
      }
    };

    if (identifier) {
      fetchData();
    }
  }, [identifier, routeName]);

  if (!initialValues) {
    return <POSLayout>Loading...</POSLayout>;
  }

  return (
    <POSLayout>
      <SectionForm
        title={title}
        submitUrl={`/${routeName}/update`}
        backUrl={backUrl}
        sections={sections}
        initialValues={initialValues}
      />
    </POSLayout>
  );
}

EditPage.propTypes = {
  title: PropTypes.string.isRequired,
  routeName: PropTypes.string.isRequired,
  backUrl: PropTypes.string.isRequired,
  sections: PropTypes.arrayOf(PropTypes.object).isRequired,
};

export default EditPage;