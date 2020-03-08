package telran.ProPets.dto.imagga;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class ColorDto {
	@JsonProperty("closest_palette_color")	
	String name;
	@JsonProperty("closest_palette_color_parent")
	String parentName;
	double percent;
	@Override
	public String toString() {
		return name + "     " + parentName + "     " + percent;
	}
}
