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

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the header of BAM files. In addition to the standard mapping file
 * header, it contains the names and lengths of the reference sequences. These
 * two piece of information should correspond to each other.
 */
public class BamHeader extends MappingHeader {

    /**
     * The list of reference sequence names.
     */
    private List<String> referenceNames;
    /**
     * the list of reference sequence lengths.
     */
    private List<Integer> referenceLengths;

    /**
     * Creates an empty header.
     */
    public BamHeader() {
        super();
        this.referenceNames = new ArrayList<String>();
        this.referenceLengths = new ArrayList<Integer>();
    }

    /**
     * Adds a reference sequence to the header.
     *
     * @param name
     *            the name of the reference sequence
     * @param length
     *            the length of the reference sequence
     */
    public void addReference(String name, int length) {
        this.referenceNames.add(name);
        this.referenceLengths.add(length);
    }

    /**
     * Returns the name of the reference sequence at a given index.
     *
     * @param index
     *            the index of the reference sequence
     * @return the name of the reference sequence
     */
    public String getName(int index) {
        return this.referenceNames.get(index);
    }

    /**
     * Returns the length of the reference sequence at a given index.
     *
     * @param index
     *            the index of the reference sequence
     * @return the length of the reference sequence
     */
    public int getLength(int index) {
        return this.referenceLengths.get(index);
    }

    /**
     * Returns the index of a reference sequence given by its name.
     *
     * @param referenceName
     *            the name of the reference sequence
     * @return the index of the reference sequence
     */
    public int indexOf(String referenceName) {
        return this.referenceNames.indexOf(referenceName);
    }

    /**
     * @return the list of reference sequence names
     */
    public List<String> getReferenceNames() {
        return referenceNames;
    }

    /**
     * @return the list of reference sequence lengths
     */
    public List<Integer> getReferenceLengths() {
        return referenceLengths;
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof BamHeader)) {
            return false;
        }

        BamHeader bamH = (BamHeader) obj;

        if (!(this.toString().equals(bamH.toString()))) {
            return false;
        }

        for (String refName : referenceNames) {
            int indexOwn = this.indexOf(refName);
            int index = bamH.indexOf(refName);
            if (indexOwn != index) {
                return false;
            }

            int lengthOwn = this.getLength(indexOwn);
            int length = bamH.getLength(indexOwn);
            if (lengthOwn != length) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89
                * hash
                + (this.referenceNames != null ? this.referenceNames.hashCode()
                : 0);
        hash = 89
                * hash
                + (this.referenceLengths != null ? this.referenceLengths.hashCode() : 0);
        return hash;
    }
}
