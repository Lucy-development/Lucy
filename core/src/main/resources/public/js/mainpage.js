
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
