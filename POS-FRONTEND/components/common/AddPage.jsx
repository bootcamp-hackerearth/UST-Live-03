'use client';

import { useState } from "react";
import { useRouter } from "next/navigation";
import PropTypes from "prop-types";
import api from "../../services/api";

const AddPage = ({ fields, modelName, children }) => {
  const router = useRouter();

  const [formData, setFormData] = useState({});
  const [message, setMessage] = useState("");
  const [errors, setErrors] = useState({});

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

    fields.forEach((field) => {
      const value = formData[field.name];
      if (field.required === false) return;
      if (!value || value.toString().trim() === "") {
        newErrors[field.name] = `${field.label} is required`;
        return;
      }
      if (field.name === "username" && value &&!/^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/.test(value)) {
      newErrors[field.name] = "Invalid email format";
    }
      if (
        field.type === "password" &&
        value &&
        value.length < 6
      ) {
        newErrors[field.name] =
          "Password must be at least 6 characters";
      }

     if (field.name === "phoneNo" && value) {
      if (!/^\d+$/.test(value)) {
        newErrors[field.name] = "Phone number must contain only digits";
      } 
      else if (value.length !== 10) {
        newErrors[field.name] = "Phone number must be exactly 10 digits";
      }
    }
   });
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const renderField = (field) => {
    if (field.component) {
      return field.component({
        value: formData[field.name],
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
          className="border rounded-lg px-3 py-2"
        />
      );
    }

    return (
      <input
        type={field.type}
        name={field.name}
        value={formData[field.name] || ""}
        onChange={handleChange}
        className="border rounded-lg px-3 py-2"
      />
    );
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const token = localStorage.getItem("token");

    if (!validate()) return;
    try {
      const res = await api.post(
        `/${modelName}/add`,
        formData,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        },
      );

      if (res.data.success === false) {
        setMessage(res.data.message);
        return;
      }

      setMessage("Added successfully");
      setTimeout(() => {
        router.push(`/${modelName}`);
      }, 1500);

    }
     catch (err) {
      console.log(formData);
      console.error(err);
      setMessage("Failed to add");
    }
  };

  return (
    <div className="min-h-screen bg-[#f4f6fb] flex items-center justify-center">
      <div className="w-[560px] bg-white p-8 rounded-xl shadow">
        <h3 className="text-center text-blue-600 text-xl font-bold mb-6">
          Add {modelName}
        </h3>
        {message && (
          <div className="text-center text-red-500 mb-3">
            {message}
          </div>
        )}
        <form onSubmit={handleSubmit} className="space-y-4">
          {fields.map((field) => (
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
          {children}
          <button className="w-full bg-blue-600 text-white py-2 rounded-lg">
            Add {modelName}
          </button>
        </form>
        <button
          onClick={() => router.push(`/${modelName}`)}
          className="w-full mt-3 border border-blue-600 text-blue-600 py-2 rounded-lg"
        >
          ← Back
        </button>
      </div>
    </div>
  );
};

AddPage.propTypes = {
  fields: PropTypes.array.isRequired,
  modelName: PropTypes.string.isRequired,
  children: PropTypes.node,
};

export default AddPage;