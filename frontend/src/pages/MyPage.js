// src/pages/MyPage.js
import React, { useState } from "react";

function MyPage() {
  // 샘플 데이터 (실제 데이터는 API에서 받아오면 됨)
  const [myPosts] = useState([
    { id: 1, title: "내 게시글 1", price: 120000 },
    { id: 2, title: "내 게시글 2", price: 30000 },
  ]);
  const [likedPosts] = useState([
    { id: 3, title: "찜한 글 1", price: 40000 },
    { id: 4, title: "찜한 글 2", price: 78000 },
  ]);
  const [blockedMembers, setBlockedMembers] = useState([
    { id: 1, nickName: "차단유저1" },
    { id: 2, nickName: "차단유저2" },
  ]);
  const [member, setMember] = useState({
    nickName: "홍길동",
    email: "hong@example.com",
    introduction: "안녕하세요~",
  });
  // 소셜 로그인 여부
  const [loginType] = useState("OAUTH2"); // "OAUTH2" 또는 "NORMAL"

  // 입력 값 관리
  const [form, setForm] = useState({
    username: member.nickName,
    email: member.email,
    password: "",
    introduction: member.introduction,
  });

  // 이벤트 핸들러
  const handleInputChange = e => {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
  };

  // 차단 해제
  const handleUnblock = (id) => {
    if (window.confirm("정말 차단을 해제하시겠습니까?")) {
      setBlockedMembers(blockedMembers.filter(user => user.id !== id));
      // 실제로는 API 호출
    }
  };

  // 내 정보 수정
  const handleSubmit = e => {
    e.preventDefault();
    // 실제로는 PUT/PATCH 요청
    alert("내 정보가 수정되었습니다.");
    setMember({
      ...member,
      nickName: form.username,
      email: form.email,
      introduction: form.introduction,
    });
  };

  // 회원 탈퇴
  const handleDelete = e => {
    e.preventDefault();
    if (window.confirm("정말 탈퇴하시겠습니까?")) {
      // 실제로는 API 호출
      alert("탈퇴 처리되었습니다.");
    }
  };

  return (
      <section className="container mt-5">

        {/* 내 게시글 */}
        <h3>내 게시글</h3>
        <ul className="list-group mb-4">
          {myPosts.map(post => (
              <li className="list-group-item d-flex justify-content-between" key={post.id}>
                <a href={`/posts/${post.id}`}>{post.title}</a>
                <span>{post.price.toLocaleString()}원</span>
              </li>
          ))}
        </ul>

        {/* 찜한 게시글 */}
        <h3 className="mt-5">찜한 게시글</h3>
        <ul className="list-group mb-4">
          {likedPosts.map(post => (
              <li className="list-group-item d-flex justify-content-between" key={post.id}>
                <a href={`/posts/${post.id}`}>{post.title}</a>
                <span>{post.price.toLocaleString()}원</span>
              </li>
          ))}
        </ul>

        {/* 차단한 회원 목록 */}
        <h3 className="mt-5">차단한 회원 목록</h3>
        <ul className="list-group mb-4">
          {blockedMembers.map(user => (
              <li className="list-group-item d-flex justify-content-between align-items-center" key={user.id}>
                <span>{user.nickName}</span>
                <button
                    className="btn btn-sm btn-outline-secondary ms-3"
                    onClick={() => handleUnblock(user.id)}
                >
                  차단 해제
                </button>
              </li>
          ))}
          {blockedMembers.length === 0 && <li className="list-group-item">차단한 사용자가 없습니다.</li>}
        </ul>

        {/* 내 정보 수정 폼 */}
        <h2 className="mt-4">내 정보 수정</h2>
        <form onSubmit={handleSubmit}>
          <div className="mb-3">
            <label htmlFor="username" className="form-label">닉네임</label>
            <input
                type="text"
                className="form-control"
                id="username"
                name="username"
                value={form.username}
                onChange={handleInputChange}
                readOnly={loginType === "OAUTH2"}
            />
          </div>
          <div className="mb-3">
            <label htmlFor="email" className="form-label">이메일</label>
            <input
                type="email"
                className="form-control"
                id="email"
                name="email"
                value={form.email}
                onChange={handleInputChange}
                readOnly={loginType === "OAUTH2"}
            />
          </div>
          <div className="mb-3">
            <label htmlFor="password" className="form-label">비밀번호 변경</label>
            <input
                type="password"
                className="form-control"
                id="password"
                name="password"
                value={form.password}
                onChange={handleInputChange}
                readOnly={loginType === "OAUTH2"}
            />
          </div>
          <div className="mb-3">
            <label htmlFor="introduction" className="form-label">자기소개</label>
            <textarea
                className="form-control"
                id="introduction"
                name="introduction"
                rows="4"
                value={form.introduction}
                onChange={handleInputChange}
            />
          </div>
          <button type="submit" className="btn btn-primary">수정</button>
        </form>

        {/* 소셜 로그인 안내 */}
        {loginType === "OAUTH2" && (
            <div className="alert alert-warning mt-2">
              닉네임, 이메일, 비밀번호는 소셜 로그인 사용자는 수정할 수 없습니다.<br />
              자기소개는 수정 가능합니다.
            </div>
        )}

        {/* 회원 탈퇴 */}
        <form className="mt-4" onSubmit={handleDelete}>
          <button type="submit" className="btn btn-outline-danger">
            회원 탈퇴
          </button>
        </form>
      </section>
  );
}

export default MyPage;
