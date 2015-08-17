package ammonite.sshd

import java.io.{InputStream, OutputStream}

import ammonite.ops.Path
import ammonite.repl.{Bind, Ref, Repl, Storage}
import ammonite.sshd.util.Environment

import scala.annotation.tailrec
import scala.io.StdIn
import scala.language.postfixOps

class SshdRepl(sshConfig:SshServerConfig,
             predef: String = "",
             replArgs: Seq[Bind[_]] = Nil) {
  private lazy val sshd = SshServer.create(
    sshConfig, terminal = SshdRepl.runRepl(sshConfig.ammoniteHome, predef, replArgs))

  def start():Unit = sshd.start()
  def stop():Unit = sshd.stop()
  def stopImmediately():Unit = sshd.stop(/*immediately =*/ true)
}

object SshdRepl {
  def main(args:Array[String]):Unit = {
    val creds = getInitialCreds()
    val config = SshServerConfig(port = 2222, users = creds :: Nil)
    val ammoniteServer = new SshdRepl(config)

    ammoniteServer.start()
    println(s"Ammonite server started. $helpMessage. To connect use ssh [${creds.login}@]<host> -p${config.port}")
    exitAwaitLoop()
    ammoniteServer.stop()

    println("Ammonite server finished")
  }

  private def getInitialCreds():Creds = {
    val startedUserName = System.getProperty("user.name")
    System.out.print("Enter password for remote session: ")
    val password = new String(System.console().readPassword())
    Creds(startedUserName, password)
  }


  private val helpMessage = "Print 'q' to quit."
  @tailrec private def exitAwaitLoop(): Unit = StdIn.readLine() match {
    case "q" => println("exiting...")
    case cmd =>
      println(s"'$cmd' is illegal command! $helpMessage")
      exitAwaitLoop()
  }

  private def replServerClassLoader = SshdRepl.getClass.getClassLoader

  private def runRepl(homePath: Path, predef: String, replArgs: Seq[Bind[_]])(in:InputStream, out:OutputStream):Unit = {
    val replSessionEnv = new Environment(replServerClassLoader, in, out)
    Environment.withEnvironment(replSessionEnv) {
      new Repl(in, out, Ref(Storage(homePath)), predef, replArgs).run()
    }
  }
}