const socket = new WebSocket("wss://virtual-file-system-f3afa8f866d4.herokuapp.com/ws");

var term = new window.Terminal({
    cursorBlink: true
});
term.setOption('theme', {
    background: '#1c1c1c', // Background color of the terminal
    foreground: '#00ff00', // Text color of the terminal
    cursor: '#00ff00',     // Cursor color
    cursorAccent: '#1c1c1c', // Color of the cursor's background
    selection: '#00ff00',    // Color of the selected text
});

term.open(document.getElementById('terminal'));

let currentDirectory = '/';

function init() {
    if (term._initialized) {
        return;
    }

    term._initialized = true;

    term.prompt = () => {
        term.write('\r\n$ ');
    };
    prompt(term);

    term.onData(e => {
        switch (e) {
            case '\u0003': // Ctrl+C
                term.write('^C');
                prompt(term);
                break;
            case '\r': // Enter
                runCommand(term, command);
                command = '';
                break;
            case '\u007F': // Backspace (DEL)
                // Do not delete the prompt
                if (term._core.buffer.x > currentDirectory.length + 2) {
                    term.write('\b \b');
                    if (command.length > 0) {
                        command = command.substr(0, command.length - 1);
                    }
                }
                break;
            case '\u0009':
                console.log('tabbed', output, ["dd", "ls"]);
                break;
            default:
                if (e >= String.fromCharCode(0x20) && e <= String.fromCharCode(0x7E) || e >= '\u00a0') {
                    command += e;
                    term.write(e);
                }
        }
    });
}

function clearInput(command) {
    var inputLengh = command.length;
    for (var i = 0; i < inputLengh; i++) {
        term.write('\b \b');
    }
}

function prompt(term) {
    command = '';
    if (!currentDirectory) {
        currentDirectory = '/';
    }
    term.write('\r\n' + currentDirectory + '$ ');
}

socket.onmessage = (event) => {
    const data = JSON.parse(event.data);

    if (data.curDir === null) {
        currentDirectory = '/';
    } else if (data.infoes) {
        currentDirectory = data.curDir;
        data.infoes.forEach(info => {
            console.log(info);
            prompt(term);
            term.write(info);
        });
    } else {
        prompt(term);
        term.write("Something wrong happened");
    }
    prompt(term);
}

function runCommand(term, command) {
    if (command.length > 0) {
        const data = {
            command: command,
            curDir: currentDirectory
        };
        const jsonMessage = JSON.stringify(data);
        socket.send(jsonMessage + '\n');
        return;
    }
}

init();
