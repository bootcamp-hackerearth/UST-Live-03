"use client";
import { useState } from 'react';
import Sidebar from './Layout/Sidebar';
import PropTypes from 'prop-types';
import Header from './Layout/Header';
import ProtectedRoute from '@/component/ProtectedRoute';
MainLayout.propTypes = {
  children: PropTypes.node.isRequired,
};
export default function MainLayout({ children }) {
  const [isSidebarCollapsed, setIsSidebarCollapsed] = useState(false);
  const toggleSidebar = () => setIsSidebarCollapsed(prev => !prev);

  return (
    <ProtectedRoute>
    <div className="flex min-h-screen bg-gray-50">
      <Sidebar isCollapsed={isSidebarCollapsed} />
      <div className="flex-1 flex flex-col min-w-0">
        <Header toggleSidebar={toggleSidebar} isSidebarCollapsed={isSidebarCollapsed} />
        <main className="flex-1 p-6 overflow-y-auto relative">
          {children}
        </main>
      </div>
    </div>
    </ProtectedRoute>
  );
}