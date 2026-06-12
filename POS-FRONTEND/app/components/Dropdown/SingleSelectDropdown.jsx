"use client";

import DropdownTemplate from "./DropdownTemplate";

export default function SingleSelectDropdown(props) {
  return (
    <DropdownTemplate
      {...props}
      multiple={false}
    />
  );
}