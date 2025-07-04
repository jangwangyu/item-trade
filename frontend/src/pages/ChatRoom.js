// src/pages/ChatRoom.js
import React, { useState, useRef, useEffect } from "react";

// 샘플 메시지, 실제로는 서버/웹소켓에서 받아옴
const dummyMessages = [
  { id: 1, sender: "상대방", content: "안녕하세요!", type: "TEXT", mine: false },
  { id: 2, sender: "나", content: "안녕하세요, 네!", type: "TEXT", mine: true },
  // { id: 3, sender: "상대방", content: "https://picsum.photos/200", type: "IMAGE", mine: false },
];

function ChatRoom() {
  // 채팅방, 상대방, 거래상태 등 가상 데이터
  const [room, setRoom] = useState({
    id: 1,
    itemTitle: "아이템 제목",
    itemPrice: 50000,
    tradeStatus: "TRADE", // "TRADE", "COMPLETE", "CANCEL"
    isSeller: true,
    isBuyer: false,
    tradeSellerComplete: false,
    tradeBuyerComplete: false,
    blocked: false,
    opponentId: 2,
  });
  const [messages, setMessages] = useState(dummyMessages);
  const [input, setInput] = useState("");
  const [image, setImage] = useState(null);

  const chatBoxRef = useRef(null);

  // 채팅 스크롤 항상 맨 아래로
  useEffect(() => {
    if (chatBoxRef.current) {
      chatBoxRef.current.scrollTop = chatBoxRef.current.scrollHeight;
    }
  }, [messages]);

  // 텍스트 메시지 전송
  const handleSend = (e) => {
    e.preventDefault();
    if (input.trim() === "") return;
    setMessages(msgs => [
      ...msgs,
      { id: Date.now(), sender: "나", content: input, type: "TEXT", mine: true },
    ]);
    setInput("");
    // 실제로는 WebSocket/SockJS로 서버에 전송
  };

  // 이미지 메시지 전송
  const handleImageSend = (e) => {
    e.preventDefault();
    if (!image) return;
    // 이미지 URL 생성(예시)
    const imgUrl = URL.createObjectURL(image);
    setMessages(msgs => [
      ...msgs,
      { id: Date.now(), sender: "나", content: imgUrl, type: "IMAGE", mine: true },
    ]);
    setImage(null);
    // 실제로는 FormData/웹소켓 전송!
  };

  // 거래 완료/취소/차단/차단 해제 버튼 핸들러 (실제 API 연동 필요)
  const handleTradeComplete = () => alert("거래 완료!");
  const handleTradeCancel = () => alert("거래 취소!");
  const handleTradeReopen = () => alert("재거래 요청!");
  const handleBlock = () => setRoom(r => ({ ...r, blocked: true }));
  const handleUnblock = () => setRoom(r => ({ ...r, blocked: false }));

  return (
      <section style={{ backgroundColor: "#f8f9fa", padding: "2rem", borderRadius: "0.5rem" }}>
        {/* 채팅 헤더 */}
        <div className="d-flex justify-content-between align-items-center border-bottom pb-3 mb-3 flex-wrap gap-2">
          <h4 className="mb-0">{room.itemTitle}</h4>
          <span className="text-primary fw-semibold fs-5">{room.itemPrice.toLocaleString()}원</span>
          {/* 거래 완료 버튼 */}
          {(room.isSeller && !room.tradeSellerComplete || room.isBuyer && !room.tradeBuyerComplete) && (
              <button className="btn btn-success" onClick={handleTradeComplete}>거래 완료</button>
          )}
          {/* 거래 취소 버튼 */}
          {(room.isSeller || room.isBuyer) && (
              <button className="btn btn-outline-danger" onClick={handleTradeCancel}>거래 취소</button>
          )}
          {/* 재거래 버튼 */}
          {room.tradeStatus === "CANCEL" && (room.isSeller || room.isBuyer) && (
              <button className="btn btn-warning" onClick={handleTradeReopen}>재거래 요청</button>
          )}
          {/* 차단/차단 해제 버튼 */}
          {!room.blocked ? (
              <button className="btn btn-outline-danger btn-sm" onClick={handleBlock}>차단</button>
          ) : (
              <button className="btn btn-outline-secondary btn-sm" onClick={handleUnblock}>차단 해제</button>
          )}
        </div>

        {/* 채팅 내용 */}
        <div
            className="chat-box d-flex flex-column gap-3 mb-3"
            id="chatBox"
            ref={chatBoxRef}
            style={{
              maxHeight: 450,
              overflowY: "auto",
              background: "#fff",
              borderRadius: "0.5rem",
              padding: "1rem"
            }}
        >
          {messages.map(msg => (
              <div
                  key={msg.id}
                  className={`d-flex ${msg.mine ? "justify-content-end" : "justify-content-start"}`}
              >
                {!msg.mine && (
                    <img
                        src="https://i.pravatar.cc/36?img=2"
                        className="profile-img me-2"
                        alt="상대방"
                        style={{
                          width: 36, height: 36, borderRadius: "50%", objectFit: "cover"
                        }}
                    />
                )}
                <div
                    className={`chat-bubble ${msg.mine ? "chat-right" : "chat-left"}`}
                    style={{
                      maxWidth: "75%",
                      wordBreak: "break-all"
                    }}
                >
                  {msg.type === "TEXT" ? (
                      msg.content
                  ) : (
                      <img src={msg.content} alt="첨부 이미지" style={{ maxWidth: 180, borderRadius: 10 }} />
                  )}
                </div>
              </div>
          ))}
        </div>

        {/* 텍스트 메시지 입력 */}
        <form className="d-flex gap-2 border-top pt-3" onSubmit={handleSend}>
          <input
              type="text"
              className="form-control"
              placeholder="메시지를 입력하세요"
              value={input}
              onChange={e => setInput(e.target.value)}
          />
          <button type="submit" className="btn btn-primary">전송</button>
        </form>

        {/* 이미지 메시지 입력 */}
        <form className="d-flex gap-2 mt-2" onSubmit={handleImageSend}>
          <input
              type="file"
              accept="image/*"
              className="form-control"
              onChange={e => setImage(e.target.files[0])}
          />
          <button type="submit" className="btn btn-secondary">이미지 전송</button>
        </form>
      </section>
  );
}

export default ChatRoom;
