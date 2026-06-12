"use client";
import Dropdown from "./Dropdown";
 
const MultiSelectDropdown = (props) => {
  return <Dropdown {...props} multiple={true} />;
};
 
export default MultiSelectDropdown;