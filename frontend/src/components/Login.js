// src/components/Login.js
import React, { useContext, useState } from "react";
import axiosInstance from "../api/axiosInstance";
import { AuthContext } from "../context/AuthContext";

const Login = () => {
  const { login } = useContext(AuthContext);
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const res = await axiosInstance.post("/auth/login", { username, password });
      login(res.data.token); // backend trả về JWT Token
      alert("Đăng nhập thành công!");
    } catch (err) {
      alert("Đăng nhập thất bại!");
    }
  };

return (
  <form onSubmit={handleSubmit}>
    <input value={username} onChange={(e) => setUsername(e.target.value)} placeholder="Username" />
    <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} placeholder="Password" />
    <button type="submit">Login</button>
  </form>
);

};

export default Login;
