$[/plugins[EC-Admin]project/scripts/perlHeaderJSON]

my ($ok, $json)=InvokeCommander("", 'getVersions');

if ($ok) {
	my $version=$json->{responses}->[0]->{serverVersion}->{version};

	$ec->setProperty ("/myJob/serverVersion", $version);
	$ec->setProperty ("summary", "Server version: $version");
} else {
	$ec->setProperty ("/myJob/serverVersion", "");
	$ec->setProperty ("summary", "Cannot retrive server version");
	$ec->setProperty ("outcome", "warning");
	
}    
$[/plugins[EC-Admin]project/scripts/perlLibJSON]
