'use client';
 
import { useEffect } from 'react';
import { usePathname, useRouter } from 'next/navigation';
 
export default function ProtectedRoute({ children }) {
 
  const router = useRouter();
  const pathname = usePathname();
 
  useEffect(() => {
 
    const token = localStorage.getItem('token');
 
    const publicRoutes = ['/login', '/register'];
 
    if (!token && !publicRoutes.includes(pathname)) {
      window.location.replace('/login');
    }
 
  }, [pathname]);
 
  return children;
}
 