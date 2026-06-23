export const htmlEscape = (str) => {
  if (!str || typeof str !== 'string') return '';
  const map = {
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
    '"': '&quot;',
    "'": '&#39;',
    '/': '&#x2F;',
  };
  return str.replaceAll(/[&<>"']/g, (char) => map[char]);
};

class RateLimiter {
  constructor(maxAttempts = 5, windowMs = 60000) {
    this.maxAttempts = maxAttempts;
    this.windowMs = windowMs;
    this.attempts = new Map();
  }
  isAllowed(key) {
    const now = Date.now();
    const record = this.attempts.get(key) || { count: 0, resetTime: now + this.windowMs };
    if (now > record.resetTime) {
      record.count = 0;
      record.resetTime = now + this.windowMs;
    }
    record.count += 1;
    this.attempts.set(key, record);
    const allowed = record.count <= this.maxAttempts;
    return allowed;
  }
  reset(key) {
    this.attempts.delete(key);
  }
  getRemaining(key) {
    const record = this.attempts.get(key);
    if (!record) return this.maxAttempts;
    return Math.max(0, this.maxAttempts - record.count);
  }
}

export const loginRateLimiter = new RateLimiter(5, 15 * 60 * 1000);
export const apiCallRateLimiter = new RateLimiter(100, 60 * 1000);
export const formSubmitRateLimiter = new RateLimiter(3, 5 * 1000);
export const debounce = (func, delayMs = 300) => {
  let timeoutId;
  return (...args) => {
    clearTimeout(timeoutId);
    timeoutId = setTimeout(() => func(...args), delayMs);
  };
};
export const throttle = (func, limitMs = 1000) => {
  let inThrottle;
  return (...args) => {
    if (!inThrottle) {
      func(...args);
      inThrottle = true;
      setTimeout(() => (inThrottle = false), limitMs);
    }
  };
};
export const validators = {
  email: (email) => {
    if (!email || typeof email !== 'string' || email.length > 254) return false;
    const re = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$/;
    return re.test(email);
  },
  phone: (phone) => {
    if (!phone || typeof phone !== 'string') return false;
    return /^\d{10}$/.test(phone.replaceAll(/\D/g, ''));
  },
  passwordStrength: (password) =>
    password && typeof password === 'string' && password.length > 0,
  positiveNumber: (value, min = 0, max = 999999) => {
    const num = Number(value);
    return !Number.isNaN(num) && num >= min && num <= max;
  },
  creditLimit: (value) => {
    return validators.positiveNumber(value, 0, 1000000);
  },
  quantity: (value) => {
    const num = Number(value);
    return !Number.isNaN(num) && num >= 1 && num <= 100000;
  },
  identifier: (value) => {
    if (!value || typeof value !== 'string') return false;
    return /^[a-zA-Z0-9_-]+$/.test(value);
  },
  name: (value) => {
    if (!value || typeof value !== 'string') return false;
    return /^[a-zA-Z\s\-']+$/.test(value);
  },
};


export const safeJsonParse = (json, defaultValue = null) => {
  try {
    return JSON.parse(json);
  } catch {
    return defaultValue;
  }
};

export const sanitizeObject = (obj, fieldsToRemove = ['password', 'token', 'secret']) => {
  if (!obj || typeof obj !== 'object') return obj;
  
  const sanitized = { ...obj };
  fieldsToRemove.forEach((field) => {
    delete sanitized[field];
  });
  
  return sanitized;
};

export const isSafeOperation = (state) => {
  if (!state) return false;
  
  if (state.saving || state.deleting) return false;
  
  if (state.error && state.error.length > 0) return false;
  
  return true;
};

export const validateCart = (cart) => {
  if (!cart) return 'No cart found';
  if (!cart.cartEntries || cart.cartEntries.length === 0) return 'Cart is empty';
  if (!cart.identifier) return 'Invalid cart';
  if (typeof cart.totalPrice !== 'number' || cart.totalPrice <= 0) return 'Invalid cart total';
  
  return null;
};

export const validateOrderPlacement = (cart, paymentMode) => {
  const cartError = validateCart(cart);
  if (cartError) return cartError;
  
  if (!paymentMode || typeof paymentMode !== 'string') return 'Payment mode required';
  const validModes = ['CASH', 'CARD', 'CREDIT', 'CHEQUE'];
  if (!validModes.includes(paymentMode.toUpperCase())) return 'Invalid payment mode';
  
  return null;
};

export default {
  htmlEscape,
  RateLimiter,
  loginRateLimiter,
  apiCallRateLimiter,
  formSubmitRateLimiter,
  debounce,
  throttle,
  validators,
  safeJsonParse,
  sanitizeObject,
  isSafeOperation,
  validateCart,
  validateOrderPlacement,
};
