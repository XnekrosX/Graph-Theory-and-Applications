package graph2;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.io.*;

public class Graph {
	
	Set<Vertex> vertexes;
	Set<Edge> edges;
	List<List<Vertex>> adjacencyList;
	
	double maxLat, minLat, maxLon, minLon, maxX, minX, maxY, minY;
	
	public Graph(Set<Vertex> vertexes, Set<Edge> edges){
		this.vertexes = vertexes;
		this.edges = edges;
		this.adjacencyList = adjacencyList();
		findMinMaxCoord();
	}
	
	public double getMaxLat() {
		return maxLat;
	}
	
	public double getMinLat() {
		return minLat;
	}
	
	public double getMaxLon() {
		return maxLon;
	}
	
	public double getMinLon() {
		return minLon;
	}
	
	public class DistancesAndPrev{
		List<Double> distances;
		List<Integer> prev;
		DistancesAndPrev(List<Double> distances, List<Integer> prev){
			this.distances = distances;
			this.prev = prev;
		}
	}
	
	public List<List<Vertex>> adjacencyList() {
		List<List<Vertex>> result = new ArrayList<List<Vertex>>();
		for (Vertex vertex : vertexes) {
			List<Vertex> list = new ArrayList<Vertex>();
			for (Edge edge : edges) {
				if (vertex.equals(edge.getVertex1()))
					list.add(edge.getVertex2());
				if (vertex.equals(edge.getVertex2()))
					list.add(edge.getVertex1());
			}
			result.add(list);
		}
		return result;
	}
	
	public void outputAdjacencyList(String filename) throws IOException {
		try(FileWriter fout = new FileWriter(filename)) {
			List<Vertex> listOfVertexes = new ArrayList<Vertex>(vertexes);
			for (int i = 0; i < adjacencyList.size(); i++) {
				fout.write((new Long(listOfVertexes.get(i).getId())).toString());
				for (Vertex vertex : adjacencyList.get(i)) {
					fout.write("," + (new Long(vertex.getId())).toString());
				}
				fout.write("\n");
			}
		} catch (IOException exc) {
			System.out.println("I/O Error: " + exc);
		}
	}
	
	public void outputAdjacencyMatrix(String filename) throws IOException {
		try(FileWriter fout = new FileWriter(filename)) {
			ArrayList<Vertex> array  = new ArrayList<>();
			array.addAll(vertexes);
			int[][] matrix = new int[array.size()][array.size()];
			for (Edge edge : edges) {
				int firstInd = array.indexOf(edge.getVertex1());
				int secondInd = array.indexOf(edge.getVertex2());
				matrix[firstInd][secondInd]++;
			}
			fout.write(",");
			String buffer = "";
			for (Vertex vertex : vertexes)
				buffer += ((new Long(vertex.getId())).toString() + ",");
			fout.write(buffer.substring(0, buffer.length() - 1) + "\n");
			for (int i = 0; i < matrix.length; i++) {
				fout.write((new Long(array.get(i).getId())).toString());
				for (int k = 0; k < matrix[i].length; k++)
					fout.write("," + matrix[i][k]);
				fout.write("\n");
			}
		} catch (IOException exc) {
			System.out.println("I/O Error: " + exc);
		}
	}
	
	public Vertex nearestVertex(double x, double y) {
		Vertex nearestVertex = vertexes.iterator().next();
		double oldDistance = Math.sqrt(Math.pow(nearestVertex.getX() - x, 2) + Math.pow(nearestVertex.getY() - y, 2));
		for (Vertex vertex : vertexes) {
			double newDistance = Math.sqrt(Math.pow(vertex.getX() - x, 2) + Math.pow(vertex.getY() - y, 2));
			if (newDistance < oldDistance) {
				oldDistance = newDistance;
				nearestVertex = vertex;
			}
		}
		return nearestVertex;
	}
	
