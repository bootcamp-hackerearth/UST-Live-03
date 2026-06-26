// src/utils/iconMapping.js
import {
  FiHome,
  FiLayers,
  FiUsers,
  FiShield,
  FiPackage,
  FiDollarSign,
  FiTrello,
  FiGrid,
  FiBox,
  FiTag,
  FiColumns,
  FiCreditCard,
  FiArchive,
  FiUserCheck,
  FiShoppingCart,
} from 'react-icons/fi';

/**
 * Maps node identifier (from backend) to a React Icon component
 * @param {string} identifier - The node identifier from backend (e.g., "User", "Product")
 * @returns {React.Component} - The corresponding icon component
 */
export const getIconForNode = (identifier) => {
  const iconMap = {
    // System & Config
    'Node': FiGrid,
    'Dashboard': FiHome,
    
    // User Management
    'User': FiUsers,
    'Role': FiShield,
    'Customer': FiUserCheck,
    
    // Inventory
    'Stock': FiPackage,
    'Product': FiLayers,
    'Warehouse': FiTrello,
    'Shelfs': FiColumns,
    'Rack': FiArchive,
    
    // Product Attributes
    'Category': FiTag,
    'Model': FiTag,
    'Brand': FiTag,
    'Unit': FiCreditCard,
    
    // Pricing
    'Price': FiDollarSign,
    'Cart': FiShoppingCart,
  };

  // Return the mapped icon or default to FiPackage
  return iconMap[identifier] || FiPackage;
};