/**
 * Created by Priit Paluoja on 13.03.2016.
 */


/**
 * Fuction takes contactList as a jason object and call function that writes all the contacts in the JSON into the html.
 *
 {
 "connected" : "true",
 "contacts" : [
  {"name" : "Mari Maasikas", "lid" : "7m1N80w"},
  {"name" : "Marge Kaevur", "lid" : "9asdj23M"}
 ]
}
 *
 */
function handleContactResponse(json) {
    var obj = JSON.parse(json);
    if (obj.connected === "true") {
        writeContacts(obj.contacts);
    }
}


//function x() {
//    handleContactResponse('{"connected": "true","contacts": [  {"name": "Mari Maasikas", "lid": "1"}, {"name": "Marge Kaevur", "lid": "2"}] }');
//}


/**
 * Method inserts html tag into the index.html
 */
function writeContactHtmlTag(id, contactName) {

    //This code creates a new <li> element:
    var para = document.createElement('li');
    //Set contact id for the element
    para.id = id;

    para.setAttribute("onclick", "changeSelectedContactClass(" + id + ");");
    para.className = 'deactivated';

    //To add text to the <el> element, you must create a text node first. This code creates a text node:
    var node = document.createTextNode(contactName);

    //Then you must append the text node to the <ul> element:
    para.appendChild(node);

    //Finally you must append the new element to an existing element.

    //This code finds an existing element:
    var element = document.getElementById('list');
    //This code appends the new element to the existing element:
    element.appendChild(para);
}

/**
 *
 * @param contacts - list of contacts that will be written to the HTML
 */
function writeContacts(contacts) {
    for (var i = 0; i < contacts.length; i++) {
        writeContactHtmlTag(contacts[i].lid, contacts[i].name);
    }
}

/**
 * Method changes chosen contact class
 */
function changeSelectedContactClass(id) {
    var activated = document.getElementsByClassName('activated');
    var index, len;
    for (index = 0, len = activated.length; index < len; ++index) {
        activated[index].setAttribute('class', 'deactivated');
    }
    document.getElementById(id).setAttribute('class', 'activated');
}
