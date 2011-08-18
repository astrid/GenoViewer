/*
 * This file is part of GenoViewer.
 *
 * GenoViewer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GenoViewer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GenoViewer.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.astrid.viewer.gui;

import abbot.tester.ComponentTester;
import abbot.util.Condition;
import hu.astrid.viewer.Viewer;
import java.awt.Frame;
import java.beans.PropertyChangeSupport;
import junit.extensions.abbot.ComponentTestFixture;

/**
 *
 * @author Szuni
 */
public class StatusBarTest extends ComponentTestFixture {

	private StatusBar statusBar;
	private PropertyChangeSupport propertyChangeSupport;
	private ComponentTester tester;
	private Frame frame;

	public StatusBarTest() {
		Viewer.init();
	}



	@Override
	protected void setUp() throws Exception {
		statusBar = new StatusBar();
		frame = showFrame(statusBar);
		propertyChangeSupport = new PropertyChangeSupport(this);
		propertyChangeSupport.addPropertyChangeListener(statusBar);
		tester = new ComponentTester();
	}

	@Override
	protected void tearDown() throws Exception {
		frame.dispose();
		frame = null;
		statusBar = null;
		propertyChangeSupport = null;
		tester = null;
	}

	public void testStatusBar() {
		propertyChangeSupport.firePropertyChange(StatusBar.MESSAGE, null, "Message");
		assertEquals("Message", statusBar.getMessage());
		tester.delay(StatusBar.MESSAGE_TIMEOUT + 1);
		assertEquals("", statusBar.getMessage());

		propertyChangeSupport.firePropertyChange(StatusBar.JOB_STARTED, null, "Busy");
		assertEquals("Busy", statusBar.getMessage());
		propertyChangeSupport.firePropertyChange(StatusBar.MESSAGE, null, "Message");
		assertEquals("Busy", statusBar.getMessage());
		tester.delay(StatusBar.MESSAGE_TIMEOUT);
		assertEquals("Busy", statusBar.getMessage());
		assertTrue(statusBar.isBusy());
		assertTrue(statusBar.progressBar.isVisible());
		assertTrue(statusBar.progressBar.isIndeterminate());

		propertyChangeSupport.firePropertyChange(StatusBar.JOB_DONE, "Busy", null);
		assertEquals("", statusBar.getMessage());
		assertFalse(statusBar.isBusy());
		assertFalse(statusBar.progressBar.isVisible());
		assertTrue(statusBar.progressBar.isIndeterminate());
	}

