package com.mycompany.emergencytravel;

import java.util.*;

public class EmergencyTravel {
    // Graph class to store roads and air routes between cities
    static class Graph {
        // List to store the roads between cities
        List<List<Integer>> roads;
        // List to store the air routes between cities
        List<List<Integer>> airs;

        // Constructor to initialize roads and air routes for all cities
        Graph(int totalCities) {
            roads = new ArrayList<>();
            airs = new ArrayList<>();
            // Create an empty list for each city
            for (int i = 0; i < totalCities; i++) {
                roads.add(new ArrayList<>());
                airs.add(new ArrayList<>());
                //roads: [[], [], []]  airs: [[], [], []]
            }
        }

        // Method to add roads between cities (connected in a sequence)
        void addRoads() {
            for (int i = 0; i < roads.size() - 1; i++) {
                roads.get(i).add(i + 1);  // Connect city i to city i+1 (road travel)
                //Agar 3 cities hain: roads: [[1], [2], []]
            }
        }

        // Method to add an air route between two cities
        void addAirRoute(int city1, int city2) {
            airs.get(city1).add(city2);  // Add an air route from city1 to city2
            //like if we see in this way kay kay jis say start horaha air route us jagah par destination likh di jaigi
            //like this airs: [[2], [], []]   it is the example of addAirRoute(0, 2)
        }
    }

    // Method to find the minimum days required to reach the destination
    public static void findMinimumDays(Graph graph, int totalCities, int startCity, int destinationCity) {
        boolean[] visited = new boolean[totalCities];  // Array to check if a city has been visited
        Queue<int[]> queue = new LinkedList<>();  // Queue for BFS (Breadth-First Search) that stores city and its day like this [city, day]
        Map<Integer, Integer> parent = new HashMap<>();  // Map to keep track of the parent city
        Map<Integer, String> travelMode = new HashMap<>();  // Map to store the travel mode (road or air)
        
        queue.offer(new int[]{startCity, 1});  //Bfs, Start the journey from the starting city with day 1
        visited[startCity] = true;//Bfs
        parent.put(startCity, -1);  // Starting city has no parent
        travelMode.put(startCity, "Start ->");  // Mark the starting city
        
        while (!queue.isEmpty()) {//BFS Traversal
            int[] current = queue.poll();  // Get the current city and day
            int currentCity = current[0];
            int currentDay = current[1];

            // If we have reached the destination, print the result and stop
            if (currentCity == destinationCity) {
                System.out.println("Optimal days needed to arrive at the destination: " + currentDay);
                printRoute(parent, travelMode, startCity, destinationCity);  // Print the travel route
                return;
            }

            // Process up to 6 cities by road on the current day
            int roadCount = 0;
            int nextCity = currentCity + 1;
            while (roadCount < 6 && nextCity < totalCities) {
                if (!visited[nextCity]) {
                    visited[nextCity] = true;
                    parent.put(nextCity, currentCity);// necessary for printing final path
                    travelMode.put(nextCity, "Road");  // Mark the mode of travel as road
                    queue.offer(new int[]{nextCity, currentDay});  //To add next city and current day in queue for bfs
                    roadCount++;
                    nextCity++;
                } else {
                    break;  // Stop if a city has already been visited
                }
            }

            // Check for air routes from the current city
            for (int next : graph.airs.get(currentCity)) {
                if (!visited[next]) {
                    visited[next] = true;
                    parent.put(next, currentCity);//we will store that current city is the parent of next city   imp for printing route 
                    travelMode.put(next, "By Air");  // Mark the mode of travel as air
                        queue.offer(new int[]{next, currentDay + 1});
                }
            }

            // If 6 cities were processed by road, check if we need to move to the next day
            if (roadCount == 6 && !queue.isEmpty()) {
                int[] nextQueue = queue.peek();//peeking because it helps either we need to increment the day or not
                if (nextQueue[1] <= currentDay) {
                    queue.peek()[1]++;
                }
            }
        }
        // If the destination is not reachable, print this message
        System.out.println("Unable to reach the destination.");
    }
    // Method to print the travel route
    public static void printRoute(Map<Integer, Integer> parent, Map<Integer, String> travelMode, int startCity, int destinationCity) {
        List<String> route = new ArrayList<>();
        int currentCity = destinationCity;
        // Build the route from destination to start by following the parent cities
        while (currentCity != -1) {//backtrack karkay route print karwaingay 
            route.add(0, (currentCity + 1) + " (" + travelMode.get(currentCity) + ")");  // Add city with travel mode ,(currentCity + 1) for 1 basedfor output
            currentCity = parent.get(currentCity);  // Move to the parent city of the current city
        }
        // Print the route
        System.out.print("Travel path: ");
        for (int i = 0; i < route.size(); i++) {
            System.out.print(route.get(i));
            if (i < route.size() - 1) {
                System.out.print(" -> ");
            }
        }
        System.out.println();
    }

