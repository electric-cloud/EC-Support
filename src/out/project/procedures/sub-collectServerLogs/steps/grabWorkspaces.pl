$[/plugins[EC-Admin]project/scripts/perlHeaderJSON]

#
# parameter
#

# Get the source workspace
my $sourceWks=getP("/resources/$[serverResource]/workspaceName");
printf("Source workspace: '%s'\n", $sourceWks);
if (($sourceWks == undef) || ($sourceWks eq "")) {
	$ec->setProperty("/myJob/sourceWorkspace", "default");
} else {
	$ec->setProperty("/myJob/sourceWorkspace", $sourceWks);
}

# Get the source workspace
my $targetWks=getP("/resources/$[targetServerResource]/workspaceName");
printf("Target workspace: '%s'\n", $targetWks);
if (($targetWks == undef) || ($targetWks eq "")) {
	$ec->setProperty("/myJob/targetWorkspace", "default");
} else {
	$ec->setProperty("/myJob/targetWorkspace", $targetWks);
}

$[/plugins[EC-Admin]project/scripts/perlLibJSON]

