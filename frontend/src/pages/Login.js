// src/pages/Login.js
import React, { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../AuthContext";   // 꼭 import!

function Login() {
  const [error, setError] = useState(false);
  const [logout, setLogout] = useState(false);
  const navigate = useNavigate();
  const { login } = useAuth();      // <-- 핵심! Context의 login 함수 사용

  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    if (params.get("error")) setError(true);
    if (params.get("logout")) setLogout(true);
    if (params.get("needAuth") === "true") {
      alert("로그인이 필요한 서비스입니다.");
    }
  }, []);

  const [form, setForm] = useState({ username: "", password: "" });

  const handleChange = e => {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
  };

  // 실제 로그인 연동
  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(false);
    try {
      // 1. 로그인 요청
      const res = await fetch("/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(form),
      });

      if (!res.ok) {
        setError(true);
        return;
      }

      const data = await res.json();
      const token = data.token;

      // 2. 토큰을 헤더에 실어서 내 정보 조회
      const meRes = await fetch("/api/user/me", {
        headers: { Authorization: "Bearer " + token }
      });
      if (!meRes.ok) {
        setError(true);
        return;
      }
      const me = await meRes.json();
      // me = { nickname: "홍길동", ... }

      // 3. Context/Storage에 토큰, 닉네임 저장
      login(me.nickName, token);

      navigate("/");

    } catch (err) {
      setError(true);
    }
  };

  return (
      <section className="login-wrapper" style={{
        minHeight: "calc(100vh - 100px)",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        backgroundColor: "#f8f9fa",
        padding: "2rem 1rem"
      }}>
        <div className="card p-4 shadow login-card" style={{ width: "100%", maxWidth: 400 }}>
          <h3 className="text-center mb-4">로그인</h3>

          {error && (
              <div className="alert alert-danger">
                아이디 또는 비밀번호가 올바르지 않습니다.
              </div>
          )}

          {logout && (
              <div className="alert alert-success">
                로그아웃 되었습니다.
              </div>
          )}

          <form onSubmit={handleSubmit}>
            <div className="mb-3">
              <label htmlFor="username" className="form-label">이메일</label>
              <input type="text" className="form-control" id="username" name="username"
                     value={form.username} onChange={handleChange} required />
            </div>
            <div className="mb-3">
              <label htmlFor="password" className="form-label">비밀번호</label>
              <input type="password" className="form-control" id="password" name="password"
                     value={form.password} onChange={handleChange} required />
            </div>
            <button type="submit" className="btn btn-primary w-100">로그인</button>
          </form>

          <hr />
          <div className="d-grid gap-2">
            <a href="/oauth2/authorization/google" className="btn btn-outline-dark">Google 로그인</a>
            <a href="/oauth2/authorization/kakao" className="btn btn-warning text-dark">Kakao 로그인</a>
          </div>

          <div className="mt-3 text-center">
            <span>계정이 없으신가요?</span>
            <Link to="/Register" className="btn btn-outline-danger w-100 mt-2">회원가입</Link>
          </div>
        </div>
      </section>
  );
}

export default Login;
