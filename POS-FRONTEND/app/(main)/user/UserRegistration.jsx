"use client";

import { useState } from "react";
import PropTypes from "prop-types";
import { FiUserPlus } from "react-icons/fi";
import CommonAdd from "@/component/CommonAdd";

const UserRegistration = ({ onClose }) => {

    const [formData, setFormData] = useState({
        name: "",
        phoneNo: "",
        username: "",
        password: "",
        roles: []
    });

    const userFields = [
        {
            key: "name",
            label: "Name",
            type: "text"
        },
        {
            key: "phoneNo",
            label: "Phone Number",
            type: "tel"
        },
        {
            key: "username",
            label: "Email",
            type: "email"
        },
        {
            key: "password",
            label: "Password",
            type: "password"
        },
        {
            key: "roles",
            label: "Role",
            type: "search",
            api: "/api/role/list"
        }
    ];


    return (
        <CommonAdd
            title="User"
            icon={FiUserPlus}
            formData={formData}
            setFormData={setFormData}
            fields={userFields}
            moduleName="user"
            onSubmit={() => { onClose(); }}
            submitLabel="Create User"
        />
    );
};
UserRegistration.propTypes = {
  onClose: PropTypes.func,
};
export default UserRegistration;