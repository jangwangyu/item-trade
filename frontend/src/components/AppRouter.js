import { Routes, Route } from 'react-router-dom';
import Home from '../pages';
import ChatList from '../pages/ChatList';
import MyPage from '../pages/MyPage';
import Login from '../pages/Login';
import Register from '../pages/Register';

function AppRouter() {
  return (
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/chat-list" element={<ChatList />} />
        <Route path="/mypage" element={<MyPage />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
      </Routes>
  );
}
export default AppRouter;
