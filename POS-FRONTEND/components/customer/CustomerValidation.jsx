export const validateCustomer = (formData) => {
  const errors = {};

  const name = formData.name?.trim();
  if (name === undefined || name === "") {
    errors.name = "Customer name is required";
  }

  const phoneNo = formData.phoneNo?.trim();

  if (phoneNo) {
    const isValidPhone = /^\d{10}$/.test(phoneNo);

    if (isValidPhone === false) {
      errors.phoneNo =
        "Enter valid 10 digit phone number";
    }
  } else {
    errors.phoneNo = "Phone number is required";
  }

  const email = formData.email?.trim();

  if (email) {
    const atIndex = email.indexOf("@");
    const dotIndex = email.lastIndexOf(".");

    const isValidEmail =
      atIndex > 0 &&
      dotIndex > atIndex + 1 &&
      dotIndex < email.length - 1;

    if (isValidEmail === false) {
      errors.email = "Enter valid email";
    }
  } else {
    errors.email = "Email is required";
  }

  const partyType = formData.partyType?.trim();

  if (partyType === undefined || partyType === "") {
    errors.partyType = "Party type is required";
  }

  return errors;
};

export const validateFinancial = (formData) => {
  const errors = {};

  const creditLimit =
    Number(formData.creditLimit);

  if (
    formData.creditLimit &&
    creditLimit < 0
  ) {
    errors.creditLimit =
      "Credit limit cannot be negative";
  }

  const balance =
    Number(formData.balance);

  if (
    formData.balance &&
    Number.isNaN(balance)
  ) {
    errors.balance =
      "Balance must be numeric";
  }

  const balanceType =
    formData.balanceType?.trim();

  if (
    balanceType === undefined ||
    balanceType === ""
  ) {
    errors.balanceType =
      "Balance type required";
  }

  return errors;
};

export const validateAddress = (
  address,
  prefix
) => {
  const errors = {};

  const addressLine =
    address?.addressLine?.trim();

  if (addressLine === undefined || addressLine === "") {
    errors[
      `${prefix}AddressLine`
    ] = `${prefix} address required`;
  }

  const city =
    address?.city?.trim();

  if (city === undefined || city === "") {
    errors[
      `${prefix}City`
    ] = `${prefix} city required`;
  }

  const state =
    address?.state?.trim();

  if (state === undefined || state === "") {
    errors[
      `${prefix}State`
    ] = `${prefix} state required`;
  }

  const zip =
    address?.zip?.trim();

  if (zip === undefined || zip === "") {
    errors[
      `${prefix}Zip`
    ] = `${prefix} ZIP required`;
  }

  const country =
    address?.country?.trim();

  if (country === undefined || country === "") {
    errors[
      `${prefix}Country`
    ] = `${prefix} country required`;
  }

  return errors;
};

export const validateForm = (
  formData,
  setErrors
) => {
  const validation = {
    ...validateCustomer(formData),
    ...validateFinancial(formData),
    ...validateAddress(
      formData.billingAddress,
      "Billing"
    ),
    ...validateAddress(
      formData.shippingAddress,
      "Shipping"
    ),
  };

  setErrors(validation);

  return (
    Object.keys(validation)
      .length === 0
  );
};