package me.gorgeousone.deathray.svg;

import org.bukkit.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SvgParser {
	
	public static void main(String[] args) {
		String svgFilePath = "rune_circle.svg";
		List<Circle> circles;
		List<Path> paths;
		
		try {
			SvgParser svgParser = new SvgParser();
			circles = svgParser.parseSvg(svgFilePath).getCircles();
			paths = svgParser.parseSvg(svgFilePath).getPaths();
			
			System.out.println("Circles:");
			for (Circle circle : circles) {
				System.out.println(circle);
			}
			
			System.out.println("\nPaths:");
			for (Path path : paths) {
				System.out.println(path);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public SvgData parseSvg(String svgFilePath) throws Exception {
		File file = new File(svgFilePath);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(file);
		doc.getDocumentElement().normalize();
		
		List<Circle> circles = extractCircles(doc);
		List<Path> paths = extractPaths(doc);
		
		return new SvgData(circles, paths);
	}
	
	private List<Circle> extractCircles(Document doc) {
		List<Circle> circles = new ArrayList<>();
		NodeList circleList = doc.getElementsByTagName("circle");
		
		for (int i = 0; i < circleList.getLength(); i++) {
			Element circleElement = (Element) circleList.item(i);
			double cx = Double.parseDouble(circleElement.getAttribute("cx"));
			double cy = Double.parseDouble(circleElement.getAttribute("cy"));
			double r = Double.parseDouble(circleElement.getAttribute("r"));
			Circle circle = new Circle(cx, cy, r);
			circle.setWidth(Double.parseDouble(circleElement.getAttribute("stroke-width")));
			circles.add(circle);
		}
		return circles;
	}
	
	private List<Path> extractPaths(Document doc) {
		List<Path> paths = new ArrayList<>();
		NodeList pathList = doc.getElementsByTagName("path");
		
		for (int i = 0; i < pathList.getLength(); i++) {
			Element pathElement = (Element) pathList.item(i);
			String d = pathElement.getAttribute("d");
			Path path = parsePathToVectors(d);
			path.setWidth(Double.parseDouble(pathElement.getAttribute("stroke-width")));
			paths.add(path);
		}
		
		return paths;
	}
	
	private Path parsePathToVectors(String d) {
		Path nodes = new Path();
		String[] pathCommands = d.split("\\s+");
		char lastCommand = 'M';
		
		for (String command : pathCommands) {
			if (command.matches("[A-Za-z]")) {
				lastCommand = command.charAt(0);
				continue;
			}
			double x, y;
			switch (lastCommand) {
				case 'M':
				case 'L':
					String[] xy = command.split(",");
					x = Double.parseDouble(xy[0]);
					y = Double.parseDouble(xy[1]);
					break;
				case 'V':
					x = nodes.get(nodes.size() - 1).getX();
					y = Double.parseDouble(command);
					break;
				case 'H':
					x = Double.parseDouble(command);
					y = nodes.get(nodes.size() - 1).getY();
					break;
				case 'Z':
					x = nodes.get(0).getX();
					y = nodes.get(0).getY();
					break;
				default:
					throw new IllegalArgumentException("Unknown path command: " + lastCommand);
			}
			nodes.add(new Vector(x, y, 0));
		}
		return nodes;
	}
	
	private static class SvgData {
		private final List<Circle> circles;
		private final List<Path> paths;
		
		public SvgData(List<Circle> circles, List<Path> paths) {
			this.circles = circles;
			this.paths = paths;
		}
		
		public List<Circle> getCircles() {
			return circles;
		}
		
		public List<Path> getPaths() {
			return paths;
		}
	}
}
