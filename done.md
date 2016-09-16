This is list of finished tasks on this project.

- implement trace context injection and propagation in the local jvm
  case
- implement simple test demonstrating asynchronous communication ( one-way)
- Fix problem when in instrumentor JVM we try to override class which
  extends another custom class. Right now it fails because it does not
  have byte code for this dependant class available
- implement logging, automatically create logging configuration in
  the instrumentor based on logging info provided to native agent
  
- add support for instrumenting anonymous classes

- Properly implement solution where server can be already running in the network
    and we just connect to it from native agent
    
- add possibility to cancel loading of class and load different first
  - ( more I think about it it won't be possible at the end anyway,
      since the class would be already loaded and we will retransform it,
      so the first instance will still use the original code, not the instrumented
      one and we don't want that)
      