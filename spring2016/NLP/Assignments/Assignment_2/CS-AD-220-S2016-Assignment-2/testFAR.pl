#! /usr/bin/perl

use strict;

################################################################################
#testFAR - A simple interface for testing FST Archives
################################################################################
# argument handling
my $argc  = @ARGV;
die "Usage: $0 <far> <rules> <inputfile>?\n"
    if ($argc < 2);
my $far  = $ARGV[0];
my $rules = $ARGV[1];
my $input = $ARGV[2];

if ($input ne ""){
  &applyrules;
}else{
  while (my $line=<STDIN>){
    $input="/tmp/fartest-$$";
    open(TMP,">$input");
    print TMP "$line";
    close(TMP);
    &applyrules;
    system("rm $input");
  }
}

sub applyrules {
  
  open(APPLY, "thraxrewrite-tester --far=$far --rules=$rules < $input |paste $input -|");
  
  while(my $line=<APPLY>){
    chomp($line);
    $line=~s/Input string: Output string: //;
    $line=~s/Input string: Rewrite failed./<REJECT>/;
    unless ($line=~s/Input string:\s*//){
      print "$line\n";
    }
  }
  close(APPLY);
}



################################################################################