	public void findMinMaxCoord() {
		maxX = vertexes.iterator().next().getX();
        minX = vertexes.iterator().next().getX();
        maxY = vertexes.iterator().next().getY();
        minY = vertexes.iterator().next().getY();
        
        for (Vertex vertex : vertexes) {
        	if (vertex.getX() > maxX)
        		maxX = vertex.getX();
        	else if (vertex.getX() < minX)
        		minX = vertex.getX();
        	if (vertex.getY() > maxY)
        		maxY = vertex.getY();
        	else if (vertex.getY() < minY)
        		minY = vertex.getY();
        }
        
        maxLat = vertexes.iterator().next().getLat();
        minLat = vertexes.iterator().next().getLat();
        maxLon = vertexes.iterator().next().getLon();
        minLon = vertexes.iterator().next().getLon();
        
        for (Vertex vertex : vertexes) {
        	if (vertex.getLat() > maxLat)
        		maxLat = vertex.getLat();
        	else if (vertex.getLat() < minLat)
        		minLat = vertex.getLat();
        	if (vertex.getLon() > maxLon)
        		maxLon = vertex.getLon();
        	else if (vertex.getLon() < minLon)
        		minLon = vertex.getLon();
        }
	}
	
	public void outputSVG() throws IOException {
        final double size = 3000.0;
        final double xCoef = (maxX - minX) / size;
        final double yCoef = (maxY - minY) / size;
        
        String SVG_File = "result/image.svg";
        PrintWriter out = new PrintWriter(new FileWriter(SVG_File));
        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<svg version = \"1.1\"\n" +
                "     baseProfile=\"full\"\n" +
                "     xmlns = \"http://www.w3.org/2000/svg\" \n" +
                "     xmlns:xlink = \"http://www.w3.org/1999/xlink\"\n" +
                "     xmlns:ev = \"http://www.w3.org/2001/xml-events\"\n" +
                "     height = \"" + size + "px\"  width = \"" + size + "px\">\n");
        
        for (Vertex vertex : vertexes)
        	out.println("<circle cx=\"" + (size - (maxX - vertex.getX())/xCoef) + "\" cy=\""+ ((maxY - vertex.getY())/yCoef) +"\" r=\"2px\" fill=\"black\"/>");

