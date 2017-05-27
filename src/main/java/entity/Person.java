package entity;

import java.io.Serializable;

/**
 * Created by Smirnov-VN on 18.04.2017.
 * Человек, который едет на лифте
 */
public class Person implements Serializable, Comparable<Person> {

    /**
     * Имя
     */
    private String name;

    /**
     * Точка отправления
     */
    private transient Floor departure;

    /**
     * Точка назначения
     */
    private Floor destination;

    /**
     * Этот человек не может подсесть в лифт, который везет пассажира, имеющего самый длинный путь
     */
    private boolean marked;

    public String getName() {
        return name;
    }

    public Floor getDeparture() {
        return departure;
    }

    public Floor getDestination() {
        return destination;
    }

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    public Person(String name, Floor departure, Floor destination) {
        this.name = name;
        this.destination = destination;
        departure.addPerson(this);
        this.departure = departure;
    }

    @Override
    public String toString() {
        return name + ", who wants to " + destination + " floor";
    }

    @Override
    public int compareTo(Person o) {
        return name.compareTo(o.name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        return name.equals(person.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
