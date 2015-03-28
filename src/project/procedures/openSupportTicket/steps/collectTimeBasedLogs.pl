#############################################################################
#
# Copyright Electric-Cloud 2015
#
#############################################################################
use File::stat;
use Time::Local;

#############################################################################
#
# Parameters
#
#############################################################################
my $timeString = "$[time]";

#############################################################################
#
# Global Variables
#
#############################################################################
my $DEBUG=1;
my $logDir="$[/server/Electric Cloud/dataDirectory]/logs";

my $serverTime=convertTime($timeString);

opendir(LOG, $logDir) or die("Cannot open the log directory\n$!");

while (my $file = readdir(LOG)) {
	next if ($file !~ m/commander[\-\d.]*.log/);
    printf("Processing $file\n") if ($DEBUG);
	my $fileTS = ctime(stat($file)->mtime);
    if ($fileTS >= $serverTime) {
    	printf("Copying $file\n");
    }
}
closedir(LOG);


#############################################################################
#
# convertTime
# Time is of format 'MM/DD/YYYY 11:35:00' or 11:35:00 or 11:35
#############################################################################
sub convertTime {
	my $timeStr=shift @_;
    
	my($date, $time)=split('\s+', $timeStr);
    my ($mday,$mon,$year)=split(/[.\\]/, $date);
    $year += 2000 if ($year < 100);
    
    my ($hour,$min,$sec) = split(/[:]/, $time);
    my $time = timelocal($sec,$min,$hour,$mday,$mon-1,$year);
	printf("Time of the incident: %s \n", $time) if ($DEBUG);
    return $time
}
    
