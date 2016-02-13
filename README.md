# Lazy fail JUnit addon

This is an experiment to alter the default JUnit behaviour to mimic the behaviour of the [Go testing framework](https://golang.org/pkg/testing/),
i.e. failed asserts will normally not end the execution of the test case, but instead all failed asserts are reported
at the end. The project is inspired by [Dan North](http://dannorth.net/)s [jgotesting](https://gitlab.com/tastapod/jgotesting) that however
also uses the Go syntax.

## Examples

Some examples can be found in [LazyFailExampleTest](junit-lazy-fail-example/src/test/java/se/jiderhamn/lazyfail/LazyFailExampleTest.java)

## Setup

In order to use this addon, you must first add a dependency to your Maven `pom.xml`
```xml
    <dependency>
      <groupId>se.jiderhamn</groupId>
      <artifactId>junit-lazy-fail</artifactId>
      <version>0.0.1-SNAPSHOT</version>
      <scope>test</scope>
    </dependency>
```

In that same POM, you need to configure AspectJ for compiling the tests
```xml
  <build>
    <plugins>
      <!-- Compile with AspectJ compiler -->
      <plugin>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>aspectj-maven-plugin</artifactId>
        <version>1.8</version>
        <configuration>
          <aspectLibraries>
            <aspectLibrary>
              <groupId>se.jiderhamn</groupId>
              <artifactId>junit-lazy-fail</artifactId>
            </aspectLibrary>
          </aspectLibraries>
          <complianceLevel>1.8</complianceLevel>
          <source>1.8</source>
          <target>1.8</target>
        </configuration>
        <executions>
          <execution>
            <id>test-compile</id>
            <configuration>
              <skip>${maven.test.skip}</skip>
            </configuration>
            <goals>
              <goal>test-compile</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
```

Last but not least, you need to annotate your test classes with
```java
@RunWith(se.jiderhamn.lazyfail.LazyFailRunner.class)
```
