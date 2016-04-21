

function redirectToChat() {
    window.location = "http://lucy-messaging.herokuapp.com"; //"http://localhost:4567/";
}

function redirectToLogin() {
    window.location = "http://lucy-messaging.herokuapp.com/login.html"; //"http://localhost:4567/login.html";
}

function sendAuthRequest(userID, accessToken, myName) {
    // TODO: should also include appID?
    var url = "http://lucy-messaging.herokuapp.com/login"; //"http://localhost:4567/login";
    var authObj = {};
    authObj.authmethod = "fb";
    authObj.userid = userID;
    authObj.accesstoken = accessToken;
    authObj.myname = myName;
    document.cookie = "myName=" + myName;
    document.cookie = "myLid=" + userID;

    req = new XMLHttpRequest();
    req.open("POST", url, true);
    req.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    req.onreadystatechange = function() {
        if (req.readyState == 4) {
            console.log("Facebook authentication request received a response from server: " +
                req.status + " " + req.statusText);
            if (req.status >= 200 && req.status < 300) {
                console.log("Facebook authentication request approved by server");
                redirectToChat();
            } else if (req.status >= 400 && req.status < 500) {
                console.log("Facebook authentication request declined by server");
                redirectToLogin();
            } else {
                console.log("Unexpected response received from server. Aborting authentication.");
            }
        }
    };
    req.send(JSON.stringify(authObj));
}

function statusChangeHandler(response) {
    console.log("FB API returned response: " + response.status);
    if (response.status === 'connected') {
        FB.api('/me', function(meResponse) {
            sendAuthRequest(response.authResponse.userID, response.authResponse.accessToken, meResponse.name);
        });
    }
}


function newLoginButtonHandler() {
    console.log("Attempting FB login");
    FB.login(function(response) {
        statusChangeHandler(response);
    }, {scope: 'public_profile'});
}

