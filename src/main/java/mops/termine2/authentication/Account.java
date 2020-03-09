package mops.termine2.authentication;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
@AllArgsConstructor
public class Account {
	
	private final String name; // Name der angemeldeten Person
	
	private final String email; // E-Mail-Adresse
	
	private final String image; // Bild (kann für jeden null sein)
	
	private final Set<String> roles = new HashSet<>(); // Rollen der Person
	
}
