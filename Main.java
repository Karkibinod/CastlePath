import java.io.*;
import java.util.*;



public class Main
{
    static ArrayList<Room> Vertex = new ArrayList<>();
    static ArrayList<Room> Path = new ArrayList<>();
    static Room currentRoom;

    // ANSI Color Codes
    static final String RESET = "\u001B[0m";
    static final String RED = "\u001B[31m";
    static final String GREEN = "\u001B[32m";
    static final String YELLOW = "\u001B[33m";
    static final String BLUE = "\u001B[34m";
    static final String MAGENTA = "\u001B[35m";
    static String CYAN = "\u001B[36m";
    static String WHITE = "\u001B[37m";
    static String BOLD = "\u001B[1m";
    static String CORAL = "\033[38;5;204m";

    public static void main(String[] args) throws IOException 
    {
        Scanner in = new Scanner(System.in);

        // Read vertices 
        File file = new File("vertex.txt");
        Scanner infile = new Scanner(file);
        while (infile.hasNextLine()) 
        {
            String roomName = infile.nextLine();
            Vertex.add(new Room(roomName));
        }

        // Read edges 
        file = new File("edge.txt");
        infile = new Scanner(file);
        while (infile.hasNext()) 
        {
            String from = infile.next();
            String direction = infile.next();
            String to = infile.next();
            int fromIndex = findRoomIndex(from);
            int toIndex = findRoomIndex(to);
            connectRooms(fromIndex, toIndex, direction);
        }

        // Start at MainGate
        currentRoom = Vertex.get(0);

        // User interaction
        String command;
        do {
            displayCampusMap();
            System.out.println(CYAN + BOLD + "You are at: " + GREEN + currentRoom.RoomName + RESET);
            System.out.println(YELLOW + "Commands:" + RESET);
            System.out.println(CORAL + BOLD + "  N (North), S (South), E (East), W (West), F (Find Path), A (Adjacency), Q (Quit)" + RESET);
            System.out.print(MAGENTA + "Enter your command: " + RESET);
            command = in.nextLine().trim().toLowerCase();

            switch (command) {
                case "n": // North
                    move(currentRoom.North);
                    break;
                case "s": // South
                    move(currentRoom.South);
                    break;
                case "e": // East
                    move(currentRoom.East);
                    break;
                case "w": // West
                    move(currentRoom.West);
                    break;
                case "f": // Find Path
                    System.out.print(BLUE + "Enter destination: " + RESET);
                    String destination = in.nextLine();
                    Room destinationRoom = Vertex.get(findRoomIndex(destination));
                    Dijkstra(currentRoom, destinationRoom);
                    System.out.println(MAGENTA + "Shortest Path: " + RESET);
                    for (int i = 0; i < Path.size(); i++) {
                        System.out.print(GREEN + Path.get(i).RoomName + RESET);
                        if (i < Path.size() - 1) {
                            System.out.print(MAGENTA + " → " + RESET);
                        }
                    }
                    System.out.println();
                    break;
                case "a": // Adjacency List
                    printAdjacencyList();
                    break;
                case "q": // Quit
                    System.out.println(RED + BOLD + "Exiting Campus Navigation. Goodbye!" + RESET);
                    break;
                default:
                    System.out.println(RED + BOLD + "Invalid command. Please try again." + RESET);
            }
        } while (!command.equals("q"));
    }

    static void displayCampusMap() 
    {
        String[][] map = {
            {"MainGate", "Administration", "Library", "ReadingHalls"},
            {"", "Classrooms", "Labs", "Cafeteria"},
            {"", "StudyRooms", "ComputerLabs", "RecreationCenter"},
            {"", "", "SportsGrounds", "Gym"},
            {"", "", "ITHelpdesk", ""}
        };
    
        System.out.println(YELLOW + BOLD + "Campus Map:" + RESET);
        System.out.println("        " + "            " + getFormattedRoom("MainGate", 25));
        System.out.println("        " + GREEN + "         ------------------------" + RESET);
        System.out.println("        " + GREEN + "         |                      " + RESET);
        System.out.println("           " + getFormattedRoom("Administration", 10) + "-------------" + getFormattedRoom("Library", 8) +"-----------"+ getFormattedRoom("ReadingHalls", 8)) ;
        System.out.println("           " + GREEN + "         |                        |" + RESET);
        System.out.println(getFormattedRoom("StudyRooms",10) +"---------"+ getFormattedRoom("Classrooms", 15) + "      " + getFormattedRoom("Cafeteria", 15));
        System.out.println("                    " + GREEN + "     |                    |" + RESET);
        System.out.println("                       "+getFormattedRoom("Labs", 10) + "          " + getFormattedRoom("RecreationCenter", 15));
        System.out.println("                 "+GREEN + "       |                     |" + RESET);
        System.out.println(getFormattedRoom("ITHelpdesk", 10) + "---------"+ getFormattedRoom("ComputerLabs", 10) + "-------------" + getFormattedRoom("Gym", 10));
        System.out.println("                    " + GREEN + "  |" + RESET);
        System.out.println("                 "+getFormattedRoom("SportsGrounds", 15));
        System.out.println(GREEN + RESET);
    }


