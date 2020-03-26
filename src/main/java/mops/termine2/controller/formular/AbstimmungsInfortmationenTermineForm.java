package mops.termine2.controller.formular;

import lombok.Getter;
import mops.termine2.KonstantenAbstimmung;
import mops.termine2.models.Terminfindung;

@Getter
public class AbstimmungsInfortmationenTermineForm {
	
	String achtung = KonstantenAbstimmung.ATTENTION;
	
	String einmaligesAbstimmen;
	
	String ergebnisVorFrist;
	
	String abstimmungsBerechtigungNutzer;
	
	public AbstimmungsInfortmationenTermineForm(Terminfindung terminfindung) {
		setAbstimmungsBerechtigungNutzer(terminfindung);
		setEinmaligesAbstimmen(terminfindung);
		setErgebnisVorFrist(terminfindung);
	}
	
	private void setEinmaligesAbstimmen(Terminfindung terminfindung) {
		if (terminfindung.getEinmaligeAbstimmung()) {
			einmaligesAbstimmen = KonstantenAbstimmung.EINMALIGES_ABSTIMMEN_BIS_FRIST;
		} else {
			einmaligesAbstimmen = KonstantenAbstimmung.BELIEBIGES_ABSTIMMEN_BIS_FRIST;
		}
	}
	
	private void setErgebnisVorFrist(Terminfindung terminfindung) {
		if (terminfindung.getErgebnisVorFrist()) {
			ergebnisVorFrist = KonstantenAbstimmung.ERGEBNIS_VOR_FRIST;
		} else {
			ergebnisVorFrist = KonstantenAbstimmung.ERGEBNIS_NACH_FRIST;
		}
	}
	
	private void setAbstimmungsBerechtigungNutzer(Terminfindung terminfindung) {
		if (terminfindung.isTeilgenommen() && terminfindung.getEinmaligeAbstimmung()) {
			abstimmungsBerechtigungNutzer = KonstantenAbstimmung.NUTZER_DARF_NICHTMEHR_ABSTIMMEN;
		} else {
			abstimmungsBerechtigungNutzer = KonstantenAbstimmung.NUTZER_DARF_NOCH_ABSTIMMEN;
		}
	}
	
	
}
