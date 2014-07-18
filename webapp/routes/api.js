/**
    api.js is where we implement our apis.
*/

var keys = require('../keys.secret.js');
var yelp = require('yelp').createClient({
  consumer_key: keys.yelpConsumerKey, 
  consumer_secret: keys.yelpSecretKey,
  token: keys.yelpToken,
  token_secret: keys.yelpTokenSecret
});

// Twilio Credentials 
var accountSid = 'AC891b3253bdbb4f0f3827604ee003e079'; 
var authToken = 'a02992da191f6296c39b6a9392bc61ac';
var tw_num = "+15128725696";

// MongoJS
var mongojs = require("mongojs");
var databaseUrl = "inqueue";
// TODO: clean collections later
var collections = ["business", "subscribers", "accounts", "bizs", "deltaBizs", "log", "emails", "campus", "test", "user"];
var mongo = mongojs(databaseUrl, collections);
var async = require('async');

var time = require('time');
// now.setTimezone("America/New_York");

exports.ping = function(req, res){
    res.json({"message":"pong"});
};

exports.getUser = function(req, res) {
    var _first = req.query.first;
    var _last = req.query.last;
    var _user = req.query.user;
    mongo.user.findOne({
        "_id": _user
    }, function(err, user) {
        if(err) {
            res.json(400, {"Message": "Error while getting user.", "Status": "Fail"});
        } else if(user === null) {
            console.log("Unable to find user. Making a new user");            
            mongo.user.insert({"first": _first, "last": _last, "point": 0 ,"history":[], "_id": _user},
                function(err, new_user) {
                    if(err || new_user === null) {
                        console.log("Unable to create a new user.");
                        res.json(400, {"Message": "Error while creating user.", "Status": "Fail"});
                    } else {
                        console.log("Created a new user.");
                        res.json(200, {"Message": "Welcome to upNext!", "User": new_user, "Status": "Success"});
                    }
                });
        } else {
            console.log("Found user.");
            res.json(200, {"Message": "Found user.", "User": user, "Status": "Success"}); 
        }
    });
}

exports.earnPoint = function(req, res) {
    var _userId = req.body.user;
    mongo.user.findAndModify({
        query:{"_id": mongojs.ObjectId(_userId)},
        update:{$inc:{point:1}}
    }, function(err, user) {
        if(err || user === null) {
            console.log("Error while performing earnPoint API.");
            res.json(400, {"Message": "Error while earnPoint.", "Status": "Fail"});  
        } else {
            console.log("Successfully earned point.");
            res.json(200, {"Message": "User earned point.", "Point": user.point+1 ,"Status": "Success"}); 
        }
    });
}

exports.reportWaitTime = function(req, res) {
    var bizId = req.query.id;
    var crowd = req.query.crowd;
    var wait = req.query.wait;
    var reportedBy = req.query.user;
    var busyness = req.query.busyness;    
    mongo.campus.findOne({"_id": bizId}, function(err, biz) {
        if(err || biz === null) {
            console.log("Unable to find business");
            res.json(400, {"Message": "Error while getting businesses.", "Status": "Fail"});
        } else {
            //think about how to handle wait time reporting
            var now = new time.Date();
            now.setTimezone("America/Chicago");
            // our linux box is reported UTC time... need to adjust to CST
            var report_time = now.getHours() + ":" + now.getMinutes();
            mongo.campus.findAndModify({
                query:{"_id":bizId},
                update:{$set:{"wait": wait, "lastReported": report_time, "crowd": crowd, "reportedBy": reportedBy, "busyness": busyness, "hearts": 0}}
            }, function(err, biz2) {
                if(err || biz2 === null) {
                    console.log("Unable to report wait time.");
                    res.json(400, {"Message": "Error while reporting wait time.", "Status": "Fail"});
                } else {
                    console.log("Successfully reported wait time.");
                    res.json(200, {"Message": "Successfully reported wait time.", "Status": "Success"});
                }
            });
        }
    });
}

