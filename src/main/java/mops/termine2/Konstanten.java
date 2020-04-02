package mops.termine2;

public class Konstanten {
	
	public static final String ROLE_ORGA = "ROLE_orga";
	
	public static final String ROLE_STUDENTIN = "ROLE_studentin";
	
	public static final String STUDENTIN = "studentin";

	public static final String ACCESS_AUTHENTICATED = "access.authenticated";
	
	//Fehlermeldungen
	public static final String ERROR_GROUP_ACCESS_DENIED = "Kein Zugriff auf Gruppe";
	
	public static final String ERROR_NOT_LOGGED_IN = "Nicht eingeloggt";
	
	public static final String ERROR_PAGE_NOT_FOUND = "Seite nicht gefunden";
  
	public static final String ERROR_ACCESS_DENIED = "Zugriff verweigert";
	
	//Modelattribute
	public static final String MODEL_ACCOUNT = "account";
	
	public static final String MODEL_GRUPPEN = "gruppen";

	public static final String MODEL_FEHLER = "fehler";

	public static final String MODEL_TERMINFINDUNG = "terminfindung";

	public static final String MODEL_UMFRAGE = "umfrage";

	public static final String MODEL_GRUPPE_SELEKTIERT = "gruppeSelektiert";

	public static final String MODEL_ERGEBNIS = "ergebnis";

	public static final String MODEL_NEUER_KOMMENTAR = "neuerKommentar";

	public static final String MODEL_KOMMENTARE = "kommentare";

	public static final String MODEL_ANTWORT = "antwort";

	public static final String MODEL_INFO = "info";

	public static final String MODEL_TERMINE = "termine";

	public static final String MODEL_UMFRAGEN = "umfragen";
	
	public static final String MODEL_ERFOLG = "erfolg";
	
	public static final String MODEL_ERROR = "error";
	
	public static final String MODEL_MESSAGE = "message";
	
	//Nachrichten
	public static final String MESSAGE_TERMIN_GESPEICHERT = 
		"Der Termin wurde gespeichert.";
	
	public static final String MESSAGE_TERMIN_FRIST_KURZFRISTIG = 
		"Die Frist ist zu kurzfristig.";
	
	public static final String MESSAGE_UMFRAGE_GESPEICHERT = 
		"Die Umfrage wurde gespeichert.";
	
	public static final String MESSAGE_KEIN_VORSCHLAG = 
		"Es muss mindestens einen Vorschlag geben.";
	
	public static final String MESSAGE_LINK_EXISTENT = 
		"Der eingegebene Link existiert bereits.";
	
	public static final String MESSAGE_LINK_UNGUELTIG = 
		"Der eingegebene Link enthält ungültige Zeichen";
	
	public static final String MESSAGE_CSV_ERFOLG = 
		"Upload erfolgreich!";
	
	public static final String MESSAGE_CSV_NICHT_VORHANDEN = 
		"Bitte eine CSV-Datei zum Upload auswählen.";
	
	public static final String MESSAGE_CSV_FEHLER = 
		"Ein Fehler ist beim Verarbeiten der CSV-Datei aufgetreten.";
	
	public static final String MESSAGE_CSV_TERMINE_NICHT_EXISTENT = 
		"Die Termine sollten existieren.";
	
	public static final String MESSAGE_CSV_NICHT_ZUKUENFTIG = 
		"Die Termine sollten in der Zukunft liegen.";
	
	public static final String MESSAGE_CSV_UNGUELTIGES_FORMAT = 
		"Alle Termine müssen im Format "
		+ "'TT.MM.JJJJ,HH:MM' übergeben werden und "
		+ "sollten existente Daten sein.";


}
