package test

import org.scalatest.junit.AssertionsForJUnit
import org.junit.Assert._
import org.junit._

class TestSuite extends AssertionsForJUnit {

  @Before 
  def iniz() {
  }

  @After 
  def finz() {
  }

  @Test 
  def testDummy() {
    println("test OK")
    assertTrue(true)
  }

}
