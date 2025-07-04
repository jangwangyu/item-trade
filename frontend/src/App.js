// src/App.js
import AppLayout from './components/AppLayout';
import { BrowserRouter } from 'react-router-dom';

function App() {
  return (
      <BrowserRouter>
        <AppLayout />
      </BrowserRouter>
  );
}

export default App;
