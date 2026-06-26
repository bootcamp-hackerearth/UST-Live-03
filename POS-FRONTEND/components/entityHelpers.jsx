"use client";

import React from "react";
import PropTypes from "prop-types";
import StatusBadge from "@/components/StatusBadge";

export const idColumn = { key: "id", label: "ID" };

export const statusColumn = {
    key: "status",
    label: "Status",
    render: (item) => <StatusBadge status={item.status} />,
};

export const identifierColumn = (label) => ({ key: "identifier", label });

export const identifierField = (placeholder = "Enter Code", opts = {}) => ({
    name: "identifier",
    type: "text",
    placeholder,
    hardCoded: "false",
    hardCodedArray: [],
    readOnly: !!opts.readOnly,
    validation: opts.validation,
});

export const statusField = (opts = {}) => ({
    name: "status",
    type: "select",
    placeholder: opts.placeholder || "Select Status",
    hardCoded: "true",
    multiple: false,
    hardCodedArray: ["true", "false"],
    readOnly: !!opts.readOnly,
    validation: opts.validation,
});

export const addressFields = (prefix = "shippingAddress", opts = {}) => ([
    { name: `${prefix}.addressline`, type: "text", placeholder: "Address Line", hardCoded: "false", hardCodedArray: [], readOnly: false, validation: opts.required ? opts.validation : undefined },
    { name: `${prefix}.city`, type: "text", placeholder: "City", hardCoded: "false", hardCodedArray: [], readOnly: false, validation: opts.required ? opts.validation : undefined },
    { name: `${prefix}.state`, type: "text", placeholder: "State", hardCoded: "false", hardCodedArray: [], readOnly: false, validation: opts.required ? opts.validation : undefined },
    { name: `${prefix}.zipcode`, type: "number", placeholder: "Zipcode", hardCoded: "false", hardCodedArray: [], readOnly: false, validation: opts.required ? opts.validation : undefined },
    { name: `${prefix}.country`, type: "text", placeholder: "Country", hardCoded: "false", hardCodedArray: [], readOnly: false, validation: opts.required ? opts.validation : undefined },
    { name: `${prefix}.phoneNo`, type: "number", placeholder: "Phone Number", hardCoded: "false", hardCodedArray: [], readOnly: false, validation: opts.phoneValidation },
    { name: `${prefix}.addressType`, type: "select", placeholder: "Address Type", hardCoded: "true", multiple: false, hardCodedArray: opts.types || ["shippingAddress"], readOnly: false, validation: opts.required ? opts.validation : undefined },
]);

StatusBadge.propTypes = {
    status: PropTypes.oneOfType([PropTypes.bool, PropTypes.string, PropTypes.number]),
};

export default {
    idColumn,
    statusColumn,
    identifierColumn,
    identifierField,
    statusField,
    addressFields,
};
