"use client";

import React from "react";
import MasterListPage from "../components/common/MasterList";

function ShelfList() {
  return (
    <MasterListPage
      title="Shelf List"
      routeName="shelf"
      entityLabel="Shelf"
      inputLabel="Shelf Name"
      placeholder="Enter Name"
      editUrl="/shelf/edit"
    />
  );
}

export default ShelfList;