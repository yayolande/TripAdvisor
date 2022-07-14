package database;

import java.lang.reflect.*;


public class Ticket {
   int id;
   int id_client;
   int id_trip;
   int nbSeat;

   public Ticket(int id, int id_client, int id_trip, int nbSeat) {
      this.id = id;
      this.id_client = id_client;
      this.id_trip = id_trip;
      this.nbSeat = nbSeat;
   }

   public Ticket() {
      //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }

    public void setWholeTicket (Ticket ticket) {
      this.id = ticket.id;
      this.id_client = ticket.id_client;
      this.id_trip = ticket.id_trip;
      this.nbSeat = ticket.nbSeat;
   }

    public int getId() {
        return this.id;
   }

    public int getIdTtrip() {
        return this.id_trip;
   }

    public int getIdClient() {
        return this.id_client;
    }

    public int getAvailableSeat() {
        return this.nbSeat;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIdTrip(int id_trip) {
        this.id_trip = id_trip;
    }

    public void setIdClient(int id_client) {
        this.id_client = id_client;
    }

    public void setAvailableSeat(int nbSeat) {
        this.nbSeat = nbSeat;
    }

 @Override
   public String toString () {
      String str = "";
      Object obj ;
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
