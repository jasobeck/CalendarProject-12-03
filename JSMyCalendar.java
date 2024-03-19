//Jane Sobeck
//12/2/2023
//Assignment 3: Calendar Part 3
//CS&141 2760

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.Scanner;

/*
*The purpose of this java file is to create a menu for the user to select a function.
*Functions include: viewing the calendar for the current month and day, viewing a calender for selected month/day,
* going to previous and next calendar, entering events into the calendar, and printing .
*I did not keep track of how many hours spent working on this, but it's probably about 20 hours.
*/

public class JSMyCalendar {

    // global event array
    public static String[][] eventArray = new String[12][];

    // My main method is quite bloated, and should probably be split down into a few
    // other methods
    // Main method initializes important variables for the file, imports an
    // eventFile automatically, and runs the menu
    public static void main(String[] args) throws FileNotFoundException {
        // System.out.println(new File("").toPath().toAbsolutePath().toString());
        Scanner userInput = new Scanner(System.in);
        Calendar javaCal = Calendar.getInstance();

        /*
         * The variable "size" represents the size of each cell in the calendar.
         * Cells are scaleable to any size as long as the console window can fit
         * size*7+1 characters horizontally.
         */
        int size = 10;
        int currentDay = javaCal.get(Calendar.DATE);
        int currentMonth = javaCal.get(Calendar.MONTH) + 1;
        String currentDate = (String.valueOf(currentMonth) + "/" + String.valueOf(currentDay));
        System.out.println("Thank you for using Jane's Calendar Program!");
        System.out.println();
        String dateInput;
        int monthDrawn = 0;
        int dayDrawn = 0;
        String command = "";
        initializeEventArray();
        File eventFile = new File("calendarEvents.txt");

        if (eventFile.exists()) {
            scanEventFile(eventFile);
        } else {
            System.out.println("Event calendar file not imported");
        }

        while (!command.equals("q")) {
            command = displayMenu(userInput);
            if (command.equalsIgnoreCase("e")) {
                dateInput = inputDate(userInput);
                drawCalendar(System.out, dateInput, size);
                monthDrawn = monthFromDate(dateInput);
                dayDrawn = dayFromDate(dateInput);
            } else if (command.equalsIgnoreCase("t")) {
                drawCalendar(System.out, currentDate, size);
                monthDrawn = monthFromDate(currentDate);
                dayDrawn = dayFromDate(currentDate);
            } else if (command.equalsIgnoreCase("n")) {
                if (monthDrawn == 0) {
                    System.out.println("You need to have a calendar displayed first");
                } else if (monthDrawn == 12) {
                    drawCalendar(System.out, ("1/" + String.valueOf(dayDrawn)), size);
                    monthDrawn = 1;
                } else {
                    drawCalendar(System.out, (String.valueOf(monthDrawn + 1) + "/" + String.valueOf(dayDrawn)), size);
                    monthDrawn++;
                }
            } else if (command.equalsIgnoreCase("p")) {
                if (monthDrawn == 0) {
                    System.out.println("You need to have a calendar displayed first");
                } else if (monthDrawn == 1) {
                    drawCalendar(System.out, ("12/" + String.valueOf(dayDrawn)), size);
                    monthDrawn = 12;
                } else {
                    drawCalendar(System.out, (String.valueOf(monthDrawn - 1) + "/" + String.valueOf(dayDrawn)), size);
                    monthDrawn--;
                }
            } else if (command.equalsIgnoreCase("ev")) {
                enterEvent(userInput);
            } else if (command.equalsIgnoreCase("fp")) {
                dateInput = inputDate(userInput);
                filePrint(dateInput, userInput, size);
            } else if (command.equalsIgnoreCase("q")) {
            } else {
                System.out.println("Please enter a valid command");
            }
        }
        System.out.print("See you next time!");
    }

