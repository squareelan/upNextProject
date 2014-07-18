var keys = require('../keys.secret.js');
var yelp = require('yelp').createClient({
  consumer_key: keys.yelpConsumerKey, 
  consumer_secret: keys.yelpSecretKey,
  token: keys.yelpToken,
  token_secret: keys.yelpTokenSecret
});

exports.yelpBusiness = function(req, res) {
	var business = req.query.bizName;
	console.log(business)
	yelp.business(business, function(error, data) {
  		if(error) {
  			res.json(error, 400);
  		} else {
  			res.json(data,200);
  		}
	});
}

exports.yelpSearch = function(req, res) {
	var keyword = req.query.term;
	var loc = req.query.location;
	console.log(keyword);
	console.log(loc);
	yelp.search({term: keyword, location: loc}, function(error, data) {
		if(error) {
			res.json(error, 400);
		} else {
			res.json(data, 200);
		}
	});
}

exports.yelpSearchGeo = function(req, res) {
	var keyword = req.query.term;
	var geoCode = req.query.geocode;
	yelp.search({term: keyword, ll: geoCode}, function(error, data) {
		if(error) {
			res.json(error, 400);
		} else {
			res.json(data, 200);
		}
	});
}

exports.two = function(req, res) {
	one = req.query.one;
	two = req.query.two;
	console.log(one);
	console.log(two);
	res.json()
}
