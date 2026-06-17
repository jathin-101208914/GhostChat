import { useParams } from "react-router-dom";
import { useState, useEffect } from "react";
import SockJS from "sockjs-client/dist/sockjs";
import { Client } from "@stomp/stompjs";

function ChatPage() {

    const { roomCode } = useParams();

    const sender = localStorage.getItem("username");

    const [message, setMessage] = useState("");
    const [messages, setMessages] = useState([]);
    
    const [client, setClient] = useState(null);

    useEffect(() => {
        const socket = new SockJS("http://localhost:8080/chat");

        const stompClient = new Client({
            webSocketFactory: () => socket,
            onConnect: () => {
                console.log("Connected");

                stompClient.subscribe(`/topic/room/${roomCode}`,
                    (msg) => {
                        const received = JSON.parse(msg.body);
                        setMessages(prev => [...prev, received]);
                    }
                );
            }
        });

        stompClient.activate();
        setClient(stompClient);

        return () => {
            stompClient.deactivate();
        };
    }, [roomCode]);

    const sendMessage = () =>{
        if(!message.trim()) return;

        client.publish({
            destination: "/app/chat.send",

            body: JSON.stringify({
                roomCode,
                sender,
                content: message
            })
        });
        setMessage("");
    };
    return (
        <div style={{padding: "20px"}}>
            <h1>GhostChat</h1>

            <h2>Room: {roomCode}</h2>

            <h3>User: {sender}</h3>

            <div
                style={{
                    border: "1px solid gray",
                    height: "300px",
                    overflowY: "auto",
                    padding: "10px",
                    marginBottom: "10px"
                }}
                >
                {messages.map((msg, index) => (
                    <p key={index}>
                    <b>{msg.sender}</b>: {msg.content}
                    </p>
                ))}
            </div>

            <input 
                value={message}
                onChange={(e) => setMessage(e.target.value)}
                placeholder="Type message..."
            />

            <button onClick={sendMessage}>Send</button>
        </div>
    );
}

export default ChatPage;