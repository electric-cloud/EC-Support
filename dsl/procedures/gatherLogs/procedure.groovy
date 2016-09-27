
procedure 'gatherLogs', {
  description = 'A procedure to gather logs without sending them in case your EF cluster does not have external internet access. Optionally, an artifact can be created that can be downloaded later to the user desktop.'
  resourceName = 'local'

  formalParameter 'agents', {
    description = 'a commas separated list of agents. Will be used to get the logs from the agents involved in the issue'
    required = '0'
    type = 'entry'
  }
  formalParameter 'createArtifact', defaultValue: 'true', {
    required = '0'
    type = 'checkbox'
  }
  formalParameter 'gatheringResource', defaultValue: 'local', {
    description = 'The resource to use to gather log and open ticket'
    required = '1'
    type = 'entry'
  }
  formalParameter 'jobNumber', {
    description = 'The ID of the job that generated the error. Will be used to collect the right logs'
    required = '0'
    type = 'entry'
  }
  formalParameter 'serverResources', defaultValue: 'default', {
    description = 'A list of resources or pools, comma separated'
    required = '0'
    type = 'entry'
  }
  formalParameter 'time', {
    description = 'The time at which the issue happened. Used to collect the right logs. If empty, will simply send the commander.log'
    required = '0'
    type = 'entry'
  }

  step 'getVersion', {
    description = 'Retrieve the server version'
    command = new File(pluginDir + "/dsl/procedures/gatherLogs/steps/getVersion.pl").text
    resourceName = '$[gatheringResource]'
    shell = 'ec-perl'
  }

  step 'grabResource', {
    description = 'Grab one of the commander server resources (in case of cluster)'
    command = new File(pluginDir + "/dsl/procedures/gatherLogs/steps/grabResource.pl").text
    resourceName = '$[gatheringResource]'
  }

  step 'createNumber', {
    command = new File(pluginDir + "/dsl/procedures/gatherLogs/steps/createNumber.pl").text
    shell = 'ec-perl'
  }

  step 'grabDestinationDir', {
    command = new File(pluginDir + "/dsl/procedures/gatherLogs/steps/grbaDestinationDir.pl").text
    resourceName = '$[/myJob/gatheringResource]'
    shell = 'ec-perl'
  }

  step 'createTicketDirectory', {
    description = 'create a directory to collect the logs'
    resourceName = '$[/myJob/gatheringResource]'
    subprocedure = 'CreateDirectory'
    subproject = '/plugins/EC-FileOps/project'
    actualParameter '''Path''', '''$[/myJob/destinationDirectory]'''
  }

  step 'collectServerLogs', {
    subprocedure = 'collectServerLogs'
    subproject = ''
    actualParameter '''destinationDirectory''', '''$[/myJob/destinationDirectory]'''
    actualParameter '''jobNumber''', '''$[jobNumber]'''
    actualParameter '''serverResources''', '''$[serverResources]'''
    actualParameter '''time''', '''$[time]'''
  }

  step 'collectAgentLogs', {
    description = 'If agent list is not empty, go grab agent.log and jagent.log'
    command = new File(pluginDir + "/dsl/procedures/gatherLogs/steps/collectAgentLogs.pl").text
    condition = '$[/javascript "$[agents]" != "" ]'
    shell = 'ec-perl'
  }

  step 'createBundle', {
    description = 'Zip the different files'
    resourceName = '$[/myJob/gatheringResource]'
    subprocedure = 'Create Zip File'
    subproject = '/plugins/EC-FileOps/project'
    actualParameter '''sourceFile''', '''$[/myJob/destinationDirectory]'''
    actualParameter '''zipFile''', '''$[/myJob/destinationDirectory].zip'''
  }

  step 'createArtifact', {
    resourceName = '$[/myJob/gatheringResource]'
    subprocedure = 'Publish'
    subproject = '/plugins/EC-Artifact/project'
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
    command = new File(pluginDir + "/dsl/procedures/gatherLogs/steps/summary.pl").text
    resourceName = '$[/myJob/gatheringResource]'
    shell = 'ec-perl'
  }

  property 'ec_customEditorData', {
    property 'parameters', {
      property 'createArtifact', {
        checkedValue = 'true'
        formType = 'standard'
        initiallyChecked = '0'
        uncheckedValue = 'false'
      }
      property 'problemScope', {
        property 'options', {
          property 'option1', {
            text = 'Test Setup'
            property 'value', value: '1_test_setup', {
              expandable = '1'
            }
          }
          property 'option2', {
            text = 'One User'
            property 'value', value: '2_one_user', {
              expandable = '1'
            }
          }
          property 'option3', {
            text = 'One Team'
            property 'value', value: '3_one_team', {
              expandable = '1'
            }
          }
          property 'option4', {
            text = 'Some users'
            property 'value', value: '4_some_users', {
              expandable = '1'
            }
          }
          property 'option5', {
            text = 'One Site'
            property 'value', value: '5_one_site', {
              expandable = '1'
            }
          }
          property 'option6', {
            text = 'Some teams'
            property 'value', value: '6_some_teams', {
              expandable = '1'
            }
          }
          property 'option7', {
            text = 'Some sites'
            property 'value', value: '7_some_sites', {
              expandable = '1'
            }
          }
          property 'option8', {
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
        property 'options', {
          property 'option1', {
            text = 'Enhancement'
            property 'value', value: '1_enhancement', {
              expandable = '1'
            }
          }
          property 'option2', {
            text = 'Need Assistance'
            property 'value', value: '2_need_assistance', {
              expandable = '1'
            }
          }
          property 'option3', {
            text = 'Nuisance'
            property 'value', value: '3_nuisance', {
              expandable = '1'
            }
          }
          property 'option4', {
            text = 'Tool Limiting'
            property 'value', value: '4_tool_limiting', {
              expandable = '1'
            }
          }
          property 'option5', {
            text = 'Performance'
            property 'value', value: '5_performance', {
              expandable = '1'
            }
          }
          property 'option6', {
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
  ec_parameterForm = new File(pluginDir + "/dsl/procedures/gatherLogs/form.xml").text
}