    // Prints out a row with days on each cell based on which row it is printing
    public static int drawRow(PrintStream out, int row, int size, String date) {
        int lastDayDrawn = 0;
        int day = 1;
        int dayInt = dayFromDate(date);
        int monthInt = monthFromDate(date);
        int firstDayOfYear = 0;
        int firstDayOfMonth = getFirstDayOfMonth(monthInt, firstDayOfYear);
        int daysInMonth = getDaysInMonth(monthInt);
        int dayTracker = (day + 7 * (row - 1));
        dayTracker -= firstDayOfMonth;
        // Print top line of row
        for (int i = 0; i < size * 7; i++) {
            out.print("=");
        }

        out.println("=");

        for (int verticalSegment = 0; verticalSegment < size / 2; verticalSegment++) {
            for (int dayOfWeek = 0; dayOfWeek < 7; dayOfWeek++) {
                if (dayOfWeek == 0) {
                    out.print("|");
                }
                int whiteSpaceCount = size - 1;
                if (verticalSegment == 0) {
                    int currentDayInt = (day + 7 * (row - 1));
                    currentDayInt -= firstDayOfMonth;
                    String currentDay = Integer.toString(currentDayInt);
                    if (currentDayInt <= 0 || currentDayInt > daysInMonth) {
                        currentDay = "";
                    }
                    int dayWidth = currentDay.length();
                    whiteSpaceCount = size - dayWidth - 1;
                    out.print(currentDay);
                    if (currentDayInt == dayInt) {
                        for (int i = 0; i < (whiteSpaceCount / 2) - 1; i++) {
                            out.print(" ");
                        }
                        out.print("X");
                        if (dayWidth == 2) {
                            out.print(" ");
                        }
                        for (int i = 0; i < (whiteSpaceCount / 2); i++) {
                            out.print(" ");
                        }
                        whiteSpaceCount = 0;
                    }
                    lastDayDrawn = currentDayInt;
                    day++;
                }
                if (verticalSegment == size / 4 - 1 && dayTracker > 0 && dayTracker <= getDaysInMonth(monthInt)) {
                    if (eventPresent(monthInt, dayTracker) == true) {
                        if (getEventLengthAt(monthInt, dayTracker) > 8) {
                            out.print(getEventAt(monthInt, dayTracker).substring(0, 8));
                            out.print("-");
                            whiteSpaceCount = size - 10;
                        } else {
                            out.print(getEventAt(monthInt, dayTracker));
                            whiteSpaceCount = size - getEventLengthAt(monthInt, dayTracker);
                        }
                    }
                }
                if (verticalSegment == size / 4 && dayTracker > 0 && dayTracker <= getDaysInMonth(monthInt)) {
                    if (eventPresent(monthInt, dayTracker) == true) {
                        if (getEventLengthAt(monthInt, dayTracker) > 8) {
                            out.print(getEventAt(monthInt, dayTracker).substring(8,
                                    getEventLengthAt(monthInt, dayTracker)));
                            whiteSpaceCount = size - ((getEventLengthAt(monthInt, dayTracker)) - 7);
                        }
                    }
                }
                for (int j = 0; j < whiteSpaceCount; j++) {
                    out.print(" ");
                }
                out.print("|");
                dayTracker++;
                if (dayOfWeek == 6 && verticalSegment < (size / 2) - 1) {
                    out.println();
                }
            }
            dayTracker -= 7;
        }

        out.println();
        return lastDayDrawn;
    }

    // This method takes in an integer representing the month in a graphical
    // representation, using drawRow
    public static void drawMonth(PrintStream out, String date, int size) {
        int monthInt = monthFromDate(date);
        int firstDayOfYear = 0;

        // This for loop ensures that the Month displayed at the top of the calender is
        // centered, regardless of the size scale of each cell
        for (int i = 0; i < 4; i++) {
            if (i == 3) {
                for (int j = 0; j < size / 2; j++) {
                    out.print(" ");
                }
            } else {
                for (int j = 0; j < size; j++) {
                    out.print(" ");
                }
            }
        }

        out.println(monthInt);
        firstDayOfYear = 0;
        for (int i = 0; i < 6; i++) {
            int lastDayDrawn = drawRow(out, i + 1, size, date);
            if (lastDayDrawn >= getDaysInMonth(monthInt)) {
                break;
            }
        }
        for (int i = 0; i < size * 7; i++) {
            out.print("=");
        }
        out.println("=");
    }

