var sys = require('sys');
// Listen on port 9001
var gith = require('gith').create( 9001 );
// Import exec, to run our bash script
var exec = require('child_process').exec;
var child;

gith({
    repo: 'apt-fall13/upNext'
}).on( 'all', function( payload ) {
	console.log( 'Post-receive happened!' );
    if( payload.branch === 'master' )
    {
    	child = exec('./hook.sh', function (error, stdout, stderr) {
		sys.print('stdout: ' + stdout);
		sys.print('stderr: ' + stderr);
		if (error !== null) {
			console.log('exec error: ' + error);
		}
});
    }
});