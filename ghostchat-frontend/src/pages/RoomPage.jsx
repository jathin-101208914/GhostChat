import { useState } from "react";
import { Navigate, useNavigate } from "react-router-dom";

function RoomPage() {

    const [roomCode, setRoomCode] = useState("");
    const navigate = useNavigate();

    const joinRoom = () => {
        navigate(`/chat/${roomCode}`);
    };

    return (
        <div>
            <h1>Join Room</h1>

            <input 
                placeholder="Room Code"
                value={roomCode}
                onChange={(e) => setRoomCode(e.target.value)}/>
            
            <button onClick={joinRoom}>Join</button>
        </div>
    );
}

export default RoomPage;