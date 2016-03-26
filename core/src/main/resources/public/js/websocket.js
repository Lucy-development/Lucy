
var webSocket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/chat/");
var connectionOpen = true;
var sessionAuthenticated = false;
var myLid = getCookie("myLid");


// WS HANDLERS
webSocket.onopen = function () {
    insertToMessageBox(composeLogMessage("WebSocket session opened"));
    insertToMessageBox(composeLogMessage("Attempting to authenticate session..."));
    sendAuthSessionRequest(getCookie("sessiontoken"));
};

webSocket.onmessage = function (messageEvent) {
    messageHandler(messageEvent.data);
};

webSocket.onclose = function (closeEvent) {
    connectionOpen = false;
    sessionAuthenticated = false;
    if (closeEvent.code === 1008) {
        insertToMessageBox(composeLogMessage("Server killed the session with reason: " + closeEvent.reason));
    }
    insertToMessageBox(composeLogMessage("Session died :("));
};


// EVENT LISTENERS
elementById("send").addEventListener("click",
    sendHandler
);
elementById("message").addEventListener("keypress", function (key) {
    if (key.keyCode === 13) {
        sendHandler();
    }
});


function messageHandler(string) {
    var serverMessage = JSON.parse(string);
    var purpose = serverMessage.purpose;
    var status = serverMessage.status;
    if (purpose === "msg_sent") {
        if (status === "success") {
            // Some message has been successfully sent
        } else if (status === "fail") {
            // Some message could not be delivered
            insertToMessageBox(composeLogMessage("Failed to deliver message"));
        } else {
            throw "Unexpected status: " + status;
        }
    } else if (purpose === "msg_received") {
        // Received message
        var sender = serverMessage.from;
        var messageContent = serverMessage.content;
        insertToMessageBox(composeRegularMessage(lidToContactName(sender), messageContent));
    } else if (purpose === "auth_resp") {
        if (status === "success") {
            sessionAuthenticated = true;
            insertToMessageBox(composeLogMessage("Session successfully authenticated"));
            insertToMessageBox(composeLogMessage("Your ID: " + myLid));
        } else {
            insertToMessageBox(composeLogMessage("Failed to authenticate session"));
        }
    } else {
        throw "Unexpected purpose for message: " + string;
    }
}

function sendHandler() {
    var msgContent = getMsgInput();
    var receiver = getCurrentlyActiveReceiver();
    if (msgContent !== "") {
        if (connectionOpen) {
            if (sessionAuthenticated) {
                sendMessage(msgContent, receiver);
                clearMsgInput();
                insertToMessageBox(composeRegularMessage(lidToContactName(myLid), msgContent));
            } else {
                insertToMessageBox(composeLogMessage("Unable to send message, WebSocket session is unauthenticated"));
            }
        } else {
            insertToMessageBox(composeLogMessage("Unable to send message, WebSocket connection is closed"));
        }
    }
}


function sendAuthSessionRequest(sessionKey) {
    var authRequestObject = {};
    authRequestObject.purpose = "auth";
    authRequestObject.content = sessionKey;
    webSocket.send(JSON.stringify(authRequestObject));
}

function sendMessage(message, receiver) {
    var messageObject = {};
    messageObject.purpose = "msg";
    messageObject.to = receiver;
    messageObject.content = message;
    webSocket.send(JSON.stringify(messageObject));
}

/**
 * Avoid inventing the wheel? (http://www.w3schools.com/js/js_cookies.asp)
 * TODO: invent the wheel
 */
function getCookie(cookieName) {
    var name = cookieName + "=";
    var ca = document.cookie.split(';');
    for (var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') c = c.substring(1);
        if (c.indexOf(name) == 0) return c.substring(name.length, c.length);
    }
    return "";
}


/**
 * Method returns the id of the currently chosen recipient
 */
function getCurrentlyActiveReceiver() {
    if (getReceiverBoxId() !== "") {
        return getReceiverBoxId();
    } else {
        if (elementByClass("activated").length === 1)
            return elementByClass("activated")[0].getAttribute("id");
        //TODO: Error handling
    }
}


function lidToContactName(lid) {
    // TODO: return corresponding contact name
    return lid;
}

function composeRegularMessage(sender, messageContent) {
    return sender + ": " + messageContent + "</br>";
}

function composeLogMessage(logMessage) {
    return "--- " + logMessage + " ---" + "</br>";
}

function getMsgInput() {
    return elementById("message").value;
}

function clearMsgInput() {
    elementById("message").value = "";
}

function getReceiverBoxId() {
    return elementById("searchcontact").value;
}

function insertToMessageBox(string) {
    insertBeforeEnd("messagebox", string);
}

function insertBeforeEnd(targetId, string) {
    elementById(targetId).insertAdjacentHTML("beforeend", string);
}

function elementById(id) {
    return document.getElementById(id);
}

function elementByClass(className) {
    return document.getElementsByClassName(className);
}