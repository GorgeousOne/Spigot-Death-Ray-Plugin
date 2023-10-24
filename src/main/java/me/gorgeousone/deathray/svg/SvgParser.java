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
	
	public static SvgData parseSvg(File svgFile, double density) throws Exception {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(svgFile);
		doc.getDocumentElement().normalize();
		
		List<Circle> circles = extractCircles(doc);
		List<Path> paths = extractPaths(doc);
		return new SvgData(circles, paths);
		
	}
	
	private static List<Circle> extractCircles(Document doc) {
		List<Circle> circles = new ArrayList<>();
		NodeList circleList = doc.getElementsByTagName("circle");
		
		for (int i = 0; i < circleList.getLength(); i++) {
			Element circleElement = (Element) circleList.item(i);
			double cx = Double.parseDouble(circleElement.getAttribute("cx"));
			double cz = Double.parseDouble(circleElement.getAttribute("cy"));
			double r = Double.parseDouble(circleElement.getAttribute("r"));
			Circle circle = new Circle(cx, cz, r);
			circle.setWidth(getWidth(circleElement));
			circles.add(circle);
		}
		return circles;
	}
	
	private static double getWidth(Element element) {
		String[] style = element.getAttribute("style").split(";");
		for (String s : style) {
			if (s.startsWith("stroke-width")) {
				System.out.println(s);
				return Double.parseDouble(s.split(":")[1]);
			}
		}
		return 1;
	}
	
	private static List<Path> extractPaths(Document doc) {
		List<Path> paths = new ArrayList<>();
		NodeList pathList = doc.getElementsByTagName("path");
		
		for (int i = 0; i < pathList.getLength(); i++) {
			Element pathElement = (Element) pathList.item(i);
			String d = pathElement.getAttribute("d");
			Path path = parsePathToVectors(d);
			path.setWidth(getWidth(pathElement));
			paths.add(path);
		}
		return paths;
	}
	
	private static Path parsePathToVectors(String d) {
		Path nodes = new Path();
		String[] pathCommands = d.split("\\s+");
		char lastCommand = 'M';
		
		for (String command : pathCommands) {
			if (command.matches("[A-Y]")) {
				lastCommand = command.charAt(0);
				continue;
			}
			if (command.equals("Z")) {
				nodes.add(nodes.get(0).clone());
				continue;
			}
			double x, z;
			switch (lastCommand) {
				case 'M':
				case 'L':
					String[] xy = command.split(",");
					x = Double.parseDouble(xy[0]);
					z = Double.parseDouble(xy[1]);
					break;
				case 'V':
					x = nodes.get(nodes.size() - 1).getX();
					z = Double.parseDouble(command);
					break;
				case 'H':
					x = Double.parseDouble(command);
					z = nodes.get(nodes.size() - 1).getZ();
					break;
				default:
					throw new IllegalArgumentException("Unknown path command: " + lastCommand);
			}
			nodes.add(new Vector(x, 0, z));
		}
		return nodes;
	}
	

}
