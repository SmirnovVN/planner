package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Smirnov-VN on 18.04.2017.
 * Этаж
 */
public class Floor implements Comparable<Floor>, Cloneable, Serializable {

    /**
     * Номер
     */
    private int number;

    /**
     * Этаж выше
     */
    private transient Floor up;

    /**
     * Этаж ниже
     */
    private transient Floor down;

    /**
     * Люди на этаже
     */
    private List<Person> people;

    /**
     * На этот этаж уже едет лифт, который всех заберет
     */
    private boolean gonnaBeEmpty;

    private Floor(int number, Floor down) {
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

    void setGonnaBeEmpty(boolean e) {
        this.gonnaBeEmpty = e;
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

        return equalLists(people, floor.people) && number == floor.number && gonnaBeEmpty == floor.gonnaBeEmpty;
    }

    private boolean equalLists(List<Person> one, List<Person> two){
        if (one == null && two == null){
            return true;
        }

        if(one == null || two == null || one.size() != two.size()){
            return false;
        }

        one = new ArrayList<>(one);
        two = new ArrayList<>(two);

        Collections.sort(one);
        Collections.sort(two);
        return one.equals(two);
    }

    @Override
    public int hashCode() {
        int result = number;
        List<Person> copy = new ArrayList<>(people);
        Collections.sort(copy);
        result = 31 * result + copy.hashCode();
        result = 31 * result + (gonnaBeEmpty ? 1 : 0);
        return result;
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

    public static Floor[] initFloors(int count) {
        Floor[] floors = new Floor[count];
        floors[0] = new Floor(1, null);
        for(int i=1; i<count; i++) {
            floors[i] = new Floor(i+1, floors[i-1]);
            floors[i-1].setUp(floors[i]);
        }
        return floors;
    }

    public static Floor[] cloneFloors(Floor[] floors) {
        Floor[] clone = initFloors(floors.length);
        for(int i=0; i<floors.length; i++) {
            for (Person person : floors[i].getPeople()) {
                new Person(person.getName(), clone[person.getDeparture().number - 1], clone[person.getDestination().number - 1]);
            }
            clone[i].setGonnaBeEmpty(floors[i].isGonnaBeEmpty());
        }
        return clone;
    }
}