exports.updateHearts = function(req, res) {
    var bizId = req.query.id;
    var plus = req.query.plus;
    mongo.campus.findOne({"_id": bizId}, function(err, biz) {
        if(err || biz === null) {
            console.log("Unable to find business");
            res.json(400, {"Message": "Error while getting businesses.", "Status": "Fail"});
        } else {
            if(plus === 'true') {
                mongo.campus.findAndModify({
                    query:{"_id":bizId},
                    update:{$inc:{"hearts": 1}}
                }, function(err, biz2) {
                    if(err || biz2 === null) {
                        console.log("Unable to update heart count.");
                        res.json(400, {"Message": "Error while reporting hearts.", "Status": "Fail"});
                    } else {
                        console.log("Successfully hearted <3.");
                        res.json(200, {"Message": "Successfully hearted.", "Status": "Success"});
                    }
                });
            } else if(plus === 'false') {
                mongo.campus.findAndModify({
                    query:{"_id":bizId},
                    update:{$inc:{"hearts": -1}}
                }, function(err, biz2) {
                    if(err || biz2 === null) {
                        console.log("Unable to update heart count.");
                        res.json(400, {"Message": "Error while reporting hearts.", "Status": "Fail"});
                    } else {
                        console.log("Successfully de-hearted <3.");
                        res.json(200, {"Message": "Successfully de-hearted.", "Status": "Success"});
                    }
                });
            }
        }
   });   
}

exports.recordHistory = function(req, res) {
    var user = req.query.user;
    var biz = req.query.biz;
    var date = req.query.date;
    // all reports are 1 pt
    mongo.user.findOne({"_id": user}, function(err, upnext) {
        if(err || upnext === null) {
            console.log("Unable to find user");
            res.json(400, {"Message": "Error while getting user.", "Status": "Fail"}); 
        } else {
            mongo.user.findAndModify({
                query:{"_id": user},
                update:{$push:{"history":{"date": date, "point": 1, "business": biz}}}
            }, function(err, upnext2) {
                if(err || upnext2 === null) {
                    console.log("Unable to recored history");
                    res.json(400, {"Message": "Error while recording history.", "Status": "Fail"});
                } else {
                    console.log("Successfully recorded history.");
                    res.json(200, {"Message": "Successfully recoreded history.", "Status": "Success"});
                }                
            });
        }
    });
}

exports.resetCampusBiz = function(req, res) {
    mongo.getCollectionNames(function(err, names) {
        //find campus collection
        if(names.indexOf('campus') !== -1) {
            mongo.campus.drop(function(err, dropped) {
                if(err || dropped === null) {
                    console.log("Error while wiping campus collection.");
                    res.json(400, {"Message": "Error while wiping campus collection.", "Status": "Fail"});
                } else {
                    console.log("Wiped campus collection.");
                    insertCampusBizs(res);
                }
            });
        } else {
            res.json(400, {"Message": "Already dropped.", "Status": "Fail"});
        }
    });
}

exports.resetUserPoint = function(req, res) {
    var first = req.param('first');
    var last = req.param('last');
    mongo.user.findAndModify({
        query:{"first": first, "last": last},
        update:{$set:{"point": 0, "history": []}}
    }, function(err, user) {
        if(err || user === null) {
            console.log("Unable to find user");
            res.json(400, {"Message": "Error while finding user.", "Status": "Fail"}); 
        } else {
            console.log("Successfully reset user point.");
            res.json(200, {"Message": "Successfully reset user point.", "Status": "Success"});
        }
    });
}

exports.emailSubscribe = function(req, res) {
    var email = req.query.email;
    mongo.emails.findOne({"email": email}, function(err, subscribers) {
        if(err || subscribers === null) {
            mongo.emails.insert({"email": email}, function(err, newUser) {
                if(err || newUser === null) {
                    console.log("problem adding a new email to our email subscription list");
                    res.json(400, {"Status":"Fail", "Message": "failed to add " + email + " to our list"});
                } else {
                    console.log("new email. Adding it to our email list.");
                    res.json(200, {"Status": "Success", "Message": "We'll be in touch! Look out for our weekly updates, " + email})
                }
            }); 
        } else {
            console.log(email + " is already subscribed!");
            res.json({"Status":"Fail", "Message": email + " is already subscribed!"})
        }
    });
}

