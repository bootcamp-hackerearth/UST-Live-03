'use client';
 
import { useEffect } from 'react';
import { usePathname} from 'next/navigation';
 
export default function ProtectedRoute({ children }) {

  const pathname = usePathname();
 
  useEffect(() => {
 
    const token = localStorage.getItem('token');
 
    const publicRoutes = ['/login', '/register'];
 
    if (!token && !publicRoutes.includes(pathname)) {
      globalThis.location.replace('/login');
    }
 
  }, [pathname]);
 
  return children;
}