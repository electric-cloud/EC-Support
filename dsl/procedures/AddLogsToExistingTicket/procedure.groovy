
procedure 'AddLogsToExistingTicket',
  description: 'A procedure to automatically add the required logs to ShareFile and comment on an existing ticket',
  resourceName: 'local',
{
  step 'Init',
    description: 'Assign ticketId to global job property',
    command: new File(pluginDir + "/dsl/procedures/AddLogsToExistingTicket/steps/Init.pl").text,
    shell: 'ec-perl',
    resourceName: '$' + '[gatheringResource]'

  step 'grabResource',
    description: 'Grab one of the commander server resources (in case of cluster)',
    command: new File(pluginDir + "/dsl/procedures/AddLogsToExistingTicket/steps/grabResource.sh").text,
    resourceName: '$' + '[gatheringResource]'

  step 'gatherLogs',
    subprocedure: 'gatherLogs',
    actualParameter: [
      gatheringResource: '$[/myJob/gatheringResource]',
      serverResources: '$[serverResources]',
      agents: '$[agents]',
      web: '$[web]',
      repo: '$[repo]',
      time: '$[time]',
      jobNumber: '$[jobNumber]',
      obfuscate: '$[obfuscate]',
      createArtifact: 'false'
    ]

  step 'uploadBundleToSharefile',
    resourceName: '$[/myJob/gatheringResource]',
    subprocedure: 'CreateFolderAndUploadFile',
    subproject: '/plugins/EC-ShareFile/project',
    actualParameter: [
      config: '$[ShareFileConfiguration]',
      pathToFile: '$[/myJob/destinationDirectory].zip',
      folder: '$[/myJob/zendesk/ticketId]'
    ]

  step 'commentForFiles',
    condition: '1',
    resourceName: '$[/myJob/gatheringResource]',
    subprocedure: 'commentOnTicket',
    subproject: '/plugins/EC-Zendesk/project',
    actualParameter: [
      ticketComment: '''The following file have been uploaded to Sharefile in the /$[/myJob/zendesk/ticketId] folder:
It contains:
$[/myJob/fileList]''',
      config: '$[zendeskConfiguration]',
      ticketNumber: '$[/myJob/zendesk/ticketId]'
    ]
}
