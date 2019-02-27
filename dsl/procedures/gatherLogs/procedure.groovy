import java.io.File

def procName='gatherLogs'
procedure procName,
  description: '''A procedure to gather logs without sending them in case your
EF cluster does not have external internet access. Optionally, an artifact can
be created that can be downloaded later to the user desktop.''',
{
  step 'getVersion',
    description: 'Retrieve the server version',
    command: new File(pluginDir + "/dsl/procedures/$procName/steps/getVersion.pl").text,
    resourceName: '$' + '[gatheringResource]',
    shell: 'ec-perl'

  step 'grabResource',
    description: 'Grab one of the commander server resources (in case of cluster)',
    command: new File(pluginDir + "/dsl/procedures/$procName/steps/grabResource.sh").text,
    resourceName: '$' + '[gatheringResource]'

  step 'createNumber',
    command: new File(pluginDir + "/dsl/procedures/$procName/steps/createNumber.pl").text,
    shell: 'ec-perl'

  step 'grabDestinationDir',
    command: new File(pluginDir + "/dsl/procedures/$procName/steps/grabDestinationDir.pl").text,
    resourceName: '$' + '[/myJob/gatheringResource]',
    shell: 'ec-perl'

  step 'createTicketDirectory',
      description: 'create a directory to collect the logs',
      resourceName: '$' + '[/myJob/gatheringResource]',
      subprocedure: 'CreateDirectory',
      subproject: '/plugins/EC-FileOps/project',
      actualParameter: [
        'Path': '$' + '[/myJob/destinationDirectory]'
      ]

  step 'sub-collectAllServerLogs',
      subprocedure: 'sub-collectAllServerLogs',
      subproject: '',
      actualParameter: [
          destinationDirectory: '$' + '[/myJob/destinationDirectory]',
          jobNumber:            '$' + '[jobNumber]',
          serverResources:      '$' + '[serverResources]',
          time:                 '$' + '[time]'
      ]

  step 'collectAgentLogs',
      description: 'If agent list is not empty, go grab agent.log and jagent.log',
      command: new File(pluginDir + "/dsl/procedures/$procName/steps/collectAgentLogs.pl").text,
      condition: '$' + '[/javascript "$' + '[agents]" != "" ]',
      shell: 'ec-perl'

  step 'obfuscateLogs',
    description: "optionaly remove IP, server URL, user Id, ... from the logs",
    resourceName: '$' + '[/myJob/gatheringResource]',
    command: new File(pluginDir + "/dsl/procedures/$procName/steps/obfuscate.pl").text,
    condition: '$[/javascript "$[obfuscate]" == "true" ]'

  step 'createBundle',
      description: 'Zip the different files',
      resourceName: '$' + '[/myJob/gatheringResource]',
      subprocedure: 'Create Zip File',
      subproject: '/plugins/EC-FileOps/project',
      actualParameter: [
          sourceFile: '$' + '[/myJob/destinationDirectory]',
          zipFile: '$' + '[/myJob/destinationDirectory].zip'
      ]

  step 'createArtifact',
    resourceName: '$' + '[/myJob/gatheringResource]',
    subprocedure: 'Publish',
    subproject:   '/plugins/EC-Artifact/project',
    actualParameter: [
        artifactName: 'EC-Support:logs',
        artifactVersionVersion: '$' + '[/myJob/packageNumber].$' + '[/increment /server/ec_counters/EC-Support/packageNumber]',
        compress: '1',
        dependentArtifactVersionList: '',
        excludePatterns: '',
        followSymlinks: '0',
        fromLocation: '$' + '[/myJob/destinationDirectory]',
        includePatterns: '',
        repositoryName: 'default'
    ]

  step 'summary',
      command: new File(pluginDir + "/dsl/procedures/$procName/steps/summary.pl").text,
      resourceName: '$' + '[/myJob/gatheringResource]',
      shell: 'ec-perl'
}
