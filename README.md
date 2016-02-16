# Spring MVC support for java.util.Optional

Java introduced [java.util.Optional](https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html) in Java 8. The introduction of an option type may be new to some Java developers but the idea has been around in languages like [Scala](http://scala-lang.org) and [Haskell](https://www.haskell.org) for a while now. Optional works a container object that forces the consumer to handle the presence or absence of the contained value. _Optionals_ can hopefully relegate _NullPointerExceptions_ to a thing of the past.

Spring MVC added support for Optional in [4.1](https://spring.io/blog/2014/09/04/spring-framework-4-1-ga-is-here). This enabled request parameters, request headers, matrix variables, and path variables to be bound to an _Optional_. The following examples will demonstrate how to use _Optional_ with request parameters.

## Bind an Optional Request Parameter

### Annotated Controller
The following example is an annotated controller that listens for a _'/hello'_ request. The controller defines the _name_ request parameter, so if '_Foo_' is supplied as the _name_ request parameter, then the controller will return 'Hello Foo!'. The _name_ request parameter is also defined as not required, so if the _name_ request parameter is absent the controller will return 'Hello null!'.

```java
@Controller
public class HelloController {
    @RequestMapping(value = "/hello", produces = "text/plain")
    @ResponseBody
    private String hello(@RequestParam(required = false) final String) {
      return String.format("Hello %s!", name);
    }
}
```

### Annotated Controller using Optionals

The following example updates the previous example to use java.util.Optional for the optional _name_ request parameter. The _name_ parameter is defined as an _Optional_ type. The _Optional_ forces the consumer to handle the case when the _name_ request parameter may be absent. In this case, a missing _name_ will result in _'Hello World!'_.

```java
@Controller
public class HelloController {
    @RequestMapping(value = "/hello", produces = "text/plain")
    @ResponseBody
    private String hello(@RequestParam(required = false, value = "name") final Optional<String> name) {
        final String helloName = name.orElse("World")
        return String.format("Hello %s!", helloName);
    }
}

```

### Testing Strategy

The following class will build a simple web context to support testing the controller endpoint using a [MockMvc](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/test/web/servlet/MockMvc.html). The test class uses the _@EnableWebMvc_ annotation to load the _DefaultFormattingConversionService_. With out the _DefaultFormattingConversionService_, Spring will not be able to use the _ObjectToOptionalConverter_ to convert a request parameter into an _Optional_.

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@WebAppConfiguration
public class HelloControllerTest {

    @Autowired
    WebApplicationContext wac;

    private MockMvc mockMvc;

    @Configuration
    @EnableWebMvc // Loads the DefaultFormattingConversionService to support binding to Optionals (ObjectToOptionalConverter)
    @ComponentScan(basePackages = "com.rseanking")
    public static class HelloControllerConfiguration {
    }

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    ...
}
```

For those still uses XML to configure Spring, the following will enable the _ObjectToOptionalConverter_.

The configuration below use the ```<mvc:annotation-driven/>```.  It registers the _DefaultAnnotationHandlerMapping_ and _AnnotationMethodHandlerAdapter_ beans that are required for Spring MVC to dispatch requests to annotated controllers. By default, the _FormattingConversionServiceFactoryBean_ will be used as the default conversion service. The _FormattingConversionServiceFactoryBean_ uses the _DefaultFormattingConversionService_ to load the _ObjectToOptionalConverter_.

Oh, the magic of Spring! How can one element be so complex‽

```xml
<beans>
  <mvc:annotation-driven/>
  ...
</beans>
```

### Handle Request without Request Parameter
The following test sends a _GET_ request to the _/hello_ URL without a _name_ request parameter and verifies the endpoint returns _'Hello World!'_.

```java
@Test
public void shouldHandleRequetWithoutName() throws Exception {
    mockMvc.perform(get("/hello"))
        .andExpect(status().isOk())
        .andExpect(content().string("Hello World!"));
}
```

### Handle Request with Request Parameter
The following test sends a _GET_ request to the _/hello_ URL with a _name_ request parameter of _'Foo'_ and verifies the endpoint returns _'Hello Foo!'_.

```java
@Test
public void shouldHandleRequetWithName() throws Exception {
    mockMvc.perform(get("/hello").param("name", "Foo"))
        .andExpect(status().isOk())
        .andExpect(content().string("Hello Foo!"));
}
```

## Conclusion

Java's implementation of an optional container isn't the best implementation, but its start in the right direction. While the API was developed for returned types (almost named OptionalReturn), it is good that Spring provides support for _Optional_. Using _Optional_ will force developers to handle the absence of a request parameter, request header, matrix variable, or path variable, instead of experiencing an unexpected behaviors.
