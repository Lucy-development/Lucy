
function changeLanguage(lan){

    if (lan == "et"){
        document.getElementById("searchcontact").placeholder = "Kontakti ID";
        document.getElementById("message").placeholder = "Kirjuta sõnum või lohista faile siia";
        document.getElementById("contact").textContent = "Kontakt";
        document.getElementById("settingsbutton").textContent = "Seaded";
        document.getElementById("logout").textContent = "Logi välja";
        document.getElementById("send").textContent = "Saada";
        document.documentElement.lang = "et";
    }
    else{
        document.getElementById("searchcontact").placeholder = "Receiver ID";
        document.getElementById("message").placeholder = "Insert message or drop files here";
        document.getElementById("contact").textContent = "Contact";
        document.getElementById("settingsbutton").textContent = "Settings";
        document.getElementById("logout").textContent = "Log out";
        document.getElementById("send").textContent = "Send";
        document.documentElement.lang = "en";
    }
}
