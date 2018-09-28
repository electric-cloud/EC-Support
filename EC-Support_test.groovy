
project 'EC-Support_Test', {
  resourceName = null
  workspaceName = null

  procedure 'SupportTest', {
    description = ''
    jobNameTemplate = ''
    resourceName = ''
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workspaceName = ''

    step 'openTicket', {
      description = ''
      alwaysRun = '0'
      broadcast = '0'
      command = null
      condition = ''
      errorHandling = 'failProcedure'
      exclusiveMode = 'none'
      logFileName = null
      parallel = '0'
      postProcessor = null
      precondition = ''
      releaseMode = 'none'
      resourceName = ''
      shell = null
      subprocedure = 'openSupportTicket'
      subproject = '/plugins/EC-Support/project'
      timeLimit = ''
      timeLimitUnits = 'minutes'
      workingDirectory = null
      workspaceName = ''
      actualParameter 'agents', ''
      actualParameter 'gatheringResource', 'local'
      actualParameter 'jobNumber', ''
      actualParameter 'obfuscate', 'true'
      actualParameter 'problemScope', '2_one_user'
      actualParameter 'problemType', '2_need_assistance'
      actualParameter 'product', 'electricflow'
      actualParameter 'serverResources', 'local'
      actualParameter 'sharefileConfiguration', 'sharefile'
      actualParameter 'ticketDescription', '''this is a test for EC-Support plugin

Feel free to close if you find still open

Laurent'''
      actualParameter 'ticketTitle', 'Test $[/increment /myProject/ticketCounter]'
      actualParameter 'time', ''
      actualParameter 'zendeskConfiguration', 'zendesk'
    }
  }

  // Custom properties
  ticketCounter = '12'
}
