package ui;

import java.util.Scanner;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.lang.reflect.*;
import java.time.*;
import java.time.temporal.*;
import java.time.format.DateTimeParseException;

import database.Trip;
import database.Client;
import database.Ticket;


import java.io.*;


public class ui {
   private static Scanner kboard = new Scanner (System.in);

   private static void separatorDisplay () {
      System.out.println ("======================================================================================================");
      System.out.println ();
   }

   private static void displayHeader (String str) {
      String logo;
      logo = String.format ("%30s___                       \n", "");
      logo = String.format ("%s%30s | _. _  /\\  _|  . _ _  _ \n", logo, " ");
      logo = String.format ("%s%30s || ||_)/--\\(_|\\/|_)(_)|  \n",  logo," ");
      logo = String.format ("%s%30s     |                    ",  logo," ");
      System.out.println ();
      System.out.println (logo);

      System.out.println ();
      System.out.println (str);

      System.out.println ();
   }

   private static void displayContentTripList (ArrayList<Object> tripList, int[] pageInfo) {
      separatorDisplay ();

      // Content list
      DecimalFormat df = new DecimalFormat("###,###");
      LocalDateTime ldt = null;
      String str, strTimeSuffix, strTmp;
      int count, hour, minute;

      count = 0;
      hour = 0;
      minute = 0;
      str = "";

      if ( tripList != null && !tripList.isEmpty() && tripList.get(0) instanceof Trip ) {
         // for (Trip trip: (ArrayList<Trip>)(tripList)) {
         Trip trip;
         for (int i=0; i < tripList.size(); i++) {
            trip = (Trip) tripList.get (i);

            ldt = trip.getDateTime();

            hour = ldt.getHour();
            minute = ldt.getMinute();

            strTimeSuffix = "am";
            if ( hour > 12 ) {
               strTimeSuffix = "pm";
               hour = hour % 12;
               if ( hour == 0)
               hour = 12;
            }

            strTmp = Month.of(ldt.getMonthValue()).toString().toLowerCase();
            strTmp = strTmp.substring(0, 1).toUpperCase() + strTmp.substring(1, 3);

            str = String.format ("[\u001b[1;7m%2d %3s %4d\u001b[0m]", ldt.getDayOfMonth(), strTmp, ldt.getYear());

            str = String.format ("%3s[%d]\t%s \u001b[32m%02d:%02d %s\u001b[0m \t\u001b[33m"
            + trip.getDeparture() + " --> " + trip.getArrival() + "\u001b[0m\t" + df.format (trip.getPrice()) + " FCFA\t[%3d seats]", " ", count, str, hour, minute, strTimeSuffix, trip.getSeat());
            System.out.println (str);
            count ++;
         }
      }
      else if (tripList != null && !tripList.isEmpty() && tripList.get(0) instanceof Client) {
         System.out.println (tripList);
      }

      int currentPage = pageInfo[0];
      int totalPage = pageInfo[1];
      System.out.println ();
      System.out.println (String.format("%3sPage : \u001b[7m %d/%d \u001b[0m", " ", currentPage, totalPage));
      System.out.println ();

   }


   private static void displayContentTripReg (Trip trip, Client client, Ticket ticket) {
      separatorDisplay ();

      String name, surname, phone, email, input, seat;

      do {
         System.out.println ("[\u001b[3m# seat \u001b[0m] \u001b[1;4m");
         seat = kboard.nextLine ().trim().toLowerCase();  // To improve upon
         System.out.println ("\u001b[0m[\u001b[3mName \u001b[0m] \u001b[1;4m");
         name = kboard.nextLine ().trim().toLowerCase();
         System.out.println ("\u001b[0m[\u001b[3mSurname \u001b[0m] \u001b[1;4m");
         surname = kboard.nextLine ().trim().toLowerCase();
         System.out.println ("\u001b[0m[\u001b[3mPhone \u001b[0m] \u001b[1;4m");
         phone = kboard.nextLine ().trim().toLowerCase();
         System.out.println ("\u001b[0m[\u001b[3mEmail \u001b[0m] \u001b[1;4m");
         email = kboard.nextLine ().trim().toLowerCase();
         System.out.println ("\u001b[0m");

         System.out.println ();
         System.out.println ("\u001b[3;4mConfirmation :\u001b[0m \u001b[1my\\n\u001b[0m");
         input = kboard.nextLine ().trim().toLowerCase();
      } while ( input.charAt (0) != 'y');

      char ch;
      int nbSeat  = 0;
      for (int i=0; i < seat.length(); i++) {
         ch = seat.charAt(i);

         if ( Character.isDigit(ch) )
            nbSeat = nbSeat * 10 + (ch - '0');
         else
            break;
      }

      if (nbSeat == 0)
         nbSeat = 1;

      client.setWholeClient (new Client (0, name, surname, phone, email));
      ticket.setWholeTicket (new Ticket (0, 12, trip.getId(), nbSeat));   // id_client
   }

