https://stackoverflow.com/questions/27925954/is-arrays-streamarray-name-sum-slower-than-iterative-approach

####2019.10.19
Been a while since an update was posted. Sideprojects, like massive amount of nerding out on awesome Project Euler problems, preparing for exams and interview got this project on hold. Spent a couple of days with offline attempts at writing pseudocode and more detailed sketches for the ranking algorithm.

As a result after several rounds of doubting whether the ranking is correct, breaking it, and realising that the original idea is sufficient enough begun testing. Current results show that in some cases reordering does give a faster result. At the moment no idea how to decide on re-sorting.

The new SingleTest file is temporary, for debugging.

Next step I'd like to work on is taking the recursion method out of the files, and give it a separate class for better structure. If that's done, the 'do-while' version will be priority. Before I get to that though, I'd like to sort out the .cvs naming convention, header and the bulk comparison method's broken pathing. 

On the side I've looked up on memory consumption monitoring methods for a more extensive comparison. As of now the tests are only ran on the "Kelk" graphs, so there shouldn't be high consumption, but the queen graphs could produce interesting numbers.

PS: Next update may take longer. Interviews coming up, and new project related books got into my hand. O'Reilly books on java performance., performance tuning and a different Test Driven development one for other projects. Announcement about those coming soon!    

----
####2019.09.11

Found the error in the printing of the lower bound to file. The lowerboundHeap file was broken due to the too excessive deletes in the previous stages.

Knowing there is still much to do with the base half of the codes I'll proceed to the actual goal finally, and start writing the new algorithms. First target is the ranking  

----
####2019.09.03
Made header for the .csv. <br>
Found a bug in the printed file. The lowerbound does not display. Further investigations showed that the function call return zero value for each run.

The output file has headers from now on. 

----
####2019.08.25
Something is still funny with the path. Ran the first successful test since restarting the project. Not having headers in the .csv really hurts quickly checking whether the data output is what is expected or something full garbage. Will spend tomorrow afternoon on it. The zero columns worry me. one is probably the upper bound, one the lower. If that's the case I must figure out where the print is missing.   

----
####2019.08.24
New class added for the weighted lower bound. Was a bit too quick, should have refactored the original first. Cleaned up commented out debug lines, and did a light overview on how the algorithm is implemented. Some comments need to be rewritten when this class gets javadoc for better understanding. Variables may be subject to another wave of renaming.

Top 25% check option temporarily removed. For the final benchmark it'd be interesting to compare how accurate each algorithm is when it restricted to only top 5, 25, 5% and 25% of the nodes in their execution order. For now it's just more clutter.

PrintMatrix added to Util. Util needs to be renamed, but refactoring would need prep. Will be done after the baseline benchmark.

Main is considered to be good enough for now.<br>
readGraph got it's imports optimised.
 
----
#### 2019.08.17
Found the hardcoded path that made BulkComparison throw nullpointer expection every time. **Hardcoded paths are bad things, MMMMMMKAY?** ~~do you best Mr. Mackey impression~~

Pathing must be fixed, not pushing till that's changed. Not hard, but it's late in the night. (Project was indeed uploaded without fixing this to ask for linux specific help from a friend. Fix pending)

----
#### 2019.08.16
As a temporary solution Config.java will hold the preset settings for things I'd like to be customizable or modifiable from a menu (to be built later).

Cleaned more of BulkComparison, and touched on readGraph to remove some commented sections.

Uncommented the disconnected node counter (except original comments, that'll be added to javaDoc), and deleted some of the original comments of the uni-project. This file's original was provided by the coordinators to give us a helping hand in starting. I took out some functionality and transformed the rest to better fit our needs in the second and third phase of the project. I'll upload the "Red Bible" if I find it somewhere.

The ErrorListener was probably added by Paul, but never properly implemented as we were very close to the deadline. For the time being it's removed.

The amount of errors SonarLint highlights is truly amazing. Just like the refactor and TODO functions. An IDE isn't that bad after all ;)
  

----
#### 2019.08.15
Cleared up the bulk comparison file, it was hard to read with all the comments. More is about to come, investigating how the IDE's refactoring changes variable names before i mess up the whole file.

----
#### 2019.08.14
Moving to package was unsuccessful due to classpath not set up on manjaro. Still needs fixing.
Didn't think I'll ever want to go back to Notepad++ and windows to develop. Past three days little time I could spare for the project was spent on IntelliJ feature/bug hunting, fixing and customising to help the workflow.

Local history proved to be the most valuable feature of the IDE, can't word how much I love this feature.
Found out that the github and gitlab backups only contain the .gitignore and readme files.

A separate settings file is needed with at least the following options:
1. debug y/n
2. runtime limit (per thread)
3. file print y/n
4. graph draw y/n
5. draw graphs y/n (the chart type)
6. visualize graphs y/n (more about this later)

Logger needs to be tested for performance later. Output should print into a csv for each file the runtime for each called method. At this point this sounds too complicated. May not even be necessary

----
#### 2019.08.04
Package created for the algorithms. Small steps to make this pile of code resemble a program. Was a bit clumsy in the beginning not knowing what intelliJ is trying to say with the tons of popups, but turned out to be really helpful to get the refactoring done. Was rather painless.Really hate myself now for not enforcing proper version control while we were writing this.<br>
Looked up the wonderful `System.getProperty` function. Is really helpful to refactor file creation to be OS independent. More on that next time.   
 
----
#### 2019.07.31
Finished revision of the files I'd like to work with. The mess is a lot bigger than I recalled.
Found bug in runtime calc, low priority fix.
###### Short term To-Do: <br>
1. Restore commandline printing
2. Deprecate items that are not priority
3. Find where CSV outputs are implemented, and restore that
----