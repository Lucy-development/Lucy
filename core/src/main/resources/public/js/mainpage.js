
var messages = document.getElementsByClassName('incoming');
var locations = document.getElementsByClassName('message_location');


function changeStyle(style) {
    if (style === "default") {
        document.getElementById('wrapper').className = "";
        document.getElementById('messagebox').className = "";
        document.getElementById('history').className = "";
        var all1 = document.getElementsByClassName('message_location_darcula');
        for (var j = 0; j < all1.length; j++) {
            all1[j].className = "message_location";
            j--;
        }
        locations = document.getElementsByClassName('message_location');

    }
    else {
        document.getElementById('wrapper').className = 'wrapper_darcula';
        document.getElementById('messagebox').className = "messagebox_darcula";
        document.getElementById('history').className = "history_darcula";
        var all2 = document.getElementsByClassName('message_location');
        for (var i = 0; i < all2.length; i++) {
            all2[i].className = "message_location_darcula";
            i--;
        }
        locations = document.getElementsByClassName('message_location_darcula');

    }
}

function getLocation(message){
    for (var i = 0; i < messages.length; i++) {
        var msg = messages[i];
        if (msg.id === message.id){
            return i;
            // const location = messageLocation(i);
            // return location;
        }
    }
}

function messageLocation(index){
    return locations[index];
}

function showMessageLocation(message){
    var location = locations[getLocation(message)];
    location.style.display = 'block';
}

function hideMessageLocation(message){
    var location = locations[getLocation(message)];
    location.style.display = 'none';
}

function changeLanguage(language){
    if (language === "eng"){
        document.getElementById("searchcontact").placeholder = "Search contact";
        document.getElementById("menu-button").textContent = "Theme";
        document.getElementById("history").textContent = "load messages";
        document.getElementById("message").placeholder = "Insert message here";
        document.getElementById("eng").style.color = "#656565";
        document.getElementById("eng").style.backgroundColor = "#ebebf1";
        document.getElementById("est").style.color = "#ffffff";
        document.getElementById("est").style.backgroundColor = "#656565";
        document.documentElement.lang = "en";
    }
    else {
        document.getElementById("searchcontact").placeholder = "Otsi kontakti";
        document.getElementById("menu-button").textContent = "Stiil";
        document.getElementById("history").textContent = "näita sõnumeid";
        document.getElementById("message").placeholder = "Sisesta sõnum";
        document.getElementById("est").style.color = "#656565";
        document.getElementById("est").style.backgroundColor = "#ebebf1";
        document.getElementById("eng").style.color = "#ffffff";
        document.getElementById("eng").style.backgroundColor = "#656565";
        document.documentElement.lang = "et";
    }
}