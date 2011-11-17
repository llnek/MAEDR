Sample program demonstrating the use-case of calling some java async lib,
resume processing after async callback is done.

1) we create a dummy web-service which this program will call upon.  The web-service
   is programmed to take ~ 10secs before coming back with a response.
   
2) the program will call the web service asynchrounously, and resume when the
   web-service returns.
