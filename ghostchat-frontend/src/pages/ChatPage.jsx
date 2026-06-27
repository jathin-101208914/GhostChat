import { useParams, useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";
import SockJS from "sockjs-client/dist/sockjs";
import { Client } from "@stomp/stompjs";
import { authenticatedFetch } from "../api/api";
import { IoSend, IoMenu, IoClose } from "react-icons/io5";

import "../styles/ChatPage.css";
import { API_URL } from "../config/api";

function ChatPage() {

    const { roomCode } = useParams();

    const sender = localStorage.getItem("username");
    const email = localStorage.getItem("email");

    const [message, setMessage] = useState("");
    const [messages, setMessages] = useState([]);
    
    const [client, setClient] = useState(null);

    const navigate = useNavigate();

    const [onlineUsers, setOnlineUsers] = useState([]);

    const [typingUser, setTypingUser] = useState("");

    // const [anonymousName, setAnonymousName] = useState("");

    // const [anonymousMode, setAnonymousMode] = useState(false);

    const [isAnonymousRoom, setIsAnonymousRoom] = useState(false);

    const [anonymousName, setAnonymousName] = useState("");

    const [sidebarOpen, setSidebarOpen] = useState(false);

    const leaveRoom = async () => {

        console.log("LEAVE BUTTON CLICKED");

        if(client && client.connected){
            client.publish({
                    destination: "/app/chat.leave",
                    body: JSON.stringify({
                        roomCode,
                        content: `🔴 ${isAnonymousRoom ? anonymousName : sender} left the room`,
                        type: "SYSTEM"
                    })
            });
        }

        try{
            await authenticatedFetch("/api/rooms/leave",{
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    roomCode,
                    username: sender
                })
            });
        }catch(error){
            console.error(error);

            if(error.message === "Unauthorized"){
                return;
            }

        }
    };

    useEffect(() => {
        const token = localStorage.getItem("token");
        
        if(!token){
            navigate("/");
            return
        }

        const socket = new SockJS(`${API_URL}/chat`);

        const stompClient = new Client({
            webSocketFactory: () => socket,
         
            onConnect: async () => {
                console.log("Connected");

                stompClient.subscribe(`/topic/room/${roomCode}`,
                    (msg) => {
                        const received = {
                            ...JSON.parse(msg.body),
                            id: Date.now() + Math.random(),
                            timestamp: Date.now()
                        };

                        setMessages(prev => [...prev, received]);

                        const expiryTime = received.type === "SYSTEM" ? 2000 : 5000;

                        setTimeout(() => {
                            setMessages(prev => prev.filter(m => m.id !== received.id));
                        }, expiryTime);
                    }
                );

                stompClient.subscribe(`/topic/typing/${roomCode}`,
                    (msg) => {
                        const data = JSON.parse(msg.body);
                        if(data.realUsername !== sender){
                            setTypingUser(data.username);

                            setTimeout(() => {
                                setTypingUser("");
                            }, 1000);
                        }
                    }
                );

                stompClient.subscribe(
                    `/topic/presence/${roomCode}`,
                    (msg) => {
                        const data = JSON.parse(msg.body);

                        setOnlineUsers(data.users);
                    }
                );

                stompClient.subscribe(`/topic/logout/${email}`, () => {
                    alert("This account has been logged in on another device.");
                    localStorage.clear();
                    navigate("/");
                });

                const modeResponse = await authenticatedFetch(`/api/rooms/${roomCode}/anonymous`);
                const mode = await modeResponse.json();
                let displayName = sender;

                if (mode){
                    const aliaResponse = await authenticatedFetch(`/api/rooms/${roomCode}/alias/${sender}`);
                    displayName = await aliaResponse.text();
                }

                await authenticatedFetch("/api/rooms/join", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({
                        roomCode,
                        username: sender
                    })
                });

                const params = new URLSearchParams(window.location.search);
                const inviteToken = params.get("token");

                if(inviteToken){
                    await authenticatedFetch(`/api/rooms/consume/${inviteToken}`,
                        {
                            method: "POST",
                            headers: {
                                "Content-Type": "application/json"
                            }
                        }
                    );
                }

                stompClient.publish({
                    destination: "/app/chat.join",
                    body: JSON.stringify({
                        roomCode,
                        username: sender,
                        content: `🟢 ${displayName} joined the room`,
                        type: "SYSTEM"
                    })
                });  
                
            }
        });


        const fetchRoomMode = async () => {
            const response = await authenticatedFetch(`/api/rooms/${roomCode}/anonymous`);

            const mode = await response.json();

            setIsAnonymousRoom(mode);

            console.log("Anonymous Room:", mode);

            const fetchAlias = async () => {
            const response = await authenticatedFetch(`/api/rooms/${roomCode}/alias/${sender}`);

            const alias = await response.text();
            setAnonymousName(alias);
            };

            if(mode){
                fetchAlias();
            }
        };

        fetchRoomMode();

        // const fetchAlias = async () => {
        //     const response = await fetch(`http://localhost:8080/api/rooms/${roomCode}/alias/${sender}`);

        //     const alias = await response.text();

        //     setAnonymousName(alias);
        // };
        // if (anonymousMode){
        //     fetchAlias();
        // }

        stompClient.activate();
        setClient(stompClient);

        return () => {
            stompClient.deactivate();
        };
        
    }, [roomCode, navigate]);

    useEffect(() => {
            const loadMessages = async () => {
                const response = await authenticatedFetch(`/api/rooms/${roomCode}/messages`);

                const data = await response.json();

                const messagesWithIds = data.map(msg => ({
                    ...msg,
                    id: Date.now() + Math.random()
                }));
                setMessages(messagesWithIds);
            };

            loadMessages();
        }, [roomCode]);

    // useEffect(() => {
    //     const fetchUsers = async () => {

    //         try{
    //             const endpoint = isAnonymousRoom ? `http://localhost:8080/api/rooms/${roomCode}/anonymous-users` : `http://localhost:8080/api/rooms/${roomCode}/users`;
    //             const response = await fetch(endpoint);
    //             const users = await response.json();

    //             setOnlineUsers(users);
    //         }catch(error){
    //             console.error(error);
    //         }
            
    //     };

    //     fetchUsers();

    //     const interval = setInterval(fetchUsers, 2000);

    //     return () => clearInterval(interval);
    // }, [roomCode, isAnonymousRoom]);
    useEffect(() => {
        const fetchUsers = async () => {
            const endpoint = isAnonymousRoom
                    ? `/api/rooms/${roomCode}/anonymous-users`
                    : `/api/rooms/${roomCode}/users`;

                const response = await authenticatedFetch(endpoint);
                const users = await response.json();

                setOnlineUsers(users);
            };

            fetchUsers();
    }, [roomCode, isAnonymousRoom]);

    const sendMessage = () =>{
        if(!message.trim()) return;

        if(!client || !client.connected){
        return;
        }

        client.publish({
            destination: "/app/chat.send",

            body: JSON.stringify({
                roomCode,
                sender: sender,
                content: message
            })
        });
        setMessage("");
    };

    return (
        <div className="chat-container">

            <div className="chat-header">
                <button
                    className="menu-btn"
                    onClick={() => setSidebarOpen(true)}
                >
                    <IoMenu/>
                </button>

                <h1>GhostChat</h1>

                <div>
                    <p>Room: {roomCode}</p>
                    <p>User: {isAnonymousRoom ? anonymousName : sender}</p>
                </div>
            </div>
             
            {sidebarOpen && (
                    <div
                        className="sidebar-overlay"
                        onClick={() => setSidebarOpen(false)}
                    />
            )}

            <div className="chat-layout">
            
                <div className={`sidebar ${sidebarOpen ? "open" : ""}`}>
                    <button className="close-sidebar" onClick={() => setSidebarOpen(false)}> <IoClose/> </button>

                    <div className="online-users">
                        <h3>Online Users</h3>

                        {onlineUsers.map((user) => (
                            
                            <p key={user}>🟢 {user}</p>
                        ))}
                    </div>

                    <div className="sidebar-actions">
                        <button className="invite-btn" onClick={async () => {
                            const response = await authenticatedFetch(`/api/rooms/${roomCode}/invite`);

                            const token = await response.text()

                            const inviteLink = `${window.location.origin}/invite/${roomCode}?token=${token}`;

                            navigator.clipboard.writeText(inviteLink);

                            alert("Invite link copied!");
                        }}>Copy Invite Link</button>

                        <button className="leave-btn" onClick={async () => {
                            await leaveRoom();
                            navigate("/rooms");
                        }}>Leave Room</button>

                    </div>
                    
                </div>

                <div className="chat-main">

                    <div className="messages">
                        {messages.map((msg, index) => (
                            msg.type === "SYSTEM" ? (
                                <p className="system-message" key = {index}>
                                    {msg.content}
                                </p>
                            ) : (
                            <div key={index} className={
                                msg.sender === sender ? "message own-message" : "message other-message"
                            }
                            >
                                <b>{msg.sender}</b>
                                <p>{msg.content}</p>
                            </div>
                            )
                        ))}
                    </div>

                    {typingUser && (
                        <div className="typing-indicator">
                            <span className="typing-name">{typingUser}</span>
                            <span className="typing-text">is typing</span>

                            <span className="typing-dots">
                                <span></span>
                                <span></span>
                                <span></span>
                            </span>
                        </div>
                        )}
                    
                    <div className="chat-input">
                        <input 
                            value={message}
                            onChange={(e) => {setMessage(e.target.value);
                                if (client && client.connected){
                                    client.publish({
                                        destination:"/app/chat.typing",
                                        body: JSON.stringify({
                                            roomCode,
                                            username:sender,
                                            realUsername: sender,
                                            anonymousMode: isAnonymousRoom
                                        })
                                    });
                                }
                            }}
                            placeholder="Type message..."
                        />

                        <button className="send-btn" onClick={sendMessage}> <IoSend size={20}/> </button>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default ChatPage;