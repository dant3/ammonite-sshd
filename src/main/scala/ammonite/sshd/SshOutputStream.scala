package ammonite.sshd

import java.io.OutputStream

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
