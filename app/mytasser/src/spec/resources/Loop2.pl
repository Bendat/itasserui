use strict;
use warnings FATAL => 'all';
my $counter = 0;
until ($counter >= 1) {
    print $counter;
    print "\n";
    sleep(1);
    $counter++;
}
0;