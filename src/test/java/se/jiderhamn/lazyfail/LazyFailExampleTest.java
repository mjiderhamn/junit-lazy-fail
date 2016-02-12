package se.jiderhamn.lazyfail;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * TODO Document
 * @author Mattias Jiderhamn
 */
@RunWith(LazyFailRunner.class)
public class LazyFailExampleTest {
  
  @Test
  public void example() {
    assertTrue("This is not true", false);
    assertFalse("This is not false", true);
  }
  
}