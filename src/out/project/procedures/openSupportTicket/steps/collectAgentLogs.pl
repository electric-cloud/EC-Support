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
           	{actualParameterName => 'destinationFile',          value => "$[/myJob/destinationDirectory]/$agent"},
           	{actualParameterName => 'destinationWorkspaceName', value => "default"},
           ],
    });
}

