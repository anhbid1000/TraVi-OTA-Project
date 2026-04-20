// src/routes/ProtectedRoute.js
import React, { useContext } from "react";
import { Navigate } from "react-router-dom";
import { AuthContext } from "../context/AuthContext";

const ProtectedRoute = ({ children }) => {
  const { token } = useContext(AuthContext);

  if (!token) {
    // Nếu chưa đăng nhập → redirect về Login
    return <Navigate to="/login" replace />;
  }

  return children;
};

export default ProtectedRoute;
