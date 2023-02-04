Main File :
	MultiFLowSpreadSketches.java (Project5/src/main/java)

Executing the program : 

1) In terminal go to the location where the program file : MultiFLowSpreadSketches.java
2) Enter the command javac MultiFLowSpreadSketches.java to compile the code.
3) Enter the command java MultiFLowSpreadSketches to run the code.
4) Enter the inputs for Number of bits in virtual bitmap for each flow, Number of bits in bitmap array, Number of bits in each register, Size of each register, Enter the Multi-Flow Spread Sketch type(virtual_bitmap or bskt).
5) After all the inputs are given respective filter output files(virtual_bitmap.txt, and bskt.txt) will be created for type of filter you entered
as input.

There are four files in src folder. 

1) MultiFLowSpreadSketches.java : It has the code that is implemented for Virtual_Bitmap, BSKT( HyperLogLog).

2) virtual_bitmap.txt: This file contains the True spread and estimated spread after  implementation.

3) bskt.txt: This file contains the True spread and estimated spread of top 25 values after BSKT implementation .

4) output.pdf: This file contains the graph generated for true spread(x-axis) and estimated spread(y-axis) for virtual bitmap. It is generated using a python code.

5) Flow.java : This file contains the Flow class

6) project5input.txt : This file contains the input for the Virtual BitMap and BSKT implementation.

