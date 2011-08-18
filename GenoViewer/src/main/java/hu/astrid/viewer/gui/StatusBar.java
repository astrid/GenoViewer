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

/*
 * StatusBar.java
 *
 * Created on 2010.04.01., 9:54:12
 */
package hu.astrid.viewer.gui;

import hu.astrid.viewer.Viewer;
import hu.astrid.viewer.util.SwingComponentQueryTask;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.apache.log4j.Logger;

/**
 * Statusbar for Frames. Can be added as property change listener, and handles the
 * specific events. Can be handled jobs and progresses.
 * <p>Job can be started with a message, then progressbar and busy animation start.
 * Stores started jobs and displayes its messages. If one job finished,
 * next message displayed. Job can be ended with the same message as started.
 * <p>Progress can be started and value modified by {@link ProgressValue}. If multiple
 * progresses started, progressbar displays their names and values in cycle. If
 * value set above 100, progress stops and if there are no other process, progressbar
 * set to indeterminate, if no other jobs either busy animation ends.
 * Values other than 0-100 interval set to 0 or 100.
 * Running progresses are shown in progressbars tooltip text.
 * @author Szuni
 */
public class StatusBar extends javax.swing.JPanel implements PropertyChangeListener {

	private static final long serialVersionUID = 6L;
	protected static final int ANIMATION_RATE = 30;
	protected static final int MESSAGE_TIMEOUT = 5000;
	protected static final int PROGRESS_CHANGE_RATE = 2000;
	/**Stop the animation*/
	public static final String JOB_DONE = "jobdone";
	/**Display a message*/
	public static final String MESSAGE = "message";
	/**Start the animation*/
	public static final String JOB_STARTED = "jobstarted";
//	/**Start value based progress animation*/
//	public static final String PROGRESS = "progress";
	/**Set progressbar value*/
	public static final String PROGRESS_VALUE = "progressvalue";
	private Timer messageTimer;
	private Timer busyIconTimer;
	private Timer progressbarTimer;
	private final Icon idleIcon = new ImageIcon(getClass().getResource("/hu/astrid/viewer/gui/busyicons/idle-icon.png"));
	private final Icon[] busyIcons = new Icon[15];
	private int busyIconIndex = 0;
	private int progressIndex = -1;
	protected final LinkedList<String> progressList = new LinkedList<String>();
	protected final HashMap<String, Integer> progressValues = new HashMap<String, Integer>();
	/**List for store started jobs messages*/
	protected final LinkedList<String> jobList = new LinkedList<String>();
	/**Is there any request with message. In this case other messages arent displayed*/
	private boolean isBusyMessage;
	/** Default logger */
	private static final Logger logger = Logger.getLogger(StatusBar.class);

