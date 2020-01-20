package fr.mgen.editions.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Centre {
	private String nom;

	List<EditionPart> editionParts = new ArrayList<>();

	public Centre(String nom) {
		super();
		this.nom = nom;
	}

	public String getEditionsGroupees() {
		StringBuilder editionsGroupees = new StringBuilder();
		this.getEditionParts().forEach(part -> {
			editionsGroupees.append(part.getContent()).append(System.getProperty("line.separator"));
		});
		return editionsGroupees.toString();
	}

}
