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

package hu.astrid.viewer.model;

import hu.astrid.core.Nucleotide;
import hu.astrid.mvc.swing.AbstractModel;
import hu.astrid.viewer.Viewer;
import hu.astrid.viewer.gui.StatusBar.ProgressValue;
import hu.astrid.viewer.model.consensus.ConsensusData;
import hu.astrid.viewer.model.mutation.Mutation;
import hu.astrid.viewer.model.mutation.MutationType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

/**
 * @author Mat
 */
public class ViewerConsensusModel extends AbstractModel {

	/**
	 * Indicates consensus load/generation/unload
	 */
	public static final String CONSENSUS_LOAD = "ConsensusSequence";
	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(ViewerConsensusModel.class);
	private ConsensusData consensusData;
	private Future<List<Mutation>> mutations;
	private Future<String> sequence;
	private Future<ConsensusData> generation;

	public void generateConsesus() {
//		synchronized (this) {
		try {
			long start = System.currentTimeMillis();

			ExecutorService executorService = Executors.newFixedThreadPool(2);

			mutations = executorService.submit(new Callable<List<Mutation>>() {

				@Override
				public List<Mutation> call() throws Exception {
					return Viewer.getReadModel().getMutationList();
				}
			});

			sequence = executorService.submit(new Callable<String>() {

				@Override
				public String call() throws Exception {
					return Viewer.getReadModel().getSequenceByReads();
				}
			});

			final List<Mutation> transformations = new ArrayList<Mutation>();
			final List<Mutation> loadedMutations = mutations.get();
			//			logger.info(transformationList.toString());
			final String nucleotiedSequence = sequence.get();

			if (!sequence.isCancelled() && !mutations.isCancelled()) {
				generation = executorService.submit(new Callable<ConsensusData>() {

					@Override
					public ConsensusData call() throws Exception {
						Viewer.startStatusbarJob(Viewer.getLabelResources().getString("statusbarMessagePreparingConsensus"));
						for (Mutation t : loadedMutations) {
							float parameter = 0.5f;
							if (t.getCoverage() > parameter) {
								transformations.add(t);
							}
						}
						return new ConsensusData(transformations, nucleotiedSequence, convertNucleotideSequenceToColor(nucleotiedSequence));
					}
				});
				ConsensusData generatedConsensus = generation.get();
				logger.info("generation time: " + (System.currentTimeMillis() - start));
				synchronized (this) {
					consensusData = generatedConsensus;
					Viewer.stopStatusbarJob(Viewer.getLabelResources().getString("statusbarMessagePreparingConsensus"));
					firePropertyChange(CONSENSUS_LOAD, null, true);
				}
			}
		} catch (InterruptedException ex) {
//			logger.error(e.getMessage(), e);
		} catch (ExecutionException ex) {
			logger.error(ex.getMessage(), ex);
		} catch (CancellationException ex) {
//			logger.error(ex.getMessage(), ex);
		}
//		}
	}

	public static String convertNucleotideSequenceToColor(String nucleotideSequence) {
		StringBuilder result = new StringBuilder(nucleotideSequence.length() - 1);
		for (int i = 1; i < nucleotideSequence.length(); i++) {
			result.append(Nucleotide.valueOf(nucleotideSequence.charAt(i)).getColor(Nucleotide.valueOf(nucleotideSequence.charAt(i - 1))));
		}
		return result.toString();
	}

	public ConsensusData readFromPosition(int position) {
		int end = Viewer.getApplicationProperties().getBufferSize();
		if (consensusData == null) {
			throw new IllegalStateException("Consensus data isn generated!");
		}
		if (position > getConsensusLength()) {
			return new ConsensusData(new ArrayList<Mutation>(), "", "");
		}
		logger.warn("part of sequence: " + position + "-" + (position + end));
		List<Mutation> trf = consensusData.getTransformationsByPosition(position, position + end);
		logger.debug("mutations in sequence part: " + trf.size());
		for (Mutation t : trf) {
			t.setStartPos(t.getStartPos() - position);
		}
		ConsensusData subConsensusData = new ConsensusData(trf, consensusData.getSequence().substring(position, Math.min(position + end, consensusData.getSequence().length())), consensusData.getColorSequence().substring(position, Math.min(position + end, consensusData.getColorSequence().length())));
		return subConsensusData;
	}

