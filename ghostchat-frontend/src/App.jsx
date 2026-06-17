import { BrowserRouter, Routes, Route } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import RoomPage from "./pages/RoomPage";
import ChatPage from "./pages/ChatPage";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path ="/" element={<LoginPage />} />
        <Route path="/rooms" element={<RoomPage />} />
        <Route path="/chat/:roomCode" element={<ChatPage/>} />  
      </Routes>
    </BrowserRouter>
  );
  
}

export default App;