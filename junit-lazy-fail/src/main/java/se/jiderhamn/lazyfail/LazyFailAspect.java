package se.jiderhamn.lazyfail;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Aspect that will change the behaviour of calls to {@link org.junit.Assert} or {@link junit.framework.Assert} methods.
 * @author Mattias Jiderhamn
 */
@Aspect
public class LazyFailAspect {
  
  /** Pointcut for call to any method in {@link org.junit.Assert} */
  @Pointcut("call(* org.junit.Assert.*(..))")
  public void org_junit_Assert() {}
  
  /** Pointcut for call to any method in {@link junit.framework.Assert} */
  @Pointcut("call(* junit.framework.Assert.*(..))")
  public void junit_framework_Assert() {}
  
  /** Around advice that will catch any {@link AssertionError} and enlist in {@link LazyFailRunner} */
  @Around("(org_junit_Assert() || junit_framework_Assert())")
  public void assertMethod(ProceedingJoinPoint jp) throws Throwable {
    try {
      jp.proceed();
    }
    catch (AssertionError e) {
      if(LazyFailRunner.isActive())
        LazyFailRunner.addThrowable(e);
      else 
        throw e;
    }
  }
  
}