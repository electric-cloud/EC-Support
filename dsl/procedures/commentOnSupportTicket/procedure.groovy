
procedure 'commentOnSupportTicket', {
  description = 'A procedure to add comment unto a ticket on Zendesk'
  resourceName = 'local'

  formalParameter 'comment', {
    description = 'The main comment to add to the ticket'
    required = '1'
    type = 'textarea'
  }

  formalParameter 'ticketId', {
    description = 'The number of the ticket to comment on'
    required = '1'
    type = 'entry'
  }

  formalParameter 'zendeskConfiguration', defaultValue: 'zendesk', {
    description = 'The name of your Zendesk configuration'
    required = '1'
    type = 'entry'
  }

  step 'comment', {
    subprocedure = 'commentOnTicket'
    subproject = '/plugins/EC-Zendesk/project'
    actualParameter '''comment''', '''$[comment]'''
    actualParameter '''credential''', '''$[zendeskConfiguration]'''
    actualParameter '''ticketNumber''', '''$[ticketId]'''
  }

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
  ec_parameterForm = new File(pluginDir + "/dsl/procedures/commentOnSupportTicket/form.xml").text
}
