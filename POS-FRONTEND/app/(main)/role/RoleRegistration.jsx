"use client";

import { useState } from "react";
import PropTypes from "prop-types";
import { FiShield } from "react-icons/fi";
import CommonAdd from "@/component/CommonAdd";

const RoleRegistration = ({ onClose }) => {

    const [formData, setFormData] = useState({
        identifier: "",
        description: ""
    });

    const fields = [
        {
            key: "identifier",
            label: "Role Name",
            type: "text"
        },
        {
            key: "description",
            label: "Description",
            type: "text"
        }
    ];


    return (
        <CommonAdd
            title="Add Role"
            icon={FiShield}
            formData={formData}
            setFormData={setFormData}
            fields={fields}
            moduleName="role"
            onSubmit={() => { onClose(); }}
            submitLabel="Add Role"
        />
    );
};
RoleRegistration.propTypes = {
  onClose: PropTypes.func,
};
export default RoleRegistration;