#############################################################################
#
# Copyright Electric-Cloud 2018
#
#     Licensed under the Apache License, Version 2.0 (the "License");
#     you may not use this file except in compliance with the License.
#     You may obtain a copy of the License at
#
#         http://www.apache.org/licenses/LICENSE-2.0
#
#     Unless required by applicable law or agreed to in writing, software
#     distributed under the License is distributed on an "AS IS" BASIS,
#     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#     See the License for the specific language governing permissions and
#     limitations under the License.
#
# History
# ---------------------------------------------------------------------------
# 2018-Sep-28 lrochette Initial version
#

#############################################################################
use Cwd;

$[/plugins[EC-Admin]project/scripts/perlHeaderJSON]

#############################################################################
#
# Parameters
#
#############################################################################
my $repoList = "$[repo]";

#############################################################################
#
# Global Variables
#
#############################################################################
my $DEBUG=1;
# assuming same directory for installation
my $logDir="$[/server/Electric Cloud/dataDirectory]/logs";
my $cwd = getcwd();

foreach my $repo (sort split(",", $repoList)) {

	# Testing repo existence
    #
	my($ok, $json)=InvokeCommander("IgnoreError SuppressLog", 'getResource', $repo);
    if (! $ok) {
    	printf("Repo agent '%s' does not exist!", $repo);
        $ec->setProperty("outcome", "warning");
        next;
    }

    # Testing if agent is running
    #
    if ($json->{responses}->[0]->{resource}->{agentState}->{state} ne "alive") {
    	printf("Repo agent '%s' is not running!\n", $repo);
        $ec->setProperty("outcome", "warning");
        next;
    }
    if ($json->{responses}->[0]->{resource}->{resourceDisabled} eq "1") {
    	printf("Repo agent '%s' is disabled!\n", $repo);
        $ec->setProperty("outcome", "warning");
        next;
    }

	# run step on remote agent so we can get the installDir
    $ec->createJobStep({
        subproject   => "/plugins/EC-FileOps/project",
        subprocedure => "Remote Copy - Native",
        jobStepName  => "Copy-Repo $repo",
        resourceName => $repo,
        actualParameter => [
         	{actualParameterName => 'sourceWorkspaceName',      value => "default"},
        	{actualParameterName => 'sourceResourceName',       value => "$repo"},
          {actualParameterName => 'sourceFile',               value => $ENV{COMMANDER_DATA}."/logs/repository/*.log"},
        	{actualParameterName => 'destinationResourceName',  value => "local"},
         	{actualParameterName => 'destinationFile',          value => "$[/myJob/destinationDirectory]/repo/$repo"},
         	{actualParameterName => 'destinationWorkspaceName', value => "default"},
           ],
    });
}

$[/plugins[EC-Admin]project/scripts/perlLibJSON]
