'use client';

import { useState, useEffect } from "react";
import { useRouter, useParams } from "next/navigation";
import PropTypes from "prop-types";
import api from "../../services/api";

const UpdatePage = ({ fields = [], modelName }) => {
  const router = useRouter();
  const params = useParams();
  const identifier = params?.identifier;

  const [formData, setFormData] = useState({});
  const [message, setMessage] = useState("");
  const [errors, setErrors] = useState({});

  const defaultFields = [
    { name: "identifier", label: "Identifier", readOnly: true },
    
  ];

  const allFields = [...defaultFields, ...fields];

  useEffect(() => {
    fetchData();
  }, [identifier]);

  const fetchData = async () => {
    try {
      const res = await api.get(
        `/${modelName}/get?identifier=${identifier}`
      );

      setFormData(res.data);
    } catch (err) {
      console.error(err);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;

    setFormData({
      ...formData,
      [name]: value,
    });

    setErrors({
      ...errors,
      [name]: "",
    });
  };

  const validate = () => {
    let newErrors = {};

    allFields.forEach((field) => {
      if (field.readOnly) {
        return;
      }

      const value = formData[field.name];

      if (!value || value.toString().trim() === "") {
        newErrors[field.name] = `${field.label} is required`;
      }

      if (
        field.type === "number" &&
        value &&
        Number(value) < 0
      ) {
        newErrors[field.name] =
          `${field.label} must be greater than 0`;
      }

      if (field.type === "email" && value) {
        const email = value.trim();
        const atIndex = email.indexOf("@");
        const dotIndex = email.lastIndexOf(".");
        const isValid = atIndex > 0 && dotIndex > atIndex + 1 && dotIndex < email.length - 1 && !email.includes(" ");
        if (!isValid) {
          newErrors[field.name] = "Invalid email format";
        }
      }

      if (
        field.type === "password" &&
        value &&
        value.length < 6
      ) {
        newErrors[field.name] =
          "Password must be at least 6 characters";
      }

      if (field.type === "phone" && value) {
        if (!/^[6-9]\d{9}$/.test(value)) {
        newErrors[field.name] = "Phone number must be exactly 10 digits";
      }
    }
  });

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validate()) {
      return;
    }

    try {
      await api.put(
        `/${modelName}/update`,
        formData
      );

      setMessage("Updated successfully");

      setTimeout(() => {
        router.push(`/${modelName}/list`);
      }, 1000);

    } catch (err) {
      console.error(err);
      setMessage("Failed to update");
    }
  };

  const renderField = (field) => {
    if (field.readOnly) {
      return (
        <input
          value={formData[field.name] || ""}
          readOnly
          className="border rounded-lg px-3 py-2 bg-gray-100 w-full"
        />
      );
    }

    if (field.component) {
      return field.component({
        value: formData[field.name] || [],
        onChange: (value) => {
          setFormData({
            ...formData,
            [field.name]: value,
          });

          setErrors({
            ...errors,
            [field.name]: "",
          });
        },
      });
    }

    if (field.type === "textarea") {
      return (
        <textarea
          name={field.name}
          value={formData[field.name] || ""}
          onChange={handleChange}
          className="border rounded-lg px-3 py-2 w-full"
        />
      );
    }

    return (
      <input
        type={field.type}
        name={field.name}
        value={formData[field.name] || ""}
        onChange={handleChange}
        readOnly={field.readOnly === true}
        className="border rounded-lg px-3 py-2 w-full"
      />
    );
  };

  return (
    <div className="min-h-screen bg-[#f4f6fb] flex items-center justify-center">
      <div className="w-[560px] bg-white p-8 rounded-xl shadow">
        <h3 className="text-center text-blue-600 text-xl font-bold mb-6">
          Update {modelName}
        </h3>
        {message && (
          <div className="text-center text-red-500 mb-3">
            {message}
          </div>
        )}
        <form onSubmit={handleSubmit} className="space-y-4">
      {allFields.map((field) => (
  <div key={field.name}>
    <div className="grid grid-cols-[150px_1fr] gap-3 items-center">
      <label className="text-sm font-semibold">
        {field.label}
      </label>

      {renderField(field)}
    </div>

    {errors[field.name] && (
      <p className="text-red-500 text-sm ml-[163px] mt-1">
        {errors[field.name]}
      </p>
    )}
  </div>
))}

<div className="border-t pt-5 mt-5">
  <h4 className="font-semibold text-gray-700 mb-4">
    Audit Details
  </h4>

  <div className="grid grid-cols-[150px_1fr] gap-3">

    <label htmlFor="audit-createdBy">Created By</label>
    <input
      id="audit-createdBy"
      value={formData.createdBy || ""}
      readOnly
      className="border rounded px-3 py-2 bg-gray-100"
    />

    <label htmlFor="audit-createdOn">Created On</label>
    <input
      id="audit-createdOn"
      value={
        formData.createdOn
          ? new Date(
              formData.createdOn
            ).toLocaleString("en-IN")
          : ""
      }
      readOnly
      className="border rounded px-3 py-2 bg-gray-100"
    />

    <label htmlFor="audit-modifiedBy">Modified By</label>
    <input
      id="audit-modifiedBy"
      value={formData.modifiedBy || ""}
      readOnly
      className="border rounded px-3 py-2 bg-gray-100"
    />

    <label htmlFor="audit-modifiedOn">Modified On</label>
    <input
      id="audit-modifiedOn"
      value={
        formData.modifiedOn
          ? new Date(
              formData.modifiedOn
            ).toLocaleString("en-IN")
          : ""
      }
      readOnly
      className="border rounded px-3 py-2 bg-gray-100"
    />

  </div>
</div>
          <button className="w-full bg-blue-600 text-white py-2 rounded-lg">
            Update {modelName}
          </button>
        </form>
        <button
          onClick={() => router.push(`/${modelName}/list`)}
          className="w-full mt-3 border border-blue-600 text-blue-600 py-2 rounded-lg"
        >
          ← Back
        </button>

      </div>
    </div>
  );
};

UpdatePage.propTypes = {
  fields: PropTypes.arrayOf(
    PropTypes.shape({
      name: PropTypes.string.isRequired,
      label: PropTypes.string.isRequired,
      type: PropTypes.string,
      readOnly: PropTypes.bool,
      component: PropTypes.func,
    })
  ),
  modelName: PropTypes.string.isRequired,
};

export default UpdatePage;