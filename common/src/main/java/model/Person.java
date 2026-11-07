package model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import enums.Color;
import enums.Country;
import exception.ValidationException;

@JacksonXmlRootElement(localName="Person")
public class Person {
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Integer height; //Поле не может быть null, Значение поля должно быть больше 0
    private Color eyeColor; //Поле может быть null
    private Color hairColor; //Поле может быть null
    private Country nationality; //Поле может быть null
    private Location location; //Поле может быть null

    public Person(String name, Integer height, Color eyeColor, Color hairColor, Country nationality, Location location) {
        this.name = name;
        this.height = height;
        this.eyeColor = eyeColor;
        this.hairColor = hairColor;
        this.nationality = nationality;
        this.location = location;
    }

    public Person() {
    }

    public String getName() {
        return name;
    }

    public Integer getHeight() {
        return height;
    }

    public Color getEyeColor() {
        return eyeColor;
    }

    public Color getHairColor() {
        return hairColor;
    }

    public Country getNationality() {
        return nationality;
    }

    public Location getLocation() {
        return location;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public void setEyeColor(Color eyeColor) {
        this.eyeColor = eyeColor;
    }

    public void setHairColor(Color hairColor) {
        this.hairColor = hairColor;
    }

    public void setNationality(Country nationality) {
        this.nationality = nationality;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public static boolean validatePerson(Person person) {
        if (person == null) {
            throw new ValidationException("Person is required");
        }

        if (person.getName() == null || person.getName().isBlank()) {
            throw new ValidationException("Person name must not be blank");
        }

        if (person.getHeight() == null || person.getHeight() <= 0) {
            throw new ValidationException("Ошибка роста");
        }

        if (person.getLocation() != null) {
            Location location = person.getLocation();
            if (location.getX() == null || location.getY() == null
                    || !Float.isFinite(location.getX()) || !Float.isFinite(location.getZ())) {
                throw new ValidationException("Location coordinates must be finite and present");
            }
        }

        return true;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", height=" + height +
                ", eyeColor=" + eyeColor +
                ", hairColor=" + hairColor +
                ", nationality=" + nationality +
                ", location=" + location +
                '}';
    }
}
