package ammonite.sshd

import java.io.{InputStream, OutputStream}

import ammonite.repl.{Ref, Repl, Storage}

import scala.annotation.tailrec
import scala.io.StdIn
import scala.language.postfixOps

object AmmoniteSshd extends Logging {
  def main(args:Array[String]):Unit = {
    val config = SshServerConfig(port = 2222, user = "user", passwd = "1")
    val sshd = SshServer(config, terminal = runAmmonite)

    sshd.start()
    println(s"Ammonite server started. Print 'q' to quit. Config: $config")
    commandLoop()
    sshd.stop()

    println("Ammonite server finished")
  }

  @tailrec
  private def commandLoop(): Unit =
    StdIn.readLine() match {
      case "q" => println("exiting...")
      case cmd =>
        println(s"'$cmd' is illegal command! Print 'q' to quit.")
        commandLoop()
    }

  private def storage = Ref(Storage(Repl.defaultAmmoniteHome))

  def runAmmonite(in:InputStream, out:OutputStream):Unit = new Repl(in, out, storage).run()
}