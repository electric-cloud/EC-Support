
procedure 'AddLogsToExistingTicket',
  description: 'A procedure to automatically add the required logs to ShareFile and comment on an existing ticket',
  resourceName: 'local',
{

  formalParameter 'agents',
      description: 'a commas separated list of agents. Will be used to get the logs from the agents involved in the issue',
      required: '0',
      type: 'entry'

  formalParameter 'jobNumber',
      description: 'The ID of the job that generated the error. Will be used to collect the right logs',
      required: '0',
      type: 'entry'

  formalParameter 'product',
      defaultValue: 'electricflow',
      description: 'the name of the product that failed',
      required: '1',
      type: 'select'

  formalParameter 'serverResources',
      defaultValue: 'local',
      description: 'A list of resources or pools, comma separated',
      required: '0',
      type: 'entry'

  formalParameter 'sharefileConfiguration',
      defaultValue: 'sharefile',
      description: 'Name of your Sharefile configuration',
      required: '1',
      type: 'entry'

  formalParameter 'sharefileUploadDirectory',
      description: 'Private directory on ShareFile on where to upload the logs',
      required: '1',
      type: 'entry'

  formalParameter 'ticketComment',
      description: 'The comment to add to the ticket along the files',
      required: '0',
      type: 'textarea'

  formalParameter 'ticketId',
      required: '1',
      type: 'entry'

  formalParameter 'time',
    description: 'The time at which the issue happened. Used to collect the right logs. If empty, will simply send the commander.log',
    required: '0',
    type: 'entry'

  formalParameter 'zendeskConfiguration',
    defaultValue: 'zendesk',
    description: 'The name of your Zendesk configuration',
    required: '1',
    type: 'entry'


  step 'Init',
    description: 'Assign ticketId to global job property',
    command: new File(pluginDir + "/dsl/procedures/AddLogsToExistingTicket/steps/Init.pl").text,
    shell: 'ec-perl',
    resourceName: ''


  step 'grabResource',
    description: 'Grab one of the commander server resources (in case of cluster)',
    command: new File(pluginDir + "/dsl/procedures/AddLogsToExistingTicket/steps/grabResource.sh").text,
    resourceName: ''

  step 'grabDestinationDir',
    command: new File(pluginDir + "/dsl/procedures/AddLogsToExistingTicket/steps/grabDestinationDir.pl").text,
    shell: 'ec-perl',
    resourceName: ''

  step 'createTicketDirectory',
    description: 'create a directory to collect the logs',
    resourceName: '$[/myJob/assignedServerResource]',
    subprocedure: 'CreateDirectory',
    subproject: '/plugins/EC-FileOps/project',
    actualParameter: [
        Path: '$' + '[/myJob/destinationDirectory]'
    ]

  step 'sub-collectAllServerLogs',
    subprocedure: 'sub-collectAllServerLogs',
    resourceName: '',
    actualParameter: [
        destinationDirectory: '$' + '[/myJob/destinationDirectory]',
        jobNumber: '$' + '[jobNumber]',
        serverResources: '$' + '[serverResources]',
        time: '$' + '[time]'
    ]

  step 'collectAgentLogs',
    description: 'If agent list is not empty, go grab agent.log and jagent.log',
    command: new File(pluginDir + "/dsl/procedures/AddLogsToExistingTicket/steps/collectAgentLogs.pl").text,
    condition: '$' + '[/javascript "$' + '[agents]" != "" ]',
    shell: 'ec-perl',
    resourceName: ''

  step 'listing',
    description: 'Get the list of collected files',
    command: new File(pluginDir + "/dsl/procedures/AddLogsToExistingTicket/steps/listing.pl").text,
    resourceName: '$' + '[/myJob/assignedServerResource]',
    shell: 'ec-perl'

  step 'createBundle',
    description: 'Zip the different files',
    subprocedure: 'Create Zip File',
    subproject: '/plugins/EC-ShareFile/project',
    resourceName: '',
    actualParameter: [
        sourceFile: '$' + '[/myJob/destinationDirectory]',
        zipFile: '$' + '[/myJob/destinationDirectory].zip'
    ]

  step 'uploadBundleToSharefile',
    subprocedure: 'CreateFolderAndUploadFile',
    subproject: '/plugins/EC-ShareFile/project',
    resourceName: '',
    actualParameter: [
        company: 'electric-cloud',
        config: '$' + '[ShareFileConfiguration]',
        folderToCreate: '$' + '[sharefileUploadDirectory]/$' + '[/myJob/zendesk/ticketId]',
        pathToFile: '$' + '[/myJob/destinationDirectory].zip'
    ]

  step 'commentForFiles',
    condition: '1',
    subprocedure: 'commentOnTicket',
    subproject: '/plugins/EC-Zendesk/project',
    resourceName: '',
    actualParameter: [
        comment: '$' +'''[ticketComment]

The following file have been uploaded  $''' + '[sharefileUploadDirectory]/$' + '''[/myJob/zendesk/ticketId]/$[/myJob/zendesk/ticketId].zip
It contains:

$''' + '[/myJob/fileList]',
        credential: '$' + '[zendeskConfiguration]',
        ticketNumber: '$' + '[/myJob/zendesk/ticketId]'
    ]

  // Custom properties
  property 'ec_customEditorData', {
    property 'parameters', {
      property 'product', {
        property 'options', {
          property 'option1', {
            property 'text', value: 'ElectricCommander'
            property 'value', value: 'electriccommander'
          }
          property 'option2', {
            property 'text', value: 'ElectricFlow'
            property 'value', value: 'electricflow'
          }
          property 'optionCount', value: '2'
          property 'type', value: 'list'
        }
        property 'formType', value: 'standard'
      }
    }
  }
  property 'ec_parameterForm', value: new File(pluginDir + "/dsl/procedures/AddLogsToExistingTicket/form.xml").text

}
