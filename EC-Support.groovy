
project 'EC-Support', {
  description = 'Interaction with Support'
  resourceName = ''
  workspaceName = ''

  procedure 'AddLogsToExistingTicket', {
    description = 'A procedure to automatically add the required logs to ShareFile and comment on an existing ticket'
    jobNameTemplate = ''
    resourceName = 'local'
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workspaceName = ''

    formalParameter 'agents', defaultValue: '', {
      description = 'a commas separated list of agents. Will be used to get the logs from the agents involved in the issue'
      expansionDeferred = '0'
      label = null
      orderIndex = null
      required = '0'
      type = 'entry'
    }

    formalParameter 'jobNumber', defaultValue: '', {
      description = 'The ID of the job that generated the error. Will be used to collect the right logs'
      expansionDeferred = '0'
      label = null
      orderIndex = null
      required = '0'
      type = 'entry'
    }

    formalParameter 'product', defaultValue: 'electricflow', {
      description = 'the name of the product that failed'
      expansionDeferred = '0'
      label = null
      orderIndex = null
      required = '1'
      type = 'select'
    }

    formalParameter 'serverResources', defaultValue: 'local', {
      description = 'A list of resources or pools, comma separated'
      expansionDeferred = '0'
      label = null
      orderIndex = null
      required = '0'
      type = 'entry'
    }

    formalParameter 'sharefileConfiguration', defaultValue: 'sharefile', {
      description = 'Name of your Sharefile configuration'
      expansionDeferred = '0'
      label = null
      orderIndex = null
      required = '1'
      type = 'entry'
    }

    formalParameter 'sharefileUploadDirectory', defaultValue: '', {
      description = 'Private directory on ShareFile on where to upload the logs'
      expansionDeferred = '0'
      label = null
      orderIndex = null
      required = '1'
      type = 'entry'
    }

    formalParameter 'ticketComment', defaultValue: '', {
      description = 'The comment to add to the ticket along the files'
      expansionDeferred = '0'
      label = null
      orderIndex = null
      required = '0'
      type = 'textarea'
    }

    formalParameter 'ticketId', defaultValue: '', {
      description = ''
      expansionDeferred = '0'
      label = null
      orderIndex = null
      required = '1'
      type = 'entry'
    }

    formalParameter 'time', defaultValue: '', {
      description = 'The time at which the issue happened. Used to collect the right logs. If empty, will simply send the commander.log'
      expansionDeferred = '0'
      label = null
      orderIndex = null
      required = '0'
      type = 'entry'
    }

    formalParameter 'zendeskConfiguration', defaultValue: 'zendesk', {
      description = 'The name of your Zendesk configuration'
      expansionDeferred = '0'
      label = null
      orderIndex = null
      required = '1'
      type = 'entry'
    }

    step 'Init', {
      description = 'Assign ticketId to global job property'
      alwaysRun = '0'
      broadcast = '0'
      command = '''$[/plugins[EC-Admin]project/scripts/perlHeaderJSON]

$ec->setProperty("/myJob/zendesk/ticketId", "$[ticketId]");


'''
      condition = ''
      errorHandling = 'failProcedure'
      exclusiveMode = 'none'
      logFileName = ''
      parallel = '0'
      postProcessor = ''
      precondition = ''
      releaseMode = 'none'
      resourceName = ''
      shell = 'ec-perl'
      subprocedure = null
      subproject = null
      timeLimit = ''
      timeLimitUnits = 'minutes'
      workingDirectory = ''
      workspaceName = ''
    }

    step 'grabResource', {
      description = 'Grab one of the commander server resources (in case of cluster)'
      alwaysRun = '0'
      broadcast = '0'
      command = '''ectool setProperty /myJob/assignedServerResource --value $[/myJobStep/assignedResourceName]
'''
      condition = ''
      errorHandling = 'failProcedure'
      exclusiveMode = 'none'
      logFileName = ''
      parallel = '0'
      postProcessor = ''
      precondition = ''
      releaseMode = 'none'
      resourceName = ''
      shell = ''
      subprocedure = null
      subproject = null
      timeLimit = ''
      timeLimitUnits = 'minutes'
      workingDirectory = ''
      workspaceName = ''
    }

    step 'grabDestinationDir', {
      description = ''
      alwaysRun = '0'
      broadcast = '0'
      command = '''#############################################################################
#
# Copyright Electric-Cloud 2015
#
#############################################################################
use Cwd;

$[/plugins[EC-Admin]project/scripts/perlHeaderJSON]

#############################################################################
#
# Global Variables
#
#############################################################################
my $cwd = getcwd();

$ec->setProperty("/myJob/destinationDirectory", "$cwd/$[/myJob/zendesk/ticketId]");
'''
      condition = ''
      errorHandling = 'failProcedure'
      exclusiveMode = 'none'
      logFileName = ''
      parallel = '0'
      postProcessor = ''
      precondition = ''
      releaseMode = 'none'
      resourceName = ''
      shell = 'ec-perl'
      subprocedure = null
      subproject = null
      timeLimit = ''
      timeLimitUnits = 'minutes'
      workingDirectory = ''
      workspaceName = ''
    }

    step 'createTicketDirectory', {
      description = 'create a directory to collect the logs'
      alwaysRun = '0'
      broadcast = '0'
      command = null
      condition = ''
      errorHandling = 'failProcedure'
      exclusiveMode = 'none'
      logFileName = null
      parallel = '0'
      postProcessor = null
      precondition = ''
      releaseMode = 'none'
      resourceName = '$[/myJob/assignedServerResource]'
      shell = null
      subprocedure = 'CreateDirectory'
      subproject = '/plugins/EC-FileOps/project'
      timeLimit = ''
      timeLimitUnits = 'minutes'
      workingDirectory = null
      workspaceName = ''
      actualParameter '''Path''', '''$[/myJob/destinationDirectory]'''
    }

    step 'collectServerLogs', {
      description = ''
      alwaysRun = '0'
      broadcast = '0'
      command = null
      condition = ''
      errorHandling = 'failProcedure'
      exclusiveMode = 'none'
      logFileName = null
      parallel = '0'
      postProcessor = null
      precondition = ''
      releaseMode = 'none'
      resourceName = ''
      shell = null
      subprocedure = 'collectServerLogs'
      subproject = ''
      timeLimit = ''
      timeLimitUnits = 'minutes'
      workingDirectory = null
      workspaceName = ''
      actualParameter '''destinationDirectory''', '''$[/myJob/destinationDirectory]'''
      actualParameter '''jobNumber''', '''$[jobNumber]'''
      actualParameter '''serverResources''', '''$[serverResources]'''
      actualParameter '''time''', '''$[time]'''
    }

    step 'collectAgentLogs', {
      description = 'If agent list is not empty, go grab agent.log and jagent.log'
      alwaysRun = '0'
      broadcast = '0'
      command = '''#############################################################################
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
	my($ok, $json)=InvokeCommander("IgnoreError SuppressLog", \'getResource\', $agent);
    if (! $ok) {
    	printf("Agent \'%s\' does not exist!", $agent);
        $ec->setProperty("outcome", "warning");
        next;
    }
    
    # Testing if agent is running
    #
    if ($json->{responses}->[0]->{resource}->{agentState}->{state} ne "alive") {
    	printf("Agent \'%s\' is not running!\\n", $agent);
        $ec->setProperty("outcome", "warning");
        next;
    }
    if ($json->{responses}->[0]->{resource}->{resourceDisabled} eq "1") {
    	printf("Agent \'%s\' is disabled!\\n", $agent);
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
         	{actualParameterName => \'sourceWorkspaceName\',      value => "default"},
          	{actualParameterName => \'sourceResourceName\',       value => "$agent"},
            {actualParameterName => \'sourceFile\',               value => $ENV{COMMANDER_DATA}."/logs/agent/*agent.log"},
          	{actualParameterName => \'destinationResourceName\',  value => "local"},
           	{actualParameterName => \'destinationFile\',          value => "$[/myJob/destinationDirectory]/$agent"},
           	{actualParameterName => \'destinationWorkspaceName\', value => "default"},
           ],
    });
}

$[/plugins[EC-Admin]project/scripts/perlLibJSON]

'''
      condition = '$[/javascript "$[agents]" != "" ]'
      errorHandling = 'failProcedure'
      exclusiveMode = 'none'
      logFileName = ''
      parallel = '0'
      postProcessor = ''
      precondition = ''
      releaseMode = 'none'
      resourceName = ''
      shell = 'ec-perl'
      subprocedure = null
      subproject = null
      timeLimit = ''
      timeLimitUnits = 'minutes'
      workingDirectory = ''
      workspaceName = ''
    }

    step 'listing', {
      description = 'Get the list of collected files'
      alwaysRun = '0'
      broadcast = '0'
      command = '''#############################################################################
#
# Copyright Electric-Cloud 2015
#
#############################################################################
$[/plugins[EC-Admin]project/scripts/perlHeaderJSON]

#############################################################################
#
# Parameters
#
#############################################################################

#############################################################################
#
# Global Variables
#
#############################################################################
my $DEBUG=1;
my $destDir="$[/myJob/zendesk/ticketId]";


my $content=directoryContent($destDir);
$ec->setProperty("/myJob/fileList", $content);

#############################################################################
#
# directoryContent: return a string containing the list of files
#                   in a directory
#############################################################################
sub directoryContent {
    my $dir=shift @_;
    my $content="";
    
    opendir(my $dh, $dir) or die("Cannot open the directory $dir\\n$!");

	while (my $file = readdir($dh)) {
    	next if $file eq \'.\' or $file eq \'..\';
    	if (-d "$dir/$file") {
    		$content .= directoryContent("$dir/$file");	
        } else {
        	$content .= "$dir/$file\\n";
            printf("$dir/$file\\n") if ($DEBUG);
        }
    }
    closedir($dh);
    return $content;
}
    
'''
      condition = ''
      errorHandling = 'failProcedure'
      exclusiveMode = 'none'
      logFileName = ''
      parallel = '0'
      postProcessor = ''
      precondition = ''
      releaseMode = 'none'
      resourceName = '$[/myJob/assignedServerResource]'
      shell = 'ec-perl'
      subprocedure = null
      subproject = null
      timeLimit = ''
      timeLimitUnits = 'minutes'
      workingDirectory = ''
      workspaceName = ''
    }

    step 'createBundle', {
      description = 'Zip the different files'
      alwaysRun = '0'
      broadcast = '0'
      command = null
      condition = ''
      errorHandling = 'failProcedure'
      exclusiveMode = 'none'
      logFileName = null
      parallel = '0'
      postProcessor = null
      precondition = ''
      releaseMode = 'none'
      resourceName = ''
      shell = null
      subprocedure = 'Create Zip File'
      subproject = '/plugins/EC-FileOps/project'
      timeLimit = ''
      timeLimitUnits = 'minutes'
      workingDirectory = null
      workspaceName = ''
      actualParameter '''sourceFile''', '''$[/myJob/destinationDirectory]'''
      actualParameter '''zipFile''', '''$[/myJob/destinationDirectory].zip'''
    }

    step 'uploadBundleToSharefile', {
      description = ''
      alwaysRun = '0'
      broadcast = '0'
      command = null
      condition = ''
      errorHandling = 'failProcedure'
      exclusiveMode = 'none'
      logFileName = null
      parallel = '0'
      postProcessor = null
      precondition = ''
      releaseMode = 'none'
      resourceName = ''
      shell = null
      subprocedure = 'CreateFolderAndUploadFile'
      subproject = '/plugins/EC-ShareFile/project'
      timeLimit = ''
      timeLimitUnits = 'minutes'
      workingDirectory = null
      workspaceName = ''
      actualParameter '''company''', '''electric-cloud'''
      actualParameter '''config''', '''$[ShareFileConfiguration]'''
      actualParameter '''folderToCreate''', '''$[sharefileUploadDirectory]/$[/myJob/zendesk/ticketId]'''
      actualParameter '''pathToFile''', '''$[/myJob/destinationDirectory].zip'''
    }

    step 'commentForFiles', {
      description = ''
      alwaysRun = '0'
      broadcast = '0'
      command = null
      condition = '''$[/javascript
  if ( "$[/server/settings/ipAddress]" == "ec54" ) {
    setProperty("/myJob/zendesk/ticketId", "114520");
   setProperty("summary", "Debug mode: 114520");
   false;
  } else {
   true;
  } ]'''
      errorHandling = 'failProcedure'
      exclusiveMode = 'none'
      logFileName = null
      parallel = '0'
      postProcessor = null
      precondition = ''
      releaseMode = 'none'
      resourceName = ''
      shell = null
      subprocedure = 'commentOnTicket'
      subproject = '/plugins/EC-Zendesk/project'
      timeLimit = ''
      timeLimitUnits = 'minutes'
      workingDirectory = null
      workspaceName = ''
      actualParameter '''comment''', '''$[ticketComment]

The following file have been uploaded  $[sharefileUploadDirectory]/$[/myJob/zendesk/ticketId]/$[/myJob/zendesk/ticketId].zip
It contains:

$[/myJob/fileList]'''
      actualParameter '''credential''', '''$[zendeskConfiguration]'''
      actualParameter '''ticketNumber''', '''$[/myJob/zendesk/ticketId]'''
    }

    // Custom properties

    property 'ec_customEditorData', {

      // Custom properties

      property 'parameters', {

        // Custom properties

        property 'ShareFileCredentialName', {

          // Custom properties
          formType = 'standard'
        }

        property 'agents', {

          // Custom properties
          formType = 'standard'
        }

        property 'jobNumber', {

          // Custom properties
          formType = 'standard'
        }

        property 'product', {

          // Custom properties

          property 'options', {

            // Custom properties

            property 'option1', {

              // Custom properties
              text = 'ElectricCommander'

              property 'value', value: 'electriccommander', {
                expandable = '1'
              }
            }

            property 'option2', {

              // Custom properties
              text = 'ElectricFlow'

              property 'value', value: 'electricflow', {
                expandable = '1'
              }
            }
            optionCount = '2'
            type = 'list'
          }
          formType = 'standard'
        }

        property 'serverResources', {

          // Custom properties
          formType = 'standard'
        }

        property 'sharefileConfiguration', {

          // Custom properties
          formType = 'standard'
        }

        property 'sharefileUploadDirectory', {

          // Custom properties
          formType = 'standard'
        }

        property 'stepId', {

          // Custom properties
          formType = 'standard'
        }

        property 'ticketComment', {

          // Custom properties
          formType = 'standard'
        }

        property 'ticketDescription', {

          // Custom properties
          formType = 'standard'
        }

        property 'ticketId', {

          // Custom properties
          formType = 'standard'
        }

        property 'ticketTitle', {

          // Custom properties
          formType = 'standard'
        }

        property 'time', {

          // Custom properties
          formType = 'standard'
        }

        property 'version', {

          // Custom properties
          formType = 'standard'
        }

        property 'zendeskConfiguration', {

          // Custom properties
          formType = 'standard'
        }

        property 'zendeskCredential', {

          // Custom properties
          formType = 'standard'
        }
      }
    }
    ec_parameterForm = '''<editor>
    <formElement> 
        <label>Ticket ID</label> 
        <property>ticketId</property> 
        <documentation>The ID of your existing ticket</documentation> 
        <type>entry</type>
        <required>1</required>
    </formElement> 

    <formElement> 
        <label>Comment</label> 
        <property>ticketComment</property> 
        <documentation>A comment to add to the ticket along the logs.</documentation> 
        <type>textarea</type>
    </formElement> 

    <formElement> 
        <label>Product</label> 
        <property>product</property> 
        <documentation>The name of the product with which you have an issue.</documentation> 
        <type>select</type> 
        <option> 
            <name>ElectricCommander</name> 
            <value>electriccommander</value> 
        </option> 
        <option> 
            <name>ElectricFlow</name> 
            <value>electricflow</value> 
        </option> 
        <value>electriccommander</value> 
        <required>1</required>
    </formElement> 

    <formElement>
        <label>Server Resources</label>
        <property>serverResources</property>
         <documentation>A list of resources -comma separated- or pools to represent your server list. Change the default value for cluster mode.</documentation> 
        <type>entry</type>
        <required>1</required>
        <value>local</value>       
    </formElement> 

    <formElement> 
        <label>Zendesk configuration</label> 
        <property>zendeskConfiguration</property> 
        <documentation>The name of the Zendesk configuration to use.</documentation> 
        <type>entry</type>
        <value>zendesk</value>
        <required>1</required>
    </formElement> 
    
    <formElement> 
        <label>ShareFile configuration</label> 
        <property>sharefileConfiguration</property> 
        <documentation>The name of the ShareFile configuration to use.</documentation> 
        <type>entry</type>
        <value>sharefile</value>
        <required>1</required>
    </formElement> 

    <formElement> 
        <label>ShareFile upload directory</label> 
        <property>sharefileUploadDirectory</property> 
        <documentation>The path to your EC-ShareFile uploads directory.</documentation> 
        <type>entry</type>
        <value>/clients/X-Z/NAME/uploads</value>
        <required>1</required>
    </formElement> 

    <formElement> 
        <label>Agent List</label> 
        <property>agents</property> 
        <documentation>The comma separated list of agents where the problem occured.</documentation> 
        <type>entry</type>
    </formElement> 

    <formElement> 
        <label>Time</label> 
        <property>time</property> 
        <documentation>The time at which the issue happened (if known). Format is [yyyy-mm-dd] HH:mm.</documentation> 
        <type>entry</type>
    </formElement> 

    <formElement> 
        <label>Job Id</label> 
        <property>jobNumber</property> 
        <documentation>The jobId in which the issue occured (if known). It is used to retrieve the correct logs.</documentation> 
        <type>entry</type>
    </formElement> 

</editor>
'''
  }

  procedure 'collectServerLogs', {
    description = 'a procedure to collect server logs'
    jobNameTemplate = ''
    resourceName = ''
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workspaceName = ''

    formalParameter 'destinationDirectory', defaultValue: '', {
      description = 'The directory in which to copy the logs.'
      expansionDeferred = '0'
      label = null
      orderIndex = null
      required = '0'
      type = 'entry'
    }

    formalParameter 'jobNumber', defaultValue: '', {
      description = 'The ID of the job that generated the error. Will be used to collect the right logs'
      expansionDeferred = '0'
      label = null
      orderIndex = null
      required = '0'
      type = 'entry'
    }

    formalParameter 'serverResources', defaultValue: '', {
      description = 'A list of resources or pools, comma separated'
      expansionDeferred = '0'
      label = null
      orderIndex = null
      required = '1'
      type = 'entry'
    }

    formalParameter 'time', defaultValue: '', {
      description = 'The time at which the issue happened. Used to collect the right logs.'
      expansionDeferred = '0'
      label = null
      orderIndex = null
      required = '0'
      type = 'entry'
    }

    step 'loop', {
      description = ''
      alwaysRun = '0'
      broadcast = '0'
      command = '''$[/plugins[EC-Admin]/project/scripts/perlHeaderJSON]

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
	$server =~ s/^\\s*//;	# removing leading  spaces
    $server =~ s/\\s*$//;	# removing trailing spaces
    
	#
	# Is this a pool?
	my ($ok, $json)=InvokeCommander("IgnoreError SuppressLog", \'getResourcePool\', $server);
    if ($ok) {
    	printf("Processing pool   $server\\n");
        foreach my $node($ec->getResourcesInPool($server)->findnodes("//resource")) {
        	my $resName=$node->{resourceName};
            printf("    processing server $resName\\n");
            $resourceHash{$resName}=1;
        } 
        next;
    }
    
    #
    # Check if it\'s a valid resource
	my ($ok, $json)=InvokeCommander("IgnoreError SuppressLog", \'getResource\', $server);
    if ($ok) {
    	printf("Processing server $server\\n");
        $resourceHash{$server}=1;
    } else {
    	printf("$server is not a recognized pool or resource\\n");
        exit(1);
    }
}

printf("\\n");
foreach my $server (keys %resourceHash) {
	printf("Collecting logs for %s\\n", $server);
	my ($ok, $json)=InvokeCommander("IgnoreError", \'getResource\', $server);
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
'''
      condition = ''
      errorHandling = 'failProcedure'
      exclusiveMode = 'none'
      logFileName = ''
      parallel = '0'
      postProcessor = ''
      precondition = ''
      releaseMode = 'none'
      resourceName = ''
      shell = 'ec-perl'
      subprocedure = null
      subproject = null
      timeLimit = ''
      timeLimitUnits = 'minutes'
      workingDirectory = ''
      workspaceName = ''
    }

    // Custom properties

    property 'ec_customEditorData', {

      // Custom properties

      property 'parameters', {

        // Custom properties

        property 'destinationDirectory', {

          // Custom properties
          formType = 'standard'
        }

        property 'jobNumber', {

          // Custom properties
          formType = 'standard'
        }

        property 'serverResources', {

          // Custom properties
          formType = 'standard'
        }

        property 'time', {

          // Custom properties
          formType = 'standard'
        }
      }
    }
  }

  procedure 'commentOnSupportTicket', {
    description = 'A procedure to add comment unto a ticket on Zendesk'
    jobNameTemplate = ''
    resourceName = 'local'
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workspaceName = ''

    formalParameter 'comment', defaultValue: '', {
      description = 'The main comment to add to the ticket'
      expansionDeferred = '0'
      label = null
      orderIndex = null
      required = '1'
      type = 'textarea'
    }

    formalParameter 'ticketId', defaultValue: '', {
      description = 'The number of the ticket to comment on'
      expansionDeferred = '0'
      label = null
      orderIndex = null
      required = '1'
      type = 'entry'
    }

    formalParameter 'zendeskConfiguration', defaultValue: 'zendesk', {
      description = 'The name of your Zendesk configuration'
      expansionDeferred = '0'
      label = null
      orderIndex = null
      required = '1'
      type = 'entry'
    }

    step 'comment', {
      description = ''
      alwaysRun = '0'
      broadcast = '0'
      command = null
      condition = ''
      errorHandling = 'failProcedure'
      exclusiveMode = 'none'
      logFileName = null
      parallel = '0'
      postProcessor = null
      precondition = ''
      releaseMode = 'none'
      resourceName = ''
      shell = null
      subprocedure = 'commentOnTicket'
      subproject = '/plugins/EC-Zendesk/project'
      timeLimit = ''
      timeLimitUnits = 'minutes'
      workingDirectory = null
      workspaceName = ''
      actualParameter '''comment''', '''$[comment]'''
      actualParameter '''credential''', '''$[zendeskConfiguration]'''
      actualParameter '''ticketNumber''', '''$[ticketId]'''
    }

    // Custom properties

    property 'ec_customEditorData', {

      // Custom properties

      property 'parameters', {

        // Custom properties

        property 'ShareFileCredentialName', {

          // Custom properties
          formType = 'standard'
        }

        property 'agents', {

          // Custom properties
          formType = 'standard'
        }

        property 'comment', {

          // Custom properties
          formType = 'standard'
        }

        property 'jobNumber', {

          // Custom properties
          formType = 'standard'
        }

        property 'product', {

          // Custom properties

          property 'options', {

            // Custom properties

            property 'option1', {

              // Custom properties
              text = 'ElectricCommander'

              property 'value', value: 'electriccommander', {
                expandable = '1'
              }
            }

            property 'option2', {

              // Custom properties
              text = 'ElectricFlow'

              property 'value', value: 'electricflow', {
                expandable = '1'
              }
            }
            optionCount = '2'
            type = 'list'
          }
          formType = 'standard'
        }

        property 'serverResources', {

          // Custom properties
          formType = 'standard'
        }

        property 'sharefileConfiguration', {

          // Custom properties
          formType = 'standard'
        }

        property 'sharefileUploadDirectory', {

          // Custom properties
          formType = 'standard'
        }

        property 'stepId', {

          // Custom properties
          formType = 'standard'
        }

        property 'ticketDescription', {

          // Custom properties
          formType = 'standard'
        }

        property 'ticketId', {

          // Custom properties
          formType = 'standard'
        }

        property 'ticketTitle', {

          // Custom properties
          formType = 'standard'
        }

        property 'time', {

          // Custom properties
          formType = 'standard'
        }

        property 'version', {

          // Custom properties
          formType = 'standard'
        }

        property 'zendeskConfiguration', {

          // Custom properties
          formType = 'standard'
        }

        property 'zendeskCredential', {

          // Custom properties
          formType = 'standard'
        }
      }
    }
    ec_parameterForm = '''<editor>
    <formElement> 
        <label>Ticket ID</label> 
        <property>ticketId</property> 
        <documentation>The id of your existing ticket.</documentation> 
        <type>entry</type>
        <required>1</required>
    </formElement> 

    <formElement> 
        <label>Comment</label> 
        <property>comment</property> 
        <documentation>The new comment to add to your ticket.</documentation> 
        <type>textarea</type>
        <required>1</required>
    </formElement> 

    <formElement> 
        <label>Zendesk configuration</label> 
        <property>zendeskConfiguration</property> 
        <documentation>The name of the Zendesk configuration to use.</documentation> 
        <type>entry</type>
        <value>zendesk</value>
        <required>1</required>
    </formElement> 

</editor>
'''
  }

  procedure 'gatherLogs', {
    description = 'A procedure to gather logs without sending them in case your EF cluster does not have external internet access. Optionally, an artifact can be created that can be downloaded later to the user desktop.'
    jobNameTemplate = ''
    resourceName = 'local'
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workspaceName = ''

    formalParameter 'agents', defaultValue: '', {
      description = 'a commas separated list of agents. Will be used to get the logs from the agents involved in the issue'
      expansionDeferred = '0'
      label = null
      orderIndex = null
      required = '0'
      type = 'entry'
    }

    formalParameter 'createArtifact', defaultValue: 'true', {
      description = ''
      expansionDeferred = '0'
      label = null
      orderIndex = null
      required = '0'
      type = 'checkbox'
    }

    formalParameter 'gatheringResource', defaultValue: 'local', {
      description = 'The resource to use to gather log and open ticket'
      expansionDeferred = '0'
      label = null
      orderIndex = null
      required = '1'
      type = 'entry'
    }

    formalParameter 'jobNumber', defaultValue: '', {
      description = 'The ID of the job that generated the error. Will be used to collect the right logs'
      expansionDeferred = '0'
      label = null
      orderIndex = null
      required = '0'
      type = 'entry'
    }

    formalParameter 'serverResources', defaultValue: 'default', {
      description = 'A list of resources or pools, comma separated'
      expansionDeferred = '0'
      label = null
      orderIndex = null
      required = '0'
      type = 'entry'
    }

    formalParameter 'time', defaultValue: '', {
      description = 'The time at which the issue happened. Used to collect the right logs. If empty, will simply send the commander.log'
      expansionDeferred = '0'
      label = null
      orderIndex = null
      required = '0'
      type = 'entry'
    }

    step 'getVersion', {
      description = 'Retrieve the server version'
      alwaysRun = '0'
      broadcast = '0'
      command = '''$[/plugins[EC-Admin]project/scripts/perlHeaderJSON]

my ($ok, $json)=InvokeCommander("", \'getVersions\');

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
'''
      condition = ''
      errorHandling = 'failProcedure'
      exclusiveMode = 'none'
      logFileName = ''
      parallel = '0'
      postProcessor = ''
      precondition = ''
      releaseMode = 'none'
      resourceName = '$[gatheringResource]'
      shell = 'ec-perl'
      subprocedure = null
      subproject = null
      timeLimit = ''
      timeLimitUnits = 'minutes'
      workingDirectory = ''
      workspaceName = ''
    }

    step 'grabResource', {
      description = 'Grab one of the commander server resources (in case of cluster)'
      alwaysRun = '0'
      broadcast = '0'
      command = '''ectool setProperty /myJob/gatheringResource --value $[/myJobStep/assignedResourceName]
'''
      condition = ''
      errorHandling = 'failProcedure'
      exclusiveMode = 'none'
      logFileName = ''
      parallel = '0'
      postProcessor = ''
      precondition = ''
      releaseMode = 'none'
      resourceName = '$[gatheringResource]'
      shell = ''
      subprocedure = null
      subproject = null
      timeLimit = ''
      timeLimitUnits = 'minutes'
      workingDirectory = ''
      workspaceName = ''
    }

    step 'createNumber', {
      description = ''
      alwaysRun = '0'
      broadcast = '0'
      command = '''$[/plugins[EC-Admin]project/scripts/perlHeaderJSON]

$ec->setProperty("/myJob/packageNumber", "$[/timestamp YYYY.MM.dd]");
'''
      condition = ''
      errorHandling = 'failProcedure'
      exclusiveMode = 'none'
      logFileName = ''
      parallel = '0'
      postProcessor = ''
      precondition = ''
      releaseMode = 'none'
      resourceName = ''
      shell = 'ec-perl'
      subprocedure = null
      subproject = null
      timeLimit = ''
      timeLimitUnits = 'minutes'
      workingDirectory = ''
      workspaceName = ''
    }

    step 'grabDestinationDir', {
      description = ''
      alwaysRun = '0'
      broadcast = '0'
      command = '''#############################################################################
#
# Copyright Electric-Cloud 2015
#
#############################################################################
use Cwd;

$[/plugins[EC-Admin]project/scripts/perlHeaderJSON]

#############################################################################
#
# Global Variables
#
#############################################################################
my $cwd = getcwd();

$ec->setProperty("/myJob/destinationDirectory", "$cwd/$[/myJob/packageNumber]");
'''
      condition = ''
      errorHandling = 'failProcedure'
      exclusiveMode = 'none'
      logFileName = ''
      parallel = '0'
      postProcessor = ''
      precondition = ''
      releaseMode = 'none'
      resourceName = '$[/myJob/gatheringResource]'
      shell = 'ec-perl'
      subprocedure = null
      subproject = null
      timeLimit = ''
      timeLimitUnits = 'minutes'
      workingDirectory = ''
      workspaceName = ''
    }

    step 'createTicketDirectory', {
      description = 'create a directory to collect the logs'
      alwaysRun = '0'
      broadcast = '0'
      command = null
      condition = ''
      errorHandling = 'failProcedure'
      exclusiveMode = 'none'
      logFileName = null
      parallel = '0'
      postProcessor = null
      precondition = ''
      releaseMode = 'none'
      resourceName = '$[/myJob/gatheringResource]'
      shell = null
      subprocedure = 'CreateDirectory'
      subproject = '/plugins/EC-FileOps/project'
      timeLimit = ''
      timeLimitUnits = 'minutes'
      workingDirectory = null
      workspaceName = ''
      actualParameter '''Path''', '''$[/myJob/destinationDirectory]'''
    }

    step 'collectServerLogs', {
      description = ''
      alwaysRun = '0'
      broadcast = '0'
      command = null
      condition = ''
      errorHandling = 'failProcedure'
      exclusiveMode = 'none'
      logFileName = null
      parallel = '0'
      postProcessor = null
      precondition = ''
      releaseMode = 'none'
      resourceName = ''
      shell = null
      subprocedure = 'collectServerLogs'
      subproject = ''
      timeLimit = ''
      timeLimitUnits = 'minutes'
      workingDirectory = null
      workspaceName = ''
      actualParameter '''destinationDirectory''', '''$[/myJob/destinationDirectory]'''
      actualParameter '''jobNumber''', '''$[jobNumber]'''
      actualParameter '''serverResources''', '''$[serverResources]'''
      actualParameter '''time''', '''$[time]'''
    }

    step 'collectAgentLogs', {
      description = 'If agent list is not empty, go grab agent.log and jagent.log'
      alwaysRun = '0'
      broadcast = '0'
      command = '''#############################################################################
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
	my($ok, $json)=InvokeCommander("IgnoreError SuppressLog", \'getResource\', $agent);
    if (! $ok) {
    	printf("Agent \'%s\' does not exist!", $agent);
        $ec->setProperty("outcome", "warning");
        next;
    }
    
    # Testing if agent is running
    #
    if ($json->{responses}->[0]->{resource}->{agentState}->{state} ne "alive") {
    	printf("Agent \'%s\' is not running!\\n", $agent);
        $ec->setProperty("outcome", "warning");
        next;
    }
    if ($json->{responses}->[0]->{resource}->{resourceDisabled} eq "1") {
    	printf("Agent \'%s\' is disabled!\\n", $agent);
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
         	{actualParameterName => \'sourceWorkspaceName\',      value => "default"},
          	{actualParameterName => \'sourceResourceName\',       value => "$agent"},
            {actualParameterName => \'sourceFile\',               value => $ENV{COMMANDER_DATA}."/logs/agent/*agent.log"},
          	{actualParameterName => \'destinationResourceName\',  value => "local"},
           	{actualParameterName => \'destinationFile\',          value => "$[/myJob/destinationDirectory]/$agent"},
           	{actualParameterName => \'destinationWorkspaceName\', value => "default"},
           ],
    });
}

$[/plugins[EC-Admin]project/scripts/perlLibJSON]

'''
      condition = '$[/javascript "$[agents]" != "" ]'
      errorHandling = 'failProcedure'
      exclusiveMode = 'none'
      logFileName = ''
      parallel = '0'
      postProcessor = ''
      precondition = ''
      releaseMode = 'none'
      resourceName = ''
      shell = 'ec-perl'
      subprocedure = null
      subproject = null
      timeLimit = ''
      timeLimitUnits = 'minutes'
      workingDirectory = ''
      workspaceName = ''
    }

    step 'createBundle', {
      description = 'Zip the different files'
      alwaysRun = '0'
      broadcast = '0'
      command = null
      condition = ''
      errorHandling = 'failProcedure'
      exclusiveMode = 'none'
      logFileName = null
      parallel = '0'
      postProcessor = null
      precondition = ''
      releaseMode = 'none'
      resourceName = '$[/myJob/gatheringResource]'
      shell = null
      subprocedure = 'Create Zip File'
      subproject = '/plugins/EC-FileOps/project'
      timeLimit = ''
      timeLimitUnits = 'minutes'
      workingDirectory = null
      workspaceName = ''
      actualParameter '''sourceFile''', '''$[/myJob/destinationDirectory]'''
      actualParameter '''zipFile''', '''$[/myJob/destinationDirectory].zip'''
    }

    step 'createArtifact', {
      description = ''
      alwaysRun = '0'
      broadcast = '0'
      command = null
      condition = ''
      errorHandling = 'failProcedure'
      exclusiveMode = 'none'
      logFileName = null
      parallel = '0'
      postProcessor = null
      precondition = ''
      releaseMode = 'none'
      resourceName = '$[/myJob/gatheringResource]'
      shell = null
      subprocedure = 'Publish'
      subproject = '/plugins/EC-Artifact/project'
      timeLimit = ''
      timeLimitUnits = 'minutes'
      workingDirectory = null
      workspaceName = ''
      actualParameter '''artifactName''', '''EC-Support:logs'''
      actualParameter '''artifactVersionVersion''', '''$[/myJob/packageNumber].$[/increment /server/ec_counters/EC-Support/packageNumber]'''
      actualParameter '''compress''', '''1'''
      actualParameter '''dependentArtifactVersionList''', ''''''
      actualParameter '''excludePatterns''', ''''''
      actualParameter '''followSymlinks''', '''0'''
      actualParameter '''fromLocation''', '''$[/myJob/destinationDirectory]'''
      actualParameter '''includePatterns''', ''''''
      actualParameter '''repositoryName''', '''default'''
    }

    step 'summary', {
      description = ''
      alwaysRun = '0'
      broadcast = '0'
      command = '''$[/plugins[EC-Admin]project/scripts/perlheaderJSON]

$ec->setProperty("/myJob/report-urls/logBundle", "/commander/jobSteps/$[/myjobStep/jobStepId]/./$[/myJob/packageNumber].zip");


'''
      condition = ''
      errorHandling = 'failProcedure'
      exclusiveMode = 'none'
      logFileName = ''
      parallel = '0'
      postProcessor = ''
      precondition = ''
      releaseMode = 'none'
      resourceName = '$[/myJob/gatheringResource]'
      shell = 'ec-perl'
      subprocedure = null
      subproject = null
      timeLimit = ''
      timeLimitUnits = 'minutes'
      workingDirectory = ''
      workspaceName = ''
    }

    // Custom properties

    property 'ec_customEditorData', {

      // Custom properties

      property 'parameters', {

        // Custom properties

        property 'ShareFileCredentialName', {

          // Custom properties
          formType = 'standard'
        }

        property 'agents', {

          // Custom properties
          formType = 'standard'
        }

        property 'createArtifact', {

          // Custom properties
          checkedValue = 'true'
          formType = 'standard'
          initiallyChecked = '0'
          uncheckedValue = 'false'
        }

        property 'gatheringResource', {

          // Custom properties
          formType = 'standard'
        }

        property 'jobNumber', {

          // Custom properties
          formType = 'standard'
        }

        property 'problemScope', {

          // Custom properties

          property 'options', {

            // Custom properties

            property 'option1', {

              // Custom properties
              text = 'Test Setup'

              property 'value', value: '1_test_setup', {
                expandable = '1'
              }
            }

            property 'option2', {

              // Custom properties
              text = 'One User'

              property 'value', value: '2_one_user', {
                expandable = '1'
              }
            }

            property 'option3', {

              // Custom properties
              text = 'One Team'

              property 'value', value: '3_one_team', {
                expandable = '1'
              }
            }

            property 'option4', {

              // Custom properties
              text = 'Some users'

              property 'value', value: '4_some_users', {
                expandable = '1'
              }
            }

            property 'option5', {

              // Custom properties
              text = 'One Site'

              property 'value', value: '5_one_site', {
                expandable = '1'
              }
            }

            property 'option6', {

              // Custom properties
              text = 'Some teams'

              property 'value', value: '6_some_teams', {
                expandable = '1'
              }
            }

            property 'option7', {

              // Custom properties
              text = 'Some sites'

              property 'value', value: '7_some_sites', {
                expandable = '1'
              }
            }

            property 'option8', {

              // Custom properties
              text = 'Everyone'

              property 'value', value: '8_everyone', {
                expandable = '1'
              }
            }
            optionCount = '8'
            type = 'list'
          }
          formType = 'standard'
        }

        property 'problemType', {

          // Custom properties

          property 'options', {

            // Custom properties

            property 'option1', {

              // Custom properties
              text = 'Enhancement'

              property 'value', value: '1_enhancement', {
                expandable = '1'
              }
            }

            property 'option2', {

              // Custom properties
              text = 'Need Assistance'

              property 'value', value: '2_need_assistance', {
                expandable = '1'
              }
            }

            property 'option3', {

              // Custom properties
              text = 'Nuisance'

              property 'value', value: '3_nuisance', {
                expandable = '1'
              }
            }

            property 'option4', {

              // Custom properties
              text = 'Tool Limiting'

              property 'value', value: '4_tool_limiting', {
                expandable = '1'
              }
            }

            property 'option5', {

              // Custom properties
              text = 'Performance'

              property 'value', value: '5_performance', {
                expandable = '1'
              }
            }

            property 'option6', {

              // Custom properties
              text = 'Blocking'

              property 'value', value: '6_blocking', {
                expandable = '1'
              }
            }
            optionCount = '6'
            type = 'list'
          }
          formType = 'standard'
        }

        property 'product', {

          // Custom properties

          property 'options', {

            // Custom properties

            property 'option1', {

              // Custom properties
              text = 'ElectricCommander'

              property 'value', value: 'electriccommander', {
                expandable = '1'
              }
            }

            property 'option2', {

              // Custom properties
              text = 'ElectricFlow'

              property 'value', value: 'electricflow', {
                expandable = '1'
              }
            }
            optionCount = '2'
            type = 'list'
          }
          formType = 'standard'
        }

        property 'serverResources', {

          // Custom properties
          formType = 'standard'
        }

        property 'sharefileConfiguration', {

          // Custom properties
          formType = 'standard'
        }

        property 'sharefileUploadDirectory', {

          // Custom properties
          formType = 'standard'
        }

        property 'stepId', {

          // Custom properties
          formType = 'standard'
        }

        property 'ticketDescription', {

          // Custom properties
          formType = 'standard'
        }

        property 'ticketTitle', {

          // Custom properties
          formType = 'standard'
        }

        property 'time', {

          // Custom properties
          formType = 'standard'
        }

        property 'version', {

          // Custom properties
          formType = 'standard'
        }

        property 'zendeskConfiguration', {

          // Custom properties
          formType = 'standard'
        }

        property 'zendeskCredential', {

          // Custom properties
          formType = 'standard'
        }
      }
    }
    ec_parameterForm = '''<editor>
    <formElement>
        <label>Gathering Resource</label>
        <property>gatheringResource</property>
         <documentation>The resource to gather the logs, open ticket and upload logs.</documentation> 
        <type>entry</type>
        <required>1</required>
        <value>local</value>       
    </formElement> 

    <formElement>
        <label>Server Resources</label>
        <property>serverResources</property>
         <documentation>A list of resources -comma separated- or pools to represent your server list. Change the default value for cluster mode.</documentation> 
        <type>entry</type>
        <required>1</required>
        <value>default</value>       
    </formElement> 

    <formElement> 
        <label>Agent List</label> 
        <property>agents</property> 
        <documentation>The comma separated list of agents where the problem occured.</documentation> 
        <type>entry</type>
    </formElement> 

    <formElement> 
        <label>Time</label> 
        <property>time</property> 
        <documentation>The time at which the issue happened (if known). Format is [yyyy-mm-dd] HH:mm.</documentation> 
        <type>entry</type>
    </formElement> 

    <formElement> 
        <label>Job Id</label> 
        <property>jobNumber</property> 
        <documentation>The jobId in which the issue occured (if known). It is used to retrieve the correct logs.</documentation> 
        <type>entry</type>
    </formElement> 

    <formElement> 
        <label>Create Artifact</label> 
        <property>createArtifact</property> 
        <documentation>A boolean to create an artifact with all the logs</documentation> 
        <type>checkbox</type> 
        <checkedValue>true</checkedValue> 
        <uncheckedValue>false</uncheckedValue> 
        <initiallyChecked>1</initiallyChecked> 
        <value>true</value>     
    </formElement> 

</editor>
'''
  }

  procedure 'openEnhancementRequest', {
    description = '''A procedure to automatically open an enhancement request on Zendesk.
No log involved'''
    jobNameTemplate = ''
    resourceName = 'local'
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workspaceName = ''

    formalParameter 'problemScope', defaultValue: '2_one_user', {
      description = ''
      expansionDeferred = '0'
      label = null
      orderIndex = null
      required = '0'
      type = 'select'
    }

    formalParameter 'product', defaultValue: 'electricflow', {
      description = 'the name of the product that failed'
      expansionDeferred = '0'
      label = null
      orderIndex = null
      required = '1'
      type = 'select'
    }

    formalParameter 'ticketDescription', defaultValue: '', {
      description = 'The main description of the issue you are facing'
      expansionDeferred = '0'
      label = null
      orderIndex = null
      required = '0'
      type = 'textarea'
    }

    formalParameter 'ticketTitle', defaultValue: 'Enhancement Request: ', {
      description = ''
      expansionDeferred = '0'
      label = null
      orderIndex = null
      required = '1'
      type = 'entry'
    }

    formalParameter 'zendeskConfiguration', defaultValue: 'zendesk', {
      description = 'The name of your Zendesk configuration'
      expansionDeferred = '0'
      label = null
      orderIndex = null
      required = '1'
      type = 'entry'
    }

    step 'getVersion', {
      description = 'Retrieve the server version'
      alwaysRun = '0'
      broadcast = '0'
      command = '''$[/plugins[EC-Admin]project/scripts/perlHeaderJSON]

my ($ok, $json)=InvokeCommander("", \'getVersions\');

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
'''
      condition = ''
      errorHandling = 'failProcedure'
      exclusiveMode = 'none'
      logFileName = ''
      parallel = '0'
      postProcessor = ''
      precondition = ''
      releaseMode = 'none'
      resourceName = ''
      shell = 'ec-perl'
      subprocedure = null
      subproject = null
      timeLimit = ''
      timeLimitUnits = 'minutes'
      workingDirectory = ''
      workspaceName = ''
    }

    step 'createTicket', {
      description = 'Open the ticket'
      alwaysRun = '0'
      broadcast = '0'
      command = null
      condition = ''
      errorHandling = 'failProcedure'
      exclusiveMode = 'none'
      logFileName = null
      parallel = '0'
      postProcessor = null
      precondition = ''
      releaseMode = 'none'
      resourceName = ''
      shell = null
      subprocedure = 'createTicket'
      subproject = '/plugins/EC-Zendesk/project'
      timeLimit = ''
      timeLimitUnits = 'minutes'
      workingDirectory = null
      workspaceName = ''
      actualParameter '''credential''', '''$[zendeskConfiguration]'''
      actualParameter '''problemScope''', '''$[problemScope]'''
      actualParameter '''problemType''', '''1_enhancement'''
      actualParameter '''product''', '''$[product]'''
      actualParameter '''ticketDescription''', '''$[ticketDescription]'''
      actualParameter '''ticketSubject''', '''$[ticketTitle]'''
      actualParameter '''version''', '''$[serverVersion]'''
    }

    // Custom properties

    property 'ec_customEditorData', {

      // Custom properties

      property 'parameters', {

        // Custom properties

        property 'ShareFileCredentialName', {

          // Custom properties
          formType = 'standard'
        }

        property 'agents', {

          // Custom properties
          formType = 'standard'
        }

        property 'jobNumber', {

          // Custom properties
          formType = 'standard'
        }

        property 'problemScope', {

          // Custom properties

          property 'options', {

            // Custom properties

            property 'option1', {

              // Custom properties
              text = 'Test Setup'

              property 'value', value: '1_test_setup', {
                expandable = '1'
              }
            }

            property 'option2', {

              // Custom properties
              text = 'One User'

              property 'value', value: '2_one_user', {
                expandable = '1'
              }
            }

            property 'option3', {

              // Custom properties
              text = 'One Team'

              property 'value', value: '3_one_team', {
                expandable = '1'
              }
            }

            property 'option4', {

              // Custom properties
              text = 'Some users'

              property 'value', value: '4_some_users', {
                expandable = '1'
              }
            }

            property 'option5', {

              // Custom properties
              text = 'One Site'

              property 'value', value: '5_one_site', {
                expandable = '1'
              }
            }

            property 'option6', {

              // Custom properties
              text = 'Some teams'

              property 'value', value: '6_some_teams', {
                expandable = '1'
              }
            }

            property 'option7', {

              // Custom properties
              text = 'Some sites'

              property 'value', value: '7_some_sites', {
                expandable = '1'
              }
            }

            property 'option8', {

              // Custom properties
              text = 'Everyone'

              property 'value', value: '8_everyone', {
                expandable = '1'
              }
            }
            optionCount = '8'
            type = 'list'
          }
          formType = 'standard'
        }

        property 'product', {

          // Custom properties

          property 'options', {

            // Custom properties

            property 'option1', {

              // Custom properties
              text = 'ElectricCommander'

              property 'value', value: 'electriccommander', {
                expandable = '1'
              }
            }

            property 'option2', {

              // Custom properties
              text = 'ElectricFlow'

              property 'value', value: 'electricflow', {
                expandable = '1'
              }
            }
            optionCount = '2'
            type = 'list'
          }
          formType = 'standard'
        }

        property 'serverResources', {

          // Custom properties
          formType = 'standard'
        }

        property 'sharefileConfiguration', {

          // Custom properties
          formType = 'standard'
        }

        property 'sharefileUploadDirectory', {

          // Custom properties
          formType = 'standard'
        }

        property 'stepId', {

          // Custom properties
          formType = 'standard'
        }

        property 'ticketDescription', {

          // Custom properties
          formType = 'standard'
        }

        property 'ticketTitle', {

          // Custom properties
          formType = 'standard'
        }

        property 'time', {

          // Custom properties
          formType = 'standard'
        }

        property 'version', {

          // Custom properties
          formType = 'standard'
        }

        property 'zendeskConfiguration', {

          // Custom properties
          formType = 'standard'
        }

        property 'zendeskCredential', {

          // Custom properties
          formType = 'standard'
        }
      }
    }
    ec_parameterForm = '''<editor>
    <formElement> 
        <label>Title</label> 
        <property>ticketTitle</property> 
        <documentation>The title of your ticket.</documentation> 
        <type>entry</type>
        <value>Enhancement Request: </value>
        <required>1</required>
    </formElement> 

    <formElement> 
        <label>Description</label> 
        <property>ticketDescription</property> 
        <documentation>The full description of your ticket.</documentation> 
        <type>textarea</type>
        <required>1</required>
    </formElement> 

    <formElement> 
        <label>Product</label> 
        <property>product</property> 
        <documentation>The name of the product with which you have an issue.</documentation> 
        <type>select</type> 
        <option> 
            <name>ElectricCommander</name> 
            <value>electriccommander</value> 
        </option> 
        <option> 
            <name>ElectricFlow</name> 
            <value>electricflow</value> 
        </option> 
        <value>electriccommander</value> 
        <required>1</required>
    </formElement> 

    <formElement> 
        <label>Problem Scope</label> 
        <property>problemScope</property> 
        <documentation>The scope of the problem, to help support prioritize your ticket.</documentation> 
        <type>select</type> 
        <option><name>Test Setup</name><value>01_test_setup</value></option> 
        <option><name>One User</name><value>2_one_user</value></option> 
        <option><name>One Team</name><value>3_one_team</value></option> 
        <option><name>Some users</name><value>4_some_users</value></option> 
        <option><name>One Site</name><value>5_one_site</value></option> 
        <option><name>Some teams</name><value>6_some_teams</value></option> 
        <option><name>Some sites</name><value>7_some_sites</value></option> 
        <option><name>Everyone</name><value>8_everyone</value></option> 
        <value>2_one_user</value> 
        <required>1</required>
    </formElement> 

    <formElement> 
        <label>Zendesk configuration</label> 
        <property>zendeskConfiguration</property> 
        <documentation>The name of the Zendesk configuration to use.</documentation> 
        <type>entry</type>
        <value>zendesk</value>
        <required>1</required>
    </formElement> 
</editor>
'''
  }

  procedure 'openSupportTicket', {
    description = 'A procedure to automatically open a ticket on Zendesk and deliver the required logs to ShareFile'
    jobNameTemplate = ''
    resourceName = 'local'
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workspaceName = ''

    formalParameter 'agents', defaultValue: '', {
      description = 'a commas separated list of agents. Will be used to get the logs from the agents involved in the issue'
      expansionDeferred = '0'
      label = null
      orderIndex = null
      required = '0'
      type = 'entry'
    }

    formalParameter 'gatheringResource', defaultValue: 'local', {
      description = 'The resource to use to gather log and open ticket'
      expansionDeferred = '0'
      label = null
      orderIndex = null
      required = '1'
      type = 'entry'
    }

    formalParameter 'jobNumber', defaultValue: '', {
      description = 'The ID of the job that generated the error. Will be used to collect the right logs'
      expansionDeferred = '0'
      label = null
      orderIndex = null
      required = '0'
      type = 'entry'
    }

    formalParameter 'problemScope', defaultValue: '2_one_user', {
      description = ''
      expansionDeferred = '0'
      label = null
      orderIndex = null
      required = '1'
      type = 'select'
    }

    formalParameter 'problemType', defaultValue: '2_need_assistance', {
      description = ''
      expansionDeferred = '0'
      label = null
      orderIndex = null
      required = '1'
      type = 'select'
    }

    formalParameter 'product', defaultValue: 'electricflow', {
      description = 'the name of the product that failed'
      expansionDeferred = '0'
      label = null
      orderIndex = null
      required = '1'
      type = 'select'
    }

    formalParameter 'serverResources', defaultValue: 'default', {
      description = 'A list of resources or pools, comma separated'
      expansionDeferred = '0'
      label = null
      orderIndex = null
      required = '0'
      type = 'entry'
    }

    formalParameter 'sharefileConfiguration', defaultValue: 'sharefile', {
      description = 'Name of your Sharefile configuration'
      expansionDeferred = '0'
      label = null
      orderIndex = null
      required = '1'
      type = 'entry'
    }

    formalParameter 'sharefileUploadDirectory', defaultValue: '', {
      description = 'Private directory on ShareFile on where to upload the logs'
      expansionDeferred = '0'
      label = null
      orderIndex = null
      required = '1'
      type = 'entry'
    }

    formalParameter 'ticketDescription', defaultValue: '', {
      description = 'The main description of the issue you are facing'
      expansionDeferred = '0'
      label = null
      orderIndex = null
      required = '0'
      type = 'textarea'
    }

    formalParameter 'ticketTitle', defaultValue: '', {
      description = ''
      expansionDeferred = '0'
      label = null
      orderIndex = null
      required = '1'
      type = 'entry'
    }

    formalParameter 'time', defaultValue: '', {
      description = 'The time at which the issue happened. Used to collect the right logs. If empty, will simply send the commander.log'
      expansionDeferred = '0'
      label = null
      orderIndex = null
      required = '0'
      type = 'entry'
    }

    formalParameter 'zendeskConfiguration', defaultValue: 'zendesk', {
      description = 'The name of your Zendesk configuration'
      expansionDeferred = '0'
      label = null
      orderIndex = null
      required = '1'
      type = 'entry'
    }

    step 'getVersion', {
      description = 'Retrieve the server version'
      alwaysRun = '0'
      broadcast = '0'
      command = '''$[/plugins[EC-Admin]project/scripts/perlHeaderJSON]

my ($ok, $json)=InvokeCommander("", \'getVersions\');

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
'''
      condition = ''
      errorHandling = 'failProcedure'
      exclusiveMode = 'none'
      logFileName = ''
      parallel = '0'
      postProcessor = ''
      precondition = ''
      releaseMode = 'none'
      resourceName = '$[gatheringResource]'
      shell = 'ec-perl'
      subprocedure = null
      subproject = null
      timeLimit = ''
      timeLimitUnits = 'minutes'
      workingDirectory = ''
      workspaceName = ''
    }

    step 'grabResource', {
      description = 'Grab one of the commander server resources (in case of cluster)'
      alwaysRun = '0'
      broadcast = '0'
      command = '''ectool setProperty /myJob/gatheringResource --value $[/myJobStep/assignedResourceName]
'''
      condition = ''
      errorHandling = 'failProcedure'
      exclusiveMode = 'none'
      logFileName = ''
      parallel = '0'
      postProcessor = ''
      precondition = ''
      releaseMode = 'none'
      resourceName = '$[gatheringResource]'
      shell = ''
      subprocedure = null
      subproject = null
      timeLimit = ''
      timeLimitUnits = 'minutes'
      workingDirectory = ''
      workspaceName = ''
    }

    step 'createTicket', {
      description = '''Open the ticket

'''
      alwaysRun = '0'
      broadcast = '0'
      command = null
      condition = '''$[/javascript
  if ( "$[/server/settings/ipAddress]" == "ec54" ) {
    setProperty("/myJob/zendesk/ticketId", "114520");
   setProperty("summary", "Debug mode: 114520");
   false;
  } else {
   true;
  } ]
'''
      errorHandling = 'abortProcedure'
      exclusiveMode = 'none'
      logFileName = null
      parallel = '0'
      postProcessor = null
      precondition = ''
      releaseMode = 'none'
      resourceName = '$[/myJob/gatheringResource]'
      shell = null
      subprocedure = 'createTicket'
      subproject = '/plugins/EC-Zendesk/project'
      timeLimit = ''
      timeLimitUnits = 'minutes'
      workingDirectory = null
      workspaceName = ''
      actualParameter '''credential''', '''$[zendeskConfiguration]'''
      actualParameter '''problemScope''', '''$[problemScope]'''
      actualParameter '''problemType''', '''$[problemType]'''
      actualParameter '''product''', '''$[product]'''
      actualParameter '''ticketDescription''', '''$[ticketDescription]'''
      actualParameter '''ticketSubject''', '''$[ticketTitle]'''
      actualParameter '''version''', '''$[/myJob/serverVersion]'''
    }

    step 'grabDestinationDir', {
      description = ''
      alwaysRun = '0'
      broadcast = '0'
      command = '''#############################################################################
#
# Copyright Electric-Cloud 2015
#
#############################################################################
use Cwd;

$[/plugins[EC-Admin]project/scripts/perlHeaderJSON]

#############################################################################
#
# Global Variables
#
#############################################################################
my $cwd = getcwd();

$ec->setProperty("/myJob/destinationDirectory", "$cwd/$[/myJob/zendesk/ticketId]");
'''
      condition = ''
      errorHandling = 'failProcedure'
      exclusiveMode = 'none'
      logFileName = ''
      parallel = '0'
      postProcessor = ''
      precondition = ''
      releaseMode = 'none'
      resourceName = '$[/myJob/gatheringResource]'
      shell = 'ec-perl'
      subprocedure = null
      subproject = null
      timeLimit = ''
      timeLimitUnits = 'minutes'
      workingDirectory = ''
      workspaceName = ''
    }

    step 'createTicketDirectory', {
      description = 'create a directory to collect the logs'
      alwaysRun = '0'
      broadcast = '0'
      command = null
      condition = ''
      errorHandling = 'failProcedure'
      exclusiveMode = 'none'
      logFileName = null
      parallel = '0'
      postProcessor = null
      precondition = ''
      releaseMode = 'none'
      resourceName = '$[/myJob/gatheringResource]'
      shell = null
      subprocedure = 'CreateDirectory'
      subproject = '/plugins/EC-FileOps/project'
      timeLimit = ''
      timeLimitUnits = 'minutes'
      workingDirectory = null
      workspaceName = ''
      actualParameter '''Path''', '''$[/myJob/destinationDirectory]'''
    }

    step 'collectServerLogs', {
      description = ''
      alwaysRun = '0'
      broadcast = '0'
      command = null
      condition = ''
      errorHandling = 'failProcedure'
      exclusiveMode = 'none'
      logFileName = null
      parallel = '0'
      postProcessor = null
      precondition = ''
      releaseMode = 'none'
      resourceName = ''
      shell = null
      subprocedure = 'collectServerLogs'
      subproject = ''
      timeLimit = ''
      timeLimitUnits = 'minutes'
      workingDirectory = null
      workspaceName = ''
      actualParameter '''destinationDirectory''', '''$[/myJob/destinationDirectory]'''
      actualParameter '''jobNumber''', '''$[jobNumber]'''
      actualParameter '''serverResources''', '''$[serverResources]'''
      actualParameter '''time''', '''$[time]'''
    }

    step 'collectAgentLogs', {
      description = 'If agent list is not empty, go grab agent.log and jagent.log'
      alwaysRun = '0'
      broadcast = '0'
      command = '''#############################################################################
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
	my($ok, $json)=InvokeCommander("IgnoreError SuppressLog", \'getResource\', $agent);
    if (! $ok) {
    	printf("Agent \'%s\' does not exist!", $agent);
        $ec->setProperty("outcome", "warning");
        next;
    }
    
    # Testing if agent is running
    #
    if ($json->{responses}->[0]->{resource}->{agentState}->{state} ne "alive") {
    	printf("Agent \'%s\' is not running!\\n", $agent);
        $ec->setProperty("outcome", "warning");
        next;
    }
    if ($json->{responses}->[0]->{resource}->{resourceDisabled} eq "1") {
    	printf("Agent \'%s\' is disabled!\\n", $agent);
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
         	{actualParameterName => \'sourceWorkspaceName\',      value => "default"},
          	{actualParameterName => \'sourceResourceName\',       value => "$agent"},
            {actualParameterName => \'sourceFile\',               value => $ENV{COMMANDER_DATA}."/logs/agent/*agent.log"},
          	{actualParameterName => \'destinationResourceName\',  value => "local"},
           	{actualParameterName => \'destinationFile\',          value => "$[/myJob/destinationDirectory]/$agent"},
           	{actualParameterName => \'destinationWorkspaceName\', value => "default"},
           ],
    });
}

$[/plugins[EC-Admin]project/scripts/perlLibJSON]

'''
      condition = '$[/javascript "$[agents]" != "" ]'
      errorHandling = 'failProcedure'
      exclusiveMode = 'none'
      logFileName = ''
      parallel = '0'
      postProcessor = ''
      precondition = ''
      releaseMode = 'none'
      resourceName = ''
      shell = 'ec-perl'
      subprocedure = null
      subproject = null
      timeLimit = ''
      timeLimitUnits = 'minutes'
      workingDirectory = ''
      workspaceName = ''
    }

    step 'listing', {
      description = 'Get the list of collected files'
      alwaysRun = '0'
      broadcast = '0'
      command = '''#############################################################################
#
# Copyright Electric-Cloud 2015
#
#############################################################################
$[/plugins[EC-Admin]project/scripts/perlHeaderJSON]

#############################################################################
#
# Parameters
#
#############################################################################

#############################################################################
#
# Global Variables
#
#############################################################################
my $DEBUG=1;
my $destDir="$[/myJob/zendesk/ticketId]";


my $content=directoryContent($destDir);
$ec->setProperty("/myJob/fileList", $content);

#############################################################################
#
# directoryContent: return a string containing the list of files
#                   in a directory
#############################################################################
sub directoryContent {
    my $dir=shift @_;
    my $content="";
    
    opendir(my $dh, $dir) or die("Cannot open the directory $dir\\n$!");

	while (my $file = readdir($dh)) {
    	next if $file eq \'.\' or $file eq \'..\';
    	if (-d "$dir/$file") {
    		$content .= directoryContent("$dir/$file");	
        } else {
        	$content .= "$dir/$file\\n";
            printf("$dir/$file\\n") if ($DEBUG);
        }
    }
    closedir($dh);
    return $content;
}
    
'''
      condition = ''
      errorHandling = 'failProcedure'
      exclusiveMode = 'none'
      logFileName = ''
      parallel = '0'
      postProcessor = ''
      precondition = ''
      releaseMode = 'none'
      resourceName = '$[/myJob/gatheringResource]'
      shell = 'ec-perl'
      subprocedure = null
      subproject = null
      timeLimit = ''
      timeLimitUnits = 'minutes'
      workingDirectory = ''
      workspaceName = ''
    }

    step 'createBundle', {
      description = 'Zip the different files'
      alwaysRun = '0'
      broadcast = '0'
      command = null
      condition = ''
      errorHandling = 'failProcedure'
      exclusiveMode = 'none'
      logFileName = null
      parallel = '0'
      postProcessor = null
      precondition = ''
      releaseMode = 'none'
      resourceName = '$[/myJob/gatheringResource]'
      shell = null
      subprocedure = 'Create Zip File'
      subproject = '/plugins/EC-FileOps/project'
      timeLimit = ''
      timeLimitUnits = 'minutes'
      workingDirectory = null
      workspaceName = ''
      actualParameter '''sourceFile''', '''$[/myJob/destinationDirectory]'''
      actualParameter '''zipFile''', '''$[/myJob/destinationDirectory].zip'''
    }

    step 'uploadBundleToSharefile', {
      description = ''
      alwaysRun = '0'
      broadcast = '0'
      command = null
      condition = ''
      errorHandling = 'failProcedure'
      exclusiveMode = 'none'
      logFileName = null
      parallel = '0'
      postProcessor = null
      precondition = ''
      releaseMode = 'none'
      resourceName = '$[/myJob/gatheringResource]'
      shell = null
      subprocedure = 'CreateFolderAndUploadFile'
      subproject = '/plugins/EC-ShareFile/project'
      timeLimit = ''
      timeLimitUnits = 'minutes'
      workingDirectory = null
      workspaceName = ''
      actualParameter '''company''', '''electric-cloud'''
      actualParameter '''config''', '''$[ShareFileConfiguration]'''
      actualParameter '''folderToCreate''', '''$[sharefileUploadDirectory]/$[/myJob/zendesk/ticketId]'''
      actualParameter '''pathToFile''', '''$[/myJob/destinationDirectory].zip'''
    }

    step 'commentForFiles', {
      description = ''
      alwaysRun = '0'
      broadcast = '0'
      command = null
      condition = '''$[/javascript
  if ( "$[/server/settings/ipAddress]" == "ec54" ) {
    setProperty("/myJob/zendesk/ticketId", "114520");
   setProperty("summary", "Debug mode: 114520");
   false;
  } else {
   true;
  } ]'''
      errorHandling = 'failProcedure'
      exclusiveMode = 'none'
      logFileName = null
      parallel = '0'
      postProcessor = null
      precondition = ''
      releaseMode = 'none'
      resourceName = '$[/myJob/gatheringResource]'
      shell = null
      subprocedure = 'commentOnTicket'
      subproject = '/plugins/EC-Zendesk/project'
      timeLimit = ''
      timeLimitUnits = 'minutes'
      workingDirectory = null
      workspaceName = ''
      actualParameter '''comment''', '''The following file have been uploaded  $[sharefileUploadDirectory]/$[/myJob/zendesk/ticketId]/$[/myJob/zendesk/ticketId].zip
It contains:

$[/myJob/fileList]'''
      actualParameter '''credential''', '''$[zendeskConfiguration]'''
      actualParameter '''ticketNumber''', '''$[/myJob/zendesk/ticketId]'''
    }

    // Custom properties

    property 'ec_customEditorData', {

      // Custom properties

      property 'parameters', {

        // Custom properties

        property 'ShareFileCredentialName', {

          // Custom properties
          formType = 'standard'
        }

        property 'agents', {

          // Custom properties
          formType = 'standard'
        }

        property 'gatheringResource', {

          // Custom properties
          formType = 'standard'
        }

        property 'jobNumber', {

          // Custom properties
          formType = 'standard'
        }

        property 'problemScope', {

          // Custom properties

          property 'options', {

            // Custom properties

            property 'option1', {

              // Custom properties
              text = 'Test Setup'

              property 'value', value: '1_test_setup', {
                expandable = '1'
              }
            }

            property 'option2', {

              // Custom properties
              text = 'One User'

              property 'value', value: '2_one_user', {
                expandable = '1'
              }
            }

            property 'option3', {

              // Custom properties
              text = 'One Team'

              property 'value', value: '3_one_team', {
                expandable = '1'
              }
            }

            property 'option4', {

              // Custom properties
              text = 'Some users'

              property 'value', value: '4_some_users', {
                expandable = '1'
              }
            }

            property 'option5', {

              // Custom properties
              text = 'One Site'

              property 'value', value: '5_one_site', {
                expandable = '1'
              }
            }

            property 'option6', {

              // Custom properties
              text = 'Some teams'

              property 'value', value: '6_some_teams', {
                expandable = '1'
              }
            }

            property 'option7', {

              // Custom properties
              text = 'Some sites'

              property 'value', value: '7_some_sites', {
                expandable = '1'
              }
            }

            property 'option8', {

              // Custom properties
              text = 'Everyone'

              property 'value', value: '8_everyone', {
                expandable = '1'
              }
            }
            optionCount = '8'
            type = 'list'
          }
          formType = 'standard'
        }

        property 'problemType', {

          // Custom properties

          property 'options', {

            // Custom properties

            property 'option1', {

              // Custom properties
              text = 'Enhancement'

              property 'value', value: '1_enhancement', {
                expandable = '1'
              }
            }

            property 'option2', {

              // Custom properties
              text = 'Need Assistance'

              property 'value', value: '2_need_assistance', {
                expandable = '1'
              }
            }

            property 'option3', {

              // Custom properties
              text = 'Nuisance'

              property 'value', value: '3_nuisance', {
                expandable = '1'
              }
            }

            property 'option4', {

              // Custom properties
              text = 'Tool Limiting'

              property 'value', value: '4_tool_limiting', {
                expandable = '1'
              }
            }

            property 'option5', {

              // Custom properties
              text = 'Performance'

              property 'value', value: '5_performance', {
                expandable = '1'
              }
            }

            property 'option6', {

              // Custom properties
              text = 'Blocking'

              property 'value', value: '6_blocking', {
                expandable = '1'
              }
            }
            optionCount = '6'
            type = 'list'
          }
          formType = 'standard'
        }

        property 'product', {

          // Custom properties

          property 'options', {

            // Custom properties

            property 'option1', {

              // Custom properties
              text = 'ElectricCommander'

              property 'value', value: 'electriccommander', {
                expandable = '1'
              }
            }

            property 'option2', {

              // Custom properties
              text = 'ElectricFlow'

              property 'value', value: 'electricflow', {
                expandable = '1'
              }
            }
            optionCount = '2'
            type = 'list'
          }
          formType = 'standard'
        }

        property 'serverResources', {

          // Custom properties
          formType = 'standard'
        }

        property 'sharefileConfiguration', {

          // Custom properties
          formType = 'standard'
        }

        property 'sharefileUploadDirectory', {

          // Custom properties
          formType = 'standard'
        }

        property 'stepId', {

          // Custom properties
          formType = 'standard'
        }

        property 'ticketDescription', {

          // Custom properties
          formType = 'standard'
        }

        property 'ticketTitle', {

          // Custom properties
          formType = 'standard'
        }

        property 'time', {

          // Custom properties
          formType = 'standard'
        }

        property 'version', {

          // Custom properties
          formType = 'standard'
        }

        property 'zendeskConfiguration', {

          // Custom properties
          formType = 'standard'
        }

        property 'zendeskCredential', {

          // Custom properties
          formType = 'standard'
        }
      }
    }
    ec_parameterForm = '''<editor>
    <formElement> 
        <label>Title</label> 
        <property>ticketTitle</property> 
        <documentation>The title of your ticket.</documentation> 
        <type>entry</type>
        <required>1</required>
    </formElement> 

    <formElement> 
        <label>Description</label> 
        <property>ticketDescription</property> 
        <documentation>The full description of your ticket.</documentation> 
        <type>textarea</type>
        <required>1</required>
    </formElement> 

    <formElement> 
        <label>Product</label> 
        <property>product</property> 
        <documentation>The name of the product with which you have an issue.</documentation> 
        <type>select</type> 
        <option> 
            <name>ElectricCommander</name> 
            <value>electriccommander</value> 
        </option> 
        <option> 
            <name>ElectricFlow</name> 
            <value>electricflow</value> 
        </option> 
        <value>electriccommander</value> 
        <required>1</required>
    </formElement> 

    <formElement> 
        <label>Problem Type</label> 
        <property>problemType</property> 
        <documentation>The type of problem, to help support prioritize your ticket.</documentation> 
        <type>select</type> 
        <option><name>Enhancement</name><value>1_enhancement</value></option> 
        <option><name>Need Assistance</name><value>2_need_assistance</value></option> 
        <option><name>Nuisance</name><value>3_nuisance</value></option> 
        <option><name>Tool Limiting</name><value>4_tool_limiting</value></option> 
        <option><name>Performance</name><value>5_performance</value></option> 
        <option><name>Blocking</name><value>6_blocking</value></option> 
        <value>2_need_assistance</value> 
        <required>1</required>
    </formElement> 

    <formElement> 
        <label>Problem Scope</label> 
        <property>problemScope</property> 
        <documentation>The scope of the problem, to help support prioritize your ticket.</documentation> 
        <type>select</type> 
        <option><name>Test Setup</name><value>01_test_setup</value></option> 
        <option><name>One User</name><value>2_one_user</value></option> 
        <option><name>One Team</name><value>3_one_team</value></option> 
        <option><name>Some users</name><value>4_some_users</value></option> 
        <option><name>One Site</name><value>5_one_site</value></option> 
        <option><name>Some teams</name><value>6_some_teams</value></option> 
        <option><name>Some sites</name><value>7_some_sites</value></option> 
        <option><name>Everyone</name><value>8_everyone</value></option> 
        <value>2_one_user</value> 
        <required>1</required>
    </formElement> 

    <formElement>
        <label>Gathering Resource</label>
        <property>gatheringResource</property>
         <documentation>The resource to gather the logs, open ticket and upload logs.</documentation> 
        <type>entry</type>
        <required>1</required>
        <value>local</value>       
    </formElement> 

    <formElement>
        <label>Server Resources</label>
        <property>serverResources</property>
         <documentation>A list of resources -comma separated- or pools to represent your server list. Change the default value for cluster mode.</documentation> 
        <type>entry</type>
        <required>1</required>
        <value>default</value>       
    </formElement> 

    <formElement> 
        <label>Zendesk configuration</label> 
        <property>zendeskConfiguration</property> 
        <documentation>The name of the Zendesk configuration to use.</documentation> 
        <type>entry</type>
        <value>zendesk</value>
        <required>1</required>
    </formElement> 
    
    <formElement> 
        <label>ShareFile configuration</label> 
        <property>sharefileConfiguration</property> 
        <documentation>The name of the ShareFile configuration to use.</documentation> 
        <type>entry</type>
        <value>sharefile</value>
        <required>1</required>
    </formElement> 

    <formElement> 
        <label>ShareFile upload directory</label> 
        <property>sharefileUploadDirectory</property> 
        <documentation>The path to your EC-ShareFile uploads directory.</documentation> 
        <type>entry</type>
        <value>/clients/S-U/Test/uploads</value>
        <required>1</required>
    </formElement> 

    <formElement> 
        <label>Agent List</label> 
        <property>agents</property> 
        <documentation>The comma separated list of agents where the problem occured.</documentation> 
        <type>entry</type>
    </formElement> 

    <formElement> 
        <label>Time</label> 
        <property>time</property> 
        <documentation>The time at which the issue happened (if known). Format is [yyyy-mm-dd] HH:mm.</documentation> 
        <type>entry</type>
    </formElement> 

    <formElement> 
        <label>Job Id</label> 
        <property>jobNumber</property> 
        <documentation>The jobId in which the issue occured (if known). It is used to retrieve the correct logs.</documentation> 
        <type>entry</type>
    </formElement> 

</editor>
'''
  }

  procedure 'sub-collectServerLogs', {
    description = 'A sub-procedure to collect the logs from 1 commander server'
    jobNameTemplate = ''
    resourceName = '$[serverResource]'
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workspaceName = ''

    formalParameter 'destinationDirectory', defaultValue: '', {
      description = 'Directory where to copy the logs on the target system'
      expansionDeferred = '0'
      label = null
      orderIndex = null
      required = '1'
      type = 'entry'
    }

    formalParameter 'serverResource', defaultValue: '', {
      description = 'The name of the commander server resource to grab the  logs from'
      expansionDeferred = '0'
      label = null
      orderIndex = null
      required = '1'
      type = 'entry'
    }

    formalParameter 'targetServerResource', defaultValue: '', {
      description = 'Name of the resource onto which to copy the logs'
      expansionDeferred = '0'
      label = null
      orderIndex = null
      required = '0'
      type = 'entry'
    }

    step 'grabWorkspaces', {
      description = '''grab the workspace value
 - not defined: default
 - empty: default
 -else: pass the value'''
      alwaysRun = '0'
      broadcast = '0'
      command = '''$[/plugins[EC-Admin]project/scripts/perlHeaderJSON]

#
# parameter
#

# Get the source workspace
my $sourceWks=getP("/resources/$[serverResource]/workspaceName");
printf("Source workspace: \'%s\'\\n", $sourceWks);
if (($sourceWks == undef) || ($sourceWks eq "")) {
	$ec->setProperty("/myJob/sourceWorkspace", "default");
} else {
	$ec->setProperty("/myJob/sourceWorkspace", $sourceWks);
}

# Get the source workspace
my $targetWks=getP("/resources/$[targetServerResource]/workspaceName");
printf("Target workspace: \'%s\'\\n", $targetWks);
if (($targetWks == undef) || ($targetWks eq "")) {
	$ec->setProperty("/myJob/targetWorkspace", "default");
} else {
	$ec->setProperty("/myJob/targetWorkspace", $targetWks);
}

$[/plugins[EC-Admin]project/scripts/perlLibJSON]

'''
      condition = ''
      errorHandling = 'failProcedure'
      exclusiveMode = 'none'
      logFileName = ''
      parallel = '0'
      postProcessor = ''
      precondition = ''
      releaseMode = 'none'
      resourceName = ''
      shell = 'ec-perl'
      subprocedure = null
      subproject = null
      timeLimit = ''
      timeLimitUnits = 'minutes'
      workingDirectory = ''
      workspaceName = ''
    }

    step 'copyCommander_log', {
      description = ''
      alwaysRun = '0'
      broadcast = '0'
      command = null
      condition = ''
      errorHandling = 'failProcedure'
      exclusiveMode = 'none'
      logFileName = null
      parallel = '0'
      postProcessor = null
      precondition = ''
      releaseMode = 'none'
      resourceName = '$[serverResource]'
      shell = null
      subprocedure = 'Remote Copy - Native'
      subproject = '/plugins/EC-FileOps/project'
      timeLimit = ''
      timeLimitUnits = 'minutes'
      workingDirectory = null
      workspaceName = ''
      actualParameter '''destinationFile''', '''$[destinationDirectory]/servers/$[serverResource]/'''
      actualParameter '''destinationResourceName''', '''$[targetServerResource]'''
      actualParameter '''destinationWorkspaceName''', '''$[/myJob/targetWorkspace]'''
      actualParameter '''sourceFile''', '''$[/server/Electric Cloud/dataDirectory]/logs/*commander.log'''
      actualParameter '''sourceResourceName''', '''$[serverResource]'''
      actualParameter '''sourceWorkspaceName''', '''$[/myJob/sourceWorkspace]'''
    }

    step 'copyService_log', {
      description = ''
      alwaysRun = '0'
      broadcast = '0'
      command = null
      condition = ''
      errorHandling = 'failProcedure'
      exclusiveMode = 'none'
      logFileName = null
      parallel = '0'
      postProcessor = null
      precondition = ''
      releaseMode = 'none'
      resourceName = '$[serverResource]'
      shell = null
      subprocedure = 'Remote Copy - Native'
      subproject = '/plugins/EC-FileOps/project'
      timeLimit = ''
      timeLimitUnits = 'minutes'
      workingDirectory = null
      workspaceName = ''
      actualParameter '''destinationFile''', '''$[destinationDirectory]/servers/$[serverResource]/'''
      actualParameter '''destinationResourceName''', '''$[targetServerResource]'''
      actualParameter '''destinationWorkspaceName''', '''$[/myJob/targetWorkspace]'''
      actualParameter '''sourceFile''', '''$[/server/Electric Cloud/dataDirectory]/logs/service.log'''
      actualParameter '''sourceResourceName''', '''$[serverResource]'''
      actualParameter '''sourceWorkspaceName''', '''$[/myJob/sourceWorkspace]'''
    }

    step 'collectTimeBasedLogs', {
      description = 'Collect the logs based on a time'
      alwaysRun = '0'
      broadcast = '0'
      command = '''#############################################################################
#
# Copyright Electric-Cloud 2015
#
#############################################################################
use Time::Local;
use Cwd;

$[/plugins[EC-Admin]project/scripts/perlHeaderJSON]

#############################################################################
#
# Parameters
#
#############################################################################
my $timeString = "$[time]";
my $serverResource = \'$[serverResource]\';	# name of the remote Server

#############################################################################
#
# Global Variables
#
#############################################################################
my $DEBUG=1;
my $logDir="$[/server/Electric Cloud/dataDirectory]/logs";
my $cwd= getcwd();

my $serverEpochTime=convertTimeToEpoch($timeString);

opendir(LOG, $logDir) or die("Cannot open the log directory\\n$!");

while (my $file = readdir(LOG)) {
    next if ($file !~ m/commander[\\-\\d.]*.log.zip/);
    # printf("Processing $file\\n") if ($DEBUG);
    my $fileModificationTime = (stat("$logDir/$file"))[9];      # get modification time
    # printf("    time: %d\\n", $fileModificationTime);
    if ($fileModificationTime >= $serverEpochTime) {
    	$ec->createJobStep({
        	subproject   => "/plugins/EC-FileOps/project",
            subprocedure => "Remote Copy - Native",
            jobStepName  => "Copy $file",
            actualParameter => [
            	{actualParameterName => \'sourceFile\',        
                               value => "$logDir/$file"},
            	{actualParameterName => \'sourceResourceName\',   
                               value => "$serverResource"},
            	{actualParameterName => \'sourceWorkspaceName\',   
                               value => "$[/myJob/sourceWorkspace]"},    
                                    
                {actualParameterName => \'destinationFile\',         
                               value => "$[destinationDirectory]/servers/$serverResource/"},
            	{actualParameterName => \'destinationResourceName\', 
                               value => "$[targetServerResource]"},
            	{actualParameterName => \'destinationWorkspaceName\',   
                               value => "$[/myJob/targetWorkspace]"},            	
            ],
        });
    }
}
closedir(LOG);


#############################################################################
#
# convertTime
# Time is of format \'MM/DD/YYYY 11:35:00\' or 11:35:00 or 11:35
#############################################################################
sub convertTimeToEpoch {
    my $timeStr=shift @_;
    
    # Get passed time
    my($date, $time);

    if ($timeStr =~ m/\\s+/) {
        ($date, $time)=split(\'\\s+\', $timeStr);
    } else {
        $time=$timeStr;
        $date="";
    }
    my ($year, $month, $day) = split(/[.\\/\\-]/, $date);
    my ($hour, $min, $sec)   = split(/[:]/, $time);

    printf("Incident time (original): %s-%s-%s %s:%s\\n", $year, $month, $day, $hour, $min) if ($DEBUG);

    # get server time to fill missing values
    my $localTime = "$[/timestamp MM-dd-yyyy HH:mm]";
    my ($localMonth,$localDay,$localYear,$localHour,$localMinute) = split(/[\\s\\-:]+/, $localTime);

    printf("Local month: $localMonth\\n") if ($DEBUG);
    $year = $localYear if ($year == 0);
    $year += 2000 if ($year < 100);
    $month = $localMonth if ($month == 0);
    $day   = $localDay if ($day == 0);

    printf("Incident time: %s-%s-%s %s:%s\\n", $year, $month, $day, $hour, $min) if ($DEBUG);
    my $time = timelocal(0,$min,$hour,$day,$month-1,$year);

    printf("Time of the incident: %s \\n", $time) if ($DEBUG);
    return $time
}

'''
      condition = '$[/javascript "$[time]" != "" ]'
      errorHandling = 'failProcedure'
      exclusiveMode = 'none'
      logFileName = ''
      parallel = '0'
      postProcessor = ''
      precondition = ''
      releaseMode = 'none'
      resourceName = '$[serverResource]'
      shell = 'ec-perl'
      subprocedure = null
      subproject = null
      timeLimit = ''
      timeLimitUnits = 'minutes'
      workingDirectory = ''
      workspaceName = ''
    }

    step 'collectJobIdBasedLogs', {
      description = 'Collect the logs based on a time'
      alwaysRun = '0'
      broadcast = '0'
      command = '''#############################################################################
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
my $jobNumber = "$[jobNumber]";
my $serverResource = "$[serverResource]";

#############################################################################
#
# Global Variables
#
#############################################################################
my $DEBUG=1;
my $logDir="$[/server/Electric Cloud/dataDirectory]/logs";
my $cwd= getcwd();

opendir(my $logD, $logDir) or die("Cannot open the log directory\\n$!");

while (my $file = readdir($logD)) {
    next if ($file !~ m/commander[\\-\\d.]*.log.zip/);
    printf("Processing $file\\n") if ($DEBUG);

my $exitCode=system("zgrep jobId=$jobNumber $logDir/$file 2>&1");
    if ($exitCode == 0) {
    	printf("    Copying\\n");
    	$ec->createJobStep({
        	subproject   => "/plugins/EC-FileOps/project",
            subprocedure => "Copy",
            jobStepName  => "Copy $file",
            actualParameter => [
            	{actualParameterName => \'sourceFile\',        
                               value => "$logDir/$file"},
            	{actualParameterName => \'sourceResourceName\',   
                               value => "$serverResource"},
            	{actualParameterName => \'sourceWorkspaceName\',   
                               value => "$[/myJob/sourceWorkspace]"},    
                                    
            	{actualParameterName => \'destinationFile\',         
                               value => "$[destinationDirectory]/servers/$serverResource/"},
            	{actualParameterName => \'destinationResourceName\', 
                               value => "$[targetServerResource]"},
            	{actualParameterName => \'destinationWorkspaceName\',   
                               	value => "$[/myJob/targetWorkspace]",            	
            ],
        });
    }
}
closedir($logD);

'''
      condition = '$[/javascript "$[jobNumber]" != "" ]'
      errorHandling = 'failProcedure'
      exclusiveMode = 'none'
      logFileName = ''
      parallel = '0'
      postProcessor = ''
      precondition = ''
      releaseMode = 'none'
      resourceName = '$[serverResource]'
      shell = 'ec-perl'
      subprocedure = null
      subproject = null
      timeLimit = ''
      timeLimitUnits = 'minutes'
      workingDirectory = ''
      workspaceName = ''
    }

    // Custom properties

    property 'ec_customEditorData', {

      // Custom properties

      property 'parameters', {

        // Custom properties

        property 'agents', {

          // Custom properties
          formType = 'standard'
        }

        property 'destinationDirectory', {

          // Custom properties
          formType = 'standard'
        }

        property 'jobNumber', {

          // Custom properties
          formType = 'standard'
        }

        property 'product', {

          // Custom properties

          property 'options', {

            // Custom properties

            property 'option1', {

              // Custom properties
              text = 'ElectricCommander'

              property 'value', value: 'electriccommander', {
                expandable = '1'
              }
            }

            property 'option2', {

              // Custom properties
              text = 'ElectricFlow'

              property 'value', value: 'electricflow', {
                expandable = '1'
              }
            }
            optionCount = '2'
            type = 'list'
          }
          formType = 'standard'
        }

        property 'serverResource', {

          // Custom properties
          formType = 'standard'
        }

        property 'stepId', {

          // Custom properties
          formType = 'standard'
        }

        property 'targetServerResource', {

          // Custom properties
          formType = 'standard'
        }

        property 'ticketDescription', {

          // Custom properties
          formType = 'standard'
        }

        property 'ticketTitle', {

          // Custom properties
          formType = 'standard'
        }

        property 'time', {

          // Custom properties
          formType = 'standard'
        }

        property 'version', {

          // Custom properties
          formType = 'standard'
        }
      }
    }
    ec_parameterForm = '''<editor>
    <formElement> 
        <label>Title</label> 
        <property>ticketTitle</property> 
        <documentation>The title of your ticket.</documentation> 
        <type>entry</type>
        <required>1</required>
    </formElement> 

    <formElement> 
        <label>Description</label> 
        <property>ticketDescription</property> 
        <documentation>The full description of your ticket.</documentation> 
        <type>textarea</type>
        <required>1</required>
    </formElement> 

    <formElement> 
        <label>Product</label> 
        <property>product</property> 
        <documentation>The name of the product with which you have an issue.</documentation> 
        <type>select</type> 
        <option> 
            <name>ElectricCommander</name> 
            <value>electriccommander</value> 
        </option> 
        <option> 
            <name>ElectricFlow</name> 
            <value>electricflow</value> 
        </option> 
        <value>electriccommander</value> 
        <required>1</required>
    </formElement> 

    <formElement> 
        <label>Product Version</label> 
        <property>version</property> 
        <documentation>Product version.</documentation> 
        <type>entry</type>
    </formElement> 


    <formElement> 
        <label>Agent List</label> 
        <property>agents</property> 
        <documentation>The comma separated list of agents where the problem occured.</documentation> 
        <type>entry</type>
    </formElement> 

    <formElement> 
        <label>Time</label> 
        <property>time</property> 
        <documentation>The time at which the issue happened (if known). Format is [yyyy-mm-dd] HH:mm.</documentation> 
        <type>entry</type>
    </formElement> 

    <formElement> 
        <label>Job Id</label> 
        <property>jobNumber</property> 
        <documentation>The jobId in which the issue occured (if known). It is used to retrieve the correct logs.</documentation> 
        <type>entry</type>
    </formElement> 

</editor>
'''
  }

  // Custom properties
  ec_setup = '''# Data that drives the create step picker registration for this plugin.
@::createStepPickerSteps = ();
'''
  ec_tags = ''
  ec_visibility = 'pickListOnly'
  help = '''<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta content="text/html; charset=us-ascii" http-equiv="content-type" />
    <title>EC-Support Plugin</title>
    <link rel="stylesheet" href= "../../plugins/EC-Support-1.4.0.44/pluginhelp.css" type="text/css" media= "screen" />
</head>

<body>
    <div class="help">
        <h1>EC-Support</h1>
		<p>Plugin Version 1.4.0.44</p>
        <hr style="margin-left: -10px; margin-top: 10px; height: 1px; width: 100%; color: #5981BD;" noshade="noshade" />

        <p>EC-Support is a collection of procedures to help you interact with Electric Cloud support. It requires both 
        <a href="/commander/pages/EC-Zendesk/help">EC-Zendesk</a> and 
        <a href="/commander/pages/EC-ShareFile/help">EC-ShareFile</a> plugins.</p>

		<p><b>Note:</b> this plugin was part of the bundle who helped Laurent Rochette and Nikhil Vaze won "Best in Show" at the Citrix Synergy 2015 conference.</p>

		<h1>Plugin Procedures</h1>

	    <p>
	        IMPORTANT: For all parameter descriptions below, required
	        parameters are shown in <span class="required">bold
	        italics</span>.
	    </p>
    
    	<h2>openSupportTicket</h2>

        <p>This procedure is the workhorse of this plugin, it opens a ticket on Zendesk, gather the required logs and upload them on ShareFile automatically.
    	</p>

	    <table class="grid">
      <thead><tr><th>Parameter</th><th>Description</th></tr></thead>
	        <tbody>
	            <tr>
	                <td class="required">Title</td>
	                <td>The subject of your ticket.</td>
	            </tr>
	            <tr>
	                <td class="required">Description</td>
	                <td>The complete description of your ticket. Be as specific as possible.</td>
	            </tr>
	            <tr>
	                <td class="required">Product</td>
	                <td>ElectricFlow or ElectricCommander</td>
	            </tr>
              <tr>
                <td class="required">Gathering Resource</td>
                <td>The resource used on which to gather the logs. Useful in 
                case your server does not have internet access.</td>
              </tr>
	           <tr>
	                <td class="required">Server Resources</td>
	                <td>The resource associated to your server (default: local). 
                  If you have a clustered configuration, enter a comma separated 
                  list of agents or a pool.</td>
	            </tr>
           		<tr>
	                <td class="required">Zendesk Configuration</td>
	                <td>The configuration in the <
                  a href="/commander/pages/EC-Zendesk/help">EC-Zendesk plugin</a> 
                  that contain login/password to connect to Zendesk and open a 
                  ticket.</td>
	            </tr>
           		<tr>
	                <td class="required">ShareFile Configuration</td>
	                <td>The configuration in the 
                  <a href="/commander/pages/EC-ShareFile/help">EC-ShareFile plugin</a> 
                  that contain login/password to connect to ShareFile to upload 
                  the logs.</td>
	            </tr>
           		<tr>
	                <td class="required">ShareFile Upload Directory</td>
	                <td>Your company directory on ShareFile where to upload the 
                  logs files. The directory is usually something like 
                  /clients/X-Z/NAME/uploads. Check on ShareFile if you are unsure.</td>
	            </tr>
           		<tr>
	                <td>Agent List</td>
	                <td>This optional parameter allows to  pass a list of agents 
                  -comma separated- for which the logs will be gathered and 
                  uploaded automatically.</td>
	            </tr>		        
           		<tr>
	                <td>Time</td>
	                <td>If this optional parameter is set, all the commander logs 
                  from that time on will be gathered from all servers and uploaded 
                  automatically.</td>
	            </tr>		        
           		<tr>
	                <td>Job Id</td>
	                <td>If this optional parameter is set, all the commander logs 
                  containing this jobId will be gathered from all servers and 
                  uploaded automatically.</td>
	            </tr>		        
	        </tbody>
    	</table>
    	<img src="../../plugins/EC-Support/images/help/openSupportTicket.png" alt="form" border="1"/>

      <h2>gatherLogs</h2>

        <p>This procedure is a simplified version of the previous one. It 
        gathers the logs identically but does not do any call to Zendesk or 
        ShareFile. Very useful for users without external internet access.
    	</p>

	    <table class="grid">
	        <thead><tr><th>Parameter</th><th>Description</th></tr></thead>
	        <tbody>
             <tr>
               <td class="required">Gathering Resource</td>
               <td>The resource used on which to gather the logs.</td>
             </tr>
	           <tr>
	                <td class="required">Server Resources</td>
	                <td>The resource associated to your server (default: local). 
                  If you have a clustered configuration, enter a comma separated 
                  list of agents or a pool.</td>
	            </tr>
           		<tr>
	                <td>Agent List</td>
	                <td>This optional parameter allows to  pass a list of agents 
                  -comma separated- for which the logs will be gathered and 
                  uploaded automatically.</td>
	            </tr>		        
           		<tr>
	                <td>Time</td>
	                <td>If this optional parameter is set, all the commander logs 
                  from that time on will be gathered from all servers and 
                  uploaded automatically.</td>
	            </tr>		        
           		<tr>
	                <td>Job Id</td>
	                <td>If this optional parameter is set, all the commander logs 
                  containing this jobId will be gathered from all servers and 
                  uploaded automatically.</td>
	            </tr>		        
              <tr>
	                <td>Create Artifact</td>
	                <td>Checkbox to create a EC-Support:logs artifact version that 
                  you can download later to you desktop.</td>
	            </tr>		        
	        </tbody>
    	</table>
  
	</div>
</body>
</html>'''
  pluginBuildNumber = '47'
  pluginDependencies = '''EC-Zendesk: 1.3.0
EC-ShareFile: 1.2.1
EC-Admin: 1.3.2'''
  project_version = '1.4.0.44'
  version = '1.4.1'
}
