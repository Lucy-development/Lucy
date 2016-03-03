/*
 This basic FB SDK is needed for all FB functionality and should be included on every page
 where FB services are needed.
 */


window.fbAsyncInit = function () {
    FB.init({
        appId: '555058557990846',
        xfbml: true,
        version: 'v2.5'
    });
};

(
    function (d, s, id) {
        var js, fjs = d.getElementsByTagName(s)[0];
        if (d.getElementById(id)) {
            return;
        }
        js = d.createElement(s);
        js.id = id;
        js.src = "//connect.facebook.net/en_US/sdk.js";
        fjs.parentNode.insertBefore(js, fjs);
    }(document, 'script', 'facebook-jssdk')
);

