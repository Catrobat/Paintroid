package at.tugraz.ist.paintroid.test.junit.tools;

import junit.framework.TestCase;
import android.graphics.Paint;
import android.graphics.Point;
import at.tugraz.ist.paintroid.commandmanagement.Command;
import at.tugraz.ist.paintroid.commandmanagement.CommandHandler;
import at.tugraz.ist.paintroid.commandmanagement.implementation.BaseCommand;
import at.tugraz.ist.paintroid.commandmanagement.implementation.PointCommand;
import at.tugraz.ist.paintroid.test.junit.stubs.CommandHandlerStub;
import at.tugraz.ist.paintroid.test.utils.PrivateAccess;
import at.tugraz.ist.paintroid.tools.Tool;
import at.tugraz.ist.paintroid.tools.implementation.DrawTool;

public class DrawToolTests extends TestCase {

	protected Tool tool;
	protected CommandHandler commandHandlerEasyMock;
	protected Paint paint;

	@Override
	public void setUp() {
		// this.commandHandlerEasyMock = EasyMock.createMock(CommandHandler.class);
		this.paint = new Paint();
		this.tool = new DrawTool(this.paint);
	}

	public void testShouldAddCommandOnTabEventEasyMock() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		Point tab = new Point(0, 0);
		CommandHandlerStub commandHandlerStub = new CommandHandlerStub();

		tool.setCommandHandler(commandHandlerStub);
		tool.handleTab(tab);

		assertEquals(1, commandHandlerStub.getCallCount("commitCommand"));
		Command command = (Command) commandHandlerStub.getCall("commitCommand", 0).get(0);
		assertTrue(command instanceof PointCommand);
		Point point = (Point) PrivateAccess.getMemberValue(PointCommand.class, command, "point");
		assertSame(tab, point);
		Paint paint = (Paint) PrivateAccess.getMemberValue(BaseCommand.class, command, "paint");
		assertSame(this.paint, paint);
	}

	public void testShouldNotThrowIfNoCommandHandler() {
		Tool tool = new DrawTool(null);
		try {
			tool.handleTab(null);
		} catch (Exception e) {
			assertTrue(false);
		}
	}

	public void testShouldNotAddCommandIfNoCoordinate() {
		CommandHandlerStub commandHandlerStub = new CommandHandlerStub();
		tool.setCommandHandler(commandHandlerStub);
		tool.handleTab(null);

		assertEquals(0, commandHandlerStub.getCallCount("commitCommand"));
	}
}
