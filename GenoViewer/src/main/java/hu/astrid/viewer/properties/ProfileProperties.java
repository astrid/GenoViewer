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

package hu.astrid.viewer.properties;

import hu.astrid.viewer.gui.content.alignment.ReadInfoPanel;
import java.util.Properties;
import java.awt.Color;

import org.apache.log4j.Logger;

/**
 *
 * @author onagy
 */
public class ProfileProperties {

	private final String profileName;
	private final Properties properties;
	/**
	 * Value for {@link ReadDisplayType ReadDisplayType}. {@code SHORT, LONG}
	 */
	private ReadDisplayType readDisplayType = ReadDisplayType.SHORT;
	/**
	 *Value for default {@link SequenceDisplayMode SequenceDisplayMode} {@code NUCLEOTIDE, COLOR, BOTH}
	 */
	private SequenceDisplayMode sequenceDisplayMode = SequenceDisplayMode.NUCLEOTIDE;
	/**
	 * Default constant for showing SNPs
	 */
	public static final boolean DEF_SHOW_SNPS = false;
	/**
	 * Default constant for showing read errors
	 */
	public static final boolean DEF_SHOW_READ_ERRORS = false;
	/**
	 * Default constant for showing direction
	 */
	public static final boolean DEF_SHOW_DIRECTION = false;
	/**
	 * Default constant for showing Navigation Panel
	 */
	public static boolean DEF_SHOW_NAVIGATION_PANEL = false;
	/**
	 * Default constant for showing Coverage Panel
	 */
	public static final boolean DEF_SHOW_COVERAGE_PANEL = false;
	/**
	 * Default constant for showing Feature Table
	 */
	public static final boolean DEF_SHOW_FEATURE_TABLE = false;
	/**
	 * Constant for insertion {@link Color Color}
	 */
	private Color insertionColor = Color.MAGENTA;
	/**
	 * Backgroud color of gaps in delitions
	 */
	private Color delitionColor = Color.DARK_GRAY;
	/**
	 * Default constant for read error {@link Color Color}
	 */
	public static final Color DEF_READ_ERROR_COLOR = Color.ORANGE;
	/**
	 * Default constant for snp {@link Color Color}
	 */
	public static final Color DEF_SNP_COLOR = Color.GRAY;
	/**
	 * Default constant for direction background {@link Color Color}
	 */
	public static final Color DEF_DIRECTION_BACKGROND_COLOR = Color.WHITE;
	/**
	 * Default constant for direction indicator {@link Color Color}
	 */
	public static final Color DEF_DIRECTION_INDICATOR_COLOR = Color.RED;
	/**
	 * Default constant for annotation type group {@link Color Color}
	 */
	public static final Color DEF_ANNOTATION_GROUP_COLOR = Color.GREEN;
	/**
	 * Default constant for annotation type feature {@link Color Color}
	 */
	public static final Color DEF_ANNOTATION_FEATURE_COLOR = Color.RED;
	/**
	 * Constant for text {@link Color Color}
	 */
	private Color textColor = Color.BLACK;
	/**
	 * Color for zoomed text (filled rectangle without characters)
	 */
	private Color zoomedTextColor = Color.lightGray;
	/**
	 * Constant for {@link Color color0}
	 */
	private Color color0 = new Color(75, 100, 255);
	/**
	 * Constant for {@link Color color1}
	 */
	private Color color1 = Color.green;
	/**
	 * Constant for {@link Color color2}
	 */
	private Color color2 = Color.yellow;
	/**
	 * Constant for {@link Color color3}
	 */
	private Color color3 = new Color(255, 75, 75);
	/**
	 * Color for reads from positive strand
	 */
	private Color positiveStrandColor = Color.green;
	/**
	 * Color for reads from negative strand
	 */
	private Color negativeStrandColor = Color.red;
	/**
	 * Color of coverage chart
	 */
	private Color coverageColor = Color.green;
	/**
	 * Color for non specific reads
	 */
	private Color nonSpecificColor = new Color(255, 200, 0);
	/**
	 * Value for higlighting non specific reads
	 */
	private boolean nonSpecificHighlight = false;
	/**
	 * Value for displaying advanced read inforamtions
	 */
	private boolean advancedReadInfosDisplayed = false;
	private Color manualSelectionColor = Color.black;
	private Color autoSelectionColor = Color.cyan;
	/** Default logger */
	private static final Logger logger = Logger.getLogger(ProfileProperties.class);
	/** Panels content need to reload to apply properties right*/
	private boolean needReload = false;

