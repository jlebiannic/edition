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

}
