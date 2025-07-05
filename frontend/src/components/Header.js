import React from "react";
import { Link, NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../AuthContext";

function Header() {
  const { auth, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
      <nav className="navbar navbar-expand-lg navbar-light bg-light px-4">
        <Link className="navbar-brand" to="/">item-trade</Link>
        <ul className="navbar-nav me-auto">
          <li className="nav-item">
            <NavLink className="nav-link" to="/">홈</NavLink>
          </li>
          <li className="nav-item">
            <NavLink className="nav-link" to="/chat-list">채팅</NavLink>
          </li>
        </ul>
        <ul className="navbar-nav ms-auto">
          {!auth ? (
              <li className="nav-item">
                <NavLink className="nav-link" to="/login">로그인</NavLink>
              </li>
          ) : (
              <>
                <li className="nav-item">
                  <span className="nav-link fw-bold">{auth.nickName}님</span>
                </li>
                <li className="nav-item">
                  <button className="nav-link btn btn-link" style={{ padding: 0 }} onClick={handleLogout}>
                    로그아웃
                  </button>
                </li>
              </>
          )}
        </ul>
      </nav>
  );
}

export default Header;