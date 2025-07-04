// src/pages/ChatList.js
import React, { useState } from "react";
import {Link} from "react-router-dom";

const dummyChatRooms = [
  {
    id: 1,
    sellerProfileImageUrl: "https://i.pravatar.cc/48?img=1",
    itemTitle: "닌텐도 스위치 팝니다",
    lastMessage: "관심 있으시면 연락주세요!",
    itemPrice: 280000,
    tradeStatus: "TRADE", // "TRADE", "COMPLETE", "CANCEL"
    unreadCount: 1,
  },
  {
    id: 2,
    sellerProfileImageUrl: "https://i.pravatar.cc/48?img=2",
    itemTitle: "플스5 미개봉",
    lastMessage: "거래 완료됐어요.",
    itemPrice: 570000,
    tradeStatus: "COMPLETE",
    unreadCount: 0,
  },
  // ...더 추가
];

function ChatList() {
  const [chatRooms, setChatRooms] = useState(dummyChatRooms);

  // 삭제 이벤트
  const handleDelete = (id) => {
    if (window.confirm("정말 이 채팅방을 삭제하시겠습니까?")) {
      setChatRooms(chatRooms.filter(room => room.id !== id));
      // 실제 API 호출 필요
    }
  };

  return (
      <section style={{ backgroundColor: "#f8f9fa", padding: "2rem", borderRadius: "0.5rem" }}>
        <h3 className="mb-4">1:1 채팅 목록</h3>
        <div className="list-group">
          {chatRooms.map(chat => (
              <Link
                  key={chat.id}
                  to={`/chat/${chat.id}`}
                  className="list-group-item list-group-item-action d-flex justify-content-between align-items-center chat-item py-3"
                  style={{ textDecoration: "none" }}
              >
                {/* 왼쪽: 프로필 + 내용 */}
                <div className="d-flex align-items-center gap-3">
                  <img src={chat.sellerProfileImageUrl} className="profile-img" alt="프로필" />
                  <div>
                    <div className="fw-semibold">{chat.itemTitle}</div>
                    <div className="text-muted ellipsis" style={{
                      whiteSpace: "nowrap",
                      overflow: "hidden",
                      textOverflow: "ellipsis",
                      maxWidth: 200
                    }}>
                      {chat.lastMessage}
                    </div>
                  </div>
                </div>
                {/* 오른쪽: 가격 + 상태 + 삭제 */}
                <div className="text-end">
                  <div className="fw-bold text-primary">
                    {chat.itemPrice.toLocaleString()}원
                  </div>
                  {/* 거래 상태 뱃지 */}
                  <div className="mt-1">
                    {chat.tradeStatus === "TRADE" && (
                        <span className="badge bg-secondary">거래가능</span>
                    )}
                    {chat.tradeStatus === "COMPLETE" && (
                        <span className="badge bg-success">거래완료</span>
                    )}
                    {chat.tradeStatus === "CANCEL" && (
                        <span className="badge bg-danger">거래취소</span>
                    )}
                  </div>
                  {/* 안읽은 메시지 뱃지 */}
                  {chat.unreadCount > 0 && (
                      <span className="badge bg-danger mt-1">{chat.unreadCount}</span>
                  )}
                  {/* 삭제 버튼 */}
                  <button
                      type="button"
                      className="btn btn-sm btn-outline-danger mt-1"
                      onClick={e => { e.preventDefault(); handleDelete(chat.id); }}
                  >
                    삭제
                  </button>
                </div>
              </Link>
          ))}
        </div>
      </section>
  );
}

export default ChatList;
