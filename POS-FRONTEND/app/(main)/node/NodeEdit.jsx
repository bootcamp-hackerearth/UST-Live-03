"use client";

import { useState, useEffect } from "react";
import { FiEdit } from "react-icons/fi";
import PropTypes from "prop-types";
import CommonEdit from "@/component/CommonEdit";
const NodeEdit = ({ node, onClose }) => {

    const [formData, setFormData] = useState({
        identifier: "",
        path: "",
        roles: []
    });

    useEffect(() => {

        if(node)
        {
            console.log("Node recieved: ", node);
            setFormData({
                identifier: node.identifier || "",
                path: node.path || "",
                roles: node.roles || []
            });
        }

    }, [node]);

    const fields = [
        {
            key: "identifier",
            label: "Node Name",
            type: "text",
            disabled: true
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
      <CommonEdit
        title="Edit Node"
        icon={FiEdit}
        formData={formData}
        setFormData={setFormData}
        fields={fields}
        moduleName="node"
        onSubmit={() => {
          globalThis.dispatchEvent(new Event("refreshSidebar"));
          onClose();
        }}
        submitLabel="Update Node"
      />
    );
};
NodeEdit.propTypes = {
    node: PropTypes.shape({
        identifier: PropTypes.string,
        path: PropTypes.string,
        roles: PropTypes.oneOfType([
            PropTypes.arrayOf(PropTypes.string),
            PropTypes.string
        ])
    }),
    onClose: PropTypes.func.isRequired
};
export default NodeEdit;