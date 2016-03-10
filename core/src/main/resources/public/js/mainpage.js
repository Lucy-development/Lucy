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
window.addEventListener("resize",sendbuttonlocation);

//TODO: could be better
function sendbuttonlocation(){
    var w = window.innerWidth;
    var c = Math.floor((1640 - w)/150);
    var p = 25 - 0.5*c;
    if (c == 4 || c == 5){
        p = 25 - 0.66*c;
    }
    if (c > 5){
        p = 25 - c*(0.8+(c-6)*0.27);
    }
    document.getElementById("send").style.right = String(p)+"%";
}