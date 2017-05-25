package planner;

import entity.Elevator;
import entity.ElevatorState;
import entity.Floor;
import entity.Person;

import java.util.List;

/**
 * Created by Smirnov-VN on 18.04.2017.
 * Виртуальный переход для расчета эвристики
 */
public class VirtualTransition implements Comparable<VirtualTransition> {

    /**
     * Тип перехода
     */
    private TransitionType type;

    /**
     * Лифт, совершающий переход
     */
    private Elevator elevator;

    /**
     * Точка назначения перехода
     */
    private Floor floor;

    /**
     * Люди, которые будут перевезены в этом переходе
     */
    private List<Person> people;

    /**
     * Значение эвристики
     */
    private double heuristic;

    private ElevatorState direction;

    ElevatorState getDirection() {
        return direction;
    }

    VirtualTransition(TransitionType type, Floor floor, Elevator elevator) {
        this.elevator = elevator;
        this.type = type;
        this.floor = floor;
    }

    VirtualTransition(TransitionType type, List<Person> people, Elevator elevator) {
        this.elevator = elevator;
        this.type = type;
        this.people = people;
    }

    VirtualTransition(TransitionType type, ElevatorState direction, Elevator elevator) {
        this.elevator = elevator;
        this.type = type;
        this.direction = direction;
    }

    TransitionType getType() {
        return type;
    }

    Elevator getElevator() {
        return elevator;
    }

    Floor getFloor() {
        return floor;
    }

    List<Person> getPeople() {
        return people;
    }

    void setHeuristic(double heuristic) {
        this.heuristic = heuristic;
    }

    public int compareTo(VirtualTransition o) {
        if (heuristic < o.heuristic) {
            return -1;
        } else if (heuristic > o.heuristic) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return String.valueOf(heuristic);
    }
}
