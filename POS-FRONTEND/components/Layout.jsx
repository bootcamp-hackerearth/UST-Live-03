'use client';

import { useEffect, useState } from 'react';
import { UserCircleIcon } from '@heroicons/react/24/solid';
import { useRouter } from 'next/navigation';
import PropTypes from 'prop-types';

import api from '@/app/services/api';
import Profile from '@/components/Profile';

const Layout = ({ children }) => {

  const router = useRouter();

  const [username, setUsername] = useState('');

  const [nodes, setNodes] = useState([]);

  const [showProfileModal, setShowProfileModal] = useState(false);

  useEffect(() => {

    setUsername(localStorage.getItem('username') || '');

    void fetchNodes();

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

  return (

    <div
      className="min-h-screen flex bg-[#f5f6fa]"
      style={{
        fontFamily: "'Poppins', sans-serif"
      }}
    >

      <div className="fixed top-0 left-0 w-[250px] h-screen bg-gradient-to-b from-[#0f2027] to-[#203a43] shadow-2xl flex flex-col overflow-hidden">

        <button
          type="button"
          onClick={() => router.push('/home')}
          className="
            px-5
            py-5
            border-b
            border-white/10
            hover:bg-white/5
            transition
            group
            w-full
            text-left
          "
        >

          <div className="flex items-center gap-3">

            <div
              className="
                min-w-[55px]
                h-[55px]
                bg-blue-500
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

              <h1
                className="
                  text-white
                  text-2xl
                  font-bold
                  truncate
                "
              >
                Dashboard
              </h1>

              <p
                className="
                  text-xs
                  text-gray-400
                  mt-1
                "
              >
                Go to Home
              </p>

            </div>

          </div>

        </button>

        <div className="flex-1 overflow-y-auto px-3 py-4 space-y-2">

          {nodes.map((node) => (

            <button
              key={node.id || node.identifier}
              type="button"
              onClick={() => router.push(node.path.toLowerCase())}
              className="
                w-full
                flex
                items-center
                px-4
                py-3
                rounded-xl
                text-left
                text-gray-300
                text-[15px]
                font-medium
                hover:bg-white/10
                hover:text-white
                transition-all
                duration-300
              "
            >
              {node.identifier}
            </button>

          ))}

        </div>

        <div className="p-4 border-t border-white/10">

          <button
            type="button"
            onClick={logout}
            className="
              w-full
              bg-red-500
              hover:bg-red-600
              text-white
              py-3
              rounded-xl
              text-sm
              font-semibold
              transition
            "
          >
            Logout
          </button>

        </div>

      </div>

      <div className="ml-[250px] flex-1 p-8 min-h-screen bg-[#f5f6fa]">

        <div
          className="
            bg-white
            rounded-2xl
            shadow-md
            px-8
            py-4
            flex
            justify-between
            items-center
            mb-8
          "
        >

          <div>

            <h1 className="text-2xl font-bold text-slate-800">
              Point Of Sale System
            </h1>

            <p className="text-sm text-gray-500">
              Manage products, users, roles
            </p>

          </div>

          <button
            type="button"
            onClick={() => setShowProfileModal(true)}
            className="
              flex
              items-center
              gap-3
              bg-slate-50
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

      {showProfileModal && (

        <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50">

          <div className="bg-white w-[700px] max-h-[90vh] overflow-y-auto rounded-2xl relative">

            <button
              type="button"
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

      )}

    </div>
  );
};

Layout.propTypes = {
  children: PropTypes.node.isRequired,
};

export default Layout;