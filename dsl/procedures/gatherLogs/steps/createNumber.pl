$[/plugins[EC-Admin]project/scripts/perlHeaderJSON]

my $ticket=getP('/myJob/zendesk/ticketId');
if ($ticket) {
  $ec->setProperty("/myJob/packageNumber", $ticket);
} else {
  $ec->setProperty("/myJob/packageNumber", "$[/timestamp YYYY.MM.dd]");
}
