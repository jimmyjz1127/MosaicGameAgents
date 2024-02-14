#!/bin/sh

DIRM=$(pwd)

JUNITPATH="$DIRM/../lib"

SRCPATH="$DIRM/../src"

CLASSPATH=".:$CLASSPATH:$JUNITPATH/*:$SRCPATH/*"

export CLASSPATH

javac $SRCPATH/*.java -d ./

javac *.java 

# java org.junit.platform.console.ConsoleLauncher -p=test

java org.junit.platform.console.ConsoleLauncher --scan-classpath