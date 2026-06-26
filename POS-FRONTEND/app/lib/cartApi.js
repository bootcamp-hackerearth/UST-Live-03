"use client";

import api from "@/app/api/axios";

export const cartApi = {
  getActiveCustomers: async () => {
    const response = await api.get("/customer/getAllActive");
    return response.data;
  },

  getActiveProducts: async () => {
    const response = await api.get("/product/getAllActive");
    return response.data;
  },

  getCustomerCart: async (customerPhone) => {
    const response = await api.post("/cart/getCart", {
      identifier: customerPhone
    });
    return response.data;
  },

  addToCart: async (productId, customerPhone, quantity) => {
    const response = await api.post("/cartEntry/addEntry", {
      product: productId,
      cart: customerPhone,
      quantity: Number.parseFloat(quantity)
    });
    return response.data;
  },

  removeFromCart: async (productId, customerPhone) => {
    const response = await api.delete("/cart/deleteEntry", {
      product: productId,
      cart: customerPhone
    });
    return response.data;
  },

  recalculateCart: async (customerPhone) => {
    const response = await api.put("/cart/addToCart", {
      identifier: customerPhone
    });
    return response.data;
  }
};