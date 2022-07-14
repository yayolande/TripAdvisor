package database;

import java.lang.reflect.*;


public class Client {
    int id;
    String name;
    String surname;
    String phone;
    String email;

    public Client() {
    }

    public Client(int id, String name, String surname, String phone, String email) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.email = email;
    }

    public void setWholeClient (Client client) {
      this.id = client.id;
      this.name = client.name;
      this.surname = client.surname;
      this.phone = client.phone;
      this.email = client.email;
   }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return this.surname;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
      return email;
   }

    public void setEmail(String email) {
        this.email = email;
    }

    @ Override
    public String toString() {
        String str = "";
        Object obj = null;
        Class t = this.getClass();
        Field [] fields = t.getDeclaredFields();
        for(Field field : fields)
        {
            str+=field.toString();
            try{
                obj = field.get(this);
                str += " === " + obj;
            } catch (IllegalAccessException e) {
            System.out.println (e);
         } catch (IllegalArgumentException e) {
            System.out.println (e);
         }
            str += "\n";
        }
        str += "\n";

    return  str;
    }

}
