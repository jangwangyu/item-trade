import { Routes, Route } from 'react-router-dom';
import Home from '../pages';
import ChatList from '../pages/ChatList';
import MyPage from '../pages/MyPage';
import Login from '../pages/Login';
import Register from '../pages/Register';
import ChatRoom from "../pages/ChatRoom";
import PostDetail from "../pages/PostDetail";
import PostEdit from "../pages/PostEdit";
import PostWrite from "../pages/PostWrite";
import Profile from "../pages/Profile";

function AppRouter() {
  return (
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/ChatRoom" element={<ChatRoom />} />
        <Route path="/chat-list" element={<ChatList />} />
        <Route path="/PostDetail" element={<PostDetail />} />
        <Route path="/PostWrite" element={<PostWrite />} />
        <Route path="/PostEdit" element={<PostEdit />} />
        <Route path="/mypage" element={<MyPage />} />
        <Route path="/Profile" element={<Profile />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
      </Routes>
  );
}
export default AppRouter;
