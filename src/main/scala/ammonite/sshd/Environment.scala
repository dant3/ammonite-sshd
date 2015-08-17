package ammonite.sshd

import java.io.{OutputStream, InputStream, PrintStream}

case class Environment(
  thread:Thread,
  contextClassLoader: ClassLoader,
  in: InputStream,
  out:PrintStream,
  err:PrintStream
) {
  def this(classLoader: ClassLoader, in: InputStream, out:PrintStream) = this(Thread.currentThread(), classLoader, in, out, out)
  def this(classLoader: ClassLoader, in: InputStream, out:OutputStream) = this(classLoader, in, new PrintStream(out))
}

object Environment {
  def collect() = Environment(
    Thread.currentThread(),
    Thread.currentThread().getContextClassLoader,
    System.in,
    System.out,
    System.err
  )

  def withEnvironment(env:Environment)(code: ⇒ Any):Any = {
    val oldEnv = collect()
    try {
      install(env)
      code
    } finally {
      install(oldEnv)
    }
  }

  def install(env:Environment):Unit = {
    env.thread.setContextClassLoader(env.contextClassLoader)
    System.setIn(env.in)
    System.setOut(env.out)
    System.setErr(env.err)
  }
}