	public void testStatusbarMessages() {
		//Simple message
		propertyChangeSupport.firePropertyChange(StatusBar.MESSAGE, null, "Message");
		assertEquals("Message", statusBar.getMessage());

		//Job1
		propertyChangeSupport.firePropertyChange(StatusBar.JOB_STARTED, null, "Job 1");
		assertEquals("Job 1", statusBar.getMessage());
		assertEquals(1, statusBar.jobList.size());
		assertTrue(statusBar.isBusy());
		assertTrue(statusBar.progressBar.isVisible());
		assertTrue(statusBar.progressBar.isIndeterminate());

		//Simple message
		propertyChangeSupport.firePropertyChange(StatusBar.MESSAGE, null, "Message");
		assertEquals("Job 1", statusBar.getMessage());
		assertEquals(1, statusBar.jobList.size());
		assertTrue(statusBar.isBusy());
		assertTrue(statusBar.progressBar.isVisible());
		assertTrue(statusBar.progressBar.isIndeterminate());

		//Empty job
		propertyChangeSupport.firePropertyChange(StatusBar.JOB_STARTED, null, null);
		assertEquals("Job 1", statusBar.getMessage());
		assertEquals(2, statusBar.jobList.size());
		assertTrue(statusBar.isBusy());
		assertTrue(statusBar.progressBar.isVisible());
		assertTrue(statusBar.progressBar.isIndeterminate());

		//Job 2
		propertyChangeSupport.firePropertyChange(StatusBar.JOB_STARTED, null, "Job 2");
		assertEquals("Job 2", statusBar.getMessage());
		assertEquals(3, statusBar.jobList.size());
		assertTrue(statusBar.isBusy());
		assertTrue(statusBar.progressBar.isVisible());
		assertTrue(statusBar.progressBar.isIndeterminate());

		//Job 3
		propertyChangeSupport.firePropertyChange(StatusBar.JOB_STARTED, null, "Job 3");
		assertEquals("Job 3", statusBar.getMessage());
		assertEquals(4, statusBar.jobList.size());
		assertTrue(statusBar.isBusy());
		assertTrue(statusBar.progressBar.isVisible());
		assertTrue(statusBar.progressBar.isIndeterminate());

		//Stop Job 3
		propertyChangeSupport.firePropertyChange(StatusBar.JOB_DONE, "Job 3", null);
		assertEquals("Job 1", statusBar.getMessage());
		assertEquals(3, statusBar.jobList.size());
		assertTrue(statusBar.isBusy());
		assertTrue(statusBar.progressBar.isVisible());
		assertTrue(statusBar.progressBar.isIndeterminate());

		//Stop unstarted job
		propertyChangeSupport.firePropertyChange(StatusBar.JOB_DONE, "Job X", null);
		assertEquals("Job 1", statusBar.getMessage());
		assertEquals(3, statusBar.jobList.size());
		assertTrue(statusBar.isBusy());
		assertTrue(statusBar.progressBar.isVisible());
		assertTrue(statusBar.progressBar.isIndeterminate());

		//Stop Job 1
		propertyChangeSupport.firePropertyChange(StatusBar.JOB_DONE, "Job 1", null);
		assertEquals("Job 2", statusBar.getMessage());
		assertEquals(2, statusBar.jobList.size());
		assertTrue(statusBar.isBusy());
		assertTrue(statusBar.progressBar.isVisible());
		assertTrue(statusBar.progressBar.isIndeterminate());

		//Stop Job 2
		propertyChangeSupport.firePropertyChange(StatusBar.JOB_DONE, "Job 2", null);
		assertEquals("", statusBar.getMessage());
		assertEquals(1, statusBar.jobList.size());
		assertTrue(statusBar.isBusy());
		assertTrue(statusBar.progressBar.isVisible());
		assertTrue(statusBar.progressBar.isIndeterminate());

		//Simple message
		propertyChangeSupport.firePropertyChange(StatusBar.MESSAGE, null, "Message");
		assertEquals("Message", statusBar.getMessage());
		assertEquals(1, statusBar.jobList.size());
		assertTrue(statusBar.isBusy());
		assertTrue(statusBar.progressBar.isVisible());
		assertTrue(statusBar.progressBar.isIndeterminate());

		//Empty job
		propertyChangeSupport.firePropertyChange(StatusBar.JOB_DONE, null, null);
		assertEquals("", statusBar.getMessage());
		assertEquals(0, statusBar.jobList.size());
		assertFalse(statusBar.isBusy());
		assertFalse(statusBar.progressBar.isVisible());
		assertTrue(statusBar.progressBar.isIndeterminate());
	}

	public void testTooMuchStops() {
		propertyChangeSupport.firePropertyChange(StatusBar.JOB_STARTED, null, "Job 1");
		propertyChangeSupport.firePropertyChange(StatusBar.JOB_STARTED, null, "Job 2");
		propertyChangeSupport.firePropertyChange(StatusBar.JOB_STARTED, null, "Job 1");
		assertEquals(3, statusBar.jobList.size());

		propertyChangeSupport.firePropertyChange(StatusBar.JOB_DONE, "Job 2", null);
		assertEquals(2, statusBar.jobList.size());
		propertyChangeSupport.firePropertyChange(StatusBar.JOB_DONE, "Job 2", null);
		assertEquals(2, statusBar.jobList.size());
		propertyChangeSupport.firePropertyChange(StatusBar.JOB_DONE, "Job 1", null);
		assertEquals(1, statusBar.jobList.size());
		propertyChangeSupport.firePropertyChange(StatusBar.JOB_DONE, "Job 1", null);
		assertEquals(0, statusBar.jobList.size());
		assertFalse(statusBar.isBusy());
		assertFalse(statusBar.progressBar.isVisible());
		assertTrue(statusBar.progressBar.isIndeterminate());
		propertyChangeSupport.firePropertyChange(StatusBar.JOB_DONE, "Job 1", null);
		assertEquals(0, statusBar.jobList.size());
		propertyChangeSupport.firePropertyChange(StatusBar.JOB_DONE, null, null);
		assertEquals(0, statusBar.jobList.size());
	}

