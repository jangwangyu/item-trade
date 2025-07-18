// === notification.js ===
console.log('window.currentUserEmail:', window.currentUserEmail);
window.addEventListener('DOMContentLoaded', function () {
  console.log('DOMContentLoaded!');
});
if (window.currentUserEmail) {
  window.addEventListener('DOMContentLoaded', function () {
    fetch('/api/notifications')
    .then(res => res.json())
    .then(list => {
      console.log('알림 데이터:', list);
      if (Array.isArray(list)) {
        list.forEach(notif => addNotificationToList(notif));
      }
    })
    .catch(err => {
      console.error('알림 데이터 불러오기 실패', err);
    });
  });
}

// 1. 알림용 WebSocket 연결 및 구독
const sockJsProtocol = window.location.protocol === 'https:' ? 'https:' : 'http:';
const wsHost = window.location.host;
const wsPath = '/ws';
const notificationSocket = new SockJS(`${sockJsProtocol}//${wsHost}${wsPath}`);
const notificationStompClient = Stomp.over(notificationSocket);

// 전역 등록 필요시
window.notificationStompClient = notificationStompClient;



notificationStompClient.connect({}, function () {
  notificationStompClient.subscribe('/user/queue/notifications', function (message) {
    console.log("[알림 도착]", message.body);
    alert('알림: ' + message.body);
    const notif = JSON.parse(message.body);
    showToast(notif.message);
    addNotificationToList(notif);
  });
  console.log("Current User Email (for subscribe):", window.currentUserEmail);
});

// 2. Toast 함수
function showToast(message) {
  const toastId = 'toast-' + Date.now();
  const toastHtml = `
    <div id="${toastId}" class="toast align-items-center text-bg-primary border-0 show" role="alert" aria-live="assertive" aria-atomic="true" style="min-width:200px; margin-bottom:10px;">
      <div class="d-flex">
        <div class="toast-body">${message}</div>
        <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close" onclick="document.getElementById('${toastId}').remove()"></button>
      </div>
    </div>
  `;
  const container = document.getElementById('toast-container');
  container.insertAdjacentHTML('beforeend', toastHtml);

  setTimeout(() => {
    const t = document.getElementById(toastId);
    if (t) t.remove();
  }, 3000);
}

// 3. 알림 리스트에 추가 함수
function addNotificationToList(notif) {
  const list = document.getElementById('notification-list');
  if (!list) return;

  const item = document.createElement('li');
  item.className = 'list-group-item d-flex justify-content-between align-items-center' +
      (notif.isRead ? ' text-secondary' : '');

  // 알림 내용 (클릭시 isRead 처리)
  const messageSpan = document.createElement('span');
  messageSpan.textContent = notif.message;
  messageSpan.style.cursor = 'pointer';
  messageSpan.onclick = function () {
    // 1. 바로 읽음 표시
    item.classList.add('text-secondary');
    // 2. 서버에 PATCH 요청
    fetch(`/api/notifications/${notif.id}/read`, { method: 'PATCH' });
    // 3. 해당 url로 이동
    window.location.href = notif.url;
  };

  // 확인 버튼도 동일하게 처리
  const confirmBtn = document.createElement('button');
  confirmBtn.className = 'btn btn-sm btn-primary ms-2';
  confirmBtn.textContent = '확인';
  confirmBtn.onclick = function () {
    item.classList.add('text-secondary');
    fetch(`/api/notifications/${notif.id}/read`, { method: 'PATCH' });
    window.location.href = notif.url;
  };

  // X버튼(삭제)
  const closeBtn = document.createElement('button');
  closeBtn.className = 'btn-close';
  closeBtn.type = 'button';
  closeBtn.onclick = function () {
    item.remove();
  };

  const timeSmall = document.createElement('small');
  timeSmall.className = 'text-muted ms-2';
  timeSmall.textContent = new Date(notif.createdAt).toLocaleTimeString();

  item.appendChild(messageSpan);
  item.appendChild(timeSmall);
  item.appendChild(confirmBtn);
  item.appendChild(closeBtn);
  list.insertBefore(item, list.firstChild);

  // 최근 5개 유지
  while (list.children.length > 5) list.removeChild(list.lastChild);

}
