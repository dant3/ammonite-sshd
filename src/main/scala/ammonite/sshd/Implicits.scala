package ammonite.sshd

import org.apache.sshd.common.Factory
import org.apache.sshd.server.Command

import scala.language.implicitConversions

trait Implicits {
  implicit def factory(factoryFunction:() ⇒ Command):Factory[Command] = new Factory[Command] {
    override def create(): Command = factoryFunction()
  }

  implicit def runnable(block: ⇒ Unit):Runnable = new Runnable {
    override def run(): Unit = block
  }
}

object Implicits extends Implicits
