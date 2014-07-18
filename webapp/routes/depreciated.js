// Get the user-reviewed rating for a particular business.
exports.businessInfo = function(req, res){
    mongo.business.findOne({"name":req.query.name}, function(err, business){
        if(err || business === null){
            res.json(400, {"Error":"Unable to find business!"});
        } else {
            res.json({
                    "name":business.name,
                    "waitTime":business.rating,
                    "ratingLog":business.ratingLog,
                    "logSize":business.ratingLog.length
                });
        }
    });
}

// Averages the intput rating with a business' current rating and saves it
// to our database.
exports.rateBusiness = function(req, res) {
    var bizName = req.query.name;
    var rating = req.query.rating;

    mongo.business.findOne({"name":bizName}, function(err, business){
        // If business is not found, add it and insert a new rating.
        if(err || business === null){
            console.log("No business found when rating. Adding it in.");
            addBusiness(bizName, rating);
            res.json(400, {"Fail": "No business found, businss added."})
        } else {
            var newRating = computeAndSaveNewRating(business, rating);
            res.json({"Success": "New rating is " + newRating});
        }
    });
}

// Get all business using a given location and term. This basically filters out
// all the extraneous information from Yelps JSON result and attaches the 
// wait time from our database to each business.
// TODO: Rename "rating" to "waitTime" both here and in Mongo.
// TODO: Optimize this (seems kind of ineffiecient at the moment, but works).
exports.findNearbyBusinesses = function(req, res) {
    var qTerm = req.query.term;
    var qLocation = req.query.location;
    yelp.search({term: qTerm, location: qLocation}, function(err, data){
        if(err || data === null){
            res.json(400, {"Fail": "Could not find any results."});
        } else {
            console.log(data);
            var json = {};
            var businesses = data.businesses;
            var bizArray = [];
            async.map(
                businesses,
                function(business, callback) {
                    mongo.business.findOne({"name": business.id}, function(err, biz){
                        if(err || biz === null)
                            callback(null, null);
                        else
                            callback(null, biz);
                    });
                }, function(err, results){
                    for(var i in businesses){
                        var business = businesses[i];
                        var businessJson = {};

                        businessJson["name"] = business.name;
                        businessJson["id"] = business.id;
                        businessJson["categories"] = business.categories;
                        businessJson["location"] = business.location;
                        businessJson["rating_img_url_small"] = business.rating_img_url_small;
                        businessJson["review_count"] = business.review_count;
                        businessJson["display_phone"] = business.display_phone;
                        businessJson["waitTime"] = findBiz(results, business.id);

                        bizArray.push(businessJson);
                    }

                    json["businesses"] = bizArray;
                    json["size"] = businesses.length;
                    res.json(json);
                }
            );
        }
    });
}

exports.findBusinessGeo = function(req, res) {
    var keyword = req.query.term;
    var qGeoCode = req.query.geocode;
    yelp.search({term: keyword, ll: qGeoCode}, function(err, data) {
        if(err || data === null) {
            res.json(400, {"Status":"Fail", "Message": "Could not find any result."});
        } else {
            //same as fixed search
            var json = {};
            var businesses = data.businesses;
            var bizArray = [];
            async.map(
                businesses,
                function(business, callback) {
                    mongo.business.findOne({"name": business.id}, function(err, biz){
                        if(err || biz === null)
                            callback(null, null);
                        else
                            callback(null, biz);
                    });
                }, function(err, results){
                    for(var i in businesses){
                        var business = businesses[i];
                        var businessJson = _makeBusinessJson(business, results);
                        bizArray.push(businessJson);
                    }
                    json["businesses"] = bizArray;
                    json["size"] = businesses.length;
                    console.log(json);
                    res.json(json);
                }
            );
        }
    });
}

exports.enqueue = function(req, res) {
    var phoneNumber = req.query.number;
    var firstName = req.query.first;
    var lastName = req.query.last;
    var partySize = req.query.partySize;
    var id = req.query.id;
    var prod = req.query.prod;
    mongo.bizs.findOne({"_id": id}, function(err, biz) {
        if(err || biz === null) {
            console.log("Fail to find a business");
            res.json(400, {"Status":"Fail", "Message": "Cannot locate a business."});
        } else {
            if(!(findUser(biz, phoneNumber, firstName, lastName))) {
               mongo.bizs.findAndModify({
                //findAndModify can add attributes with $push or $set!
                query: {"_id": id},
                update: {$push:{queue: {
                            "phone": phoneNumber,
                            "last": lastName,
                            "first": firstName,
                            "partySize": partySize       
                        }},
                        $inc: {size: 1}}
                }, function(err, biz2) {
                    if(err || biz2 === null) {
                        console.log("Fail to enqueue an user.");
                        res.json(400, {"Status": "Failed", "Message": "Unable to enqueue."});
                    } else {
                        console.log("successfully enqueue an user");                    
                        if(prod != null) {
                            console.log("Sending SMS notificiation to user...");
                            enqueueSMS(phoneNumber, firstName);
                        } else{
                            console.log("not prod. will not send sms.");
                        }                    
                        res.json({"Status": "Success", "Message": "You are in queue! Enjoy your time elsewhere!"});
                    }
                }); 
            } else{
                console.log("User is already in this waitlist.");
                res.json(400, {"Status": "Failed", "Message": "Already in this waitlist."});
            }
        }
    });
}

