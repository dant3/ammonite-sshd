package ammonite.sshd

import org.slf4j.LoggerFactory

trait Logging {
  lazy val logger = LoggerFactory.getLogger(loggerName)
  def loggerName = getClass.getName
}
