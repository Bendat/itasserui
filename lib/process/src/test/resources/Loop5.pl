use strict;
use warnings FATAL => 'all';
my $counter = 0;
until ($counter > 5) {
    sleep(1);
    print $counter;
    print "\n";
    $counter++;
}
0;