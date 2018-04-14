package graph2;

public class Vertex implements Comparable<Vertex> {
	
	private long id;
	private double lat, lon;
	private double x, y;
	
	public Vertex (long id) {
		this.id = id;
	}
	
	public Vertex(long id , double lat, double lon) {
		this.id = id;
		this.lat = lat;
		this.lon = lon;
		double rLat = Math.toRadians(lat);
		double rLon = Math.toRadians(lon);
		double a = 6378137.0;
		double b = 6356752.3142;
		double f = (a - b) / a;
		double e = Math.sqrt(2 * f - Math.pow(f, 2));
		x = a * rLon;
		y = a * Math.log(Math.PI / 4 + rLat / 2) * ((1 - e * Math.sin(rLat)) / Math.pow((1 + e * Math.sin(rLat)), (e / 2)));
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
	
	public double getLat() {
		return lat;
	}
	
	public double getLon() {
		return lon;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public double distance(Vertex secondVertex) {
		return Math.sqrt(Math.pow(this.x - secondVertex.getX(), 2) + Math.pow(this.y - secondVertex.getY(), 2));
	}
	
	public double distance(Vertex secondVertex, String heuristic) {
		if (heuristic.equals("manhattan") || (heuristic.equals("m")))
			return Math.abs(this.x - secondVertex.getX()) + Math.abs(this.y - secondVertex.getY());
		else if ((heuristic.equals("chebishev")) || (heuristic.equals("c")))
			return Math.max(Math.abs(this.x - secondVertex.getX()), Math.abs(this.y - secondVertex.getY()));
		else
			return Math.sqrt(Math.pow(this.x - secondVertex.getX(), 2) + Math.pow(this.y - secondVertex.getY(), 2));
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