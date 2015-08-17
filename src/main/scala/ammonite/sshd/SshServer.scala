package ammonite.sshd

import java.io.{InputStream, OutputStream}

import ammonite.ops.Path
import ammonite.sshd.util.Implicits
import org.apache.sshd.server.PasswordAuthenticator
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider
import org.apache.sshd.server.session.ServerSession

import scala.language.implicitConversions

object SshServer {
  val in = System.in
  val out = System.out

  type TerminalTask = ((InputStream, OutputStream) ⇒ Unit)

  def create(options:SshServerConfig, terminal: TerminalTask) = {
    import Implicits._

    val passwdAuthenticator = passwordAuthenticator(options.users).getOrElse(cantAuthenticateError)
    val sshServer = org.apache.sshd.SshServer.setUpDefaultServer()
    sshServer.setPort(options.port)
    sshServer.setPasswordAuthenticator(passwdAuthenticator)
    // TODO: public key auth
    // sshServer.setPublickeyAuthenticator(pkAuth))

    val hostKeyFile = options.hostKeyFile.getOrElse(options.ammoniteHome / "ssh" / "hostkeys")
    sshServer.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(touch(hostKeyFile).toString()))

    sshServer.setShellFactory(() ⇒ new ShellSession(terminal))
    sshServer
  }

  def touch(file:Path):Path = {
    import ammonite.ops._
    if (!exists(file)) {
      write(file, Array.empty[Byte])
    }
    file
  }


  def passwordAuthenticator(users:List[Creds]):Option[PasswordAuthenticator] = users match {
    case Nil ⇒ None
    case someList ⇒
      Some(passwordAuthenticator(someList.foldLeft(Map.newBuilder[String, String]) { _ += _.toTuple }.result()))
  }

  def passwordAuthenticator(users:Map[String, String]) = new PasswordAuthenticator {
    override def authenticate(username: String, password: String, session: ServerSession): Boolean =
      users.get(username).contains(password)
  }

  def cantAuthenticateError[T] = throw new IllegalArgumentException("Can't authenticate without users setting")
}
