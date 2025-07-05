import AppLayout from './components/AppLayout';
import { BrowserRouter } from 'react-router-dom';
import { AuthProvider } from './AuthContext';   // <- 추가

function App() {
  return (
      <AuthProvider>
        <BrowserRouter>
          <AppLayout />
        </BrowserRouter>
      </AuthProvider>
  );
}

export default App;
