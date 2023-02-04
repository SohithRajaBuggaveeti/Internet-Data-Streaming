Main File :
	Sketches.java (Project3/src)

Executing the program : 

1) In terminal go to the location where the program file : Sketches.java
2) Enter the command javac Sketches.java to compile the code.
3) Enter the command java Sketches to run the code.
4) Enter the inputs for Number of flows, Number of Counter Arrays, Number of counters in each array, Type of Sketch(count_min or counter_sketch or active_counter), Number part in active counter and exponent part.
5) After all the inputs are given respective sketch output files(countMinSketch.txt, counterSketch.txt, and activeCounter.txt) will be created for type of filter you entered
as input.

There are six files in src folder.

1) Sketches.java : It has the code that is implemented for Counter sketch, Count Min Sketch, and Active Counters.

2) CountMinSketch.txt : This file contains the Avg error among all the flows, the flows of the 100 largest estimated sizes, the flow id, its estimated size and its true size after implementing the Count Min Sketch.

3)counterSketch.txt : This file contains the Avg error among all the flows, the flows of the 100 largest estimated sizes, the flow id, its estimated size and its true size after implementing the Counter Sketch.

4)activeCounter.txt:This file contains the final value of the active counter in decimal.

5) Flow.java : It is class that has FlowId,size, and estimated size.

6)project3input.txt : It contains the inputs for Count Min sketch and Counter sketch.


