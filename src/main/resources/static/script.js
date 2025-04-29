document.addEventListener('DOMContentLoaded', function () {
  const roomId = window.roomId;
  const senderId = window.senderId;

  console.log('roomId:', roomId);
  console.log('senderId:', senderId);

  const socket = new SockJS('http://localhost:8080/ws');
  const stompClient = Stomp.over(socket);

  // 🧹 여기 추가! 입장하자마자 과거 메시지 불러오기
  fetch(`/chat/${roomId}/messages`)
  .then(response => response.json())
  .then(messages => {
    messages.forEach(msg => {
      appendMessage(msg);
    });
  });

  stompClient.connect({}, function (frame) {
    stompClient.subscribe('/topic/chat/' + roomId, function (message) {
      const msg = JSON.parse(message.body);
      appendMessage(msg);
    });
  });

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

  document.getElementById("chatForm").addEventListener("submit", function (e) {
    e.preventDefault();
    const input = document.getElementById("chatInput");
    const message = input.value.trim();

    if (message !== '') {
      stompClient.send("/app/chat/" + roomId + "/send", {}, JSON.stringify({
        content: message,
        senderId: senderId
      }));
      input.value = '';
    }
  });
});
function appendMessage(msg) {
  const isMine = Number(msg.senderId) === Number(window.senderId);

  const wrapper = document.createElement("div");
  wrapper.className = "d-flex " + (isMine ? "justify-content-end align-items-start" : "align-items-start") + " gap-2";

  if (isMine) {
    wrapper.innerHTML = `
      <div>
        <div class="chat-bubble chat-right shadow-sm text-end">
          <strong class="d-block mb-1">나</strong>
          <span>${msg.content}</span>
        </div>
        <div class="text-muted small mt-1 text-end">
          지금
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
          <span>${msg.content}</span>
        </div>
        <div class="text-muted small mt-1 ms-1">
          지금
        </div>
      </div>
    `;
  }
  console.log('받은 메시지', msg);
  console.log('msg.senderId:', msg.senderId, 'window.senderId:', window.senderId);
  document.getElementById("chatBox").appendChild(wrapper);
  document.getElementById("chatBox").scrollTop = document.getElementById("chatBox").scrollHeight;
}



