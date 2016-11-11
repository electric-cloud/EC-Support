
procedure 'sub-collectAllServerLogs',
  description: 'a sub-procedure to collect server logs',
{
  formalParameter 'destinationDirectory',
    description: 'The directory in which to copy the logs.',
    required: '0',
    type: 'entry'

  formalParameter 'jobNumber',
    description: 'The ID of the job that generated the error. Will be used to collect the right logs',
    required: '0',
    type: 'entry'

  formalParameter 'serverResources',
    description: 'A list of resources or pools, comma separated',
    required: '1',
    type: 'entry'

  formalParameter 'time',
    description: 'The time at which the issue happened. Used to collect the right logs.',
    required: '0',
    type: 'entry'

  step 'loop',
    command: new File(pluginDir + "/dsl/procedures/sub-collectAllServerLogs/steps/loop.pl").text,
    errorHandling: 'failProcedure',
    shell: 'ec-perl'
}
