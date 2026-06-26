"use client";

import React from "react";
import MasterListPage from "../components/common/MasterList";
function UnitList() {
  return (
    <MasterListPage
      title="Unit List"
      routeName="unit"
      entityLabel="Unit"
      inputLabel="Unit"
      placeholder="Enter Unit"
      editUrl="/unit/edit"
    />
  );
}

export default UnitList;