(function () {
    "use strict"
    let userData;
    let get = (id) => document.getElementById("login" + "-" + id);
    let getById = (id) => document.getElementById(id);

    let COUNTRY_CODE = "+91"
    let hardCodedPassword = "BlahBl@01"
    let SIGNUP_CONFIRMATION_API = "http://localhost:8080/signup/confirm"

    let view = function () {

        const loginBox = get("main-box");
        loginBox.style = "display: none;";

        const boxShowBtn = get("box-show-btn");
        boxShowBtn.onclick = () => {
            loginBox.style = "";
            showLogin();
        }

        const boxCancelBtn = get("box-cancel-btn");
        boxCancelBtn.onclick = () => {
            loginBox.style = "display: none;";
        }

        const logoutBtn = getById("logout-btn");
        logoutBtn.hidden = true;
        logoutBtn.onclick = userLogout;


        getById("signin-btn").onclick = userLogin;
        getById("signup-btn").onclick = userRegister;
        getById("confirm-signin-btn").onclick = userConfirmLogin;

        getById("signin-href").onclick = showLogin;
        getById("signup-href").onclick = showRegister;

        getById("resend-code-href").onclick = resentConfirmCode;

        const welcome = get("welcome-msg");
        welcome.innerText = "";

        const message = get("box-msg");

        // Username saved for confirmation flows. Not to be used for any other purpose
        let confirmationUser = "";

        function show(idSuffixes, hide = false) {
            for (let suffix of idSuffixes) {
                if (suffix.endsWith("-in")) {
                    // input field
                    let field = get(suffix);
                    field.hidden = hide;
                    if (field.value != undefined) {
                        field.value = "";
                    }
                    let fieldError = get(suffix + "-error");
                    fieldError.innerText = "";

                } else if (suffix.endsWith("-nav")) {
                    // nav links
                    let nav = getById(suffix);
                    nav.hidden = hide;
                } else if (suffix.endsWith("-btn")) {
                    // buttons
                    let btn = getById(suffix);
                    btn.hidden = hide;
                } else {
                    console.log(`Unknown field suffix ${suffix}`);
                }
            }
        }

        function hide(idSuffixes) {
            show(idSuffixes, true);
        }

        function showLogin() {
            message.innerText = "";
            show([ "phone-in", "signin-btn", "reg-nav"]);
            hide(["name-in", "confirm-in", "login-nav", "resent-code-nav", "signup-btn", "confirm-signin-btn"]);
        }

        function showRegister() {
            message.innerText = "";
            show([ "name-in", "phone-in", "login-nav", "signup-btn"]);
            hide([ "confirm-in", "reg-nav", "resent-code-nav", "signin-btn", "confirm-signin-btn"]);
        }
        function showConfirmLogin() {
            message.innerText = "";
            show(["confirm-in", "resent-code-nav", "confirm-signin-btn"]);
            hide([ "name-in", "phone-in",  "reg-nav", "login-nav",  "signin-btn", "signup-btn"]);
        }


        // Validation utilities 
        function validateName(auth) {
            let name = get("name-in").value;
            let error = get("name-in-error");

            if (name == null
                || name == undefined
                || name.trim().length == 0) {
                error.innerText = "Please enter your name."
                return false;
            }
            if (name.trim().length > 50) {
                error.innerText = "Sorry, your name doesn't look right."
                return false;
            }

            auth.Name = name;
            error.innerText = "";
            return true;
        }

        function validatePhone(auth) {
            let phone = get("phone-in").value;
            let error = get("phone-in-error");

            if (phone == null
                || phone == undefined
                || phone.trim().length == 0) {
                error.innerText = "Please enter your phone address."
                return false;
            }

            let isValid = phone.trim().match(/^[0-9]{10,10}$/);
            if (isValid == null || isValid == undefined) {
                error.innerText = "Sorry, your phone doesn't look right."
                return false;
            }
            auth.Phone = COUNTRY_CODE + phone;
            auth.Username = auth.Phone;
            error.innerText = "";
            return true;
        }

        function validateConfirmCode(auth) {
            let confirmCode = get("confirm-in").value;
            let error = get("confirm-in-error");

            if (confirmCode == null
                || confirmCode == undefined
                || confirmCode.trim().length == 0) {
                error.innerText = "Please enter your confirmation code."
                return false;
            }
            if (confirmCode.trim().length > 50 || isNaN(confirmCode)) {
                error.innerText = "Sorry, your confirmation code doesn't look right."
                return false;
            }
            auth.ConfirmCode = confirmCode;
            error.innerText = "";
            return true;
        }


        return {

            getAuthData: function (action) {
                let authData = {};
                if (action == "signin") {
                    let uVal = validatePhone(authData);
                    if (uVal) {
                        // save the username for handing FORCE_CHANGE_PASSWORD event on new users created by admin
                        confirmationUser = authData.Username;
                        return authData;
                    }
                } else if (action == "signUp") {
                    let nVal = validateName(authData);
                    let pVal = validatePhone(authData);
                    if (nVal && pVal) {
                        // save the username for confirmation 
                        confirmationUser = authData.Username;
                        return authData;
                    }
                } else if (action == "signUpConfirmation") {
                    if (validateConfirmCode(authData)) {
                        // set the username for confirmation
                        authData.Username = confirmationUser;
                        return authData;
                    }
                } else if(action == "resendConfirmationCode"){
                    // set the username for confirmation
                    authData.Username = confirmationUser;
                    return authData;
                } else if (action == "signinConfirmation") {
                    let pVal = validateConfirmCode(authData);
                    if (pVal) {
                        // set the username for confirmation
                        authData.Username = confirmationUser;                        
                        return authData;
                    }
                }else {
                    console.log(`Unknown action ${action}`);
                }
                return null;
            },
            welcome: function (welcomemsg) {
                welcome.innerText = welcomemsg;
                logoutBtn.hidden = false;
                boxShowBtn.hidden = true;
                loginBox.style = "display: none;";
            },
            logout: function () {
                welcome.innerText = "";
                logoutBtn.hidden = true;
                boxShowBtn.hidden = false;
            },
            error: function (errormsg) {
                message.innerText = errormsg;
            },
            showConfirmRegister: function (msg) {
                showConfirmRegister();
                message.innerText = msg;
            },
            showConfirmLogin: function (msg) {
                showConfirmLogin();
                message.innerText = msg;
            },            
            showLogin: function (msg) {
                showLogin();
                message.innerText = msg;
            }           
        };
    }();

    // Auto signin based on local storage tokens
    (function () {
        autoSignin({
            onSuccess: (result) => {
                userData = result;
                view.welcome(`Welcome ${userData.name}! `);
            },
            onFailure: (err) => {
                console.log("AutoSignin error - ", err);
            }
        });
    })();

    function userLogin() {
        let authData = view.getAuthData("signin");
        authData.Password = hardCodedPassword;

        if (authData == null || authData == undefined) return;

        const callback = {
            onSuccess: (result) => {
                userData = result;
                view.welcome(`Welcome ${userData.name}! `);
            },
            onFailure: (err) => {
                view.error(err);
            },
            onConfirmLogin: (requiredAttributes) => {
                view.showConfirmLogin(`We have sent a code at your phone address, enter it below to login.`);
            }
        }
        signin(authData, callback);
    }

    function userLogout() {
        const callback = {
            onSuccess: (result) => {
                userData = null;
                view.logout();
            },
            onFailure: (err) => {
                console.log(err);
            }
        }
        signout(callback);
    }

    function userRegister() {
        let authData = view.getAuthData("signUp");
        authData.Password = hardCodedPassword;
        if (authData == null || authData == undefined) return;
        const callback = {
            onSuccess: (username) => {
                console.log(username)
                fetch(`${SIGNUP_CONFIRMATION_API}/${username}`)
                .then((response) => {
                    console.log(response)
                    if (!response.ok) {
                        view.error(`Unable to auto confirm signup. Please contact admin.`)
                    }else{
                        // signin on success
                        userLogin();
                    }
                    return "success";
                })
            },
            onFailure: (err) => {
                view.error(err);
            }
        }
        signUp(authData, callback);
    }

    function resentConfirmCode() {
        let authData = view.getAuthData("resendConfirmationCode");
        if (authData == null || authData == undefined) return;
        const callback = {
            onSuccess: (result) => {
                view.showLogin(`We have sent a verification code again at your phone address!`);
            },
            onFailure: (err) => {
                view.error(err);
            }
        }
        resendConfirmationCode(authData, callback);
    }  
    
    
    function userConfirmLogin() {
        let authData = view.getAuthData("signinConfirmation");
        if (authData == null || authData == undefined) return;
        const callback = {
            onSuccess: (result) => {
                userData = result;
                view.welcome(`Welcome ${userData.name}! `);
            },
            onFailure: (err) => {
                view.error(err);
            }
        }
        completeMFALogin(authData, callback);
    }

})();


