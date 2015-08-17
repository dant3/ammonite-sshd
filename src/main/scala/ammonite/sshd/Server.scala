package ammonite.sshd

import java.io.{InputStream, OutputStream}

import ammonite.ops.Path
import ammonite.repl.{Bind, Ref, Repl, Storage}

import scala.annotation.tailrec
import scala.io.StdIn
import scala.language.postfixOps

class Server(sshConfig:SshServerConfig,
             ammoniteHomePath: Path = Repl.defaultAmmoniteHome,
             predef: String = "",
             replArgs: Seq[Bind[_]] = Nil) {
  private lazy val sshd = Ssh.createServer(sshConfig, terminal = Server.runAmmonite(ammoniteHomePath, predef, replArgs))

  def start():Unit = sshd.start()
  def stop():Unit = sshd.stop()
  def stopImmediately():Unit = sshd.stop(/*immediately =*/ true)
}

object Server {
  def main(args:Array[String]):Unit = {
    val config = SshServerConfig(port = 2222, users = SshUser("user", "1") :: Nil)
    val ammoniteServer = new Server(config)

    ammoniteServer.start()
    println(s"Ammonite server started. $helpMessage. Config: $config")
    exitAwaitLoop()
    ammoniteServer.stop()

    println("Ammonite server finished")
  }

  private val helpMessage = "Print 'q' to quit."

  @tailrec private def exitAwaitLoop(): Unit = StdIn.readLine() match {
    case "q" => println("exiting...")
    case cmd =>
      println(s"'$cmd' is illegal command! $helpMessage")
      exitAwaitLoop()
  }

  private def ammoniteServerClassLoader = Server.getClass.getClassLoader

  private def runAmmonite(homePath: Path, predef: String, replArgs: Seq[Bind[_]])(in:InputStream, out:OutputStream):Unit = {
    val ammoniteSessionEnv = new Environment(ammoniteServerClassLoader, in, out)
    Environment.withEnvironment(ammoniteSessionEnv) {
      new Repl(in, out, Ref(Storage(homePath)), predef, replArgs).run()
    }
  }
}