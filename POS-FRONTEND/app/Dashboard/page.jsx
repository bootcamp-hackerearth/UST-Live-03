"use client";
import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import axios from "axios";
import Layout from "../Components/Layout";

function Dashboard() {

  const [nodes, setNodes] = useState([]);
  const [user, setUser] = useState(null);

  const router = useRouter();

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      router.push(
        "/login?error=" +
          encodeURIComponent(
            "Session expired or missing credentials. Please login again."
          )
      );
      return;
    }

    fetchNodes();
    fetchProfile();
  }, []);

  const fetchProfile = async () => {

    try {

      const token = localStorage.getItem("token");

      if (!token) {
        router.push("/login");
        return;
      }

      const response = await axios.get(
        "http://localhost:8080/api/user/profile",
        {
          headers: { Authorization: `Bearer ${token}` }
        }
      );

      setUser(response.data);

    } catch (error) {

      console.error("Profile fetch error:", error);
      if (error.response?.status === 401 || error.response?.status === 403) {
        localStorage.removeItem("token");
        localStorage.removeItem("user");
        router.push(
          "/Login?error=" +
            encodeURIComponent(
              "Session expired or invalid login. Please login again."
            )
        );
      }

    }
  };

  const fetchNodes = async () => {

    try {

      const token = localStorage.getItem("token");

      if (!token) {
        localStorage.removeItem("token");
        localStorage.removeItem("user");
        router.push(
          "/Login?error=" +
            encodeURIComponent(
              "Session expired or missing credentials. Please login again."
            )
        );
        return;
      }

      const response = await axios.post(
        "http://localhost:8080/api/node/list",
        {
          page: 0,
          sizePerPage: 100,
          sortDirection: "ASC",
          sortField: "id"
        },
        {
          headers: { Authorization: `Bearer ${token}` }
        }
      );

      setNodes(response.data);

    } catch (error) {

      console.error("Node fetch error:", error);
      if (error.response?.status === 401 || error.response?.status === 403) {
        localStorage.removeItem("token");
        localStorage.removeItem("user");
        router.push(
          "/Login?error=" +
            encodeURIComponent(
              "Session expired or invalid login. Please login again."
            )
        );
      }

    }
  };

  const logout = () => {

    localStorage.clear();

    router.push("/Login");

  };

  return (

    <Layout
      user={user}
      nodes={nodes}
      logout={logout}
      navigate={router}
    >

      {/* Page Content */}

    </Layout>

  );
}

export default Dashboard;