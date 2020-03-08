package telran.ProPets.dto.imagga;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class Tag {
	@JsonProperty("en")
	String word;

}
