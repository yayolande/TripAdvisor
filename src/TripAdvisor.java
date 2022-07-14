import java.util.ArrayList;
import java.time.*;
import java.time.format.DateTimeParseException;

import ui.ui;
import database.Database;
import database.Trip;
import database.Client;
import database.Ticket;



import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

enum StateApp { TRIP_LIST_MENU, TRIP_REG_MENU, CLIENT_MENU, CANCEL_CLIENT_MENU, QUIT}

public class TripAdvisor {
   public static void main (String[] args) {
      StateApp stateApp = StateApp.TRIP_LIST_MENU;
      StringBuilder userInput;


      // Client client = new Client (0, "steve", "djio", "123-46-56", "me@em.com");
      // Ticket ticket = new Ticket (0, 2, 5, 6);
      // Trip trip = new Trip (0, "yd", "dl", 4000, LocalDateTime.now(), 12);
      Client client = new Client ();
      Ticket ticket = new Ticket ();
      Trip trip = new Trip ();

      Trip tripPreference = new Trip ("yaounde", LocalDateTime.now());
      ArrayList<Object> resultDatabaseList = null;
      ArrayList<Object> arrList = null;

      int pageSize = 10;
      int[] pageInfo = {1, 1};

      stateApp = StateApp.TRIP_LIST_MENU;
      System.out.println ("\u001b[?1049h");
      System.out.println ("\u001b[?25h");

      while (true) {
         System.out.println ("\u001b[2J");
         System.out.println ("\u001b[H");

         if ( stateApp == StateApp.TRIP_LIST_MENU ) {
            client = new Client ();
            ticket = new Ticket ();
            trip = new Trip ();

            resultDatabaseList = Database.getDatabaseResultFromQuery(tripPreference);
            sortTripListByTime (resultDatabaseList);
            sortTripListByTime (resultDatabaseList);
            arrList = pagination (resultDatabaseList, pageSize, pageInfo);

            userInput = ui.displayManagerTripList (arrList, tripPreference, pageInfo);
            stateApp = makeChoiceFromInput (userInput, tripPreference, client, ticket, trip, arrList, pageInfo);
         }
         else if ( stateApp == StateApp.TRIP_REG_MENU) {
            ui.displayManagerTripReg (trip, client, ticket);
            Database.addEntryToEndDatabase (client);
            ticket.setIdClient (client.getId());
            Database.addEntryToEndDatabase (ticket);

            stateApp = StateApp.TRIP_LIST_MENU;
         }
         else if ( stateApp == StateApp.CLIENT_MENU) {
            trip.setId(ticket.getIdTtrip());

            ArrayList<Object> tripList = Database.getDatabaseResultFromQuery(trip);

            trip = (Trip) tripList.get(0);

            ui.displayManagerClient (client, ticket, trip);
            stateApp = StateApp.TRIP_LIST_MENU;
         }
         else if ( stateApp == StateApp.CANCEL_CLIENT_MENU) {
            Database.deleteEntryDatabase (ticket);
            Database.deleteEntryDatabase (client);

            stateApp = StateApp.TRIP_LIST_MENU;
         }
         else if ( stateApp == StateApp.QUIT)
            break;
      }

      System.out.println ("\u001b[?1049l");
      System.exit (0);
   }


   private static ArrayList<Object> pagination (ArrayList<Object> arrList, int pageSize, int[] pageInfo) {
      ArrayList<Object> pageList = new ArrayList<>();

      int currentPage = pageInfo[0];
      int maxPage = (int) Math.ceil (arrList.size() / (double)pageSize);

      if ( currentPage > 0 && currentPage <= maxPage ) ;
      else
         currentPage = 1;

      ;
      for (int i=0, j = (currentPage-1) * pageSize; i < pageSize ; i++ )
         if ( (i+j) < arrList.size() )
            pageList.add (arrList.get(i+j));


      pageInfo[0] = currentPage;
      pageInfo[1] = maxPage;

      return pageList;
   }

