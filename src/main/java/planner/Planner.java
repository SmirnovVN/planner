package planner;

import entity.Elevator;
import entity.Floor;
import entity.Person;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Smirnov-VN on 18.04.2017.
 * Planner
 */
public class Planner {

    public static List<Transition> solve(Floor[] floors, Elevator[] elevators) {
        List<Transition> plan = new ArrayList<>();
        while (!solved(floors)) {
            for (Elevator elevator: elevators) {
                if (!solved(floors)){
                    Transition transition = getTransition(floors, elevator);
                    if (transition != null) {
                        plan.add(transition);
                    }
                }
            }
        }
        return plan;
    }

    private static Transition getTransition(Floor[] floors, Elevator elevator) {
        List<VirtualTransition> transitions = getTransitions(floors, elevator);
        if (transitions.isEmpty()) {
            return null;
        }
        VirtualTransition transition = Collections.max(transitions);
        if (transition.getType().equals(TransitionType.EMPTY)) {
            return elevator.goEmpty(transition.getFloor());
        } else {
            return elevator.goWithPeople(transition.getPeople());
        }
    }

    private static List<VirtualTransition> getTransitions(Floor[] floors, Elevator elevator) {
        List<VirtualTransition> transitions = new ArrayList<>();
        for (Floor floor: floors){
            if (elevator.getPosition().compareTo(floor) == 0
                    || floor.getPeople().isEmpty()
                    || floor.isGonnaBeEmpty()) {
                continue;
            }
            VirtualTransition transition = new VirtualTransition(TransitionType.EMPTY, floor);
            transition.setHeuristic((Transition.EMPTY_COST * floor.getPeople().size() / Transition.MOVE_COST /
                                Math.abs(elevator.getPosition().getNumber() - floor.getNumber())));
            transitions.add(transition);
        }
        List<Person> copy = copyToGoUp(elevator);
        if (!copy.isEmpty()) {
            VirtualTransition transition = new VirtualTransition(TransitionType.WITH_PEOPLE, copy);
            transition.setHeuristic((double) copy.size() / Transition.MOVE_COST / floors.length);
            transitions.add(transition);
        }
        copy = copyToGoDown(elevator);
        if (!copy.isEmpty()) {
            VirtualTransition transition = new VirtualTransition(TransitionType.WITH_PEOPLE, copy);
            transition.setHeuristic((double) copy.size() / Transition.MOVE_COST / floors.length);
            transitions.add(transition);
        }
        return transitions;
    }

    private static List<Person> copyToGoUp(Elevator elevator) {
        List<Person> source = elevator.getPosition().getPeople();
        List<Person> result = new ArrayList<>();
        for (Person person: source) {
            if (person.getDestination().compareTo(elevator.getPosition()) > 0) {
                result.add(person);
            }
        }
        return result;
    }

    private static List<Person> copyToGoDown(Elevator elevator) {
        List<Person> source = elevator.getPosition().getPeople();
        List<Person> result = new ArrayList<>();
        for (Person person: source) {
            if (person.getDestination().compareTo(elevator.getPosition()) < 0) {
                result.add(person);
            }
        }
        return result;
    }

    private static boolean solved(Floor[] floors) {
        for (Floor floor : floors) {
            if (!floor.getPeople().isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
