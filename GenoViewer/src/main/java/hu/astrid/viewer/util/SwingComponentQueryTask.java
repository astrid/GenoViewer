
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

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * {@link Runnable} for querying a property of a swing component. Used to query property
 * in {@code  EventDispatchThread}. {@code query()} should be implemented.
 * @param <V> type of queried property
 * @author Szuni
 */
public abstract class SwingComponentQueryTask<V> implements Runnable, Future<V> {

	private final FutureTask<V> query = new Query();

	/**
	 * This class privedes the result of query can be get by invoker
	 */
    private class Query extends FutureTask<V> {
        public Query() {
            super(new Callable<V>() {
				@Override
                public V call() throws Exception {
                    return SwingComponentQueryTask.this.query() ;
                }
            });
        }
	}

	/**
	 * Query method of the requested property. Can be get by (@see get())
	 * @return requested property
	 */
    protected abstract V query();

	@Override
	public void run() {
		query.run();
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return query.cancel(mayInterruptIfRunning);
	}

	@Override
	public boolean isCancelled() {
		return query.isCancelled();
	}

	@Override
	public boolean isDone() {
		return query.isDone();
	}

	@Override
	public V get() throws InterruptedException, ExecutionException {
		return query.get();
	}

	@Override
	public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return query.get(timeout, unit);
	}

}
