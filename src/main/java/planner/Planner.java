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
            VirtualTransition transition = new VirtualTransition(TransitionType.EMPTY, floor, elevator);
            transition.setHeuristic((1.0/(elevator.getCapacity() + floors.length)) * Math.min(floor.getPeople().size(), elevator.getCapacity()) /
                    (Transition.MOVE_COST * Math.abs(elevator.getPosition().getNumber() - floor.getNumber()) + Transition.STOP_COST));
            transitions.add(transition);
        }
        List<Person> copy = elevator.getPosition().copyToGoUp();
        if (!copy.isEmpty()) {
            VirtualTransition transition = new VirtualTransition(TransitionType.WITH_PEOPLE, copy, elevator);
            transition.setHeuristic((double) Math.min(copy.size(), elevator.getCapacity()) /
                    (Transition.MOVE_COST * (maxDestination(copy) - elevator.getPosition().getNumber()) + Transition.STOP_COST * Math.min(copy.size(), elevator.getCapacity())));
            transitions.add(transition);
        }
        copy = elevator.getPosition().copyToGoDown();
        if (!copy.isEmpty()) {
            VirtualTransition transition = new VirtualTransition(TransitionType.WITH_PEOPLE, copy, elevator);
            transition.setHeuristic((double) Math.min(copy.size(), elevator.getCapacity()) /
                    (Transition.MOVE_COST * (elevator.getPosition().getNumber() - minDestination(copy)) + Transition.STOP_COST * Math.min(copy.size(), elevator.getCapacity())));
            transitions.add(transition);
        }
        return transitions;
    }

    private static boolean solved(Floor[] floors) {
        for (Floor floor : floors) {
            if (!floor.getPeople().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private static int minDestination(List<Person> people) {
        int result = people.get(0).getDestination().getNumber();
        for (Person person : people) {
            if (result > person.getDestination().getNumber()) {
                result = person.getDestination().getNumber();
            }
        }
        return result;
    }

    private static int maxDestination(List<Person> people) {
        int result = people.get(0).getDestination().getNumber();
        for (Person person : people) {
            if (result < person.getDestination().getNumber()) {
                result = person.getDestination().getNumber();
            }
        }
        return result;
    }
}
