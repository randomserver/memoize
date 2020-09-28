package se.randomserver.memoize

import scala.collection.mutable

class Memoize1[T, R](f: T => R) extends (T => R) {
  private val c: mutable.Map[T, R] = mutable.Map.empty

  override def apply(v1: T): R = c.getOrElseUpdate(v1, f(v1))
}