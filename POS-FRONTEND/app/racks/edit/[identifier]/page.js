"use client";

import { useState } from "react";
import PropTypes from "prop-types";
import EditFormSkeleton from "../../../components/EditFormSkeleton";
import MultiDropDown from "../../../components/MultiDropDown";

function ShelfsMultiDropdownWrapper({ value, onChange }) {
    return (
        <MultiDropDown
            label="Shelfs"
            apiUrl="/shelfs/findByStatus"
            valueField="identifier"
            labelField="identifier"
            selectedValues={value || []}
            onChange={onChange}
        />
    );
}

ShelfsMultiDropdownWrapper.propTypes = {
    value: PropTypes.arrayOf(PropTypes.string),
    onChange: PropTypes.func.isRequired,
};

ShelfsMultiDropdownWrapper.defaultProps = {
    value: [],
};

export default function EditRacks() {
    const [shelfs, setShelfs] = useState([]);

    const rackFields = [
        {
            key: "shelfs",
            type: "custom",
            label: "Shelfs",
            CustomComponent: ShelfsMultiDropdownWrapper,
        },
    ];

    return (
        <EditFormSkeleton
            title="Rack"
            apiPath="racks"
            extraFields={rackFields}
            setters={{ shelfs: setShelfs }}
            externalExtraData={{ shelfs }}
        />
    );
}