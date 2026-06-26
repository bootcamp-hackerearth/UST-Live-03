"use client";

import { useState, useEffect } from "react";
import PropTypes from "prop-types";
import { FiEdit } from "react-icons/fi";
import CommonEdit from "@/component/CommonEdit";

const RoleEdit = ({ role, onClose }) => {

    const [formData, setFormData] = useState({
        identifier: "",
        description: ""
    });

    useEffect(() => {

        if(role)
        {
            setFormData({
                identifier: role.identifier || "",
                description: role.description || ""
            });
        }

    }, [role]);

    const fields = [
        {
            key: "identifier",
            label: "Role Name",
            type: "text",
            disabled: true
        },
        {
            key: "description",
            label: "Description",
            type: "text"
        }
    ];

    return (
        <CommonEdit
            title="Edit Role"
            icon={FiEdit}
            formData={formData}
            setFormData={setFormData}
            fields={fields}
            moduleName="role"
            onSubmit={() => {onClose();}}
            submitLabel="Update Role"
        />
    );
};
RoleEdit.propTypes = {
  onClose: PropTypes.func,
  role: PropTypes.shape({
    identifier: PropTypes.string,
    description: PropTypes.string,
  }),
};
export default RoleEdit;