	/**
	 * Creates new object from the properties with the given name as profile name
	 * @param properties
	 * @param profileName
	 */
	public ProfileProperties(Properties properties, String profileName) {
		this.profileName = profileName;
		this.properties = properties;
		//Temp string for getting property value
		String value = null;

		try {
			readDisplayType = ReadDisplayType.valueOf(properties.getProperty("readsShowType"));
		} catch (NullPointerException ex) {
			properties.setProperty("readsShowType", readDisplayType.toString());
		}
		try {
			sequenceDisplayMode = SequenceDisplayMode.valueOf(properties.getProperty("sequenceShowMode"));
		} catch (NullPointerException ex) {
			properties.setProperty("sequenceShowMode", sequenceDisplayMode.toString());
		}

		value = properties.getProperty("nonSpecificHighlight");
		if (value != null) {
			nonSpecificHighlight = Boolean.valueOf(value);
		} else {
			properties.setProperty("nonSpecificHighlight", Boolean.toString(nonSpecificHighlight));
		}
		value = null;

		try {
			autoSelectionColor = new Color(Integer.parseInt(properties.getProperty("autoSelectionColor")));
		} catch (NumberFormatException ex) {
			properties.setProperty("autoSelectionColor", Integer.toString(autoSelectionColor.getRGB()));
		}
		try {
			manualSelectionColor = new Color(Integer.parseInt(properties.getProperty("manualSelectionColor")));
		} catch (NumberFormatException ex) {
			properties.setProperty("manualSelectionColor", Integer.toString(manualSelectionColor.getRGB()));
		}
		try {
			textColor = new Color(Integer.parseInt(properties.getProperty("textColor")));
		} catch (NumberFormatException ex) {
			properties.setProperty("textColor", Integer.toString(textColor.getRGB()));
		}
		try {
			zoomedTextColor = new Color(Integer.parseInt(properties.getProperty("zoomedTextColor")));
		} catch (NumberFormatException ex) {
			properties.setProperty("zoomedTextColor", Integer.toString(zoomedTextColor.getRGB()));
		}
		try {
			nonSpecificColor = new Color(Integer.parseInt(properties.getProperty("nonSpecificColor")));
		} catch (NumberFormatException ex) {
			properties.setProperty("nonSpecificColor", Integer.toString(nonSpecificColor.getRGB()));
		}
		try {
			color0 = new Color(Integer.parseInt(properties.getProperty("0Color")));
		} catch (NumberFormatException ex) {
			properties.setProperty("0Color", Integer.toString(color0.getRGB()));
		}
		try {
			color1 = new Color(Integer.parseInt(properties.getProperty("1Color")));
		} catch (NumberFormatException ex) {
			properties.setProperty("1Color", Integer.toString(color1.getRGB()));
		}
		try {
			color2 = new Color(Integer.parseInt(properties.getProperty("2Color")));
		} catch (NumberFormatException ex) {
			properties.setProperty("2Color", Integer.toString(color2.getRGB()));
		}
		try {
			color3 = new Color(Integer.parseInt(properties.getProperty("3Color")));
		} catch (NumberFormatException ex) {
			properties.setProperty("3Color", Integer.toString(color3.getRGB()));
		}
		try {
			insertionColor = new Color(Integer.parseInt(properties.getProperty("insertionColor")));
		} catch (NumberFormatException ex) {
			properties.setProperty("insertionColor", Integer.toString(insertionColor.getRGB()));
		}
		try {
			delitionColor = new Color(Integer.parseInt(properties.getProperty("delitionColor")));
		} catch (NumberFormatException ex) {
			properties.setProperty("delitionColor", Integer.toString(delitionColor.getRGB()));
		}
		try {
			positiveStrandColor = new Color(Integer.parseInt(properties.getProperty("positiveAlignmnentStrandColor")));
		} catch (NumberFormatException ex) {
			properties.setProperty("positiveAlignmnentStrandColor", Integer.toString(positiveStrandColor.getRGB()));
		}
		try {
			negativeStrandColor = new Color(Integer.parseInt(properties.getProperty("negativeAlignmnentStrandColor")));
		} catch (NumberFormatException ex) {
			properties.setProperty("negativeAlignmnentStrandColor", Integer.toString(negativeStrandColor.getRGB()));
		}
		try {
			coverageColor = new Color(Integer.parseInt(properties.getProperty("coverageColor")));
		} catch (NumberFormatException ex) {
			properties.setProperty("coverageColor", Integer.toString(coverageColor.getRGB()));
		}

		value = properties.getProperty("advancedInfos");
		if (value != null) {
			advancedReadInfosDisplayed = Boolean.valueOf(value);
		} else {
			properties.setProperty("advancedInfos", Boolean.toString(advancedReadInfosDisplayed));
		}
		value = null;
	}

