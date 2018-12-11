## Klutter/config-typesafe

A helpful set of extensions for TypeSafe Config that present an API that can eventually be supported for wrapping simple JSON, Maps, or
other configuration systems.  Also puts the control of loading order into your application by allowing you to create a configuration loading
chain that says what you want loaded, and how.  If `ConfigFactory.load()` has too much magic, take control!

See the docs for Typesafe Config: https://github.com/typesafehub/config but note that anything related to loading configuration files
can be ignored if you want to lose the `loadConfig()` methods from Klutter/config-typesafe.

Related artifacts and modules:

* uy.kohesive.klutter:klutter-config-typesafe
* [uy.kohesive.klutter:klutter-config-typesafe-kodein](../config-typesafe-kodein) -- injecting configuration as objects via Kodein

## Configuration Loading

For default behavior similar to `ConfigFactory.load()`, can be simply:

```kotlin
val cfg = loadApplicationConfig()
```

This uses a default configuration loading chain but does not let Typesafe Config make the decisions.  In fact any part of the chain you
like or dislike, you can keep or toss out.  For example, loading from a custom chain that only loads an `application.conf` from the
classpath (ignoring `reference.conf`) and overrides that with a map containing values, which is overridden by System properties:

```kotlin
val cfg = loadConfig(SystemPropertiesConfig(), MapAsConfig(myOverrides), ApplicationConfig())
```

You can chain together any combination you desire of:

|Loader|Description|
|------|-----------|
|EnvironmentVariablesConfig|Loads environment variables as a config, and makes available for resolving variables|
|SystemPropertiesConfig|Load system propreties as a config, this will be resolved separately against environment if exists at load time|
|ReferenceConfig|Load all `reference.conf` from classpath and merge them, no variable resolution is done|
|ApplicationConfig|Load all `Application.conf` from classpath and merge, or load from location pointed to by `config.resource`, or `config.file`, or `config.url` system properties|
|ResourceConfig|Load all matching resources from classpath, optionally specify classpath, and control if it should fail when resource is missing|
|ClassResourceConfig|Load resources relative to a given class, optionally specify if it should fail when resouce is missing|
|FileConfig|Load config from a file/path, optionally specify if it should fail when resouce is missing|
|ReaderConfig|Load config from reader, it should be HOCON or JSON formatted content to be parsed|
|UrlConfig|Load config from a URL (file, http)|
|MapAsConfig|Use a Map<String, Any> as a config|
|StringAsConfig|Use a String containing the HOCON or JSON to be parsed into config|
|PropertiesAsConfig|Use a java.umimitil.Properties object as config|

And you can insert into the chain a resolution step (converting ${variables} into values previous loaded) by adding a `ResolveConfig()`
object one or more times in the chain.  A resolve always happens after the chain is loaded.  Also note, resolve does NOT automatically use
System properties or Environment variables.  You must include them in the chain.

The default `loadApplicationConfig()` mimicking `ConfigFactory.load()` chain looks like:

```kotlin
val cfg = loadConfig(SystemPropertiesConfig(),
                     ApplicationConfig(),
                     ResolveConfig(),
                     SystemPropertiesConfig(),
                     ReferenceConfig(),
                     EnvironmentVariablesConfig())
```

All loading code can be seen in [ConfigLoading.kt](https://github.com/klutter/klutter/blob/master/config-typesafe-jdk7/src/main/kotlin/uy/klutter/config/typesafe/ConfigLoading.kt)

## Config Extensions

This library provides extensions making Typesafe Config easier to use from Kotlin, and for the code to read more naturally.  For example:

```kotlin
// use + instead of `.withFallback()`
val cfg = loadConfig(MapAsConfig(myLocalConfigMap)) + existingLoadedConfig
// render to String
logger.log("Loaded config: ${cfg.render()}")

val numCores = Runtime.getRuntime().availableProcessors()
val httpCfg = cfg.nested("server.http")
val server = MyHttpServer(httpCfg.value("host").asString("0.0.0.0"),
                          httpCfg.value("port").asInt(8080),
                          httpCfg.value("workerThreads").asInt(numCores).coerceInRange(numCores..numCores*32),
                          httpCfg.value("publicDir").asPath(),
                          httpCfg.value("timeout").parseDuration(TimeUnit.SECONDS)
```

Extension methods allow the original option of throwing exception when item missing, or returning a default, or returning null.

```kotlin
cfg.value("something").asString()           // exception if missing
cfg.value("something").asString("default")  // default if missing
cfg.value("something").asStringOrNull()     // null if missing
```

For configuration items that are lists, the same choices plus an option to return empty when absent:

```kotlin
cfg.value("otherThings").asStringList()
cfg.value("otherThings").asStringList(defaultList)
cfg.value("otherThings").asStringListOrNull()
cfg.value("otherThings").asStringListOrEmpty()  // empty list if mising
```

The extensions provides an light-weight intermediary object that can be passed around that references both the configuration item but also the fact if it exists or not:

```kotlin
foo(cfg.value("important.stuff.needed"))

public fun foo(stuff: ConfiguredValue) {
   if (stuff.exists()) {
      // ...
   }

   val stuffString = stuff.asString("someDefault")
   // ...
}
```

All extensions can be seen in [TypesafeConfig_Ext.kt](https://github.com/klutter/klutter/blob/master/config-typesafe-jdk7/src/main/kotlin/uy/klutter/config/typesafe/TypesafeConfig_Ext.kt)

## Examples in the Wild

For a sligthly demented use of this library (and one of the original sources of the code), see [Solr-Undertow configuration](https://github.com/bremeld/solr-undertow/blob/master/src/main/kotlin/org/bremeld/solr/undertow/Config.kt) which loads and layers config in a specific order to keep consistent behavior with how Solr was traditionally loaded with a mix of environment variables overriding configuration files.  Whereas Typesafe Config is the reverse.  This is evident in the [load chain from Solr-Undertow](https://github.com/bremeld/solr-undertow/blob/1624d41f0b222be0f946efeeb2485601a3ba49ab/src/main/kotlin/org/bremeld/solr/undertow/Config.kt#L84-L87).  The main use of configuration starts in the [ServerConfig class](https://github.com/bremeld/solr-undertow/blob/1624d41f0b222be0f946efeeb2485601a3ba49ab/src/main/kotlin/org/bremeld/solr/undertow/Config.kt#L144) which loads configuration paths relative to the configuration file.  A bit of Klutter-core library is sprinkled throughout.

## Roadmap (random order)

* File/Path lists
* Same API of loading, fallback and ConfiguredValue over straight JSON or Maps (although waiting to see if there is really demand)
* Allow binding an object that has a 1 parameter constructor, of type Config that does not use binding, but rather loads the config directly using Typesafe Config or Klutter/config-typesafe methods.  See issue #9
