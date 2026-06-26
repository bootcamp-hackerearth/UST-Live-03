"use client";

import { useState } from "react";
import Add from "../../../components/add";
import SingleDropdown from "../../../components/SingleDropdown";

export default function RackAdd() {
  const [shelfs, setShelfs] = useState([]);

  const extraFields = [
    {
      key: "shelfs",
      type: "custom",
      component: (
        <SingleDropdown
          value={shelfs[0] || ""}
          label="Shelf"
          apiPath="shelfs/getAllActive"
          required
          onChange={(value) => setShelfs([value])}
          urlMethod={"get"}
        />
      ),
    },
  ];

  return (
    <Add
      title="Rack"
      apiPath="rack"
      showDescription={true}
      extraFields={extraFields}
      extraData={{
        shelfs,
      }}
    />
  );
}
