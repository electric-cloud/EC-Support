
procedure 'openSupportTicket',
  description: 'A procedure to automatically open a ticket on Zendesk and deliver the required logs to ShareFile',
  resourceName: 'local',
{
  formalParameter 'agents',
    description: 'a commas separated list of agents. Will be used to get the logs from the agents involved in the issue',
    required: '0',
    type: 'entry'

  formalParameter 'gatheringResource',
    defaultValue: 'local',
    description: 'The resource to use to gather log and open ticket',
    required: '1',
    type: 'entry'

  formalParameter 'jobNumber',
    description: 'The ID of the job that generated the error. Will be used to collect the right logs',
    type: 'entry'

  formalParameter 'problemScope',
    defaultValue: '2_one_user',
    required: '1',
    type: 'select'

  formalParameter 'problemType',
    defaultValue: '2_need_assistance',
    required: '1',
    type: 'select'

  formalParameter 'product',
    defaultValue: 'electricflow',
    description: 'the name of the product that failed',
    required: '1',
    type: 'select'

  formalParameter 'serverResources',
    defaultValue: 'default',
    description: 'A list of resources or pools, comma separated',
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

  formalParameter 'ticketDescription',
    description: 'The main description of the issue you are facing',
    type: 'textarea'

  formalParameter 'ticketTitle',
    required: '1',
    type: 'entry'

  formalParameter 'time',
    description: 'The time at which the issue happened. Used to collect the right logs. If empty, will simply send the commander.log',
    type: 'entry'

  formalParameter 'zendeskConfiguration',
    defaultValue: 'zendesk',
    description: 'The name of your Zendesk configuration',
    required: '1',
    type: 'entry'


  step 'getVersion',
    description: 'Retrieve the server version',
    command: new File(pluginDir + "/dsl/procedures/openSupportTicket/steps/getVersion.pl").text,
    resourceName: '$' + '[gatheringResource]',
    shell: 'ec-perl'

  step 'grabResource',
    description: 'Grab one of the commander server resources (in case of cluster)',
    command: new File(pluginDir + "/dsl/procedures/openSupportTicket/steps/grabResource.sh").text,
    resourceName: '$' + '[gatheringResource]'

  step 'createTicket',
    description: 'Open the ticket',
    condition: '1',
    resourceName: '$' + '[/myJob/gatheringResource]',
    subprocedure: 'createTicket',
    subproject: '/plugins/EC-Zendesk/project',
    actualParameter: [
      credential: '$' + '[zendeskConfiguration]',
      problemScope: '$' + '[problemScope]',
      problemType: '$' + '[problemType]',
      product: '$' + '[product]',
      ticketDescription: '$' + '[ticketDescription]',
      ticketSubject: '$' + '[ticketTitle]',
      version: '$' + '[/myJob/serverVersion]'
    ]

  step 'grabDestinationDir',
    command: new File(pluginDir + "/dsl/procedures/openSupportTicket/steps/grabDestinationDir.pl").text,
    resourceName: '$' + '[/myJob/gatheringResource]',
    shell: 'ec-perl'

  step 'createTicketDirectory',
    description: 'create a directory to collect the logs',
    resourceName: '$' + '[/myJob/gatheringResource]',
    subprocedure: 'CreateDirectory',
    subproject: '/plugins/EC-FileOps/project',
    actualParameter: [
      Path: '$' + '[/myJob/destinationDirectory]'
    ]

  step 'collectServerLogs',
    subprocedure: 'collectServerLogs',
    actualParameter: [
      destinationDirectory: '$' + '[/myJob/destinationDirectory]',
      jobNumber: '$' + '[jobNumber]',
      serverResources: '$' + '[serverResources]',
      time: '$' + '[time]'
    ]

  step 'collectAgentLogs',
    description: 'If agent list is not empty, go grab agent.log and jagent.log',
    command: new File(pluginDir + "/dsl/procedures/openSupportTicket/steps/collectAgentLogs.pl").text,
    condition: '$' + '[/javascript "$' + '[agents]" != "" ]',
    shell: 'ec-perl'

  step 'listing',
    description: 'Get the list of collected files',
    command: new File(pluginDir + "/dsl/procedures/openSupportTicket/steps/listing.pl").text,
    resourceName: '$' + '[/myJob/gatheringResource]',
    shell: 'ec-perl'

  step 'createBundle',
    description: 'Zip the different files',
    resourceName: '$' + '[/myJob/gatheringResource]',
    subprocedure: 'Create Zip File',
    subproject: '/plugins/EC-FileOps/project',
    actualParameter: [
      sourceFile: '$' + '[/myJob/destinationDirectory]',
      zipFile: '$' + '[/myJob/destinationDirectory].zip'
    ]

  step 'uploadBundleToSharefile',
    resourceName: '$' + '[/myJob/gatheringResource]',
    subprocedure: 'CreateFolderAndUploadFile',
    subproject: '/plugins/EC-ShareFile/project',
    actualParameter: [
      company: 'electric-cloud',
      config: '$' + '[ShareFileConfiguration]',
      folderToCreate: '$' + '[sharefileUploadDirectory]/$' + '[/myJob/zendesk/ticketId]',
      pathToFile: '$' + '[/myJob/destinationDirectory].zip'
    ]

  step 'commentForFiles',
    condition: '1',
    resourceName: '$' + '[/myJob/gatheringResource]',
    subprocedure: 'commentOnTicket',
    subproject: '/plugins/EC-Zendesk/project',
    actualParameter: [
      comment: 'The following file have been uploaded  $' + '[sharefileUploadDirectory]/$' + '[/myJob/zendesk/ticketId]/$' + '''[/myJob/zendesk/ticketId].zip
It contains:

$''' + '[/myJob/fileList]',
      credential: '$' + '[zendeskConfiguration]',
      ticketNumber: '$' + '[/myJob/zendesk/ticketId]'
    ]

  property 'ec_customEditorData', {
    property 'parameters', {
      property 'problemScope', {
        property 'options', {
          property 'option1', {
            property 'text', value: 'Test Setup'
            property 'value', value: '1_test_setup'
          }
          property 'option2', {
            property 'text', value: 'One User'
            property 'value', value: '2_one_user'
          }
          property 'option3', {
            property 'text', value: 'One Team'
            property 'value', value: '3_one_team'
          }
          property 'option4', {
            property 'text', value: 'Some users'
            property 'value', value: '4_some_users'
          }
          property 'option5', {
            property 'text', value: 'One Site'
            property 'value', value: '5_one_site'
          }
          property 'option6', {
            property 'text', value: 'Some teams'
            property 'value', value: '6_some_teams'
          }
          property 'option7', {
            property 'text', value: 'Some sites'
            property 'value', value: '7_some_sites'
          }
          property 'option8', {
            property 'text', value: 'Everyone'
            property 'value', value: '8_everyone'
          }
          property 'optionCount', value: '8'
          property 'type', value: 'list'
        }
        property 'formType', value: 'standard'
      }

      property 'problemType', {
        property 'options', {
          property 'option1', {
            property 'text', value: 'Enhancement'
            property 'value', value: '1_enhancement'
          }
          property 'option2', {
            property 'text', value: 'Need Assistance'
            property 'value', value: '2_need_assistance'
          }
          property 'option3', {
            property 'text', value: 'Nuisance'
            property 'value', value: '3_nuisance'
          }
          property 'option4', {
            property 'text', value: 'Tool Limiting'
            property 'value', value: '4_tool_limiting'
          }
          property 'option5', {
            property 'text', value: 'Performance'
            property 'value', value: '5_performance'
          }
          property 'option6', {
            property 'text', value: 'Blocking'
            property 'value', value: '6_blocking'
          }
          property 'optionCount', value: '6'
          property 'type', value: 'list'
        }
        property 'formType', value: 'standard'
      }

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
  property 'ec_parameterForm', value: new File(pluginDir + "/dsl/procedures/openSupportTicket/form.xml").text
}
