export const requiredValidation = {
    required: "Required"
};

export const nameValidation = {
    required: "Name is required",
    minLength: {
        value: 3,
        message: "Name must be at least 3 characters",
    },
    pattern: {
        value: /^[A-Za-z]+$/,
        message: "Name must contain only letters",
    },
};

export const emailValidation = {
    required: "Email is required",
    pattern: {
        value: /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/,
        message: "Invalid email format",
    },
};

export const passwordValidation = {
    required: "Password is required",
    minLength: {
        value: 8,
        message: "Password must be at least 8 characters",
    },
    validate: {
        hasUpperCase: (value) =>
            /[A-Z]/.test(value) || "Must contain at least one uppercase letter",
        hasLowerCase: (value) =>
            /[a-z]/.test(value) || "Must contain at least one lowercase letter",
        hasNumber: (value) =>
            /\d/.test(value) || "Must contain at least one number",
        hasSpecialChar: (value) =>
            /[@$!%*?&]/.test(value) ||
            "Must contain at least one special character",
    },
};

export const phoneValidation = {
    required: "Phone number is required",
    pattern: {
        value: /^\d+$/,
        message: "Phone number must contain only digits",
    },
    minLength: {
        value: 10,
        message: "Phone number must be 10 digits",
    },
    maxLength: {
        value: 10,
        message: "Phone number must be 10 digits",
    },
};