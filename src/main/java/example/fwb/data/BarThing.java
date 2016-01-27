package example.fwb.data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class BarThing {
	@Id
	public String theIdentifier;
	public String aDescription;
}
