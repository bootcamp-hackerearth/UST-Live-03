"use client";
import Dropdown from "./Dropdown";
 
const SingleSelectDropdown = (props) => {
  return <Dropdown {...props} multiple={false} />;
};
 
export default SingleSelectDropdown;