exports.campus_biz = function(req, res) {
    var lat = req.query.lat;
    var lng = req.query.lng;
    // filter by name, location, category, wait time (hold)
    var _location = req.query.location;
    var _name = req.query.name;
    var _category = req.query.category;
    if(!_location && !_name && !_category){
        mongo.campus.find(function(err, business) {
            console.log("no param");
            if(err || business === null) {
                console.log("Error while getting nearby businesses.");
                res.json(400, {"Message": "Error while getting businesses.", "Status": "Fail"});
            } else {
                var distances = findDistances(lat, lng, business);
                var _business = nearestBusinesses(distances, business);
                var _distances = justDistances(distances);
                console.log("Returning nearby businesses.");
                res.json(200, {"Message": "nearby businesses.", "Businesses": _business, "Distances": _distances, "Size": business.length, "Status": "Success"});
            }
        });
    } else {
        if(_location !== undefined) {
            mongo.campus.find({"location": _location}, function(err, filtered) {
                if(err || filtered === null) {
                    console.log("Error while getting campus businesses.");
                    res.json(400, {"Message": "Error while getting businesses.", "Status": "Fail"});
                } else {
                    console.log("Returning filtered campus businesses.");
                    res.json(200, {"Message": "campus businesses.", "Businesses": filtered, "Size": filtered.length, "Status": "Success"});
                }
            });
        } else if(_name !== undefined) {
            mongo.campus.find({"name": _name}, function(err, filtered) {
                if(err || filtered === null) {
                    console.log("Error while getting campus businesses.");
                    res.json(400, {"Message": "Error while getting businesses.", "Status": "Fail"});
                } else {
                    console.log("Returning filtered campus businesses.");
                    res.json(200, {"Message": "campus businesses.", "Businesses": filtered, "Size": filtered.length, "Status": "Success"});
                }
            });
        } else {
            mongo.campus.find({"category": _category}, function(err, filtered) {
                if(err || filtered === null) {
                    console.log("Error while getting campus businesses.");
                    res.json(400, {"Message": "Error while getting businesses.", "Status": "Fail"});
                } else {
                    console.log("Returning filtered campus businesses.");
                    res.json(200, {"Message": "campus businesses.", "Businesses": filtered, "Size": filtered.length, "Status": "Success"});
                }
            });
        }
    }
}

//=====================================================================================================================
//                                                  Helper methods
function enqueueSMS(number, firstName) {
    //require the Twilio module and create a REST client 
    var client = require('twilio')(accountSid, authToken);
    client.messages.create({ 
        to: number,
        from: tw_num,
        body: firstName + ", Thank you for using upNext! We will notify you when your table is ready.",
    }, function(err, message) { 
        console.log(message.sid); 
    });
}

// Exponential Moving Average Computation
function EMA(input, numberOfInputs, avg) {
    var k = 2/(numberOfInputs +1);
    return input*k + avg*(1-k);
}

function filteredCampusBiz(type, keyword, res) {
    mongo.campus.find({t: keyword}, function(err, filtered) {
        if(err || filtered === null) {
            console.log("Error while getting campus businesses.");
            res.json(400, {"Message": "Error while getting businesses.", "Status": "Fail"});
        } else {
            console.log("Returning filtered campus businesses.");
            res.json(200, {"Message": "campus businesses.", "Businesses": filtered, "Size": filtered.length, "Status": "Success"});
        }
    });
}

function calcDistance(x1, y1, geocode) {
    var x2 = geocode[0];
    var y2 = geocode[1];
    var R = 6371; // Radius of the earth in km
    var dLat = (x2-x1)* Math.PI / 180;  // Javascript functions in radians
    var dLon = (y2-y1)* Math.PI / 180; 
    var a = Math.sin(dLat/2) * Math.sin(dLat/2) +
            Math.cos(x1* Math.PI / 180) * Math.cos(x2* Math.PI / 180) * 
            Math.sin(dLon/2) * Math.sin(dLon/2); 
    var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
    var d = R * c; // Distance in km
    return (d * 0.62137).toFixed(2);
}

function nearestBusinesses(distances, business) {
    var new_business = [];
    // correctly make business array based on index
    for(var i=0; i<distances.length; i++) {
        var index = distances[i][1];
        new_business.push(business[index]);
    }

    return new_business;
}

function findDistances(lat, lng, business) {
    var distances = [];
    for(var i=0; i< business.length; i++) {
        var dist = [];
        var _geocode = business[i].geocode;
        var distance = calcDistance(lat, lng, _geocode);
        dist.push(distance, i);
        distances.push(dist);
    }

    // now sort distances array
    return distances.sort();
}

