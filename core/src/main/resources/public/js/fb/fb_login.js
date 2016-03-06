
function redirectToChat() {
    window.location = "http://localhost:4567/";
}

function redirectToLogin() {
    window.location = "http://localhost:4567/login";
}

function sendAuthRequest(userID, accessToken) {
    // TODO: should also include appID?
    var url = "http://localhost:4567/login";
    var params = "userid=" + userID + "&accesstoken=" + accessToken;
    req = new XMLHttpRequest();
    req.open("POST", url, true);
    req.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    req.onreadystatechange = function() {
        if (req.readyState == 4) {
            if (req.status >= 200 && req.status < 300) {
                console.log("Facebook authentication request approved by server");
                redirectToChat();
            } else if (req.status >= 400 && req.status < 500) {
                console.log("Facebook authentication request declined by server");
                redirectToLogin();
            } else {
                console.log("Facebook authentication request received an unexpected " +
                    "response from server: " + req.status + " " + req.statusText);
            }
        }
    };
    req.send(params);
}

function statusChangeHandler(response) {
    console.log("FB API returned response: " + response.status)
    if (response.status === 'connected') {
        sendAuthRequest(response.authResponse.userID, response.authResponse.accessToken);
    }
}

function loginButtonHandler() {
    console.log("Attempting FB login");
    FB.getLoginStatus(function(response) {
        statusChangeHandler(response);
    });
}
