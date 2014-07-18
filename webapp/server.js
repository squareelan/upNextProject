// use express for routing
var express = require('express')
  , http = require ('http')
  , app = module.exports = express()
  , server = http.createServer(app);

var passport = require('passport')
  , flash = require('connect-flash')
  , LocalStrategy = require('passport-local').Strategy;

var sys = require('sys');

var queue = require('./routes/queue');
var api = require('./routes/api');
var yelp = require('./routes/yelp');
var twilio = require('./routes/twilio');
var keys = require('./keys.secret');

// connect to mongoDB
var databaseUrl = "inqueue"; /** CHANGE TO INQUEUE*/
var collections = ["accounts", "bizaccounts", "subscribers", "bizs"]; /** CHANGE TO USERS*/
app.db = require('mongojs').connect(databaseUrl, collections);

// set up static routes
app.configure(function(){
    app.set('views', __dirname + '/views');
    app.set('view engine', 'jade');
    app.use(express.logger());
    app.use(express.bodyParser());
    app.use(express.methodOverride());
    app.use(express.cookieParser());
    app.use(express.session({ secret: 'secret' }));
    app.use(express.static(__dirname + '/public'));
    app.use(flash());
    app.use(passport.initialize());
    app.use(passport.session());
    app.use(app.router);
});

passport.serializeUser(function(user, done) {
  done(null, user);
});

passport.deserializeUser(function(obj, done) {
  done(null, obj);
});

passport.use(new LocalStrategy(
  function(username, password, done) {
    app.db.bizs.findOne({ name: username }, function(err, user) {
      if(err) {return done(err);}
      if(!user) {
        return done(null, false, {message: 'Incorrect username.'});
      }
      if(user.password != password) {
        return done(null, false, {message: 'Incorrect password.'});
      }

      console.log("found user.");
      return done(null, user);
    });
  }
));

// Website routing URLs
app.get('/', queue.home);

// API GET URLs
app.get('/api/ping', api.ping);
app.get('/api/email-subscribe/', api.emailSubscribe);
app.get('/api/campus_biz', api.campus_biz);
app.get('/api/reportWaitTime', api.reportWaitTime);
app.get('/api/getUser', api.getUser);
app.get('/api/updateHearts', api.updateHearts);
app.get('/api/recordHistory', api.recordHistory);

// API Twilio
app.post('/twilio', twilio.sms);
app.post('/incoming', twilio.incoming);

//PassportJS get 
app.get('/loginfail', function(req, res){
    res.json(403, {message: 'Invalid username/password'});
});
app.get('/loginSuccess', function(req, res) {
  res.json(200, {message: 'Login Success!'});
});

// API POST URLs
app.post('/api/resetCampusBiz', api.resetCampusBiz);
app.post('/api/earnPoint', api.earnPoint);
app.post('/api/resetUserPoint', api.resetUserPoint);
app.post('/login',
  passport.authenticate('local', {failureRedirect: '/loginfail'}),
    function(req, res) {
      res.json(200, {message: 'Login Success!'});
    }
);

// start server
// aws -> 80 default http connection
server.listen(80);
console.log('Listening on port 80');
