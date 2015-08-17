package ammonite.sshd

import ammonite.ops.Path

case class SshServerConfig(
  port: Int,
  users:List[SshUser],
  hostKeyPath: Option[Path] = None
) { override def toString = s"(port = $port, users = '$users', hostKeyPath = $hostKeyPath)" }

case class SshUser(login:String, password:String) {
  def toTuple:(String, String) = (login, password)
}
