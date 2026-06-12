export const validateEmail = (
  email
) => {

  const emailPattern =
    /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

  return emailPattern.test(email);
};

export const validatePhone = (
  phone
) => {

  return /^\d{10}$/.test(phone);
};

export const validatePassword = (
  password
) => {

  return password.length >= 6;
};