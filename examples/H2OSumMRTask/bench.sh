 #!/usr/bin/env bash

for (( c=1; c<=10; c++ ))
do  
 echo "Iteration $c"
 time ./test.sh >> ti.txt
 sleep 10
kill -9 $(jps | grep Starter | cut -f 1)
kill -9 $(jps | grep distrace | cut -f 1)
done

