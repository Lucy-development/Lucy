
function changeStyle(style) {
    if (style === "default") {
        document.getElementById('wrapper').className = "";
        document.getElementById('messagebox').className = "";
        var all = document.getElementsByClassName('message_location_darcula');
        for (var i = 0; i < all.length; i++) {
            all[i].className = "message_location";
        }
    }
    else {
        document.getElementById('wrapper').className = 'wrapper_darcula';
        document.getElementById('messagebox').className = "messagebox_darcula";
        var all = document.getElementsByClassName('message_location');
        for (var i = 0; i < all.length; i++) {
            all[i].className = "message_location_darcula";
        }
    }
}