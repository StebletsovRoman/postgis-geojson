package net.postgis.geojson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import net.postgis.jdbc.geometry.Geometry;
import net.postgis.jdbc.geometry.GeometryCollection;
import net.postgis.jdbc.geometry.LineString;
import net.postgis.jdbc.geometry.MultiLineString;
import net.postgis.jdbc.geometry.MultiPoint;
import net.postgis.jdbc.geometry.MultiPolygon;
import net.postgis.jdbc.geometry.Point;
import net.postgis.jdbc.geometry.Polygon;

import java.io.IOException;

import static net.postgis.geojson.GeometryTypes.GEOMETRY_COLLECTION;
import static net.postgis.geojson.GeometryTypes.LINE_STRING;
import static net.postgis.geojson.GeometryTypes.MULTI_LINE_STRING;
import static net.postgis.geojson.GeometryTypes.MULTI_POINT;
import static net.postgis.geojson.GeometryTypes.MULTI_POLYGON;
import static net.postgis.geojson.GeometryTypes.POINT;
import static net.postgis.geojson.GeometryTypes.POLYGON;

/**
 * Serializer for Geometry types.
 *
 * @author Maycon Viana Bordin <mayconbordin@gmail.com>
 */
public class GeometrySerializer extends StdSerializer<Geometry> {

    public GeometrySerializer() {
        super(Geometry.class);
    }

    @Override
    public void serialize(Geometry geom, JsonGenerator json, SerializerProvider provider)
        throws IOException, JsonProcessingException {
        json.writeStartObject();

        writeSridField(geom, json);

        // separated to avoid bugs
        serializeGenericGeometry(geom, json);

        json.writeEndObject();
    }

    protected void serializeGenericGeometry(Geometry geom, JsonGenerator json) throws IOException {
        if (geom instanceof Point) {
            serializePoint((Point) geom, json);
        } else if (geom instanceof Polygon) {
            serializePolygon((Polygon) geom, json);
        } else if (geom instanceof LineString) {
            serializeLineString((LineString) geom, json);
        } else if (geom instanceof MultiPolygon) {
            serializeMultiPolygon((MultiPolygon) geom, json);
        } else if (geom instanceof MultiPoint) {
            serializeMultiPoint((MultiPoint) geom, json);
        } else if (geom instanceof MultiLineString) {
            serializeMultiLineString((MultiLineString) geom, json);
        } else if (geom instanceof GeometryCollection) {
            serializeGeometryCollection((GeometryCollection) geom, json);
        }
    }

    protected void serializeGeometryCollection(GeometryCollection gc, JsonGenerator json) throws IOException {
        writeTypeField(GEOMETRY_COLLECTION, json);
        json.writeArrayFieldStart("geometries");

        for (Geometry geom : gc.getGeometries()) {
            json.writeStartObject();
            serializeGenericGeometry(geom, json);
            json.writeEndObject();
        }

        json.writeEndArray();
    }

    protected void serializeMultiLineString(MultiLineString mls, JsonGenerator json) throws IOException {
        writeTypeField(MULTI_LINE_STRING, json);
        writeStartCoordinates(json);

        for (LineString ls : mls.getLines()) {
            json.writeStartArray();
            writePoints(json, ls.getPoints());
            json.writeEndArray();
        }

        writeEndCoordinates(json);
    }

    protected void serializeMultiPoint(MultiPoint mp, JsonGenerator json) throws IOException {
        writeTypeField(MULTI_POINT, json);
        writeStartCoordinates(json);
        writePoints(json, mp.getPoints());
        writeEndCoordinates(json);
    }

    protected void serializeMultiPolygon(MultiPolygon mp, JsonGenerator json) throws IOException {
        writeTypeField(MULTI_POLYGON, json);
        writeStartCoordinates(json);

        for (Polygon polygon : mp.getPolygons()) {
            json.writeStartArray();

            for (int i = 0; i < polygon.numRings(); i++) {
                json.writeStartArray();
                writePoints(json, polygon.getRing(i).getPoints());
                json.writeEndArray();
            }

            json.writeEndArray();
        }

        writeEndCoordinates(json);
    }

    protected void serializeLineString(LineString ls, JsonGenerator json) throws IOException {
        writeTypeField(LINE_STRING, json);
        writeStartCoordinates(json);
        writePoints(json, ls.getPoints());
        writeEndCoordinates(json);
    }

    protected void serializePolygon(Polygon polygon, JsonGenerator json) throws IOException {
        writeTypeField(POLYGON, json);
        writeStartCoordinates(json);

        for (int i = 0; i < polygon.numRings(); i++) {
            json.writeStartArray();
            writePoints(json, polygon.getRing(i).getPoints());
            json.writeEndArray();
        }

        writeEndCoordinates(json);
    }

    protected void serializePoint(Point point, JsonGenerator json) throws IOException {
        writeTypeField(POINT, json);
        writeStartCoordinates(json);
        if (point.dimension > 2) {
            writeNumbers(json, point.getX(), point.getY(), point.getZ());
        } else {
            writeNumbers(json, point.getX(), point.getY());
        }

        writeEndCoordinates(json);
    }

    protected void writeTypeField(String type, JsonGenerator json) throws IOException {
        json.writeStringField("type", type);
    }

    protected void writeStartCoordinates(JsonGenerator json) throws IOException {
        json.writeArrayFieldStart("coordinates");
    }

    protected void writeEndCoordinates(JsonGenerator json) throws IOException {
        json.writeEndArray();
    }

    protected void writeNumbers(JsonGenerator json, double... numbers) throws IOException {
        for (double number : numbers) {
            json.writeNumber(number);
        }
    }

    protected void writePoints(JsonGenerator json, Point[] points) throws IOException {
        for (Point point : points) {
            json.writeStartArray();
            if (point.dimension > 2) {
                writeNumbers(json, point.getX(), point.getY(), point.getZ());
            } else {
                writeNumbers(json, point.getX(), point.getY());
            }
            json.writeEndArray();
        }
    }

    protected void writeSridField(Geometry geom, JsonGenerator json) throws IOException {
        int srid = geom.getSrid();
        if (srid > 0) {
            json.writeObjectFieldStart("crs");
            json.writeStringField("type", "name");
            json.writeObjectFieldStart("properties");
            json.writeStringField("name", "EPSG:" + srid);
            json.writeEndObject();
            json.writeEndObject();
        } else {
            System.out.println(
                "[GeometrySerializer] Warning: No SRID in this geometry: "
                    + geom.toString().substring(0, 10)
                    + "..."
            );
        }
        // "crs":{"type":"name","properties":{"name":"EPSG:4326"}}
    }
}