	/**
	 * @return reads display type {@code SHORT, LONG}
	 */
	public ReadDisplayType getReadDisplayType() {
		return readDisplayType;
	}

	/**
	 * @param readsDisplayType reads show type {@code SHORT, LONG}
	 */
	public void setReadDisplayType(ReadDisplayType readsDisplayType) {
		this.readDisplayType = readsDisplayType;
		properties.setProperty("readsShowType", readsDisplayType.toString());
	}

	/**
	 * @return show mode of contigs and reads
	 */
	public SequenceDisplayMode getSequenceDisplayMode() {
		return sequenceDisplayMode;
	}

	/**
	 * @param sequenceDisplayMode show mode of contigs and reads {@code NUCLEOTIDE, COLOR, BOTH}
	 */
	public void setSequenceDisplayMode(SequenceDisplayMode sequenceDisplayMode) {
		this.sequenceDisplayMode = sequenceDisplayMode;
		properties.setProperty("sequenceShowMode", sequenceDisplayMode.toString());
	}

	/**
	 * @return {@code true} - if reads strand direction is showed<br>
	 * {@code false} - if not showed
	 */
	public boolean isShowDirection() {
		boolean retVal = DEF_SHOW_DIRECTION;
		String data = properties.getProperty("showDirection");
		if (data != null) {
			retVal = Boolean.parseBoolean(data);
		} else {
			properties.setProperty("showDirection", Boolean.toString(DEF_SHOW_DIRECTION));
		}
		return retVal;
	}

	/**
	 *
	 * @param showDirection
	 */
	public void setShowDirection(boolean showDirection) {
		properties.setProperty("showDirection", Boolean.toString(showDirection));
	}

	/**
	 * @return {@code true} - if read errors are showed in color code mode<br>
	 * {@code false} - if not showed
	 */
	public boolean isShowReadErrors() {
		boolean retVal = DEF_SHOW_READ_ERRORS;
		String data = properties.getProperty("showReadErrors");
		if (data != null) {
			retVal = Boolean.parseBoolean(data);
		} else {
			properties.setProperty("showReadErrors", Boolean.toString(DEF_SHOW_READ_ERRORS));
		}
		return retVal;
	}

	/**
	 *
	 * @param showReadErrors
	 */
	public void setShowReadErrors(boolean showReadErrors) {
		properties.setProperty("showReadErrors", Boolean.toString(showReadErrors));
	}

	/**
	 * @return {@code true} - if SNPs are showed<br>
	 * {@code false} - if not showed
	 */
	public boolean isShowSNPs() {
		boolean retVal = DEF_SHOW_SNPS;
		String data = properties.getProperty("showSNPs");
		if (data != null) {
			retVal = Boolean.parseBoolean(data);
		} else {
			properties.setProperty("showSNPs", Boolean.toString(DEF_SHOW_SNPS));
		}
		return retVal;
	}

	/**
	 *
	 * @param showSNPs
	 */
	public void setShowSNPs(boolean showSNPs) {
		properties.setProperty("showSNPs", Boolean.toString(showSNPs));
	}

	/**
	 *
	 * @return value for higlighting non specific reads
	 */
	public boolean isNonSpecificHighlight() {
		return nonSpecificHighlight;
	}

	/**
	 *
	 * @param nonSpecificHighlight value for higlighting non specific reads
	 */
	public void setNonSpecificHighlight(boolean nonSpecificHighlight) {
		properties.setProperty("nonSpecificHighlight", Boolean.toString(nonSpecificHighlight));
		this.nonSpecificHighlight = nonSpecificHighlight;
	}

