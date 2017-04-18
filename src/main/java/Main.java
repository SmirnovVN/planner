import entity.Elevator;
import entity.Floor;
import entity.Person;
import planner.Planner;
import planner.Transition;

import java.util.List;

/**
 * Created by Smirnov-VN on 18.04.2017.
 * Главный класс, задаем начальное состояние
 */
public class Main {


    private static Floor[] floors;
    private static Elevator[] elevators;

    public static void main(String[] args) {
        startLevel(Main::initLevel1, true);
        startLevel(Main::initLevel2, false);
    }

    private static void initLevel1() {
        int FLOORS = 10;
        int ELEVATORS = 4;
        int CAPACITY = 1;
        Transition.MOVE_COST = 1;
        Transition.STOP_COST = 3;
        Transition.ENTER_COST = 0;
        Transition.EXIT_COST = 0;
        Transition.EMPTY_COST = 0.05;
        
        floors = initFloors(FLOORS);
        elevators = initElevators(ELEVATORS, CAPACITY);

        new Person("Steve", floors[0], floors[1]);
        new Person("John", floors[0], floors[1]);
        new Person("Ann", floors[0], floors[2]);
        new Person("Carol", floors[1], floors[2]);
        new Person("Jake",  floors[1], floors[2]);
        new Person("Frank",  floors[5], floors[6]);
        new Person("Julia",  floors[8], floors[1]);
    }    

    private static void initLevel2() {
        int FLOORS = 200;
        int ELEVATORS = 1;
        int CAPACITY = 5;
        Transition.MOVE_COST = 1;
        Transition.STOP_COST = 3;
        Transition.ENTER_COST = 0;
        Transition.EXIT_COST = 0;
        Transition.EMPTY_COST = 0.001;

        floors = initFloors(FLOORS);
        elevators = initElevators(ELEVATORS, CAPACITY);

        new Person("Steve", floors[120], floors[1]);
        new Person("John", floors[1], floors[54]);
        new Person("Ann", floors[23], floors[2]);
        new Person("Carol", floors[67], floors[145]);
        new Person("Jake",  floors[65], floors[84]);
        new Person("Frank",  floors[5], floors[175]);
        new Person("Julia",  floors[125], floors[34]);
    }

    private static void startLevel(Runnable initLevel, boolean showTrace) {
        initLevel.run();

        List<Transition> plan = Planner.solve(floors, elevators);

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>Level " + initLevel + "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");

        if (showTrace) {
            for (Transition transition : plan) {
                System.out.println(transition.getTrace());
                System.out.println("----------------------------------");
            }
        }

        for(Transition transition: plan) {
            System.out.println(transition);
        }
        int total = 0;
        for(Transition transition: plan) {
            total += transition.getCost();
        }
        System.out.println("Total cost: " + total);
    }

    private static Floor[] initFloors(int count) {
        Floor[] floors = new Floor[count];
        floors[0] = new Floor(1, null);
        for(int i=1; i<count; i++) {
            floors[i] = new Floor(i+1, floors[i-1]);
            floors[i-1].setUp(floors[i]);
        }
        return floors;
    }

    private static Elevator[] initElevators(int count, int capacity) {
        Elevator[] elevators = new Elevator[count];
        for(int i=0; i<count; i++) {
            elevators[i] = new Elevator(i+1, floors[0], capacity);
        }
        return elevators;
    }
}