exports.dequeue = function(req, res) {
    var phoneNumber = req.query.number;
    // var firstName = req.query.first;
    // var lastName = req.query.last;
    // var partySize = req.query.partySize;
    var id = req.query.id;
    mongo.bizs.findOne({"_id": id}, function(err, biz) {
        if(err || biz === null) {
            console.log("Fail to find a business.");
            res.json(400, {"Status":"Fail", "Message": "Cannot locate a business."});
        } else {
            if(biz.size > 0) {
                mongo.bizs.findAndModify({
                    query: {"_id": id},
                    update: {$pull:{queue:{
                                "phone":phoneNumber,
                                // "last": lastName,
                                // "first": firstName,
                                // "partySize": partySize
                            }},
                            $inc: {size: -1}}
                }, function(err, biz2) {
                    if(err || biz2 === null) {
                        console.log("Fail to dequeue an user.");
                        res.json(400, {"Status": "Fail", "Message": "Unable to dequeue"});
                    } else {
                        console.log("successfully dequeue an user");
                        mongo.deltaBizs.findAndModify({
                            query: {"_id": id},
                            update: {$push:{delta: {
                                        "phone": phoneNumber,
                                        // "last": lastName,
                                        // "first": firstName,
                                        // "partySize": partySize                                     
                                    }},
                                    $inc: {size: 1}}
                        }, function(err, deltaBiz) {
                            if(err || deltaBiz === null) {
                                console.log("Fail to add an user to deltaDB.");                        
                            } else {
                                console.log("successfully add an user to deltaDB.");                                        
                            }
                        });
                        res.json({"Status":"Success", "Message": "You are out of queue!"});
                    }
                });
            } else {
                console.log("No user to dequeue");
                res.json(400, {"Status": "Fail", "Message": "No one is in queue."});
            }
        }
    });
}

exports.myWaitList = function(req, res) {
    var id = req.query.id;
    mongo.bizs.findOne({"_id": id}, function(err, biz) {
        if(err || biz === null) {
            console.log("Fail to find a business.");
            res.json(400, {"Message": "Cannot locate a business.", "Status": "Fail"});
        } else {
            console.log("Returning a queue.");            
            res.json(200, {"Message": "Here is your waitlist!", "queue": biz.queue, "size": biz.size, "Status": "Success"});
        }
    });
}

// exports.userWaitList = function(req, res) {
//     var id = req.query.id;
//     var lastName= req.query.last;
//     var firstName = req.query.first;
//     var phoneNumber = req.query.number;
//     mongo.bizs.findOne({"_id": id}, function(err, biz){
//         if(err || biz === null) {
//             console.log("Fail to find a business.");
//             res.json(400, {"Message": "Cannot locate a business.", "Status": "Fail"});
//         } else {
//             console.log("Found business.");
//             mongo.bizs.findOne(
//                 {"_id": id}, 
//                 {"queue": {$elemMatch:{"phone": phoneNumber, "first": firstName, "last": lastName}}}, 
//                 function(err2, position) {
//                     if(err2 || position === null) {
//                         console.log("Fail to user's position in a business queue.");
//                         res.json(400, {"Message": "Cannot locate a user.", "Status": "Fail"});
//                     } else {
//                         console.log("Returning a position.");            
//                         res.json(200, {"Message": "Here is your position!", "Position": position, "Status": "Success"});
//                     }
//             });
//         }
//     });
// }

