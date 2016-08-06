package pfeile.test.scala

import org.scalamock.matchers.Matchers
import org.scalamock.scalatest.MockFactory
import org.scalatest.{Inspectors, FlatSpec}

class CommonTestSuite extends FlatSpec with Matchers with MockFactory with Inspectors
