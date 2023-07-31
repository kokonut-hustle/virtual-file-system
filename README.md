This project was deployed at: https://virtual-file-system-f3afa8f866d4.herokuapp.com/

## Requirements
The project involves building a web-based application deployed on Heroku. Users can interact with the application's file system through a webpage. The file system must be persisted on a server-side SQL database. While third-party libraries are permitted, the core logic must be implemented from scratch.

## Components
The front-end provides an interface for users to interact with the application, type commands, and view the results. To quickly implement this, I used the <link href="http://xtermjs.org/docs/api/addons/attach/">xterm</link> library, which provides a command-line interface on the web.

The back-end receives messages from the front-end, processes the logic, manipulates the database, and sends the result message back to the front-end.

I opted for WebSocket to exchange messages between the front-end and back-end because it allows the back-end to send messages to users without a specific user request. This is beneficial for handling complex commands that require multiple responses from the back-end.

The workflow is as follows:
- The user sends an initialization request through a browser.
- The browser sends a request to the backend server.
- The backend agrees to serve the request and sends HTML, CSS, and JS to the browser.
- The browser starts making a request to establish a WebSocket connection through the JS file.
- The backend agrees to connect the WebSocket.
- The user types a command into the browser.
- The browser sends the command through WebSocket.
- The backend processes the logic and manipulates the data.
- The backend sends a response message to the browser.
- The browser displays the result to the user.

In the code, I defined a Message class representing the message schema sent from the backend to the frontend. It includes the status of the processed command, a list of information the backend wants to show to the user, and the current directory of the user. The current directory is initially set to the root directory and can be changed as needed.

## Data Models
The core component of this project involves defining data models to work with the file system. Each item should store its name, the data it holds, and the timestamp when the file/folder was created. Additionally, it needs to show the relationship between folders and files, which is a tree-based structure. To achieve this, I decided to store the id of each item's parent.

Specifically, I defined two models: FileMetadata and FileContent. FileMetadata stores all the necessary information, including the size of files and folders, as some commands require size calculations. FileContent simply stores the content of files, identified by their id.

## Commands
In the code, I created a CommandHandler interface, with each type of command having its own handler implementation.

- The `cd` command does not require complex logic; it checks if the specified path exists and changes the current directory accordingly.
- The `cr` command involves creating a new file, and if successful, it traverses to the root and updates the size of all affected folders.
- The `cat` command finds and displays the content of the requested data.
- The `ls` command lists all items directly under the current directory.
- The `find` command recursively searches for all files/folders whose names contain the specified substring.
- The `up` command updates the name, path, and optionally the content and size of its ancestors.
- The `mv` command moves a file/folder to the specified destination folder, ensuring it is not moved into its own descendants or conflicts with existing items. It also updates the paths of affected descendants and the size of ancestors.
- The `rm` command removes the content and updates the size of ancestors.

## Others
To prevent conflicts when multiple users write the same data simultaneously, every command that changes the data is defined as a transaction. This may lead to longer processing times, but it ensures data consistency.

I chose to add an index on the `path` column since it is frequently called, which can improve performance when retrieving records. However, it may slightly slow down updates to the path.

## TODO
- Improve UI/UX, performance
- Refactor code
- Optimize
