package at.tugraz.ist.paintroid.test.junit.stubs;

import java.util.ArrayList;
import java.util.List;

import at.tugraz.ist.paintroid.commandmanagement.Command;
import at.tugraz.ist.paintroid.commandmanagement.CommandHandler;

public class CommandHandlerStub extends BaseStub implements CommandHandler {

	@Override
	public boolean commitCommand(Command commandObject) {
		Throwable throwable = new Throwable();
		List<Object> arguments = new ArrayList<Object>();
		arguments.add(commandObject);
		addCall(throwable, arguments);
		return getBooleanReturnValue(throwable);
	}

	@Override
	public Command getNextCommand() {
		Throwable throwable = new Throwable();
		addCall(throwable, new ArrayList<Object>());
		return null;
	}

	@Override
	public void clearCommandHandlerQueue() {

	}

}
