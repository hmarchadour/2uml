2uml (ALPHA)
=======
This Eclipse project (EPL license) aims to provide a simple way to convert a JDT projects/Jars to UML.

Jar -> UML
-------

* We have a set of jars in input, we create an UML file in output.
* It's a one shot operation (not synchronizable or mergeable).

Technically:
* we read jar entries.
* we create releated UML items.
* we handle relations between previously created items.


JDT project -> UML
-------

* The scope isn't already defined, the idea is to provide a kind or synchronizable or mergeable life cycle between a set of JDT projects.