    // Main method to start the application and handle user input
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the Emergency Travel Planner!");
        System.out.print("Please specify the number of test cases: ");

        int testCases = 0;

        try {
            testCases = scanner.nextInt();
            if (testCases <= 0) {
                System.out.println("Error: Number of test cases must be greater than 0.");
                scanner.close();
                return;
            }
        } catch (InputMismatchException e) {
            System.out.println("Error: Invalid input for number of test cases. Please enter an integer.");
            scanner.close();
            return;
        }
        scanner.nextLine();

        // Loop through each test case
        for (int t = 1; t <= testCases; t++) {
            System.out.println("\n--- Test Case " + t + " ---");

            int totalCities = 0;
            int routesCount = 0;

            try {
                // Get number of cities and aerial routes from the user
                System.out.print("Please specify the number of cities: ");
                totalCities = scanner.nextInt();

                if (totalCities <= 0) {
                    System.out.println("Error: Number of cities must be greater than 0.");
                    continue;
                }

                System.out.print("Please specify the number of aerial routes: ");
                routesCount = scanner.nextInt();
                if (routesCount < 0) {
                    System.out.println("Error: Number of aerial routes cannot be negative");
                    continue;
                }
            }
            catch (InputMismatchException e) {
                System.out.println("Error: Invalid input for cities or routes. Please enter an integer.");
                scanner.nextLine();
                continue;
            }

            // Initialize the graph with the number of cities
            Graph graph = new Graph(totalCities);
            graph.addRoads();  // Add roads between cities
            scanner.nextLine();

            // Get aerial routes from the user
            for (int i = 0; i < routesCount; i++) {
                try {
                    System.out.print("Enter route " + (i + 1) + " (source destination): ");
                    int source = scanner.nextInt() - 1;//To making it 0-base for the code
                    int dest = scanner.nextInt() - 1;

                    if (source < 0 || source >= totalCities || dest < 0 || dest >= totalCities) {
                        System.out.println("Error: Invalid city index in route " + (i + 1) + ". City indices must be within range of (1-" + totalCities + ")");
                        scanner.nextLine();
                        i--;
                        continue;
                    }
                    graph.addAirRoute(source, dest);  // Add air route to the graph
                } catch (InputMismatchException e) {
                    System.out.println("Error: Invalid input for source or destination city. Please enter an integer.");
                    scanner.nextLine();
                    i--;
                    continue;
                }
            }

            // Get start and destination cities from the user
            int startCity = 0;
            int destCity = 0;
            try {
                System.out.print("Enter the starting city (1-based index): ");
                startCity = scanner.nextInt() - 1;

                if (startCity < 0 || startCity >= totalCities) {
                    System.out.println("Error: Invalid starting city index. City indices must be within range of (1-" + totalCities + ")");
                    continue;
                }

                System.out.print("Enter the destination city (1-based index): ");
                destCity = scanner.nextInt() - 1;
                if (destCity < 0 || destCity >= totalCities) {
                    System.out.println("Error: Invalid destination city index. City indices must be within range of (1-" + totalCities + ")");
                    continue;
                }
            }
            catch (InputMismatchException e) {
                System.out.println("Error: Invalid input for starting or destination city. Please enter an integer.");
                scanner.nextLine();
                continue;
            }
            //After setting up start city ,destination city and everything we will call this funtion
            // Call the function to find the minimum days and the route
            findMinimumDays(graph, totalCities, startCity, destCity);
        }
    }
}
