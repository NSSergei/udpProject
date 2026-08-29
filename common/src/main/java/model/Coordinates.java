package model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import exception.ValidationException;

@JacksonXmlRootElement(localName="Coordinates")
public class Coordinates {
    private Integer x; //Значение поля должно быть больше -176, Поле не может быть null
    private Double y; //Максимальное значение поля: 743, Поле не может быть null

    public Coordinates(Integer x, Double y) {
        this.x = x;
        this.y = y;
    }

    public Coordinates() {

    }

    public Integer getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public static boolean validateCoordinate(Integer x, Double y) {
        if (x == null || x <= -176) {
            throw new ValidationException("Ось X не может быть меньше -176");
        }

        if (y == null || y > 743) {
            throw new ValidationException("Ось Y не может быть больше 743");

        }

        return true;
    }

    @Override
    public String toString() {
        return "Coordinates{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