   // Add a new parameter to display userInputError
   private static StringBuilder displayFooter () {
      separatorDisplay ();

      System.out.print ("> \u001b[35m");

      StringBuilder userInput;
      userInput = new StringBuilder (kboard.nextLine ().trim().toLowerCase());
      // Trim and Lowercase user input
      System.out.print ("> \u001b[0m");

      return userInput;
   }

   public static StringBuilder displayManagerTripList (ArrayList<Object> tripList, Trip tripPreference, int[] pageInfo) {
      StringBuilder userInput;
      String str;

      str = buildStringHeader (tripPreference, "\u001b[7mUser preference :\u001b[0m");
      displayHeader (str);

      displayContentTripList (tripList, pageInfo);
      userInput = displayFooter ();

      return userInput;
   }

   public static void displayManagerTripReg (Trip trip, Client client, Ticket ticket) {
      String str ;

      // "client's trip : "
      str = buildStringHeader (trip, "\u001b[7mTrip :\u001b[0m");
      displayHeader (str);

      displayContentTripReg (trip, client, ticket);
   }

   public static void displayManagerClient (Client client, Ticket ticket, Trip trip) {
      String str ;

      str = buildStringHeader (trip, "\u001b[7mTrip :\u001b[0m \t" + trip.getId());
      displayHeader (str);

      separatorDisplay ();

      str = String.format ("%4sName : \u001b[1;4m%s  %s\u001b[0m\tPhone : \u001b[1;4m%s\u001b[0m\tEmail: \u001b[1;4m%s\u001b[0m\t# Seat : \u001b[1;4m%d\u001b[0m", " ", client.getName(), client.getSurname(), client.getPhone(), client.getEmail(), ticket.getAvailableSeat());

      System.out.println (str);
      System.out.println ();

      System.out.println ("     \u001b[3m[Press a key to continue] ....\u001b[0m");
      kboard.nextLine ();
   }

   private static String buildStringHeader (Trip tripPreference, String strLabel) {
      DecimalFormat df = new DecimalFormat("#,###,###");
      String str, strMonth;
      String[] strLocation = new String [2];
      int hour, minute, price, nbSeat, year, month, dayOfMonth;

      strLocation[0] = tripPreference.getDeparture();
      strLocation[1] = tripPreference.getArrival();

      for (int i=0; i < strLocation.length; i++)
         strLocation[i] = (strLocation[i] == null) ? "*" : strLocation[i];

         // System.out.println (tripPreference);
         // System.out.println ("toasted !!!!!!!!!!!!!!!!!!!");

      year = tripPreference.getDateTime().getYear();
      // System.out.println ("toasted !!!!!!!!!!!!!!!!!!!");
      month = tripPreference.getDateTime().getMonthValue();
      dayOfMonth = tripPreference.getDateTime().getDayOfMonth();
      hour = tripPreference.getDateTime().getHour();
      minute = tripPreference.getDateTime().getMinute();
      price = tripPreference.getPrice();
      nbSeat = tripPreference.getSeat();

      strMonth = Month.of(month).toString().substring(0,3).toLowerCase();
      strMonth = strMonth.substring(0, 1).toUpperCase() + strMonth.substring(1);

      str = strLabel + "\t" + String.format("[%d %s %d]", dayOfMonth, strMonth, year) + String.format (" %02d:%02d %2s", hour, minute, "am") ;
      str += String.format ("%5s%s --> %s", " ", strLocation[0], strLocation[1]);
      str += String.format ("%5s%s FCFA%5s[%3d seats]", " ", df.format(price), " ", nbSeat);
      str = String.format ("%4s%s", " ", str);

      return str;
   }

}
