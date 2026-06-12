import axiosInstance from "./axiosInstance";

const commonApi = {
  active: async (routeName) => {
    return await axiosInstance.get(`/${routeName}/active`);
  },

  toggle: async (routeName, identifier) => {
    return await axiosInstance.get(`/${routeName}/toggle`, {
      params: {
        identifier,
      },
    });
  },

  list: async (routeName, payload) => {
    return await axiosInstance.post(`/${routeName}/list`, payload);
  },

  add: async (routeName, payload) => {
    return await axiosInstance.post(`/${routeName}/add`, payload);
  },

  update: async (routeName, payload) => {
    return await axiosInstance.post(`/${routeName}/update`, payload);
  },

  delete: async (routeName, deleteField, value) => {
    return await axiosInstance.get(`/${routeName}/delete`, {
      params: {
        [deleteField]: value,
      },
    });
  },
  
  get: async (routeName, fieldName, value) => {
  return await axiosInstance.get(`/${routeName}/get`, {
    params: {
      [fieldName]: value,
    },
  });
},
};

export default commonApi;