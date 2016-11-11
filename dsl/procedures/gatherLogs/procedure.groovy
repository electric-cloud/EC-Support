
procedure 'gatherLogs',
  description: '''A procedure to gather logs without sending them in case your
EF cluster does not have external internet access. Optionally, an artifact can
be created that can be downloaded later to the user desktop.''',
  resourceName: 'local',
{
  //
  //  Parameters
  //
  formalParameter 'agents',
    description: 'a commas separated list of agents. Will be used to get the logs from the agents involved in the issue',
    required: '0',
    type: 'entry'

  formalParameter 'createArtifact', defaultValue: 'true',
    required: '0',
    type: 'checkbox'

  formalParameter 'gatheringResource', defaultValue: 'local',
    description: 'The resource to use to gather logs',
    required: '1',
    type:'entry'

  formalParameter 'jobNumber',
    description: 'The ID of the job that generated the error. Will be used to collect the right logs',
    required: '0',
    type: 'entry'

  formalParameter 'serverResources',
    defaultValue: 'default',
    description: 'A list of resources or pools, comma separated',
    required: '0',
    type: 'entry'

  formalParameter 'time',
    description: 'The time at which the issue happened. Used to collect the right logs. If empty, will simply send the commander.log',
    required: '0',
    type: 'entry'

  formalParameter 'time',
    description: 'The time at which the issue happened. Used to collect the right logs. If empty, will simply send the commander.log',
    required: '0',
    type: 'entry'


    //
    //   steps
    //
    step 'getVersion',
      description: 'Retrieve the server version',
      command: new File(pluginDir + "/dsl/procedures/gatherLogs/steps/getVersion.pl").text,
      resourceName: '$' + '[gatheringResource]',
      shell: 'ec-perl'

    step 'grabResource',
      description: 'Grab one of the commander server resources (in case of cluster)',
      command: new File(pluginDir + "/dsl/procedures/gatherLogs/steps/grabResource.sh").text,
      resourceName: '$' + '[gatheringResource]'

    step 'createNumber',
      command: new File(pluginDir + "/dsl/procedures/gatherLogs/steps/createNumber.pl").text,
      shell: 'ec-perl'

    step 'grabDestinationDir',
      command: new File(pluginDir + "/dsl/procedures/gatherLogs/steps/grabDestinationDir.pl").text,
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
        command: new File(pluginDir + "/dsl/procedures/gatherLogs/steps/collectAgentLogs.pl").text,
        condition: '$' + '[/javascript "$' + '[agents]" != "" ]',
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
        command: new File(pluginDir + "/dsl/procedures/gatherLogs/steps/summary.pl").text,
        resourceName: '$' + '[/myJob/gatheringResource]',
        shell: 'ec-perl'


  property 'ec_customEditorData', {

    property 'parameters', {

      property 'createArtifact', {
        property 'checkedValue', value: 'true'
        property 'formType', value: 'standard'
        property 'initiallyChecked', value: '0'
        property 'uncheckedValue', value: 'false'
      }

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
            property 'text', 'One Team'
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
  property 'ec_parameterForm', value: new File(pluginDir + "/dsl/procedures/gatherLogs/form.xml").text

}
