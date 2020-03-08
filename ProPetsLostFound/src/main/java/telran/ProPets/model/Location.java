package telran.ProPets.model;

import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Embeddable
public class Location {
	String country;
    String city;
    String street;
    int building;
	double longitude;
	double latitude;
	

}
