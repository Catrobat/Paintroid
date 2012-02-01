package at.tugraz.ist.paintroid.test.junit.tools;

import static at.tugraz.ist.paintroid.test.utils.PaintroidAsserts.assertPaintEquals;
import static at.tugraz.ist.paintroid.test.utils.PaintroidAsserts.assertPathEquals;

import java.util.List;

import junit.framework.TestCase;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Path;
import android.graphics.PointF;
import at.tugraz.ist.paintroid.MainActivity.ToolType;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.commandmanagement.Command;
import at.tugraz.ist.paintroid.commandmanagement.implementation.BaseCommand;
import at.tugraz.ist.paintroid.commandmanagement.implementation.PathCommand;
import at.tugraz.ist.paintroid.commandmanagement.implementation.PointCommand;
import at.tugraz.ist.paintroid.test.junit.stubs.CommandHandlerStub;
import at.tugraz.ist.paintroid.test.junit.stubs.PathStub;
import at.tugraz.ist.paintroid.test.utils.PrivateAccess;
import at.tugraz.ist.paintroid.tools.Tool;
import at.tugraz.ist.paintroid.tools.implementation.BaseTool;
import at.tugraz.ist.paintroid.tools.implementation.DrawTool;

public class DrawToolTests extends TestCase {

	protected Tool tool;
	protected CommandHandlerStub commandHandlerStub;
	protected Paint paint;