	public void testProgress() {
		propertyChangeSupport.firePropertyChange(StatusBar.PROGRESS_VALUE, null, new StatusBar.ProgressValue("Progress", 0));
		assertEquals("", statusBar.getMessage());
		tester.wait(new Condition() {

			@Override
			public boolean test() {
				return statusBar.progressBar.getString().equals("Progress");
			}
		});
		assertEquals(0, statusBar.jobList.size());
		assertTrue(statusBar.isBusy());
		assertTrue(statusBar.progressBar.isVisible());
		assertFalse(statusBar.progressBar.isIndeterminate());
		assertEquals(0, statusBar.progressBar.getValue());

		propertyChangeSupport.firePropertyChange(StatusBar.PROGRESS_VALUE, null, new StatusBar.ProgressValue("Progress 2", 0));
		tester.wait(new Condition() {

			@Override
			public boolean test() {
				return statusBar.progressBar.getString().equals("Progress 2");
			}
		});
		assertEquals("", statusBar.getMessage());
		assertEquals(0, statusBar.jobList.size());
		assertTrue(statusBar.isBusy());
		assertTrue(statusBar.progressBar.isVisible());
		assertFalse(statusBar.progressBar.isIndeterminate());
		assertEquals(0, statusBar.progressBar.getValue());

		for (int i = 0; i <= 100; ++i) {
			propertyChangeSupport.firePropertyChange(StatusBar.PROGRESS_VALUE, null, new StatusBar.ProgressValue("Progress 2", i));
			if (statusBar.progressBar.getString().equals("Progress 2")) {
				assertEquals(i, statusBar.progressBar.getValue());
			} else if (statusBar.progressBar.getString().equals("Progress")) {
				assertEquals(0, statusBar.progressBar.getValue());
			} else {
				fail(statusBar.progressBar.getString() +" is unknown progress");
			}
			assertEquals("", statusBar.getMessage());
			assertEquals(0, statusBar.jobList.size());
			assertTrue(statusBar.isBusy());
			assertTrue(statusBar.progressBar.isVisible());
			assertFalse(statusBar.progressBar.isIndeterminate());
		}

		propertyChangeSupport.firePropertyChange(StatusBar.PROGRESS_VALUE, null, new StatusBar.ProgressValue("Progress 2", 101));
		tester.wait(new Condition() {

			@Override
			public boolean test() {
				return statusBar.progressBar.getString().equals("Progress");
			}
		});

		assertEquals("Progress", statusBar.progressBar.getString());
		assertEquals(0, statusBar.progressBar.getValue());
		assertEquals("", statusBar.getMessage());
		assertEquals(0, statusBar.jobList.size());
		assertTrue(statusBar.isBusy());
		assertTrue(statusBar.progressBar.isVisible());
		assertFalse(statusBar.progressBar.isIndeterminate());

		propertyChangeSupport.firePropertyChange(StatusBar.PROGRESS_VALUE, null, new StatusBar.ProgressValue("Progress", 101));

		assertEquals("", statusBar.getMessage());
		assertEquals("", statusBar.progressBar.getString());
		assertEquals(0, statusBar.jobList.size());
		assertFalse(statusBar.isBusy());
		assertFalse(statusBar.progressBar.isVisible());
		assertTrue(statusBar.progressBar.isIndeterminate());
	}

