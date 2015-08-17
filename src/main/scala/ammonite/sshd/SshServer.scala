package ammonite.sshd

import java.io.{InputStream, OutputStream}

import org.apache.sshd.server.PasswordAuthenticator
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider
import org.apache.sshd.server.session.ServerSession

case class SshServerConfig(
  port: Int,
  user: String,
  passwd: String,
  host: Option[List[String]] = None
) { override def toString = s"(port = $port, user = '$user', passwd = '$passwd', host = $host)" }

object SshServer {
  import Implicits._

  type TerminalTask = ((InputStream, OutputStream) ⇒ Unit)

  def passwordAuthenticator(username:String, password:String) = new PasswordAuthenticator {
    override def authenticate(login: String, passwd: String, session: ServerSession): Boolean = {
      login == username && password == passwd
    }
  }
  
  def passwordAuthenticator(options:SshServerConfig):Option[PasswordAuthenticator] = {
    Some(passwordAuthenticator(options.user, options.passwd))
  } // might be not needed for pk-auth only
  
  def apply(options:SshServerConfig, terminal: TerminalTask) = {
    val sshServer = org.apache.sshd.SshServer.setUpDefaultServer()
    sshServer.setPort(options.port)
    options.host.map(_.mkString(",")).foreach(sshServer.setHost)
    passwordAuthenticator(options).foreach(sshServer.setPasswordAuthenticator)
    // TODO: public key auth
    //sshServer.setPublickeyAuthenticator(pkAuth))
    sshServer.setKeyPairProvider(new SimpleGeneratorHostKeyProvider())
    sshServer.setShellFactory(() ⇒ new Shell(terminal))
    sshServer
  }
}