	/**
	 *
	 * @return color of selection by auto scrolling
	 */
	public Color getAutoSelectionColor() {
		return autoSelectionColor;
	}

	/**
	 *
	 * @param autoSelectionColor color of selection by auto scrolling
	 */
	public void setAutoSelectionColor(Color autoSelectionColor) {
		this.autoSelectionColor = autoSelectionColor;
		properties.setProperty("autoSelectionColor", Integer.toString(autoSelectionColor.getRGB()));
	}

	/**
	 *
	 * @return color of manual selection
	 */
	public Color getManualSelectionColor() {
		return manualSelectionColor;
	}

	/**
	 *
	 * @param manualSelectionColor color of manual selection
	 */
	public void setManualSelectionColor(Color manualSelectionColor) {
		this.manualSelectionColor = manualSelectionColor;
		properties.setProperty("manualSelectionColor", Integer.toString(manualSelectionColor.getRGB()));
	}

	/*******************
	 * Sequence colors *
	 *******************/
	/**
	 * @param color color of {@link SequenceLabel}s characters
	 */
	public void setTextColor(Color color) {
		textColor = color;
		properties.setProperty("textColor", Integer.toString(color.getRGB()));
	}

	/**
	 *
	 * @return color of {@link SequenceLabel}s characters
	 */
	public Color getTextColor() {
		return textColor;
	}

	/**
	 *
	 * @return color for zoomed text (filled rectangle without characters)
	 */
	public Color getZoomedTextColor() {
		return zoomedTextColor;
	}

	/**
	 *
	 * @param color color for zoomed text (filled rectangle without characters)
	 */
	public void setZoomedTextColor(Color color) {
		properties.setProperty("zoomedTextColor", Integer.toString(color.getRGB()));
		this.zoomedTextColor = color;
	}

	/**
	 *
	 * @return color for non spectific reads
	 */
	public Color getNonSpecificColor() {
		return nonSpecificColor;
	}

	/**
	 *
	 * @param color color for non specific reads
	 */
	public void setNonSpecificColor(Color color) {
		properties.setProperty("nonSpecificColor", Integer.toString(color.getRGB()));
		this.nonSpecificColor = color;
	}

	/**
	 *
	 * @return background color of 0 color code
	 */
	public Color get0Color() {
		return color0;
	}

	/**
	 *
	 * @param color background color of 0 color code
	 */
	public void set0Color(Color color) {
		color0 = color;
		properties.setProperty("0Color", Integer.toString(color.getRGB()));
	}

	/**
	 *
	 * @return background color of 1 color code
	 */
	public Color get1Color() {
		return color1;
	}

	/**
	 *
	 * @param color background color of 0 color code
	 */
	public void set1Color(Color color) {
		color1 = color;
		properties.setProperty("1Color", Integer.toString(color.getRGB()));
	}

	/**
	 *
	 * @return background color of 0 color code
	 */
	public Color get2Color() {
		return color2;
	}

	/**
	 *
	 * @param color background color of 0 color code
	 */
	public void set2Color(Color color) {
		color2 = color;
		properties.setProperty("2Color", Integer.toString(color.getRGB()));
	}

	/**
	 *
	 * @return background color of 0 color code
	 */
	public Color get3Color() {
		return color3;
	}

	/**
	 *
	 * @param color background color of 0 color code
	 */
	public void set3Color(Color color) {
		color3 = color;
		properties.setProperty("3Color", Integer.toString(color.getRGB()));
	}

	/***********************
	 * Read features color *
	 ***********************/
	/**
	 *
	 * @return
	 */
	public Color getInsertionColor() {
		return insertionColor;
	}

	/**
	 *
	 * @param color
	 */
	public void setInsertionColor(Color color) {
		insertionColor = color;
		properties.setProperty("insertionColor", Integer.toString(color.getRGB()));
	}

	/**
	 *
	 * @return background color of gaps in delitions
	 */
	public Color getDelitionColor() {
		return delitionColor;
	}

	/**
	 *
	 * @param color background color of gaps in delitions
	 */
	public void setDelitionColor(Color color) {
		delitionColor = color;
		properties.setProperty("delitionColor", Integer.toString(color.getRGB()));
	}

