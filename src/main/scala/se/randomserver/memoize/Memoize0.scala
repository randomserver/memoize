package se.randomserver.memoize

class Memoize0[R](f: () => R) extends (() => R) {
  private lazy val cached = f()
  def apply(): R = cached
}
