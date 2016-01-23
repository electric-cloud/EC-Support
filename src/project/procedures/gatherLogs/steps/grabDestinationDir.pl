#############################################################################
#
# Copyright Electric-Cloud 2015
#
#############################################################################
use Cwd;

$[/plugins[EC-Admin]project/scripts/perlHeaderJSON]

#############################################################################
#
# Global Variables
#
#############################################################################
my $cwd = getcwd();

$ec->setProperty("/myJob/destinationDirectory", "$cwd/$[/myJob/packageNumber]");
