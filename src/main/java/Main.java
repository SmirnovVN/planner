import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import entity.Elevator;
import entity.Floor;
import entity.Person;
import planner.AStar;
import planner.Planner;
import planner.State;
import planner.Transition;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static entity.Floor.initFloors;

/**
 * Created by Smirnov-VN on 18.04.2017.
 * Главный класс, задаем начальное состояние
 */
public class Main {

    private static int cap = 4;

    public static void main(String[] args) {
        Transition.STOP_COST = 3;
        Transition.MOVE_COST = 1;
        Transition.ENTER_COST = 0;
        Transition.EXIT_COST = 0;
        startLevel("Direct", "test.json", true);
        startLevel("A*", "test.json", false);
        startLevel("Direct", "initial.json", false);
        startLevel("A*", "initial.json", false);
        cap = 3;
        startLevel("Direct", "initial2.json", false);
        startLevel("A*", "initial2.json", false);
    }

    private static void startLevel(String method, String fileName, boolean showTrace) {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>Find plan for " + fileName + " with " + method + "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");

        State initial = parse(fileName);

        initial.getElevators()[1].setCapacity(cap);
        List<Transition> plan;
        if ("A*".equals(method)) {
            plan = AStar.solve(initial);
        } else {
            plan = Planner.solve(initial.getFloors(), initial.getElevators());
        }

        if (showTrace) {
            System.out.println("Trace: ");
            for (Transition transition : plan) {
                System.out.println(transition.getTrace());
            }
            System.out.println();
        }

        System.out.println("Plan: ");

        for(Transition transition: plan) {
            System.out.println(transition);
        }
        int total = 0;
        for(Transition transition: plan) {
            total += transition.getCost();
        }
        System.out.println("Total cost: " + total);
        System.out.println();
    }

    private static State parse(String fileName) {
        byte[] bytes = new byte[0];
        try {
            Path path = Paths.get(fileName);
            bytes = Files.readAllBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String str = new String(bytes);

        JsonParser parser = new JsonParser();
        JsonObject mainObject = parser.parse(str).getAsJsonObject();
        final int FLOORS = mainObject.get("floors").getAsInt();
        final int ELEVATORS = mainObject.get("elevators").getAsInt();
        final int CAPACITY = mainObject.get("capacity").getAsInt();
        final boolean stopsForWaiting = mainObject.get("stopsForWaiting").getAsBoolean();

        Floor[] floors = initFloors(FLOORS);
        Elevator[] elevators = initElevators(ELEVATORS, CAPACITY, floors, stopsForWaiting);

        JsonArray people = mainObject.getAsJsonArray("people");

        for (JsonElement person : people) {
            JsonObject personObject = person.getAsJsonObject();
            new Person(personObject.get("name").getAsString(), floors[personObject.get("departure").getAsInt() - 1],
                    floors[personObject.get("destination").getAsInt() - 1]);
        }

        return new State(floors, elevators, new ArrayList<>(), null);
    }

    private static Elevator[] initElevators(int count, int capacity, Floor[] floors, boolean stopsForWaiting) {
        Elevator[] elevators = new Elevator[count];
        for(int i=0; i<count; i++) {
            elevators[i] = new Elevator(i+1, floors[0], capacity, stopsForWaiting);
        }
        return elevators;
    }
}
