package ammonite.sshd

import org.apache.sshd

class Shell(runner: SshServer.TerminalTask) extends sshd.server.Command with Logging {
  logger.debug("Instantiated")

  var in: java.io.InputStream = null
  var out: java.io.OutputStream = null
  var err: java.io.OutputStream = null
  var exit: Option[sshd.server.ExitCallback] = None
  var thread: Option[Thread] = None

  def setInputStream(in: java.io.InputStream) { this.in = in }
  def setOutputStream(out: java.io.OutputStream) { this.out = new SshOutputStream(out) }
  def setErrorStream(err: java.io.OutputStream) { this.err = err }

  def setExitCallback(exit: org.apache.sshd.server.ExitCallback) { this.exit = Option(exit) }

  def start(env: org.apache.sshd.server.Environment) {
    import Implicits._
    val thread = new Thread({
      runner(in, out)
      this.thread = None
      exit.foreach(_.onExit(0, "repl finished"))
    })
    this.thread = Some(thread)
    thread.start()
  }

  def destroy() {
    thread.foreach(_.interrupt())
  }
}
