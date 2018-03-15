package graphProj;

public class Vertex implements Comparable<Vertex> {
	
	private long id;
	private double lat, lon;
	
	public Vertex (long id) {
		this.id = id;
	}
	
	public Vertex(long id , double lat, double lon) {
		this.id = id;
		this.lat = lat;
		this.lon = lon;
	}
	
	public long getId() {
		return id;
	}
	
	public void setLatitude(double lat) {
		this.lat = lat;
	}
	
	public void setLongitude(double lon) {
		this.lon = lon;
	}
	
	public double getLatitude() {
		return lat;
	}
	
	public double getLongitude() {
		return lon;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vertex other = (Vertex) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public int compareTo(Vertex o) {
		return (new Long(id)).compareTo(new Long(o.getId()));
	}
}
