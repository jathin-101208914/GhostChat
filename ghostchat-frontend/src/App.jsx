import { BrowserRouter, Routes, Route } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import RoomPage from "./pages/RoomPage";
import ChatPage from "./pages/ChatPage";
import RegisterPage from "./pages/RegisterPage";
import InvitePage from "./pages/InvitePage";

function App() {
  return (
    
      <BrowserRouter>
        <Routes>
          <Route path ="/" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage/>} />
          <Route path="/rooms" element={<RoomPage />} />
          <Route path="/chat/:roomCode" element={<ChatPage/>} />
          <Route path="/invite/:roomCode" element={<InvitePage/>} />
            
        </Routes>
      </BrowserRouter>
    
  );
  
}

export default App;