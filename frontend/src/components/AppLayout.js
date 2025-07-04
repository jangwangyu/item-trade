import AppRouter from './AppRouter';
import Header from './Header'; // 네비게이션(분리 가능)
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min.js';

function AppLayout() {
  return (
      <>
        <Header />
        <div className="container mt-4">
          <AppRouter />
        </div>
      </>
  );
}
export default AppLayout;