	@Override
	public void setUp() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		this.paint = new Paint();
		this.paint.setColor(Color.BLUE);
		this.paint.setStrokeCap(Cap.ROUND);
		this.paint.setStrokeWidth(15);
		this.commandHandlerStub = new CommandHandlerStub();
		PaintroidApplication.COMMAND_HANDLER = this.commandHandlerStub;
		this.tool = new DrawTool();
		this.tool.setDrawPaint(this.paint);
	}

	public void testShouldReturnCorrectToolType() {
		ToolType toolType = tool.getToolType();

		assertEquals(ToolType.BRUSH, toolType);
	}

	public void testShouldReturnPaint() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		Paint drawPaint = (Paint) PrivateAccess.getMemberValue(BaseTool.class, tool, "drawPaint");

		assertSame(this.paint, drawPaint);
	}

	// down event
	public void testShouldMovePathOnDownEvent() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		PointF event = new PointF(0, 0);
		PathStub pathStub = new PathStub();
		PrivateAccess.setMemberValue(DrawTool.class, tool, "pathToDraw", pathStub);

		boolean returnValue = tool.handleDown(event);

		assertTrue(returnValue);
		assertEquals(1, pathStub.getCallCount("moveTo"));
		List<Object> arguments = pathStub.getCall("moveTo", 0);
		assertEquals(event.x, arguments.get(0));
		assertEquals(event.y, arguments.get(1));
	}

	public void testShouldNotAddCommandOnDownEvent() {
		PointF event = new PointF(0, 0);

		boolean returnValue = tool.handleDown(event);

		assertTrue(returnValue);
		assertEquals(0, commandHandlerStub.getCallCount("commitCommand"));
	}

	public void testShouldNotStartPathIfNoCoordinateOnDownEvent() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		PathStub pathStub = new PathStub();
		PrivateAccess.setMemberValue(DrawTool.class, tool, "pathToDraw", pathStub);

		boolean returnValue = tool.handleDown(null);

		assertFalse(returnValue);
		assertEquals(0, pathStub.getCallCount("reset"));
		assertEquals(0, pathStub.getCallCount("moveTo"));
	}

	// move event
	public void testShouldMovePathOnMoveEvent() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		PointF event1 = new PointF(0, 0);
		PointF event2 = new PointF(5, 6);
		PathStub pathStub = new PathStub();
		PrivateAccess.setMemberValue(DrawTool.class, tool, "pathToDraw", pathStub);

		tool.handleDown(event1);
		boolean returnValue = tool.handleMove(event2);

		assertTrue(returnValue);
		assertEquals(1, pathStub.getCallCount("moveTo"));
		assertEquals(1, pathStub.getCallCount("quadTo"));
		List<Object> arguments = pathStub.getCall("quadTo", 0);
		final float cx = (event1.x + event2.x) / 2;
		final float cy = (event1.y + event2.y) / 2;
		assertEquals(event1.x, arguments.get(0));
		assertEquals(event1.y, arguments.get(1));
		assertEquals(cx, arguments.get(2));
		assertEquals(cy, arguments.get(3));
	}

	public void testShouldNotAddCommandOnMoveEvent() {
		PointF event = new PointF(0, 0);

		tool.handleDown(event);
		boolean returnValue = tool.handleMove(event);

		assertTrue(returnValue);
		assertEquals(0, commandHandlerStub.getCallCount("commitCommand"));
	}

	public void testShouldNotMovePathIfNoCoordinateOnMoveEvent() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		PointF event = new PointF(0, 0);
		PathStub pathStub = new PathStub();
		PrivateAccess.setMemberValue(DrawTool.class, tool, "pathToDraw", pathStub);

		tool.handleDown(event);
		boolean returnValue = tool.handleMove(null);

		assertFalse(returnValue);
		assertEquals(0, pathStub.getCallCount("quadTo"));
	}

	// up event
	public void testShouldMovePathOnUpEvent() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		PointF event1 = new PointF(0, 0);
		PointF event2 = new PointF(PaintroidApplication.MOVE_TOLLERANCE, PaintroidApplication.MOVE_TOLLERANCE);
		PointF event3 = new PointF(PaintroidApplication.MOVE_TOLLERANCE * 2, -PaintroidApplication.MOVE_TOLLERANCE);
		PathStub pathStub = new PathStub();
		PrivateAccess.setMemberValue(DrawTool.class, tool, "pathToDraw", pathStub);

		tool.handleDown(event1);
		tool.handleMove(event2);
		boolean returnValue = tool.handleUp(event3);

		assertTrue(returnValue);
		assertEquals(1, pathStub.getCallCount("moveTo"));
		assertEquals(1, pathStub.getCallCount("quadTo"));
		assertEquals(1, pathStub.getCallCount("lineTo"));
		List<Object> arguments = pathStub.getCall("lineTo", 0);
		assertEquals(event3.x, arguments.get(0));
		assertEquals(event3.y, arguments.get(1));
	}

	public void testShouldNotMovePathIfNoCoordinateOnUpEvent() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		PointF event = new PointF(0, 0);
		PathStub pathStub = new PathStub();
		PrivateAccess.setMemberValue(DrawTool.class, tool, "pathToDraw", pathStub);

		tool.handleDown(event);
		tool.handleMove(event);
		boolean returnValue = tool.handleUp(null);

		assertFalse(returnValue);
		assertEquals(0, pathStub.getCallCount("lineTo"));
	}

	public void testShouldAddCommandOnUpEvent() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		PointF event = new PointF(0, 0);
		PointF event1 = new PointF(PaintroidApplication.MOVE_TOLLERANCE + 0.1f, 0);
		PointF event2 = new PointF(PaintroidApplication.MOVE_TOLLERANCE + 2, PaintroidApplication.MOVE_TOLLERANCE + 2);
		PathStub pathStub = new PathStub();
		PrivateAccess.setMemberValue(DrawTool.class, tool, "pathToDraw", pathStub);

		tool.handleDown(event);
		tool.handleMove(event1);
		boolean returnValue = tool.handleUp(event2);

		assertTrue(returnValue);
		assertEquals(1, commandHandlerStub.getCallCount("commitCommand"));
		Command command = (Command) commandHandlerStub.getCall("commitCommand", 0).get(0);
		assertTrue(command instanceof PathCommand);
		Path path = (Path) PrivateAccess.getMemberValue(PathCommand.class, command, "path");
		assertPathEquals(pathStub, path);
		Paint paint = (Paint) PrivateAccess.getMemberValue(BaseCommand.class, command, "paint");
		assertPaintEquals(this.paint, paint);
	}

	// Spurious test. CommandHandler should always be instatiated by MainActivity and referenced in
	// PaintroidApplication.
	//
	// public void testShouldNotThrowIfNoCommandHandlerOnUpEvent() {
	// PointF event = new PointF(0, 0);
	//
	// Tool tool = new DrawTool();
	// try {
	// tool.handleDown(event);
	// tool.handleMove(event);
	// boolean returnValue = tool.handleUp(event);
	// assertFalse(returnValue);
	// } catch (Exception e) {
	// assertTrue(false);
	// }
	// }

	public void testShouldNotAddCommandIfNoCoordinateOnUpEvent() {
		PointF event = new PointF(0, 0);

		tool.handleDown(event);
		tool.handleMove(event);
		boolean returnValue = tool.handleUp(null);

		assertFalse(returnValue);
		assertEquals(0, commandHandlerStub.getCallCount("commitCommand"));
	}

	// tab event
	public void testShouldAddCommandOnTabEvent() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		PointF tab = new PointF(0, 0);

		boolean returnValue1 = tool.handleDown(tab);
		boolean returnValue2 = tool.handleUp(tab);

		assertTrue(returnValue1);
		assertTrue(returnValue2);
		assertEquals(1, commandHandlerStub.getCallCount("commitCommand"));
		Command command = (Command) commandHandlerStub.getCall("commitCommand", 0).get(0);
		assertTrue(command instanceof PointCommand);
		PointF point = (PointF) PrivateAccess.getMemberValue(PointCommand.class, command, "point");
		assertTrue(tab.equals(point.x, point.y));
		Paint paint = (Paint) PrivateAccess.getMemberValue(BaseCommand.class, command, "paint");
		assertPaintEquals(this.paint, paint);
	}

	public void testShouldAddCommandOnTabWithinTolleranceEvent() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		PointF tab1 = new PointF(0, 0);
		PointF tab2 = new PointF(PaintroidApplication.MOVE_TOLLERANCE - 0.1f, 0);
		PointF tab3 = new PointF(PaintroidApplication.MOVE_TOLLERANCE - 0.1f,
				PaintroidApplication.MOVE_TOLLERANCE - 0.1f);

		boolean returnValue1 = tool.handleDown(tab1);
		boolean returnValue2 = tool.handleMove(tab2);
		boolean returnValue3 = tool.handleUp(tab3);

		assertTrue(returnValue1);
		assertTrue(returnValue2);
		assertTrue(returnValue3);
		assertEquals(1, commandHandlerStub.getCallCount("commitCommand"));
		Command command = (Command) commandHandlerStub.getCall("commitCommand", 0).get(0);
		assertTrue(command instanceof PointCommand);
		PointF point = (PointF) PrivateAccess.getMemberValue(PointCommand.class, command, "point");
		assertTrue(tab1.equals(point.x, point.y));
		Paint paint = (Paint) PrivateAccess.getMemberValue(BaseCommand.class, command, "paint");
		assertPaintEquals(this.paint, paint);
	}

	public void testShouldAddPathCommandOnMultipleMovesWithinTolleranceEvent() throws SecurityException,
			IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
		PointF tab1 = new PointF(0, 0);
		PointF tab2 = new PointF(0, PaintroidApplication.MOVE_TOLLERANCE - 0.1f);
		PointF tab3 = new PointF(0, 0);
		PointF tab4 = new PointF(0, -PaintroidApplication.MOVE_TOLLERANCE + 0.1f);
		PointF tab5 = new PointF(0, 0);

		tool.handleDown(tab1);
		tool.handleMove(tab2);
		tool.handleMove(tab3);
		tool.handleMove(tab4);
		tool.handleUp(tab5);

		assertEquals(1, commandHandlerStub.getCallCount("commitCommand"));
		Command command = (Command) commandHandlerStub.getCall("commitCommand", 0).get(0);
		assertTrue(command instanceof PathCommand);
	}

	// Spurious test. CommandHandler should always be instatiated by MainActivity and referenced in
	// PaintroidApplication.
	//
	// public void testShouldNotThrowIfNoCommandHandlerOnTabEvent() {
	// PointF tab = new PointF(0, 0);
	//
	// Tool tool = new DrawTool();
	// try {
	// boolean returnValue1 = tool.handleDown(tab);
	// boolean returnValue2 = tool.handleUp(tab);
	//
	// assertTrue(returnValue1);
	// assertFalse(returnValue2);
	// } catch (Exception e) {
	// assertTrue(false);
	// }
	// }

	public void testShouldRewindPathOnAppliedToBitmap() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		PathStub pathStub = new PathStub();
		PrivateAccess.setMemberValue(DrawTool.class, tool, "pathToDraw", pathStub);

		tool.onAppliedToBitmap();

		assertEquals(1, pathStub.getCallCount("rewind"));
	}

	public void testShouldReturnPaintsColor() {
		int color = tool.getAttributeButtonColor();

		assertEquals(paint.getColor(), color);
	}

	public void testShouldReturnCorrectResource() {
		int resource = tool.getAttributeButtonResource();

		assertEquals(R.drawable.circle_3_32, resource);
	}
}
