"use client";

import { useState } from "react";
import { FiGrid } from "react-icons/fi";
import PropTypes from "prop-types";
import CommonAdd from "@/component/CommonAdd";

const NodeRegistration = ({ onClose }) => {

    const [formData, setFormData] = useState({
        identifier: "",
        path: "",
        roles: []
    });

    const fields = [
        {
            key: "identifier",
            label: "Identifier",
            type: "text"
        },
        {
            key: "path",
            label: "Path",
            type: "text"
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
        title="Add Node"
        icon={FiGrid}
        formData={formData}
        setFormData={setFormData}
        fields={fields}
        moduleName="node"
        onSubmit={() => {
          globalThis.dispatchEvent(new Event("refreshSidebar"));
          onClose();
        }}
        submitLabel="Add Node"
      />
    );
};
NodeRegistration.propTypes = {
    onClose: PropTypes.func.isRequired
};
export default NodeRegistration;