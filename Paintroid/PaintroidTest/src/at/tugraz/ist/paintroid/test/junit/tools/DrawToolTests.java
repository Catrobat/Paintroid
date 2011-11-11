package at.tugraz.ist.paintroid.test.junit.tools;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import junit.framework.TestCase;
import android.graphics.Color;
import android.graphics.Paint;
import at.tugraz.ist.paintroid.commandmanagement.CommandHandler;
import at.tugraz.ist.paintroid.tools.Tool;
import at.tugraz.ist.paintroid.tools.implementation.DrawTool;

public class DrawToolTests extends TestCase {

	protected Tool tool;
	protected CommandHandler commandHandler;
	protected int color;
	protected Paint paint;

	@Override
	public void setUp() {
		this.commandHandler = createMock(CommandHandler.class);
		this.color = Color.RED;
		this.paint = new Paint();
		this.tool = new DrawTool(this.color, this.paint);
	}

	public void testShouldAddRunnableOnTabEvent() {
		expect(commandHandler.commitCommand(null)).andStubReturn(true);
		replay(commandHandler);

		tool.handleTab(null);

		verify(commandHandler);
	}

	public void testShouldNotThrowIfNoCommandHandler() {
		Tool tool = new DrawTool(0, null);
		try {
			tool.handleTab(null);
		} catch (Exception e) {
			assertTrue(false);
		}
	}
}
