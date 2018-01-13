var express = require('express');
var bodyParser = require('body-parser');
var request = require('request');
var AWS = require('aws-sdk');
var Hashids = require('hashids');
var bcrypt = require('bcrypt-nodejs');
var path = require('path');
require('log-timestamp');

var app = express();
app.use(bodyParser.json());
var urlencodedParser = bodyParser.urlencoded({ extended: false })

var region = "us-west-1";
var accessKeyId = "AKIAJCG7NWGGQTB4RV2A";
var secretAccessKey = "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";
var secretHashSalt = "voBxXOCwSmjtGHYk6mVVzFI2Yr9gbf";

AWS.config.update({
	region: region,
	    accessKeyId: accessKeyId,
	    secretAccessKey: secretAccessKey
	    });

var dynamoDB = new AWS.DynamoDB.DocumentClient();
app.use('/master.css', express.static(__dirname + "/master.css"));

// -------------------------------------------------------

// Signs in hosts with email and password
app.post('/', urlencodedParser, function(req, res){
    console.log("POST: Received an authentication request...");

	console.log(req);
    var username = req.body.username
    var password = req.body.password
	console.log("Encrypted password should be " + bcrypt.hashSync(password));
    var params = {
        TableName: "grouper-admin",
        Key: {
            "username": username
        }
    };
    dynamoDB.get(params, function(err, data){
        if (err) {
            console.log(err);
            console.log("ERROR: Could not find user: " + username);
            res.status(503).send({status: "Error", description: "Invalid Key.", field: "username", value: username});
        } else {
            console.log(data.Item);
            if (data.Item == undefined) {
				console.log("ERROR: Could not find user: " + username);
	            res.status(503).send({status: "Error", description: "Invalid Key.", field: "username", value: username});
            } else {
                console.log("POST: Found login details for user: " + username);
                if (bcrypt.compareSync(password, data.Item.password)) {
                    console.log("Successfully authenticated user: " + username);
                    res.status(200).sendFile(path.join(__dirname+"/internal.html"));
                } else {
                    console.log("POST: Error authenticating user: " + username + ". Incorrect password.");
                    res.status(503).sendFile(path.join(__dirname+"/failure.html"));
                }
            }
        }
    });
});

// run --------------------------------------------------

app.listen(8080, function(){
    console.log("Listening on port 8080...");
});
