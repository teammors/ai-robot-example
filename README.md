# Teammors Readme

Secure, intelligent, private, and convenient – ​​these are the pursuits of Teammors.

Teammors is not just a professional IM tool, but also an AI knowledge base user terminal.

Teammors currently supports the following platforms: **macOS, Android, iOS, Windows, Linux**.

---

## Teammors Features

### 1. Advanced Security and Privacy Protection

- **End-to-End Encryption (E2EE):** Enabled by default in "Chat" (including group chats), messages are only visible to the two parties involved and are not stored on the server.

- **Self-Destruct Messages:** Self-destruct countdowns can be set for both private and group chats.

- **Anonymity:** Registration requires only a string.

- **No Data Mining:** No data collection is performed.

### 2. Customizable Intelligent AI Bots

- **Third-Party Developers:** Users can open up to multiple knowledge base bots, each with its own independent channel, handling message push and receiving.

- **50,000+ Robot Clusters:** Each robot channel supports over 50,000 subscribers.

- **Super Robot Channels:** Stable and efficient robot channels, providing a robot SDK for various RAGs to integrate with, offering RAGs an efficient communication channel with ordinary users.

### 3. Rich Private and Group Chat Features
Teammors itself is also an efficient and easy-to-use communication tool:

- Supports multimedia message types such as emoticons, image messages, voice messages, and video messages.

- Supports free voice and video call functions.

- Supports large file uploads.

- Supports message editing functions.

- Supports message reply functions.

- Supports mutual message clearing functions.

- Supports setting message clearing time functions.

- Supports marking important conversations.

### 4. Multimedia Support

- Supports free Voice Calls and Video Calls.

- Supports multimedia message types such as emoticons, image messages, voice messages, and video messages.

- Supports large file uploads, up to 2GB. ### 5. Cross-Platform Support

- Supports multi-device login; the same account can be logged in simultaneously on multiple devices, such as Mac, Windows, Linux, and mobile.

- Supports multiple languages; users can switch between different languages ​​at any time. Currently, over 20 languages ​​are supported, with more being added continuously.

- Supports multi-platform clients: macOS, Android, iOS, Windows, and Linux.

---

## Teammors Business Structure Diagram

[Insert business structure diagram here]

---

## The Process of Building a Teammors Intelligent Service

1. Download the corresponding client from https://www.teammors.top/

2. Register an account

3. Create a bot (with image)

4. Configure basic bot information (with image)

5. Copy the bot's token for later use

6. Download the example project (Spring Boot project) in this example

7. Create an intelligent agent service or application service on a third-party platform or a self-built intelligent agent service platform (such as Coze, or Dify..., this article uses Coze as an example) (the name varies from platform to platform, but it is ultimately an intelligent agent)

8. Obtain the corresponding access-url, access-token, and other information for the intelligent agent. For example, for Coze, you need to obtain: access-token, bot-id, and user-id (the required keys are different for each platform)

9. Enter the access-token, bot-id, and user-id obtained from the Coze platform into the corresponding locations in the Spring Boot configuration file. Also, enter the token obtained from the Teammors robot development interface into the corresponding location in the Spring Boot configuration file (see image).

10. Run the robot (start the Spring Boot project)

---

## Client-side testing

1. First, as the provider of the intelligent robot, you need to complete the robot's basic information (point 4 mentioned above).

2. Then, submit the robot information to the platform for review. The platform will normally complete the review within 1-2 hours and notify you of the review result (via Teammors client message).

3. After approval, you can "list" or "delist" your robot on the development page in robot management. Once listed, it will be displayed in the robot marketplace for other users to follow and use.

4. Ordinary users can use your configured robot normally by following it or directly interacting with it.
