#!/bin/bash

# CONTROLLING STARTUP:
#
# This script relies on few environment variables to determine startup
# behavior, those variables are:
#
#   CLASSPATH -- A Java classpath containing everything necessary to run.
#   JVM_OPTIONS -- Path to file containing JVM options
#

parse_jvm_options() {
  if [ -f "$1" ]; then
    echo "$(grep "^-" "$1" | tr '\n' ' ')"
  fi
}


CDPATH=""
SCRIPT="$0"


# SCRIPT may be an arbitrarily deep series of symlinks. Loop until we have the concrete path.
while [ -h "$SCRIPT" ] ; do
  ls=`ls -ld "$SCRIPT"`
  # Drop everything prior to ->
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    SCRIPT="$link"
  else
    SCRIPT=`dirname "$SCRIPT"`/"$link"
  fi
done

# determine spark home
JCSEG_H=`dirname "$SCRIPT"`

# make JCSEG_H an absolute path
JCSEG_H=`cd "$JCSEG_H"; pwd`

# set the jcseg classpath
JCSEG_CLASSPATH="$JCSEG_H/lib/*"

if [ -z "$JCSEG_JVM_OPTIONS" ]; then
    for jvm_options in "$JCSEG_H"/config/jvm.options /etc/jcseg/jvm.options; do
        if [ -r "$jvm_options" ]; then
            JCSEG_JVM_OPTIONS=$jvm_options
            break
        fi
    done
fi

JCSEG_JAVA_OPTS="$(parse_jvm_options "$JCSEG_JVM_OPTIONS") $JCSEG_JAVA_OPTS"

# Check the java binary starter
if [ -x "$JAVA_HOME/bin/java" ]; then
    JAVA="$JAVA_HOME/bin/java"
else
    JAVA=`which java`
fi

if [ ! -x "$JAVA" ]; then
    echo "Could not find any executable java binary. Please install java in your PATH or set JAVA_HOME"
    exit 1
fi

# Future feature
# the may needed spark classpath setting 
# if [ -z "$JCSEG_CLASSPATH" ]; then
#     echo "You must set the JCSEG_CLASSPATH var" >&2
#     exit 1
# fi


# full hostname passed through cut for portability on systems that do not support hostname -s
# export on separate line for shells that do not support combining definition and export
HOSTNAME=`hostname | cut -d. -f1`
export HOSTNAME


# ./test 1 2 3
# $*: "1 2 3"
# $@: "1" "2" "3"
# $#: 3 (arguments number)

# manual parsing to find out, if process should be detached
properties="$JCSEG_H/config/jcseg-server.properties";
daemonized=`echo $* | egrep -- '(^-d |-d$| -d |--daemonize$|--daemonize )'`
if [ -z "$daemonized" ] ; then
    exec "$JAVA" $JCSEG_JAVA_OPTS -Dpath.home="$JCSEG_H" \
        -cp "$JCSEG_CLASSPATH" org.lionsoul.jcseg.server.Bootstrap "$properties" "$@"
else
    exec "$JAVA" $JCSEG_JAVA_OPTS -Dpath.home="$JCSEG_H" \
        -cp "$JCSEG_CLASSPATH" org.lionsoul.jcseg.server.Bootstrap "$properties" > /dev/null &
    retval=$?
    [ $retval -eq 0 ] || exit $retval
fi

exit $?
