@echo off

set DBGOPTS=-agentlib:jdwp=transport=dt_socket,server=y,address=8787,suspend=n
set CWD=%CD%
set ARGC=0
for %%x in (%*) do set /A ARGC+=1

set PIPELINE_OPT=-Dmaedr.pipeline=com.zotoh.maedr.wflow.FlowModule
set MAEDR_LOCALE=en_US
set MAEDR_HOME=%~dp0..
set BINDIR=%~dp0
set PATCHDIR=%MAEDR_HOME%\patch\*
set DISTRO=%MAEDR_HOME%\dist\*
set TPCL=%MAEDR_HOME%\thirdparty\*
set LIBDIR=%MAEDR_HOME%\lib\*
set DBG=%CWD%\lib\*;%CWD%\thirdparty\*;%CWD%\classes
set CP=%PATCHDIR%;%CLASSPATH%;%DISTRO%;%LIBDIR%;%TPCL%
set JAVA_CMD=java.exe
set L4JFILE=%CD%\cfg\log4j.properties
set L4J=file:/%L4JFILE%
set LOGCFG=%L4J:\=/%
set LOGREF=-Dlog4j.configuration=%LOGCFG% 

if "%JAVA_HOME%" == "" goto noJavaHome
if NOT EXIST %L4JFILE% SET LOGREF=
if %ARGC% GTR 1 goto testDebug
:b0


:apprun
"%JAVA_HOME%\bin\%JAVA_CMD%" -cp %CP% %LOGREF% -Dmaedr.locale=%MAEDR_LOCALE% %PIPELINE_OPT% com.zotoh.maedr.etc.AppRunner %MAEDR_HOME% %*
goto end

:appdbg
"%JAVA_HOME%\bin\%JAVA_CMD%" %DBGOPTS% -cp %DBG%;%CP% %LOGREF% -Dmaedr.locale=%MAEDR_LOCALE% %PIPELINE_OPT% com.zotoh.maedr.etc.AntStart %*
goto end





:j664
set JAVA_HOME=C:\Program Files\Java\jre6
goto b0
:j564
set JAVA_HOME=C:\Program Files\Java\jre5
goto b0
:j632
set JAVA_HOME=C:\Program Files (x86)\Java\jre6
goto b0
:j532
set JAVA_HOME=C:\Program Files (x86)\Java\jre5
goto b0


:testDebug
if "%1%2" == "remote-debugmaedr-server" goto appdbg
goto b0 



:noJavaHome
echo No JAVA_HOME set, attempt to reference standard java location.
if exist "C:\Program Files\Java\jre6" goto j664
if exist "C:\Program Files\Java\jre5" goto j564
if exist "C:\Program Files (x86)\Java\jre6" goto j632
if exist "C:\Program Files (x86)\Java\jre5" goto j532
echo Please set JAVA_HOME
:end



