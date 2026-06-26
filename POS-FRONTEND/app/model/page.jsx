"use client";

import React from "react";
import MasterListPage from "../components/common/MasterList";
function ModelList() {
  return (
    <MasterListPage
      title="Model List"
      routeName="model"
      entityLabel="Model"
      inputLabel="Model"
      placeholder="Enter Model Name"
    />
  );
}

export default ModelList;