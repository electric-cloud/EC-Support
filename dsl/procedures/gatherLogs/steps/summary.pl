$[/plugins[EC-Admin]project/scripts/perlheaderJSON]

$ec->setProperty("/myJob/artifactsDirectory",".");
$ec->setProperty("/myJob/report-urls/logBundle", "/commander/jobSteps/$[/myjobStep/jobStepId]/$[/myJob/packageNumber].zip");
