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

## Arshida Azadvari

### Handling client-server Communication (client side)

used a separated thread named ResponseListener to manage the receiving messages from server and react based on it.

The protocol of messages is in Json; We decided to design the pattern of Json objects this way:

There are two main keys (fields), one is `type` and the other one is `payload` or `message` based on need.

But the major part of front-end is about designing a friendly UI. In order to do that, I used javafx, css and java controllers.

In a nutshell, the architecture of fxml files usage is like this:

1. There are two scene changes; first one is related to the authentication and second one is the main scene.

2. The user may user sidebar buttons to open windows such as `post window` or to go to different pages such as `profile` page.

### Object-Oriented Design

The kay classes of `client` folder are controllers connected to `fxml` files, for they handle a variety of user's requests;

such as posting and deleting a tweet, making social connections through following other accounts etc.

There are classes related to networking and some data formatter classes too.

### AI Usage

The three main reasons why I used an AI assistant:

- Learning parts of the UI concepts that I was almost new to, such as some tags in javafx, using css attributes,
the equivalent syntax in java and some styling tools such as `FontAwesome`.

- Helping with debugging especially for the `nullpointer` exception.

- Gaining data about the design structure of X.

Here are some of those prompts along with the AI response (Chatgpt):

https://chatgpt.com/share/6a6d82be-9524-83ed-9d48-f6b26d94420d?ogimg=plain

https://chatgpt.com/share/6a6d82ec-217c-83ed-8f50-17754bcf3ea7?ogimg=plain

https://chatgpt.com/share/6a6d8371-17b0-83ed-bb6b-a1651cc62f8f?ogimg=plain

https://chatgpt.com/share/6a6d83cf-7c84-83ed-b850-c24c6aae0853?ogimg=plain