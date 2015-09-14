$[/plugins[EC-Admin]/project/scripts/perlHeaderJSON]

#
# Parameters
#
my $serverResources = "$[serverResources]";

#
# global variables
#
my $targetServerResource="$[/myJobStep/assignedResourceName]";
my %resourceHash=();

#
# Parse the list of servers
foreach my $server (split(",", $serverResources)) {
	$server =~ s/^\s*//;	# removing leading  spaces
    $server =~ s/\s*$//;	# removing trailing spaces
    
	#
	# Is this a pool?
	my ($ok, $json)=InvokeCommander("IgnoreError SuppressLog", 'getResourcePool', $server);
    if ($ok) {
    	printf("Processing pool   $server\n");
        foreach my $node($ec->getResourcesInPool($server)->findnodes("//resource")) {
        	my $resName=$node->{resourceName};
            printf("    processing server $resName\n");
            $resourceHash{$resName}=1;
        } 
        next;
    }
    
    #
    # Check if it's a valid resource
	my ($ok, $json)=InvokeCommander("IgnoreError SuppressLog", 'getResource', $server);
    if ($ok) {
    	printf("Processing server $server\n");
        $resourceHash{$server}=1;
    } else {
    	printf("$server is not a recognized pool or resource\n");
        exit(1);
    }
}

printf("\n");
foreach my $server (keys %resourceHash) {
	printf("Collecting logs for %s\n", $server);
	my ($ok, $json)=InvokeCommander("IgnoreError", 'getResource', $server);
    my $node=$json->{responses}->[0]->{resource};
    my $resName=$node->{resourceName};
    
    if (($node->{resourceDisabled} == 0) && ($node->{agentState}->{alive} == 1) ) {
    	$ec->createJobStep({
              subprocedure=>"sub-collectServerLogs",
              jobStepName => "collect-$resName",
              actualParameter => [
                {actualParameterName => "serverResource",       value => $resName},
                {actualParameterName => "destinationDirectory", value => "$[destinationDirectory]"},
                {actualParameterName => "targetServerResource", value => "$targetServerResource"},
              ]});

    }
}

$[/plugins[EC-Admin]/project/scripts/perlLibJSON]
