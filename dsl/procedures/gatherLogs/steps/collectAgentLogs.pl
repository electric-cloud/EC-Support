#############################################################################
#
# Copyright Electric-Cloud 2015
#
#############################################################################
use Cwd;

$[/plugins[EC-Admin]project/scripts/perlHeaderJSON]

#############################################################################
#
# Parameters
#
#############################################################################
my $agentList = "$[agents]";

#############################################################################
#
# Global Variables
#
#############################################################################
my $DEBUG=1;
my $logDir="$[/server/Electric Cloud/dataDirectory]/logs";
my $cwd = getcwd();

foreach my $agent (sort split(",", $agentList)) {

	# Testing agent existence
    #
	my($ok, $json)=InvokeCommander("IgnoreError SuppressLog", 'getResource', $agent);
    if (! $ok) {
    	printf("Agent '%s' does not exist!", $agent);
        $ec->setProperty("outcome", "warning");
        next;
    }

    # Testing if agent is running
    #
    if ($json->{responses}->[0]->{resource}->{agentState}->{state} ne "alive") {
    	printf("Agent '%s' is not running!\n", $agent);
        $ec->setProperty("outcome", "warning");
        next;
    }
    if ($json->{responses}->[0]->{resource}->{resourceDisabled} eq "1") {
    	printf("Agent '%s' is disabled!\n", $agent);
        $ec->setProperty("outcome", "warning");
        next;
    }

	# run step on remote agent so we can get the installDir
    $ec->createJobStep({
        subproject   => "/plugins/EC-FileOps/project",
        subprocedure => "Remote Copy - Native",
        jobStepName  => "Copy $agent",
        resourceName => $agent,
        actualParameter => [
         	{actualParameterName => 'sourceWorkspaceName',      value => "default"},
          	{actualParameterName => 'sourceResourceName',       value => "$agent"},
            {actualParameterName => 'sourceFile',               value => $ENV{COMMANDER_DATA}."/logs/agent/*agent.log"},
          	{actualParameterName => 'destinationResourceName',  value => "local"},
           	{actualParameterName => 'destinationFile',          value => "$[/myJob/destinationDirectory]/agents/$agent"},
           	{actualParameterName => 'destinationWorkspaceName', value => "default"},
           ],
    });
}

$[/plugins[EC-Admin]project/scripts/perlLibJSON]
