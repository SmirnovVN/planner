package planner;

import entity.Elevator;
import entity.Floor;
import entity.Person;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by houston on 24.05.2017.
 * Состояние системы
 */
public class State implements Serializable, Comparable<State>{


    private Floor[] floors;

    private Elevator[] elevators;

    private List<Transition> path;

    private int cost = 0;

    private int heuristic;

    public State(Floor[] floors, Elevator[] elevators, List<Transition> path, VirtualTransition futureTransition) {
        this.floors = Floor.cloneFloors(floors);
        this.elevators = cloneElevators(elevators);
        this.path = new ArrayList<>(path);
        transit(futureTransition);
    }

    private int getF() {
        return heuristic+cost;
    }

    public Floor[] getFloors() {
        return floors;
    }

    public Elevator[] getElevators() {
        return elevators;
    }

    List<Transition> getPath() {
        return path;
    }

    private void transit(VirtualTransition transition) {
        if (transition == null) return;
        if (transition.getType().equals(TransitionType.EMPTY)) {
            path.add(elevators[transition.getElevator().getId()-1].goEmpty(floors[transition.getFloor().getNumber() -1]));
        } else {
            path.add(elevators[transition.getElevator().getId()-1].goWithPeople(transition.getDirection()));
        }
        elevators[transition.getElevator().getId()-1].setBusy(true);
        update();
    }

    private void update() {
        cost = 0;
        for (Transition transition : path) {
            cost += transition.getCost();
        }
        heuristic = 0;
        List<Person> up = new ArrayList<>();
        for (Floor floor : floors) {
            up.addAll(floor.copyToGoUp());
        }
        List<Person> down = new ArrayList<>();
        for (Floor floor : floors) {
            down.addAll(floor.copyToGoDown());
        }
        while (!up.isEmpty() || !down.isEmpty()) {
            for (Elevator elevator : elevators) {
                if (up.isEmpty()&&down.isEmpty()) break;
                int range = 0;
                int target = 0;
                for (Person person : up) {
                    int r = person.getDestination().getNumber() - person.getDeparture().getNumber();
                    if (r > range) {
                        range = r;
                        target = 0;
                        for (Elevator elevator1: elevators) {
                            int tmp = Math.abs(person.getDeparture().getNumber() - elevator1.getPosition().getNumber());
                            if (tmp < target) {
                                target = tmp;
                            }
                        }
                    }
                }
                int upH = Transition.MOVE_COST * (range + target) + Transition.STOP_COST;
                range = 0;
                target = 0;
                for (Person person : down) {
                    int r = person.getDeparture().getNumber() - person.getDestination().getNumber();
                    if (r > range) {
                        range = r;
                        target = 0;
                        for (Elevator elevator1: elevators) {
                            int tmp = Math.abs(person.getDeparture().getNumber() - elevator1.getPosition().getNumber());
                            if (tmp < target) {
                                target = tmp;
                            }
                        }
                    }
                }
                int downH = Transition.MOVE_COST * (range + target) + Transition.STOP_COST;
                Person max = null;
                double content = 0;
                if (upH > downH) {
                    heuristic += upH;
                    while (content < elevator.getCapacity()) {
                        if (!up.isEmpty()&&!isMarked(up)) {
                            Person first = up.get(0);
                            for (Person person : up) {
                                if (!person.isMarked() && person.getDestination().getNumber() - person.getDeparture().getNumber()
                                        > first.getDestination().getNumber() - first.getDeparture().getNumber()) {
                                    first = person;
                                }
                            }
                            if (max==null){
                                max = first;
                            }
                            if (max.getDestination().getNumber()>=first.getDeparture().getNumber()) {
                                up.remove(first);
                                content += (double) (first.getDestination().getNumber() - first.getDeparture().getNumber())
                                        / (max.getDestination().getNumber() - max.getDeparture().getNumber());
                                heuristic += Transition.MOVE_COST * (first.getDestination().getNumber() - max.getDestination().getNumber()) +Transition.STOP_COST;
                            } else {
                                first.setMarked(true);
                            }
                        } else {
                            break;
                        }
                    }
                } else {
                    heuristic += downH;
                    while (content < elevator.getCapacity()) {
                        if (!down.isEmpty()&&!isMarked(down)) {
                            Person first = down.get(0);
                            for (Person person : down) {
                                if (!person.isMarked() && person.getDeparture().getNumber() - person.getDestination().getNumber()
                                        > first.getDeparture().getNumber() - first.getDestination().getNumber()) {
                                    first = person;
                                }
                            }
                            if (max==null){
                                max = first;
                            }
                            if (max.getDestination().getNumber()<=first.getDeparture().getNumber()) {
                                down.remove(first);
                                content += (double) (first.getDeparture().getNumber() - first.getDestination().getNumber())
                                        / (max.getDeparture().getNumber()-  max.getDestination().getNumber());
                                heuristic += Transition.MOVE_COST * (max.getDestination().getNumber() - first.getDestination().getNumber()) +Transition.STOP_COST;
                            } else {
                                first.setMarked(true);
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
        }
    }

    private boolean isMarked(List<Person> people) {
        for (Person person : people) {
            if (!person.isMarked()) return false;
        }
        for (Person person : people) {
            person.setMarked(false);
        }
        return true;
    }

    private Elevator[] cloneElevators(Elevator[] elevators) {
        Elevator[] clone = new Elevator[elevators.length];
            for (int i = 0; i < elevators.length; i++) {
                clone[i] = new Elevator(elevators[i].getId(), floors[elevators[i].getPosition().getNumber() - 1],
                        elevators[i].getCapacity(), elevators[i].isStopsForWaiting(), elevators[i].isBusy());
            }
        return clone;
    }

    @Override
    public int compareTo(State o) {
        if (getF() < o.getF()) {
            return -1;
        } else if (getF()> o.getF()) {
            return 1;
        } else {
            if (heuristic < o.heuristic) {
                return -1;
            } else if (heuristic> o.heuristic) {
                return 1;
            } else {
                if (equals(o)) {
                    return 0;
                } else {
                    return 1;//random
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        State state = (State) o;

        Elevator[] e = new Elevator[elevators.length];
        System.arraycopy(elevators,0,e,0,e.length);
        Arrays.sort(e);
        Elevator[] e2 = new Elevator[elevators.length];
        System.arraycopy(state.elevators,0,e2,0,e2.length);
        Arrays.sort(e2);
        return path.size() == state.path.size() && cost == state.cost && heuristic == state.heuristic
                && Arrays.deepEquals(floors, state.floors)
                && Arrays.deepEquals(e, e2);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(floors);
        result = 31 * result + Arrays.hashCode(elevators);
        result = 31 * result + cost;
        result = 31 * result + heuristic;
        return result;
    }

    @Override
    public String toString() {
        return getF() +
                ", cost=" + cost +
                ", heuristic=" + heuristic +
                ", path=" + path.size();
    }
}
