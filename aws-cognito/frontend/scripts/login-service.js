"use strict"

const errormsg = (err) => err.message || JSON.stringify(err);
let UserPoolId= "ap-south-1_VVfBigSkH";
let ClientId= "4cus8h801ro6k4g8rno56e1t0o";

const poolData = {
    UserPoolId: UserPoolId,
    ClientId: ClientId
};


// session attribute for handing FORCE_CHANGE_PASSWORD event on new users created by admin
let cognitoUserSession, sessionUserAttributes;

const userPool = new AmazonCognitoIdentity.CognitoUserPool(poolData);

/**
 * Sign in the user on page refresh by retrieving the sesson from browser's localstorage
 * @param {*} callback 
 */
function autoSignin(callback) {
    let cognitoUser = userPool.getCurrentUser();
    if (cognitoUser != null) {
        cognitoUser.getSession(function (err, session) {
            if (err) {
                callback.onFailure(errormsg(err));
                return;
            }
            console.log('session validity: ' + session.isValid());
            afterLoginSuccess(cognitoUser,session,callback);
        });
    }
}

/**
 * Sign in user with auth data
 * 
 * @param {*} authData 
 * @param {*} callback 
 */
function signin(authData, callback) {
    console.log(authData)
    const authenticationDetails = new AmazonCognitoIdentity.AuthenticationDetails(authData);
    const cognitoUser = getCognitoUser(authData.Username);
    cognitoUser.authenticateUser(authenticationDetails, {
        onSuccess: function (result) {
            console.log("Authentication successful!");
            afterLoginSuccess(cognitoUser,result,callback);

        },
        onFailure: function (err) {
            console.log(err);
            console.log(typeof err);
            callback.onFailure(errormsg(err));
        },
        mfaRequired: function(userAttributes, requiredAttributes) {
            // store userAttributes and congitoUser on global variable
            sessionUserAttributes = userAttributes;
            cognitoUserSession = cognitoUser;

            // show the force password change UI
            callback.onConfirmLogin(requiredAttributes);
        }        
    });
}

/**
 * Gets the username for Welcome message and reinitialized the config for S3 access
 * @param {*} callback 
 */
function afterLoginSuccess(cognitoUser,session,callback) {
    // Get the user name from the attributes
    cognitoUser.getUserAttributes(function (err, result) {
        if (err) {
            console.log(err);
            callback.onFailure(errormsg(err));
            return;
        }
        let principalName;
        for (let i in result) {
            if (result[i].Name == "name") {
                principalName = result[i].Value;
                break;
            }
        }
        let cognitoUser = userPool.getCurrentUser();
        // Start the session and initiate the config for with IAM roles defined in 
        // Identity pool ( required for access to other aws services like s3 ) 
        cognitoUser.getSession(function (err, result) {
            if (err) {
                console.log(err);
                callback.onFailure(errormsg(err))
                return;
            }
            if (result) {
                // AWS.config.update({
                //     region: bucketRegion,
                //     credentials: new AWS.CognitoIdentityCredentials({
                //         IdentityPoolId: IdentityPoolId,
                //         Logins: { [idKey]: session.getIdToken().getJwtToken() }
                //     })
                // });

                // s3 = new AWS.S3({ params: { Bucket: imageBucket } });
                // Pass on the user name to callback
                callback.onSuccess({
                    name: principalName,
                    accessToken: session.getAccessToken().getJwtToken(),
                    idToken: session.getIdToken().getJwtToken()
                });
            }
        });
    });

}

/**
 * Sign out the user and invalidates the tokens 
 * @param {*} callback 
 */
function signout(callback) {
    let cognitoUser = userPool.getCurrentUser();
    console.log(cognitoUser, "signing out...")
    cognitoUser.signOut();

    cognitoUser = null;;
    callback.onSuccess("Seccessful logout!");

    //TODO:  signout from S3 object also
}

function signUp(authData, callback) {
    console.log(authData)
    let attributeList = [];
    let dataPhone = {
        Name: 'phone_number',
        Value: authData.Phone,
    };
    let dataName = {
        Name: 'name',
        Value: authData.Name,
    };
    let attributePhone = new AmazonCognitoIdentity.CognitoUserAttribute(dataPhone);
    let attributeName = new AmazonCognitoIdentity.CognitoUserAttribute(dataName);
    attributeList.push(attributePhone);
    attributeList.push(attributeName);

    userPool.signUp(authData.Username, authData.Password, attributeList, null, function (err, result) {
        if (err) {
            console.log("Error object");
            console.log(err);
            callback.onFailure(errormsg(err));
            return;
        }
        let cognitoUser = result.user;
        console.log(cognitoUser)
        callback.onSuccess(cognitoUser.username);
    });
}


function resendConfirmationCode(authData, callback) {
    cognitoUserSession.resendConfirmationCode(function (err, result) {
        if (err) {
            callback.onFailure(errormsg(err));
            return;
        }
        callback.onSuccess(result);
    });
}

function forgotPassword(authData, callback) {
    let cognitoUser = getCognitoUser(authData.Username);
    cognitoUser.forgotPassword({
        onSuccess: function(data) {
            callback.onSuccess(data);
        },
        onFailure: function(err) {
            callback.onFailure(errormsg(err));
        }
    });    
}

function confirmPassword(authData, callback) {
    let cognitoUser = getCognitoUser(authData.Username);
    cognitoUser.confirmPassword(authData.ConfirmCode, authData.Password, {
        onSuccess: function(data) {
            callback.onSuccess(data);
        },
        onFailure: function(err) {
            callback.onFailure(errormsg(err));
        }
    });
}


function completeMFALogin(authData, callback) {
    cognitoUserSession.sendMFACode(authData.ConfirmCode, {
        onSuccess: function(result) {
            afterLoginSuccess(cognitoUserSession,result,callback)
        },
        onFailure: function(err) {
            callback.onFailure(errormsg(err));
        }
    },sessionUserAttributes );
}


function getCognitoUser(username) {
    let userData = {
        Username: username,
        Pool: userPool,
    };
    return new AmazonCognitoIdentity.CognitoUser(userData);
}