package at.tugraz.ist.paintroid.test.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import at.tugraz.ist.paintroid.test.junit.stubs.BaseStub;

public class TestObserver extends BaseStub implements Observer {

	@Override
	public void update(Observable observable, Object data) {
		Throwable throwable = new Throwable();
		List<Object> arguments = new ArrayList<Object>();
		arguments.add(observable);
		arguments.add(data);
		addCall(throwable, arguments);
	}

}
