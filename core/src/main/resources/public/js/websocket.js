var webSocket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/chat/");

var connectionOpen = true;

webSocket.onmessage = function (msg) {
    messageHandler(msg);
};

webSocket.onclose = function () {
    connectionOpen = false;
    addMessageToGUI("--- Connection closed ---" + "<br />");
};

elementById("send").addEventListener("click", sendHandler);
elementById("message").addEventListener("keypress", function (key) {
    if (key.keyCode === 13) {
        sendHandler();
    }
});

function sendHandler() {
    var msgContent = elementById("message").value;
    var receiver = getCurrentlyActiveReceiver();
    if (msgContent != "") {
        sendMessage(msgContent, receiver);
        clearMsgInput();
        if (connectionOpen) {
            addMessageToGUI(composeMessageString("--", msgContent));
        } else {
            addMessageToGUI("--- Unable to send message: connection is closed ---" + "<br />");
        }
    }
}

function clearMsgInput() {
    elementById("message").value = "";
}

function sendMessage(message, receiver) {
    // TODO: should use JSON instead of this silly "receiver;message" format
    // TODO: or should we use XML or something for points?
    webSocket.send(receiver + ";" + message);
}

//TODO: there are two options: message can either be REAL message or System/WebServer data message
// Need to check it (while sending and while receiving by websocket

/*
 [17:01:45] Kaspar Papli: Sõnum läks edukalt kohale:
 {
 "status" : "success",
 "errors" : ""
 }
 [17:02:02] Kaspar Papli: Sõnum läks edukalt kohale ja kontakte tuleb uuendada:
 {
 "status" : "updcontacts",
 "contacts" : {...} # täpselt sama formaat, mis alguses
 "errors" : ""
 }
 [17:02:36 | Muudetud - 17:03:41] Kaspar Papli: Sõnumi läkitamisel tekkis viga, pani pange:
 {
 "status" : "failed",
 "errors" : [
 {"errcode" : "9000", "errmsg" : "3mi93Qsoa23d"}
 ]
 }

 */

function messageHandler(msg) {
    var messageSting = parseMessage(msg);
    addMessageToGUI(messageSting);
}

function parseMessage(msg) {
    var data = JSON.parse(msg.data);
    return composeMessageString(data.from, data.msg);
}

function composeMessageString(receiver, msgString) {
    return receiver + ": " + msgString + "<br />";
}

function addMessageToGUI(msgString) {
    insert("messagebox", msgString);
}

function insert(targetId, string) {
    // TODO: sanitize content before inserting
    elementById(targetId).insertAdjacentHTML("afterbegin", string);
}

function elementById(id) {
    return document.getElementById(id);
}

function elementByClass(className) {
    return document.getElementsByClassName(className);
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


function getReceiverBoxId() {
    return elementById("searchcontact").value;
}