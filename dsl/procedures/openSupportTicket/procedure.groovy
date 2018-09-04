
procedure 'openSupportTicket',
  description: 'A procedure to automatically open a ticket on Zendesk and deliver the required logs to ShareFile',
  resourceName: 'local',
{
  step 'getVersion',
    description: 'Retrieve the server version',
    command: new File(pluginDir + "/dsl/procedures/openSupportTicket/steps/getVersion.pl").text,
    resourceName: '$[gatheringResource]',
    shell: 'ec-perl'

  step 'grabResource',
    description: 'Grab one of the commander server resources (in case of cluster)',
    command: new File(pluginDir + "/dsl/procedures/openSupportTicket/steps/grabResource.sh").text,
    resourceName: '$[gatheringResource]'

  step 'createTicket',
    description: 'Open the ticket',
    condition: '1',
    resourceName: '$[/myJob/gatheringResource]',
    subprocedure: 'createTicket',
    subproject: '/plugins/EC-Zendesk/project',
    actualParameter: [
      configuration: '$[zendeskConfiguration]',
      problemScope: '$[problemScope]',
      problemType: '$[problemType]',
      product: '$[product]',
      ticketDescription: '$[ticketDescription]',
      ticketSubject: '$[ticketTitle]',
      version: '$[/myJob/serverVersion]'
    ]

  step 'gatherLogs',
    subprocedure: 'gatherLogs',
    actualParameter: [
      gatheringResource: '$[/myJob/gatheringResource]',
      serverResources: '$[serverResources]',
      agents: '$[agents]',
      time: '$[time]',
      jobNumber: '$[jobNumber]',
      obfuscate: '$[obfuscate]',
      createArtifact: 'false'
    ]
/*  step 'createBundle',
    description: 'Zip the different files',
    resourceName: '$[/myJob/gatheringResource]',
    subprocedure: 'Create Zip File',
    subproject: '/plugins/EC-FileOps/project',
    actualParameter: [
      sourceFile: '$[/myJob/destinationDirectory]',
      zipFile: '$[/myJob/destinationDirectory].zip'
    ]
*/
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
