"use client";

import { useState, useEffect } from "react";
import PropTypes from "prop-types";
import { FiUsers } from "react-icons/fi";
import CommonEdit from "@/component/CommonEdit";

const UserEdit = ({ user, onClose }) => {

    const [formData, setFormData] = useState({
        id: "",
        name: "",
        phoneNo: "",
        username: "",
        roles: []
    });

    useEffect(() => {

        if (user) {

            setFormData({
                ...user,
                roles: user.roles || []
            });

        }

    }, [user]);

    const userFields = [
        {
            key: "name",
            label: "Name",
            type: "text"
        },
        {
            key: "phoneNo",
            label: "Phone Number",
            type: "number"
        },
        {
            key: "username",
            label: "Email",
            type: "email",
            disabled: true
        },
        {
            key: "roles",
            label: "Role",
            type: "search",
            api: "/api/role/list"
        }
    ];


    return (
        <CommonEdit
            title="User"
            icon={FiUsers}
            formData={formData}
            setFormData={setFormData}
            fields={userFields}
            moduleName="user"
            onSubmit={() => { onClose(); }}
            submitLabel="Update User"
        />
    );
};

UserEdit.propTypes = {
  onClose: PropTypes.func,
  user: PropTypes.shape({
    id: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
    name: PropTypes.string,
    phoneNo: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    username: PropTypes.string,
    roles: PropTypes.arrayOf(PropTypes.string),
  }),
};
export default UserEdit;