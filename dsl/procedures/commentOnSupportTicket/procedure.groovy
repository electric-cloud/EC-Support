
procedure 'commentOnSupportTicket',
  description: 'A procedure to add comment unto a ticket on Zendesk',
  resourceName: 'local',
{
  step 'comment',
    subprocedure: 'commentOnTicket',
    subproject: '/plugins/EC-Zendesk/project',
    actualParameter: [
      ticketComment: '$[ticketComment]',
      config: '$[config]',
      ticketNumber: '$[ticketId]'
    ]
}
