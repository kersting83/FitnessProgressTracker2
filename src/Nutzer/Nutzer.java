package Nutzer;
import java.util.*;
import java.sql.*;

public class Nutzer {
    private int id;
    private String vorname;
    private String nachname;
    private String email;
    private double gewicht;

    public Nutzer(int id, String vorname, String nachname, String email, double gewicht) {
        this.id = id;
        this.vorname = vorname;
        this.nachname = nachname;
        this.email = email;
        this.gewicht = gewicht;
    }
    public int getId() {
        return id;
    }
    public String getVorname() {
        return vorname;
    }
    public String getNachname() {
        return nachname;
    }
    public String getEmail() {
        return email;
    }
    public double getGewicht() {
        return gewicht;
    }
    @Override
    public String toString() {
        return String.format("%-4d %-15s %-15s %-25s %6.1f kg", id, vorname, nachname, email,  gewicht);
    }
    public String toJson(){
        return String.format(java.util.Locale.US, "{\"id\":%d,\"name\":\"%s\",\"email\":\"%s\",\"gewicht\":%.1f}",
                id, vorname.replace("\"", "\\\""), nachname.replace("\"", "\\\""),
                email.replace("\"", "\\\""), gewicht);
    }
}
