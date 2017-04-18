package planner;

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

    VirtualTransition(TransitionType type, Floor floor) {
        this.type = type;
        this.floor = floor;
    }

    VirtualTransition(TransitionType type, List<Person> people) {
        this.type = type;
        this.people = people;
    }

    TransitionType getType() {
        return type;
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