	/**
	 *
	 * @return
	 */
	public Color getReadErrorColor() {
		Color color = DEF_READ_ERROR_COLOR;
		String data = properties.getProperty("readErrorColor");
		if (data != null) {
			color = new Color(Integer.parseInt(data));
		} else {
			properties.setProperty("readErrorColor", Integer.toString(DEF_READ_ERROR_COLOR.getRGB()));
		}
		return color;
	}

	/**
	 *
	 * @param color
	 */
	public void setReadErrorColor(Color color) {
		properties.setProperty("readErrorColor", Integer.toString(color.getRGB()));
	}

	/**
	 *
	 * @return
	 */
	public Color getSNPColor() {
		Color color = DEF_SNP_COLOR;
		String data = properties.getProperty("snpColor");
		if (data != null) {
			color = new Color(Integer.parseInt(data));
		} else {
			properties.setProperty("snpColor", Integer.toString(DEF_SNP_COLOR.getRGB()));
		}
		return color;
	}

	/**
	 *
	 * @param color
	 */
	public void setSNPColor(Color color) {
		properties.setProperty("snpColor", Integer.toString(color.getRGB()));
	}

	/**
	 *
	 * @return
	 */
	public Color getDirectionBackgroundColor() {
		Color color = DEF_DIRECTION_BACKGROND_COLOR;
		String data = properties.getProperty("directionBackgroundColor");
		if (data != null) {
			color = new Color(Integer.parseInt(data));
		} else {
			properties.setProperty("directionBackgroundColor", Integer.toString(DEF_DIRECTION_BACKGROND_COLOR.getRGB()));
		}
		return color;
	}

	/**
	 *
	 * @param color
	 */
	public void setDirectionBackgroundColor(Color color) {
		properties.setProperty("directionBackgroundColor", Integer.toString(color.getRGB()));
	}

	/**
	 *
	 * @return
	 */
	public Color getDirectionIndicatorColor() {
		Color color = DEF_DIRECTION_INDICATOR_COLOR;
		String data = properties.getProperty("directionIndicatorColor");
		if (data != null) {
			color = new Color(Integer.parseInt(data));
		} else {
			properties.setProperty("directionIndicatorColor", Integer.toString(DEF_DIRECTION_INDICATOR_COLOR.getRGB()));
		}
		return color;
	}

	/**
	 *
	 * @param color
	 */
	public void setDirectionIndicatorColor(Color color) {
		properties.setProperty("directionIndicatorColor", Integer.toString(color.getRGB()));
	}

	/**
	 *
	 * @return the default or associated Color of annotation groups
	 */
	public Color getAnnotationGroupColor() {
		Color color = DEF_ANNOTATION_GROUP_COLOR;
		String data = properties.getProperty("annotationGroupColor");
		if (data != null) {
			color = new Color(Integer.parseInt(data));
		} else {
			properties.setProperty("annotationGroupColor", Integer.toString(DEF_ANNOTATION_GROUP_COLOR.getRGB()));
		}
		return color;
	}

	/**
	 *
	 * @param color the new color for annotation groups
	 */
	public void setAnnotationGroupColor(Color color) {
		properties.setProperty("annotationGroupColor", Integer.toString(color.getRGB()));
	}

	/**
	 *
	 * @return the default or associated Color of annotation features
	 */
	public Color getAnnotationFeatureColor() {
		Color color = DEF_ANNOTATION_FEATURE_COLOR;
		String data = properties.getProperty("annotationFeatureColor");
		if (data != null) {
			color = new Color(Integer.parseInt(data));
		} else {
			properties.setProperty("annotationFeatureColor", Integer.toString(DEF_ANNOTATION_FEATURE_COLOR.getRGB()));
		}
		return color;
	}

	/**
	 *
	 * @param color the new color for annotation features
	 */
	public void setAnnotationFeatureColor(Color color) {
		properties.setProperty("annotationFeatureColor", Integer.toString(color.getRGB()));
	}

	/**
	 *
	 * @return color for reads from positive strand
	 */
	public Color getPositiveStrandColor() {
		return positiveStrandColor;
	}

	/**
	 *
	 * @param color color for reads from positive strand
	 */
	public void setPositiveStrandColor(Color color) {
		this.positiveStrandColor = color;
		properties.setProperty("positiveAlignmnentStrandColor", Integer.toString(color.getRGB()));
	}

