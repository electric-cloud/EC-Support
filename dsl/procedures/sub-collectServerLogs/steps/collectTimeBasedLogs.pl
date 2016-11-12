#############################################################################
#
# Copyright Electric-Cloud 2015
#
#############################################################################
use Time::Local;
use Cwd;

$[/plugins[EC-Admin]project/scripts/perlHeaderJSON]

#############################################################################
#
# Parameters
#
#############################################################################
my $timeString = "$[time]";
my $serverResource = '$[serverResource]';	# name of the remote Server

#############################################################################
#
# Global Variables
#
#############################################################################
my $DEBUG=1;
my $logDir="$[/server/Electric Cloud/dataDirectory]/logs";
my $cwd= getcwd();

my $serverEpochTime=convertTimeToEpoch($timeString);

opendir(LOG, $logDir) or die("Cannot open the log directory\n$!");

while (my $file = readdir(LOG)) {
    next if ($file !~ m/commander.*\.log\.zip/);
    # printf("Processing $file\n") if ($DEBUG);
    my $fileModificationTime = (stat("$logDir/$file"))[9];      # get modification time
    # printf("    time: %d\n", $fileModificationTime);
    if ($fileModificationTime >= $serverEpochTime) {
    	$ec->createJobStep({
        	subproject   => "/plugins/EC-FileOps/project",
            subprocedure => "Remote Copy - Native",
            jobStepName  => "Copy $file",
            actualParameter => [
            	{actualParameterName => 'sourceFile',
                               value => "$logDir/$file"},
            	{actualParameterName => 'sourceResourceName',
                               value => "$serverResource"},
            	{actualParameterName => 'sourceWorkspaceName',
                               value => "$[/myJob/sourceWorkspace]"},

                {actualParameterName => 'destinationFile',
                               value => "$[destinationDirectory]/servers/$serverResource/"},
            	{actualParameterName => 'destinationResourceName',
                               value => "$[targetServerResource]"},
            	{actualParameterName => 'destinationWorkspaceName',
                               value => "$[/myJob/targetWorkspace]"},
            ],
        });
    }
}
closedir(LOG);


#############################################################################
#
# convertTime
# Time is of format 'MM/DD/YYYY 11:35:00' or 11:35:00 or 11:35
#############################################################################
sub convertTimeToEpoch {
    my $timeStr=shift @_;

    # Get passed time
    my($date, $time);

    if ($timeStr =~ m/\s+/) {
        ($date, $time)=split('\s+', $timeStr);
    } else {
        $time=$timeStr;
        $date="";
    }
    my ($year, $month, $day) = split(/[.\/\-]/, $date);
    my ($hour, $min, $sec)   = split(/[:]/, $time);

    printf("Incident time (original): %s-%s-%s %s:%s\n", $year, $month, $day, $hour, $min) if ($DEBUG);

    # get server time to fill missing values
    my $localTime = "$[/timestamp MM-dd-yyyy HH:mm]";
    my ($localMonth,$localDay,$localYear,$localHour,$localMinute) = split(/[\s\-:]+/, $localTime);

    #printf("Local month: $localMonth\n") if ($DEBUG);
    $year = $localYear if ($year == 0);
    $year += 2000 if ($year < 100);
    $month = $localMonth if ($month == 0);
    $day   = $localDay if ($day == 0);

    printf("Incident time: %s-%s-%s %s:%s\n", $year, $month, $day, $hour, $min) if ($DEBUG);
    my $time = timelocal(0,$min,$hour,$day,$month-1,$year);

    printf("Time of the incident: %s \n", $time) if ($DEBUG);
    return $time
}
