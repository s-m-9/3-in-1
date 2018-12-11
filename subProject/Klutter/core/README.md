## klutter/core

Core extension functions that are dependency-free (mostly), things improving the JDK.

Module is available in artifacts:

* uy.kohesive.klutter:klutter-core
* uy.kohesive.klutter:klutter-core-jodatime

### Initialization extensions

When initializing members or variables, these extensions allow you to encapsulate functionality with a nice label making the code more readable:

Further Initialization:

```kotlin
val myData = SomeDataClass() initializedBy {
        it.addExtraData(123)
        it.setRateName("fast")
}
```

or

```kotlin
val myData = SomeDataClass() initializedWith {
        addExtraData(123)
        setRateName("fast")
}
```

Verification before use:

```kotlin
val server = ServerConfigLoader(configFile) verifiedBy { loader ->
        if (!loader.loggingDir.exists()) {
            throw IllegalArgumentException("Invalid logging dir")
        }
        if (!loader.publicDir.exists()) {
            throw IllegalArgumentException("Invalid public asset dir")
        }
}
```

or `verifiedWith` to operate directly on the instance as `this`.

Turning an instance into a receiver (similar to `with(instance) {}` but is `instance.with {}` and can be used in an expression:

```kotlin
val userSet = getAllUsers().with {
       addUser("Frank")
       addUser("Gillian")
    }.map { it.userId }.toSet()
```

Or when you want to execute code when something is not null, kinda the opposite of `?:` operator:

```kotlin
val byName: List<Company> = name.whenNotNull { companyService.findCompanyByName(name!!) }.whenNotNull { listOf(it) } ?: emptyList()
val byCountry: List<Company> = country.whenNotNull { companyService.findCompaniesByCountry(country!!) } ?: emptyList()
return (byName + byCountry).toSet()
```

or `withNotNull` to operate directly on the instance as `this`.

### Number Extensions

To all of the Number classes (Int, Long, Byte, Short, Double, ...)...

* minimum / maximum
* coerce

```kotlin
val threadCount = serverConfig.workerThreads.mimimum(8).maximum(64)
// or
val threadCount = serverConfig.workerThreads.coerce(8..64)
```

Note:  these have since been added to Kotlin std runtime with different names.  Keeping them for now, because the names here seem a bit more obvious.

Also available for Long and Int, `humanReadable`:

```kotlin
println(1024.humanReadble()) // 1KB
```

### String Extensions

* `fromEnd(N)` / `fromStart(N)`  - return the N number of characters at the start or end
* `exceptEnding(N)` - return all but the last N characters
* `exceptLast()` - return all but the last 1 character
* `exceptStarting(N)` - return all after the first N characters
* `exceptFirst()` - return all after the first 1 character
* `mustStartWith(prefix)` - ensures the string starts with a given prefix, if it already does, do nothing
* `mustEndWith(suffix)` - ensures the string ends with a given suffix, if it already does, do nothing
* `mustNotStartWith(prefix)` - ensures the string does not start with a given prefix by removing it if it exists
* `mustNotEndWith(suffix)` - ensures the string does not end with a given suffix by removing it if it exists
* `whenStartsWith(prefix, lambda)` - run a block of code when a string starts with a prefix, passing in the remainder of the string
* `whenStartsWith(prefixes, lamba)` - run a block of code when a string starts with any of a list of prefixes, passing in the remainder of the string with prefix removed
* `nullIfBlank()` - return null if the string is already null, empty, or only consists of white space
* `nullifEmpty()` - return null if the string is arleady null, or empty

### URI Building / Encoding / Decoding

The `URLEncoder` / `URLDecoder` pairs in the JDK are not correct for the full standard, nor take into account new JavaScript %uXXXX encodings.  Updated encoding methods for each type of item (path segment, query name/value, user info, ...) are provided.  A universal decoder is also provided.

For encoding/decoding the `UrlEncoding` singleton object contains these methods:

* `decode` - decode anything, including Javascript %uXXXX encoded chars
* `encodeFragmentString` - encoding the part of the URL after the #
* `encodeUserInfo` - encode the user auth info before the host name
* `encodePathParamName` - encode URL matrix parameter names in the path
* `encodePathParamValue` - encode URL matrix parameter values in the path
* `encodeQueryNameOrValue` - encode a query name or value, for the part after the ?
* `encodeQueryNameOrValueNoParen` - same as `encodeQueryNameOrValue` but strips parenthesis
* `encodePathSegment` - encode a path segment (each part of the path between / slashes)

A `UriBuilder` is provided that can be used to manipulate URL/URI in a safe encoded or decoded manner.  The builder can return an `ImmutableUri`, a Java `URI`, or directly to a String.
The `UriBuilder` has both properties and fluent methods.  Any setting to an encoded value immediately is reflected in the matching decoded property.  And vis versa.

`UriBuilder` top-level helper functions returning `UriBuilders`:

* buildUri(URI)
* buildUri(string)
* buildUri(URL)
* buildUri(ImmutableUri)

Some properties such as `scheme`, `host`, `port`, `userInfo` are somewhat self-evident.  Other properties that are less so:

* `encodedPath` - the path encoded, if you split('/') you have encoded path segments (encoded by `UrlEncoding.encodePathSegment`)
* `decodedPath` - a List<String> of already decoded path segments
* `encodedFragment` - the fragment in its raw form (encoded with `UrlEncoding.encodeFragmentString`)
* `decodedFragment` - the fragment decoded as one string
* `encodedQuery` - the query string after the ? still encoded.  If you split('&') you would have encoded query name=value pairs.
* `decodedQuery` - the query string parsed, decoded and available as a multimap, with multiple values possible per parameter name.
* `decodedQueryDeduped` - the query string parsed, decoded and deduped into a Map<String, String> with one value per parameter name.

Setting `encodedPath` makes `decodedPath` available automatically.  And setting a list of strings to `decodedPath`
will encode them, and concatanate them into a `encodedPath` string.  This two way encode/decode is true for all property
pairs (or in the case of a query, setting one of the three, updates the others).

Since the Fragment can be used in many ways, there are helper methods for the `encodedFragment` to be decoded other than as one string:

* `fragmentAsDecodedPath` - same as `decodedPath` but using fragment as the source
* `fragmentAsDecodedQuery` - same as `decodedQuery` by using fragment as the source
* `fragmentAsDecodedQueryDeduped` - same as `decodedQueryDeduped` by using fragment as the source

Other properties, fluent methods, hasXYZ() methods, clearXYZ() methods, and more can be seen by exploring the `UriBuilder` class.

When an instance is full built, you can `toString()`, `toURI()` or `build()` (as Immutable copy) the URI.

### Collections

* `batch` - for Sequence and Iterable, batch a sequence into a sequence of lists of max N size
* `lazyBatch` - A purely Lazy batch must have the source consumed to progress, but does not need to materialize a list per iteration (is a combination of batch+forEach)

### ReadOnly / Immutable Collections

Kotlin collections have a read and write interface such as `List` vs. `MutableList` but these do not protect against write access since a simple cast back to the underlaying type can allow writing.  Klutter adds lightweight delegating wrappers that block this write access for the collection, and any collection they return (such as iterator, listIterator, subList, etc).

* `*.asReadOnly()` - Wraps a Collection (extension functions exist for Iterator, Collection, List, ListIterator, Set, and Map) with a lightweight delegating class (low overhead) that prevents casting back to mutable type, all methods that return other collections (i.e. map.entries) are also wrapped with a protecting class
* `*.toImmutable()` - copies the collection and then wraps it with the same as `*.asReadOnly()` to prevent write access.

(based off of the answer from @miensol in this Stackoverflow answer http://stackoverflow.com/a/37936456/3679676)

### Classloading

* `ChildFirstClassloader` - a classloader that tries to load within the child classloader, before the parent.  Adding a minimal level of isolation.  Useful for dynamic loading ino a container.

### JDK 7 Extensions (in module klutter/klutter-core-jdk7 or higher JDK)

Extension functions for:

* Path.exists()
* Path.notExists()
* Path.deleteRecursively()  - Kotlin has File.deleteRecursively() but not for JDK 7 Path class

### JDK 8 Extensions (in module klutter/klutter-core-jdk8 or higher JDK)

Top-level functions for:

* utcNow() - return current time as UTC Instant
* isoDateFormat() - return formatter for ISO date in the form ``yyyy-MM-dd`T`hh:mm:ss.SSSZ``

Extension functions for:

* Temporal.toIsoString() - convert any JDK 8 Date/Time class to ISO formatted string ``yyyy-MM-dd`T`hh:mm:ss.SSSZ``

### JodaTime Extensions (in module klutter/klutter-core-jodatime)

Top-level functions for:

* utcNow() - return current time as UTC timezone Joda DateTime
* isoDateFormat() - return Joda DateTimeFormatter for ISO date in the form ``yyyy-MM-dd`T`hh:mm:ss.SSSZ``

Extension functions for:

* DateTime.toIsoString() - convert Joda DateTime class to ISO formatted string ``yyyy-MM-dd`T`hh:mm:ss.SSSZ``
