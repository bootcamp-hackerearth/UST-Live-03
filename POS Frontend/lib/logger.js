const isProduction = process.env.NODE_ENV === 'production';
export const logger = {
  info: (message, context = '') => {
    if (!isProduction) console.log(`[INFO] ${message}`, context);
  },
  warn: (message, context = '') => {
    console.warn(`[WARN] ${message}`, context);
  },
  error: (message, error = null, context = '') => {
    console.error(`[ERROR] ${message}`, { error, context });
  },
  apiError: (url, method, statusCode, errorMessage) => {
    if (statusCode >= 500) {
      console.error(`[API ERROR] ${method} ${url} - ${statusCode}`, {
        url, method, statusCode, errorMessage,
        timestamp: new Date().toISOString(),
      });
    }
  },
  apiSuccess: (url, method, statusCode) => {
    if (!isProduction) console.log(`[API SUCCESS] ${method} ${url} - ${statusCode}`);
  },
  getStorageItem: (key) => {
    try {
      if (globalThis.window?.localStorage) return globalThis.window.localStorage.getItem(key);
    } catch (error) {
      logger.error(`Failed to access localStorage for key: ${key}`, error, 'getStorageItem');
    }
    return null;
  },
  setStorageItem: (key, value) => {
    try {
      if (globalThis.window !== undefined) {
        globalThis.window.localStorage.setItem(key, value);
        return true;
      }
    } catch (error) {
      logger.error(`Failed to set localStorage for key: ${key}`, error, 'setStorageItem');
    }
    return false;
  },
  removeStorageItem: (key) => {
    try {
      if (globalThis.window?.localStorage) {
        globalThis.window.localStorage.removeItem(key);
        return true;
      }
    } catch (error) {
      logger.error(`Failed to remove localStorage for key: ${key}`, error, 'removeStorageItem');
    }
    return false;
  },
};
export default logger;