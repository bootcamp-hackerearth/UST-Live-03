// src/layouts/Sidebar.jsx
"use client";
import { useState, useEffect } from 'react';
import Link from 'next/link';
import { usePathname } from 'next/navigation'; 
import PropTypes from 'prop-types';
import api from '../api/axios.jsx';
import { FiChevronRight } from 'react-icons/fi';
import { getIconForNode } from '../utils/iconMapping.jsx';

const Sidebar = ({ isCollapsed = false }) => {
  const [nodes, setNodes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const pathname = usePathname(); 
  const fetchNodes = async () => {
      try {
        const response = await api.post('/api/node/list', {
          page: 0,
          sizePerPage: 100,
          sortDirection: 'ASC',
          sortField: 'identifier'
        });
        
        const datanode = response.data.dtoList;
        const nodeData = Array.isArray(datanode) 
          ? datanode 
          : datanode.content || [];
        console.log(nodeData);
        
        const userRoles = JSON.parse(localStorage.getItem("roles") || "[]");
        const filteredNodes = nodeData.filter(node => {
          if(!node.roles || node.roles.length === 0)
            return true;
          return node.roles.some(role => userRoles.includes(role));
        });

        setNodes(filteredNodes);
        
        setError(null);
      } catch (err) {
        console.error("Could not load navigation nodes:", err);
        setError("Failed to load menu");
      } finally {
        setLoading(false);
      }
    };
    
  useEffect(() => {
    fetchNodes();
  }, []);
  useEffect(() => {

    const refreshSidebar = () => {
        console.log("Sidebar refresh received");
        fetchNodes();
    };

    globalThis.addEventListener(
        "refreshSidebar",
        refreshSidebar
    );

    return () => {
        globalThis.removeEventListener(
            "refreshSidebar",
            refreshSidebar
        );
    };

}, []);
let sidebarContent;

if (loading) {
  sidebarContent = (
    <li className="text-center text-sm opacity-75 mt-8 animate-pulse">
      Loading...
    </li>
  );
} else if (error) {
  sidebarContent = (
    <li className="text-center text-xs text-red-200 bg-red-500/20 rounded-lg p-3 mt-4">
      {error}
    </li>
  );
} else {
  sidebarContent = nodes.map((node) => {
    const IconComponent = getIconForNode(node.identifier);
    const navPath = node.path || `/${node.identifier.toLowerCase()}`;

    const isActive = pathname === navPath;

    return (
      <li key={node.identifier || node.id}>
        <Link
          href={navPath}
          className={`flex items-center rounded-lg transition-all duration-200 group
            ${isCollapsed ? 'justify-center px-2 py-3' : 'gap-3 px-4 py-3'}
            ${isActive
              ? 'bg-white/25 shadow-lg'
              : 'hover:bg-white/10 hover:shadow-md'
            }`}
          title={isCollapsed ? node.identifier : undefined}
        >
          <div className="flex-shrink-0">
            <IconComponent
              size={20}
              className="transition-transform group-hover:scale-110"
            />
          </div>

          {!isCollapsed && (
            <>
              <span className="flex-1 text-sm font-medium whitespace-nowrap">
                {node.identifier}
              </span>
              <FiChevronRight
                size={14}
                className="opacity-50 transition-transform group-hover:translate-x-1"
              />
            </>
          )}
        </Link>
      </li>
    );
  });
}

  return (
    <aside 
      className={`flex flex-col text-white transition-all duration-300 ease-in-out ${
        isCollapsed ? 'w-20' : 'w-64'
      }`}
      style={{ 
        minHeight: '100vh',
        background: '#231F20',
        flexShrink: 0
      }}
    >
      <Link
  href="/Layout"
  className={`px-4 py-5 border-b border-white/20 flex items-center cursor-pointer hover:bg-white/10 transition-colors sticky top-0 z-10${
    isCollapsed ? 'justify-center' : 'justify-start'
  }`}
  style={{background: '#231F20'}}
>
  {isCollapsed ? (
    <div className="w-10 h-10 bg-white/20 rounded-lg flex items-center justify-center font-bold text-sm">
      POS
    </div>
  ) : (
    <h5 className="font-bold text-lg">POS System</h5>
  )}
</Link>
      <nav className="flex-1 overflow-y-auto py-4">
        <ul className="flex flex-col gap-1 px-3">
          {sidebarContent}
        </ul>
      </nav>
    </aside>
  );
};
Sidebar.propTypes = {
  isCollapsed: PropTypes.bool
};
export default Sidebar;
