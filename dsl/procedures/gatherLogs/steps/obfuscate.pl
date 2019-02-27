#############################################################################
#
# Remove sensitive information from the gatherLogs
#
# Author: L.Rochette
#
# Copyright 2018 Electric Cloud, Inc.
#
#     Licensed under the Apache License, Version 2.0 (the "License");
#     you may not use this file except in compliance with the License.
#     You may obtain a copy of the License at
#
#         http://www.apache.org/licenses/LICENSE-2.0
#
#     Unless required by applicable law or agreed to in writing, software
#     distributed under the License is distributed on an "AS IS" BASIS,
#     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#     See the License for the specific language governing permissions and
#     limitations under the License.
#
# History
# ---------------------------------------------------------------------------
# 2018-Aug-10 lrochette Initial Version
#
#############################################################################
$[/plugins/EC-Admin/project/scripts/perlHeaderJSON]

#
# Loop over files
#
=======
# TODO:
#   - save Hash
#   - reuse Hash
#   - allow for user defined regexp
#############################################################################

use Archive::Zip qw( :ERROR_CODES :CONSTANTS );
use Cwd;
use File::Copy;

$[/plugins/EC-Admin/project/scripts/perlHeaderJSON]
my $DEBUG=0;
my %hash;

my $serverName="SERVER";  # property in the future, need to survice from call to call
my $urlName="URL";
my $counter=0;            # property in the future, need to survice from call to call
my $replacement;

# Patterns
my $ipPattern = qr/(\d{1,3}\.){3}\d{1,3}/;  # 192.168.1.1
my $urlPattern = qr/[\w_\-]+(\.[\w_\-]+)*/;

sub obfuscateFile {
  my $file=shift @_;

  my @newlines;

  printf("  Obfuscating $file\n");
  copy($file, "${file}.ORIG") if ($DEBUG);

  open(my $fh, $file) || die "Cannot read $file";
  my @lines = <$fh>;
  close($fh);

 foreach my $line (@lines) {
   #
   # IP search
   #
   if ($line =~ /($ipPattern)/) {
     my $ip=$1;
     # printf("    found IP at $line") if ($DEBUG);
     #
     # Let's weed some version number from IP
     my ($i1,$i2,$i3,$i4)=split /\./, $ip;
     #printf ("$i1,$i2,$i3,$i4\n") if ($DEBUG);
     if (($i1 =~ /^0/) || ($i4>255)) {
       next;
     }
     if (! exists($hash{$ip})) {
       #printf("      $ip is NOT in hash\n");
       $replacement="$serverName-$counter";
       $hash{$ip}="$serverName-$counter";
       $counter++;
     } else {
        #printf("      $ip is PRESENT\n");
        $replacement=$hash{$ip};
     }
     $line =~ s/$ip/$replacement/g;
   }
   #
   # let's remove URL as well
   #
   elsif ($line =~ m!http(s)?://($urlPattern)!) {
     my $url=$2;
     if (! exists($hash{$url})) {
       #printf("      URL $url is NOT in hash\n");
       $replacement="$urlName-$counter";
       $hash{$url}="$urlName-$counter";
       $counter++;
     } else {
        #printf("      URL $url is PRESENT\n");
        $replacement=$hash{$url};
     }
     $line =~ s/$url/$replacement/g;
   }

    push(@newlines, $line);
  }

  open($fh, ">$file") || die "Cannot write $file";
  print $fh @newlines;
  close($fh);
}

sub unzip {
  my ($dir, $zipFile)=@_;
  my $file;

  $file = $zipFile; $file =~ s/\.zip$//;
  printf("  unzip $dir/$zipFile\n");
  printf("    -> $file\n");
  my $zip = Archive::Zip->new();
  my $status  = $zip->read("$dir/$zipFile");
  die "Read of $dir/$zipFile failed\n" if $status != AZ_OK;
  unless ($zip->extractMember($file, "$dir/$file") == AZ_OK) {
    die ("Cannot extract $file");
  }
  unlink "$dir/$zipFile";
  return $file;
}

#
# Parse recursively the content of directory
#   Unzip .zip files
#   Obfuscate files
#
sub parseDirectory {
  my $dir=shift @_;

  printf("Parsing $dir\n") if ($DEBUG);
  opendir(my $dh, $dir) || die "Can't open $dir: $!";

  while (my $file = readdir($dh)) {
  	next if $file eq '.' or $file eq '..';
    if (-d "$dir/$file") {
      parseDirectory("$dir/$file");
    } else {
      if ($file =~ /.zip$/) {
        printf("  Unzip $dir/$file\n") if ($DEBUG);
        $file=unzip($dir, $file);
      }
      obfuscateFile("$dir/$file");
    }
  }
  closedir $dh;
}

printf("Obfuscating files in %s\n", cwd());
parseDirectory(".");

if ($DEBUG) {
  printf("Hash:\n");
  print Dumper \%hash;
}

$[/plugins/EC-Admin/project/scripts/perlLibJSON]
