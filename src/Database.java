package database;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

import java.lang.reflect.*;
import java.time.*;

public class Database {
   public static ArrayList<Object> getDatabaseResultFromQuery (Object queryUser) {
      // [1] Open then read file line per line;
      // [2] Check if the currrent line match the query, trip_list
      // [3] If it does match, save in ArrayList, or continue to the next line of the File

      File fptr = null;
      ArrayList<Object> arrayList = new ArrayList<>();

      if ( queryUser instanceof Trip)
         fptr = new File ("files_db/trip_list.db");
      else if ( queryUser instanceof Client )
         fptr = new File ("files_db/client_list.db");
      else if ( queryUser instanceof Ticket )
         fptr = new File ("files_db/ticket_list.db");


      // ArrayList<Trip> arrayList = new ArrayList<>();
      //
      // File fptr = new File ("trip_list.db");
      try (BufferedReader bf = new BufferedReader (new FileReader(fptr))) {
         String[] tokens;
         Field[] fields;
         LocalDateTime dateTime = null;
         String str = null;
         String type = null;
         Object obj = null;
         int tmpInt = 0;
         int count = 0;

         Class t = queryUser.getClass ();

         while ((str = bf.readLine()) != null) {
            if (str.isEmpty() || str.charAt(0) == '/' && str.charAt(1) == '/')
               continue;

            tokens = str.split(",");
            fields = t.getDeclaredFields ();

            for (count = 0; count < fields.length; count++) {
               try {
                  obj = fields[count].get (queryUser);
                  type = fields[count].getType().getSimpleName();

                  tokens[count] = tokens[count].trim();
                  if (type.equals("int") ) {
                     tmpInt = Integer.parseInt(obj.toString());
                     if (tmpInt != 0) {
                        if (Integer.parseInt(obj.toString()) <= Integer.parseInt (tokens[count]))
                           ;
                        else
                           break;
                     }
                  }
                  else if (type.equals("String") ) {
                     if (obj != null) {
                        if (obj.toString().equals(tokens[count]))
                           ;
                        else
                           break;
                     }
                  }
                  else if (type.equals("LocalDateTime") ) {
                     if (obj != null) {
                        if (obj.toString().compareTo(tokens[count]) <= 0)
                           ;
                        else
                           break;
                     }
                  }
                  else {
                     System.out.println (" Big deal !!! A problem occur in the database class when reading the type of field.");
                  }

               } catch (IllegalArgumentException e) {
                  System.out.println (e);
               } catch (IllegalAccessException e) {
                  System.out.println (e);
               }
            }

            // Save data in ArrayList
            // What I really meant to say was :
            // isFileHaveBeenCompletelyRead()    INSTEAD    count == fields.length
            if (count == fields.length) {
               if ( queryUser instanceof Trip) {
                  dateTime = LocalDateTime.parse (tokens[4]);

                  arrayList.add (new Trip (Integer.parseInt  (tokens[0]), tokens[1], tokens[2], Integer.parseInt(tokens[3]), dateTime, Integer.parseInt (tokens[5])));
               }
               else if ( queryUser instanceof Client ){
                  arrayList.add (new Client (Integer.parseInt  (tokens[0]), tokens[1], tokens[2], tokens[3], tokens[4]));
               }
               else if ( queryUser instanceof Ticket ){
                  arrayList.add (new Ticket (Integer.parseInt  (tokens[0]), Integer.parseInt (tokens[1]), Integer.parseInt (tokens[2]), Integer.parseInt (tokens[3])));
               }
            }
         }     // End while () loop
      } catch (Exception e) {}

      return arrayList;
   }

