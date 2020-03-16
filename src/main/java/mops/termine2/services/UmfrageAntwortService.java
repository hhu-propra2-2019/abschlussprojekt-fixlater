package mops.termine2.services;

import org.springframework.stereotype.Service;

import mops.termine2.database.UmfrageAntwortRepository;

@Service
public class UmfrageAntwortService {
	
	private UmfrageAntwortRepository antwortRepo;
	
	public UmfrageAntwortService(UmfrageAntwortRepository umfrageAntwortRepository) {
		antwortRepo = umfrageAntwortRepository;
	}
	
}
