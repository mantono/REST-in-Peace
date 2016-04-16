package com.mantono.webserver.reflection;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResourceFinder
{
	private final static String CLASS = "\\w+\\.class\\b";
	private final String[] classPath;
	private List<Path> pathResourceClasses;
	private List<Class<?>> classes = new ArrayList<Class<?>>();

	public ResourceFinder()
	{
		classPath = System.getProperty("java.class.path").split(":");
	}

	public int search() throws IOException
	{
		pathResourceClasses = new ArrayList<Path>();
		for(String path : classPath)
		{
			Stream<Path> paths = Files.walk(createPath(path));
			List<Path> foundResources = paths
				.filter(t -> isClassFile(t.getFileName()))
				.filter(c -> isResource(c))
				.collect(Collectors.toList());
			pathResourceClasses.addAll(foundResources);
			paths.close();
		}

		return pathResourceClasses.size();
	}

	private Path createPath(String path)
	{
		return new File(path).toPath();
	}

	private boolean isResource(Path c)
	{
		try
		{
			String className = getQualifiedClassName(c);
			Class<?> loadedClass = Class.forName(className);
			return ClassParser.hasResources(loadedClass);
		}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		return false;
	}

	private String getQualifiedClassName(Path c)
	{
		String className = removeClassPath(c.toString());
		className = className.substring(0, className.length()-6);
		className = className.replaceAll("\\/", ".");
		className = className.replaceFirst("\\B\\.", "");
		return className;
	}

	private String removeClassPath(String resource)
	{
		for(String path : classPath)
			if(resource.contains(path))
				return resource.replaceFirst(path, "");

		return null;
	}

	private boolean isClassFile(Path fileName)
	{
		Pattern p = Pattern.compile(CLASS);
		Matcher m = p.matcher(fileName.toString());
		return m.find();
	}

	private void loadClasses() throws ClassNotFoundException
	{
		for(Path path : pathResourceClasses)
		{
			Class<?> loadedClass = Class.forName(getQualifiedClassName(path));
			classes.add(loadedClass);
		}
	}

	public List<Class<?>> getClasses() throws ClassNotFoundException
	{
		loadClasses();
		return classes;
	}
}
