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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.lang.reflect.Method;

import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 * Asztali alkalmazások esetén MVC vezérlő komponensének ősosztálya. A vezérlő teremt kacsolatot modell és nézet között, ezért mindkettőt regisztrálni kell a vezérlőnél.
 * Modell változása estén értesíti a nézeteket. Nézet változása estén a teendőket a kontkrét megvalósításoknak kell konkretizálni.
 * A változás szempontjából fontos tulajdonságok neveit szintén a konkrét megvalósítások tartalmazzák.
 * @author Szuni
 */
public abstract class AbstractController implements PropertyChangeListener {

	/**Regisztrált modellek*/
	protected ArrayList<AbstractModel> registeredModels;
	/**Regisztrált nézetek*/
	protected ArrayList<AbstractView> registeredViews;
	/** Default logger */
	private static final Logger logger = Logger.getLogger(AbstractController.class);

	/**
	 * Vezérlő létrehozása. Előkészíti a nézetek és modellek regisztrációját.
	 */
	public AbstractController() {
		this.registeredModels = new ArrayList<AbstractModel>();
		this.registeredViews = new ArrayList<AbstractView>();
	}

	/**
	 * Új modell regisztrálása. Ezzel egyidőben a modellnél is regisztréálásra kerül
	 * a vezérlő, mint eseményfigyelő.
	 * @param model
	 */
	public void addModel(AbstractModel model) {
		registeredModels.add(model);
		model.addPropertyChangeListener(this);
	}

	/**
	 * Modell regisztrációjának törlése. Ezzel egyidőben a modellnél is törlésre kerül a vezérlő.
	 * @param model
	 */
	public void removeModel(AbstractModel model) {
		registeredModels.remove(model);
		model.removePropertyChangeListener(this);
	}

	/**
	 * Új nézet regisztárlása.
	 * @param view
	 */
	public void addView(AbstractView view) {
		registeredViews.add(view);
	}

	/**
	 * Nézet regisztrációjának törlése.
	 * @param view
	 */
	public void removeView(AbstractView view) {
		registeredViews.remove(view);
	}

	/**
	 * Modellben történt változásról értesíti a nézeteket. Modell által kiveáltott
	 * eseményre reagálva minden regisztrált nézetnek elküldi azt.
	 * @param evt
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		for (AbstractView view : registeredViews) {
			view.modelPropertyChange(evt);
		}
	}

	/**
	 * Modell valamelyik tulajdonságát beállító metódus hívható meg. Amint a
	 * tulajdonság megváltozott, a változásról a regisztrált nézetek is értesítést
	 * kapnak. A beállító metódust a reflection adta lehetőségeket használva keresi
	 * meg, minden regisztrált modellnél próbáljozik. Ha adott modellnek nincs
	 * megfelelő metódusa, NoSuchMethodException keletkezik, amelyet csak jelzünk
	 * és nem adódik tovább.
	 *
	 * @param propertyName beállítandó tulajdonság neve, érdemes a konkrét vezérlő osztályba konstansként felvenni
	 * @param newValue = tulajdonság új értéke
	 */
	public void setModelProperty(String propertyName, Object... newValue) {
		boolean isPropertySet = false;
		Class[] classes = new Class[newValue.length];
		for (int i = 0; i < newValue.length; ++i) {
			classes[i] = newValue[i].getClass();
		}
		for (AbstractModel model : registeredModels) {
			try {
				Method method = model.getClass().getMethod("set" + propertyName, classes);
				method.invoke(model, newValue);
				isPropertySet = true;
			} catch (NoSuchMethodException ex) {
				logger.warn("No such method " + ex.getMessage());
			} catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
			}
		}
		if (!isPropertySet) {
			logger.warn("No property set with name " + propertyName);
		}
	}

//	public void loadModelProperty(String propertyName, Object newValue) {
//		boolean isPropertySet = false;
//		for (AbstractModel model : registeredModels) {
//			try {
//				Method method = model.getClass().getMethod(propertyName, new Class[]{newValue.getClass()});
//				method.invoke(model, newValue);
//			} catch (NoSuchMethodException ex) {
//				logger.warn("No such method " + ex.getMessage());
//			} catch (Exception ex) {
//				logger.error(ex.getMessage(), ex);
//			}
//			isPropertySet = true;
//		}
//		if (!isPropertySet) {
//			logger.warn("No property set with name " + propertyName);
//		}
//	}
}
