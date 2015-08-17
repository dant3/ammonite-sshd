package ammonite.sshd

import java.io.OutputStream

// fixes newline for ssh
class SshOutputStream(out:OutputStream) extends OutputStream {
  override def close() { out.close() }
  override def flush() { out.flush() }

  override def write(b: Int) {
    if (b.toChar == '\n') out.write('\r')
    out.write(b)
  }

  override def write(b: Array[Byte]) {
    var i = 0
    while (i < b.length) {
      write(b(i))
      i += 1
    }
  }

  override def write(b: Array[Byte], off: Int, len: Int) {
    write(b.slice(off, off + len))
  }
}