	/**
	 *
	 * @return color for reads from positive strand
	 */
	public Color getNegativeStrandColor() {
		return negativeStrandColor;
	}

	/**
	 *
	 * @param color color for reads from positive strand
	 */
	public void setNegativeStrandColor(Color color) {
		this.negativeStrandColor = color;
		properties.setProperty("negativeAlignmnentStrandColor", Integer.toString(color.getRGB()));
	}

	/********************
	 * Panels for reads *
	 ********************/
	/**
	 * @return 
	 */
	public boolean isShowNavigationPanel() {
		boolean showNavigationPanel = DEF_SHOW_NAVIGATION_PANEL;
		String data = properties.getProperty("showNavigationPanel");
		if (data != null) {
			showNavigationPanel = Boolean.parseBoolean(data);
		} else {
			properties.setProperty("showNavigationPanel", Boolean.toString(DEF_SHOW_NAVIGATION_PANEL));
		}
		return showNavigationPanel;
	}

	/**
	 * 
	 * @param showNavigationPanel
	 */
	public void setShowNavigationPanel(boolean showNavigationPanel) {
		properties.setProperty("showNavigationPanel", Boolean.toString(showNavigationPanel));
	}

	/**
	 *
	 * @return
	 */
	public boolean isShowCoveragePanel() {
		boolean showCoveragePanel = DEF_SHOW_COVERAGE_PANEL;
		String data = properties.getProperty("showCoveragePanel");
		if (data != null) {
			showCoveragePanel = Boolean.parseBoolean(data);
		} else {
			properties.setProperty("showCoveragePanel", Boolean.toString(DEF_SHOW_COVERAGE_PANEL));
		}
		return showCoveragePanel;
	}

	/**
	 *
	 *
	 * @param showCoveragePanel
	 */
	public void setShowCoveragePanel(boolean showCoveragePanel) {
		properties.setProperty("showCoveragePanel", Boolean.toString(showCoveragePanel));
	}

	/**
	 *
	 * @return {@code true} - if advanced infos visible on {@link ReadInfoPanel}
	 */
	public boolean isAdvancedReadInfosDisplayed() {
		return advancedReadInfosDisplayed;
	}

	/**
	 *
	 * @param advancedReadInfosDisplayed visibility of advanced infos on {@link ReadInfoPanel}
	 */
	public void setAdvancedReadInfosDisplayed(boolean advancedReadInfosDisplayed) {
		this.advancedReadInfosDisplayed = advancedReadInfosDisplayed;
		properties.setProperty("advancedInfos", Boolean.toString(advancedReadInfosDisplayed));
	}

	/*******
	 * GFF *
	 *******/
	/**
	 *
	 * @return
	 */
	public boolean isShowFeatureTable() {
		boolean showFeatureTable = DEF_SHOW_FEATURE_TABLE;
		String data = properties.getProperty("showFeatureTable");
		if (data != null) {
			showFeatureTable = Boolean.parseBoolean(data);
		} else {
			properties.setProperty("showFeatureTable", Boolean.toString(DEF_SHOW_FEATURE_TABLE));
		}
		return showFeatureTable;
	}

	/**
	 *
	 * @param showFeatureTable
	 */
	public void setShowFeatureTable(boolean showFeatureTable) {
		properties.setProperty("showFeatureTable", Boolean.toString(showFeatureTable));
	}

	/************
	 * Coverage *
	 ************/
	/**
	 * 
	 * @return color of coverage chart
	 */
	public Color getCoverageColor() {
		return coverageColor;
	}

	/**
	 *
	 * @param color color of coverage chart
	 */
	public void setCoverageColor(Color color) {
		this.coverageColor = color;
		properties.setProperty("coverageColor", Integer.toString(color.getRGB()));
	}

	/**************************
	 * Class handling methods *
	 **************************/
	/**
	 * {@inheritDoc }
	 * @return {@inheritDoc }
	 */
	@Override
	public String toString() {
		return profileName;
	}

	Properties getProperties() {
		return this.properties;
	}

	/**
	 * Prints the content of {@link Properties Properties}
	 */
	public void printProperties() {
		logger.info(properties.toString());
	}

	/**
	 *
	 * @return {@code true} - if panels content need to reload to apply properties right
	 */
	public boolean isNeedReload() {
		return needReload;
	}

