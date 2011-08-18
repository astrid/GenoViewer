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

package hu.astrid.viewer.util;

import java.io.File;
import java.io.FileFilter;

/**
 * @author onagy
 * {@link java.util.Enumeration Enumeration} for storing different file types in a safely and simple manner
 *
 */
public enum FileTypes {

    /**
     * Enum type for FASTA file type
     */
    FASTA {

	@Override
	public String toString() {

	    return "FASTA";
	}

	@Override
	public FileFilter getFileFilter() {
	    return new FileFilter() {

		@Override
		public boolean accept(File f) {
		    return f.getName().toLowerCase().endsWith(".fasta") || f.getName().toLowerCase().endsWith(".fna") || f.getName().toLowerCase().endsWith(".fa") || f.isDirectory();
		}
	    };
	}
    },
    /**
     * Enum type for BAM file type
     */
    BAM {

	@Override
	public String toString() {

	    return "BAM";
	}

	@Override
	public FileFilter getFileFilter() {
	    return new FileFilter() {

		@Override
		public boolean accept(File f) {
		    return f.getName().toLowerCase().endsWith(".bam") || f.isDirectory();
		}
	    };
	}
    },
    /**
     *Enum type for SAM file type
     */
    SAM {

	@Override
	public String toString() {

	    return "SAM";
	}

	@Override
	public FileFilter getFileFilter() {
	    return new FileFilter() {

		@Override
		public boolean accept(File f) {
		    return f.getName().toLowerCase().endsWith(".sam") || f.isDirectory();
		}
	    };
	}
    },
    /**
     *Enum type for GFF file type
     */
    GFF {

	@Override
	public String toString() {

	    return "GFF";
	}

	@Override
	public FileFilter getFileFilter() {
	    return new FileFilter() {

		@Override
		public boolean accept(File f) {
		    return f.getName().toLowerCase().endsWith(".gff") || f.isDirectory();
		}
	    };
	}
    },
    /**
     *Enum type for viewer profile file type
     */
    PROFILE {

	@Override
	public String toString() {

	    return "PROFILE";
	}

	@Override
	public FileFilter getFileFilter() {
	    return new FileFilter() {

		@Override
		public boolean accept(File f) {
		    return f.getName().toLowerCase().endsWith(".profile");
		}
	    };
	}
    },
    /**
     * Enum type for Workspace
     */
    WORKSPACE {

	@Override
	public String toString() {

	    return "WORKSPACE";
	}

	@Override
	public FileFilter getFileFilter() {

	    return new FileFilter() {

		@Override
		public boolean accept(File f) {
		    return true;
		}
	    };
	}
    };

    /**
     * Method returns an object implementing {@link FileFilter FileFilter} interface based on the enum's type
     */
    public abstract FileFilter getFileFilter();
}
