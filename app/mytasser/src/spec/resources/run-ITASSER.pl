#!/usr/bin/perl -w

use strict;

print "contents of @ARGV:\n";
print "[$_]\n" foreach @ARGV;
print "\$0: $0\n";
sleep(240);
