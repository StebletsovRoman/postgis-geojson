package net.postgis.geojson;

import com.fasterxml.jackson.databind.module.SimpleModule;
import net.postgis.jdbc.geometry.Geometry;
import net.postgis.jdbc.geometry.GeometryCollection;
import net.postgis.jdbc.geometry.LineString;
import net.postgis.jdbc.geometry.MultiLineString;
import net.postgis.jdbc.geometry.MultiPoint;
import net.postgis.jdbc.geometry.MultiPolygon;
import net.postgis.jdbc.geometry.Point;
import net.postgis.jdbc.geometry.Polygon;
import net.postgis.geojson.deserializers.GeometryDeserializer;
import net.postgis.geojson.serializers.GeometrySerializer;

/**
 * Module for loading serializers/deserializers.
 *
 * @author Maycon Viana Bordin <mayconbordin@gmail.com>
 * @author Sebastien Deleuze
 * @author C Daniel Sanchez <cdsanchez@geosat.com.co>
 */
public class PostGISModule extends SimpleModule {
    private static final long serialVersionUID = 1L;

    public PostGISModule() {
        super("PostGISModule");

        addSerializer(Geometry.class, new GeometrySerializer());

        addDeserializer(Geometry.class, new GeometryDeserializer<Geometry>());
        addDeserializer(Point.class, new GeometryDeserializer<Point>());
        addDeserializer(Polygon.class, new GeometryDeserializer<Polygon>());
        addDeserializer(LineString.class, new GeometryDeserializer<LineString>());
        addDeserializer(MultiPolygon.class, new GeometryDeserializer<MultiPolygon>());
        addDeserializer(MultiPoint.class, new GeometryDeserializer<MultiPoint>());
        addDeserializer(MultiLineString.class, new GeometryDeserializer<MultiLineString>());
        addDeserializer(GeometryCollection.class, new GeometryDeserializer<GeometryCollection>());
    }
}
