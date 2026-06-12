import DropdownTemplate from "./DropdownTemplate";

const SingleSelectDropdown = (props) => {
  return <DropdownTemplate {...props} multiple={false} />;
};

export default SingleSelectDropdown;