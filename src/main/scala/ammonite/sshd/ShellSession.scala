package ammonite.sshd

import java.io.{OutputStream, InputStream}

import ammonite.sshd.util.Implicits
import org.apache.sshd

class ShellSession(runner: SshServer.TerminalTask) extends sshd.server.Command {
  var in: InputStream = _
  var out: OutputStream = _
  var err: OutputStream = _
  var exit: Option[sshd.server.ExitCallback] = None
  var thread: Option[Thread] = None

  def setInputStream(in: InputStream) { this.in = in }
  def setOutputStream(out: OutputStream) { this.out = new SshOutputStream(out) }
  def setErrorStream(err: OutputStream) { this.err = err }

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


  class SshOutputStream(out:OutputStream) extends OutputStream {
    override def close() { out.close() }
    override def flush() { out.flush() }

    override def write(b: Int) { // ssh only accepts new lines with \r
      if (b.toChar == '\n') out.write('\r')
      out.write(b)
    }

    override def write(bytes: Array[Byte]):Unit = for {
      i ← bytes.indices
    } write(bytes(i))

    override def write(bytes: Array[Byte], off: Int, len: Int) {
      write(bytes.slice(off, off + len))
    }
  }
}
