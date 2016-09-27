
procedure 'openEnhancementRequest', {
  description = '''A procedure to automatically open an enhancement request on Zendesk.
No log involved'''
  resourceName = 'local'

  formalParameter 'problemScope', defaultValue: '2_one_user', {
    required = '0'
    type = 'select'
  }

  formalParameter 'product', defaultValue: 'electricflow', {
    description = 'the name of the product that failed'
    required = '1'
    type = 'select'
  }

  formalParameter 'ticketDescription', {
    description = 'The main description of the issue you are facing'
    required = '0'
    type = 'textarea'
  }

  formalParameter 'ticketTitle', defaultValue: 'Enhancement Request: ', {
    required = '1'
    type = 'entry'
  }

  formalParameter 'zendeskConfiguration', defaultValue: 'zendesk', {
    description = 'The name of your Zendesk configuration'
    required = '1'
    type = 'entry'
  }

  step 'getVersion', {
    description = 'Retrieve the server version'
    command = new File(pluginDir + "/dsl/procedures/openEnhancementRequest/steps/getVersion.pl").text
    shell = 'ec-perl'
  }

  step 'createTicket', {
    description = 'Open the ticket'
    subprocedure = 'createTicket'
    subproject = '/plugins/EC-Zendesk/project'
    actualParameter '''credential''', '''$[zendeskConfiguration]'''
    actualParameter '''problemScope''', '''$[problemScope]'''
    actualParameter '''problemType''', '''1_enhancement'''
    actualParameter '''product''', '''$[product]'''
    actualParameter '''ticketDescription''', '''$[ticketDescription]'''
    actualParameter '''ticketSubject''', '''$[ticketTitle]'''
    actualParameter '''version''', '''$[serverVersion]'''
  }

  property 'ec_customEditorData', {
    property 'parameters', {
      property 'problemScope', {
        property 'options', {
          property 'option1', {
            text = 'Test Setup'
            property 'value', value: '1_test_setup'
          }
          property 'option2', {
            text = 'One User'
            property 'value', value: '2_one_user'
          }
          property 'option3', {
            text = 'One Team'
            property 'value', value: '3_one_team'
          }
          property 'option4', {
            text = 'Some users'
            property 'value', value: '4_some_users'
          }
          property 'option5', {
            text = 'One Site'
            property 'value', value: '5_one_site'
          }
          property 'option6', {
            text = 'Some teams'
            property 'value', value: '6_some_teams'
          }
          property 'option7', {
            text = 'Some sites'
            property 'value', value: '7_some_sites'
          }
          property 'option8', {
            text = 'Everyone'
            property 'value', value: '8_everyone'
          }
          optionCount = '8'
          type = 'list'
        }
        formType = 'standard'
      }

      property 'product', {
        property 'options', {
          property 'option1', {
            text = 'ElectricCommander'
            property 'value', value: 'electriccommander',
          }
          property 'option2', {
            text = 'ElectricFlow'
            property 'value', value: 'electricflow'
          }
          optionCount = '2'
          type = 'list'
        }
        formType = 'standard'
      }
    }
  }
  ec_parameterForm = new File(pluginDir + "/dsl/procedures/openEnhancementRequest/form.xml").text
}
