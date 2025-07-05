// src/pages/Register.js
import React, { useState } from "react";

function Register() {
  const [form, setForm] = useState({
    email: "",
    nickname: "",
    password: "",
    confirmPassword: ""
  });

  const [loading, setLoading] = useState(false);

  // 메시지 상태
  const [message, setMessage] = useState({ type: "", text: "" });

  const handleChange = e => {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async e => {
    e.preventDefault();
    setMessage({ type: "", text: "" });

    // 간단한 클라이언트 검증
    if (form.password !== form.confirmPassword) {
      setMessage({ type: "danger", text: "비밀번호가 일치하지 않습니다." });
      return;
    }

    setLoading(true);
    try {
      const res = await fetch("/api/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(form)
      });

      if (!res.ok) {
        const error = await res.json();
        setMessage({ type: "danger", text: error.message || "회원가입 실패" });
        setLoading(false);
        return;
      }

      setMessage({ type: "success", text: "✅ 회원가입 성공! 로그인 페이지로 이동합니다." });
      setTimeout(() => {
        window.location.href = "/login";
      }, 1500);

    } catch (err) {
      setMessage({ type: "danger", text: "서버 오류가 발생했습니다." });
      setLoading(false);
    }
  };

  return (
      <section className="register-wrapper" style={{
        minHeight: "calc(100vh - 100px)",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        backgroundColor: "#f8f9fa",
        padding: "2rem 1rem"
      }}>
        <div className="card p-4 shadow register-card" style={{ width: "100%", maxWidth: 500, maxHeight: "90vh", overflowY: "auto" }}>
          <h3 className="text-center mb-4">회원가입</h3>

          {/* 메시지 표시 */}
          {message.text && (
              <div className={`alert alert-${message.type}`}>{message.text}</div>
          )}

          <form onSubmit={handleSubmit} autoComplete="off">
            <div className="mb-3">
              <label htmlFor="email" className="form-label">이메일</label>
              <input
                  type="email"
                  name="email"
                  className="form-control"
                  id="email"
                  value={form.email}
                  onChange={handleChange}
                  required
                  autoComplete="off"
              />
            </div>
            <div className="mb-3">
              <label htmlFor="nickname" className="form-label">닉네임</label>
              <input
                  type="text"
                  name="nickname"
                  className="form-control"
                  id="nickname"
                  value={form.nickname}
                  onChange={handleChange}
                  required
                  autoComplete="off"
              />
            </div>
            <div className="mb-3">
              <label htmlFor="password" className="form-label">비밀번호</label>
              <input
                  type="password"
                  name="password"
                  className="form-control"
                  id="password"
                  value={form.password}
                  onChange={handleChange}
                  required
                  autoComplete="new-password"
              />
            </div>
            <div className="mb-3">
              <label htmlFor="confirmPassword" className="form-label">비밀번호 확인</label>
              <input
                  type="password"
                  name="confirmPassword"
                  className="form-control"
                  id="confirmPassword"
                  value={form.confirmPassword}
                  onChange={handleChange}
                  required
                  autoComplete="new-password"
              />
            </div>
            <button
                type="submit"
                className="btn btn-primary w-100"
                disabled={loading}
            >
              {loading ? "가입 중..." : "가입하기"}
            </button>
          </form>

          <div className="mt-3 text-center">
            이미 계정이 있으신가요?{" "}
            <a href="/login">로그인하기</a>
          </div>
        </div>
      </section>
  );
}

export default Register;
