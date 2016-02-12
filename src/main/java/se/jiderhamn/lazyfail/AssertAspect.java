package se.jiderhamn.lazyfail;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * TODO Document
 * @author Mattias Jiderhamn
 */
@Aspect
public class AssertAspect {
  
  @Pointcut("call(* org.junit.Assert.*(..))")
  public void orgJunitAssert() {}
  
  @Pointcut("call(* junit.framework.Assert.*(..))")
  public void junitFrameworkAssert() {}
  
  @Around("(orgJunitAssert() || junitFrameworkAssert())")
  public void assertMethod(ProceedingJoinPoint jp) throws Throwable {
    try {
      jp.proceed();
    }
    catch (AssertionError e) {
      if(LazyFailRunner.isActive())
        LazyFailRunner.addAssertionError(e);
      else 
        throw e;
    }
  }
  
}