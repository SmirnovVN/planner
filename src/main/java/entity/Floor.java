package entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Smirnov-VN on 18.04.2017.
 * Этаж
 */
public class Floor implements Comparable<Floor>{
    /**
     * Номер
     */
    private int number;

    /**
     * Этаж выше
     */
    private Floor up;

    /**
     * Этаж ниже
     */
    private Floor down;

    /**
     * Люди на этаже
     */
    private List<Person> people;

    private boolean gonnaBeEmpty;

    public Floor(int number, Floor down) {
        this.number = number;
        this.down = down;
        this.people = new ArrayList<>();
    }

    public int getNumber() {
        return number;
    }


    Floor getUp() {
        return up;
    }

    public void setUp(Floor up) {
        this.up = up;
    }

    Floor getDown() {
        return down;
    }

    public List<Person> getPeople() {
        return people;
    }

    void addPerson(Person person) {
        people.add(person);
    }

    public boolean isGonnaBeEmpty() {
        return gonnaBeEmpty;
    }

    void setGonnaBeEmpty() {
        this.gonnaBeEmpty = true;
    }

    @Override
    public String toString() {
        return String.valueOf(number);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Floor floor = (Floor) o;

        return number == floor.number;
    }

    @Override
    public int hashCode() {
        return number;
    }

    @Override
    public int compareTo(Floor o) {
        return new Integer(number).compareTo(o.getNumber());
    }

    public List<Person> copyToGoUp() {
        List<Person> result = new ArrayList<>();
        for (Person person: people) {
            if (person.getDestination().getNumber() > number) {
                result.add(person);
            }
        }
        return result;
    }

    public List<Person> copyToGoDown() {
        List<Person> result = new ArrayList<>();
        for (Person person: people) {
            if (person.getDestination().getNumber() < number) {
                result.add(person);
            }
        }
        return result;
    }
}