    static String getFormattedRoom(String roomName, int width) 
    {
        if (roomName.equals(currentRoom.RoomName)) {
            return BOLD + GREEN + String.format("%-" + width + "s", "[ " + roomName + " ]") + RESET;
        }
        return YELLOW + String.format("%-" + width + "s", roomName) + RESET;
    }

    static int findRoomIndex(String roomName) {
        for (int i = 0; i < Vertex.size(); i++) {
            if (Vertex.get(i).RoomName.equals(roomName)) return i;
        }
        System.out.println(RED + "Error: Room '" + roomName + "' not found in vertex list." + RESET);
        return-1;
    }

    static void connectRooms(int fromIndex, int toIndex, String direction) {
        if (fromIndex == -1 || toIndex == -1) {
            System.out.println(RED + "Skipping invalid edge due to missing room." + RESET);
            return;
        }

        Room from = Vertex.get(fromIndex);
        Room to = Vertex.get(toIndex);

        switch (direction.toLowerCase()) {
            case "north":
                from.North = to;
                to.South = from;
                break;
            case "south":
                from.South = to;
                to.North = from;
                break;
            case "east":
                from.East = to;
                to.West = from;
                break;
            case "west":
                from.West = to;
                to.East = from;
                break;
        }
    }

    static void move(Room nextRoom) {
        if (nextRoom != null) {
            currentRoom = nextRoom;
        } else {
            System.out.println(RED + BOLD + "You can't move in that direction!" + RESET);
        }
    }

    static void Dijkstra(Room start, Room finish) 
    {
        for (Room room : Vertex) 
        {
            room.Distance = (room == start) ? 0 : Integer.MAX_VALUE;
            room.Visited = false;
        }

        Room temp = start;
        while (!finish.Visited) 
        {
            temp.Visited = true;
            updateDistances(temp);

            int smallest = Integer.MAX_VALUE;
            Room smallestRoom = null;
            for (Room room : Vertex) 
            {
                if (!room.Visited && room.Distance < smallest) 
                {
                    smallest = room.Distance;
                    smallestRoom = room;
                }
            }
            temp = smallestRoom;
        }

        Path.clear();
        temp = finish;
        while (temp != null && temp != start) 
        {
            Path.add(0, temp);
            temp = findNextInPath(temp);
        }
        Path.add(0, start);
    }

    static void updateDistances(Room room) {
        if (room.North != null && !room.North.Visited)
            room.North.Distance = Math.min(room.North.Distance, room.Distance + 1);
        if (room.South != null && !room.South.Visited)
            room.South.Distance = Math.min(room.South.Distance, room.Distance + 1);
        if (room.East != null && !room.East.Visited)
            room.East.Distance = Math.min(room.East.Distance, room.Distance + 1);
        if (room.West != null && !room.West.Visited)
            room.West.Distance = Math.min(room.West.Distance, room.Distance + 1);
    }

    static Room findNextInPath(Room room) {
        Room next = null;
        int smallest = Integer.MAX_VALUE;

        if (room.North != null && room.North.Distance < smallest) {
            smallest = room.North.Distance;
            next = room.North;
        }
        if (room.South != null && room.South.Distance < smallest) {
            smallest = room.South.Distance;
            next = room.South;
        }
        if (room.East != null && room.East.Distance < smallest) {
            smallest = room.East.Distance;
            next = room.East;
        }
        if (room.West != null && room.West.Distance < smallest) {
            next = room.West;
        }
        return next;
    }

    static void printAdjacencyList() {
        System.out.println(CYAN + BOLD + "Adjacency List:" + RESET);
        for (Room room : Vertex) {
            System.out.print(BOLD + room.RoomName + ": " + RESET);
            if (room.North != null) System.out.print("North -> " + room.North.RoomName + ", ");
            if (room.South != null) System.out.print("South -> " + room.South.RoomName + ", ");
            if (room.East != null) System.out.print("East -> " + room.East.RoomName + ", ");
            if (room.West != null) System.out.print("West -> " + room.West.RoomName + ", ");
            System.out.println();
        }
    }
}


class Room {
    String RoomName;
    Room North, South, East, West;
    boolean Visited;
    int Distance;

    Room(String theRoomName) {
        RoomName = theRoomName;
    }
}

