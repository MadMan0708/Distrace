This is list of future task on this project:

- Fix problem when in instrumentor JVM we try to override class which
  extends another custom class. Right now it fails because it does not
  have byte code for this dependant class available