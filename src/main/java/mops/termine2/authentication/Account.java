package mops.termine2.authentication;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class Account {
	
	private final String name; // Name der angemeldeten Person
	
	private final String email; // E-Mail-Adresse
	
	private final String image; // Bild (kann für jeden null sein)
	
	private Set<String> roles = new HashSet<>(); // Rollen der Person
	
	public Account(String name, String email, String image, Set<String> roles) {
		this.name = name;
		this.email = email;
		this.image = image;
		this.roles = roles;
	}
}
