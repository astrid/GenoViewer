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

package hu.astrid.mvc.swing;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Asztali alkalmazások esetén MVC modell komponensének ősosztálya. Figyelők nyilvántartásáért és értesítéséért felelős.
 * @author Szuni
 */
public abstract class AbstractModel {

	/**Figyelők regisztrációját tárolja*/
	private PropertyChangeSupport propertyChangeSupport;

	/**
	 * Modell létrehozása. Előkészíti a figyelők regisztrációját
	 */
	public AbstractModel() {
		propertyChangeSupport = new PropertyChangeSupport(this);
	}

	/**
	 * Új propertychangelistener regisztrálása
	 * @param listener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	/**
	 * Propertychangelistener regisztrációjának törlése
	 * @param listener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	/**
	 * Tulajdonság megváltozásáról értesíti a figyelőket
	 * @param propertyName megváltozott tulajdonság neve
	 * @param oldValue régi érték
	 * @param newValue új érték
	 */
	protected void firePropertyChange(String propertyName, Object oldValue,
			Object newValue) {
		propertyChangeSupport.firePropertyChange(propertyName, oldValue,
				newValue);
	}
}
