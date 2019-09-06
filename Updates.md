
####2019.09.03
Made header for the .csv.
Found a bug in the printed file 

----
####2019.08.25
Something is still funny with the path. Ran the first successful test since restarting the project. Not having headers in the .csv really hurts quickly checking whether the data output is what is expected or something full garbage. Will spend tomorrow afternoon on it. The zero columns worry me. one is probably the upper bound, one the lower. If that's the case I must figure out where the print is missing.   

----
####2019.08.24
New class added for the weighted lower bound. Was a bit too quick, should have refactored the original first. Cleaned up commented out debug lines, and did a light overview on how the algorithm is implemented. Some comments need to be rewritten when this class gets javadoc for better understanding. Variables may be subject to another wave of renaming.

Top 25% check option temporarily removed. For the final benchmark it'd be interesting to compare how accurate each algorithm is when it restricted to only top 5, 25, 5% and 25% of the nodes in their execution order. For now it's just more clutter.

PrintMatrix added to Util. Util needs to be renamed, but refactoring would need prep. Will be done after the baseline benchmark.

Main is considered to be good enough for now.
readGraph got it's imports optimised.
 
----
#### 2019.08.17
Found the hardcoded path that made BulkComparison throw nullpointer expection every time. **Hardcoded paths are bad things, MMMMMMKAY?** ~~do you best Mr. Mackey impression~~

Pathing must be fixed, not pushing till that's changed. Not hard, but it's late in the night.

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