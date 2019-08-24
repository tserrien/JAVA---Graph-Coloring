# Picking up the graph coloring project again

After months of inactivity after submitting the "final" version of this project for university many things have changed. Sticking strictly to coding related things, this projects became interesting again. With a re-explored interest in software engineering, this codebase is going to be partially updated. As I know the lower-bound calculation method the best, this'll possibly be the only method focused on.

#### The new aim of the project is to:
1. Further enhance the existing Lower-Bound calculation algorithm.
    1. Add a weighting algorithm to the class 
    2. Re-write the class to use while loops as opposed to the base version's recursion
    3. Investigate GPU acceleration and the possible performance gains
    4. examine hashmaps as substitutes to current data storage.
2. Practice software engineering concepts and practices;
    1. Special focus on documenting and coding practices.
3. Explore Java by practice;
4. Create a benchmarking system to compare different versions of the same program;
5. Create a python counterpart at least to the base version
6. Familiarise myself with the JetBrains IDEs
###### List is subject to changes.
Links to the original project description and requirements, and to the external graphs used will be provided later.

Expected update schedule is bi-weekly-ish on sundays (latest).

Questions and feedback are welcome at andor.lindtner@gmail.com

----------
##The old description
## Graph Colouring - Group 7

To easily navigate the project, you need a proper IDE.
We are using IntelliJ IDEA, so we will provide a mini tutorial for IDEA users.
It should work for Eclipse similarly.

For this project, we are using the Apache Commons Math package to compute
eigenvalues of the adjacency matrix. You can get the library here:
https://commons.apache.org/proper/commons-math/download_math.cgi

It is also located in the 'lib' directory, which you have to add as a
library in your IDE.

In IntelliJ IDEA, make the 'graph-coloring' folder as a source folder,
'algorithm' directory has to be regarded as a package.