	/**
	 * Set needreload to {@code false} to indicate the panels content showing is up to date
	 */
	public void noNeedReload() {
		this.needReload = false;
	}

	/**
	 *
	 * Creates new profile with deep copy, so modification on the original profile does'nt interrupt with the copy
	 * @param newProfileName name of the new profile
	 * @return copy of the original profile with new name newProfileName
	 */
	public ProfileProperties makeCopy(String newProfileName) {
		Properties tempProperties = new Properties();
		synchronized (properties) {
			for (Object key : properties.keySet()) {
				tempProperties.setProperty((String) key, properties.getProperty((String) key));
			}
		}
		return (new ProfileProperties(tempProperties, newProfileName));
	}

	/**
	 *
	 * @return the profile's name
	 */
	public String getProfileName() {
		return this.profileName;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof ProfileProperties)) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		ProfileProperties profileProperties = (ProfileProperties) obj;
		if (this.getReadDisplayType() != profileProperties.getReadDisplayType()) {
			return false;
		} else if (this.getSequenceDisplayMode() != profileProperties.getSequenceDisplayMode()) {
			return false;
		} else if (this.isShowSNPs() != profileProperties.isShowSNPs()) {
			return false;
		} else if (!this.getAutoSelectionColor().equals(profileProperties.getAutoSelectionColor())) {
			return false;
		} else if (!this.getManualSelectionColor().equals(profileProperties.getManualSelectionColor())) {
			return false;
		} else if (!this.getCoverageColor().equals(profileProperties.getCoverageColor())) {
			return false;
		} else if (!this.getInsertionColor().equals(profileProperties.getInsertionColor())) {
			return false;
		} else if (!this.getDelitionColor().equals(profileProperties.getDelitionColor())) {
			return false;
		} else if (!this.getNonSpecificColor().equals(profileProperties.getNonSpecificColor())) {
			return false;
		} else if (this.isNonSpecificHighlight() != profileProperties.isNonSpecificHighlight()) {
			return false;
		} else if (!this.getSNPColor().equals(profileProperties.getSNPColor())) {
			return false;
		} else if (this.isShowReadErrors() != profileProperties.isShowReadErrors()) {
			return false;
		} else if (!this.getReadErrorColor().equals(profileProperties.getReadErrorColor())) {
			return false;
		} else if (this.isShowDirection() != profileProperties.isShowDirection()) {
			return false;
		} else if (!this.getDirectionBackgroundColor().equals(profileProperties.getDirectionBackgroundColor())) {
			return false;
		} else if (!this.getDirectionIndicatorColor().equals(profileProperties.getDirectionIndicatorColor())) {
			return false;
		} else if (this.isShowNavigationPanel() != profileProperties.isShowNavigationPanel()) {
			return false;
		} else if (this.isShowFeatureTable() != profileProperties.isShowFeatureTable()) {
			return false;
		} else if (!this.getPositiveStrandColor().equals(profileProperties.getPositiveStrandColor())) {
			return false;
		} else if (!this.getNegativeStrandColor().equals(profileProperties.getNegativeStrandColor())) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 29 * hash + (this.profileName != null ? this.profileName.hashCode() : 0);
		hash = 29 * hash + (this.properties != null ? this.properties.hashCode() : 0);
		return hash;
	}

	/**
	 * Compare this objet to another, if there are differences that need the panels
	 * content to reload, it will be signed in this object. There differences are
	 * {@link ReadDisplayType}
	 * @param prop other profile proerties for compare
	 */
	public void compareForReload(ProfileProperties prop) {
		if (prop == null || this.getReadDisplayType() != prop.getReadDisplayType()) {
			needReload = true;
		}
	}

	/**
	 * Enum for storing how to fragment reads when rendering
	 */
	public enum ReadDisplayType {

		/**Reads displayed multiple column*/
		SHORT,
		/**Reads displayed in one column under each other*/
		LONG
	}

	/**
	 *  Enum for storing how to render reads
	 */
	public enum SequenceDisplayMode {

		/**Reference contig and reads will be show as nucleotide sequence*/
		NUCLEOTIDE,
		/**Reference contig and reads (if possible) will be shown as color code sequence*/
		COLOR,
		/**Reference contig and reads will be show as nucleotide sequence and (if possible)as color code sequence*/
		BOTH;
	}
}