   public static boolean addEntryToEndDatabase (Object entry) {
      Object query = null;

      if ( entry instanceof Client)
         query = new Client ();
      else if ( entry instanceof Ticket )
         query = new Ticket ();
      else if ( entry instanceof Trip )
         query = new Trip ();

      ArrayList<Object> resultDatabaseList = getDatabaseResultFromQuery(query);

      int lastIndex = resultDatabaseList.size() - 1;
      int number = 0;

      if ( entry instanceof Client) {
         number =  ((Client) resultDatabaseList.get(lastIndex)).getId();

         ((Client) entry).setId (number + 1);
      }
      else if ( entry instanceof Ticket ) {
         number =  ((Ticket) resultDatabaseList.get(lastIndex)).getId();

         ((Ticket) entry).setId (number + 1);
      }
      else if ( entry instanceof Trip ) {
         number =  ((Trip) resultDatabaseList.get(lastIndex)).getId();

         ((Trip) entry).setId (number + 1);
      }

      resultDatabaseList.add (entry);
      saveDatabaseFromList (resultDatabaseList);

      return true;
   }

   public static boolean saveDatabaseFromList (ArrayList<Object> databaseList) {
      File file = null;
      String str = "";

      if ( databaseList != null ) {

         if ( databaseList.get(0) instanceof Client){
            file = new File ("client_list.db");
            str += "//\n// id | Name | Surname | Phone | Email\n//\n\n";
         }
         else if ( databaseList.get(0) instanceof Ticket ) {
            file = new File ("ticket_list.db");
            str += "//\n// id | idClient | idTrip | nbSeat\n//\n\n";
         }
         else if ( databaseList.get(0) instanceof Trip ) {
            file = new File ("trip_list.db");
            str += "//\n// id | Departure | Arrival | Price (XAF) | Date | Time dep. | Time arr. | nb Seat\n//\n\n";
         }

         try ( BufferedWriter bw = new BufferedWriter(new FileWriter (file)) ){
            Field[] fields;
            StringBuilder strbuilder = new StringBuilder(str);
            Object obj = null;
            int count = 0;



            Class t = databaseList.get(0).getClass ();
            fields = t.getDeclaredFields ();

            for (int i=0; i < databaseList.size(); i++) {
               for (count = 0; count < fields.length; count++) {
                  try {
                     obj = fields[count].get (databaseList.get(i));
                     strbuilder.append (obj.toString() + ",");

                  } catch (IllegalArgumentException e) {
                     System.out.println (e);
                  } catch (IllegalAccessException e) {
                     System.out.println (e);
                  }
               }

               strbuilder.deleteCharAt(strbuilder.length() -1);
               strbuilder.append ("\n");
               // str += "\n";
            }
            str = strbuilder.toString();

            bw.write (str);
         } catch (IOException e) {}
      }

      return true;
   }

   public static boolean deleteEntryDatabase (Object entry) {
      Object query = null;

      if ( entry instanceof Client )
         query = new Client ();
      else if ( entry instanceof Ticket )
         query = new Ticket ();
      else if ( entry instanceof Trip )
         query = new Trip ();

      ArrayList<Object> arl = getDatabaseResultFromQuery(query);

      int id, count;
      id = count = 0;

      if ( entry instanceof Client ){
         id = ((Client) entry).getId ();

         for ( count=0; count < arl.size(); count++) {
            Client client = (Client) arl.get(count);
            if ( id == client.getId() )
               break;
         }

         if ( count < arl.size() )
            arl.remove (count);
      }
      else if ( entry instanceof Ticket ){
         id = ((Ticket) entry).getId ();

         for ( count=0; count < arl.size(); count++) {
            Ticket ticket = (Ticket) arl.get(count);
            if ( id == ticket.getId() )
               break;
         }

         if ( count < arl.size() )
            arl.remove (count);
      }
      else if ( entry instanceof Trip ){
         id = ((Trip) entry).getId ();

         for ( count=0; count < arl.size(); count++) {
            Trip trip = (Trip) arl.get(count);
            if ( id == trip.getId() )
               break;
         }

         if ( count < arl.size() )
            arl.remove (count);
      }

      saveDatabaseFromList (arl);

      return true;
   }
}
