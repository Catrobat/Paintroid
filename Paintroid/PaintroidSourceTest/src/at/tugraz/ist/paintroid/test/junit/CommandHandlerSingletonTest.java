package at.tugraz.ist.paintroid.test.junit;

import junit.framework.Assert;
import junit.framework.TestCase;
import java.lang.reflect.Field;
import java.util.Random;
import java.util.Vector;

//import org.junit.After;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;

import at.tugraz.ist.paintroid.commandmanagement.implementation.CommandHandlerSingleton;

public class CommandHandlerSingletonTest extends TestCase {
	protected static CommandHandlerSingleton KEEP_ALIVE_REFERENCE = CommandHandlerSingleton.COMMAND_HANDLER_SINGLETON_INSTANCE;
	private Random rand = new Random();
	private Vector<Integer> uniqueRunneableIDS = new Vector<Integer>();
	private Vector<Object> referenceToCommandQueue = new Vector<Object>();//!this is the command queue from the singleton!

//	@Before
	public void setUp() throws Exception {
		uniqueRunneableIDS.clear();
		uniqueRunneableIDS.add(-1);
		referenceToCommandQueue.clear();
		rand.setSeed(System.currentTimeMillis());
		Assert.assertNotNull(CommandHandlerSingleton.COMMAND_HANDLER_SINGLETON_INSTANCE);
		Assert.assertSame(CommandHandlerSingleton.COMMAND_HANDLER_SINGLETON_INSTANCE, KEEP_ALIVE_REFERENCE);
		
		try {
			Class commandHandler = Class.forName("at.tugraz.ist.paintroid.commandmanagement.implementation.CommandHandlerSingleton");
			Field fieldlist[] = commandHandler.getDeclaredFields();
			for (int fieldIndex = 0; fieldIndex < fieldlist.length; fieldIndex++) {			
				if (fieldlist[fieldIndex].getName().compareToIgnoreCase("commandQueue") == 0) {
					fieldlist[fieldIndex].setAccessible(true);
					fieldlist[fieldIndex].set(fieldlist[fieldIndex].get(fieldlist[fieldIndex]), referenceToCommandQueue);
					fieldlist[fieldIndex].setAccessible(false);
					}
				}
			}catch (Exception e) {
					Assert.fail(e.toString());
			}		
	}

//	@After
	public void tearDown() throws Exception {
		Assert.assertNotNull(CommandHandlerSingleton.COMMAND_HANDLER_SINGLETON_INSTANCE);
		Assert.assertSame(CommandHandlerSingleton.COMMAND_HANDLER_SINGLETON_INSTANCE, KEEP_ALIVE_REFERENCE);
	}

//	@Test
	public void testIfSingletonExists() {
		Assert.assertNotNull(CommandHandlerSingleton.COMMAND_HANDLER_SINGLETON_INSTANCE);
		Assert.assertEquals(CommandHandlerSingleton.COMMAND_HANDLER_SINGLETON_INSTANCE, KEEP_ALIVE_REFERENCE);
		Assert.assertSame(CommandHandlerSingleton.COMMAND_HANDLER_SINGLETON_INSTANCE, KEEP_ALIVE_REFERENCE);
	}

//	@Test
	public void testCommitCommand() {
		int countRunneables = 1000;
		Vector<TestRunneableClass> runneableClasses = this.helperBuildTestRunneableClasses(countRunneables, false);
		Assert.assertEquals(countRunneables, runneableClasses.size());
		for (int objects = 0; objects < runneableClasses.size(); objects++) {
			Assert.assertTrue(CommandHandlerSingleton.COMMAND_HANDLER_SINGLETON_INSTANCE.commitCommand(runneableClasses.get(objects)));
		}
		Assert.assertNotNull(referenceToCommandQueue);
		Assert.assertEquals(referenceToCommandQueue.size(), runneableClasses.size());
		Assert.assertTrue(runneableClasses.equals(referenceToCommandQueue));
	}

//	@Test
	public void testCommitCommandWithThreads() {
		int countRunneables = 1000;
		Vector<TestRunneableClass> runneableClasses = helperBuildTestRunneableClasses(countRunneables, true);
		Assert.assertEquals(countRunneables, runneableClasses.size());
		Vector<Thread> runneableTestClassesThreads = new Vector<Thread>();
		for (int objects = 0; objects <runneableClasses.size(); objects++) {
			runneableTestClassesThreads.addElement(new Thread(runneableClasses.get(objects))); 
			runneableTestClassesThreads.lastElement().run();			
//			try {
//				Thread.yield();
//			} catch (Exception e) {
//				Assert.fail(e.toString());
//			}
		}

		boolean threadsFinished = false;
		long time = System.currentTimeMillis();
		while (threadsFinished == false) {
			try {
				Thread.yield();
			} catch (Exception e) {
				Assert.fail(e.toString());
			}
			threadsFinished = true;
			for (int index = 0; index < runneableTestClassesThreads.size(); index++) {
				if (runneableTestClassesThreads.get(index).isAlive()) {
					threadsFinished = false;
					break;
				}
			}
			if((time+5000)<System.currentTimeMillis())
				Assert.fail("Thread execution timeout");
		}
		Assert.assertNotNull(referenceToCommandQueue);
		Assert.assertEquals(referenceToCommandQueue.size(), runneableClasses.size());
		Assert.assertTrue(referenceToCommandQueue.containsAll(runneableClasses));
//		Assert.assertEquals(referenceToCommandQueue, runneableClasses);
	}

//	@Test
	public void testGetNextCommand() {
		Vector<TestRunneableClass> testRunneables = new Vector<TestRunneableClass>();
		Assert.assertEquals(referenceToCommandQueue.size(), 0);
		TestRunneableClass nextCommandObject = (TestRunneableClass)CommandHandlerSingleton.COMMAND_HANDLER_SINGLETON_INSTANCE.getNextCommand();
		Assert.assertNull(nextCommandObject);
		referenceToCommandQueue.clear();
		testRunneables = helperBuildTestRunneableClasses(100, false);
		referenceToCommandQueue.addAll(testRunneables);
		
		Assert.assertEquals(referenceToCommandQueue, testRunneables);
		
		for(int index = 0;index<testRunneables.size();index++){
			nextCommandObject = (TestRunneableClass)CommandHandlerSingleton.COMMAND_HANDLER_SINGLETON_INSTANCE.getNextCommand();
			TestRunneableClass commandToCompare = testRunneables.get(index);
			Assert.assertEquals(commandToCompare.uniqueID, nextCommandObject.uniqueID);
			Assert.assertEquals(testRunneables.size()-index-1, referenceToCommandQueue.size());
		}
		Assert.assertNull(CommandHandlerSingleton.COMMAND_HANDLER_SINGLETON_INSTANCE.getNextCommand());
		Assert.assertTrue(referenceToCommandQueue.isEmpty());
	}

//	private Vector<Object> helperGetCommandQueue() {
//		Vector<Object> commandQueue = null;
//		try {
//			Class commandHandler = Class.forName("at.tugraz.ist.paintroid.commandmanagement.implementation.CommandHandlerSingleton");
//			Field fieldlist[] = commandHandler.getDeclaredFields();
//			boolean commandQueueFound = false;
//			for (int fieldIndex = 0; fieldIndex < fieldlist.length; fieldIndex++) {			
//				if (fieldlist[fieldIndex].getName().compareToIgnoreCase("commandQueue") == 0) {
//					commandQueueFound = true;
//					fieldlist[fieldIndex].setAccessible(true);
//					 commandQueue = (Vector<Object>) fieldlist[fieldIndex]
//							.get(fieldlist[fieldIndex]);
//					 fieldlist[fieldIndex].setAccessible(false);
//						break;
//				}
//			}
//			Assert.assertTrue( commandQueueFound );
//		} catch (Exception e) {
//			Assert.fail(e.toString());
//		}	
//		return commandQueue;
//	}
	
