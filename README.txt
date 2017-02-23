John Burns
2/22/2017

Everything works fine.  I figured out my issue.  I had identical lines in my
MINI and MAXI methods in the wrong scope.  It was a much easier fix than I initially
thought, but the entire debugging process led me to polish up my code a little more.
I have only .zipped the .java files, so the program needs to be compiled before execution.
When I ran the unit tests, the 1st one worked perfectly, but on test2, after I play 3 for the first move,
my computer player player 2 instead of 4, but if I invert what the unit test says to do 
(i.e. 3 is still 3, but 6 = 0, 5 = 1, etc.), I get the same outcome.  It works, it just chooses the
left side of that initial 3 slot instead of the right.