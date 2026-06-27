import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/RoomPage.css";
import { authenticatedFetch } from "../api/api";

function RoomPage() {

    const [roomCode, setRoomCode] = useState("");
    const navigate = useNavigate();
    const [anonymousMode, setAnonymousMode] = useState(false);

    const createRoom = async () => {
        try{
            const response = await authenticatedFetch("/api/rooms/create",
                {
                    method: "POST",
                    headers:{
                            "Content-Type": "application/json"
                        },
                    body: JSON.stringify({
                        anonymousMode
                    })
                }
            );

            const roomCode = await response.text();

            const username = localStorage.getItem("username");

            await authenticatedFetch("/api/rooms/join",
                {
                    method: "POST",
                    headers: {
                            "Content-Type": "application/json"
                        },
                    body: JSON.stringify({
                        roomCode,
                        username
                    })
                }
            );

            navigate(`/chat/${roomCode}`);
        } catch(error){
            console.error(error);

            if(error.message === "Unauthorized"){
                return;
            }

            alert("Failed to create room");
        }
    };

    const joinRoom = async () => {
        if(!roomCode.trim()){
            alert("Please enter room code");
            return;
        }

       try{
            const username = localStorage.getItem("username");
            const response = await authenticatedFetch("/api/rooms/join",
                {
                    method: "POST",
                    headers: {
                            "Content-Type": "application/json"
                        },
                    body: JSON.stringify({
                        roomCode,
                        username
                    })
                }
            );


            const result = await response.text();
            console.log(result);
            if(result === "Room not found"){
                alert(result);
                return;
            }

            navigate(`/chat/${roomCode}`);
       }catch(error){
            console.error(error);

            if(error.message === "Unauthorized"){
                return;
            }
            
            alert("Failed to join room");    
        }
    };

    useEffect(() => {
        const token = localStorage.getItem("token");

        if(!token){
            navigate("/");
        }
    }, [navigate]);

    return (
        <div className="room-page">
            <div className="room-card">

                <h1>GhostChat</h1>

                <p className="subtitle">
                    Ephemeral • Private • Anonymous Messaging
                </p>

                <div className="divider"></div>

                <h2 className="section-title">Join Room</h2>

                <input 
                    type="text"
                    placeholder="Room Code"
                    value={roomCode}
                    onChange={(e) => setRoomCode(e.target.value)}
                />

                <button className="join-btn" onClick={joinRoom}>Join Room</button>

                <div className="divider"></div>

                <h2 className="section-title">Create Room</h2>

                <label className="checkbox">

                    <input 
                        type="checkbox" 
                        checked={anonymousMode}
                        onChange={(e) => setAnonymousMode(e.target.checked)}
                    />
                    Anonymous Mode<span className="anonymous-info">(Hide usernames for everyone in this room.)</span>
                </label>
                
                <button className="create-btn" onClick={createRoom}>Create Room</button>
                

                <button className="logout-btn" onClick={() => {
                    localStorage.clear();
                    navigate("/");
                }}>Logout</button>
            </div>
        </div>
    );
}

export default RoomPage;