	public void testProgressWithJobs() {
		propertyChangeSupport.firePropertyChange(StatusBar.PROGRESS_VALUE, null, new StatusBar.ProgressValue("Progress", 0));
		assertEquals("", statusBar.getMessage());
		tester.wait(new Condition() {

			@Override
			public boolean test() {
				return statusBar.progressBar.getString().equals("Progress");
			}
		});
		assertEquals(0, statusBar.jobList.size());
		assertTrue(statusBar.isBusy());
		assertTrue(statusBar.progressBar.isVisible());
		assertFalse(statusBar.progressBar.isIndeterminate());
		assertEquals(0, statusBar.progressBar.getValue());

		propertyChangeSupport.firePropertyChange(StatusBar.JOB_STARTED, null, "Job 1");
		assertEquals("Job 1", statusBar.getMessage());
		assertEquals("Progress", statusBar.progressBar.getString());
		assertEquals(1, statusBar.jobList.size());
		assertTrue(statusBar.isBusy());
		assertTrue(statusBar.progressBar.isVisible());
		assertFalse(statusBar.progressBar.isIndeterminate());
		assertEquals(0, statusBar.progressBar.getValue());

		propertyChangeSupport.firePropertyChange(StatusBar.PROGRESS_VALUE, null, new StatusBar.ProgressValue("Progress", 50));
		assertEquals("Job 1", statusBar.getMessage());
		assertEquals("Progress", statusBar.progressBar.getString());
		assertEquals(1, statusBar.jobList.size());
		assertTrue(statusBar.isBusy());
		assertTrue(statusBar.progressBar.isVisible());
		assertFalse(statusBar.progressBar.isIndeterminate());
		assertEquals(50, statusBar.progressBar.getValue());

		propertyChangeSupport.firePropertyChange(StatusBar.JOB_STARTED, null, "Job 2");
		assertEquals("Job 2", statusBar.getMessage());
		assertEquals("Progress", statusBar.progressBar.getString());
		assertEquals(2, statusBar.jobList.size());
		assertTrue(statusBar.isBusy());
		assertTrue(statusBar.progressBar.isVisible());
		assertFalse(statusBar.progressBar.isIndeterminate());
		assertEquals(50, statusBar.progressBar.getValue());

		propertyChangeSupport.firePropertyChange(StatusBar.JOB_DONE, "Job 2", null);
		assertEquals("Job 1", statusBar.getMessage());
		assertEquals("Progress", statusBar.progressBar.getString());
		assertEquals(1, statusBar.jobList.size());
		assertTrue(statusBar.isBusy());
		assertTrue(statusBar.progressBar.isVisible());
		assertFalse(statusBar.progressBar.isIndeterminate());
		assertEquals(50, statusBar.progressBar.getValue());

		propertyChangeSupport.firePropertyChange(StatusBar.PROGRESS_VALUE, null, new StatusBar.ProgressValue("Progress", 101));
		assertEquals("Job 1", statusBar.getMessage());
		assertEquals("", statusBar.progressBar.getString());
		assertEquals(1, statusBar.jobList.size());
		assertTrue(statusBar.isBusy());
		assertTrue(statusBar.progressBar.isVisible());
		assertTrue(statusBar.progressBar.isIndeterminate());

		propertyChangeSupport.firePropertyChange(StatusBar.JOB_DONE, "Job 1", null);
		assertEquals("", statusBar.getMessage());
		assertEquals("", statusBar.progressBar.getString());
		assertEquals(0, statusBar.jobList.size());
		assertFalse(statusBar.isBusy());
		assertFalse(statusBar.progressBar.isVisible());

		propertyChangeSupport.firePropertyChange(StatusBar.JOB_STARTED, null, "Job");
		assertEquals("Job", statusBar.getMessage());
		assertEquals("", statusBar.progressBar.getString());
		assertEquals(1, statusBar.jobList.size());
		assertTrue(statusBar.isBusy());
		assertTrue(statusBar.progressBar.isVisible());
		assertTrue(statusBar.progressBar.isIndeterminate());

		propertyChangeSupport.firePropertyChange(StatusBar.PROGRESS_VALUE, null, new StatusBar.ProgressValue("Progress", 0));
		assertEquals("Job", statusBar.getMessage());
		tester.wait(new Condition() {

			@Override
			public boolean test() {
				return statusBar.progressBar.getString().equals("Progress");
			}
		});
		assertEquals(1, statusBar.jobList.size());
		assertTrue(statusBar.isBusy());
		assertTrue(statusBar.progressBar.isVisible());
		assertFalse(statusBar.progressBar.isIndeterminate());
		assertEquals(0, statusBar.progressBar.getValue());

		propertyChangeSupport.firePropertyChange(StatusBar.JOB_DONE, "Job", null);
		assertEquals("", statusBar.getMessage());
		assertEquals("Progress", statusBar.progressBar.getString());
		assertEquals(0, statusBar.jobList.size());
		assertTrue(statusBar.isBusy());
		assertTrue(statusBar.progressBar.isVisible());
		assertFalse(statusBar.progressBar.isIndeterminate());
		assertEquals(0, statusBar.progressBar.getValue());

		propertyChangeSupport.firePropertyChange(StatusBar.PROGRESS_VALUE, null, new StatusBar.ProgressValue("Progress", 101));
		assertEquals("", statusBar.getMessage());
		assertEquals("", statusBar.progressBar.getString());
		assertEquals(0, statusBar.jobList.size());
		assertFalse(statusBar.isBusy());
		assertFalse(statusBar.progressBar.isVisible());
		assertTrue(statusBar.progressBar.isIndeterminate());
	}
}
