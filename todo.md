This is list of future task on this project:
  
- Add support for passing arguments to scripts which starts examples
- Create nice API for tracing "threads"

[25.8.2016]
- instrument network communication
- create API for storing spans on disk

- create example for callback handling

- add support for instrumenting anonymous classes

- Zipkin UI for GUI ?

- optimilization for future - don't send complete bytecode to 
  instrumenter - do decision whether a class should be instrumented on
  app side and send the class code when needed

- Properly implement solution where server can be already running in the network
    and we just connect to it from native agent
 
[ARCHITECTONICALLY NOT POSSIBLE]
- add possibility to cancel loading of class and load different first
  - ( more I think about it it won't be possible at the end anyway,
      since the class would be already loaded and we will retransform it,
      so the first instance will still use the original code, not the instrumented
      one and we don't want that)
      
      