function justDistances(distances) {
    var just = [];
    for(var i=0; i<distances.length; i++) {        
        just.push(distances[i][0]);
    }
    return just;
}

// Re-add all the elements
function insertCampusBizs(res) {
    mongo.campus.insert([
        {"_id": "jack-in-the-box-drag", "name": "Jack In the Box",  "category": "Fast Food", "location": "Drag", "wait": 0, "lastReported":"-1", "crowd": 0, "addresss":"2552 Guadalupe St Austin, TX 78705", "geocode":[30.290247, -97.741397], "shortAddress": "2552 Guadalupe St", "reportedBy": "-1", "hearts": 0, "busyness": "-1"},
        {"_id": "fuzzys-taco-drag", "name": "Fuzzy's Taco",  "category": "Mexican", "location": "Drag", "wait": 0, "lastReported":"-1", "crowd": 0, "addresss":"2522 Guadalupe St Austin, TX 78705", "geocode":[30.290012, -97.741406], "shortAddress": "2522 Guadalupe St", "reportedBy": "-1", "hearts": 0, "busyness": "-1"},
        {"_id": "torchys-taco-drag", "name": "Torchy's Taco",  "category": "Mexican", "location": "Drag", "wait": 0, "lastReported":"-1", "crowd": 0, "addresss":"2801 Guadalupe St Austin, TX 78705", "geocode":[30.294159, -97.742258], "shortAddress": "2801 Guadalupe St", "reportedBy": "-1", "hearts": 0, "busyness": "-1"},
        {"_id": "whataburger-drag", "name": "Whataburger",  "category": "Fast Food", "location": "Drag", "wait": 0, "lastReported":"-1", "crowd": 0, "addresss":"2800 Guadalupe St Austin, TX 78705", "geocode":[30.293454, -97.741902], "shortAddress": "2800 Guadalupe St Austin", "reportedBy": "-1", "hearts": 0, "busyness": "-1"},
        {"_id": "kismet-cafe-drag", "name": "Kismet Cafe",  "category": "Middle Eastern", "location": "Drag", "wait": 0, "lastReported":"-1", "crowd": 0, "addresss":"411 W 24th St, Austin, TX 78705", "geocode":[30.287857, -97.742264], "shortAddress": "411 W 24th St", "reportedBy": "-1", "hearts": 0, "busyness": "-1"},
        {"_id": "pita-pit-drag", "name": "Pita Pit",  "category": "Greek", "location": "Drag", "wait": 0,  "lastReported":"-1", "crowd": 0, "addresss":"2350 Guadalupe St Austin, TX 78705", "geocode":[30.287219, -97.741732], "shortAddress": "2350 Guadalupe St", "reportedBy": "-1", "hearts": 0, "busyness": "-1"},
        {"_id": "which-wich-drag", "name": "Which Wich?",  "category": "Sandwich", "location": "Drag", "wait": 0, "lastReported":"-1", "crowd": 0, "addresss":"2348 Guadalupe St Austin, TX 78705", "geocode":[30.287195, -97.741734], "shortAddress": "2348 Guadalupe St", "reportedBy": "-1", "hearts": 0, "busyness": "-1"},
        {"_id": "chipotle-drag", "name": "Chipotle",  "category": "Mexican", "location": "Drag", "wait": 0, "lastReported":"-1", "crowd": 0, "addresss":"2230 Guadalupe St Austin, TX 78705", "geocode":[30.285566, -97.741852], "shortAddress": "2230 Guadalupe St", "reportedBy": "-1", "hearts": 0, "busyness": "-1"},
        {"_id": "raising-canes-drag", "name": "Raising Cane's",  "category": "Fast Food", "location": "Drag", "wait": 0, "lastReported":"-1", "crowd": 0, "addresss":"413 W Martin Luther King Blvd Austin, TX 78701", "geocode":[30.281904, -97.742262], "shortAddress": "413 W Martin Luther King Blvd", "reportedBy": "-1", "hearts": 0, "busyness": "-1"},
        {"_id": "starbucks-drag", "name": "Starbucks Drag",  "category": "Coffee", "location": "Drag", "wait": 0, "lastReported":"-1", "crowd": 0, "addresss":"504 W 24th St Ste B Austin, TX 78705", "geocode":[30.287896, -97.742684], "shortAddress": "504 W 24th St", "reportedBy": "-1", "hearts": 0, "busyness": "-1"},
        {"_id": "starbucks-union", "name": "Starbucks Union",  "category": "Coffee", "location": "Union", "wait": 0, "lastReported":"-1", "crowd": 0, "addresss":"24th & Guadalupe Austin, TX 78705", "geocode":[30.287807, -97.741683], "shortAddress": "504 W 24th St", "reportedBy": "-1", "hearts": 0, "busyness": "-1"},
        {"_id": "quiznos-union", "name": "Quiznos",  "category": "Sandwich", "location": "Union", "wait": 0, "lastReported":"-1", "crowd": 0, "addresss":"2247 Guadalupe St Austin, TX 78712", "geocode":[30.285107, -97.741883], "shortAddress": "2247 Guadalupe St", "reportedBy": "-1", "hearts": 0, "busyness": "-1"},
        {"_id": "taco-bell-union", "name": "Taco Bell",  "category": "Mexican", "location": "Union", "wait": 0, "lastReported":"-1", "crowd": 0, "addresss":"2247 Guadalupe St Austin, TX 78712", "geocode":[30.285811, -97.741836], "shortAddress": "2247 Guadalupe St", "reportedBy": "-1", "hearts": 0, "busyness": "-1"},
        {"_id": "chick-fil-a-union", "name": "Chick-Fil-A Union",  "category": "Fast Food", "location": "Union", "wait": 0, "lastReported":"-1", "crowd": 0, "addresss":"2247 Guadalupe St Austin, TX 78712", "geocode":[30.285107, -97.741883], "shortAddress": "2247 Guadalupe St", "reportedBy": "-1", "hearts": 0, "busyness": "-1"},
        {"_id": "wendys-union", "name": "Wendy's",  "category": "Fast Food", "location": "Union", "wait": 0, "lastReported":"-1", "crowd": 0, "addresss":"2247 Guadalupe St Austin, TX 78712", "geocode":[30.285107, -97.741883], "shortAddress": "2247 Guadalupe St", "reportedBy": "-1", "hearts": 0, "busyness": "-1"},
        {"_id": "starbucks-sac", "name": "Starbucks SAC",  "category": "Coffee", "location": "SAC", "wait": 0, "lastReported":"-1", "crowd": 0, "addresss":"2201 Speedway University of Texas Student Activity Center Austin, TX 78712", "geocode":[30.284899, -97.737418], "shortAddress": "2201 Speedway", "reportedBy": "-1", "hearts": 0, "busyness": "-1"},
        {"_id": "taco-cabana-sac", "name": "Taco Cabana",  "category": "Mexican", "location": "SAC", "wait": 0, "lastReported":"-1", "crowd": 0, "addresss":"2201 Speedway University of Texas Student Activity Center Austin, TX 78712", "geocode":[30.284899, -97.737418], "shortAddress": "2201 Speedway", "reportedBy": "-1", "hearts": 0, "busyness": "-1"},
        {"_id": "chick-fil-a-sac", "name": "Chick-Fil-A SAC",  "category": "Fast Food", "location": "SAC", "wait": 0, "lastReported":"-1", "crowd": 0, "addresss":"2201 Speedway University of Texas Student Activity Center Austin, TX 78712", "geocode":[30.284899, -97.737418], "shortAddress": "2201 Speedway", "reportedBy": "-1", "hearts": 0, "busyness": "-1"},
        {"_id": "zen-sac", "name": "Zen",  "category": "Japanese", "location": "SAC", "wait": 0, "lastReported":"-1", "crowd": 0, "addresss":"2201 Speedway University of Texas Student Activity Center Austin, TX 78712", "geocode":[30.284899, -97.737418], "shortAddress": "2201 Speedway", "reportedBy": "-1", "hearts": 0, "busyness": "-1"}
        ],
    function(err, biz) {
        if(err || biz === null) {
            res.json(400, {"Message": "Campus business reset failed!", "Status": "Fail"});
        } else {
            res.json(200, {"Message": "Campus business reset complete!", "Status": "Success"});
        }
    });
}
