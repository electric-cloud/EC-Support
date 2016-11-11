#!/usr/bin/env perl
# Build, upload and promote EC-Support
use Getopt::Long;
use XML::Simple qw(:strict);
use Data::Dumper;
use Archive::Zip qw( :ERROR_CODES :CONSTANTS );
use Archive::Zip::Tree;
use strict;
use ElectricCommander ();
$| = 1;
my $ec = new ElectricCommander->new();

my $pluginVersion = "1.4.2";
my $pluginKey = "EC-Support";
my $description = "A set of procedures to interact with Electric Cloud support.";
GetOptions ("version=s" => \$pluginVersion,
			"pluginKey=s"   => \$pluginKey,
			"description=s"  => \$description
			) or die(qq(
Error in command line arguments

	createPlugin.pl
		[--version <version>]
		[--pluginKey <version>]
		[--description <version>]

		)
);

my $pluginName = "${pluginKey}-${pluginVersion}";

my $xs = XML::Simple->new(
	ForceArray => 1,
	KeyAttr    => { },
	KeepRoot   => 1,
);

# Update project.xml with ec_setup.pl
print "[INFO] - Processing 'META-INF/project.xml' file...\n";
my $xmlFile = "META-INF/project.xml";
my $file = "ec_setup.pl";
my $value;
{
  local $/ = undef;
  open FILE, $file or die "Couldn't open file: $!";
  binmode FILE;
  $value = <FILE>;
  close FILE;
}
my $ref  = $xs->XMLin($xmlFile);
$ref->{exportedData}[0]->{project}[0]->{propertySheet}[0]->{property}[0]->{value}[0] = $value;
open(my $fh, '>', $xmlFile) or die "Could not open file '$xmlFile' $!";
print $fh $xs->XMLout($ref);
close $fh;

# Update plugin.xml with key, version, label, description
print "[INFO] - Processing 'META-INF/plugin.xml' file...\n";
$xmlFile = "META-INF/plugin.xml";
$ref  = $xs->XMLin($xmlFile);
$ref->{plugin}[0]->{key}[0] = $pluginKey;
$ref->{plugin}[0]->{version}[0] = $pluginVersion;
$ref->{plugin}[0]->{label}[0] = $pluginKey;
$ref->{plugin}[0]->{description}[0] = $description;
open(my $fh, '>', $xmlFile) or die "Could not open file '$xmlFile' $!";
print $fh $xs->XMLout($ref);
close $fh;

# Update help.xml.txt with key, version,
# Save as help.xml
print "[INFO] - Processing 'pages/help.xml' file...\n";
$file = "pages/help.xml.txt";
{
  local $/ = undef;
  open FILE, $file or warn "Couldn't open file: $!";
	my $value=<FILE>;
  close FILE;

	$value =~	s/\@PLUGIN_NAME\@/$pluginName/g;
	$value =~	s/\@PLUGIN_KEY\@/$pluginKey/g;
	$value =~	s/\@PLUGIN_VERSION\@/$pluginVersion/g;

	open FILE, "> pages/help.xml" or die "Couldn't open file: $!";
  print FILE $value;
  close FILE;
}

# Create plugin jar file
print "[INFO] - Creating plugin jar file, ${pluginKey}.jar\n";
my $zip = Archive::Zip->new();
my $directory = '.';
opendir (DIR, $directory) or die $!;
while (my $file = readdir(DIR)) {
	$zip->addTree( $file, $file ) unless (
		$file eq "${pluginKey}.jar" or
		$file eq ".git" or
		$file eq "." or
		$file eq ".." or
		$file eq "help.xml.txt"
		);
}
# Save the Zip file
unless ( $zip->writeToFileNamed("${pluginKey}.jar") == AZ_OK ) {
	die 'write error';
}

# Uninstall old plugin
print "[INFO] - Uninstalling old plugin...\n";
$ec->uninstallPlugin($pluginName) || print "No old plugin\n";

# Install plugin
print "[INFO] - Installing plugin...\n";
$ec->installPlugin("${pluginKey}.jar");

# Promote plugin
print "[INFO] - Promoting plugin...\n";
$ec->promotePlugin($pluginName);
