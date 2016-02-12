package se.jiderhamn.lazyfail;

import java.util.ArrayList;
import java.util.List;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;

/**
 * TODO Document
 * @author Mattias Jiderhamn
 */
public class LazyFailRunner extends BlockJUnit4ClassRunner {
  
  // TODO Consider implications of ThreadLocal + initialValue
  private static ThreadLocal<List<Throwable>> assertErrors = new ThreadLocal<>();

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
      try {
        final ArrayList<Throwable> errorsWhileInvoking = new ArrayList<>();
        
        assertErrors.set(errorsWhileInvoking); // Indicate we are ready to have errors enlisted
        
        method.invokeExplosively(target);

        MultipleFailureException.assertEmpty(errorsWhileInvoking); 
      }
      finally {
        assertErrors.remove();
      }
    }
  }
  
  /** TODO */
  public static boolean isActive() {
    return assertErrors.get() != null;
  }

  /** TODO */
  public static void addAssertionError(AssertionError error) {
    assertErrors.get().add(error);
  }
}