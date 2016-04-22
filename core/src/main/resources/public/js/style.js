function changeStyle(style){
    if (style === "default"){
        document.getElementById('wrapper').className = "";
        document.getElementById('messagebox').className = "";
    }
    else{
        document.getElementById('wrapper').className = 'wrapper_darcula';
        document.getElementById('messagebox').className = "messagebox_darcula";
    }
}