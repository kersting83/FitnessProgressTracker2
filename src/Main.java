import java.util.*;
import java.sql.*;
import Datenbank.Datenbank;
import Nutzer.Nutzer;
import NutzerDAO.NutzerDAO;
import Uebungsverwaltung.Uebungsverwaltung;
//import com.sun.net.httpserver.HttpExchange;
//import com.sun.net.httpserver.HttpServer;
//import java.io.IOException;
//import java.io.OutputStream;
//import java.net.InetSocketAddress;
//import java.nio.charset.StandardCharsets;


public class Main {
    public static void main(String[] args) {

        // 1. Verbindung testen
        try (Connection con = Datenbank.verbindungHerstellen()) {
            System.out.println("Datenbankverbindung erfolgreich.");
        } catch (SQLException e) {
            System.out.println("Keine Verbindung zur Datenbank: " + e.getMessage());
            return;
        }
        // 1.1 Objekte der externen Klassen erzeugen
        NutzerDAO nutzerVw = new NutzerDAO();
        Uebungsverwaltung uebungVw = new Uebungsverwaltung();
        Scanner sc = new Scanner(System.in);

        // 2. API starten
       // HttpServer server = starteApi(nutzerVw, uebungVw);

        // 3. Menüschleife
        boolean running = true;
        while (running) {
            zeigeMenue();
            int wahl = leseInt(sc, "Auswahl: ");
            switch (wahl) {
                case 1 -> {
                    System.out.print("Vorname: ");
                    String vorname = sc.nextLine();
                    System.out.print("Nachname: ");
                    String nachname = sc.nextLine();
                    System.out.print("E-Mail: ");
                    String email = sc.nextLine();
                    double gewicht = leseDouble(sc, "Gewicht in kg: ");
                    nutzerVw.nutzerAnlegen(vorname, nachname, email, gewicht);
                }case 2 -> nutzerVw.nutzerAnzeigen();
                case 3 -> nutzerVw.nutzerLoeschen(leseInt(sc, "Nutzer-ID: "));
                case 4 -> uebungVw.fortschrittAnzeigen(leseInt(sc, "Nutzer-ID: "));
                case 5 -> uebungVw.workoutsAnzeigen(leseInt(sc, "Nutzer-ID: "));
                case 6 -> {
                int id = leseInt(sc, "Workout-ID: ");
                int saetze = leseInt(sc, "Neue Sätze: ");
                int wdh = leseInt(sc, "Neue Wiederholungen: ");
                double kg = leseDouble(sc, "Neues Gewicht in kg: ");
                uebungVw.workoutAendern(id, saetze, wdh, kg);
                }
                case 7 -> uebungVw.workoutLoeschen(leseInt(sc, "Workout-ID: "));
                case 8 -> uebungVw.statistikAnzeigen(leseInt(sc, "Nutzer-ID: "));
                case 9 -> {
                    uebungVw.uebungenAnzeigen();                  // zeigt die Übungs-IDs
                    int nutzerId = leseInt(sc, "Nutzer-ID: ");
                    int uebungId = leseInt(sc, "Übungs-ID: ");
                    int saetze   = leseInt(sc, "Sätze: ");
                    int wdh      = leseInt(sc, "Wiederholungen: ");
                    double kg    = leseDouble(sc, "Gewicht in kg: ");
                    uebungVw.workoutHinzufuegen(nutzerId, uebungId, saetze, wdh, kg);
                }
                case 0 -> running = false;
                default -> System.out.println("Ungültige Auswahl.");
        }
    }
    // 4. Server stoppen
       // if (server != null) {
        //server.stop(0);
   // }
     //   System.out.println("Programm beendet.");
}
private static void zeigeMenue() {
    System.out.println();
    System.out.println("===== FITNESSTRACKER =====");
    System.out.println("1 Nutzer anlegen");
    System.out.println("2 Nutzer anzeigen");
    System.out.println("3 Nutzer löschen");
    System.out.println("4 Fortschritt anzeigen");
    System.out.println("5 Workouts anzeigen");
    System.out.println("6 Workout ändern");
    System.out.println("7 Workout löschen");
    System.out.println("8 Statistik anzeigen");
    System.out.println("9 Workout hinzufügen");
    System.out.println("0 Beenden");
}
// Exception Handling bei Benutzereingaben
private static int leseInt(Scanner sc, String text) {
    while (true) {
        System.out.print(text);
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Bitte eine ganze Zahl eingeben.");
        }
    }
}
private static double leseDouble(Scanner sc, String text) {
    while (true) {
        System.out.print(text);
        try {
            return Double.parseDouble(sc.nextLine().trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            System.out.println("Bitte eine Zahl eingeben.");
        }
    }
}
// ---------- API ----------
 //GET http://localhost:8080/api/nutzer
//GET http://localhost:8080/api/fortschritt?id=1
//private static HttpServer starteApi(NutzerDAO nutzerVw, Uebungsverwaltung uebungVw) {
  //  try {
    //    HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

      //  server.createContext("/api/nutzer", exchange -> {
        //    try {
          //      ArrayList<Nutzer> liste = nutzerVw.alleNutzerLaden();
            //    StringBuilder json = new StringBuilder("[");
              //  for (int i = 0; i < liste.size(); i++) {
                //    json.append(liste.get(i).toJson());
                  //  if (i < liste.size() - 1) {
                    //    json.append(",");
                    //}
               // }
               // json.append("]");
               // antworten(exchange, 200, json.toString());
            //} catch (SQLException e) {
              //  antworten(exchange, 500, "{\"fehler\":\"Datenbankfehler\"}");
            //}
        //});
    //}catch(SQLException e) {
      //  System.out.println("Fehler bei der Auswahl: " + e.getMessage());
   // }
   // server.createContext("/api/fortschritt", exchange -> {
     //   String query = exchange.getRequestURI().getQuery();
       // if (query == null || !query.startsWith("id=")) {
         //   antworten(exchange, 400, "{\"fehler\":\"Parameter id fehlt\"}");
           // return;
       // }
       // try {
         //   int id = Integer.parseInt(query.substring(3));
           // int anzahl = uebungVw.zaehleWorkouts(id);
            //String level = Uebungsverwaltung.ermittleLevel(anzahl);
            //antworten(exchange, 200, String.format(
              //      "{\"nutzerId\":%d,\"workouts\":%d,\"level\":\"%s\"}", id, anzahl, level));
        //} catch (NumberFormatException e) {
          //  antworten(exchange, 400, "{\"fehler\":\"id muss eine Zahl sein\"}");
        //} catch (SQLException e) {
          //  antworten(exchange, 500, "{\"fehler\":\"Datenbankfehler\"}");
        //}
    //});
    //server.start();
    //System.out.println("API läuft auf http://localhost:8080/api/nutzer");
    //return server;
    //}catch(IOException e) {
      //  System.out.println("API konnte nicht gestartet werden: " + e.getMessage());
        //return null;
        //}
        //}
//private static void antworten(HttpExchange exchange, int status, String json) throws IOException {
  //  byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
    //exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
    //exchange.sendResponseHeaders(status, bytes.length);
    //try (OutputStream os = exchange.getResponseBody()) {
      //  os.write(bytes);
    //}
//}
}
}