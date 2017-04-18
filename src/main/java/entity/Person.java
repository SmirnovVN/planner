package entity;

/**
 * Created by Smirnov-VN on 18.04.2017.
 * Человек, который едет на лифте
 */
public class Person {

    /**
     * Имя
     */
    private String name;

    /**
     * Точка назначения
     */
    private Floor destination;


    public String getName() {
        return name;
    }

    public Floor getDestination() {
        return destination;
    }

    public Person(String name, Floor departure, Floor destination) {
        this.name = name;
        this.destination = destination;
        departure.addPerson(this);
    }

    @Override
    public String toString() {
        return name + ", who wants to " + destination + " floor";
    }
}
