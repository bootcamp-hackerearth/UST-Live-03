import React, { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import POSLayout from "./components/POSLayout";
function Home() {

  const [user, setUser] = useState(null);
  const [nodes, setNodes] = useState([]);

  const navigate = useNavigate();

  useEffect(() => {

    const token = localStorage.getItem("token");
    const username = localStorage.getItem("username");

    if (!token) {
      navigate("/login");
      return;
    }

      axios
        .get("http://localhost:8080/api/home", {
          headers: {
            Authorization: `Bearer ${token}`,
          },
      })
      .then((res) => {

        const data = res.data;

        if (Array.isArray(data)) {
          setNodes(data);
          setUser({ name: username });

        } else {
          setNodes(data.nodes || []);
          setUser(data.user || { name: username });
        }
      })
      .catch(() => {
        localStorage.clear();
        navigate("/login");
      });

  }, [navigate]);

  const handleLogout = () => {

    localStorage.clear();
    navigate("/login");
  };

  return (

    <POSLayout
      user={user}
      nodes={nodes}
      handleLogout={handleLogout}
    >

      {/* Home Page Content */}

      <div className="bg-white rounded-xl border border-gray-200 p-6 shadow-sm">

        <h1 className="text-lg font-semibold text-gray-800 mb-2">
          Welcome back, {user?.name}
        </h1>

        <p className="text-sm text-gray-500">
          You're logged into the POS system.
        </p>

      </div>

    </POSLayout>
  );
}

export default Home;