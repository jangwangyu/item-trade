document.addEventListener('DOMContentLoaded', function () {
  const roomId = window.roomId;
  const senderId = window.senderId;

  console.log('roomId:', roomId);
  console.log('senderId:', senderId);

  const socket = new SockJS('http://localhost:8080/ws');
  const stompClient = Stomp.over(socket);

  // 1. 과거 메시지 불러오기
  fetch(`/chat/${roomId}/messages`)
  .then(response => response.json())
  .then(messages => {
    messages.forEach(msg => appendMessage(msg));
  });

  // 2. WebSocket 연결 및 구독
  stompClient.connect({}, function (frame) {
    console.log('소켓 연결 성공:', frame);

    stompClient.subscribe('/topic/chat/' + roomId, function (message) {
      console.log('메시지 수신:', message);
      const msg = JSON.parse(message.body);
      appendMessage(msg);
    });
  }, function (error) {
    console.error('소켓 연결 실패:', error);
  });

  // 3. 텍스트 메시지 전송
  document.getElementById("chatForm").addEventListener("submit", function (e) {
    e.preventDefault();
    const input = document.getElementById("chatInput");
    const message = input.value.trim();

    if (message !== '') {
      stompClient.send("/app/chat/" + roomId + "/send", {}, JSON.stringify({
        content: message,
        senderId: senderId,
        type: "text"
      }));
      input.value = '';
    }
  });

  // 4. 이미지 메시지 전송
  document.getElementById("imageForm").addEventListener("submit", async function (e) {
    e.preventDefault();

    const fileInput = document.getElementById("imageInput");
    const file = fileInput.files[0];
    if (!file) return;

    const formData = new FormData();
    formData.append("file", file);

    try {
      const response = await fetch("/api/images/upload", {
        method: "POST",
        body: formData
      });

      const imageUrl = await response.text();

      // WebSocket으로 전송
      stompClient.send("/app/chat/" + roomId + "/send", {}, JSON.stringify({
        content: imageUrl,
        senderId: senderId,
        type: "image"
      }));

      fileInput.value = ""; // 초기화
    } catch (err) {
      console.error("이미지 업로드 실패:", err);
    }
  });
});

function appendMessage(msg) {
  const isMine = Number(msg.senderId) === Number(window.senderId);
  const isImage = msg.type === 'image';

  const wrapper = document.createElement("div");
  wrapper.className = "d-flex " + (isMine ? "justify-content-end align-items-start" : "align-items-start") + " gap-2";

  const time = new Date(msg.createdAt).toLocaleTimeString('ko-KR', {
    hour: '2-digit',
    minute: '2-digit',
    hour12: true
  });

  const contentHtml = isImage
      ? `<img src="${msg.content}" class="img-fluid rounded" style="max-width: 200px;">`
      : `<span>${msg.content}</span>`;

  if (isMine) {
    wrapper.innerHTML = `
      <div>
        <div class="chat-bubble chat-right shadow-sm text-end">
          <strong class="d-block mb-1">나</strong>
          ${contentHtml}
        </div>
        <div class="text-muted small mt-1 text-end">
          ${time}
          <span class="ms-2" style="color:${msg.isRead ? 'green' : 'gray'};">
            ${msg.isRead ? '읽음' : '안읽음'}
          </span>
        </div>
      </div>
      <img src="/images/user2.png" class="profile-img" alt="나">
    `;
  } else {
    wrapper.innerHTML = `
      <img src="/images/user1.png" class="profile-img" alt="상대방">
      <div>
        <div class="chat-bubble chat-left shadow-sm">
          <strong class="d-block mb-1">${msg.senderNickname}</strong>
          ${contentHtml}
        </div>
        <div class="text-muted small mt-1 ms-1">${time}</div>
      </div>
    `;
  }

  document.getElementById("chatBox").appendChild(wrapper);
  document.getElementById("chatBox").scrollTop = document.getElementById("chatBox").scrollHeight;
}
