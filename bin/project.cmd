::/*#! 2> /dev/null                                   #
@ 2>/dev/null # 2>nul & echo off & goto BOF           #
if [ -z "${SIREUM_HOME}" ]; then                      #
  echo "Please set SIREUM_HOME env var"               #
  exit -1                                             #
fi                                                    #
exec "${SIREUM_HOME}/bin/sireum" slang run "$0" "$@"  #
:BOF
setlocal
if not defined SIREUM_HOME (
  echo Please set SIREUM_HOME env var
  exit /B -1
)
"%SIREUM_HOME%\bin\sireum.bat" slang run %0 %*
exit /B %errorlevel%
::!#*/
// #Sireum

import org.sireum._
import org.sireum.project.ProjectUtil._
import org.sireum.project.Project

val logika = "logika"

val protocol = "protocol"

val homeDir = Os.slashDir.up.canon

val protocolShared = moduleSharedPub(
  id = protocol,
  baseDir = homeDir,
  sharedDeps = sharedId(logika),
  sharedIvyDeps = ISZ(),
  pubOpt = pub(
    desc = "Sireum Client/Server Protocol",
    url = "github.com/sireum/protocol",
    licenses = bsd2,
    devs = ISZ(robby)
  )
)

val project = Project.empty + protocolShared

projectCli(Os.cliArgs, project)