	/**
	 * @return length of generated consensus
	 */
	public int getConsensusLength() {
		return consensusData.getSequence().length();
	}

	/**
	 * @return {@code true} - if consensus data is already generated
	 */
	public boolean isConsensusAvailable() {
		return consensusData != null;
	}

	/**
	 * Unload consesnsus data and notify views
	 */
	public void unloadConsensus() {
		synchronized (this) {
			if (sequence != null && sequence.cancel(true)) {
				Viewer.setStatusbarProgresValue(new ProgressValue("generating", 101));
				sequence = null;
			}
			if (mutations != null && mutations.cancel(true)) {
				Viewer.setStatusbarProgresValue(new ProgressValue("searching", 101));
				mutations = null;
			}
			if (generation != null && generation.cancel(true)) {
				Viewer.stopStatusbarJob(Viewer.getLabelResources().getString("statusbarMessagePreparingConsensus"));
				generation = null;
			}
		}
		if (consensusData != null) {
			synchronized (this) {
				consensusData = null;
			}
			firePropertyChange(CONSENSUS_LOAD, null, false);
		}
	}

	public ConsensusData getConsensusData() {
		return consensusData;
	}

	public void setConsensusData(ConsensusData consensusData) {
		this.consensusData = consensusData;
	}

	/**
	 * Defines the mutations between the actual selection positions
	 * @param start, selection start position
	 * @param end, selection end position
	 * @return Selected region's mutations for the consensus sequence
	 */
	private List<Mutation> actualMutations(int start, int end) {
		List<Mutation> mutations = new ArrayList<Mutation>();
		for (Mutation mutation : getConsensusData().getMutations()) {
			int mutationStartPos = mutation.getStartPos();
			if (mutationStartPos >= start && mutationStartPos <= end) {
				mutationStartPos = mutationStartPos - start;
				Mutation clonedMutation = new Mutation(mutation);
				clonedMutation.setStartPos(mutationStartPos);
				mutations.add(clonedMutation);
			}
			//stops when mutation start position bigger than selection end position
			if (mutationStartPos > end) {
				break;
			}
		}
		return mutations;
	}
	/**
	 * Generates consensus sequence between the start and end positions
	 * @param start, start position in consensus sequence
	 * @param end, position in consensus sequence
	 * @return, generated consensus
	 */
	public String getConsensusSequence(int start, int end) {
		StringBuilder sb = null;
		List<Mutation> reversed;
		if (end > 0) {
			sb = new StringBuilder(getConsensusData().getSequence().substring(start, end));
			reversed = actualMutations(start, end);
		} else {
			sb = new StringBuilder(getConsensusData().getSequence());
			reversed = getConsensusData().getMutations();
		}

		Collections.reverse(reversed);
		logger.debug("mutations: " + reversed);
		for (Mutation mutation : reversed) {
			if (mutation.getMutationType() == MutationType.SNP || mutation.getMutationType() == MutationType.MNP) {
				logger.debug("snp or mnp: " + mutation.getStartPos() + " " + mutation.getMutationSequence() + " " + mutation.getReferenceSequence() + " " + mutation.getMutationSequence().substring(1, mutation.getMutationSequence().length() - 1) + " " + (mutation.getStartPos() - 1 + mutation.getLength()));
				sb.replace(mutation.getStartPos() - 1, mutation.getStartPos() - 1 + mutation.getLength(), mutation.getMutationSequence().substring(1, mutation.getMutationSequence().length() - 1));
			}
			if (mutation.getMutationType() == MutationType.DELETION) {
				logger.debug("delition: " + mutation.getStartPos() + " " + mutation.getLength() + " " + mutation.getReferenceSequence() + " " + (mutation.getStartPos() - 1 + mutation.getLength()));
				sb.delete(mutation.getStartPos() - 1, mutation.getStartPos() - 1 + mutation.getLength());
			}
			if (mutation.getMutationType() == MutationType.INSERTION) {
				logger.debug("insertion: " + mutation.getStartPos() + " " + mutation.getLength() + " " + mutation.getMutationSequence() + " " + (mutation.getMutationSequence().substring(1, mutation.getMutationSequence().length() - 1)));
				sb.insert(mutation.getStartPos() - 1, mutation.getMutationSequence().substring(1, mutation.getMutationSequence().length() - 1));
			}
		}
		if (end > 0) {
			return sb.toString();
		}
		return sb.toString();
	}
}
