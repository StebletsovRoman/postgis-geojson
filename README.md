# postgis-geojson
[![](https://jitpack.io/v/StebletsovRoman/postgis-geojson.svg)](https://jitpack.io/#StebletsovRoman/postgis-geojson)

GeoJSON Jackson Serializers and Deserializers for PostGIS Geometry objects.

Note: this is a fork from https://github.com/GeosatCO/postgis-geojson with updated dependencies

## GeoJSON Support

This library gives support for serialization/deserialization of all [Geometry Objects](https://stevage.github.io/geojson-spec/#section-3.1) defined
in the GeoJSON specification.

The relation between GeoJSON geometry objects and PostGIS objects is given below:

| GeoJSON                                                                         | PostGIS                                                                                                                  |
|---------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------|
| [ Point ]( https://stevage.github.io/geojson-spec/#section-3.1.2 )              | [ Point ]( https://javadoc.io/doc/net.postgis/postgis-geometry/2.5.1/org/postgis/Point.html )                            |
| [ MultiPoint ]( https://stevage.github.io/geojson-spec/#section-3.1.3 )         | [ MultiPoint ]( https://javadoc.io/doc/net.postgis/postgis-geometry/2.5.1/org/postgis/MultiPoint.html )                  |
| [ LineString ]( https://stevage.github.io/geojson-spec/#section-3.1.4 )         | [ LineString ]( https://javadoc.io/doc/net.postgis/postgis-geometry/2.5.1/org/postgis/LineString.html )                  |
| [ MultiLineString ]( https://stevage.github.io/geojson-spec/#section-3.1.5 )    | [ MultiLineString ](https://javadoc.io/doc/net.postgis/postgis-geometry/2.5.1/org/postgis/MultiLineString.html)          |
| [ Polygon ]( https://stevage.github.io/geojson-spec/#section-3.1.6 )            | [ Polygon ]( https://javadoc.io/doc/net.postgis/postgis-geometry/2.5.1/org/postgis/Polygon.html )                        |
| [ MultiPolygon ]( https://stevage.github.io/geojson-spec/#section-3.1.7 )       | [ MultiPolygon ]( https://javadoc.io/doc/net.postgis/postgis-geometry/2.5.1/org/postgis/MultiPolygon.html )              |
| [ GeometryCollection ]( https://stevage.github.io/geojson-spec/#section-3.1.8 ) | [ GeometryCollection ]( https://javadoc.io/doc/net.postgis/postgis-geometry/2.5.1/org/postgis/GeometryCollection.html )  |

## Installation

Add the JitPack repository to your `<repositories>` list in the `pom.xml` file:

```xml
<repository>
  <id>jitpack.io</id>
  <url>https://jitpack.io</url>
</repository>
```

Then add the dependency to your `pom.xml` file:

```xml
<dependency>
  <groupId>com.github.StebletsovRoman</groupId>
  <artifactId>postgis-geojson</artifactId>
  <version>1.7</version>
</dependency>
```

Or in a `build.sbt`:

```sbt
resolvers += "jitpack" at "https://jitpack.io"

libraryDependencies += "com.github.GeosatCO" % "postgis-geojson" % "1.6"

```


For more information on how to build the library with other tools (Gradle, Sbt, Leiningen) see the [JitPack documentation](https://jitpack.io/docs/BUILDING/).

## Usage

First you need to register the library module within the `ObjectMapper` instance you are going to use:

```java
ObjectMapper mapper = new ObjectMapper();
mapper.registerModule(new PostGISModule());
```

The you can serialize objects:

```java
String json = mapper.writeValueAsString(new Point(125.6, 10.1));
```

And deserialize them:

```java
Point point = (Point) mapper.readValue(json, Geometry.class);
```
