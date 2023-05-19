# Developing JBR API

1. [How does it work](#how-does-it-work)
2. [Development setup](#development-setup)
3. [Adding new API](#adding-new-api)
4. [Contributing your changes](#contributing-your-changes)


## How does it work

Core functionality of JBR API consists of dynamic linkage of **_interface_**
and **_target implementation_** at runtime. It does so by generating proxy
classes implementing given interfaces and delegating all calls into actual
implementation.

In most simple cases **_client_** calls *interface* method which is
translated into *target implementation* method in **_JBR_**, but it can
as well go the other way, when *JBR* calls *interface* method, which
ends up in *target implementation* code on *client* side.
Such **_mapping_** between *interface* and *implementation*, can belong to
one of 4 types:

<details>
  <summary>1. Proxy</summary>

*Proxy* is the most straightforward type and is used when *interface* is
called by *client*.
```
╭───────────────╮           ╭───────────────╮        
│     CLIENT    │           │      JBR      │              
│╭─────────────╮│   Proxy   │╭─────────────╮│               
││jetbrains.api││ ========> ││  java.base  ││                
││ [I] Foo     ││           ││ [C] Bar     ││  
│╰─────────────╯│           │╰─────────────╯│  
╰───────────────╯           ╰───────────────╯    
```
```java
// jetbrains.api
@Proxy
public interface Foo {
    void doSomething();
}
// java.base
public class Bar {
    void doSomething() {
        System.out.println("Hello Proxy!");
    }
}
// generated at runtime (proxy)
public class Baz implements Foo {
    private final Bar bar;
    @Override
    void doSomething() {
        bar.doSomething();
    }
}
// unnamed user module
void hello(Foo foo) {
    foo.doSomething(); // prints "Hello Proxy!"
}
```
</details>

<details>
  <summary>2. Service</summary>

*Service* is a singleton *proxy*, exposed via `JBR`.
```
╭───────────────╮           ╭───────────────╮        
│     CLIENT    │           │      JBR      │              
│╭─────────────╮│  Service  │╭─────────────╮│               
││jetbrains.api││ ========> ││  java.base  ││                
││ [I] Foo     ││           ││ [C] Bar     ││  
│╰─────────────╯│           │╰─────────────╯│  
╰───────────────╯           ╰───────────────╯    
```
```java
Foo foo = JBR.getFoo();
if (foo != null) foo.doSomething();
```
</details>

<details>
  <summary>3. Client proxy</summary>

*Client proxy* is the reverse version of *proxy*, when *interface* is
called by *JBR*. This type can be used for callbacks.
```
╭───────────────╮           ╭───────────────╮        
│     CLIENT    │   Client  │      JBR      │              
│╭─────────────╮│   proxy   │╭─────────────╮│               
││jetbrains.api││ <======== ││  java.base  ││                
││ [I] Foo     ││           ││ [I] Bar     ││  
│╰─────────────╯│           │╰─────────────╯│  
╰───────────────╯           ╰───────────────╯    
```
```java
// jetbrains.api
@Client
public interface Foo {
    void doSomething();
}
// java.base
public interface Bar {
    void doSomething();
}
// generated at runtime (client proxy)
public class Baz implements Bar {
    private final Foo foo;
    @Override
    void doSomething() {
        foo.doSomething();
    }
}
// unnamed user module
void hello(MyService service) {
    service.setCallback(new Foo() {
        @Override
        void doSomething() {
            System.out.println("Hello callback!");
        }
    });
}
```
</details>

<details>
  <summary>4. Dynamic two-way</summary>

*Dynamic two-way* mapping is a combination of *proxy* and *client proxy* types.
Objects with such mapping can be passed back and forth between *client* and *JBR*
with automatic dynamic conversion, so that implementation can be on either side.
```
╭───────────────╮           ╭───────────────╮        
│     CLIENT    │  Dynamic  │      JBR      │              
│╭─────────────╮│  two-way  │╭─────────────╮│               
││jetbrains.api││ <=======> ││  java.base  ││                
││ [I] Foo     ││           ││ [I] Bar     ││  
│╰─────────────╯│           │╰─────────────╯│  
╰───────────────╯           ╰───────────────╯    
```
```java
// jetbrains.api
@Client
@Proxy
public interface Foo {
    void doSomething();
}
// java.base
public interface Bar {
    void doSomething();
}
// generated at runtime (dynamic 2-way)
public class Foz implements Foo {
    private final Bar bar;
    @Override
    void doSomething() {
        bar.doSomething();
    }
}
public class Baz implements Bar {
    private final Foo foo;
    @Override
    void doSomething() {
        foo.doSomething();
    }
}
// unnamed user module
Foo hello(Foo foo) {
    Foo.doSomething(); // call into JBR
    return () -> System.out.println("Hello 2-way!"); // user's implementation
}
```
</details>

JBR API produces a multi-release jar compatible with Java 8 and newer.
Code in JBR API must conform to Java 8 with the following exceptions:

1. There is a `module-info.java` defining `jetbrains.api` module,
   it is included into Java 9+ builds.
2. `@Deprecated` annotation allows `forRemoval`
   member despite being added in Java 9.


## Development setup

1. First off you need to have JBR [cloned](https://github.com/JetBrains/JetBrainsRuntime)
   and [built](https://github.com/JetBrains/JetBrainsRuntime#configuring-the-build-environment).
   Development of JBR itself is not covered here.

2. `cd JetBrainsRuntime` and `make jbr-api` - this will initialize nested repo
   under `JetBrainsRuntime/jbr-api` and build JBR API.

   > <picture>
   >   <source media="(prefers-color-scheme: light)" srcset="https://raw.githubusercontent.com/Mqxx/GitHub-Markdown/f167aefa480e8d37e9941a25f0b40981b74a47be/blockquotes/badge/light-theme/tip.svg">
   >   <img alt="Tip" src="https://raw.githubusercontent.com/Mqxx/GitHub-Markdown/f167aefa480e8d37e9941a25f0b40981b74a47be/blockquotes/badge/dark-theme/tip.svg">
   > </picture><br>
   >
   > If you have previously built JBR API, `make jbr-api`
   > may issue a warning about outdated branch, it's advised to keep
   > your branch up-to-date with `origin/main`.

   You will see JBR API built under
   `JetBrainsRuntime/jbr-api/out/jbr-api-SNAPSHOT.jar`
   It will also be installed into your local Maven repository as
   `com.jetbrains:jbr-api:SNAPSHOT`. The easiest way to try the
   new JBR API is to add it as Maven artifact, it will be updated
   automatically every time you build it with `make jbr-api`.

   > <picture>
   >   <source media="(prefers-color-scheme: light)" srcset="https://raw.githubusercontent.com/Mqxx/GitHub-Markdown/f167aefa480e8d37e9941a25f0b40981b74a47be/blockquotes/badge/light-theme/tip.svg">
   >   <img alt="Tip" src="https://raw.githubusercontent.com/Mqxx/GitHub-Markdown/f167aefa480e8d37e9941a25f0b40981b74a47be/blockquotes/badge/dark-theme/tip.svg">
   > </picture><br>
   >
   > If needed, JBR API can be built standalone,
   > it only requires any JDK 18 or newer:
   > ```shell
   > bash tools/build.sh full /path/to/jdk
   > ```
   > There are other build types than `full`,
   > see `build.sh` for more info.

3. Create a new feature branch. You may need to update remote to your
   fork if you don't have write access to JBR API repository.
   It's easy to do via IDEA: *Git -> Manage Remotes...*


## Adding new API

Usually you start by adding a new *service*.
It is an interface in `com.jetbrains` package marked with `@Service` annotation.

> <picture>
>   <source media="(prefers-color-scheme: light)" srcset="https://raw.githubusercontent.com/Mqxx/GitHub-Markdown/f167aefa480e8d37e9941a25f0b40981b74a47be/blockquotes/badge/light-theme/example.svg">
>   <img alt="Example" src="https://raw.githubusercontent.com/Mqxx/GitHub-Markdown/f167aefa480e8d37e9941a25f0b40981b74a47be/blockquotes/badge/dark-theme/example.svg">
> </picture><br>
>
> ```java
> // JetBrainsRuntime/jbr-api/src/com/jetbrains/MyService.java
> package com.jetbrains;
> 
> @Service
> public interface MyService {
>     void print(String string);
> }
> ```

Next, you need to specify the mapping between interfaces and implementation.
This is done via adding a new entry in
`JetBrainsRuntime/src/java.base/share/classes/com/jetbrains/registry/JBRApiRegistry.java`
for the corresponding module containing the target implementation.
Class names are
[binary names](https://docs.oracle.com/javase/9/docs/api/java/lang/ClassLoader.html#name).

> <picture>
>   <source media="(prefers-color-scheme: light)" srcset="https://raw.githubusercontent.com/Mqxx/GitHub-Markdown/f167aefa480e8d37e9941a25f0b40981b74a47be/blockquotes/badge/light-theme/example.svg">
>   <img alt="Example" src="https://raw.githubusercontent.com/Mqxx/GitHub-Markdown/f167aefa480e8d37e9941a25f0b40981b74a47be/blockquotes/badge/dark-theme/example.svg">
> </picture><br>
>
> ```java
> // JetBrainsRuntime/src/java.desktop/share/classes/javax/swing/JOptionPane.java
> // ...
> private static class MyServiceImpl {
>     void print(String string) {
>         showMessageDialog(null, string);
>     }
> }
> // ...
> ```
> ```java
> // JetBrainsRuntime/src/java.base/share/classes/com/jetbrains/registry/JBRApiRegistry.java
> // ...
> JBRApi.registerModule("com.jetbrains.base.JBRApiModule") // this is for java.base
> // ...
> JBRApi.registerModule("com.jetbrains.desktop.JBRApiModule") // this is for java.desktop
> // ...
>     .service("com.jetbrains.MyService", "javax.swing.JOptionPane$MyServiceImpl")
> // ...
> ```

> <picture>
>   <source media="(prefers-color-scheme: light)" srcset="https://raw.githubusercontent.com/Mqxx/GitHub-Markdown/f167aefa480e8d37e9941a25f0b40981b74a47be/blockquotes/badge/light-theme/tip.svg">
>   <img alt="Tip" src="https://raw.githubusercontent.com/Mqxx/GitHub-Markdown/f167aefa480e8d37e9941a25f0b40981b74a47be/blockquotes/badge/dark-theme/tip.svg">
> </picture><br>
>
> Visibility modifiers don't matter here:
> target implementation can be private and in non-exported package,
> but still be discoverable by JBR API.

Interface methods can be also mapped directly to static methods inside JBR,
*service* can even not have its implementation class at all, with all methods
mapped statically.

> <picture>
>   <source media="(prefers-color-scheme: light)" srcset="https://raw.githubusercontent.com/Mqxx/GitHub-Markdown/f167aefa480e8d37e9941a25f0b40981b74a47be/blockquotes/badge/light-theme/example.svg">
>   <img alt="Example" src="https://raw.githubusercontent.com/Mqxx/GitHub-Markdown/f167aefa480e8d37e9941a25f0b40981b74a47be/blockquotes/badge/dark-theme/example.svg">
> </picture><br>
>
> ```java
> // JetBrainsRuntime/src/java.desktop/share/classes/javax/swing/JOptionPane.java
> // ...
> private static void printForJBRApi(String string) {
>     showMessageDialog(null, string);
> }
> // ...
> ```
> ```java
> // JetBrainsRuntime/src/java.base/share/classes/com/jetbrains/registry/JBRApiRegistry.java
> // ...
> JBRApi.registerModule("com.jetbrains.desktop.JBRApiModule")
> // ...
>     .service("com.jetbrains.MyService")
>         .withStatic("print", "printForJBRApi", "javax.swing.JOptionPane")
> // ...
> ```

> <picture>
>   <source media="(prefers-color-scheme: light)" srcset="https://raw.githubusercontent.com/Mqxx/GitHub-Markdown/f167aefa480e8d37e9941a25f0b40981b74a47be/blockquotes/badge/light-theme/tip.svg">
>   <img alt="Tip" src="https://raw.githubusercontent.com/Mqxx/GitHub-Markdown/f167aefa480e8d37e9941a25f0b40981b74a47be/blockquotes/badge/dark-theme/tip.svg">
> </picture><br>
>
> `.service()` and `.withStatic()` mapping methods accept variable
> number of target classes. The first one found is used when binding the
> implementation. This is useful when you need to specify different implementations
> for different platforms - just specify all of them and first found wins.

Different mapping types are registered similarly: `.proxy()`, `.clientProxy()`,
`.twoWayProxy()`, see Javadoc for more details. Each mapping type requires
corresponding annotation to be placed on types in `jetbrains.api` module, 
the following table summarizes usage of annotations with supported mapping types:

| Annotations                | Meaning                                                                                                      | Mapping                        |
|----------------------------|--------------------------------------------------------------------------------------------------------------|--------------------------------|
| `@Service`                 | Annotated type is a *service*, it gets `JBR.get<NAME>()` and `JBR.is<NAME>Supported()` methods.              | `.service()`                   |
| `@Proxy`                   | Annotated type is a *proxy*, it is implemented on *JBR* side.                                                | `.proxy()`                     |
| `@Client`                  | Annotated type is intended to be implemented by *client*. It *may* be a *client proxy*.                      | `.clientProxy()` or none       |
| `@Proxy` & <br/> `@Client` | Annotated type is a *proxy*, but can also be implemented by *client*. It *may* be a *dynamic two-way proxy*. | `.proxy()` or `.twoWayProxy()` |
| none                       | Only applicable to `final` types.                                                                            | none                           |

> <picture>
>   <source media="(prefers-color-scheme: light)" srcset="https://raw.githubusercontent.com/Mqxx/GitHub-Markdown/f167aefa480e8d37e9941a25f0b40981b74a47be/blockquotes/badge/light-theme/info.svg">
>   <img alt="Info" src="https://raw.githubusercontent.com/Mqxx/GitHub-Markdown/f167aefa480e8d37e9941a25f0b40981b74a47be/blockquotes/badge/dark-theme/info.svg">
> </picture><br>
>
> All interfaces and classes in `jetbrains.api` module *must*
> either be annotated *and* inheritable, *or* be `final`.
> These annotations are not only needed for mapping to work,
> but also indicate the intended usage of the annotated class/interface.

When objects with defined mapping are passed between *client* and *JBR*,
they are automatically converted by wrapping/unwrapping proxy objects.

> <picture>
>   <source media="(prefers-color-scheme: light)" srcset="https://raw.githubusercontent.com/Mqxx/GitHub-Markdown/f167aefa480e8d37e9941a25f0b40981b74a47be/blockquotes/badge/light-theme/example.svg">
>   <img alt="Example" src="https://raw.githubusercontent.com/Mqxx/GitHub-Markdown/f167aefa480e8d37e9941a25f0b40981b74a47be/blockquotes/badge/dark-theme/example.svg">
> </picture><br>
>
> ```java
> // jetbrains.api
> @Service
> public interface MyService {
>     Foo newFoo();
> }
> @Proxy
> public interface Foo {
>     void doSomething();
> }
> // java.base
> class Bar {
>     void doSomething() {
>         System.out.println("Hello Bar!");
>     }
>     static Bar newBar() {
>         return new Bar();
>     }
> }
> // JBRApiRegistry.java
> JBRApi.registerModule("com.jetbrains.base.JBRApiModule")
>     .service("com.jetbrains.MyService")
>         .withStatic("newFoo", "newBar", "blah.blah.Bar")
>     .proxy("com.jetbrains.Foo", "blah.blah.Bar")
> // unnamed user module
> void hello() {
>     JBR.getMyService().newFoo().doSomething(); // prints "Hello Bar!"
> }
> ```

When JBR API backend determines service availability, it also considers
all mapped types, reachable from that service, that means that failure
to find implementation for a proxy type, used (even indirectly) by a
service, will render that service unsupported.

> <picture>
>   <source media="(prefers-color-scheme: light)" srcset="https://raw.githubusercontent.com/Mqxx/GitHub-Markdown/f167aefa480e8d37e9941a25f0b40981b74a47be/blockquotes/badge/light-theme/example.svg">
>   <img alt="Example" src="https://raw.githubusercontent.com/Mqxx/GitHub-Markdown/f167aefa480e8d37e9941a25f0b40981b74a47be/blockquotes/badge/dark-theme/example.svg">
> </picture><br>
>
> If we rename `Bar#doSomething` to `Bar#doAnother` in
> previous example, JBR API backend will fail to bind `Foo` and `Bar`
> together due to missing implementation for `Foo#doSomething`.
> This will cause whole `MyService` to become unavailable, resulting
> in `JBR.getMyService()` returning `null`.

> <picture>
>   <source media="(prefers-color-scheme: light)" srcset="https://raw.githubusercontent.com/Mqxx/GitHub-Markdown/f167aefa480e8d37e9941a25f0b40981b74a47be/blockquotes/badge/light-theme/tip.svg">
>   <img alt="Tip" src="https://raw.githubusercontent.com/Mqxx/GitHub-Markdown/f167aefa480e8d37e9941a25f0b40981b74a47be/blockquotes/badge/dark-theme/tip.svg">
> </picture><br>
>
> You can troubleshoot mapping (and not only) issues by using
> `-Djetbrains.api.verbose=true` system property when running your tests.

When building JBR API via `make jbr-api` or `build.sh`, it will report
a digest of API changes with compatibility status. If build script reports
*MAJOR* status, that means that you've broken compatibility and need to
revise your API changes.


## Contributing your changes

When your new API is ready, you have built both JBR and JBR API, tested them
together and made sure you didn't break compatibility or anything else,
it's time to contribute your changes. All JBR API changes *must* go through
GitHub Pull Requests, after bot checked your changes and at least one approval
from a reviewer, they will be merged into `main` branch and will be assigned a
new version.

**After JBR API Pull Request is merged and assigned a version, you need to
update supported version inside JBR to this new version -
`SUPPORTED_VERSION` field in
`JetBrainsRuntime/src/java.base/share/classes/com/jetbrains/registry/JBRApiRegistry.java`.**

> <picture>
>   <source media="(prefers-color-scheme: light)" srcset="https://raw.githubusercontent.com/Mqxx/GitHub-Markdown/f167aefa480e8d37e9941a25f0b40981b74a47be/blockquotes/badge/light-theme/warning.svg">
>   <img alt="Warning" src="https://raw.githubusercontent.com/Mqxx/GitHub-Markdown/f167aefa480e8d37e9941a25f0b40981b74a47be/blockquotes/badge/dark-theme/warning.svg">
> </picture><br>
>
> JBR changes *must not* be pushed into stable/development branches
> until JBR API changes are merged and implementation version is updated in JBR.
> However, it's advised that you do a branch review of both JBR and JBR API
> simultaneously, providing a link to one another to give reviewer more context.
> When JBR API is merged, just update the implementation version -
> now you are ready to push JBR changes.

When newly pushed changes are assigned a version, it also results in:
1. New tag in form `v1.2.3`.
2. New GitHub release.
3. Updated [javadoc](https://jetbrains.github.io/JetBrainsApiTest).

That's it, thanks for contributing!
Just a few last words to make your life easier and JBR API better:
1. JBR API is intended to be a high-level API -
   provide only what's needed for user, don't make Lego.
2. Plan API ahead - once it's released, it still *sometimes*
   can be extended, but never amended.
3. Don't break compatibility - major changes are always long planned,
   with everything possible done to mitigate the impact of incompatible
   changes. If that's not your case - you are doing wrong.
4. When in doubts, contact me, I will try to help - **@nikita.gubarkov**.
   
