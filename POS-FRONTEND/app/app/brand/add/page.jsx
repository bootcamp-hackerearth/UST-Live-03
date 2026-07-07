"use client";

import Add from "../../../components/add";

export default function BrandAdd() {
  return (
    <Add
      title="Brand"
      apiPath="brand"
      showDescription={true}
      urlMethod={"post"}
    />
  );
}
