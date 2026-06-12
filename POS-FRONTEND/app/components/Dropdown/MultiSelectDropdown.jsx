"use client";

import DropdownTemplate from "./DropdownTemplate";

export default function MultiSelectDropdown(props) {
  return (
    <DropdownTemplate
      {...props}
      multiple={true}
    />
  );
}