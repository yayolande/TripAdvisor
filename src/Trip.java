package database;

import java.lang.reflect.*;
import java.time.*;

public class Trip {
   int id;  //0
   String departureLocation;  //1
   String arrivalLocation; //2
   int price;  //3
   LocalDateTime dateTime;
   int availableSeat;   //7

   public Trip () {
   }

   public Trip (String departure, LocalDateTime ldt) {
      this.departureLocation = departure;
      this.dateTime = ldt;
   }

   public Trip (int id, String dep, String arrival, int price, LocalDateTime dateTime, int seat) {
      this.id = id;
      this.departureLocation = dep;
      this.arrivalLocation = arrival;
      this.price = price;
      this.dateTime = dateTime;
      // this.date = date;
      // this.departureTime = departureTime;
      // this.arrivalTime = arrivalTime;
      this.availableSeat = seat;
   }

   public void setWholeTrip (Trip trip) {
      setId (trip.id);
      setDeparture(trip.departureLocation);
      setArrival(trip.arrivalLocation);
      setPrice (trip.price);
      setDateTime(LocalDateTime.parse(trip.dateTime.toString()));
      setSeat(trip.availableSeat);
   }

   public void setId (int id) {
      this.id = id;
   }

   public int getId () {
      return this.id;
   }

   public String getDeparture () {
      return this.departureLocation;
   }

   public void setDeparture (String str) {
      this.departureLocation = str;
   }

   public String getArrival () {
      return this.arrivalLocation;
   }

   public void setArrival (String str) {
      this.arrivalLocation = str;
   }

   public int getPrice () {
      return this.price;
   }

   public void setPrice (int price) {
      this.price = price;
   }

   public int getSeat () {
      return this.availableSeat;
   }

   public void setSeat (int seat) {
      this.availableSeat = seat;
   }

   public void setDateTime (LocalDateTime dateTime) {
      this.dateTime = dateTime;
   }

   public LocalDateTime getDateTime () {
      return this.dateTime;
   }

   public String toString () {
      String str = "";
      Object obj = null;
      Class t = this.getClass ();
      Field[] fields = t.getDeclaredFields ();

      for (Field field: fields) {
         //System.out.println (field);
         str += field.toString();
         try {
            obj = field.get (this);
            //System.out.print (" === " + obj);
            str += " === " + obj;
         } catch (IllegalAccessException e) {
            System.out.println (e);
         } catch (IllegalArgumentException e) {
            System.out.println (e);
         }
         str += "\n";
      }
      str += "\n";

      return str;
   }
}
