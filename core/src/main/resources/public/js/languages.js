
function changeLanguage(lan){

    if (lan == "et"){
        document.getElementById("searchcontact").placeholder = "Kontakti ID";
        document.getElementById("message").placeholder = "Kirjuta sõnum või lohista faile siia";
        document.getElementById("send").value = "Saada";
        document.getElementById("menu-button").textContent = "Valikud";
        document.getElementById("map-link").textContent = "Näita kaardil";
        document.getElementById("en").textContent = "Inglise";
        document.getElementById("et").textContent = "Eesti";
        document.getElementById("smallscreenEn").textContent = "Inglise";
        document.getElementById("smallscreenEt").textContent = "Eesti";
        document.getElementById("smallscreenMap").textContent = "Näita kaardil";
        document.getElementById("messagebox").textContent = "--- Ühendus serveriga puudub ---";
        document.getElementById("searchcontact").title = "Kirjuta kontakti ID siia";
        document.getElementById("contacts").title = "Kontaktid ilmuvad siia";
        document.getElementById("map-link").title = "Teid jälgitakse!";
        document.getElementById("smallscreenMap").title = "Teid jälgitakse!";
        document.getElementById("home").textContent = "Kodu";
        document.getElementById("map").textContent = "Kaart";
        document.documentElement.lang = "et";
    }
    else{
        document.getElementById("searchcontact").placeholder = "Receiver ID";
        document.getElementById("message").placeholder = "Insert message or drop files here";
        document.getElementById("send").value = "Send";
        document.getElementById("menu-button").textContent = "Menu";
        document.getElementById("map-link").textContent = "Show Map";
        document.getElementById("en").textContent = "English";
        document.getElementById("et").textContent = "Estonian";
        document.getElementById("smallscreenEn").textContent = "English";
        document.getElementById("smallscreenEt").textContent = "Estonian";
        document.getElementById("smallscreenMap").textContent = "Show Map";
        document.getElementById("messagebox").textContent = "--- Connection closed ---";
        document.getElementById("searchcontact").title = "Write contact ID here";
        document.getElementById("contacts").title = "Contacts will appear here";
        document.getElementById("map-link").title = "You are being watched!";
        document.getElementById("smallscreenMap").title = "You are being watched!";
        document.getElementById("home").textContent = "Home";
        document.getElementById("map").textContent = "Map";
        document.documentElement.lang = "en";
    }
}
