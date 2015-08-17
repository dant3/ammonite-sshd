package ammonite.sshd

import java.io.{InputStream, OutputStream}

import org.apache.sshd.server.PasswordAuthenticator
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider
import org.apache.sshd.server.session.ServerSession

import scala.language.implicitConversions

object Ssh {
  type TerminalTask = ((InputStream, OutputStream) ⇒ Unit)

  def passwordAuthenticator(users:Map[String, String]) = new PasswordAuthenticator {
    override def authenticate(username: String, password: String, session: ServerSession): Boolean =
      users.get(username).contains(password)
  }

  def passwordAuthenticator(users:List[SshUser]):Option[PasswordAuthenticator] = users match {
    case Nil ⇒ None
    case someList ⇒
      Some(passwordAuthenticator(someList.foldLeft(Map.newBuilder[String, String]) { _ += _.toTuple }.result()))
  }
  
  def createServer(options:SshServerConfig, terminal: TerminalTask) = {
    import Implicits._

    val passwdAuthenticator = passwordAuthenticator(options.users).getOrElse(cantAuthenticateError)
    val sshServer = org.apache.sshd.SshServer.setUpDefaultServer()
    sshServer.setPort(options.port)
    sshServer.setPasswordAuthenticator(passwdAuthenticator)
    // TODO: public key auth
    // sshServer.setPublickeyAuthenticator(pkAuth))
    sshServer.setKeyPairProvider(new SimpleGeneratorHostKeyProvider())
    sshServer.setShellFactory(() ⇒ new Shell(terminal))
    sshServer
  }

  def cantAuthenticateError[T] = throw new IllegalArgumentException("Can't authenticate without users setting")
}
