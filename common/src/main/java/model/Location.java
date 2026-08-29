package model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName="Location")
public class Location {
    private Float x; //Поле не может быть null
    private Integer y; //Поле не может быть null
    private float z;

    public Location(Float x, Integer y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Location() {
    }

    public Float getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    @Override
    public String toString() {
        return "Location{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
