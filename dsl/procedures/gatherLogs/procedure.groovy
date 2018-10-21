/*

     Copyright 2016-2018 Electric Cloud, Inc.

     Licensed under the Apache License, Version 2.0 (the "License");
     you may not use this file except in compliance with the License.
     You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

     Unless required by applicable law or agreed to in writing, software
     distributed under the License is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     See the License for the specific language governing permissions and
     limitations under the License.

*/
import java.io.File

def procName='gatherLogs'
procedure procName,
  description: '''A procedure to gather logs without sending them in case your
EF cluster does not have external internet access. Optionally, an artifact can
be created that can be downloaded later to the user desktop.''',
{
/*
    step 'getVersion',
      description: 'Retrieve the server version',
      command: new File(pluginDir + "/dsl/procedures/$procName/steps/getVersion.pl").text,
      resourceName: '$[gatheringResource]',
      shell: 'ec-perl'
*/
    step 'grabResource',
      description: 'Grab one of the commander server resources (in case of cluster)',
      command: new File(pluginDir + "/dsl/procedures/$procName/steps/grabResource.sh").text,
      resourceName: '$[gatheringResource]'

    step 'createNumber',
      command: new File(pluginDir + "/dsl/procedures/$procName/steps/createNumber.pl").text,
      shell: 'ec-perl'

    step 'grabDestinationDir',
      command: new File(pluginDir + "/dsl/procedures/$procName/steps/grabDestinationDir.pl").text,
      resourceName: '$[/myJob/gatheringResource]',
      shell: 'ec-perl'

    step 'createTicketDirectory',
        description: 'create a directory to collect the logs',
        resourceName: '$[/myJob/gatheringResource]',
        subprocedure: 'CreateDirectory',
        subproject: '/plugins/EC-FileOps/project',
        actualParameter: [
          'Path': '$[/myJob/destinationDirectory]'
        ]

    step 'sub-collectAllServerLogs',
        subprocedure: 'sub-collectAllServerLogs',
        subproject: '',
        actualParameter: [
            destinationDirectory: '$[/myJob/destinationDirectory]',
            jobNumber:            '$[jobNumber]',
            serverResources:      '$[serverResources]',
            time:                 '$[time]'
        ]

    step 'collectAgentLogs',
        description: 'If agent list is not empty, go grab agent.log and jagent.log',
        command: new File(pluginDir + "/dsl/procedures/$procName/steps/collectAgentLogs.pl").text,
        condition: '$[/javascript "$[agents]" != "" ]',
        shell: 'ec-perl'

  step 'collectWebLogs',
      description: 'If web list is not empty, go grab error.log and stdout.log',
      command: new File(pluginDir + "/dsl/procedures/$procName/steps/collectWebLogs.pl").text,
      condition: '$[/javascript "$[web]" != "" ]',
      shell: 'ec-perl'

  step 'collectRepoLogs',
      description: 'If repo list is not empty, go grab repository.log and repository-service.log.log',
      command: new File(pluginDir + "/dsl/procedures/$procName/steps/collectRepoLogs.pl").text,
      condition: '$[/javascript "$[repo]" != "" ]',
      shell: 'ec-perl'

    step 'obfuscateLogs',
      description: "optionaly remove IP, server URL, user Id, ... from the logs",
      resourceName: '$[/myJob/gatheringResource]',
      command: new File(pluginDir + "/dsl/procedures/$procName/steps/obfuscate.pl").text,
      condition: '$[/javascript "$[obfuscate]" == "true" ]',
      shell: 'ec-perl',
      workingDirectory: '$[/myJob/destinationDirectory]'

    step 'createBundle',
        description: 'Zip the different files',
        resourceName: '$[/myJob/gatheringResource]',
        subprocedure: 'Create Zip File',
        subproject: '/plugins/EC-FileOps/project',
        actualParameter: [
            sourceFile: '$[/myJob/destinationDirectory]',
            zipFile: '$[/myJob/destinationDirectory].zip'
        ]
    step 'listing',
      description: 'Get the list of collected files',
      command: new File(pluginDir + "/dsl/procedures/$procName/steps/listing.pl").text,
      resourceName: '$' + '[/myJob/gatheringResource]',
      shell: 'ec-perl'

    step 'createArtifact',
      resourceName: '$[/myJob/gatheringResource]',
      subprocedure: 'Publish',
      subproject:   '/plugins/EC-Artifact/project',
      condition: '$[/javascript "$[createArtifact]" == "true" ]',
      actualParameter: [
          artifactName: 'EC-Support:logs',
          artifactVersionVersion: '$[/myJob/packageNumber].$[/increment /server/ec_counters/EC-Support/packageNumber]',
          compress: '1',
          dependentArtifactVersionList: '',
          excludePatterns: '',
          followSymlinks: '0',
          fromLocation: '$[/myJob/destinationDirectory]',
          includePatterns: '',
          repositoryName: 'default'
      ]

    step 'summary',
        command: new File(pluginDir + "/dsl/procedures/$procName/steps/summary.pl").text,
        resourceName: '$[/myJob/gatheringResource]',
        shell: 'ec-perl'
}
