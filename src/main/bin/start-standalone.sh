#!/bin/sh
WC_HOME=`dirname $0`
WC_PORT=8080

echo WC_HOME:${WC_HOME}

CLASSPATH="${WC_HOME}/../webapp/WEB-INF/classes"
for jar in ${WC_HOME}/../webapp/WEB-INF/lib/*.jar; do
	CLASSPATH=$CLASSPATH:$jar
done

JAVA_OPTS="-Xmx128m -Xms64m"

# Use JAVA_HOME if set, otherwise look for java in PATH
if [ -x "$JAVA_HOME/bin/java" ]; then
    JAVA="$JAVA_HOME/bin/java"
else
    JAVA=`which java`
fi

# Special-case path variables.
case "`uname`" in
    CYGWIN*) 
        CLASSPATH=`cygpath -p -w "$CLASSPATH"`
        CASSANDRA_CONF=`cygpath -p -w "$CASSANDRA_CONF"`
    ;;
esac

echo ${CLASSPATH}
exec "${JAVA}" ${JAVA_OPTS} net.ameba.cassandra.web.standalone.StandaloneServer --port=${WC_PORT} --base="${WC_HOME}/../"
