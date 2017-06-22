package com.mantono.webserver.reflection;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.junit.Before;
import org.junit.Test;

public class CompilerTest
{
	@Test
	public void test() throws IOException, ClassNotFoundException
	{
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
		Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromStrings(Arrays.asList("test/com/mantono/webserver/reflection/DummyClass.java"));
		final List<String> compilerArgs = new LinkedList<String>();
		compilerArgs.add("-parameters");
		JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, compilerArgs, null, compilationUnits);
		boolean success = task.call();
		fileManager.close();
		
		ResourceFinder finder = new ResourceFinder();
		finder.search();
		List<Class<?>> cla = finder.getClasses();
		System.out.println(cla);
		for(Class<?> cl : cla)
		{
			System.out.println(cl.getName());
		}
	}

}
