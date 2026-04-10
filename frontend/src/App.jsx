// src/App.js
import React from "react";
import { AuthProvider } from "./context/AuthContext";
import Login from "./components/Login";

function App() {
  return (
    <AuthProvider>
      <Login />
      {/* Các component khác */}
    </AuthProvider>
  );
}

export default App;
