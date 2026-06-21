export const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

export const STORAGE_KEYS = {
  TOKEN: 'token',
  USERNAME: 'username',
  USER_DATA: 'userData',
};

export const PATHS = {
  LOGIN: '/login',
  REGISTER: '/register',
  HOME: '/home',
  DASHBOARD: '/',
  PROFILE: '/profile',
  BRANDS: '/brands',
  CATEGORIES: '/categories',
  MODELS: '/models',
  NODES: '/nodes',
  PRICES: '/prices',
  PRODUCTS: '/products',
  RACKS: '/racks',
  ROLES: '/roles',
  SHELVES: '/shelves',
  STOCKS: '/stocks',
  UNITS: '/units',
  USERS: '/users',
  WAREHOUSES: '/warehouses',
  CUSTOMERS: '/customers',
  CART: '/cart',
  ORDERS: '/orders',
};

export const HTTP_STATUS = {
  OK: 200,
  CREATED: 201,
  BAD_REQUEST: 400,
  UNAUTHORIZED: 401,
  FORBIDDEN: 403,
  NOT_FOUND: 404,
  SERVER_ERROR: 500,
};

export const ERROR_MESSAGES = {
  NETWORK_ERROR: 'Network error. Please try again.',
  UNAUTHORIZED: 'Unauthorized access. Please login again.',
  INVALID_CREDENTIALS: 'Invalid email or password.',
  VALIDATION_ERROR: 'Please check your input and try again.',
  SERVER_ERROR: 'Server error. Please try again later.',
  NOT_FOUND: 'Resource not found.',
  STORAGE_ERROR: 'Unable to access local storage. Please check your browser settings.',
};

export const ARIA_LABELS = {
  MENU_OPEN: 'Open navigation menu',
  MENU_CLOSE: 'Close navigation menu',
  LOGOUT: 'Logout from the application',
  SEARCH: 'Search records',
  ADD: 'Add new record',
  EDIT: 'Edit record',
  DELETE: 'Delete record',
};

export const MODULE_NAMES = {
  brand: 'Brands',
  category: 'Categories',
  models: 'Models',
  node: 'Nodes',
  price: 'Prices',
  product: 'Products',
  rack: 'Racks',
  role: 'Roles',
  shelf: 'Shelves',
  stock: 'Stocks',
  unit: 'Units',
  user: 'Users',
  warehouse: 'Warehouses',
  customer: 'Customers',
};
