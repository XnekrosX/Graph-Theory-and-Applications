package graph3;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
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
	
	public class DistancesAndPrev {
		List<Double> distances;
		List<Integer> prev;
		DistancesAndPrev(List<Double> distances, List<Integer> prev){
			this.distances = distances;
			this.prev = prev;
		}
	}
	
	public class PathAndObjects {
		List<Vertex> path;
		List<Vertex> objects;
		PathAndObjects(List<Vertex> path, List<Vertex> objects){
			this.path = path;
			this.objects = objects;
		}
	}
	
	public class WeightedEdge {
		Edge edge;
		double weight;
		WeightedEdge(Edge edge, double weight){
			this.edge = edge;
			this.weight = weight;
		}
		public boolean equals(Object obj){
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			WeightedEdge other = (WeightedEdge) obj;
			if (!edge.equals(other.edge))
				return false;
			if (weight != other.weight)
				return false;
			return true;
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
        
        for (Vertex vertex : points) {
        	out.println("<circle cx=\"" + (size - (maxX - vertex.getX())/xCoef) + "\" cy=\""+ ((maxY - vertex.getY())/yCoef) +"\" r=\"10px\" fill=\"orange\" />");
        	out.println("<text x=\"" + (size - (maxX - vertex.getX())/xCoef) + "\" y=\""+ ((maxY - vertex.getY())/yCoef) + "\" font-size=\"8\" > s </text>");
        }
        
        out.print("</svg>");
        out.close();
	}
	
public void outputSVG(String fileName, List<Vertex> path, double x, double y, List<Vertex> points) throws IOException {
		
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
        
        for (int i = 0; i < path.size() - 1; i++)
        	out.println("<line x1=\"" + (size - (maxX - path.get(i).getX())/xCoef) + "\" y1=\"" + ((maxY - path.get(i).getY())/yCoef) + "\" x2=\"" + 
        			(size - (maxX - path.get(i + 1).getX())/xCoef) +"\" y2=\"" + ((maxY - path.get(i + 1).getY())/yCoef) + "\" style=\"stroke: blue; stroke-width:5\" />");
        
        for (int i = 0; i < points.size(); i++) {
        	out.println("<circle cx=\"" + (size - (maxX - points.get(i).getX())/xCoef) + "\" cy=\""+ ((maxY - points.get(i).getY())/yCoef) +"\" r=\"10px\" fill=\"orange\" />");
        	out.println("<text x=\"" + ((size - (maxX - points.get(i).getX())/xCoef)-4) + "\" y=\""+ (((maxY - points.get(i).getY())/yCoef)+4) + "\" font-size=\"16\" >" + i + "</text>");
        }
        
        Vertex vertex = nearestVertex(x, y);
        out.println("<circle cx=\"" + (size - (maxX - vertex.getX())/xCoef) + "\" cy=\""+ ((maxY - vertex.getY())/yCoef) +"\" r=\"10px\" fill=\"green\"/>");
        
        out.print("</svg>");
        out.close();
	}
	
public void outputSVG(String fileName, List<Vertex> path, double x, double y, List<Vertex> points, List<WeightedEdge> wedges) throws IOException {
	
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
    
    for (WeightedEdge wedge : wedges)
    	out.println("<line x1=\"" + (size - (maxX - wedge.edge.getVertex1().getX())/xCoef) + "\" y1=\"" + ((maxY - wedge.edge.getVertex1().getY())/yCoef) + "\" x2=\"" + 
    			(size - (maxX - wedge.edge.getVertex2().getX())/xCoef) +"\" y2=\"" + ((maxY - wedge.edge.getVertex2().getY())/yCoef) + "\" style=\"stroke:rgb(120,0,120);stroke-width:2\" />");
    
    
    out.println("<circle cx=\"" + (size - (maxX - x)/xCoef) + "\" cy=\""+ ((maxY - y)/yCoef) +"\" r=\"5px\" fill=\"red\"/>");
    
    for (int i = 0; i < path.size() - 1; i++)
    	out.println("<line x1=\"" + (size - (maxX - path.get(i).getX())/xCoef) + "\" y1=\"" + ((maxY - path.get(i).getY())/yCoef) + "\" x2=\"" + 
    			(size - (maxX - path.get(i + 1).getX())/xCoef) +"\" y2=\"" + ((maxY - path.get(i + 1).getY())/yCoef) + "\" style=\"stroke: blue; stroke-width:5\" />");
    
    for (int i = 0; i < points.size(); i++) {
    	out.println("<circle cx=\"" + (size - (maxX - points.get(i).getX())/xCoef) + "\" cy=\""+ ((maxY - points.get(i).getY())/yCoef) +"\" r=\"10px\" fill=\"orange\" />");
    	out.println("<text x=\"" + ((size - (maxX - points.get(i).getX())/xCoef)-4) + "\" y=\""+ (((maxY - points.get(i).getY())/yCoef)+4) + "\" font-size=\"16\" >" + i + "</text>");
    }
    
    Vertex vertex = nearestVertex(x, y);
    out.println("<circle cx=\"" + (size - (maxX - vertex.getX())/xCoef) + "\" cy=\""+ ((maxY - vertex.getY())/yCoef) +"\" r=\"10px\" fill=\"green\"/>");
    
    out.print("</svg>");
    out.close();
}
	
	public int numberOfReachableVertexes(Vertex vertex, List<Edge> listOfEdges) {
		Set<Vertex> allVertexes = new HashSet<Vertex>();
		Deque<Vertex> stack = new ArrayDeque<Vertex>();
		allVertexes.add(vertex);
		for (int i = 0; i < 10; i++)
			stack.push(vertex);
		Vertex newVertex = vertex;
		outerloop: while(!stack.isEmpty()) {
			Vertex currentVertex = newVertex;
			for (Edge edge : listOfEdges) {
				if (edge.getVertex1().equals(currentVertex)) {
					if (!allVertexes.contains(edge.getVertex2())) {
						newVertex = edge.getVertex2();
						stack.push(newVertex);
						allVertexes.add(newVertex);
						continue outerloop;
					}
				}
				if (edge.getVertex2().equals(currentVertex)) {
					if (!allVertexes.contains(edge.getVertex1())) {
						newVertex = edge.getVertex1();
						stack.push(newVertex);
						allVertexes.add(newVertex);
						continue outerloop;
					}
				}
			}
			newVertex = stack.pop();
		}
		return allVertexes.size();
	}

	public List<WeightedEdge> distanceMatrix(List<Vertex> objects) {
		List<WeightedEdge> result = new ArrayList<WeightedEdge>();
		List<Vertex> listOfVertexes = new ArrayList<Vertex>(vertexes);
		for (Vertex start : objects) {
			for (Vertex end : objects) {
				if (!start.equals(end)) {
					Vertex nearestStart = nearestVertex(start.getX(), start.getY());
					Vertex nearestEnd = nearestVertex(end.getX(), end.getY());
					DistancesAndPrev dp = Astar(nearestStart, nearestEnd, "e");
					int endInd = listOfVertexes.indexOf(nearestEnd);
		       		double distance = dp.distances.get(endInd);
		       		result.add(new WeightedEdge(new Edge(nearestStart, nearestEnd), distance));
				}
			}
		}
		return result;
	}
	
	public List<WeightedEdge> minimumSpanningTree(List<Vertex> objects) {
		List<Vertex> nearestObjects = new ArrayList<Vertex>();
		for (Vertex object : objects)
			nearestObjects.add(nearestVertex(object.getX(), object.getY()));
		List<WeightedEdge> result = new ArrayList<WeightedEdge>();
		List<WeightedEdge> allEdges = distanceMatrix(nearestObjects);
				nearestObjects.remove(0);
		while (!nearestObjects.isEmpty()) {
			double minimumWeight = Double.POSITIVE_INFINITY;
			WeightedEdge minimumEdge = null;
			for (WeightedEdge we : allEdges) {
				if (nearestObjects.contains(we.edge.getVertex1()) || (nearestObjects.contains(we.edge.getVertex2()))) {
					if (!nearestObjects.contains(we.edge.getVertex1()) || (!nearestObjects.contains(we.edge.getVertex2()))) {
						if (we.weight < minimumWeight) {
							minimumWeight = we.weight;
							minimumEdge = we;
						}
					}
				}
			}
			result.add(minimumEdge);
			if (nearestObjects.contains(minimumEdge.edge.getVertex1())) {
				nearestObjects.remove(minimumEdge.edge.getVertex1());
			}
			else if (nearestObjects.contains(minimumEdge.edge.getVertex2())) {
				nearestObjects.remove(minimumEdge.edge.getVertex2());
			}
		}
		return result;
	}
	
	public PathAndObjects DoubleMinSpanTreeAlg(Vertex firstVertex, List<Vertex> objects) {
		List<Vertex> path = new ArrayList<Vertex>();
		List<Vertex> nearestObjects = new ArrayList<Vertex>();
		for (Vertex objct : objects)
			nearestObjects.add(nearestVertex(objct.getX(), objct.getY()));
		List<Vertex> newObjects = new ArrayList<Vertex>();
		nearestObjects.add(firstVertex);
		List<WeightedEdge> minSpanTree = minimumSpanningTree(nearestObjects);
		List<WeightedEdge> doubleMinSpanTree = new ArrayList<WeightedEdge>();
		for (WeightedEdge we : minSpanTree) {
			doubleMinSpanTree.add(we);
			doubleMinSpanTree.add(we);
		}
		Vertex currentVertex = firstVertex;
		newObjects.add(currentVertex);
		while (!doubleMinSpanTree.isEmpty()) {
			boolean isABridge = true;
			boolean newIsABridge = false;
			WeightedEdge currentEdge = null;
			int reachableVert = 0;
			for (WeightedEdge we : doubleMinSpanTree) {
				if ((we.edge.getVertex1().equals(currentVertex)) || (we.edge.getVertex2().equals(currentVertex))) {
					List<Edge> newSet = new ArrayList<Edge>();
					for (int i = 0; i < doubleMinSpanTree.size(); i++)
						newSet.add(doubleMinSpanTree.get(i).edge);
					reachableVert = numberOfReachableVertexes(currentVertex, newSet);
					newSet.remove(we.edge);
					int newReachableVert = numberOfReachableVertexes(currentVertex, newSet);
					if (newReachableVert < reachableVert)
						newIsABridge = true;
					else
						newIsABridge = false;
					if ((currentEdge == null) || (isABridge && !newIsABridge)) {
						currentEdge = we;
						isABridge = newIsABridge;
					}
				}
			}
			if (currentEdge.edge.getVertex1().equals(currentVertex))
				currentVertex = currentEdge.edge.getVertex2();
			else
				currentVertex = currentEdge.edge.getVertex1();
			doubleMinSpanTree.remove(currentEdge);
			newObjects.add(currentVertex);
		}
		List<Vertex> section = null;
		List<Vertex> allVertexes = new ArrayList<Vertex>();
		allVertexes.add(firstVertex);
		for (int i = 0; i < newObjects.size() - 1; i++) {
			Vertex thisVertex = newObjects.get(i);
			Vertex nextVertex = null;
			while (nextVertex == null) {
				if (!allVertexes.contains(newObjects.get(i + 1))) {
					nextVertex = newObjects.get(i + 1);
					allVertexes.add(nextVertex);
				}
				else if (newObjects.size() == i + 2) {
					nextVertex = newObjects.get(i + 1);
					allVertexes.add(nextVertex);
				}
				else
					i++;
			}
			DistancesAndPrev dp = Astar(thisVertex, nextVertex, "e");
			section = shortestPath(thisVertex, nextVertex, dp.prev);
			Collections.reverse(section);
			path.addAll(section);
		}
		return new PathAndObjects(path, allVertexes);
	}
	
	public PathAndObjects NearestNeighbourAlg (Vertex firstVertex, List<Vertex> objcts) {
		List<Vertex> listOfVertexes = new ArrayList<Vertex>(vertexes);
		List<Vertex> path = new ArrayList<Vertex>();
		List<Vertex> objects = new ArrayList<Vertex>(objcts);
		List<Vertex> newObjects = new ArrayList<Vertex>();
		newObjects.add(firstVertex);
		Vertex start = new Vertex(firstVertex);
		while (!objects.isEmpty()) {
			List<DistancesAndPrev> dp = new ArrayList<DistancesAndPrev>();
			for (Vertex object : objects) {
				Vertex nearestVertex = nearestVertex(object.getX(), object.getY());
				dp.add(Astar(start, nearestVertex, "e"));
			}
			Vertex finishVertex = nearestVertex(objects.get(0).getX(), objects.get(0).getY());
			double minDistance = dp.get(0).distances.get(listOfVertexes.indexOf(nearestVertex(objects.get(0).getX(), objects.get(0).getY())));
			int objectInd = 0;
			for (int i = 0; i < dp.size(); i++) {
				if (dp.get(i).distances.isEmpty())
					continue;
				Vertex nearestVertex = nearestVertex(objects.get(i).getX(), objects.get(i).getY());
				int vertexInd = listOfVertexes.indexOf(nearestVertex);
				double newDistance = dp.get(i).distances.get(vertexInd);
				if (newDistance < minDistance) {
					minDistance = newDistance;
					finishVertex = nearestVertex;
					objectInd = i;
				}
			}
			List<Vertex> section = shortestPath(start, finishVertex, dp.get(objectInd).prev);
			Collections.reverse(section);
			path.addAll(section);
			newObjects.add(nearestVertex(objects.get(objectInd).getX(), objects.get(objectInd).getY()));
			objects.remove(objectInd);
			start = finishVertex;
		}
		DistancesAndPrev dap = Astar(start, firstVertex, "e");
		List<Vertex> section = shortestPath(start, firstVertex, dap.prev);
		Collections.reverse(section);
		path.addAll(section);
		return new PathAndObjects(path, newObjects);
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