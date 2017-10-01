Maps [![Build Status](https://travis-ci.org/marschall/maps.svg)](https://travis-ci.org/marschall/maps) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.marschall/maps/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.marschall/maps) [![Javadocs](http://www.javadoc.io/badge/com.github.marschall/maps.svg)](http://www.javadoc.io/doc/com.github.marschall/maps)
=====

Special purpose implementations of `java.util.Map` that in the right niche use case can be much more efficient than implementations shipped with the JDK.

The implementations support serialization but this has not been optimized.

Currently includes classes:
<dl>
<dt><a href="http://static.javadoc.io/com.github.marschall/maps/1.0.0/com/github/marschall/maps/com.github.marschall.maps.ReadWriteLockMap.html">ReadWriteLockMap</a></dt>
<dd>A <code>Map</code> backed by a <code>ReadWriteLock</code>.</dd>
</dl>

All methods are below 325 byte and should therefore HotSpot should be able to inline them if they are hot.

