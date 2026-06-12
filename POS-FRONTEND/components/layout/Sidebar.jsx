'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import PropTypes from "prop-types";
import api from "../../services/api"; 

const Sidebar = ({ children }) => {
  const router = useRouter();

  const [nodes, setNodes] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchNodes();
  }, []);

  const fetchNodes = async () => {
    try {
      const token = localStorage.getItem('token');

      if (!token) {
        router.push('/login');
        return;
      }

      const res = await api.get('/node/NodesForRoles', {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      setNodes(res.data);
    } catch (err) {
      console.log('Error fetching nodes:', err);
      router.push('/login');
    } finally {
      setLoading(false);
    }
  };

  const handleNavigation = (node) => {
    let path = node.path || '/home';

    if (path.endsWith('/list')) {
      path = path.replace('/list', '');
    }

    if (!path.startsWith('/')) {
      path = '/' + path;
    }
    router.push(path);
  };

  const logout = () => {
    localStorage.clear();
    router.push('/login');
  };

  if (loading) return <div>Loading...</div>;

  return (
    <div className="min-h-screen flex">
      <div className="fixed w-[240px] h-screen bg-blue-700 text-white flex flex-col overflow-hidden">
        <div className="py-6 text-center text-lg font-bold border-b border-white/20">
          POS System
        </div>
        <div
          className="flex-1 overflow-y-auto bg-blue-700"
          style={{
            scrollbarWidth: 'thin',
            scrollbarColor: '#3b82f6 #1d4ed8'
          }}>
          {nodes.map((node) => (
             <button
              key={node.identifier}
              onClick={() => handleNavigation(node)}
              className="block w-full text-left px-6 py-3 hover:bg-blue-500" >
                {node.identifier}
                </button>
              ))}

        </div>
        <div className="p-4 border-t border-white/20">
          <button
            onClick={logout}
            className="w-full bg-white text-blue-700 py-2 rounded hover:bg-gray-200"
          >
            Logout
          </button>
        </div>
      </div>
      <div className="ml-[240px] flex-1">
        <div className="p-6">
          {children}
        </div>
      </div>

    </div>
  );
};
Sidebar.propTypes = {
  children: PropTypes.node.isRequired,
};
export default Sidebar;