@REM
@REM  Licensed to the Apache Software Foundation (ASF) under one or more
@REM  contributor license agreements.  See the NOTICE file distributed with
@REM  this work for additional information regarding copyright ownership.
@REM  The ASF licenses this file to You under the Apache License, Version 2.0
@REM  (the "License"); you may not use this file except in compliance with
@REM  the License.  You may obtain a copy of the License at
@REM
@REM      http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM  Unless required by applicable law or agreed to in writing, software
@REM  distributed under the License is distributed on an "AS IS" BASIS,
@REM  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@REM  See the License for the specific language governing permissions and
@REM  limitations under the License.

@echo off
if "%OS%" == "Windows_NT" setlocal

if NOT DEFINED WC_HOME set WC_HOME=%~dp0..
if NOT DEFINED JAVA_HOME goto err

set JAVA_OPTS=-Xms64m -Xmx128m
set WC_PORT=8080

for %%i in ("%WC_HOME%\webapp\WEB-INF\lib\*.jar") do call :append "%%i"
goto okClasspath

:append
set CLASSPATH=%CLASSPATH%;%1
goto :eof

:okClasspath
set WC_CLASSPATH=%CLASSPATH%;"%WC_HOME%\webapp\WEB-INF\classes"
goto runDaemon

:runDaemon
echo Starting standalone Cassandra Web Console
"%JAVA_HOME%\bin\java" %JAVA_OPTS% -cp "%WC_CLASSPATH%" "net.ameba.cassandra.web.standalone.StandaloneServer" --port=%WC_PORT% --base=%WC_HOME%
goto finally

:err
echo JAVA_HOME environment variable must be set!
pause

:finally

ENDLOCAL
