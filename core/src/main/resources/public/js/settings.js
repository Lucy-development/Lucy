//TODO: not changing on the first click

function displaySettings(){
    if (document.getElementById("settingsbox").style.display == "none"){
        document.getElementById("settingsbox").style.display = "block";
        document.getElementById("thingy").style.display = "block";

    }
    else{
        document.getElementById("settingsbox").style.display = "none";
        document.getElementById("thingy").style.display = "none";

    }
}