/**
 * Created by Priit Paluoja on 13.03.2016.
 */


//TODO: created css classes (li.activated and li.deactivated)
//TODO: set <ul> id to "list" or change js.
//TODO: create method that sets receiver id
//TODO: why do we need setAttribute method? why .classname does not work?

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
    //TODO: CORRECT ELEMENT ID NEEDED (reference from html) - list is the parent name
    var element = document.getElementById('list');
    //This code appends the new element to the existing element:
    element.appendChild(para);
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
