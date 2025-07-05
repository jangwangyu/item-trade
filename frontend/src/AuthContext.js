import React, { createContext, useContext, useState } from "react";

const AuthContext = createContext();

export function AuthProvider({ children }) {
  // 새로고침해도 로그인 유지: localStorage에서 읽기
  const [auth, setAuth] = useState(() => {
    const nickName = localStorage.getItem("nickName");
    const token = localStorage.getItem("token");
    return nickName && token ? { nickName, token } : null;
  });

  // 로그인
  const login = (nickName, token) => {
    setAuth({ nickName, token });
    localStorage.setItem("nickName", nickName);
    localStorage.setItem("token", token);
  };

  // 로그아웃
  const logout = () => {
    setAuth(null);
    localStorage.removeItem("nickName");
    localStorage.removeItem("token");
  };

  return (
      <AuthContext.Provider value={{ auth, login, logout }}>
        {children}
      </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
