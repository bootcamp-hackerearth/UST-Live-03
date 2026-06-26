import axiosInstance from "./axiosInstance";

const commonApi = {
  active: async (routeName) => {
    return await axiosInstance.get(`/${routeName}/active`);
  },

  toggle: async (routeName, identifier) => {
  return await axiosInstance.patch(
    `/${routeName}/toggle`,
    null,
    {
      params: {
        identifier,
      },
    }
  );
},

  list: async (routeName, payload) => {
    return await axiosInstance.post(`/${routeName}/list`, payload);
  },

  add: async (routeName, payload) => {
    return await axiosInstance.post(`/${routeName}/add`, payload);
  },

  update: async (routeName, payload) => {
    return await axiosInstance.put(`/${routeName}/update`, payload);
  },

  delete: async (routeName, deleteField, value) => {
    return await axiosInstance.delete(`/${routeName}/delete`, {
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

  customPost: async (url, payload) => {
    return await axiosInstance.post(`/${url}`, payload);
  },

  customGet: async (url, params = {}) => {
    return await axiosInstance.get(`/${url}`, {
      params,
    });
  },
};

export default commonApi;