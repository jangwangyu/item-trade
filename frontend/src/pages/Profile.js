// src/pages/Profile.js
import React from "react";

// 샘플 프로필 데이터 (실제는 props, API 등으로 전달)
const profile = {
  id: 2,
  nickName: "성실한상대방",
  profileImageUrl: "https://i.pravatar.cc/120?img=2",
  createdAt: "2025-01-01",
  totalTrades: 17,
  introduction: "안녕하세요! 성실한 거래 약속드립니다 :)"
};

function Profile() {
  // 버튼 핸들러(실제 서버 연동 필요)
  const handleBlock = e => {
    e.preventDefault();
    if (window.confirm("정말 차단하시겠습니까?")) {
      alert("차단 처리되었습니다!");
      // fetch("/block", {method:"POST", ...})
    }
  };

  const handleReport = e => {
    e.preventDefault();
    if (window.confirm("정말 신고하시겠습니까?")) {
      alert("신고가 접수되었습니다!");
      // fetch("/report", {method:"POST", ...})
    }
  };

  return (
      <section className="container mt-5">
        <div className="card shadow rounded-4 p-4">
          <div className="row">
            {/* 프로필 이미지 */}
            <div className="col-md-3 text-center">
              <img
                  src={profile.profileImageUrl}
                  className="img-fluid rounded-circle mb-3"
                  alt="프로필 이미지"
                  style={{ width: 120, height: 120 }}
              />
            </div>

            {/* 프로필 정보 */}
            <div className="col-md-9">
              <h4>{profile.nickName}</h4>
              <p className="text-muted">
                가입일: <span>{profile.createdAt.replace(/-/g, ".")}</span>
              </p>

              {/* 거래 정보 */}
              <div className="mb-2">
              <span className="badge bg-primary">
                총 거래 <span>{profile.totalTrades}</span>회
              </span>
              </div>

              {/* 자기소개 */}
              <div className="mb-3">
                <strong>자기소개:</strong>
                <p>{profile.introduction}</p>
              </div>

              {/* 버튼 */}
              <div className="d-flex gap-2">
                <a
                    href={`/chat/room/create/${profile.id}`}
                    className="btn btn-outline-primary"
                >
                  채팅하기
                </a>
                <form onSubmit={handleBlock}>
                  <input type="hidden" name="blockedId" value={profile.id} />
                  <button className="btn btn-outline-danger" type="submit">차단</button>
                </form>
                <form onSubmit={handleReport}>
                  <input type="hidden" name="reportedId" value={profile.id} />
                  <button className="btn btn-outline-warning" type="submit">신고</button>
                </form>
              </div>
            </div>
          </div>
        </div>
      </section>
  );
}

export default Profile;
