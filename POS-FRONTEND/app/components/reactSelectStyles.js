const reactSelectStyles = {
    control: (provided, state) => ({
      ...provided,
      minHeight: "42px",
      borderRadius: "8px",
      borderColor: state.isFocused
        ? "#4f46e5"
        : "#d1d5db",
      boxShadow: "none",
      cursor: "pointer",

      "&:hover": {
        borderColor: "#4f46e5",
      },
    }),

    valueContainer: (provided) => ({
      ...provided,
      padding: "2px 10px",
    }),

    placeholder: (provided) => ({
      ...provided,
      color: "#9ca3af",
      fontSize: "14px",
    }),

    singleValue: (provided) => ({
      ...provided,
      color: "#111827",
      fontSize: "14px",
    }),

    menu: (provided) => ({
      ...provided,
      borderRadius: "8px",
      overflow: "hidden",
      zIndex: 9999,
    }),

    menuPortal: (provided) => ({
      ...provided,
      zIndex: 99999,
    }),

    option: (provided, state) => {
      let backgroundColor = "#fff";

      if (state.isSelected) {
        backgroundColor = "#4f46e5";
      } else if (state.isFocused) {
        backgroundColor = "#eef2ff";
      }

      return {
        ...provided,

        backgroundColor,

        color: state.isSelected
          ? "#fff"
          : "#111827",

        cursor: "pointer",
        fontSize: "14px",
      };
    },

    multiValue: (provided) => ({
      ...provided,
      backgroundColor: "#eef2ff",
      borderRadius: "6px",
    }),

    multiValueLabel: (provided) => ({
      ...provided,
      color: "#4f46e5",
      fontWeight: "500",
    }),

    multiValueRemove: (provided) => ({
      ...provided,

      color: "#4f46e5",

      ":hover": {
        backgroundColor: "#4f46e5",
        color: "#fff",
      },
    }),
  };
export default reactSelectStyles;