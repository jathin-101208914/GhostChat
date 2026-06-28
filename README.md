# 👻 GhostChat

A secure, real-time ephemeral chat application where messages automatically disappear after a configurable time. Built with **Spring Boot**, **React**, **WebSockets**, **JWT Authentication**, **Redis**, and **MySQL**.

## 🚀 Live Demo

**Frontend:** https://ghost-chat-indol.vercel.app

**Backend API:** https://ghostchat-f3sq.onrender.com

---

## 💡 About

GhostChat is a full-stack real-time messaging application inspired by ephemeral messaging platforms. It demonstrates secure authentication, WebSocket communication, Redis integration, and automatic message expiration using a modern Spring Boot + React architecture.

The project was built to showcase production-ready backend development concepts including JWT authentication, REST APIs, WebSockets, Redis caching, MySQL persistence, and cloud deployment.

## 🎮 How to Use

1. Create an account or log in.
2. Create a private room or join using an invite link.
3. (Optional) Enable Anonymous Mode before creating a room.
4. Share the invite link with another user.
5. Exchange messages in real time.
6. Messages automatically disappear after the configured expiration time.
7. Leave the room when you're finished.

## ✨ Features

- 🔐 JWT Authentication
- 💬 Real-time messaging with WebSockets
- ⏳ Automatic message expiration
- 👤 Anonymous room mode
- 🔗 One-time invite links
- 🟢 Online user presence
- 🚪 Private chat rooms
- 📱 Responsive UI
- ⚡ Redis integration
- 🗄️ MySQL database

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|------------|
| Frontend | React, Vite, React Router, CSS |
| Backend | Spring Boot, Spring Security |
| Authentication | JWT |
| Real-time Communication | WebSocket (STOMP + SockJS) |
| Database | MySQL (TiDB Cloud) |
| Cache | Redis (Upstash) |
| Deployment | Vercel, Render |

---

## 🏗️ Architecture

```text
                React (Vite)
                     │
      REST APIs + WebSocket (STOMP)
                     │
               Spring Boot
              /            \
         MySQL          Redis
```

---

# 📸 Screenshots

## Login

![Login](screenshots/Login.png)

---

## Register

![Register](screenshots/Register.png)

---

## Room Dashboard

![Dashboard](screenshots/Dashboard.png)

---

## Chat Room

![Chat](screenshots/Chat.png)

---

## Anonymous Mode

![Anonymous](screenshots/Anonymous.png)

---

## Chatting

![Chatting](screenshots/Chatting.png)
## ⚙️ Running Locally

### Backend

```bash
cd ghostchat
./mvnw spring-boot:run
```

### Frontend

```bash
cd ghostchat-frontend
npm install
npm run dev
```

Frontend runs on:

```
http://localhost:5173
```

Backend runs on:

```
http://localhost:8080
```

---

## 🎯 Future Improvements

- 🔒 End-to-end encryption
- 📁 File sharing
- 🎤 Voice messages
- ✅ Read receipts
- 🔔 Push notifications
- 👥 Group chats
- 😊 Emoji reactions
- 📸 Image sharing
