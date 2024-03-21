package org.postgis.geojson;

import org.postgis.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.postgis.geojson.deserializers.GeometryDeserializer;
import org.postgis.geojson.serializers.GeometrySerializer;

/**
 * Module for loading serializers/deserializers.
 *
 * @author Maycon Viana Bordin <mayconbordin@gmail.com>
 * @author Sebastien Deleuze
 * @author C Daniel Sanchez <cdsanchez@geosat.com.co>
 */
public class PostGISModule extends SimpleModule {
    private static final long serialVersionUID = 1L;
    private int defaultSrid;

    public PostGISModule(int defaultSrid) {
        this();
        this.defaultSrid = defaultSrid;
    }

    public PostGISModule() {
        super("PostGISModule");

        addSerializer(Geometry.class, new GeometrySerializer());

        addDeserializer(Geometry.class, new GeometryDeserializer<Geometry>(defaultSrid));
        addDeserializer(Point.class, new GeometryDeserializer<Point>(defaultSrid));
        addDeserializer(Polygon.class, new GeometryDeserializer<Polygon>(defaultSrid));
        addDeserializer(LineString.class, new GeometryDeserializer<LineString>(defaultSrid));
        addDeserializer(MultiPolygon.class, new GeometryDeserializer<MultiPolygon>(defaultSrid));
        addDeserializer(MultiPoint.class, new GeometryDeserializer<MultiPoint>(defaultSrid));
        addDeserializer(MultiLineString.class, new GeometryDeserializer<MultiLineString>(defaultSrid));
        addDeserializer(GeometryCollection.class, new GeometryDeserializer<GeometryCollection>(defaultSrid));
    }
}
