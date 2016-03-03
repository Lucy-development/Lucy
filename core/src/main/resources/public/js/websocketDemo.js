
// Establish the WebSocket connection and set up event handlers
var webSocket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/chat/");
webSocket.onmessage = function (msg) {
    addToChat(msg);
};
webSocket.onclose = function () {
    // TODO: this isn't showing on connection close
    addToChat("---WebSocket connection closed---")
};


// "Send" button listener
id("send").addEventListener("click", function () {
    sendMessage(id("message").value, id("searchcontact").value);
});

// "Enter" listener
id("message").addEventListener("keypress", function (e) {
    if (e.keyCode === 13) {
        sendMessage(id("message").value, id("searchcontact").value);
    }
});


// Send a message if it's not empty, then clear the input field
function sendMessage(message, receiver) {
    // TODO: should use JSON instead of this silly "receiver;message" format
    // TODO: or should we use XML or something for points?
    if (message !== "") {
        webSocket.send(receiver + ";" + message);
        id("message").value = "";
    }
}

// Update chat
function addToChat(msg) {
    var data = JSON.parse(msg.data);
    // Fetch sender and message content from JSON and add it to GUI
    var message = data.from + ": " + data.msg + "<br />";
    insert("messagebox", message);
}

// Helper function for inserting HTML as the first child of an element
function insert(targetId, message) {
    id(targetId).insertAdjacentHTML("afterbegin", message);
}

// Helper function for selecting element by id
function id(id) {
    return document.getElementById(id);
}