        for (Edge edge : edges)
        	out.println("<line x1=\"" + (size - (maxX - edge.getVertex1().getX())/xCoef) + "\" y1=\"" + ((maxY - edge.getVertex1().getY())/yCoef) + "\" x2=\"" + 
        			(size - (maxX - edge.getVertex2().getX())/xCoef) +"\" y2=\"" + ((maxY - edge.getVertex2().getY())/yCoef) + "\" style=\"stroke:rgb(0,0,0);stroke-width:1\" />");
        out.print("</svg>");
        out.close();
	}
	
	public void outputSVG(String fileName, List<Vertex> path, List<List<Vertex>> otherPaths, double x, double y, List<Vertex> points) throws IOException {
		
        double maxX, minX, maxY, minY;
        final double size = 3000.0;
        
        maxX = vertexes.iterator().next().getX();
        minX = vertexes.iterator().next().getX();
        maxY = vertexes.iterator().next().getY();
        minY = vertexes.iterator().next().getY();
        
        for (Vertex vertex : vertexes) {
        	if (vertex.getX() > maxX)
        		maxX = vertex.getX();
        	else if (vertex.getX() < minX)
        		minX = vertex.getX();
        	if (vertex.getY() > maxY)
        		maxY = vertex.getY();
        	else if (vertex.getY() < minY)
        		minY = vertex.getY();
        }
        
        final double xCoef = (maxX - minX) / size;
        final double yCoef = (maxY - minY) / size;
        
        String SVG_File = fileName;
        PrintWriter out = new PrintWriter(new FileWriter(SVG_File));
        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<svg version = \"1.1\"\n" +
                "     baseProfile=\"full\"\n" +
                "     xmlns = \"http://www.w3.org/2000/svg\" \n" +
                "     xmlns:xlink = \"http://www.w3.org/1999/xlink\"\n" +
                "     xmlns:ev = \"http://www.w3.org/2001/xml-events\"\n" +
                "     height = \"" + size + "px\"  width = \"" + size + "px\">\n");
        
        for (Vertex vertex : vertexes)
        	out.println("<circle cx=\"" + (size - (maxX - vertex.getX())/xCoef) + "\" cy=\""+ ((maxY - vertex.getY())/yCoef) +"\" r=\"2px\" fill=\"black\"/>");
        
        for (Edge edge : edges)
        	out.println("<line x1=\"" + (size - (maxX - edge.getVertex1().getX())/xCoef) + "\" y1=\"" + ((maxY - edge.getVertex1().getY())/yCoef) + "\" x2=\"" + 
        			(size - (maxX - edge.getVertex2().getX())/xCoef) +"\" y2=\"" + ((maxY - edge.getVertex2().getY())/yCoef) + "\" style=\"stroke:rgb(0,0,0);stroke-width:1\" />");
         
        out.println("<circle cx=\"" + (size - (maxX - x)/xCoef) + "\" cy=\""+ ((maxY - y)/yCoef) +"\" r=\"5px\" fill=\"red\"/>");
         
        for (List<Vertex> list : otherPaths)
        	for (int i = 0; i < list.size() - 1; i++)
            	out.println("<line x1=\"" + (size - (maxX - list.get(i).getX())/xCoef) + "\" y1=\"" + ((maxY - list.get(i).getY())/yCoef) + "\" x2=\"" + 
            			(size - (maxX - list.get(i + 1).getX())/xCoef) +"\" y2=\"" + ((maxY - list.get(i + 1).getY())/yCoef) + "\" style=\"stroke: aqua; stroke-width:5\" />");
        
        for (int i = 0; i < path.size() - 1; i++)
        	out.println("<line x1=\"" + (size - (maxX - path.get(i).getX())/xCoef) + "\" y1=\"" + ((maxY - path.get(i).getY())/yCoef) + "\" x2=\"" + 
        			(size - (maxX - path.get(i + 1).getX())/xCoef) +"\" y2=\"" + ((maxY - path.get(i + 1).getY())/yCoef) + "\" style=\"stroke: blue; stroke-width:5\" />");
        
        for (Vertex vertex : points)
        	out.println("<circle cx=\"" + (size - (maxX - vertex.getX())/xCoef) + "\" cy=\""+ ((maxY - vertex.getY())/yCoef) +"\" r=\"10px\" fill=\"orange\"/>");
        
        out.print("</svg>");
        out.close();
	}
	
	public DistancesAndPrev Astar(Vertex start, Vertex finish, String heuristic) {
		List<Vertex> listOfVertexes = new ArrayList<Vertex>(vertexes);
		Set<Vertex> closed = new HashSet<Vertex>();
		List<Vertex> open = new ArrayList<Vertex>();
		open.add(start);
		List<Integer> prev = new ArrayList<Integer>();
		for (int i = 0; i < listOfVertexes.size(); i++) {
			prev.add(null);
		}
		List<Double> gScore = new ArrayList<Double>();
		for (int i = 0; i < listOfVertexes.size(); i++) {
			if (start.equals(listOfVertexes.get(i)))
				gScore.add(0.0);
			else
				gScore.add(Double.POSITIVE_INFINITY);
		}
		List<Double> fScoreOpen = new ArrayList<Double>();
		fScoreOpen.add(start.distance(finish, heuristic));
		while (!open.isEmpty()) {
			Vertex currentVertex = open.get(0);
			Double minfScore = fScoreOpen.get(0);
			for (int i = 0; i < open.size(); i++) {
				Double currentfScore = fScoreOpen.get(i);
				if (currentfScore < minfScore) {
					minfScore = currentfScore;
					currentVertex = open.get(i);
				}
			}
			if (currentVertex.equals(finish))
				return new DistancesAndPrev(gScore, prev);
			fScoreOpen.remove(open.indexOf(currentVertex));
			open.remove(currentVertex);
			closed.add(currentVertex);
			int currentInd = listOfVertexes.indexOf(currentVertex);
			for (Vertex succesor : adjacencyList.get(currentInd)) {
				int succesorInd = listOfVertexes.indexOf(succesor);
				if (closed.contains(succesor))
					continue;
				if (!open.contains(succesor)) {
					open.add(succesor);
					fScoreOpen.add(Double.POSITIVE_INFINITY);
				}
				double altScore = gScore.get(currentInd) + currentVertex.distance(succesor);
				if (altScore < gScore.get(succesorInd)) {
					prev.set(succesorInd, currentInd);
					gScore.set(succesorInd, altScore);
					fScoreOpen.set(open.indexOf(succesor), gScore.get(succesorInd) + succesor.distance(finish, heuristic));
				}
			}
		}
		//System.out.println("Failure!");
		return new DistancesAndPrev(new ArrayList<Double>(), new ArrayList<Integer>());
	}
	
	public DistancesAndPrev Levit(Vertex firstVertex) {
		List<Vertex> listOfVertexes = new ArrayList<Vertex>(vertexes);
		List<Integer> prev = new LinkedList<Integer>();
		for (int i = 0; i < listOfVertexes.size(); i++) {
			prev.add(null);
		}
		List<Double> distances = new ArrayList<Double>();
		for (int i = 0; i < listOfVertexes.size(); i++) {
			if (firstVertex.equals(listOfVertexes.get(i)))
				distances.add(new Double(0));
			else
				distances.add(Double.POSITIVE_INFINITY);
		}
		Deque<Vertex> m1 = new ArrayDeque<Vertex>();
		m1.add(firstVertex);
		Deque<Vertex> m11 = new ArrayDeque<Vertex>();
		Set<Vertex> m0 = new HashSet<Vertex>();
		Set<Vertex> m2 = new HashSet<Vertex>();
		for (Vertex vertex : listOfVertexes)
			if (!vertex.equals(firstVertex))
				m2.add(vertex);
		Vertex currentVertex;
		while (!m1.isEmpty() || !m11.isEmpty()) {
			if (m11.isEmpty())
				currentVertex = m1.poll();
			else
				currentVertex = m11.poll();
			int currentVertexInd = listOfVertexes.indexOf(currentVertex);
			for (Vertex succesor : adjacencyList.get(currentVertexInd)) {
				int succesorInd = listOfVertexes.indexOf(succesor);
				double oldDistance = distances.get(succesorInd);
				double newDistance = distances.get(currentVertexInd) + currentVertex.distance(succesor);
				if (m2.contains(succesor)) {
					m1.add(succesor);
					m2.remove(succesor);
					if (newDistance < oldDistance) {
						distances.set(succesorInd, newDistance);
						prev.set(succesorInd, currentVertexInd);
					}
				} else if (m1.contains(succesor) || m11.contains(succesor)) {
					if (newDistance < oldDistance) {
						distances.set(succesorInd, newDistance);
						prev.set(succesorInd, currentVertexInd);
					}
				} else if (m0.contains(succesor) && (newDistance < oldDistance)) {
					  m11.add(succesor);
					  m0.remove(succesor);
					  distances.set(succesorInd, newDistance);
					  prev.set(succesorInd, currentVertexInd);
				  }
			}
			m0.add(currentVertex);
		}
		return new DistancesAndPrev(distances, prev);
	}

	public DistancesAndPrev Dijkstra(Vertex firstVertex) {
		List<Vertex> listOfVertexes = new ArrayList<Vertex>(vertexes);
		List<Integer> prev = new LinkedList<Integer>();
		for (int i = 0; i < listOfVertexes.size(); i++) {
			prev.add(null);
		}
		
		List<Double> distances = new ArrayList<Double>();
		for (int i = 0; i < listOfVertexes.size(); i++) {
			if (firstVertex.equals(listOfVertexes.get(i)))
				distances.add(new Double(0));
			else
				distances.add(Double.POSITIVE_INFINITY);
		}

		List<Boolean> visitedVertexes = new ArrayList<Boolean>();
		for (int i = 0; i < listOfVertexes.size(); i++)
			visitedVertexes.add(false);
		while(true) {
			Integer x = null;
			Double minDistance = Double.POSITIVE_INFINITY;
			for (int i = 0; i < distances.size(); i++) {
				Double dist = distances.get(i);
				if (!visitedVertexes.get(i) && (dist < minDistance) && (!dist.isInfinite())) {
					minDistance = distances.get(i);
					x = i;
				}
			}
			if (x == null)
				break;
			visitedVertexes.set(x, true);
			for (Vertex succ : adjacencyList.get(x)) {
				Double alt = distances.get(x) + listOfVertexes.get(x).distance(succ);
				int succInd = listOfVertexes.indexOf(succ);
				if (alt < distances.get(succInd)) {
					distances.set(succInd, alt);
					prev.set(succInd, x);
				}
			}
		}
		return new DistancesAndPrev(distances, prev);
	}
	
	public List<Vertex> shortestPath(Vertex firstVertex, Vertex finalVertex, List<Integer> prev){
		if (prev.isEmpty())
			return new ArrayList<Vertex>();
		//if (finalVertex == null)
		//	System.out.println("!!");
		//if (firstVertex == null)
		//	System.out.println("!!!");
		List<Vertex> listOfVertexes = new ArrayList<Vertex>(vertexes);
		List<Vertex> path = new ArrayList<Vertex>();
		Vertex currentVertex = finalVertex;
		path.add(finalVertex);
		while (!currentVertex.equals(firstVertex)) {
			if (prev.get(listOfVertexes.indexOf(currentVertex)) == null)
				return new ArrayList<Vertex>();
			currentVertex = listOfVertexes.get(prev.get(listOfVertexes.indexOf(currentVertex)));
			path.add(currentVertex);
		}
		return path;
	}
	
}