	private Vector<TestRunneableClass> helperBuildTestRunneableClasses(int howMany, boolean yield) {
		Vector<TestRunneableClass> runneableClasses = new Vector<TestRunneableClass>();
		for (int objects = 0; objects < howMany; objects++) {
			runneableClasses.addElement(new TestRunneableClass(yield));
		}
		return runneableClasses;
	}
	
	public class TestRunneableClass implements Runnable {

		public long birthTime;
		public long startTime;
		public long finishTime = 0;
		private boolean yield = false;
		public int uniqueID = -1;

		public TestRunneableClass(boolean yield) {
			this.birthTime = System.currentTimeMillis();
			this.yield = yield;
			while(uniqueRunneableIDS.contains(this.uniqueID)){
				this.uniqueID = rand.nextInt();
			}
			uniqueRunneableIDS.add(this.uniqueID);
		}

		@Override
		public void run() {
			this.startTime = System.currentTimeMillis();
			if (this.yield == true) {
				try {					
					if(this.yield) {
						Thread.yield();
					}
				} catch (Exception e) {
					Assert.fail(e.toString());
				}
			}
			CommandHandlerSingleton.COMMAND_HANDLER_SINGLETON_INSTANCE.commitCommand(this);
			finishTime = System.currentTimeMillis();	
		}
	}
}
