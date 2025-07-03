import logo from './logo.svg';
import './App.css';

function App() {
  return (
    <div className="App">
      <header className="App-header">
        <img src={logo} className="App-logo" alt="logo" />
        <p>
          Edit <code>/App.js</code> and save to reload.
        </p>
        <div className="min-h-screen flex flex-col items-center justify-center bg-blue-100">
          <h1 className="text-3xl font-bold text-blue-600">Tailwind CSS 성공못함asasdasdd</h1>
          <button className="mt-4 px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-700">
            버튼 예시
          </button>
        </div>
        <a
          className="App-link"
          href="https://reactjs.org"
          target="_blank"
          rel="noopener noreferrer"
        >
          Learn React
        </a>
      </header>
    </div>
  );
}

export default App;