	/** Creates new StatusBar */
	public StatusBar() {
		initComponents();

		messageTimer = new Timer(MESSAGE_TIMEOUT, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				statusMessageLabel.setText("");
			}
		});
		messageTimer.setRepeats(false);

		for (int i = 0; i < busyIcons.length; i++) {
			busyIcons[i] = new ImageIcon(getClass().getResource("/hu/astrid/viewer/gui/busyicons/busy-icon" + i + ".png"));
		}
		busyIconTimer = new Timer(ANIMATION_RATE, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
				statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
			}
		});

		progressbarTimer = new Timer(PROGRESS_CHANGE_RATE, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				progressIndex = ++progressIndex % progressValues.size();
				String progressName = progressList.get(progressIndex);
				progressBar.setValue(progressValues.get(progressName));
				if (Viewer.getLabelResources().containsKey("progressbarMessage" + progressName)) {
					progressName = Viewer.getLabelResources().getString("progressbarMessage" + progressName);
				}
				progressBar.setString(progressName);
			}
		});
		progressbarTimer.setInitialDelay(0);

		progressBar.setVisible(false);
		progressBar.setIndeterminate(true);

		progressBar.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent e) {
				super.mouseEntered(e);
				StringBuilder toolTipText = new StringBuilder(MessageFormat.format(Viewer.getLabelResources().getString("progressbarToolTip"), progressList.size()));
				for (String progressName : progressList) {
					int value = progressValues.get(progressName);
					if (Viewer.getLabelResources().containsKey("progressbarMessage" + progressName)) {
						progressName = Viewer.getLabelResources().getString("progressbarMessage" + progressName);
					}
					toolTipText.append("\n  ").append(progressName).append(" ").append(value).append("%");
				}
				progressBar.setToolTipText(toolTipText.toString());
			}
		});
	}

	/**
	 * @return displayed message
	 */
	public String getMessage() {
		if (SwingUtilities.isEventDispatchThread()) {
			return statusMessageLabel.getText();
		} else {
			SwingComponentQueryTask<String> task = new SwingComponentQueryTask<String>() {

				@Override
				protected String query() {
					return statusMessageLabel.getText();
				}
			};

			SwingUtilities.invokeLater(task);

			try {
				return task.get();
			} catch (InterruptedException ex) {
				logger.error(ex.getMessage(), ex);
				return null;
			} catch (ExecutionException ex) {
				logger.error(ex.getMessage(), ex);
				return null;
			}
		}
	}

	/**
	 * @return {@code true} - if animaation running
	 */
	public Boolean isBusy() {
		if (SwingUtilities.isEventDispatchThread()) {
			return busyIconTimer.isRunning();
		} else {
			SwingComponentQueryTask<Boolean> task = new SwingComponentQueryTask<Boolean>() {

				@Override
				protected Boolean query() {
					return busyIconTimer.isRunning();
				}
			};

			SwingUtilities.invokeLater(task);

			try {
				return task.get();
			} catch (InterruptedException ex) {
				logger.error(ex.getMessage(), ex);
				return null;
			} catch (ExecutionException ex) {
				logger.error(ex.getMessage(), ex);
				return null;
			}
		}
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar(){
            public JToolTip createToolTip()
            {
                return new JMultiLineToolTip();
            }
        };

        setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        setMaximumSize(new java.awt.Dimension(32767, 25));
        setMinimumSize(new java.awt.Dimension(0, 25));
        setPreferredSize(new java.awt.Dimension(605, 25));

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        statusAnimationLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hu/astrid/viewer/gui/busyicons/idle-icon.png"))); // NOI18N

        progressBar.setFocusable(false);
        progressBar.setIndeterminate(true);
        progressBar.setString("");
        progressBar.setStringPainted(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(statusMessageLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 425, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusAnimationLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusAnimationLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 21, Short.MAX_VALUE)
            .addComponent(statusMessageLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 21, Short.MAX_VALUE)
            .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 21, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JProgressBar progressBar;
    protected javax.swing.JLabel statusAnimationLabel;
    protected javax.swing.JLabel statusMessageLabel;
    // End of variables declaration//GEN-END:variables

	@Override
	public void propertyChange(final java.beans.PropertyChangeEvent evt) {
		if (!SwingUtilities.isEventDispatchThread()) {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {

					@Override
					public void run() {
						StatusBar.this.propertyChange(evt);
					}
				});
			} catch (InterruptedException ex) {
				logger.warn("propertyChange interrupted");
			} catch (InvocationTargetException ex) {
				logger.error(ex.getMessage(), ex);
			}
			return;
		}

		String propertyName = evt.getPropertyName();
		if (JOB_STARTED.equals(propertyName)) {
			//Start animation
			statusAnimationLabel.setIcon(busyIcons[0]);
			busyIconIndex = 0;
			busyIconTimer.start();
			progressBar.setVisible(true);

			//Display and store message
			if (evt.getNewValue() != null) {
				isBusyMessage = true;
				messageTimer.stop();
				statusMessageLabel.setText(evt.getNewValue().toString());
				setWindowCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));

				jobList.add(evt.getNewValue().toString());
			} else {
				jobList.add("");
			}

			logger.trace("start '" + evt.getNewValue() + "' list: " + jobList.size());
		} else if (JOB_DONE.equals(propertyName)) {
			//Remove job
			String message = "";
			if (evt.getOldValue() != null) {
				message = evt.getOldValue().toString();
			}
			if (!jobList.remove(message)) {
				logger.warn("no such job '" + message + "'");
			}

			//Revert message
			if (statusMessageLabel.getText().equals(message)) {
				message = "";
				for (String s : jobList) {
					if (s.length() > 0) {
						message = s;
						break;
					}
				}
				statusMessageLabel.setText(message);

				if (message.length() == 0) {
					isBusyMessage = false;
				}
			}

			logger.trace("end '" + evt.getOldValue() + "' list: " + jobList.size());

			//Stop animation if no other requests
			if (jobList.size() == 0) {
				if (progressBar.isIndeterminate()) {
					busyIconTimer.stop();
					progressBar.setVisible(false);
					statusAnimationLabel.setIcon(idleIcon);
					isBusyMessage = false;
					statusMessageLabel.setText("");
				}
				setWindowCursor(java.awt.Cursor.getDefaultCursor());
			}
		} else if (MESSAGE.equals(propertyName)) {
			//Show a message if request message inst shown
			if (!isBusyMessage) {
				String text = (String) (evt.getNewValue());
				statusMessageLabel.setText((text == null) ? "" : text);
				messageTimer.restart();
			}
		} else if (PROGRESS_VALUE.equals(propertyName)) {
			ProgressValue value = (ProgressValue) evt.getNewValue();
			if (value.value < 100) {
				if (progressValues.put(value.name, value.value) == null) {
					progressList.add(value.name);
					logger.trace("progress '" + value.name + "' started");
				}
			}
			busyIconTimer.start();

			if (progressBar.isIndeterminate()) {
				progressBar.setIndeterminate(false);
				progressbarTimer.start();
				progressBar.setVisible(true);
			} else if (progressBar.getString().equals(value.name)) {
				progressBar.setValue(value.value);
			}

			if (value.value > 100) {
				progressValues.remove(value.name);
				Iterator<String> iterator = progressList.iterator();
				while (iterator.hasNext()) {
					String progressName = iterator.next();
					if (progressName.equals(value.name)) {
						iterator.remove();
						break;
					}
				}
//				if (progressBar.getString().equals(value.name)) {
					progressbarTimer.stop();
					progressbarTimer.start();
//				}

				//Stop every animation if no active jobs
				if (progressList.isEmpty()) {
					progressBar.setIndeterminate(true);
					progressBar.setValue(0);
					progressBar.setString("");
					progressbarTimer.stop();
					progressIndex = -1;

					if (jobList.isEmpty()) {
						busyIconTimer.stop();
						progressBar.setVisible(false);
						statusAnimationLabel.setIcon(idleIcon);
					}
				}

				logger.trace("progress '" + value.name + "' ended");
			}
		}
	}

	/**
	 * Set containing windows cursor. If this component isnt added as fist level
	 * component to a JFrame, it has to be overriden
	 * @param cursor
	 */
	private void setWindowCursor(Cursor cursor) {
		this.getTopLevelAncestor().setCursor(cursor);
	}

	/**
	 * Value object for changing progressbar value
	 */
	public static class ProgressValue {

		/**Name of target progress*/
		public final String name;
		/**Value of target progress*/
		public final int value;

		/**
		 *
		 * @param name name of target progress
		 * @param value value of target progress
		 */
		public ProgressValue(String name, int value) {
			this.name = name;
			this.value = value;
		}
	}
}
