package se.jiderhamn.lazyfail;

import java.util.List;

/**
 * TODO Document
 * @author Mattias Jiderhamn
 */
public class MultipleAssertionsFailedError extends junit.framework.AssertionFailedError {
  
  private final List<AssertionError> errors;

  public MultipleAssertionsFailedError(List<AssertionError> errors) {
    super(errors.toString());
    this.errors = errors;
    initStacktrace(errors);
  }

  public MultipleAssertionsFailedError(String message, List<AssertionError> errors) {
    super(message);
    this.errors = errors;
    initStacktrace(errors);
  }
  
  private void initStacktrace(List<AssertionError> errors) {
    this.setStackTrace(errors.get(0).getStackTrace()); // Copy stacktrace of first element
  }

  // TODO Show all nested stacktraces when failing
}