package se.jiderhamn.lazyfail;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static se.jiderhamn.lazyfail.LazyFailRunner.failFast;
import static se.jiderhamn.lazyfail.LazyFailRunner.failNow;

/**
 * TODO Document
 * @author Mattias Jiderhamn
 */
@RunWith(LazyFailRunner.class)
public class LazyFailExampleTest {
  
  @Test
  public void twoAsserts() {
    assertTrue("This will be included...", false);
    assertFalse("... as well as this", true);
  }
  
  @Test
  public void failImmediately() {
    assertTrue("This will be included", false);
    failNow("This will fail immediately");
    assertFalse("This will not be included", true);
  }
  
  @Test
  public void assertImmediately() {
    failFast(() -> {
      assertTrue("This will fail immediately", false);
      assertFalse("This will not be included", true);
    });
  }

}