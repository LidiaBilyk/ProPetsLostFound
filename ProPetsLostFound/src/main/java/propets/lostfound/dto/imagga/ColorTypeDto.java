package propets.lostfound.dto.imagga;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ColorTypeDto {	
	
	@JsonProperty("background_colors")
	List<ColorDto> backgroundColors;
	@JsonProperty("foreground_colors")
	List<ColorDto> foregroundColors;
	@JsonProperty("image_colors")
	List<ColorDto> imageColors;	

}
