This is list of finished tasks on this project.

- implement trace context injection and propagation in the local jvm
  case
- implement simple test demonstrating asynchronous communication ( one-way)
- Fix problem when in instrumentor JVM we try to override class which
  extends another custom class. Right now it fails because it does not
  have byte code for this dependant class available
- implement logging, automatically create logging configuration in
  the instrumentor based on logging info provided to native agent