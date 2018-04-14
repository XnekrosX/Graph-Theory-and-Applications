package graph2;

import javafx.application.*;
import javafx.scene.*;
import javafx.stage.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.event.*;
//import javafx.geometry.*;
//import javafx.geometry.Pos;
import javafx.geometry.Insets;

import java.util.ArrayList;
import java.io.*;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class graphProj2 extends Application {
	
	File map;
	
	Graph graph;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	private static Graph parseXML(File file) {
        Set<Edge> edgeList = new HashSet<>();
        Set<Vertex> allNodes = new HashSet<>();
        Set<Vertex> vertexList = new HashSet<>();
        List<Vertex> vertexes = new ArrayList<>();
        
        double curr_lat = 0, curr_lon = 0;
        long curr_id = 0;
        
        boolean isRoad = false;
        
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        try {
            XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(new FileInputStream(file));
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
	
	public class Coordinates {
		public double x;
		public double y;
		
		Coordinates(double lat, double lon){
			double rLat = Math.toRadians(lat);
			double rLon = Math.toRadians(lon);
			double a = 6378137.0;
			double b = 6356752.3142;
			double f = (a - b) / a;
			double e = Math.sqrt(2 * f - Math.pow(f, 2));
			x = a * rLon;
			y = a * Math.log(Math.PI / 4 + rLat / 2) * ((1 - e * Math.sin(rLat)) / Math.pow((1 + e * Math.sin(rLat)), (e / 2)));
		}
	}
	
	Vertex startVertex = null;
	Coordinates startCoord = null;
	List<Vertex> hospitals = new ArrayList<Vertex>();
	
	public void start(Stage myStage) {
		myStage.setTitle("Title");
		
		Label openMapLabel = new Label("Choose .osm file: ");
		Label statusLabel = new Label("");
		Label exportLabel = new Label("Export: ");
		Label latLabel = new Label("Latitude: ");
		Label lonLabel = new Label("Longitude: ");
		Label latMinMaxLabel = new Label("");
		Label lonMinMaxLabel = new Label("");
		
		Button openMapBtn = new Button("Open");
		Button parseMapBtn = new Button("Parse");
		Button exportListBtn = new Button("List");
		Button exportMatrixBtn = new Button("Matrix");
		Button exportImageBtn = new Button("Image");
		Button setStartBtn = new Button("Set");
		Button dijkstraBtn = new Button("Dijkstra");
		Button levitBtn = new Button("Levit");
		Button astarBtn = new Button("A Star");
		
		TextField latField = new TextField ();
		TextField lonField = new TextField ();
		
		parseMapBtn.setDisable(true);
		exportListBtn.setDisable(true);
		exportMatrixBtn.setDisable(true);
		exportImageBtn.setDisable(true);
		setStartBtn.setDisable(true);
		dijkstraBtn.setDisable(true);
		levitBtn.setDisable(true);
		astarBtn.setDisable(true);
		
		latField.setDisable(true);
		lonField.setDisable(true);
		
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open map");
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("osm", "*.osm"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
            );
		
		hospitals.add(new Vertex(1, 43.027807, 44.659144));
		hospitals.add(new Vertex(2, 43.042416, 44.679376));
		hospitals.add(new Vertex(3, 43.030917, 44.712085));
		hospitals.add(new Vertex(4, 43.067759, 44.661631));
		hospitals.add(new Vertex(5, 43.062304, 44.744694));
		hospitals.add(new Vertex(6, 43.040180, 44.632376));
		hospitals.add(new Vertex(7, 43.020088, 44.640005));
		hospitals.add(new Vertex(8, 43.053748, 44.667499));
		hospitals.add(new Vertex(9, 43.038693, 44.672031));
		hospitals.add(new Vertex(10, 43.017310, 44.676089));
		
		//event handler for dijkstraBtn
		dijkstraBtn.setOnAction(new EventHandler<ActionEvent>() {
			public void handle (final ActionEvent ae) {
				List<Vertex> listOfVertexes = new ArrayList<Vertex>(graph.vertexes);
				Graph.DistancesAndPrev dp;
				Long time1 = System.nanoTime();
				dp = graph.Dijkstra(startVertex);
		       	Long time2 = System.nanoTime();
		       	Vertex finishVertex = hospitals.get(0);
		       	double minDistance = dp.distances.get(listOfVertexes.indexOf(graph.nearestVertex(hospitals.get(0).getX(), hospitals.get(0).getY())));
		       	for (Vertex hospital : hospitals) {
		       		Vertex nearestVertex = graph.nearestVertex(hospital.getX(), hospital.getY());
		       		int vertexInd = listOfVertexes.indexOf(nearestVertex);
		       		double newDistance = dp.distances.get(vertexInd);
		       		if (newDistance < minDistance) {
		       			minDistance = newDistance;
		       			finishVertex = nearestVertex;
		       		}
		       	}
		       	List<Vertex> path = graph.shortestPath(startVertex, finishVertex, dp.prev);
		        System.out.println(path.size() + " : size of the path");
		       	try {
					graph.outputSVG("result/dijkstraPath.svg", path, startCoord.x, startCoord.y, hospitals);
				} catch (IOException e) {
					e.printStackTrace();
				}
		       	statusLabel.setText((time2 - time1)*1e-9 + " is elapsed");
			}
		});
		//event handler for levitBtn
		levitBtn.setOnAction(new EventHandler<ActionEvent>() {
			public void handle (final ActionEvent ae) {
				List<Vertex> listOfVertexes = new ArrayList<Vertex>(graph.vertexes);
				Graph.DistancesAndPrev dp;
				Long time1 = System.nanoTime();
				dp = graph.Levit(startVertex);
		       	Long time2 = System.nanoTime();
		       	Vertex finishVertex = hospitals.get(0);
		       	double minDistance = dp.distances.get(listOfVertexes.indexOf(graph.nearestVertex(hospitals.get(0).getX(), hospitals.get(0).getY())));
		       	for (Vertex hospital : hospitals) {
		       		Vertex nearestVertex = graph.nearestVertex(hospital.getX(), hospital.getY());
		       		int vertexInd = listOfVertexes.indexOf(nearestVertex);
		       		double newDistance = dp.distances.get(vertexInd);
		       		if (newDistance < minDistance) {
		       			minDistance = newDistance;
		       			finishVertex = nearestVertex;
		       		}
		       	}
		       	List<Vertex> path = graph.shortestPath(startVertex, finishVertex, dp.prev);
		        System.out.println(path.size() + " : size of the path");
		       	try {
					graph.outputSVG("result/levitPath.svg", path, startCoord.x, startCoord.y, hospitals);
				} catch (IOException e) {
					e.printStackTrace();
				}
		       	statusLabel.setText((time2 - time1)*1e-9 + " is elapsed");
			}
		});
		//event handler for astarBtn
		astarBtn.setOnAction(new EventHandler<ActionEvent>() {
			public void handle (final ActionEvent ae) {
				List<Vertex> listOfVertexes = new ArrayList<Vertex>(graph.vertexes);
				List<Graph.DistancesAndPrev> dp = new ArrayList<Graph.DistancesAndPrev>();
				Long time1 = System.nanoTime();
				for (Vertex hospital : hospitals) {
					Vertex nearestVertex = graph.nearestVertex(hospital.getX(), hospital.getY());
					dp.add(graph.Astar(startVertex, nearestVertex));
				}
		       	Long time2 = System.nanoTime();
		       	Vertex finishVertex = hospitals.get(0);
		       	double minDistance = dp.get(0).distances.get(listOfVertexes.indexOf(graph.nearestVertex(hospitals.get(0).getX(), hospitals.get(0).getY())));
		       	int hospitalInd = 0;
		       	for (int i = 0; i < dp.size(); i++) {
		       		if (dp.get(i).distances.isEmpty())
		       			continue;
		       		Vertex nearestVertex = graph.nearestVertex(hospitals.get(i).getX(), hospitals.get(i).getY());
		       		int vertexInd = listOfVertexes.indexOf(nearestVertex);
		       		double newDistance = dp.get(i).distances.get(vertexInd);
		       		if (newDistance < minDistance) {
		       			minDistance = newDistance;
		       			finishVertex = nearestVertex;
		       			hospitalInd = i;
		       		}
		       	}
				List<Vertex> path = graph.shortestPath(startVertex, finishVertex, dp.get(hospitalInd).prev);
		        System.out.println(path.size() + " : size of the path");
		       	try {
					graph.outputSVG("result/astarPath.svg", path, startCoord.x, startCoord.y, hospitals);
				} catch (IOException e) {
					e.printStackTrace();
				}
		       	statusLabel.setText((time2 - time1)*1e-9 + " is elapsed");
			}
		});
		//event handler for openMapBtn
		openMapBtn.setOnAction(new EventHandler<ActionEvent>() {
			public void handle (final ActionEvent ae) {
				map = fileChooser.showOpenDialog(myStage);
				statusLabel.setText("");
				exportListBtn.setDisable(true);
				exportMatrixBtn.setDisable(true);
				exportImageBtn.setDisable(true);
				dijkstraBtn.setDisable(true);
				levitBtn.setDisable(true);
				astarBtn.setDisable(true);
                if (map != null) {
                    parseMapBtn.setDisable(false);
                    statusLabel.setText("File opened");
                } else
                	parseMapBtn.setDisable(true);
			}
		});
		//event handler for parseMapBtn
		parseMapBtn.setOnAction(new EventHandler<ActionEvent>() {
			public void handle (final ActionEvent ae) {
				graph = parseXML(map);
				statusLabel.setText("Parsing done");
				latMinMaxLabel.setText("min Lat: " + graph.getMinLat() + ", max Lat: " + graph.getMaxLat());
				lonMinMaxLabel.setText("min Lon: " + graph.getMinLon() + ", max Lon: " + graph.getMaxLon());
				exportListBtn.setDisable(false);
				exportMatrixBtn.setDisable(false);
				exportImageBtn.setDisable(false);
				latField.setDisable(false);
				lonField.setDisable(false);
				setStartBtn.setDisable(false);
			}
		});
		////event handler for setStartBtn
		setStartBtn.setOnAction(new EventHandler<ActionEvent>() {
			public void handle (final ActionEvent ae) {
				if (!latField.getText().isEmpty() && !lonField.getText().isEmpty()) {
					Double lat = Double.valueOf(latField.getText());
					Double lon = Double.valueOf(lonField.getText());
					if (!lat.isNaN() && !lon.isNaN() && (lat >= graph.minLat) 
							&& (lat <= graph.maxLat) && (lon >= graph.minLon) && (lon <= graph.maxLon)) {
						startCoord = new Coordinates(lat, lon);
						startVertex = graph.nearestVertex(startCoord.x, startCoord.y);
						statusLabel.setText("Start node set");
						dijkstraBtn.setDisable(false);
						levitBtn.setDisable(false);
						astarBtn.setDisable(false);
						return;
					}
				}
					statusLabel.setText("Incorrect input");
					dijkstraBtn.setDisable(true);
					levitBtn.setDisable(true);
					astarBtn.setDisable(true);
			}
		});
		//event handler for exportListBtn
		exportListBtn.setOnAction(new EventHandler<ActionEvent>() {
			public void handle (final ActionEvent ae) {
				try {
        			graph.outputAdjacencyList("result/list.csv");
        			statusLabel.setText("Adjacency list exported!");
        		} catch (IOException e) {
        			e.printStackTrace();
        		}
			}
		});
		//event handler for exportMatrixBtn
		exportMatrixBtn.setOnAction(new EventHandler<ActionEvent>() {
			public void handle (final ActionEvent ae) {
				try {
		        	graph.outputAdjacencyMatrix("result/matrix.csv");
		        	statusLabel.setText("Adjacency matrix exported!");
		        	} catch (IOException e) {
		        		e.printStackTrace();
		        	}
				}
			});
		//event handler for exportImageBtn
		exportImageBtn.setOnAction(new EventHandler<ActionEvent>() {
			public void handle (final ActionEvent ae) {
				try {
		        	graph.outputSVG();
		        	statusLabel.setText("Image exported!");
		       	} catch (IOException e) {
		       		e.printStackTrace();
		       	}
			}
		});
		
		final GridPane inputGridPane = new GridPane();
		
		//let the nodes grow in order for them to respect the request to grow that the grid pane make
		openMapBtn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		parseMapBtn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		exportListBtn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		exportMatrixBtn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		exportImageBtn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		dijkstraBtn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		levitBtn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		astarBtn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		
		//specify column constraints
		for (int colIndex = 0; colIndex < 4; colIndex++) {
		    ColumnConstraints cc = new ColumnConstraints();
		    cc.setHgrow(Priority.ALWAYS) ; // allow column to grow
		    cc.setFillWidth(true); // ask nodes to fill space for column
		    inputGridPane.getColumnConstraints().add(cc);
		}
		
		//specifying places of nodes in the grid
		GridPane.setConstraints(openMapLabel, 0, 1);
        GridPane.setConstraints(openMapBtn, 1, 1);
        GridPane.setConstraints(parseMapBtn, 2, 1);
        GridPane.setConstraints(exportLabel, 0, 2);
        GridPane.setConstraints(exportListBtn, 1, 2);
        GridPane.setConstraints(exportMatrixBtn, 2, 2);
        GridPane.setConstraints(exportImageBtn, 3, 2);
        GridPane.setConstraints(latLabel, 0, 3);
        GridPane.setConstraints(latField, 1, 3, 2, 1);
        GridPane.setConstraints(lonLabel, 0, 4);
        GridPane.setConstraints(lonField, 1, 4, 2, 1);
        GridPane.setConstraints(setStartBtn, 3, 3, 2, 2);
        GridPane.setConstraints(dijkstraBtn, 1, 5);
        GridPane.setConstraints(levitBtn, 2, 5);
        GridPane.setConstraints(astarBtn, 3, 5);
        GridPane.setConstraints(latMinMaxLabel, 0, 6, 4, 1);
        GridPane.setConstraints(lonMinMaxLabel, 0, 7, 4, 1);
        GridPane.setConstraints(statusLabel, 0, 8, 4, 1);
        
        //setting gaps
        inputGridPane.setHgap(6);
        inputGridPane.setVgap(6);
        
        //adding nodes into the grid
        inputGridPane.getChildren().addAll(openMapLabel, openMapBtn, parseMapBtn, exportLabel, 
        		exportListBtn, exportMatrixBtn, exportImageBtn, latLabel, lonLabel, latField,
        		lonField, setStartBtn, dijkstraBtn, levitBtn, astarBtn, latMinMaxLabel, 
        		lonMinMaxLabel, statusLabel);

        final Pane rootGroup = new VBox(12);
        rootGroup.getChildren().addAll(inputGridPane);
        rootGroup.setPadding(new Insets(12, 12, 12, 12));
		
		Scene myScene = new Scene(rootGroup, 300, 250);
		
		myStage.setScene(myScene);
		
		myStage.show();
	}
	
}
