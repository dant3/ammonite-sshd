package ammonite.sshd

case class Creds(login:String, password:String) {
  def toTuple:(String, String) = (login, password)
}

