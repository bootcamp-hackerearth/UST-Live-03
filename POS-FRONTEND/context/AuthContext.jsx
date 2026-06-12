"use client";

import {createContext, useContext, useEffect, useState, useCallback, useMemo } from "react";
import {getCurrentUser, getNodesForRoles } from "@/services/api";
import PropTypes from "prop-types";

const AuthContext = createContext();

export function AuthProvider({
  children,
}) {

  const [user, setUser] =
    useState(null);

  const [nodes, setNodes] =
    useState([]);

  const [loading, setLoading] =
    useState(true);

  const loadUserData = useCallback(async () => {

    try {

      const userData =
        await getCurrentUser();

      setUser(userData);

      const nodeData =
        await getNodesForRoles();

      const activeNodes =
        nodeData.filter(
          (node) => node.status
        );

      setNodes(activeNodes);

    } catch (error) {

      console.log(error);

    } finally {

      setLoading(false);

    }

  }, []);

  useEffect(() => {

    loadUserData();

  }, [loadUserData]);

  const value = useMemo(
    () => ({
      user,
      setUser,
      nodes,
      setNodes,
      loading,
      loadUserData,
    }),
    [user, nodes, loading, loadUserData]
  );

  return (

    <AuthContext.Provider
      value={value}
    >

      {children}

    </AuthContext.Provider>

  );

}

AuthProvider.propTypes = {
  children: PropTypes.node,
};

export function useAuth() {

  return useContext(AuthContext);

}