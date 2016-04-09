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
    } else if (purpose === "init_contacts") {
        if (status === "success") {
            handleContactResponse(serverMessage);
        } else {
            insertToMessageBox(composeLogMessage("Failed to show contacts"));
            throw "Received init_contacts response with status: " + status;
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

function handleContactResponse(jsonObj) {
    writeContacts(jsonObj.contacts);
}

/**
 * Method inserts html tag into the index.html
 */
function writeContactHtmlTag(id, contactName) {
    //This code creates a new <li> element:
    var para = document.createElement('li');
    //Set contact id for the element
    para.id = id;

    para.setAttribute("onclick", "changeSelectedContactClass(" + "'" + id + "'" + ");");

    para.className = 'deactivated';

    //To add text to the <el> element, you must create a text node first. This code creates a text node:
    var node = document.createTextNode(contactName);

    //Then you must append the text node to the <ul> element:
    para.appendChild(node);

    //Finally you must append the new element to an existing element.

    //This code finds an existing element:
    var element = document.getElementById('list');
    //This code appends the new element to the existing element:
    element.appendChild(para);
}


/**
 *
 * @param contacts - list of contacts that will be written to the HTML
 */
function writeContacts(contacts) {
    for (var i = 0; i < contacts.length; i++) {
        writeContactHtmlTag(contacts[i].lid, contacts[i].fname + " " + contacts[i].lname);
    }
}

/**
 * Method changes chosen contact class
 */
function changeSelectedContactClass(id) {
    var activated = document.getElementsByClassName('activated');
    var index, len;
    for (index = 0, len = activated.length; index < len; ++index) {
        activated[index].setAttribute('class', 'deactivated');
    }

    var el = document.getElementById(String(id));
    console.log(id);

    // Check that elements exists before editing
    if (el) {
        el.setAttribute('class', 'activated');
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

    //TODO: we are dependent of Google
    // IMPORTANT: the following is async and uses callbacks! Edit with caution!
    // Insert geolocation data
    // Check if browser supports geolocation
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
            //First function is success function
            function (position) {
                //Get the latitude and the longitude;
                messageObject.latitude = position.coords.latitude;
                messageObject.longitude = position.coords.longitude;
                //Google service for reverse geocoding: we can get address by coordinates. Note, it is asyinc

                var geocoder = new google.maps.Geocoder();
                var latlng = new google.maps.LatLng(messageObject.latitude, messageObject.longitude);
                geocoder.geocode({'latLng': latlng}, function (results, status) {
                    if (status == google.maps.GeocoderStatus.OK) {
                        // Use 0 for more precision
                        if (results[1]) messageObject.location = results[1].formatted_address;
                    }
                    webSocket.send(JSON.stringify(messageObject));
                });
            },
            // Second function is for case where geolocation fails
            function () {
                webSocket.send(JSON.stringify(messageObject));
            });
    } else {
        // When browser does not support geolocating
        webSocket.send(JSON.stringify(messageObject));
    }
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
    if (sender === myLid)
        return "<div class='outgoing'>" + sender + ": "+messageContent+"</div>";
    return "<div class='incoming'>" + sender + ": " + messageContent + "</div>";
}

function composeLogMessage(logMessage) {
    return "<div class='logmessage'>" + logMessage + "</div>";
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