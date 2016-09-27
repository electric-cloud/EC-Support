
procedure 'AddLogsToExistingTicket', {
  description = 'A procedure to automatically add the required logs to ShareFile and comment on an existing ticket'
  resourceName = 'local'

  formalParameter 'agents', {
    description = 'a commas separated list of agents. Will be used to get the logs from the agents involved in the issue'
    required = '0'
    type = 'entry'
  }

  formalParameter 'jobNumber', {
    description = 'The ID of the job that generated the error. Will be used to collect the right logs'
    required = '0'
    type = 'entry'
  }

  formalParameter 'product', defaultValue: 'electricflow', {
    description = 'the name of the product that failed'
    required = '1'
    type = 'select'
  }

  formalParameter 'serverResources', defaultValue: 'local', {
    description = 'A list of resources or pools, comma separated'
    required = '0'
    type = 'entry'
  }

  formalParameter 'sharefileConfiguration', defaultValue: 'sharefile', {
    description = 'Name of your Sharefile configuration'
    required = '1'
    type = 'entry'
  }

  formalParameter 'sharefileUploadDirectory', {
    description = 'Private directory on ShareFile on where to upload the logs'
    required = '1'
    type = 'entry'
  }

  formalParameter 'ticketComment', {
    description = 'The comment to add to the ticket along the files'
    required = '0'
    type = 'textarea'
  }

  formalParameter 'ticketId', defaultValue: '', {
    required = '1'
    type = 'entry'
  }

  formalParameter 'time', defaultValue: '', {
    description = 'The time at which the issue happened. Used to collect the right logs. If empty, will simply send the commander.log'
    required = '0'
    type = 'entry'
  }

  formalParameter 'zendeskConfiguration', defaultValue: 'zendesk', {
    description = 'The name of your Zendesk configuration'
    required = '1'
    type = 'entry'
  }

  step 'Init', {
    description = 'Assign ticketId to global job property'
    command = new File(pluginDir + "/dsl/procedures/AddLogsToExistingTicket/steps/Init.pl").text
    shell = 'ec-perl'
  }

  step 'grabResource', {
    description = 'Grab one of the commander server resources (in case of cluster)'
    command = new File(pluginDir + "/dsl/procedures/AddLogsToExistingTicket/steps/grabResource.sh").text
  }

  step 'grabDestinationDir', {
    command = new File(pluginDir + "/dsl/procedures/AddLogsToExistingTicket/steps/grabDestinationDir.pl").text
    shell = 'ec-perl'
  }

  step 'createTicketDirectory', {
    description = 'create a directory to collect the logs'
    resourceName = '$[/myJob/assignedServerResource]'
    subprocedure = 'CreateDirectory'
    subproject = '/plugins/EC-FileOps/project'
    actualParameter 'Path', '$' + '[/myJob/destinationDirectory]'
  }

  step 'collectServerLogs', {
    subprocedure = 'collectServerLogs'
    actualParameter 'destinationDirectory', '$' + '[/myJob/destinationDirectory]'
    actualParameter 'jobNumber', '$' + '[jobNumber]'
    actualParameter 'serverResources', '$' + '[serverResources]'
    actualParameter 'time', '$' + '[time]'
  }

  step 'collectAgentLogs', {
    description = 'If agent list is not empty, go grab agent.log and jagent.log'
    command = new File(pluginDir + "/dsl/procedures/AddLogsToExistingTicket/steps/collectAgentLogs.pl").text
    condition = '$' + '[/javascript "$' + '[agents]" != "" ]'
    shell = 'ec-perl'
  }

  step 'listing', {
    description = 'Get the list of collected files'
    command = new File(pluginDir + "/dsl/procedures/AddLogsToExistingTicket/steps/listing.pl").text
    resourceName = '$' + '[/myJob/assignedServerResource]'
    shell = 'ec-perl'
  }

  step 'createBundle', {
    description = 'Zip the different files'
    subprocedure = 'Create Zip File'
    actualParameter 'sourceFile', '$' + '[/myJob/destinationDirectory]'
    actualParameter 'zipFile', '$' + '[/myJob/destinationDirectory].zip'
  }

  step 'uploadBundleToSharefile', {
    subprocedure = 'CreateFolderAndUploadFile'
    subproject = '/plugins/EC-ShareFile/project'
    actualParameter 'company', 'electric-cloud'
    actualParameter 'config', '$' + '[ShareFileConfiguration]'
    actualParameter 'folderToCreate', '$' + '[sharefileUploadDirectory]/$' + '[/myJob/zendesk/ticketId]'
    actualParameter 'pathToFile', '$' + '[/myJob/destinationDirectory].zip'
  }

  step 'commentForFiles', {
    condition = '1'
    subprocedure = 'commentOnTicket'
    subproject = '/plugins/EC-Zendesk/project'
    actualParameter 'comment', '$' +'''[ticketComment]

The following file have been uploaded  $[sharefileUploadDirectory]/$[/myJob/zendesk/ticketId]/$[/myJob/zendesk/ticketId].zip
It contains:

$[/myJob/fileList]'''
    actualParameter 'credential', '$' + '[zendeskConfiguration]'
    actualParameter 'ticketNumber', '$' + '[/myJob/zendesk/ticketId]'
  }

  // Custom properties
  property 'ec_customEditorData', {
    property 'parameters', {
      property 'product', {
        property 'options', {
          property 'option1', {
            text = 'ElectricCommander'
            property 'value', value: 'electriccommander', {
              expandable = '1'
            }
          }
          property 'option2', {
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
    }
  }
  ec_parameterForm = new File(pluginDir + "/dsl/procedures/AddLogsToExistingTicket/form.xml").text
}
