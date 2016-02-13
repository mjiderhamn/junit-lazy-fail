package se.jiderhamn.lazyfail;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * JUnit runner that - in combination with {@link LazyFailAspect} - allows collecting multiple {@link AssertionError}s
 * in the same test case.
 * @author Mattias Jiderhamn
 */
public class LazyFailRunner extends BlockJUnit4ClassRunner {
  
  /** 
   * {@link ThreadLocal} that holds the list of {@link AssertionError}s (and possibly other {@link Throwable}s) for the 
   * test case currently being executed.
   */
  private static ThreadLocal<LinkedHashSet<Throwable>> assertErrors = new ThreadLocal<>();
  
  /** When this {@link ThreadLocal} has a value (of {@link Boolean#TRUE}), the thread is in fail fast mode and will throw
   * an exception as soon as an assertion fails or {@link Assert#fail()} is called. 
   */
  private static ThreadLocal<Boolean> failFastMode = new ThreadLocal<>();

  public LazyFailRunner(Class<?> klass) throws InitializationError {
    super(klass);
  }

  @Override
  protected Statement methodInvoker(FrameworkMethod method, Object test) {
    return new LazyFailInvokerInvoker(method, test);
  }

  private static class LazyFailInvokerInvoker extends Statement {
    
    private final FrameworkMethod method;
    
    private final Object target;

    public LazyFailInvokerInvoker(FrameworkMethod method, Object target) {
      this.method = method;
      this.target = target;
    }

    @Override
    public void evaluate() throws Throwable {
      final LinkedHashSet<Throwable> errorsWhileInvoking = new LinkedHashSet<>();
      assertErrors.set(errorsWhileInvoking); // Indicate we are ready to have errors enlisted

      try {
        method.invokeExplosively(target); // Run test case
      }
      catch (Throwable t) { // Non AssertionError stopped the execution
        addThrowable(t);
      }
      finally {
        if(! errorsWhileInvoking.isEmpty())
          MultipleFailureException.assertEmpty(new ArrayList<>(errorsWhileInvoking));

        assertErrors.remove();
      }
    }
  }

  /** 
   * Is lazy failing active? I.e. are we executing a test case in a test class annotated with 
   * {@code @RunWith(se.jiderhamn.lazyfail.LazyFailRunner.class)}?
   */
  public static boolean isActive() {
    return assertErrors.get() != null;
  }

  /** 
   * Add {@link AssertionError} to list of errors in the particular test case. In case we are in fail fast mode, will throw
   * all the accumulated errors. 
   */
  public static void addThrowable(Throwable throwable) throws Exception {
    final LinkedHashSet<Throwable> errors = assertErrors.get();
    errors.add(throwable);
    if(failFastMode.get() == Boolean.TRUE)
      MultipleFailureException.assertEmpty(new ArrayList<>(errors));
  }

  /** 
   * Perform job in {@code callback}, and if there are {@link AssertionError}s we will fail immediately, altough with 
   * all the errors collected so far. 
   */
  public static void failFast(Runnable callback) {
    if(failFastMode.get() == Boolean.TRUE) { // We are already in fail fast mode  
      callback.run();
    }
    else {
      try {
        failFastMode.set(Boolean.TRUE);
        callback.run();
      }
      finally {
        failFastMode.remove();
      }
    }
  }
  
  /** Non-lazy equivalent of {@link Assert#assertThat(Object, Matcher)} */
  public static <T> void assertNowThat(T actual, Matcher<? super T> matcher) {
    assertNowThat("", actual, matcher);
      }
  
  /** Non-lazy equivalent of {@link Assert#assertThat(String, Object, Matcher)} */
  public static <T> void assertNowThat(String reason, T actual, Matcher<? super T> matcher) {
    failFast(() -> assertThat(reason, actual, matcher));
  }
  
  /** Non-lazy equivalent of {@link Assert#fail(String)}, i.e. fail immediately */
  public static void failNow(String message) {
    failFast(() -> fail(message));
  }
}