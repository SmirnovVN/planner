package planner;

import entity.Elevator;
import entity.ElevatorState;
import entity.Floor;
import entity.Person;

import java.util.List;
import java.util.TreeSet;

/**
 * Created by houston on 25.05.2017.
 * A star для лифтов
 */
public class AStar {

    private static TreeSet<State> searchTree;

    public static List<Transition> solve(State initial) {
        searchTree = new TreeSet<>();
        searchTree.add(initial);
        int observed = 0;
        while (!searchTree.isEmpty()) {
            State observing = searchTree.pollFirst();
            observed++;
            if (isTarget(observing)) {
                System.out.println("Search tree size: " + searchTree.size());
                System.out.println("Observed: " + observed);
                return observing.getPath();
            } else {
                if (allBusy(observing)) {
                    for (Elevator elevator : observing.getElevators()){
                        elevator.setBusy(false);
                    }
                }
                observe(observing);
            }
        }
        throw new RuntimeException("Solution not found");
    }

    private static boolean allBusy(State observing) {
        for (Elevator elevator : observing.getElevators()) {
            if (!elevator.isBusy()) return false;
        }
        return true;
    }

    private static void observe(State observing) {
        Floor[] floors = observing.getFloors();
        Elevator[] elevators = observing.getElevators();
        List<Transition> path = observing.getPath();
        for (Elevator elevator : elevators) {
            if (elevator.getPosition().getPeople().isEmpty() && !elevator.isBusy()) {
                for (Floor floor : floors) {
                    if (elevator.getPosition().compareTo(floor) == 0
                            || floor.getPeople().isEmpty()
                            || floor.isGonnaBeEmpty()) {
                        continue;
                    }
                    VirtualTransition transition = new VirtualTransition(TransitionType.EMPTY, floor, elevator);
                    searchTree.add(new State(floors, elevators, path, transition));
                }
            } else {
                List<Person> copy = elevator.getPosition().copyToGoUp();
                if (!copy.isEmpty()) {
                    VirtualTransition transition = new VirtualTransition(TransitionType.WITH_PEOPLE, ElevatorState.UP, elevator);
                    searchTree.add(new State(floors, elevators, path, transition));
                }
                copy = elevator.getPosition().copyToGoDown();
                if (!copy.isEmpty()) {
                    VirtualTransition transition = new VirtualTransition(TransitionType.WITH_PEOPLE, ElevatorState.DOWN, elevator);
                    searchTree.add(new State(floors, elevators, path, transition));
                }
            }
        }
    }

    private static boolean isTarget(State observing) {
        for (Floor floor : observing.getFloors()) {
            if (!floor.getPeople().isEmpty()) {
                return false;
            }
        }
        return true;
    }

}
