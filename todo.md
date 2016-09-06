This is list of future task on this project:
  
- Add support for passing arguments to scripts which starts examples
- Create nice API for tracing "threads"

[25.8.2016]
- create example for callback handling
- instrument network communication
- create API for storing spans on disk

- add support for instrumenting anonymous classes

[ARCHITECTONICALLY NOT POSSIBLE]
- add possibility to cancel loading of class and load different first
  - ( more I think about it it won't be possible at the end anyway,
      since the class would be already loaded and we will retransform it,
      so the first instance will still use the original code, not the instrumented
      one and we don't want that)