    // This method takes a date as a string, and returns the month as an integer
    // value
    public static int monthFromDate(String date) {
        int slash = date.indexOf("/");
        String monthString = date.substring(0, slash);
        int monthInt = Integer.parseInt(monthString);
        return monthInt;
    }

    // This method takes a user inputed date as a string, and returns the month as
    // an integer value
    public static int dayFromDate(String date) {
        int slash = date.indexOf("/");
        int length = date.length();
        String dayString = date.substring(slash + 1, length);
        int dayInt = Integer.parseInt(dayString);
        return dayInt;
    }
    /*
     * //This method takes the date as ints, given by monthFromDate and dayFromDate,
     * and outputs them to the console so the user can see the given date
     * public static void displayDate(int month, int day) {
     * System.out.println("Month: " + month);
     * System.out.println("Day: " + day);
     * System.out.println();
     * }
     */

    // This method draws the art before each month on the calendar
    public static void drawArt(PrintStream out) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < i; j++) {
                out.print(">");
            }
            for (int k = 0; k < (17 - 2 * i); k++) {
                if (k == 8 - i) {
                    out.print("$");
                } else {
                    out.print("*");
                }
            }
            for (int l = 0; l < i; l++) {
                out.print("<");
            }
            out.println();
        }
    }

    // This method displays the menu to the user
    public static String displayMenu(Scanner userInput) {
        System.out.println("Please type a command");
        System.out.println("    \"e\" to enter a date and display the corresponding calendar");
        System.out.println("    \"t\" to get today's date and display the calendar for the current month");
        System.out.println("    \"n\" to display the next month");
        System.out.println("    \"p\" to display the previous month");
        System.out.println("    \"ev\" to enter an event into the calendar");
        System.out.println("    \"fp\" to print a calendar month to a file");
        System.out.println("    \"q\" to quit the program");
        String command = userInput.nextLine();
        return command;
    }

    // This method gets the specified date from the user
    public static String inputDate(Scanner userInput) {
        int monthInt = 0;
        int dayInt = 0;
        String dateInput = "";
        while (monthInt > 12 || dayInt > 31 || monthInt < 1 || dayInt < 1) {
            System.out.println("Please enter a valid date: (mm/dd)");
            dateInput = userInput.nextLine();
            dayInt = dayFromDate(dateInput);
            monthInt = monthFromDate(dateInput);
        }
        return dateInput;
    }

    // This method draws the calendar by calling other methods
    public static void drawCalendar(PrintStream out, String date, int size) {
        drawArt(out);
        drawMonth(out, date, size);
    }

    /*
     * This method takes the given month, and a variable representing what day of
     * the week the first day of the year is,
     * and returns the day of the week for the first day of the given month.
     */
    public static int getFirstDayOfMonth(int monthInt, int firstDayOfYear) {
        int daysInMonth = 0;

        for (int i = 1; i < monthInt; i++) {
            daysInMonth += getDaysInMonth(i);
        }
        return (daysInMonth + firstDayOfYear) % 7;
    }

    // This method returns how many days in a given argument month
    public static int getDaysInMonth(int monthInt) {
        switch (monthInt) {
            case 1:
                return 31;
            case 2:
                return 28;
            case 3:
                return 31;
            case 4:
                return 30;
            case 5:
                return 31;
            case 6:
                return 30;
            case 7:
                return 31;
            case 8:
                return 31;
            case 9:
                return 30;
            case 10:
                return 31;
            case 11:
                return 30;
            case 12:
                return 31;
            default:
                // Throws exception if month is out of necessary range
                throw new IllegalArgumentException("Month must be in range 1-12");
        }
    }

    /**
     * Initalizes the array for events, ensuring that each index has the correct
     * number of days per month
     */
    public static void initializeEventArray() {
        int[] daysInMonth = new int[12];
        for (int i = 0; i <= 11; i++) {
            daysInMonth[i] = getDaysInMonth(i + 1);
            eventArray[i] = new String[daysInMonth[i]];
        }
    }

    // Prompts the user for an event, and stores the event into the eventArray
    public static void enterEvent(Scanner userInput) {
        System.out.println("Please enter an event in \"MM/DD event_title\" format:");
        String givenEventString = userInput.nextLine();
        String[] splitInput = givenEventString.split(" ");
        String eventDate = splitInput[0];
        String eventName = splitInput[1];
        int monthInt = monthFromDate(eventDate);
        int dayInt = dayFromDate(eventDate);
        eventName = eventName.replace("_", " ");
        storeEvent(monthInt, dayInt, eventName);
    }

    /**
     * Stores the given event data in our event table.
     * 
     * @param monthInt  the number that refers to the desired month, in the range
     *                  [1-12]
     * @param dayInt    the number that refers to the desired day in the month, in
     *                  the range [1-31]
     * @param eventName the name of the event to store.
     * @return true if the event was stored in our table, false otherwise (table
     *         occupied)
     */
    public static boolean storeEvent(int monthInt, int dayInt, String eventName) {
        // Check that no event is currently being stored before we assign the value,
        // prevents us from accidentally destroying events.
        if (eventArray[monthInt - 1][dayInt - 1] == null) {
            eventArray[monthInt - 1][dayInt - 1] = eventName;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gets the event stored at the given month and day.
     * 
     * @param monthInt the number that refers to the desired month, in the range
     *                 [1-12]
     * @param dayInt   the number that refers to the desired day in the month, in
     *                 the range [1-31]
     */
    public static String getEventAt(int monthInt, int dayInt) {
        return eventArray[monthInt - 1][dayInt - 1];
    }

    // takes a month and day and returns whether or not there is an event present on
    // that date
    public static boolean eventPresent(int monthInt, int dayInt) {
        if (eventArray[monthInt - 1][dayInt - 1] == null) {
            return false;
        } else {
            return true;
        }
    }

    // Returns the length of an event string for the given date
    public static int getEventLengthAt(int monthInt, int dayInt) {
        int eventLength = getEventAt(monthInt, dayInt).length();
        return eventLength;
    }

    // Scans the eventFile automatically loaded at the start of main, and parses it
    // for data to insert to eventArray
    public static void scanEventFile(File eventFile) throws FileNotFoundException {
        Scanner fileScanner = new Scanner(eventFile);
        while (fileScanner.hasNextLine() == true) {
            String givenEventString = fileScanner.nextLine();
            String[] splitInput = givenEventString.split(" ");

            // Ensures entries in file are correctly formatted
            if (splitInput.length != 2) {
                System.out.println("Failed to parse Event Calendar file, found invalid entry");
                System.out.println(givenEventString);
                break;
            }

            String eventDate = splitInput[0];
            String eventName = splitInput[1];
            int monthInt = monthFromDate(eventDate);
            int dayInt = dayFromDate(eventDate);
            eventName = eventName.replace("_", " ");
            storeEvent(monthInt, dayInt, eventName);
        }
        fileScanner.close();
    }

    /**
     * Prompts the user for a file to output their selected calendar to, then
     * outputs the file and tells the user where the file is.
     * 
     * @param dateInput the date the user selected.
     * @param userInput the user input to parse the file name from.
     * @param size      the size of the calendar cells.
     */
    public static void filePrint(String dateInput, Scanner userInput, int size) throws FileNotFoundException {
        System.out.println("Please enter a name for the file to be printed to in \"exampleFile.txt\" format:");
        String nameOut = userInput.nextLine();
        File out = new File(nameOut);
        PrintStream outputToFile = new PrintStream(out);
        drawCalendar(outputToFile, dateInput, size);
        outputToFile.close();
        System.out.println("Wrote to file: \"" + out.getAbsolutePath().toString() + "\"");
    }
}
