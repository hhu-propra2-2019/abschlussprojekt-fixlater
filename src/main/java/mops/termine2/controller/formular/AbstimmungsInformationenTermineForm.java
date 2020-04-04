package mops.termine2.controller.formular;

import lombok.Getter;
import mops.termine2.models.Terminfindung;

@Getter
public class AbstimmungsInformationenTermineForm {
	
	Boolean einmaligeAbstimmung;
	
	Boolean zwischenergebnis;
	
	Boolean teilnahmeMoeglich;
	
	public AbstimmungsInformationenTermineForm(Terminfindung terminfindung) {
		setAbstimmungsBerechtigungNutzer(terminfindung);
		setEinmaligesAbstimmen(terminfindung);
		setErgebnisVorFrist(terminfindung);
	}
	
	private void setEinmaligesAbstimmen(Terminfindung terminfindung) {
		einmaligeAbstimmung = terminfindung.getEinmaligeAbstimmung();
	}
	
	private void setErgebnisVorFrist(Terminfindung terminfindung) {
		zwischenergebnis = terminfindung.getErgebnisVorFrist();
	}
	
	private void setAbstimmungsBerechtigungNutzer(Terminfindung terminfindung) {
		teilnahmeMoeglich = !(terminfindung.getTeilgenommen() && terminfindung.getEinmaligeAbstimmung());
	}
	
}

