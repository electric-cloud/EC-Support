#############################################################################
#
# Copyright Electric-Cloud 2015
#
#############################################################################
$[/plugins[EC-Admin]project/scripts/perlHeaderJSON]

#############################################################################
#
# Parameters
#
#############################################################################

#############################################################################
#
# Global Variables
#
#############################################################################
my $DEBUG=1;
my $destDir='$[/myJob/destinationDirectory]';


my $content=directoryContent($destDir);
$ec->setProperty("/myJob/fileList", $content);

#############################################################################
#
# directoryContent: return a string containing the list of files
#                   in a directory
#############################################################################
sub directoryContent {
    my $dir=shift @_;
    my $content="";

    opendir(my $dh, $dir) or die("Cannot open the directory $dir\n$!");

	while (my $file = readdir($dh)) {
    	next if $file eq '.' or $file eq '..';
    	if (-d "$dir/$file") {
    		$content .= directoryContent("$dir/$file");
        } else {
        	$content .= "$dir/$file\n";
            printf("$dir/$file\n") if ($DEBUG);
        }
    }
    closedir($dh);
    return $content;
}
