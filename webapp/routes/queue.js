// Grab everything in this folder and require it.

var fs = require('fs');
var test = require('../debug');

exports.home = function(req, res){
	isDebug = test.debug;
    if (req.user){
	res.redirect('user-dashboard');
    } else if (isDebug) {
    	res.render('index');
    } else{
    	res.render('index_no_login');
    }
};

exports.template = function(req, res) {
    res.render('template');
};

exports.login = function(req, res){
    res.render('login');
};

exports.userDashboard = function(req, res) {
    res.render('user-dashboard_2', { user : req.user });
};

exports.bizDashboard = function(req, res) {
	res.render('biz-dashboard');
}

exports.partial = function(req, res) {
    var name = req.params.name;
    res.render('partials/' + name);
}
