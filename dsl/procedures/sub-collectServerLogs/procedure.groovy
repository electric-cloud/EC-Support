
procedure 'sub-collectServerLogs',
  description: 'Do not call directly!!! This is a sub-procedure to collect the logs from 1 commander server',
  resourceName: '$' + '[serverResource]',
{
  step 'grabWorkspaces',
    description: '''grab the workspace value
 - not defined: default
 - empty: default
 - else: pass the value''',
    command: new File(pluginDir + "/dsl/procedures/sub-collectServerLogs/steps/grabWorkspaces.pl").text,
    shell: 'ec-perl'

  step 'copyCommander_log',
    resourceName: '$' + '[serverResource]',
    subprocedure: 'Remote Copy - Native',
    subproject: '/plugins/EC-FileOps/project',
    actualParameter: [
        destinationFile: '$' + '[destinationDirectory]/servers/$' + '[serverResource]/',
        destinationResourceName: '$' + '[targetServerResource]',
        destinationWorkspaceName: '$' + '[/myJob/targetWorkspace]',
        sourceFile: '$' + '[/server/Electric Cloud/dataDirectory]/logs/commander*.log',
        sourceResourceName: '$' + '[serverResource]',
        sourceWorkspaceName: '$' + '[/myJob/sourceWorkspace]'
    ]

  // step 'copyService_log',
  //   resourceName: '$' + '[serverResource]',
  //   subprocedure: 'Remote Copy - Native',
  //   subproject: '/plugins/EC-FileOps/project',
  //   actualParameter: [
  //       destinationFile: '$' + '[destinationDirectory]/servers/$' + '[serverResource]/',
  //       destinationResourceName: '$' + '[targetServerResource]',
  //       destinationWorkspaceName: '$' + '[/myJob/targetWorkspace]',
  //       sourceFile: '$' + '[/server/Electric Cloud/dataDirectory]/logs/commander-service.log',
  //       sourceResourceName: '$' + '[serverResource]',
  //       sourceWorkspaceName: '$' + '[/myJob/sourceWorkspace]'
  //   ]

  step 'collectTimeBasedLogs',
    description: 'Collect the logs based on a time',
    command: new File(pluginDir + "/dsl/procedures/sub-collectServerLogs/steps/collectTimeBasedLogs.pl").text,
    condition: '$' + '[/javascript "$' + '[time]" != "" ]',
    resourceName: '$' + '[serverResource]',
    shell: 'ec-perl'

  step 'collectJobIdBasedLogs',
    description: 'Collect the logs based on a time',
    command: new File(pluginDir + "/dsl/procedures/sub-collectServerLogs/steps/collectJobIdBasedLogs.pl").text,
    condition: '$' + '[/javascript "$' + '[jobNumber]" != "" ]',
    resourceName: '$' + '[serverResource]',
    shell: 'ec-perl'

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

  property 'ec_parameterForm', value: new File(pluginDir + "/dsl/procedures/sub-collectServerLogs/form.xml").text
}
