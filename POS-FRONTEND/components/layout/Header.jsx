'use client';

import { useRouter } from 'next/navigation';

const Header = () => {
  const router = useRouter();
  let username = '';

  if (typeof globalThis !== 'undefined') {
    username = globalThis.localStorage.getItem('username') || '';
  }

  let initial = '';

  if (username) {
    initial = username.charAt(0).toUpperCase();
  }

  return (
    <header className="fixed top-0 left-64 right-0 h-20 bg-white border-b border-blue-500 shadow-md z-50 flex justify-end items-center px-6">
      <div className="flex items-center gap-3">
        <span className="text-base text-gray-700 hidden md:block font-medium">
          {username}
        </span>
        <button
          onClick={() => router.push('/profile')}
          className="bg-blue-600 hover:bg-blue-700 transition text-white w-10 h-10 rounded-full flex items-center justify-center text-base font-semibold shadow"
          title={username}>
          {initial}
        </button>
      </div>
    </header>
  );
};

export default Header;