package planner;

import entity.Elevator;
import entity.Floor;
import entity.Person;

/**
 * Created by Smirnov-VN on 18.04.2017.
 * Переход между состояниями
 */
public class Transition {

    public static int STOP_COST = 3;

    public static int MOVE_COST = 1;

    public static int ENTER_COST = 0;

    public static int EXIT_COST = 0;

    /**
     * Краткое описание перехода
     */
    private String name;

    /**
     * Трассировка перехода
     */
    private StringBuilder trace;

    /**
     * Стоимость
     */
    private int cost;

    /**
     * Количество перевезенных людей
     */
    private int transported;

    /**
     * Лифт, совершающий переход
     */
    private Elevator elevator;

    public Transition(String name, Elevator elevator) {
        this.name = name;
        this.trace = new StringBuilder();
        this.cost = 0;
        this.transported = 0;
        this.elevator = elevator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTrace(){
        return trace.toString();
    }

    public int getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return name + ". It costs " + cost + ". Transported: " + transported;
    }

    public void addStop() {
        cost += STOP_COST;
        trace.append(elevator)
                .append(" STOPS at ")
                .append(elevator.getPosition())
                .append(" floor\n");
    }

    public void addMove() {
        cost += MOVE_COST;
        trace.append(elevator)
                .append(" MOVED to ")
                .append(elevator.getPosition())
                .append(" floor\n");
    }

    public void addEnter(Person person) {
        name += " " + person.getName();
        cost += ENTER_COST;
        trace.append(person)
                .append(" ENTER to ")
                .append(elevator)
                .append(" at ")
                .append(elevator.getPosition())
                .append(" floor\n");
    }

    public void addExit(Person person) {
        cost += EXIT_COST;
        transported += 1;
        trace.append(person)
                .append(" EXIT ")
                .append(elevator)
                .append(" at ")
                .append(elevator.getPosition())
                .append(" floor\n");
    }

    public void addEmpty(Floor floor) {
//        transported += (1.0/(elevator.getCapacity() + 1)) * Math.min(floor.getPeople().size(), elevator.getCapacity());
        trace.append(elevator)
                .append(" goes EMPTY to ")
                .append(elevator.getPosition())
                .append(" floor with ")
                .append(floor.getPeople().size())
                .append(" floor\n");
    }
}
