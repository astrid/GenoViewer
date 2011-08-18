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

package hu.astrid.mapping.model;

import hu.astrid.core.Strand;
import hu.astrid.mapping.exception.GffFileFormatException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 * An instance of this class represents an GFF record (version 3).
 * <p> http://www.sequenceontology.org/gff3.shtml
 */
public class GffRecord {

    private String seqId;

    private String source;

    private String type;

    /**
     * The starting position of the feature in the sequence.
     */
    private int start;

    /**
     * The ending position of the feature (inclusive).
     */
    private int end;

    /**
     * A floating point value. 
     * If there is no score value, enter ".".
     */
    private String score;

    private Strand starnd;

    private Phase phase;
	/**
	 * {@link java.util.Map} for storing key-value / key-multiple value pairs
	 */
    private Map<String,List<String>> attributes = new HashMap<String, List<String>>();

	/**
	 * {@link org.apache.log4j.Logger} logger for this class
	 */
	private static final Logger logger = Logger.getLogger(GffRecord.class);

    public GffRecord() {}

    /**
     * @return the seqname
     */
    public String getSeqId() {
        return seqId;
    }

    /**
     * @param seqname the seqname to set
     */
    public void setSeqId(String seqId) {
        this.seqId = seqId;
    }

    /**
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * @return the feature
     */
    public String getType() {
        return type;
    }

    /**
     * @param feature the feature to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the start
     */
    public int getStart() {
        return start;
    }

    /**
     * @param start the start to set
     */
    public void setStart(int start) {
        this.start = start;
    }

    /**
     * @return the end
     */
    public int getEnd() {
        return end;
    }

    /**
     * @param end the end to set
     */
    public void setEnd(int end) {
        this.end = end;
    }

    /**
     * @return the score
     */
    public String getScore() {
        return score;
    }

    /**
     * @param score the score to set
     */
    public void setScore(String score) {
        this.score = score;
    }

    /**
     * @return the starnd
     */
    public Strand getStarnd() {
        return starnd;
    }

    /**
     * @param starnd the starnd to set
     */
    public void setStarnd(Strand starnd) {
        this.starnd = starnd;
    }

    /**
     * @return the frame
     */
    public Phase getPhase() {
        return phase;
    }

    /**
     * @param frame the frame to set
     */
    public void setPhase(Phase phase) {
        this.phase = phase;
    }

    /**
     * Add an attribute tag value to GFF record.
     *
     * @param name name of tag
     * @param value value of tag
     */
    public void addAttribute( String name, String value ) {

        if( !attributes.containsKey(name) )
            attributes.put(name, new ArrayList<String>() );

        attributes.get(name).add(value);

    }

    /**
     * Add list of attribute.
     *
     * @param attribTags list of the attribute tags
     */
    public void addAttributes( Map<String,List<String>> attributes ) {

        this.attributes = attributes;

    }

    /**
     *  This method returns values of tag which belong to name.
     * @param name tag name
     */
    public List<String> getAttributeValues(String name ) {

		return attributes.get(name);
    }
/**
 * @param IDs vararg {@link String String} parameter, these elements will be used in order to find the first existing key and the corresponding list of values
 * @return list containing values mapped by one of the key in IDs
 * @throws GffFileFormatException if doesn't exist any key as specified in IDs 
 */
	public String getAttributeValue(String... IDs) throws GffFileFormatException{

		List<String> returnList = null;
		for (String key : IDs) {

			try {

				returnList = attributes.get(key);

				//System.out.println("" + returnList.get(0));
				//System.out.println("Return list found!Key(s):" + Arrays.toString(IDs));

				return returnList.get(0);
			} catch (NullPointerException exc) {
				//doesn't matter try next key...
				;
			}
		}

		throw new GffFileFormatException("Doesn't find any key(s) with name(s) " + Arrays.toString(IDs));
	}

	/**
     *  This method returns occurent names of tags.
     */
    public Set<String> getAttributeNames( ) {

        return attributes.keySet();

    }

    @Override
    public String toString() {

        StringBuilder result = new StringBuilder();

        result.append(seqId).append('\t').append(source).append('\t').append(type).append('\t');

        result.append(start).append('\t').append(end).append('\t').append(score).append('\t');

        result.append(starnd.getValue()).append('\t').append(phase.getValue()).append('\t');

        boolean attrib = false;

        for( String name : attributes.keySet() ) {

            if( attrib )
                result.append(';');
            else
                attrib = true;

            result.append(name).append('=');

            boolean tagValue = false;

            for( String value : attributes.get(name) ) {

                if( tagValue )
                    result.append(',');
                else
                    tagValue = true;

                result.append(value);

            }

        }

        return result.toString();
    }

}