   private static void sortTripListByTime (ArrayList<Object> arrList) {
      Trip trip, otherTrip;
      int indexMin = 0;

      trip = otherTrip = null;

      for (int i=0; i < arrList.size(); i++) {
         trip = (Trip) arrList.get(i);
         LocalDateTime ldtTrip = trip.getDateTime();
         indexMin = i;

         for (int j=i+1; j < arrList.size(); j++) {
            otherTrip = (Trip) arrList.get(j);
            LocalDateTime ldtOtherTrip = otherTrip.getDateTime();

            if ( ldtOtherTrip.isBefore(ldtTrip) )
               indexMin = j;
         }

         if ( indexMin != i )
            swapElementTripList (arrList, i, indexMin);
      }
   }

   private static void swapElementTripList (ArrayList<Object> arrList, int i, int indexMin) {
      Object tmp = arrList.get(i);
      arrList.set (i, arrList.get(indexMin));
      arrList.set (indexMin, tmp);
   }

   /*
   Description:
   Read user input command, first token, to determine which command and function to call so as to modifiy the state of the application
   ** IN: StringBuilder userInput,
   ** OUT: StateApp

   How-to:
   ** userInput: is made of the command following by zero or many options. eg: date 2019-12-22 (to set the preceding date in search criteria). The command is the first token, and the options are the rest.
    */
   public static StateApp makeChoiceFromInput (StringBuilder userInput, Trip tripPreference, Client client, Ticket ticket , Trip trip, ArrayList<Object> resultDatabaseList, int[] pageInfo) {
      // Input Stuff
      // Add error handling with String strInputError
      StateApp stateApp = StateApp.TRIP_LIST_MENU;
      String str;
      int index, lastIndex;
      int[] indexArr = new int[2];

      lastIndex = 0;
      index = 0;

      // TODO : Change indexOf() and charAt() for split()
      index = userInput.indexOf (" ", lastIndex);
      if ( index < 0 )
         index = userInput.length();

      str = userInput.substring (lastIndex, index);
      lastIndex = index;

      // User search stuff
      if ( str.equals ("departure") || str.equals ("dep")) {
         str = getLocationFromInput (userInput, index, lastIndex);

         tripPreference.setDeparture (str);
      }
      else if ( str.equals ("arrival") || str.equals ("arl")) {
         str = getLocationFromInput (userInput, index, lastIndex);

         tripPreference.setArrival (str);
      }
      else if ( str.equals ("price") ) {
         boolean isError;
         char ch;
         int price;

         price = 0;
         isError = false;

         while ( userInput.charAt(index) == ' ' )
            index ++;

         for (int i = index; i < userInput.length(); i++) {
            ch = userInput.charAt (i);

            if ( ch == ' ' || ch == ',' || ch == '.' )   ;
            else if ( Character.isDigit (ch) )
               price = price * 10 + (ch - '0');
            else
               isError = true;

            if (isError)
               break;
         }

         tripPreference.setPrice (price);
      }
      else if ( str.equals ("date") ) {
         int[] dateArray = {0, 0, 0};
         int timeAdder, year, month, day;

         timeAdder = getDateTimeFromInput (userInput, index, dateArray);

         year = dateArray[0];
         month = dateArray[1];
         day = dateArray[2];

         try {
            LocalDateTime ldt ;
            if ( tripPreference.getDateTime() != null  )
               ldt = tripPreference.getDateTime();
            else
               ldt = LocalDateTime.now ();

            // If date is 'date yyyy-mm-dd', use with() to modify
            // If date is otherwise enter as 'date +n' format, do nothing
            if ( year != 0 ) {
               ldt = ldt.withYear(year);
               ldt = ldt.withMonth(month);
               ldt = ldt.withDayOfMonth(day);
            }

            tripPreference.setDateTime (ldt.plusDays (timeAdder));
         }
         catch (DateTimeException e) {
            System.out.println (e);
         }

      }
      else if ( str.equals ("time") ) {
         // "time" and "date" have similar code. As such we can mix the 2
         // and use if-else statement where it matter
         int[] dateArray = {0, 0};
         int timeAdder, hour, minute;

         timeAdder = getDateTimeFromInput (userInput, index, dateArray);

         hour = dateArray[0];
         minute = dateArray[1];

         try {
            LocalDateTime ldt ;
            if ( tripPreference.getDateTime() != null  )
               ldt = tripPreference.getDateTime();
            else
               ldt = LocalDateTime.now ();

            // If time is 'time hh:mm', use with() to modify
            // If time is otherwise enter as 'time +n' format, do nothing
            if ( hour != 0 ) {
               ldt = ldt.withHour(hour);
               ldt = ldt.withMinute(minute);
            }

            tripPreference.setDateTime (ldt.plusHours (timeAdder));
         }
         catch (DateTimeException e) {
            System.out.println (e);
         }
      }
      // Pagination stuff
      else if ( str.equals ("next") || str.equals ("n")) {
         if ( lastIndex != userInput.length() ) {
            indexArr[0] = index;
            indexArr[1] = lastIndex;
            str = getNextToken (userInput, indexArr);
            int number = 0;
            char ch = ' ';

            for (int i=0; i < str.length(); i++) {
               ch = str.charAt(i);

               if ( Character.isDigit(ch) )
                  number = number * 10 + (ch - '0');
               else
                  break;
            }

            if (number <= 0);
            else
               pageInfo[0] += number;
         }
         else
            pageInfo[0]++;
      }
      else if ( str.equals ("previous") || str.equals ("p")) {
         if ( lastIndex != userInput.length() ) {
            indexArr[0] = index;
            indexArr[1] = lastIndex;
            str = getNextToken (userInput, indexArr);
            int number = 0;
            char ch = ' ';

            for (int i=0; i < str.length(); i++) {
               ch = str.charAt(i);

               if ( Character.isDigit(ch) )
                  number = number * 10 + (ch - '0');
               else
                  break;
            }

            if (number <= 0);
            else
               pageInfo[0] -= number;
         }
         else
            pageInfo[0]--;
      }
      // State machine stuff
      else if ( str.equals ("select") || str.equals ("se")) {
         boolean isError;
         char ch;
         int tripNumber;

         tripNumber = 0;
         isError = false;

         // Retrieve tripNumber from the userInput
         while ( userInput.charAt(index) == ' ' )
            index ++;

         for (int i = index; i < userInput.length(); i++) {
            ch = userInput.charAt (i);

            // if ( ch == ' ' || ch == ',' || ch == '.' )   ;
            if ( Character.isDigit (ch) )
               tripNumber = tripNumber * 10 + (ch - '0');
            else
               isError = true;

            if (isError)
               break;
         }

         // Save the user trip selection and change stateApp
         if ( tripNumber >= 0 && tripNumber <= resultDatabaseList.size() ) {
            trip.setWholeTrip ((Trip)resultDatabaseList.get (tripNumber));
            stateApp = StateApp.TRIP_REG_MENU;
         }
         else
            trip = null;
      }
      else if ( str.equals ("client") && lastIndex != userInput.length()) {
         while ( userInput.charAt(lastIndex) == ' ' )
            lastIndex ++;

         index = userInput.indexOf (" ", lastIndex);
         if ( index < 0 )
            index = userInput.length();

         str = userInput.substring (lastIndex, index);
         lastIndex = index;

         if ( str.equals ("cancel")) {
            indexArr[0] = index;
            indexArr[1] = lastIndex;
            str = getNextToken(userInput, indexArr);

            if (getClienTicketByName (str, client, ticket))
               stateApp = StateApp.CANCEL_CLIENT_MENU;
         }
         else if ( lastIndex == userInput.length() ) {
            if (getClienTicketByName (str, client, ticket))
               stateApp = StateApp.CLIENT_MENU;
         }
      }
      else if ( str.equals ("save") ) {
         if (tripPreference.getDateTime() != null && tripPreference.getDeparture() != null && tripPreference.getArrival() != null)
            Database.addEntryToEndDatabase (tripPreference);
      }
      else if ( str.equals ("quit") || str.equals ("qt")) {
         stateApp = StateApp.QUIT;
      }

      return stateApp;
   }

