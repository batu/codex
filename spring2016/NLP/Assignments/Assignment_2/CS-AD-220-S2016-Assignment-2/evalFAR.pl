#! /usr/bin/perl

use strict;

################################################################################
#testFAR - A simple interface for evaluating FST Archives
################################################################################
# argument handling
my $argc  = @ARGV;
die "Usage: $0 <far> <rules> <example-file>\n"
    if ($argc < 3);
my $far  = $ARGV[0];
my $rules = $ARGV[1];
my $examples = $ARGV[2];

my $match=0;
my $all=0;

open(APPLY, "cut -f1 $examples | thraxrewrite-tester --far=$far --rules=$rules |paste $examples -|");
  
while(my $line=<APPLY>){
  chomp($line);
  $line=~s/Input string: Output string: //;
    $line=~s/Input string: Rewrite failed./<REJECT>/;
    unless ($line=~s/Input string:\s*//){
      print "$line";
      my ($in,$gold,$pred)=split('\t',$line);
      if ($gold eq $pred) {$match++; print "\tMATCH\n";} else {print "\tFAIL\n";}
      $all++;
    }
  }
close(APPLY);

printf("Accuracy = %3.2f\n", 100*($match/$all));



################################################################################
