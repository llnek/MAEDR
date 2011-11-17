Sample program demonstrating the implementation of fork-delay/split.

1) The parent process runs and then fork off a *new* child process, without
   waiting for the child to return.  After the fork, the parent will continue
   to do it's own thing - calculating some Fibonacci stuff.
   (we call this a fork-split)
   
2) The child process runs and fork off a *new* child process (we call it grand-child)
   but waits for the grand-child to come back.  The child process passes on some
   information (input) to the grand-child process, and when the grand-child
   returns, the grand-child passes back some (result) to its parent (child process).
   
   (Here, the child process simply asks the grand-child to do a calculation and
    expect the result to be returned)
    
   (we call this a fork-join)
    
