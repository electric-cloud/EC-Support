use Cwd;
use File::Spec;
use POSIX;
my $dir = getcwd;
my $logfile ="";
my $pluginDir;
if(defined $ENV{QUERY_STRING}) { # Promotion through UI
       $pluginDir = $ENV{COMMANDER_PLUGINS} . "/$pluginName";

} else {
       $pluginDir = $dir;
}

$commander->setProperty("/plugins/$pluginName/project/pluginDir",{value=>$pluginDir});
$logfile .= "Plugin Name: $pluginName\n";
$logfile .= "Current directory: $dir\n";

# Evaluate promote.groovy or demote.groovy based on whether plugin is being promoted or demoted ($promoteAction)
local $/ = undef;
# If env variable QUERY_STRING exists:
if(defined $ENV{QUERY_STRING}) { # Promotion through UI
       open FILE, $ENV{COMMANDER_PLUGINS} . "/$pluginName/dsl/$promoteAction.groovy" or die "Couldn't open file: $!";
} else {  # Promotion from the command line
       open FILE, "dsl/$promoteAction.groovy" or die "Couldn't open file: $!";
}
my $dsl = <FILE>;
close FILE;
my $dslReponse = $commander->evalDsl($dsl,
              {parameters=>qq(
                     {
                           "pluginName":"$pluginName"
                     }
              )}
)->findnodes_as_string("/");
$logfile .= $dslReponse;

# Create output property

my $nowString = localtime;
$commander->setProperty("/plugins/$pluginName/project/logs/$nowString",{value=>$logfile});
