package NutzerDAO;
import Nutzer.Nutzer;
import Datenbank.Datenbank;

import java.sql.*;
import java.util.*;


public class NutzerDAO {

    public void nutzerAnlegen(String vorname, String nachname, String email, double gewicht){
        String sql = """
                INSERT INTO nutzer (vorname, nachname, email, gewicht)
                VALUES (?, ?, ?, ?);
                """;
        try (Connection con = Datenbank.verbindungHerstellen();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, vorname);
            ps.setString(2, nachname);
            ps.setString(3, email);
            ps.setDouble(4, gewicht);
            ps.executeUpdate();
            System.out.println("Nutzer wurde angelegt. ");
        }catch (SQLException e) {
            System.out.println("Fehler beim Anlegen des Nutzers: " + e.getMessage());
        }
    }
    public ArrayList<Nutzer> alleNutzerLaden() throws SQLException{
        ArrayList<Nutzer> liste = new ArrayList<>();
        String sql = """
                SELECT nutzer_id, vorname, nachname, email, gewicht
                FROM nutzer
                ORDER BY nutzer_id;
                """;
        try (Connection con = Datenbank.verbindungHerstellen();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                liste.add(new Nutzer(
                        rs.getInt("Nutzer_id"),
                        rs.getString("vorname"),
                        rs.getString("nachname"),
                        rs.getString("email"),
                        rs.getDouble("gewicht")));
            }
        }
        return liste;

    }
    public void nutzerAnzeigen() {
        try {
            ArrayList<Nutzer> liste = alleNutzerLaden();
            if (liste.isEmpty()) {
                System.out.println("Keine Nutzer vorhanden.");
                return;
            }
            System.out.printf("%-4s %-15s %-15s %-25s %9s%n", "ID", "Vorname", "Nachname", "E-Mail", "Gewicht");
            for (Nutzer n : liste) {
                System.out.println(n);
            }
        } catch (SQLException e) {
            System.out.println("Fehler beim Laden: " + e.getMessage());
        }
    }
    public void nutzerLoeschen(int nutzer_id) {
        String sql = """
                DELETE FROM nutzer
                WHERE nutzer_id = ?;
                """;
        try (Connection con = Datenbank.verbindungHerstellen();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, nutzer_id);
            int zeilen = ps.executeUpdate();
            if (zeilen == 0) {
                System.out.println("Nutzer nicht gefunden. ");
            }else {
                System.out.println("Nutzer und seine Workouts wurden gelöscht.");
            }
        }catch (SQLException e) {
            System.out.println("Fehler beim Löschen der Nutzerdaten: " + e.getMessage());
        }

    }
}