   /*
      Description:
      Read the user input to extract the location (arrival or departure) and return it as String.

      How-to:
      As the tokens, words, or commands are space separated, read the next word rigth after the first one. Once the string is found, check if all the characters are letter.
      If ok, return the String, and if not throw an error.
    */
   private static String getLocationFromInput (StringBuilder userInput, int index, int lastIndex) {
      String str;

      // Check if the string is alphabetic first then set it in trip
      while ( userInput.charAt(lastIndex) == ' ' )
         lastIndex ++;

      index = userInput.indexOf (" ", lastIndex);
      if ( index < 0 )
         index = userInput.length();

      str = userInput.substring (lastIndex, index);

      boolean isLetter = true;
      for (int i=0; i < index - lastIndex; i++)
         if ( ! Character.isLetter (str.charAt(i)) ) {
            isLetter = false;
            break;
         }

      return isLetter ? str : null;
   }

   private static int getDateTimeFromInput (StringBuilder userInput, int index, int[] dateArray) {
      boolean isAdderTimeInPorgress, isAdderCompleted, isError;
      char ch;
      int timeAdder, indexDate;

      isAdderTimeInPorgress = false;
      isAdderCompleted = false;
      isError = false;
      timeAdder = 0;
      indexDate = 0;

      for (int i=index; i < userInput.length(); i++) {
         ch = userInput.charAt (i);

         if ( ch == ' ' && isAdderTimeInPorgress && timeAdder > 0) {
            isAdderCompleted = true;
            isAdderTimeInPorgress = false;
         }
         else if ( ch == ' ' ) ;
         else if ( ch == '+' && ! isAdderCompleted)
            isAdderTimeInPorgress = true;
         else if ( Character.isDigit (ch) && isAdderTimeInPorgress )
            timeAdder = timeAdder * 10 + (ch - '0');
         else if ( Character.isDigit (ch) )
            dateArray [indexDate] = dateArray [indexDate] * 10 + (ch - '0');
         else if ( ch == '-' || ch == '/' || ch == '.' ) {
            if ( indexDate < dateArray.length - 1 )
               indexDate ++;
            else
               isError = true;
         }
         else
            isError = true;

         if (isError)
            break;
      }

      return isError ? -1 : timeAdder;
   }

   private static String getNextToken (StringBuilder userInput, int[] indexArr) {
      String str;
      int index = indexArr[0];
      int lastIndex = indexArr[1];

      lastIndex = index;
      while ( userInput.charAt(lastIndex) == ' ' )
         lastIndex ++;

      index = userInput.indexOf (" ", lastIndex);
      if ( index < 0 )
         index = userInput.length();

      str = userInput.substring (lastIndex, index);
      lastIndex = index;

      indexArr[0] = index;
      indexArr[1] = lastIndex;

      return str;
   }

   private static boolean getClienTicketByName (String str, Client client, Ticket ticket) {
      client.setName (str);
      ArrayList<Object> arl = Database.getDatabaseResultFromQuery(client);

      if (arl.size() > 0) {
         client.setWholeClient ((Client) arl.get(0));
         ticket.setIdClient(client.getId());
         arl = Database.getDatabaseResultFromQuery(ticket);
         ticket.setWholeTicket ((Ticket) arl.get(0));

         return true;
      }

      return false;
   }
}
