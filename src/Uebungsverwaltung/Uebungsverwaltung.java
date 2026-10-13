package Uebungsverwaltung;
import java.sql.*;
import Datenbank.Datenbank;

public class Uebungsverwaltung {

    //Bedingungen: Level nach Anzahl der Workouts
    public static String ermittleLevel(int anzahl) {
        if (anzahl < 5){
            return "Anfänger";
        }else if (anzahl < 15){
            return "Fortgeschritten";
        }else {
            return "Profi";
        }
    }
    // Dieser Abschnitt wird für die Statistik gebraucht
    public int zaehleWorkouts(int nutzer_id) throws SQLException {
        String sql = """
                SELECT COUNT(*)
                FROM workouts
                WHERE nutzer_id = ?;
                """;
        try(Connection con = Datenbank.verbindungHerstellen();
            PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, nutzer_id);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }
    public void fortschrittAnzeigen(int nutzer_id) {
        String sql = """
                SELECT n.vorname, n.nachname, COUNT(w.workout_id) AS anzahl
                FROM nutzer n
                LEFT JOIN workouts w ON n.nutzer_id = w.nutzer_id
                WHERE n.nutzer_id = ?
                GROUP BY n.vorname,  n.nachname;
                """;
        try (Connection con = Datenbank.verbindungHerstellen();
             PreparedStatement ps = con.prepareStatement(sql)){
            ps.setInt(1, nutzer_id);
            try(ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    int anzahl = rs.getInt("anzahl");
                    System.out.println("Nutzer: " + rs.getString("vorname") + " " + rs.getString  ("nachname"));
                    System.out.println("Workouts: " + anzahl);
                    System.out.println("Level: " + ermittleLevel(anzahl));
                }else {
                    System.out.println("Nutzer nicht gefunden. ");
                }
            }
        }catch(SQLException e) {
            System.out.println("Fehler: " + e.getMessage());
        }
    }
    public void uebungenAnzeigen() {
        String sql = "SELECT uebung_id, name, muskelgruppe FROM uebungen ORDER BY uebung_id";
        try (Connection con = Datenbank.verbindungHerstellen();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.printf("%-4s %-18s %-12s%n", "ID", "Übung", "Muskelgruppe");
            while (rs.next()) {
                System.out.printf("%-4d %-18s %-12s%n",
                        rs.getInt("uebung_id"),
                        rs.getString("name"),
                        rs.getString("muskelgruppe"));
            }
        } catch (SQLException e) {
            System.out.println("Fehler: " + e.getMessage());
        }
    }
    public void workoutsAnzeigen(int nutzer_id) {
        String sql = """
                SELECT w.workout_id, w.datum, u.name AS uebung,
                       w. saetze, w.wiederholungen, w.gewicht_kg
                FROM workouts w
                JOIN uebungen u ON w.uebung_id = u.uebung_id
                WHERE w.nutzer_id = ?
                ORDER BY w.datum DESC;
                """;
        try (Connection con = Datenbank.verbindungHerstellen();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, nutzer_id);
            try (ResultSet rs = ps.executeQuery()) {
                boolean gefunden = false;
                System.out.printf("%-4s %-12s %-18s %6s %5s %8s%n",
                        "ID", "Datum", "uebung", "Sätze", "Wdh", "kg");
                while (rs.next()) {
                    gefunden = true;
                    System.out.printf("%-4d %-12s %-18s %6d %5d %8.1f%n",
                            rs.getInt("workout_id"),
                            rs.getDate("datum"),
                            rs.getString("uebung"),
                            rs.getInt("saetze"),
                            rs.getInt("wiederholungen"),
                            rs.getDouble("gewicht_kg"));
                }
                if (!gefunden) {
                    System.out.println("Keine Workouts gefunden.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Fehler: " + e.getMessage());
        }
    }
    public void workoutHinzufuegen(int nutzerId, int uebungId, int saetze,
                                   int wiederholungen, double gewichtKg) {
        // Eingaben prüfen, bevor die Datenbank angefragt wird
        if (saetze <= 0 || wiederholungen <= 0 || gewichtKg < 0) {
            System.out.println("Sätze und Wiederholungen müssen größer als 0 sein, "
                    + "das Gewicht darf nicht negativ sein.");
            return;
        }
        String sql = "INSERT INTO workouts "
                + "(nutzer_id, uebung_id, saetze, wiederholungen, gewicht_kg) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = Datenbank.verbindungHerstellen();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, nutzerId);
            ps.setInt(2, uebungId);
            ps.setInt(3, saetze);
            ps.setInt(4, wiederholungen);
            ps.setDouble(5, gewichtKg);
            ps.executeUpdate();
            System.out.println("Workout wurde dem Nutzer " + nutzerId + " zugeordnet.");
        } catch (SQLException e) {
            // 23503 = Fremdschlüssel-Verletzung: Nutzer oder Übung existiert nicht
            if ("23503".equals(e.getSQLState())) {
                System.out.println("Nutzer-ID oder Übungs-ID existiert nicht.");
            } else {
                System.out.println("Fehler beim Hinzufügen: " + e.getMessage());
            }
        }
    }
    public void workoutAendern(int workoutId, int saetze, int wiederholungen, double gewicht_kg) {
        String sql = """
                UPDATE workouts
                SET saetze = ?,
                wiederholungen = ?,
                gewicht_kg = ?
                WHERE workout_id = ?;
                """;
        try(Connection con = Datenbank.verbindungHerstellen();
            PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, saetze);
            ps.setInt(2, wiederholungen);
            ps.setDouble(3, gewicht_kg);
            ps.setInt(4, workoutId);
            int zeilen = ps.executeUpdate();
            if (zeilen == 0) {
                System.out.println("Workout nicht gefunden. ");
            }else {
                System.out.println("Workout wurde geändert.");
            }
        }catch(SQLException e) {
            System.out.println("Fehler beim Ändern: " + e.getMessage());
        }
    }
    public void workoutLoeschen(int workoutId) {
        String sql = """
                   DELETE FROM workouts
                    WHERE workout_id = ?;
                    """;

        try (Connection con = Datenbank.verbindungHerstellen();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, workoutId);
            int zeilen = ps.executeUpdate();
            if (zeilen == 0) {
                System.out.println("Workout nicht gefunden.");
            } else {
                System.out.println("Workout wurde gelöscht.");
            }
        } catch (SQLException e) {
            System.out.println("Fehler beim Löschen: " + e.getMessage());
        }
    }
    public void statistikAnzeigen(int nutzerId) {
        String sql = """
                SELECT u.name AS uebung,
                    COUNT(*) AS einheiten,
                    SUM(w.saetze * w.wiederholungen) AS gesamt_wdh,
                    ROUND(AVG(w.gewicht_kg), 1) AS durchschnitt_kg,
                    MAX(w.gewicht_kg) AS max_kg
                FROM workouts w
                JOIN uebungen u ON w.uebung_id = u.uebung_id
                WHERE w.nutzer_id = ?
                GROUP BY u.name
                HAVING COUNT(*) >= 2
                ORDER BY einheiten DESC;
                """;

        try(Connection con = Datenbank.verbindungHerstellen();
            PreparedStatement ps = con.prepareStatement(sql)){
            ps.setInt(1, nutzerId);
            try(ResultSet rs = ps.executeQuery()) {
                boolean gefunden = false;
                System.out.printf("%-18s %9s %12s %10s %8s%n",
                        "Übung", "Einheiten", "Gesamt-Wdh", "Ø kg", "Max kg");
                while (rs.next()) {
                    gefunden = true;
                    System.out.printf("%-18s %9d %12d %10.1f %8.1f%n",
                            rs.getString("uebung"),
                            rs.getLong("einheiten"),
                            rs.getLong("gesamt_wdh"),
                            rs.getDouble("durchschnitt_kg"),
                            rs.getDouble("max_kg"));
                }
                if (!gefunden) {
                    System.out.println("Noch keine Auswertung (mind. 2 Einheiten je Übung nötig).");
                }
            }
        }catch(SQLException e) {
            System.out.println("Fehler bei der Auswertung: " + e.getMessage());
        }
    }
}
