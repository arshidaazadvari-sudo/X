# Twitter/X Project

# CONTRIBUTERS: Arshida Azadvari, Kimia Moosavi, Zahra Mirvakili

## Kimia Moosavi

### Client-Server Communication

did the connection between the client and the server.

The app uses a normal client-server structure. The server opens a port with `ServerSocket` and waits for clients. Every time a client connects, the server starts a new thread (`ClientHandler`) for that client.

data is sent as json over the socket. each message has a `type` field (like `LOGIN`, `REGISTER`, `CREATE_TWEET`, `FOLLOW`…) so the server knows what the client wants.

Basic flow:
1. Client builds a request and turns it into JSON with Jackson.
2. Client sends it through the socket.
3. Server reads it, checks the type, and calls the right method.
4. Server sends a JSON response back.
5. On the client, a background thread (`ResponseListener`) reads the response and updates the UI.

the token system was also overlooked by me. after login, the server gives the client a token. the client has to send this token with the next requests so the server knows who is making the request.

### OOP Parts I Used

- I tried to keep classes focused on one job (`ClientHandler` only deals with the socket and routing, other handlers deal with their own features).
- Used composition instead of putting everything in one big class.
- Wrapped request data in objects instead of passing lots of separate values.

### Problems I Ran Into

- **Port already in use**: Happened when I forgot to close the previous server. Had to find the process and kill it.
- **NullPointerException on client**: The client was reading the response too early, before the server had answered. Fixed it by waiting properly in `ResponseListener`.
- **JSON type check failing**: Using `.toString()` on JsonNode added extra quotes. Changed it to `.asText()`.
- **Database connection error**: Wrong username/password in the connection settings.
- **Git issues**: Had some trouble with pull, merge conflicts, and keeping my branch updated with the rest of the team.

---

## AI Usage

I used LLM a few times during the project, mainly for:

- Understanding how to convert Java objects to JSON and back with Jackson
- Getting help with Git (commits, pulls, conflicts, upstream)
- Debugging a couple of errors that showed up while testing
---