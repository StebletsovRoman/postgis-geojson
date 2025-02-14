package net.postgis.geojson.deserializers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.postgis.jdbc.geometry.MultiLineString;


@JsonIgnoreProperties(ignoreUnknown = true)
class InnerClass {
    @JsonProperty("id")
    private int id;

    @JsonProperty("shape")
    private MultiLineString shape;

    public InnerClass() {
        super();
    }

    public InnerClass(int id, MultiLineString shape) {
        this.id = id;
        this.shape = shape;
    }

    int id() {
        return id;
    }

    MultiLineString shape() {
        return shape;
    }

    @Override
    public String toString() {
        return "id=" + id + ", shape=" + shape;
    }
}
