use Cwd;
use File::Spec;
use POSIX;

my $dir = getcwd;
my $logfile ="";
my $nowString = localtime;
my $pluginDir=$commander->getProperty("/server/settings/pluginsDirectory")
                ->findnodes("//value")->string_value() . "/$pluginName" ;

$commander->setProperty("/plugins/$pluginName/project/pluginDir",{value=>$pluginDir});
$logfile .= "Plugin Name: $pluginName\n";
$logfile .= "Current directory: $dir\n";
$logfile .= "Plugin directory: $pluginDir\n";

$commander->setProperty("/plugins/$pluginName/project/logs/$nowString",{value=>$logfile});

# Evaluate promote.groovy or demote.groovy based on whether plugin is being promoted or demoted ($promoteAction)
local $/ = undef;
my $promoteFile="";
#If env variable QUERY_STRING exists or we are in a step:
if(defined $ENV{COMMANDER_JOBSTEPID} || defined $ENV{QUERY_STRING}) { # Promotion through UI
  $promoteFile="$pluginDir/dsl/$promoteAction.groovy";
} else {  # Promotion from the command line
  $promoteFile = "dsl/$promoteAction.groovy";
}
open FILE,  $promoteFile or die "Couldn't open file $promoteFile: $!";
my $dsl = <FILE>;
close FILE;
my $dslReponse = $commander->evalDsl($dsl,
      { parameters=>qq({"pluginName":"$pluginName"}),
        serverLibraryPath=>"$pluginDir/dsl"
      }
)->findnodes_as_string("/");
$logfile .= $dslReponse;

# Create output property
$commander->setProperty("/plugins/$pluginName/project/logs/$nowString",{value=>$logfile});
