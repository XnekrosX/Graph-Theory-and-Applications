package graphProj;

import java.util.ArrayList;
import java.io.*;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class graphProj {

	public static void main(String[] args) {
		PrintWriter pw = new PrintWriter(System.out, true);
		
		String inFileName = "map.osm";
		String outFileName = "result/out.csv";
		String mode = null;
		for (int i = 0; i < args.length; i++) {
			switch (args[i]) {
				case ("-h"):
				case ("h"): 
				case ("help"): {
					pw.println("List of commands:\n"
						+ "-h, h, help: print all available commands\n"
						+ "-f, f, file: specify .osm file name (default is map.osm)\n"
						+ "-o, o, out: specify output .csv file name (default is out.csv)\n"
						+ "-l, l, list: output adjacency list (default)\n"
						+ "-m, m, matrix: output adjacency matrix\n"
						+ "-i, i, image: output svg image\n");
					return;
				}
				case ("-f"):
				case ("f"):
				case ("file"): {
					if (i < args.length - 1) {
						inFileName = args[i++ + 1];
					} else {
						pw.println("You should specify input file name!");
						return;
					}
					break;
				}
				case ("-o"):
				case ("o"):
				case ("out"): {
					if (i < args.length - 1) {
						outFileName = args[i++ + 1];
					} else {
						pw.println("You should specify output file name!");
						return;
					}
					break;
				}
				case ("-l"):
				case ("l"):
				case ("list"): {
					if (mode == null) {
						mode = "list";
					} else {
						pw.println("You can output only adjacency list or adjacency matrix or svg image!");
						return;
					}
					break;
				}
				case ("-m"):
				case ("m"):
				case ("matrix"): {
					if (mode == null) {
						mode = "matrix";
					} else {
						pw.println("You can output only adjacency list or adjacency matrix or svg image!");
						return;
					}
					break;
				}
				case ("-i"):
				case ("i"):
				case ("image"): {
					if (mode == null) {
						mode = "image";
					} else {
						pw.println("You can output only adjacency list or adjacency matrix or svg image!");
						return;
					}
					break;
				}
			}
		}
        Graph g = parseXML(inFileName);
        pw.println("parsing done!");
        if (mode == null)
        	mode = "list";
        switch (mode) {
        	case "list": {
        		try {
        			g.outputAdjacencyList(outFileName);
        			pw.println("Adjacency list exported!");
        		} catch (IOException e) {
        			e.printStackTrace();
        		}
        		break;
        	}
        	case "matrix": {
        		try {
        			g.outputAdjacencyMatrix(outFileName);
        			pw.println("Adjacency matrix exported!");
        		} catch (IOException e) {
        			e.printStackTrace();
        		}
        		break;
        	}
        	case "image": {
        		try {
        			g.outputSVG();
        			pw.println("SVG image exported!");
        		} catch (IOException e) {
        			e.printStackTrace();
        		}
        		break;
        	}
        }
        pw.println("Done!");
        
	}
	
	private static Graph parseXML(String fileName) {
        Set<Edge> edgeList = new HashSet<>();
        Set<Vertex> allNodes = new HashSet<>();
        Set<Vertex> vertexList = new HashSet<>();
        List<Vertex> vertexes = new ArrayList<>();
        
        double curr_lat = 0, curr_lon = 0;
        long curr_id = 0;
        
        boolean isRoad = false;
        
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        try {
            XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(new FileInputStream(fileName));
            while(xmlEventReader.hasNext()){
               XMLEvent xmlEvent = xmlEventReader.nextEvent();
               if (xmlEvent.isStartElement()){
                   StartElement startElement = xmlEvent.asStartElement();
                   if(startElement.getName().getLocalPart().equals("node")) {
                	   Iterator<Attribute> attribute = xmlEvent.asStartElement().getAttributes();
                	   while(attribute.hasNext()) {
                		   Attribute myAttribute = attribute.next();
                		   if ((myAttribute.getName().toString().equals("id"))){
                			   String value = myAttribute.getValue();
                			   curr_id = Long.parseLong(value);
                		   } else if ((myAttribute.getName().toString().equals("lat"))){
                			   String value = myAttribute.getValue();
                			   curr_lat = Double.parseDouble(value);
                		   } else if ((myAttribute.getName().toString().equals("lon"))){
                			   String value = myAttribute.getValue();
                			   curr_lon = Double.parseDouble(value);
                		   }
                	   }
                   } else if(startElement.getName().getLocalPart().equals("nd")) {
                	   Iterator<Attribute> attribute = xmlEvent.asStartElement().getAttributes();
                	   while(attribute.hasNext()) {
                		   Attribute myAttribute = attribute.next();
                		   if ((myAttribute.getName().toString().equals("ref"))){
                			   String value = myAttribute.getValue();
                			   vertexes.add(new Vertex(Long.valueOf(value)));
                		   }
                	   }
                   } else if(startElement.getName().getLocalPart().equals("tag")) {
                	   Iterator<Attribute> attribute = xmlEvent.asStartElement().getAttributes();
                	   while(attribute.hasNext()) {
                		   Attribute myAttribute = attribute.next();
                		   if ((myAttribute.getName().toString().equals("v"))){
                			   String value = myAttribute.getValue();
                			   if (value.equals("motorway") || value.equals("trunk") || value.equals("primary") 
                			   || value.equals("secondary") || value.equals("tertiary") || value.equals("unclassified") 
                			   || value.equals("motorway_link") || value.equals("trunk_link") || value.equals("primary_link")
                			    || value.equals("secondary_link") || value.equals("tertiary_link") || value.equals("unclassified_link")
                			   || value.equals("residential")) {
                				   isRoad = true;
                			   }
                		   }
                	   }
                   }
               }
               
               if(xmlEvent.isEndElement())
                   if (xmlEvent.asEndElement().getName().getLocalPart().equals("way")) {
                	   if (isRoad) {
                		   for (int i = 0; i < vertexes.size(); i++)
                			   if (allNodes.contains(vertexes.get(i)))
                				   for (Iterator<Vertex> it = allNodes.iterator(); it.hasNext();) {
                					   Vertex f = it.next();
                					   if (f.equals(vertexes.get(i)))
                						   vertexes.set(i, f);
                			    }
                		   vertexList.addAll(vertexes);
                		   for (int i = 0; i < vertexes.size() - 1; i++) {
                			   edgeList.add(new Edge(vertexes.get(i), vertexes.get(i + 1)));
                		   }
                		   isRoad = false;
                	   }
                	   vertexes.clear();
                   } else if (xmlEvent.asEndElement().getName().getLocalPart().equals("node"))
                	   allNodes.add(new Vertex(curr_id, curr_lat, curr_lon));
            }
            
        } catch (FileNotFoundException | XMLStreamException e) {
            e.printStackTrace();
        }
        return new Graph(vertexList, edgeList);
    }
}
