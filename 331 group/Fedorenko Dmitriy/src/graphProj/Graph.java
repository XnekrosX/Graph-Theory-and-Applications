package graphProj;

import java.util.ArrayList;
import java.util.Set;
import java.io.*;

public class Graph {
	
	Set<Vertex> vertexes;
	Set<Edge> edges;
	
	public Graph(Set<Vertex> vertexes, Set<Edge> edges){
		this.vertexes = vertexes;
		this.edges = edges;
	}
	
	public void outputAdjacencyList(String filename) throws IOException {
		try(FileWriter fout = new FileWriter(filename)) {
			for (Vertex vertex : vertexes) {
				fout.write((new Long(vertex.getId())).toString());
				for (Edge edge : edges) {
					if (vertex.equals(edge.getVertex1()))
						fout.write("," + (new Long(edge.getVertex2().getId())).toString());
					if (vertex.equals(edge.getVertex2()))
						fout.write("," + (new Long(edge.getVertex1().getId())).toString());
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
	
	public void outputSVG() throws IOException {
		
        double maxLat = -90, minLat = 90, maxLon = -180, minLon = 180;
        final double size = 3000.0;
        
        for (Vertex vertex : vertexes) {
        	if (vertex.getLatitude() > maxLat)
        		maxLat = vertex.getLatitude();
        	else if (vertex.getLatitude() < minLat)
        		minLat = vertex.getLatitude();
        	if (vertex.getLongitude() > maxLon)
        		maxLon = vertex.getLongitude();
        	else if (vertex.getLongitude() < minLon)
        		minLon = vertex.getLongitude();
        }

        final double latCoef = (maxLat - minLat) / size;
        final double lonCoef = (maxLon - minLon) / size;
        
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
        	out.println("<circle cx=\"" + (size-(maxLon - vertex.getLongitude())/lonCoef) + "\" cy=\""+ (maxLat - vertex.getLatitude())/latCoef +"\" r=\"2px\" fill=\"black\"/>");

        for (Edge edge : edges)
        	out.println("<line x1=\"" + (size-(maxLon - edge.getVertex1().getLongitude())/lonCoef) + "\" y1=\"" + (maxLat - edge.getVertex1().getLatitude())/latCoef + "\" x2=\"" + 
        			(size-(maxLon - edge.getVertex2().getLongitude())/lonCoef) +"\" y2=\"" + (maxLat - edge.getVertex2().getLatitude())/latCoef + "\" style=\"stroke:rgb(0,0,0);stroke-width:1\" />");
        out.print("</svg>");
        out.close();
	}
	
}
