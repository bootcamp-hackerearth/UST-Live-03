"use client";
 
import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
 
export default function useAuth() {
  const router = useRouter();
  const [authorized, setAuthorized] = useState(false);
 
  useEffect(() => {
    const token = localStorage.getItem("token");
 
    if (!token) {
      router.replace("/login");
      return;
    }
 
    setAuthorized(true);
  }, [router]);
 
  return authorized;
}
 