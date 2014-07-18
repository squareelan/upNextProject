var sys = require('sys');

// Twilio Credentials 
var accountSid = 'AC891b3253bdbb4f0f3827604ee003e079'; 
var authToken = 'a02992da191f6296c39b6a9392bc61ac';
var tw_num = "+15128725696"; 
 
//require the Twilio module and create a REST client 
var client = require('twilio')(accountSid, authToken); 

exports.incoming = function(req, res) {
	var message = req.body.Body;
	var from = req.body.From;
	sys.log('From: ' + from + ', Message: ' + message);
	var twiml = '<?xml version="1.0" encoding="UTF-8" ?>\n<Response>\
		\n<Sms from="+18179622552" to="' + from + '">Thanks for using upNext! We will notify you when your table is ready.</Sms>\n</Response>';

	res.send(twiml, {'Content-Type':'text/xml'}, 200);
}

exports.sms = function(req, res) {
  var user_number = req.body.number;
  client.messages.create({ 
    to: user_number, 
    from: tw_num, 
    body: "Thank you for using upNext! We will notify you when your table is ready.",   
  }, function(err, message) { 
      console.log("sending msg"); 
  });
} 