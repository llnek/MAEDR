Sample program demonstrating the implementation of a stateful server. Stateful
in MAEDR means long-live jobs.

This is similar to the http example, however, we keep the history of all invocations
from a user, and we display the full history all the time.

1)  The user points to the http://hostname:8080/test/helloworld?user=joe

2)  The program will check and see if previous jobs have already written out
    data to database for this user(joe). If data exists, then it is read back
    into the current job. 
    
3)  When the current job ends, it will write out the job to database.

4)  The result is that each invocation of (1) will trigger the complete history
    to be displayed even if the server has been brought up and down.
