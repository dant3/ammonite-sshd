package ammonite.sshd

import ammonite.ops.Path
import ammonite.repl.Repl

case class SshServerConfig(
  port: Int,
  users:List[Creds],
  ammoniteHome:Path = Repl.defaultAmmoniteHome,
  hostKeyFile:Option[Path] = None
) { override def toString = s"(port = $port, users = '$users', hostKeyFile = $hostKeyFile)" }