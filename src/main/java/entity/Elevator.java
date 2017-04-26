package entity;

import planner.Transition;

import java.util.*;

/**
 * Created by Smirnov-VN on 18.04.2017.'
 * Лифт
 */
public class Elevator {

    /**
     * Идентификатор лифта
     */
    private int id;

    /**
     * Этаж
     */
    private Floor position;

    /**
     * Вместимость
     */
    private int capacity;

    /**
     * Заполненность
     */
    private List<Person> content;

    /**
     * Состояние
     */
    private ElevatorState state;

    /**
     * Совершаемый переход
     */
    private Transition transition;

    /**
     * Лифт будет останавливаться, чтобы подобрать пассажиров по пути
     */
    private boolean stopsForWaiting;

    public Elevator(int id, Floor position, int capacity, boolean stopsForWaiting) {
        this.id = id;
        this.position = position;
        this.capacity = capacity;
        this.stopsForWaiting = stopsForWaiting;
        this.content = new ArrayList<>();
        this.state = ElevatorState.STOP;
    }

    public Floor getPosition() {
        return position;
    }

    public int getCapacity() {
        return capacity;
    }

    @Override
    public String toString() {
        return "Elevator " + id + " with state " + state;
    }

    public Transition goEmpty(Floor floor) {
        transition = new Transition("Elevator " + id + " goes EMPTY from " + position + " to " + floor, this);
        go(floor);
        transition.addEmpty(floor);
        if (floor.getPeople().size() <= capacity) {
            floor.setGonnaBeEmpty();
        }
        state = ElevatorState.STOP;
        return transition;
    }

    public Transition goWithPeople(List<Person> people) {
        transition = new Transition(" from " + position + ". People delivered:", this);
        for (Person person : people) {
            if (capacity > content.size()) {
                content.add(person);
                position.getPeople().remove(person);
                transition.addEnter(person);
            }
        }
        go(content.get(0).getDestination());
        transition.setName("Elevator " + id + " goes " + state + transition.getName() + ". Now the elevator is on the " + position + " floor");
        state = ElevatorState.STOP;
        return transition;
    }

    private void pick() {
        Iterator<Person> i = position.getPeople().iterator();
        while (i.hasNext()) {
            Person person = i.next();
            if (capacity > content.size()) {
                if (position.getNumber() < person.getDestination().getNumber() && state.equals(ElevatorState.UP)
                        || position.getNumber() > person.getDestination().getNumber() && state.equals(ElevatorState.DOWN)) {
                    content.add(person);
                    i.remove();
                    transition.addEnter(person);
                }
            } else {
                break;
            }
        }
    }

    private void go(Floor floor) {
        do {
            if (position.getNumber() < floor.getNumber()) {
                state = ElevatorState.UP;
                while (position.getNumber() != floor.getNumber()) {
                    move(position.getUp());
                }
            } else {
                state = ElevatorState.DOWN;
                while (position.getNumber() != floor.getNumber()) {
                    move(position.getDown());
                }
            }
            floor = content.isEmpty() ? floor : content.get(0).getDestination();
        } while (!content.isEmpty());
    }

    private void move(Floor floor) {
        position = floor;
        transition.addMove();
        for (Person person : content) {
            if (person.getDestination().equals(position)
                    || (stopsForWaiting && content.size() < capacity
                        && ((state.equals(ElevatorState.DOWN) && !position.copyToGoDown().isEmpty())
                            || (state.equals(ElevatorState.UP) && !position.copyToGoUp().isEmpty())))) {
                stop();
                break;
            }
        }
    }

    private void stop() {
        transition.addStop();
        Iterator<Person> i = content.iterator();
        while (i.hasNext()) {
            Person next = i.next();
            if (next.getDestination().equals(position)) {
                i.remove();
                transition.addExit(next);
            }
        }
        pick();
    }
}