exports.userPosition = function(req, res) {
    var success = false;
    var pos = -1;
    var id = req.query.id;
    var lastName= req.query.last;
    var firstName = req.query.first;
    var phoneNumber = req.query.number;
    mongo.bizs.findOne({"_id": id}, function(err, biz) {
        if(err || biz === null) {
            console.log("Fail to find a business.");
            res.json(400, {"Message": "Cannot locate a business.", "Status": "Fail"});
        } else {
            console.log("Found business.");
            var queueArray = biz.queue;
            for(var i = 0; i < queueArray.length; i++) {
                var element = queueArray[i];
                var number = element.phone;
                var first = element.first;
                var last = element.last;
                if(number === phoneNumber && first === firstName && last === lastName) {
                    // this will break out finding the first match.
                    success = true;
                    pos = i+1;
                    break;                    
                }
            }
            if(success) {
                res.json(200, {"Status": "Success", "Total": queueArray.length, "Position": pos, 
                    "bizName": biz.name,"Message": "Here is your position"});
            } else {
                res.json(400, {"Status": "Fail", "Message": "Unable to get user's position"});
            }            
        }
    });
}

exports.delta = function(req, res) {
    var id = req.query.id;
    console.log(id);
    mongo.deltaBizs.findOne({"_id": id}, function(err, deltaBiz) {
        if(err || deltaBiz === null) {
            console.log("Fail to find delta.");
            res.json(400, {"Message": "Cannot find delta", "Status": "Fail"});
        } else {
            console.log("Returning delta.");
            res.json(200, {"Message": "Returning delta.", "deltaBiz": deltaBiz.delta, "size": deltaBiz.size, "Status": "Success"});
        }
    });
}

// returns upNext Business customers
exports.upNextBizs = function(req, res) {
    mongo.bizs.find({},function(err, biz) {
        if(err || biz === null) {
            console.log("Problem loading upNext business customers.");
            res.json(400, {"Status":"Fail", "Message": "failed to load upNext Businesses."});
        } else {
            console.log("Found upNext business customers.");
            var bizNameArray = []
            for(var i=0; i<biz.length; i++) {
                var bizName = biz[i].name;
                bizNameArray.push(bizName);
            }
            res.json(200, {"Status": "Success", "BusinessNames": bizNameArray});
        }
    });
}

//========================================================================================================================
// compute the average and save the result.
function computeAndSaveNewRating(biz, rating){
    averagedRating = (parseFloat(biz.rating) + parseFloat(rating)) / 2.0;
    mongo.business.findAndModify({
            query: { name: biz.name },
            update: { $set: { rating: averagedRating },
                      $push: { ratingLog: rating } }
        }, function(err, result){});
    return averagedRating;
}

// Helper method to add a particular business to our database.
function addBusiness(name, rating){
    mongo.business.insert({"name":name,
        "rating":rating,
        "ratingLog": [rating]},
        function(err, business){
            if(err || business === null){
                return; 
            } else {
                console.log("Business " + name + " successfully added."); 
                return;
            }
        });
}

function _makeBusinessJson(business, results) {
    var bizJson = {};
    bizJson["name"] = business.name;
    bizJson["id"] = business.id;
    bizJson["categories"] = business.categories;
    bizJson["location"] = business.location;
    bizJson["rating_img_url_small"] = business.rating_img_url_small;
    bizJson["review_count"] = business.review_count;
    bizJson["display_phone"] = business.display_phone;
    bizJson["waitTime"] = findBiz(results, business.id);
    return bizJson;
}

// Search for the wait time results.
function findBiz(arr, id) {
    for(var i in arr){
        if(arr[i] !== null && arr[i].name === id)
            return parseInt(arr[i].rating);
    }
    return -1;
}

function findUser(biz, phoneNumber, firstName, lastName) {
    var queueArray = biz.queue;
    for(var i = 0; i < queueArray.length; i++) {
        var element = queueArray[i];
        var number = element.phone;
        var first = element.first;
        var last = element.last;
        if(number === phoneNumber && first === firstName && last === lastName) {
            return true;
        }
    }
    return false;
}

// Record a log given by a particular device.
// ************ DEPRECATED FOR NOW *************
exports.log = function(req, res) {
    var log = req.body;
    var device = log.device;
    var messages = log.messages;

    mongo.log.insert({"device":device, "timestamp":new Date(),"messages":messages},
        function(err, log) {
            if(err || log === null){
                res.json(400, {"Fail":"Unable to add log!"});   
            } else {
                res.json({"Success":"Successfuly added log for device " + device});   
            }
        });
}

// Retrieves the most recent log using the "_id" field. Since the ObjectID 
// is constructed using the current date, we can sort it in decending order
// and retrieve the first object.
// ************ DEPRECATED FOR NOW *************
exports.mostRecentLog = function(req, res) {
    mongo.log.find().sort({"_id":-1}).limit(1, function(err, log){
        if(err || log === null){
            res.json(400, {"Fail":"Unable to find the most recent log!"});
        } else {
            res.json({"device":log.device, "timestamp":log.timestamp, "messages":log.messages}); 
        }
    });
}
