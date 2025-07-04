import { Link, NavLink } from 'react-router-dom';

function Header() {
  // TODO: 실제 인증 상태와 닉네임은 전역상태나 Context로
  const isAuthenticated = false;
  const nickname = "사용자";

  return (
      <nav className="navbar navbar-expand-lg navbar-light bg-light px-4">
        <Link className="navbar-brand" to="/">item-trade</Link>
        <ul className="navbar-nav me-auto">
          <li className="nav-item">
            <NavLink className="nav-link" to="/">홈</NavLink>
          </li>
          <li className="nav-item">
            <NavLink className="nav-link" to="/chat-list">채팅</NavLink>
          </li>
        </ul>
        <ul className="navbar-nav ms-auto">
          <li className="nav-item">
            <NavLink className="nav-link" to="/login">로그인</NavLink>
          </li>
        </ul>
      </nav>
  );
}
export default Header;
