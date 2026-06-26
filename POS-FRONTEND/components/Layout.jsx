'use client';
 
import { useEffect, useState } from 'react';
import { UserCircleIcon,Bars3Icon,XMarkIcon } from '@heroicons/react/24/solid';
import { useRouter } from 'next/navigation';
import PropTypes from 'prop-types';
 
import api from '@/app/services/api';
import Profile from '@/components/Profile';
 
const Layout = ({ children }) => {
 
  const router = useRouter();
 
  const [username, setUsername] = useState('');
 
  const [nodes, setNodes] = useState([]);
 
  const [showProfileModal, setShowProfileModal] = useState(false);

  const [sidebarOpen, setSidebarOpen] = useState(true);

  useEffect(() => {
    const storedUsername = localStorage.getItem('username');
    setUsername(storedUsername);
    fetchNodes();
  }, []);
 
  const fetchNodes = async () => {
    try {
      const response = await api.get('/home');
      setNodes(response.data);
    } catch (error) {
      console.error(error);
    }
  };
 
  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    localStorage.removeItem('userRole');
    router.push('/login');
  };

  const getShortCode = (name) => {
    const map = {
      Order: "OR",
      Warehouse: "WA",
      User: "US",
      Customer: "CU",
      Category: "CA",
      Shelf: "SH",
      Node: "NO",
      Product: "PR",
      Role: "RO",
    };

    return map[name] || name.substring(0, 2).toUpperCase();
  };
 
  return (
 
        <div
          className="min-h-screen flex bg-[#f5f6fa]"
          style={{
            fontFamily: "'Poppins', sans-serif"
          }}
        >
            <div
              className={`
                fixed
                top-0
                left-0
                h-screen
                bg-white
                border-r
                border-gray-200
                shadow-lg
                flex
                flex-col
                overflow-hidden
                transition-all
                duration-300
                ${sidebarOpen ? "w-[250px]" : "w-[80px]"}
                `}
            >

              <button
                onClick={() => router.push('/home')}
                className="
                px-5
                py-5
                border-b
                border-white/10
                cursor-pointer
                hover:bg-white/5
                transition
                group
                text-left
                "
              >
 
              <div className="flex items-center gap-3">
                <div
                  className="
                    min-w-[55px]
                    h-[55px]
                    bg-cyan-500
                    rounded-xl
                    flex
                    items-center
                    justify-center
                    shadow-md
                    group-hover:scale-105
                    transition
                  "
                >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
                strokeWidth={1.8}
                stroke="currentColor"
                className="w-7 h-7 text-white"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  d="M2.25 12 11.204 3.045a1.125 1.125 0 0 1 1.592 0L21.75 12M4.5 9.75V19.5A2.25 2.25 0 0 0 6.75 21.75h10.5A2.25 2.25 0 0 0 19.5 19.5V9.75"
                />
              </svg>
            </div>
 
            <div className="overflow-hidden">
 
              <h1 className="
                text-slate-800
                text-2xl
                font-bold
                truncate
              ">
                Dashboard
              </h1>
 
              <p className="
                text-xs
                text-slate-500
                mt-1
              ">
                Go to Home
              </p>
            </div>
        </div>
        </button>
 
 
        <div className="flex-1 overflow-y-auto px-3 py-4 space-y-2">
          {nodes.map((node) => (
            <button
              key={node.identifier}
              onClick={() => router.push(node.path.toLowerCase())}
              className="
                w-full
                flex
                items-center
                gap-3
                px-3
                py-3
                rounded-xl
                hover:bg-cyan-50
                transition
              "
            >
              <div
                className="
                  w-9
                  h-9
                  rounded-lg
                  bg-cyan-500
                  text-white
                  flex
                  items-center
                  justify-center
                  text-xs
                  font-bold
                  shrink-0
                "
              >
                {getShortCode(node.identifier)}
              </div>

              {sidebarOpen && (
                <span className="text-slate-700 font-medium">
                  {node.identifier}
                </span>
              )}
            </button>
          ))}
        </div>
 
        <div className="p-4 border-t border-white/10">
 
          <button
            onClick={logout}
            className="
              w-full
              bg-orange-500
              hover:bg-orange-600
              text-white
              py-3
              rounded-xl
              font-semibold
              transition
            "
          >
            Logout
          </button>
        </div>
      </div>

          <div
            className={`
              flex-1
              p-8
              min-h-screen
              bg-[#f8fafc]
              transition-all
              duration-300
              ${sidebarOpen ? "ml-[250px]" : "ml-[80px]"}
            `}
          >

          <div
            className="
              bg-white
              rounded-2xl
              shadow-lg
              border-t-4
              border-cyan-500
              px-8
              py-4
              flex
              justify-between
              items-center
              mb-8
            "
          >
 
          <div className="flex items-center gap-4">

            <button
              onClick={() => setSidebarOpen(!sidebarOpen)}
              className="
              p-2
              rounded-lg
              hover:bg-gray-100
              transition
            "
            >
              {sidebarOpen ? (
                <XMarkIcon className="w-6 h-6 text-slate-700" />
              ) : (
                <Bars3Icon className="w-6 h-6 text-slate-700" />
              )}
            </button>

            <div>
              <h1 className="text-2xl font-bold text-slate-800">
                Point Of Sale System
              </h1>

              <p className="text-sm text-gray-500">
                Manage products, users, roles
              </p>
            </div>

          </div>
 
              <button
                onClick={() => setShowProfileModal(true)}
                className="
                  flex
                  items-center
                  gap-3
                  bg-slate-50
                  border
                  border-slate-200
                  px-4
                  py-2
                  rounded-xl
                  hover:bg-slate-100
                  transition
                "
              >
 
                <UserCircleIcon className="h-10 w-10 text-slate-700" />
            
                <div className="text-left">
            
                  <p className="text-xs text-gray-500">
                    Logged in as
                  </p>
            
                  <p className="text-sm font-semibold text-gray-800">
                    {username}
                  </p>
            
                </div>
 
            </button>
 
          </div>
        {children}
    </div>
      {
        showProfileModal && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50">
            <div className="bg-white w-[700px] max-h-[90vh] overflow-y-auto rounded-2xl relative">
              <button
                onClick={() => setShowProfileModal(false)}
                className="
                  absolute
                  top-4
                  right-4
                  bg-red-500
                  hover:bg-red-600
                  text-white
                  px-3
                  py-1
                  rounded-lg
                  z-50
                "
              >
                X
              </button>
              <Profile
                closeModal={() => setShowProfileModal(false)}
              />
            </div>
          </div>
        )
      }
    </div>
  );
};
 
Layout.propTypes = {
  children: PropTypes.node.isRequired
};

export default Layout;