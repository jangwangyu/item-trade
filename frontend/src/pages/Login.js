// src/pages/Login.js
import React, { useState, useEffect } from "react";
import {Link} from "react-router-dom";

function Login() {
  // 에러/로그아웃 메시지 쿼리파라미터에서 읽기
  const [error, setError] = useState(false);
  const [logout, setLogout] = useState(false);

  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    if (params.get("error")) setError(true);
    if (params.get("logout")) setLogout(true);
    if (params.get("needAuth") === "true") {
      alert("로그인이 필요한 서비스입니다.");
    }
  }, []);

  // 폼 입력값 관리
  const [form, setForm] = useState({ username: "", password: "" });

  const handleChange = e => {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
  };

  // 로그인 submit (실제는 fetch/axios로 서버에 POST)
  const handleSubmit = e => {
    e.preventDefault();
    // 서버와 통신하여 로그인 처리
    alert("로그인 기능은 서버와 연동해야 합니다.");
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

          {/* 로그인 실패 메시지 */}
          {error && (
              <div className="alert alert-danger">
                아이디 또는 비밀번호가 올바르지 않습니다.
              </div>
          )}

          {/* 로그아웃 성공 메시지 */}
          {logout && (
              <div className="alert alert-success">
                로그아웃 되었습니다.
              </div>
          )}

          {/* 로그인 폼 */}
          <form onSubmit={handleSubmit}>
            <div className="mb-3">
              <label htmlFor="username" className="form-label">이메일 또는 아이디</label>
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

          {/* 소셜 로그인 버튼 */}
          <hr />
          <div className="d-grid gap-2">
            <a href="/oauth2/authorization/google" className="btn btn-outline-dark">Google 로그인</a>
            <a href="/oauth2/authorization/kakao" className="btn btn-warning text-dark">Kakao 로그인</a>
          </div>

          {/* 회원가입 이동 버튼 */}
          <div className="mt-3 text-center">
            <span>계정이 없으신가요?</span>
            <Link to="/Register" className="btn btn-outline-danger w-100 mt-2">회원가입</Link>
          </div>
        </div>
      </section>
  );
}

export default Login;
