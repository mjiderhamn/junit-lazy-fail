package se.jiderhamn.lazyfail;

import java.util.ArrayList;
import java.util.List;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;

import static org.junit.Assert.fail;

/**
 * TODO Document
 * @author Mattias Jiderhamn
 */
public class LazyFailRunner extends BlockJUnit4ClassRunner {
  
  // TODO Consider implications of ThreadLocal + initialValue
  private static ThreadLocal<List<Throwable>> assertErrors = new ThreadLocal<>();
  
  /** When this {@link ThreadLocal} has a value (of {@code true}), the thread is in fail fast mode */
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
      final ArrayList<Throwable> errorsWhileInvoking = new ArrayList<>();
      assertErrors.set(errorsWhileInvoking); // Indicate we are ready to have errors enlisted

      try {
        method.invokeExplosively(target);
      }
      catch (Throwable t) {
        addAssertionError(t);
      }
      finally {
        MultipleFailureException.assertEmpty(errorsWhileInvoking); 

        assertErrors.remove();
      }
    }
  }
  
  /** TODO */
  public static boolean isActive() {
    return assertErrors.get() != null;
  }

  /** TODO */
  public static void addAssertionError(Throwable throwable) throws Exception {
    final List<Throwable> errors = assertErrors.get();
    errors.add(throwable);
    if(failFastMode.get() == Boolean.TRUE)
      MultipleFailureException.assertEmpty(errors);
  }
  
  /** 
   * Perform job in {@code callback}, and if there are {@link AssertionError}s we will fail immediately, altough with 
   * all the errors collected so far. 
   */
  static void failFast(Runnable callback) {
    try {
      failFastMode.set(Boolean.TRUE);
      callback.run();
    }
    finally {
      failFastMode.remove();
    }
  }
  
  /** TODO */
  public static void failNow(String message) {
    failFast(() -> fail(message));
  }
}