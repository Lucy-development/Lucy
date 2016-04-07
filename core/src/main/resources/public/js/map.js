function showLocation(position) {
    // Get coordinates
    var latitude = position.coords.latitude;
    var longitude = position.coords.longitude;

    // Map center:
    var myCenter = new google.maps.LatLng(latitude, longitude);

    // Map properties
    var mapProp = {
        center: myCenter,
        zoom: 15,
        mapTypeId: google.maps.MapTypeId.ROADMAP,
        disableDefaultUI: true
    };

    // Create map
    var map = new google.maps.Map(document.getElementById("googleMap"), mapProp);

    // Add marker
    var marker = new google.maps.Marker({
        position: myCenter
    });

    marker.setMap(map);

    // Create info window
    var infowindow = new google.maps.InfoWindow({
        content: "We know where you are!"
    });

    infowindow.open(map, marker);

    // Set traffic layer
    var trafficLayer = new google.maps.TrafficLayer();
    trafficLayer.setMap(map);
}


function errorHandler(err) {
    if (err.code == 1) {
        alert("Error: Access is denied!");
    }
    else if (err.code == 2) {
        alert("Error: Position is unavailable!");
    }
}

function getLocation() {
    if (navigator.geolocation) {
        // timeout at 60000 milliseconds (60 seconds)
        var options = {timeout: 60000};
        navigator.geolocation.getCurrentPosition(showLocation, errorHandler, options);
    }
    else {
        alert("Sorry, browser does not support geolocation!");
    }
}

google.maps.event.addDomListener(window, 'load', getLocation());


// function changeLanguage(lan){
//
//     if (lan == "et"){
//         document.getElementById("menu-button").textContent = "Valikud";
//         document.getElementById("en").textContent = "Inglise";
//         document.getElementById("et").textContent = "Eesti";
//         document.getElementById("smallscreenEn").textContent = "Inglise";
//         document.getElementById("smallscreenEt").textContent = "Eesti";
//         document.getElementById("chat").textContent = "Kodu";
//         document.getElementById("map").textContent = "Kaart";
//         document.documentElement.lang = "et";
//     }
//     else{
//         document.getElementById("menu-button").textContent = "Menu";
//         document.getElementById("en").textContent = "English";
//         document.getElementById("et").textContent = "Estonian";
//         document.getElementById("smallscreenEn").textContent = "English";
//         document.getElementById("smallscreenEt").textContent = "Estonian";
//         document.getElementById("chat").textContent = "Home";
//         document.getElementById("map").textContent = "Map";
//         document.documentElement.lang = "en";
//     }
// }
