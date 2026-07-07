
export const buildCustomerPayload = (formData) => {
  const phone = Number(formData.phoneNo || 0);

  return {
    id: formData.id,
    identifier: formData.identifier || "",
    name: formData.name || "",
    phoneNo: phone,
    partyType: formData.partyType || "customer",
    balance: Number(formData.balance || 0),
    creditLimit: Number(formData.creditLimit || 0),

    billingAddress: {
      addressLine: formData.billingAddressLine || "",
      city: formData.billingCity || "",
      state: formData.billingState || "",
      zipcode: Number(formData.billingZipcode || 0),
      country: formData.billingCountry || "",
      phoneNo: phone,
      addressType: "billing",
    },

    shippingAddress: {
      addressLine: formData.shippingAddressLine || "",
      city: formData.shippingCity || "",
      state: formData.shippingState || "",
      zipcode: Number(formData.shippingZipcode || 0),
      country: formData.shippingCountry || "",
      phoneNo: phone,
      addressType: "shipping",
    },
  };
};

export const mapCustomerAddresses = (data, transformedData) => ({
  ...transformedData,
partyType: data.partyType ?? "",
  shippingAddressLine: data.shippingAddress?.addressLine ?? "",
  shippingCity: data.shippingAddress?.city ?? "",
  shippingState: data.shippingAddress?.state ?? "",
  shippingZipcode: data.shippingAddress?.zipcode ?? "",
  shippingCountry: data.shippingAddress?.country ?? "",

  billingAddressLine: data.billingAddress?.addressLine ?? "",
  billingCity: data.billingAddress?.city ?? "",
  billingState: data.billingAddress?.state ?? "",
  billingZipcode: data.billingAddress?.zipcode ?? "",
  billingCountry: data.billingAddress?.country ?? "",
});