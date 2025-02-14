package net.postgis.geojson.deserializers;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import net.postgis.jdbc.geometry.Geometry;
import net.postgis.jdbc.geometry.GeometryCollection;
import net.postgis.jdbc.geometry.LineString;
import net.postgis.jdbc.geometry.LinearRing;
import net.postgis.jdbc.geometry.MultiLineString;
import net.postgis.jdbc.geometry.MultiPoint;
import net.postgis.jdbc.geometry.MultiPolygon;
import net.postgis.jdbc.geometry.Point;
import net.postgis.jdbc.geometry.Polygon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static net.postgis.geojson.GeometryTypes.LINE_STRING;
import static net.postgis.geojson.GeometryTypes.MULTI_LINE_STRING;
import static net.postgis.geojson.GeometryTypes.MULTI_POINT;
import static net.postgis.geojson.GeometryTypes.MULTI_POLYGON;
import static net.postgis.geojson.GeometryTypes.POINT;
import static net.postgis.geojson.GeometryTypes.POLYGON;

/**
 * Deserializer for Geometry types.
 *
 * @author Maycon Viana Bordin <mayconbordin@gmail.com>
 * @author Sebastien Deleuze
 * @author C Daniel Sanchez <cdsanchez@geosat.com.co>
 */
public class GeometryDeserializer<T extends Geometry> extends StdDeserializer<T> {
    public GeometryDeserializer() {
        this(null);
    }

    GeometryDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T deserialize(JsonParser jp, DeserializationContext dc)
        throws IOException, JsonProcessingException {

        final JsonNode nodeTree = jp.readValueAsTree();

        final String type = nodeTree.get("type").asText();
        final int srid = nodeTree.has("crs")
            ? readSridFromCrs(nodeTree.get("crs"))
            : 0;

        T geom = nodeTree.has("geometries")
            ? (T) new GeometryCollection(readNodeAsGeometryArray(nodeTree.get("geometries"), jp))
            : coordinatesToGeometry(type, nodeTree.get("coordinates"), jp);

        geom.setSrid(srid);

        return geom;

    }

    @SuppressWarnings("unchecked")
    protected T coordinatesToGeometry(String type, JsonNode coordinates, JsonParser jp)
        throws JsonParseException {
        switch (type) {
            case POINT:
                return (T) readNodeAsPoint(coordinates);
            case LINE_STRING:
                return (T) readNodeAsLineString(coordinates);
            case POLYGON:
                return (T) new Polygon(readNodeAsLinearRingArray(coordinates));
            case MULTI_POINT:
                return (T) new MultiPoint(readNodeAsPointArray(coordinates));
            case MULTI_LINE_STRING:
                return (T) new MultiLineString(readNodeAsLineStringArray(coordinates));
            case MULTI_POLYGON:
                return (T) new MultiPolygon(readNodeAsPolygonArray(coordinates));
            default:
                throw new JsonParseException(jp, "\"" + type + "\" is not a valid Geometry type.",
                    jp.currentLocation());
        }
    }

    protected Geometry[] readNodeAsGeometryArray(JsonNode node, JsonParser jp)
        throws JsonParseException {
        if (!node.isArray()) {
            return null;
        }

        List<Geometry> values = new ArrayList<>();
        Iterator<JsonNode> it = node.iterator();

        while (it.hasNext()) {
            JsonNode val = it.next();
            if (val.isObject()) {
                Iterator<Map.Entry<String, JsonNode>> fields = val.fields();
                String type = null;
                JsonNode coordinates = null;

                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> e = fields.next();

                    if (e.getKey().equals("type")) {
                        type = e.getValue().asText();
                    } else if (e.getKey().equals("coordinates")) {
                        coordinates = e.getValue();
                    }
                }

                values.add(coordinatesToGeometry(type, coordinates, jp));
            }
        }

        return values.toArray(new Geometry[values.size()]);
    }

    protected LineString[] readNodeAsLineStringArray(JsonNode node) {
        if (!node.isArray()) {
            return null;
        }

        List<LineString> values = new ArrayList<>();
        Iterator<JsonNode> it = node.iterator();

        while (it.hasNext()) {
            JsonNode val = it.next();
            if (val.isArray()) {
                values.add(readNodeAsLineString(val));
            }
        }

        return values.toArray(new LineString[values.size()]);
    }

    protected LineString readNodeAsLineString(JsonNode node) {
        Point[] points = readNodeAsPointArray(node);
        return new LineString(points);
    }

    protected Polygon[] readNodeAsPolygonArray(JsonNode node) {
        if (!node.isArray()) {
            return null;
        }

        List<Polygon> values = new ArrayList<>();
        Iterator<JsonNode> it = node.iterator();

        while (it.hasNext()) {
            JsonNode val = it.next();
            if (val.isArray()) {
                values.add(new Polygon(readNodeAsLinearRingArray(val)));
            }
        }

        return values.toArray(new Polygon[values.size()]);
    }

    protected LinearRing[] readNodeAsLinearRingArray(JsonNode node) {
        if (!node.isArray()) {
            return null;
        }

        List<LinearRing> values = new ArrayList<>();
        Iterator<JsonNode> it = node.iterator();

        while (it.hasNext()) {
            JsonNode val = it.next();
            if (val.isArray()) {
                values.add(readNodeAsLinearRing(val));
            }
        }

        return values.toArray(new LinearRing[values.size()]);
    }

    protected LinearRing readNodeAsLinearRing(JsonNode node) {
        Point[] points = readNodeAsPointArray(node);
        return new LinearRing(points);
    }

    protected Point[] readNodeAsPointArray(JsonNode node) {
        if (!node.isArray()) {
            return null;
        }

        List<Point> values = new ArrayList<>();
        Iterator<JsonNode> it = node.iterator();

        while (it.hasNext()) {
            JsonNode val = it.next();
            if (val.isArray()) {
                values.add(readNodeAsPoint(val));
            }
        }

        return values.toArray(new Point[values.size()]);
    }

    protected Point readNodeAsPoint(JsonNode node) {
        if (!node.isArray()) {
            return null;
        }

        List<Double> values = new ArrayList<>();
        Iterator<JsonNode> it = node.iterator();

        while (it.hasNext()) {
            values.add(it.next().asDouble());
        }

        return values.size() > 2
            ? new Point(values.get(0), values.get(1), values.get(2))
            : new Point(values.get(0), values.get(1));
    }

    protected int readSridFromCrs(JsonNode crsNode) {
        final String nameSrid = crsNode.at("/properties/name").asText();
        return Integer.parseInt(nameSrid.split(":")[1]);
    }
}
