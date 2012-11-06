JFLAGS = -g
JC = javac
.SUFFIXES: .java .class

Echoer.class : Echoer.java
	javac Echoer.java
clean:
	$(RM) *.class	

